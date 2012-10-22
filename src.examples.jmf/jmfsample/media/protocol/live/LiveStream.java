
/*
 * @(#)LiveStream.java	1.4 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


package jmfsample.media.protocol.live;

import java.awt.Dimension;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

public class LiveStream implements PushBufferStream, Runnable {

    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected byte [] data;
    protected Dimension size;
    protected RGBFormat rgbFormat;
    protected AudioFormat audioFormat;
    protected boolean started;
    protected Thread thread;
    protected float frameRate = 20f;
    protected BufferTransferHandler transferHandler;
    protected Control [] controls = new Control[0];

    protected boolean videoData = true;
    
    public LiveStream() {
	if (videoData) {
	    int x, y, pos, revpos;
	    
	    size = new Dimension(320, 240);
	    maxDataLength = size.width * size.height * 3;
	    rgbFormat = new RGBFormat(size, maxDataLength,
				      Format.byteArray,
				      frameRate,
				      24,
				      3, 2, 1,
				      3, size.width * 3,
				      VideoFormat.FALSE,
				      Format.NOT_SPECIFIED);
	    
	    // generate the data
	    data = new byte[maxDataLength];
	    pos = 0;
	    revpos = (size.height - 1) * size.width * 3;
	    for (y = 0; y < size.height / 2; y++) {
		for (x = 0; x < size.width; x++) {
		    byte value = (byte) ((y*2) & 0xFF);
		    data[pos++] = value;
		    data[pos++] = 0;
		    data[pos++] = 0;
		    data[revpos++] = value;
		    data[revpos++] = 0;
		    data[revpos++] = 0;
		}
		revpos -= size.width * 6;
	    }
	} else { // audio data
	    audioFormat = new AudioFormat(AudioFormat.LINEAR,
					  8000.0,
					  8,
					  1,
					  Format.NOT_SPECIFIED,
					  AudioFormat.SIGNED,
					  8,
					  Format.NOT_SPECIFIED,
					  Format.byteArray);
	    maxDataLength = 1000;
	}

	thread = new Thread(this);
    }

    /***************************************************************************
     * SourceStream
     ***************************************************************************/
    
    public ContentDescriptor getContentDescriptor() {
	return cd;
    }

    public long getContentLength() {
	return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
	return false;
    }

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/

    int seqNo = 0;
    double freq = 2.0;
    
    public Format getFormat() {
	if (videoData)
	    return rgbFormat;
	else
	    return audioFormat;
    }

    public void read(Buffer buffer) throws IOException {
	synchronized (this) {
	    Object outdata = buffer.getData();
	    if (outdata == null || !(outdata.getClass() == Format.byteArray) ||
		((byte[])outdata).length < maxDataLength) {
		outdata = new byte[maxDataLength];
		buffer.setData(outdata);
	    }
	    if (videoData) {
		buffer.setFormat( rgbFormat );
		buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) * 1000000) );
		int lineNo = (seqNo * 2) % size.height;
		int chunkStart = lineNo * size.width * 3;
		System.arraycopy(data, chunkStart,
				 outdata, 0,
				 maxDataLength - (chunkStart));
		if (chunkStart != 0) {
		    System.arraycopy(data, 0,
				     outdata, maxDataLength - chunkStart,
				     chunkStart);
		}
	    } else {
		buffer.setFormat( audioFormat );
		buffer.setTimeStamp( 1000000000 / 8 );
		for (int i = 0; i < 1000; i++) {
		    ((byte[])outdata)[i] = (byte) (Math.sin(i / freq) * 32);
		    freq = (freq + 0.01);
		    if (freq > 10.0)
			freq = 2.0;
		}
	    }
	    buffer.setSequenceNumber( seqNo );
	    buffer.setLength(maxDataLength);
	    buffer.setFlags(0);
	    buffer.setHeader( null );
	    seqNo++;
	}
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
	synchronized (this) {
	    this.transferHandler = transferHandler;
	    notifyAll();
	}
    }

    void start(boolean started) {
	synchronized ( this ) {
	    this.started = started;
	    if (started && !thread.isAlive()) {
		thread = new Thread(this);
		thread.start();
	    }
	    notifyAll();
	}
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/

    public void run() {
	while (started) {
	    synchronized (this) {
		while (transferHandler == null && started) {
		    try {
			wait(1000);
		    } catch (InterruptedException ie) {
		    }
		} // while
	    }

	    if (started && transferHandler != null) {
		transferHandler.transferData(this);
		try {
		    Thread.sleep( 10 );
		} catch (InterruptedException ise) {
		}
	    }
	} // while (started)
    } // run

    // Controls
    
    public Object [] getControls() {
	return controls;
    }

    public Object getControl(String controlType) {
       try {
          Class  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
             if (cls.isInstance(cs[i]))
                return cs[i];
          }
          return null;

       } catch (Exception e) {   // no such controlType or such control
         return null;
       }
    }
}
