package rtp.custompayload;
/*
 * @(#)PcmDepacketizer.java	1.2 01/03/13
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

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.AudioFormat;


/**
 * Implements an PCM depacketizer.  It uses a custom payload header
 * to send uncompressed PCM data over RTP.  This is not the same
 * as the standard PCMU or PCMA payloads as defined in the RTP spec.
 * Nor do we claim that this is an efficient way to transmit PCM
 * audio.  The sole purpose of this sample is to illustrate the 
 * concept to write a custom RTP depacketizer in JMF.
 */
public class PcmDepacketizer implements Codec {

    static String PLUGIN_NAME = "PCM DePacketizer";
    static String CUSTOM_PCM = "mypcm/rtp";
    static int HDR_SIZE = 8;

    static int DEFAULT_RATE = 8000;
    static int DEFAULT_SIZE = 8;
    static int DEFAULT_CHNLS = 1;

    private Format supportedInputFormats[];
    private Format supportedOutputFormats[];
    private AudioFormat inFormat;
    private AudioFormat outFormat;

    public PcmDepacketizer() {
	   supportedInputFormats = new AudioFormat[] { 
			new AudioFormat(
			    CUSTOM_PCM
			)
	   };

	   // We have to assume some defaults for the output
	   // format.  Otherwise, the data flow graph cannot
	   // be initialized.
	   supportedOutputFormats = new AudioFormat[] {
			new AudioFormat(
			    AudioFormat.LINEAR,
			    DEFAULT_RATE,
			    DEFAULT_SIZE,
			    DEFAULT_CHNLS
			)
	   };
    }


    public String getName() {
      return PLUGIN_NAME;
    }


    static public boolean matches(Format input, Format supported[]) {
	for (int i = 0; i < supported.length; i++) {
	    if (input.matches(supported[i]))
		return true;
	}
	return false;
    }


    public Format [] getSupportedInputFormats() {
	return supportedInputFormats;
    }


    public Format [] getSupportedOutputFormats(Format input) {
	if (input == null || matches(input, supportedInputFormats)) {
	    return supportedOutputFormats;
	}
	return new Format[0];
    }


    public Format setInputFormat(Format format) {
	if (!matches(format, supportedInputFormats))
	    return null;
	inFormat = (AudioFormat)format;
	return format;
    }


    public Format setOutputFormat(Format format) {
	if (!matches(format, supportedOutputFormats))
	    return null;
	outFormat = (AudioFormat)format;
	return format;
    }


    protected Format getInputFormat() {
	return inFormat;
    }


    protected Format getOutputFormat() {
	return outFormat;
    }


    public void open() {
    }


    public void close() {
    }
    

    /**
     * No controls implemented for this plugin.
     */
    public Object[] getControls() {
        return new Object[0];
    }


    /**
     * No controls implemented for this plugin.
     */
    public Object getControl(String type) {
	return null;
    }

    
    /**
     * The processing function that does all the work.
     */
    public int process(Buffer inBuf, Buffer outBuf) {
	
	if (inBuf.isEOM()) {
	    outBuf.setLength(0);
	    outBuf.setEOM(true);
	    return BUFFER_PROCESSED_OK;
	}

	// Decode the packet header which contains the format.
	byte hdr[] = (byte[])inBuf.getData();
	int offset = inBuf.getOffset();

	int rate = ((hdr[offset+1] & 0xff) << 16) | 
		       ((hdr[offset+2] & 0xff) << 8) |
		       (hdr[offset+3] & 0xff);
	int sizeInBits = hdr[offset+4];
	int channels = hdr[offset+5];
	int endian = hdr[offset+6];
	int signed = hdr[offset+7];

	// Generate the output format if it's been changed from last time.
	if ((int)outFormat.getSampleRate() != rate ||
	    outFormat.getSampleSizeInBits() != sizeInBits ||
	    outFormat.getChannels() != channels ||
	    outFormat.getEndian() != endian ||
	    outFormat.getSigned() != signed) {
	    // There's either a format change or the initially
	    // assumed format has been updated to the correct one.
	    outFormat = new AudioFormat(AudioFormat.LINEAR,
					rate, 
					sizeInBits, 
					channels, 
					endian, 
					signed);
	}

	Object outData = outBuf.getData();
	outBuf.setData(inBuf.getData());
	inBuf.setData(outData);

	outBuf.setLength(inBuf.getLength() - HDR_SIZE);
	outBuf.setOffset(inBuf.getOffset() + HDR_SIZE);
	outBuf.setFormat(outFormat);

	return BUFFER_PROCESSED_OK;
    }

    
    public void reset() {
    }
}

