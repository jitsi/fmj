package net.sf.fmj.media.protocol.res;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * Protocol handler for res: protocol, loads stream from Java resource.
 *
 * @author Ken Larson
 *
 */
public class DataSource extends PullDataSource implements SourceCloneable
{
    class ResSourceStream implements PullSourceStream
    {
        private boolean endOfStream = false;

        public boolean endOfStream()
        {
            return endOfStream;
        }

        public ContentDescriptor getContentDescriptor()
        {
            return contentType;
        }

        public long getContentLength()
        {
            return -1; // TODO
        }

        public Object getControl(String controlType)
        {
            return null;
        }

        public Object[] getControls()
        {
            return new Object[0];
        }

        public int read(byte[] buffer, int offset, int length)
                throws IOException
        {
            final int result = inputStream.read(buffer, offset, length); // TODO:
                                                                         // does
                                                                         // this
                                                                         // handle
                                                                         // the
                                                                         // requirement
                                                                         // of
                                                                         // not
                                                                         // returning
                                                                         // 0
                                                                         // unless
                                                                         // passed
                                                                         // in
                                                                         // 0?
            if (result == -1) // end of stream
                endOfStream = true;

            return result;
        }

        public boolean willReadBlock()
        {
            try
            {
                return inputStream.available() <= 0;
            } catch (IOException e)
            {
                return true;
            }
        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private static String getContentTypeFor(String path)
    {
        final String ext = PathUtils.extractExtension(path);
        // TODO: what if ext is null?
        String result = MimeManager.getMimeType(ext);

        // if we can't find it in our mime table, use URLConnection's

        if (result != null)
            return result;

        result = URLConnection.getFileNameMap().getContentTypeFor(path);
        return result;

    }

    private InputStream inputStream;

    private ContentDescriptor contentType;

    private boolean connected = false;

    private ResSourceStream[] sources;

    public DataSource()
    {
        super();
    }

    @Override
    public void connect() throws IOException
    {
        // we allow a re-connection even if we are connected, due to an oddity
        // in the way Manager works. See comments there
        // in createPlayer(MediaLocator sourceLocator).
        // if (connected) // TODO: FMJ tends to call this twice. Check with JMF
        // to see if that is normal.
        // return;

        final String path = getLocator().getRemainder();

        inputStream = DataSource.class.getResourceAsStream(path);

        final String s = getContentTypeFor(path); // TODO: use our own mime
                                                  // mapping
        if (s == null)
            throw new IOException("Unknown content type for path: " + path);
        // TODO: what is the right place to apply
        // ContentDescriptor.mimeTypeToPackageName?
        contentType = new ContentDescriptor(
                ContentDescriptor.mimeTypeToPackageName(s));

        sources = new ResSourceStream[1];
        sources[0] = new ResSourceStream();

        connected = true;
    }

    public javax.media.protocol.DataSource createClone()
    {
        final DataSource d;

        d = new DataSource();
        d.setLocator(getLocator());

        if (connected)
        {
            try
            {
                d.connect();
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
                return null; // according to the API, return null on failure.
            }
        }

        return d;
    }

    @Override
    public void disconnect()
    {
        if (!connected)
            return;

        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            } catch (IOException e)
            {
                logger.log(Level.WARNING, "" + e, e);
            }
        }

        connected = false;
    }

    @Override
    public String getContentType()
    {
        if (!connected)
            throw new Error("Source is unconnected.");
        String path = getLocator().getRemainder();
        String s = getContentTypeFor(path); // TODO: use our own mime mapping
        return ContentDescriptor.mimeTypeToPackageName(s);
    }

    @Override
    public Object getControl(String controlName)
    {
        return null;
    }

    @Override
    public Object[] getControls()
    {
        return new Object[0];
    }

    @Override
    public Time getDuration()
    {
        return Time.TIME_UNKNOWN; // TODO: any case where we know the duration?
    }

    @Override
    public PullSourceStream[] getStreams()
    {
        if (!connected)
            throw new Error("Unconnected source.");
        return sources;
    }

    @Override
    public void start() throws java.io.IOException
    {
        // throw new UnsupportedOperationException(); // TODO - what to do?
    }

    @Override
    public void stop() throws java.io.IOException
    { // throw new UnsupportedOperationException(); // TODO - what to do?
    }

    /**
     * Strips trailing ; and anything after it. Is generally only used for
     * multipart content.
     */
    private String stripTrailer(String contentType)
    {
        final int index = contentType.indexOf(";");
        if (index < 0)
            return contentType;
        final String result = contentType.substring(0, index);
        return result;

    }
}
