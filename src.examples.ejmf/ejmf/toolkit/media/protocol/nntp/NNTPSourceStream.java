package ejmf.toolkit.media.protocol.nntp;

import java.io.IOException;
import java.io.InputStream;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullSourceStream;

public class NNTPSourceStream implements PullSourceStream {

    private InputStream is;
    private ContentDescriptor content;
    private boolean endOfStream;
    private int readBytes;
    
    public NNTPSourceStream(InputStream is) {
        this.is = is;
        endOfStream = false;
        readBytes = 0;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.protocol.SourceStream methods
    //
    ////////////////////////////////////////////////////////////

    public ContentDescriptor getContentDescriptor() {
        if( content == null ) {
            content = new ContentDescriptor("text/plain"); // kenlars99: TODO: this should really be text.plain, according to ContentDescriptor.mimeTypeToPackageName
        }
        return content;
    }

    public long getContentLength() {
        try {
            return readBytes + is.available();
        } catch(IOException e) {
            return readBytes;
        }
    }
    
    public boolean endOfStream() {
        return endOfStream;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.protocol.PullSourceStream methods
    //
    ////////////////////////////////////////////////////////////

    public boolean willReadBlock() {
        try {
            return (is.available() == 0);
        } catch(IOException e) {
            return true;
        }
    }

    public int read(byte[] buffer, int offset, int length)
        throws IOException
    {
        int result = is.read(buffer, offset, length);
        endOfStream = (result == -1);

        if(! endOfStream) {
            readBytes += result;
        }

        return result;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Controls methods
    //
    ////////////////////////////////////////////////////////////

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String controlType) {
        return null;
    }
}
