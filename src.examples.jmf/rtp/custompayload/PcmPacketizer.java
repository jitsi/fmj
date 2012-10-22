package rtp.custompayload;

/*
 * @(#)PcmPacketizer.java	1.2 01/03/13
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
import javax.media.ResourceUnavailableException;
import javax.media.format.AudioFormat;

/**
 * Implements an PCM packetizer.  It uses a custom payload header
 * to send uncompressed PCM data over RTP.  This is not the same
 * as the standard PCMU or PCMA payloads as defined in the RTP spec.
 * Nor do we claim that this is an efficient way to transmit PCM
 * audio.  The sole purpose of this sample is to illustrate the 
 * concept to write a custom RTP packetizer in JMF.
 */
public class PcmPacketizer implements Codec {

    static String PLUGIN_NAME = "PCM Packetizer";
    static String CUSTOM_PCM = "mypcm/rtp";
    static int DATA_SIZE = 480;
    static int HDR_SIZE = 8;
    static int PACKET_SIZE = DATA_SIZE + HDR_SIZE;

    private Format supportedInputFormats[];
    private Format supportedOutputFormats[];
    private AudioFormat inFormat;
    private AudioFormat outFormat;
    private byte[] history;
    private int historyLength;
    private byte[] pktHdr;


    public PcmPacketizer() {
       	supportedInputFormats = new AudioFormat[] {
	        new AudioFormat(
		    AudioFormat.LINEAR
                )
        };
        supportedOutputFormats  = new AudioFormat[] {
	        new AudioFormat(
                    CUSTOM_PCM
                )
        };
    }


    public String getName() {
	return PLUGIN_NAME;
    }


    /**
     * Just a simple utility function.
     */
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
	if (input == null || matches(input, supportedInputFormats))
	    return supportedOutputFormats;
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


    public void open() throws ResourceUnavailableException{
	// Initialize the history buffer to save data from the
	// previous input buffer if that data is not enough to
	// fill a single packet.
	history = new byte[PACKET_SIZE];
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
    public synchronized int process(Buffer inBuf, Buffer outBuf) {

        int inLength = inBuf.getLength();

        byte[] inData = (byte[])inBuf.getData();
        byte[] outData = (byte[])outBuf.getData();

	if (outData == null || outData.length < PACKET_SIZE) {
	    outData = new byte[PACKET_SIZE];
	    outBuf.setData(outData);
	}

	// Generate the packet header.
	int rate = (int)inFormat.getSampleRate();
	int size = (int)inFormat.getSampleSizeInBits();
	int channels = (int)inFormat.getChannels();

	outData[0] = 0;	// filler
	outData[1] = (byte)((rate >> 16) & 0xff);
	outData[2] = (byte)((rate >> 8) & 0xff);
	outData[3] = (byte)(rate & 0xff);
	outData[4] = (byte)inFormat.getSampleSizeInBits();
	outData[5] = (byte)inFormat.getChannels();
	outData[6] = (byte)inFormat.getEndian();
	outData[7] = (byte)inFormat.getSigned();

	int frameSize = inFormat.getSampleSizeInBits() * inFormat.getChannels();

	// Recompute the output format if the input format has changed.
	// The crucial info is the frame rate and size.  These are used
	// to compute the actual rate the data being sent.
	if (rate != (int)outFormat.getFrameRate() ||
	    frameSize != outFormat.getFrameSizeInBits()) {
		outFormat = new AudioFormat(CUSTOM_PCM,
				AudioFormat.NOT_SPECIFIED,  // rate
				AudioFormat.NOT_SPECIFIED,  // size
				AudioFormat.NOT_SPECIFIED,  // channel
				AudioFormat.NOT_SPECIFIED,  // endian
				AudioFormat.NOT_SPECIFIED,  // signed
				size * channels,	// frame size
				rate,			// frame rate
				null);
	}

        if (inLength + historyLength >= DATA_SIZE) {

	    // Enough data for one packet.

            int copyFromHistory = Math.min(historyLength, DATA_SIZE);

            System.arraycopy(history, 0, outData, HDR_SIZE , copyFromHistory);

            int remainingBytes = DATA_SIZE - copyFromHistory;

            System.arraycopy(inData, inBuf.getOffset(), 
			outData, copyFromHistory + HDR_SIZE, remainingBytes);

            historyLength -= copyFromHistory;
            inBuf.setOffset( inBuf.getOffset() + remainingBytes);
            inBuf.setLength( inLength - remainingBytes);

	    outBuf.setFormat(outFormat);
	    outBuf.setLength(PACKET_SIZE);
	    outBuf.setOffset(0);
	    
            return INPUT_BUFFER_NOT_CONSUMED ;
        }

        if (inBuf.isEOM()) { // last packet

            System.arraycopy(history, 0, outData, HDR_SIZE, historyLength);
            System.arraycopy(inData, inBuf.getOffset(), 
			outData, historyLength + HDR_SIZE, inLength);

	    outBuf.setFormat(outFormat);
	    outBuf.setLength(inLength + historyLength + HDR_SIZE);
	    outBuf.setOffset(0);

            historyLength = 0;

            return BUFFER_PROCESSED_OK;
        }

        // Not enough data for one packet.  Save the remainder 
	// for next time.
        System.arraycopy(inData, inBuf.getOffset(), 
			history, historyLength,inLength) ;
        historyLength += inLength;

        return OUTPUT_BUFFER_NOT_FILLED ;
    }

    public void reset() {
        historyLength=0;
    }
}


