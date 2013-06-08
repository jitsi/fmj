package net.sf.fmj.ffmpeg_java;

import java.io.*;
import java.util.logging.*;

import javax.media.protocol.*;

import net.sf.ffmpeg_java.*;
import net.sf.ffmpeg_java.AVFormatLibrary.URLContext;
import net.sf.ffmpeg_java.custom_protocol.*;
import net.sf.fmj.utility.*;

import com.sun.jna.*;

/**
 * Implements CallbackURLProtocolHandler by using a PullSourceStream.
 *
 * Because seeking functions have to be implemented, either the source has to be
 * Cloneable, or the streams provided by the source have to be Seekable.
 * Otherwise, seeking will fail, and ffmpeg does not really check for seek
 * failures, it mostly just assumes they will work. This was written with an eye
 * on ffmpeg's http.c, which shows that when a seek is needed, they simply open
 * a new stream. http.c is not of course used when this is used, it just shows
 * what kind of behavior is expected from a URLProtocol.
 *
 * @author Ken Larson
 *
 */
public class PullDataSourceCallbackURLProtocolHandler implements
        CallbackURLProtocolHandler
{
    private static final Logger logger = LoggerSingleton.logger;

    private PullDataSource source;
    private PullSourceStream pss;
    private long curpos; // for seeking

    public static final boolean TRACE = false;
    private boolean mustClone = false;
    private final boolean isSourceCloneable;

    private boolean opened = false;

    public PullDataSourceCallbackURLProtocolHandler(PullDataSource source)
    {
        super();
        this.source = source;
        isSourceCloneable = source instanceof SourceCloneable;

    }

    public int close(URLContext h)
    {
        // TODO: it is not clear that we should really do the close here, if the
        // source would be closed elsewhere.
        if (TRACE)
            System.out.print("close");

        try
        {
            doClose();
        } catch (IOException e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.WARNING, "" + e, e);
            return -1;
        } catch (Throwable e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.SEVERE, "" + e, e);
            return -1;
        }

        if (TRACE)
            System.out.println(" return " + 0);
        return 0;
    }

    private void closeAndReopen() throws IOException
    {
        doClose();
        doOpen();
    }

    private void doClose() throws IOException
    {
        if (!opened)
            return;
        try
        {
            source.stop();
            source.disconnect();
        } finally
        {
            mustClone = true;
            opened = false;
        }
    }

    private void doOpen() throws IOException
    {
        if (opened)
            return;

        if (mustClone)
        {
            if (!isSourceCloneable)
            {
                logger.severe("PullSourceStreamCallbackURLProtocolHandler: cannot reopen because source is not SourceCloneable");
                throw new IOException("not SourceCloneable");
            }
            source = (PullDataSource) ((SourceCloneable) source).createClone();
        }

        source.connect(); // TODO: we don't need to connect if already connected
        source.start();

        final PullSourceStream[] pullSourceStreams = source.getStreams();
        if (pullSourceStreams.length < 1)
        {
            logger.warning("No streams");
            throw new IOException("No streams");
        }

        pss = pullSourceStreams[0];

        curpos = 0;
        opened = true;
    }

    public int open(URLContext h, String filename, int flags)
    {
        if (TRACE)
            System.out.print("open: flags=" + flags);

        if ((flags & AVFormatLibrary.URL_RDWR) != 0)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.severe("PullSourceStreamCallbackURLProtocolHandler: only read-only open supported");
            return -1;
        } else if ((flags & AVFormatLibrary.URL_WRONLY) != 0)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.severe("PullSourceStreamCallbackURLProtocolHandler: only read-only open supported");
            return -1;
        }

        try
        {
            doOpen();
        } catch (IOException e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.WARNING, "" + e, e);
            return -1;
        } catch (Throwable e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.SEVERE, "" + e, e);
            return -1;
        }

        if (TRACE)
            System.out.println(" return " + 0);
        return 0;
    }

    public int read(URLContext h, Pointer buf, int size)
    {
        if (TRACE)
            System.out.print("read: size=" + size);

        // TODO: is there a way to do this without having a copy of the byte
        // array?
        final byte[] ba = new byte[size]; // buf.getByteArray(0, size);

        try
        {
            if (!opened)
            {
                logger.warning("Attempt to read with closed stream");
                if (TRACE)
                    System.out.println(" return " + -1);
                return -1;
            }

            final int ret = pss.read(ba, 0, size);
            buf.write(0, ba, 0, size);
            curpos += ret;

            if (TRACE)
                System.out.println(" return " + ret);
            return ret;
        } catch (IOException e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.WARNING, "" + e, e);
            return -1;
        } catch (Throwable e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.SEVERE, "" + e, e);
            return -1;
        }

    }

    public long seek(URLContext h, long pos, int whence)
    {
        if (TRACE)
            System.out.print("seek: pos=" + pos + " whence=" + whence);

        // doesn't need to be seekable to query the size:
        if (whence == AVFormatLibrary.AVSEEK_SIZE)
        {
            final long ret = pss.getContentLength();
            if (TRACE)
                System.out.println(" return " + ret);
            return ret;
        }

        if (whence == SEEK_END)
        {
            // cannot seek relative to end if no known content length.
            if (pss.getContentLength() < 0)
            {
                if (TRACE)
                    System.out.println(" return " + -1);
                return -1;
            }
        }

        final boolean isSeekable = pss instanceof Seekable;

        try
        {
            if (!isSeekable)
            {
                // if the stream is not seekable, we have to seek by
                // reading/ignoring and/or closing/reopening the stream to get
                // to zero.

                if (whence == SEEK_END)
                {
                    // seek relative to the end - translate this into an
                    // absolute seek
                    whence = SEEK_SET;
                    pos += pss.getContentLength();
                }

                // at this point whence is either SEEK_SET or SEEK_CUR

                if (whence == SEEK_CUR)
                {
                    // relative seek: translate to absolute seek
                    whence = SEEK_SET;
                    pos += curpos;
                }

                // at this point whence is definitely SEEK_SET (or an invalid
                // value)

                if (whence == SEEK_SET)
                {
                    if (pss.getContentLength() > 0
                            && pos >= pss.getContentLength())
                    {
                        if (TRACE)
                            System.out.println("Seek to end: closing...");
                        // seek to end or past. No need to read everything, just
                        // close stream and update pos.
                        // subsequent seeks will cause us to reopen.
                        doClose();
                        curpos = pos;
                        return curpos;

                    } else if (pos >= curpos)
                    { // absolute seek to after our position: simply do a
                      // SEEK_CUR below.
                        whence = SEEK_CUR;
                        pos -= curpos;
                    } else
                    {
                        // absolute seek to something before where we are:
                        // translate to relative seek to zero, and close and
                        // reopen stream, so that we are back at zero.
                        whence = SEEK_CUR;
                        if (TRACE)
                            System.out.println("Closing and reopening...");
                        closeAndReopen();
                    }
                }

                if (whence == SEEK_CUR)
                { // implement seek by repeated read:
                    final byte[] b = new byte[1];

                    // TODO: very inefficient
                    // TODO: we don't actually have to do the read until later,
                    // if we don't want to.
                    for (int i = 0; i < pos; ++i)
                    {
                        if (pss.read(b, 0, 1) < 0)
                        {
                            // end of stream - TODO: is this what we are
                            // supposed to do?
                            logger.warning("attempt to seek past end of stream");
                            if (TRACE)
                                System.out.println(" return " + -1);
                            return -1;
                        }
                        curpos += 1;
                    }
                    return curpos;
                }
            }

            if (!(isSeekable))
            {
                if (TRACE)
                    System.out.println(" return " + -1);
                // System.err.println("pss not Seekable");
                return -1;
            }

            final Seekable seekable = (Seekable) pss;

            final long seekTo;
            if (whence == SEEK_SET)
                seekTo = pos;
            else if (whence == SEEK_CUR)
                seekTo = curpos + pos;
            else if (whence == SEEK_END)
            {
                if (pss.getContentLength() < 0)
                    return -1;
                seekTo = pss.getContentLength() + pos;
            } else
            {
                if (TRACE)
                    System.out.println(" return " + -1);
                logger.warning("seek: Invalid whence value: " + whence);
                return -1;
            }

            final long ret = seekable.seek(seekTo);
            curpos = ret;
            if (TRACE)
                System.out.println(" return " + ret);
            return ret;
        } catch (IOException e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.WARNING, "" + e, e);
            return -1;
        } catch (Throwable e)
        {
            if (TRACE)
                System.out.println(" return " + -1);
            logger.log(Level.SEVERE, "" + e, e);
            return -1;
        }

    }

    public int write(URLContext h, Pointer buf, int size)
    {
        if (TRACE)
            System.out.print("write: size=" + size);

        logger.severe("write not supported");
        return -1;

    }

}
