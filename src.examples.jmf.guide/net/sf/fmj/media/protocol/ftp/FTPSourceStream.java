package net.sf.fmj.media.protocol.ftp;

import java.io.IOException;
import java.io.InputStream;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.SourceStream;

/**
 * adapted from http://java.sun.com/products/java-media/jmf/2.1.1/guide/JMFApp-DataSource.html#82808
 *
 */
public class FTPSourceStream implements PullSourceStream
{

	protected InputStream dataIn;
    protected boolean eofMarker;
    protected ContentDescriptor cd;
 
    public FTPSourceStream(InputStream in)
    {
       this.dataIn = in;
       eofMarker = false;
       cd = new ContentDescriptor("unknown");
    }
 
    // SourceSteam methods
 
    public ContentDescriptor getContentDescriptor()
    {
       return cd;
    }
 
    public void close() throws IOException
    {
       dataIn.close();
    }
 
    public boolean endOfStream()
    {
       return eofMarker;
    }
 
    // PullSourceStream methods
 
    public int available() throws IOException
    {
       return dataIn.available();
    }

    public int read(byte[] buffer, int offset, int length) throws IOException
    {
       int n = dataIn.read(buffer, offset, length);
       if (n == -1)
       {
          eofMarker = true;
       }
       return n;
    }
 
    public boolean willReadBlock()
    {
       if(eofMarker) 
       {
          return true;
       } 
       else 
       {
          try
		{
			return dataIn.available() == 0;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
       }
    }
 
    public long getContentLength()
    {
       return SourceStream.LENGTH_UNKNOWN;
    }
    
    public Object getControl(String controlType)
	{
		return null;
	}

	public Object[] getControls()
	{
		return new Object[0];
	}
 }

