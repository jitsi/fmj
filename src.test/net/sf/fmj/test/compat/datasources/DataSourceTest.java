package net.sf.fmj.test.compat.datasources;

import java.io.*;
import java.net.*;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class DataSourceTest extends TestCase
{
    class MyDataSource extends DataSource
    {
        private final StringBuffer stringBuffer = new StringBuffer();

        public MyDataSource()
        {
            super();
        }

        public MyDataSource(MediaLocator arg0)
        {
            super(arg0);
        }

        // @Override
        @Override
        public void connect() throws IOException
        {
            stringBuffer.append("connect");
        }

        // @Override
        @Override
        public void disconnect()
        {
            stringBuffer.append("disconnect");
        }

        public void doInitCheck()
        {
            initCheck();
        }

        // @Override
        @Override
        public String getContentType()
        {
            stringBuffer.append("getContentType");
            return null;
        }

        // @Override
        @Override
        public Object getControl(String arg0)
        {
            stringBuffer.append("getControl");
            return null;
        }

        // @Override
        @Override
        public Object[] getControls()
        {
            stringBuffer.append("getControls");
            return null;
        }

        // @Override
        @Override
        public Time getDuration()
        {
            stringBuffer.append("getDuration");
            return null;
        }

        public StringBuffer getStringBuffer()
        {
            return stringBuffer;
        }

        // @Override
        @Override
        public void start() throws IOException
        {
            stringBuffer.append("start");
        }

        // @Override
        @Override
        public void stop() throws IOException
        {
            stringBuffer.append("stop");
        }

    }

    class MyMediaLocator extends MediaLocator
    {
        private final StringBuffer stringBuffer = new StringBuffer();

        public MyMediaLocator(String arg0)
        {
            super(arg0);
        }

        public MyMediaLocator(URL arg0)
        {
            super(arg0);
        }

        // @Override
        @Override
        public String getProtocol()
        {
            stringBuffer.append("getProtocol");
            return super.getProtocol();
        }

        // @Override
        @Override
        public String getRemainder()
        {
            stringBuffer.append("getRemainder");
            return super.getRemainder();
        }

        public StringBuffer getStringBuffer()
        {
            return stringBuffer;
        }

        // @Override
        @Override
        public URL getURL() throws MalformedURLException
        {
            stringBuffer.append("getURL");
            return super.getURL();
        }

        // @Override
        @Override
        public String toExternalForm()
        {
            return super.toExternalForm();
        }

        // @Override
        @Override
        public String toString()
        {
            return super.toString();
        }

    }

    public void testDataSource()
    {
        {
            MyMediaLocator m = new MyMediaLocator("asdf");
            MyDataSource d = new MyDataSource(m);
            d.doInitCheck();
            // make sure no unexpected methods were called:
            assertEquals(m.getStringBuffer().toString(), "");
            assertEquals(d.getStringBuffer().toString(), "");
        }
        try
        {
            MyDataSource d = new MyDataSource(null);
            d.doInitCheck();
            assertTrue(false);
        } catch (Error t)
        {
            assertTrue(t.getClass() == Error.class);
            assertEquals(t.getMessage(), "Uninitialized DataSource error.");
        }

    }
}
