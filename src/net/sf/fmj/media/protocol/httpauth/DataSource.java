package net.sf.fmj.media.protocol.httpauth;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * HTTPAUTH (pseudo-) protocol handler DataSource. This is a pseudo-protocol
 * that allows the username and password for an http datasource to be specified
 * in the URL. Copied and modified from URLDataSource. The syntax for an
 * httpauth URL is derived from an http URL as follows:
 * http://myipcameraimage.com/cam with username "user" and password "pass" would
 * become httpauth:user:pass@//myipcameraimage.com/cam This is simply a
 * convenience data source. It allows things like FMJ studio to play IP camera
 * streams that are password protected, without having to prompt the user.
 *
 * @author Ken Larson
 *
 */
public class DataSource extends PullDataSource implements SourceCloneable
{
    class URLSourceStream implements PullSourceStream
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
            return conn.getContentLength(); // returns -1 if unknown, which is
                                            // the same as LENGTH_UNKNOWN
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
            final int result = conn.getInputStream().read(buffer, offset,
                    length); // TODO: does this handle the requirement of not
                             // returning 0 unless passed in 0?
            if (result == -1) // end of stream
                endOfStream = true;

            return result;
        }

        public boolean willReadBlock()
        {
            try
            {
                return conn.getInputStream().available() <= 0;
            } catch (IOException e)
            {
                return true;
            }
        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private URLConnection conn;

    private boolean connected = false;
    private String contentTypeStr;

    private ContentDescriptor contentType;

    protected URLSourceStream[] sources;

    public DataSource()
    {
        super();
    }

    public DataSource(URL url)
    {
        setLocator(new MediaLocator(url));

    }

    @Override
    public void connect() throws IOException
    {
        // we allow a re-connection even if we are connected, due to an oddity
        // in the way Manager works. See comments there
        // in createPlayer(MediaLocator sourceLocator).
        //
        // if (connected)
        // return;

        // example: httpauth:guest:guest@// + real url without http:
        final String remainder = getLocator().getRemainder();
        final int atIndex = remainder.indexOf('@');
        if (atIndex < 0)
            throw new IOException("Invalid httpauth url: expected: @");
        final int colonIndex = remainder.indexOf(':');
        if (colonIndex < 0 || colonIndex > atIndex)
            throw new IOException("Invalid httpaut url: expected: :");
        final String user = remainder.substring(0, colonIndex);
        final String pass = remainder.substring(colonIndex + 1, atIndex);

        final String realUrlStr = "http:"
                + getLocator().getRemainder().substring(atIndex + 1);

        conn = new URL(realUrlStr).openConnection();

        if (conn instanceof HttpURLConnection)
        { // TODO: this is probably why JMF has explicit HTTP and FTP data
          // sources - so we can check things explicitly.
            final HttpURLConnection huc = (HttpURLConnection) conn;
            if (user != null && !user.equals(""))
            {
                huc.setRequestProperty(
                        "Authorization",
                        "Basic "
                                + StringUtils.byteArrayToBase64String((user
                                        + ":" + pass).getBytes()));
            }
            huc.connect();

            final int code = huc.getResponseCode();
            if (!(code >= 200 && code < 300))
            {
                huc.disconnect();
                throw new IOException("HTTP response code: " + code);
            }

            // TODO: what is the right place to apply
            // ContentDescriptor.mimeTypeToPackageName?
            contentTypeStr = ContentDescriptor
                    .mimeTypeToPackageName(stripTrailer(conn.getContentType()));
        } else
        {
            conn.connect();
            // TODO: what is the right place to apply
            // ContentDescriptor.mimeTypeToPackageName?
            contentTypeStr = ContentDescriptor.mimeTypeToPackageName(conn
                    .getContentType());
        }

        contentType = new ContentDescriptor(contentTypeStr);
        sources = new URLSourceStream[1];
        sources[0] = new URLSourceStream();

        connected = true;
    }

    public javax.media.protocol.DataSource createClone()
    {
        final DataSource d;
        try
        {
            d = new DataSource(getLocator().getURL());
        } catch (MalformedURLException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            return null; // according to the API, return null on failure.
        }

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
        if (conn != null)
        {
            if (conn instanceof HttpURLConnection)
            {
                final HttpURLConnection huc = (HttpURLConnection) conn;
                huc.disconnect();
            }
            // TODO: others
        }
        connected = false;
    }

    @Override
    public String getContentType()
    {
        return contentTypeStr;
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
