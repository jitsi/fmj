/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.media.codec.audio.speex;

import java.io.*;

import javax.media.*;
import javax.media.format.*;

import net.java.sip.communicator.impl.media.codec.*;

import org.xiph.speex.*;

/**
 * Speex to PCM java decoder
 *
 * @author Damian Minkov
 */
public class JavaDecoder extends com.ibm.media.codec.audio.AudioCodec
{
    private Format lastFormat = null;
    private SpeexDecoder decoder = null;

    static int SpeexSubModeSz[] = { 0, 43, 119, 160, 220, 300, 364, 492, 79, 0,
            0, 0, 0, 0, 0, 0 };

    static int SpeexInBandSz[] = { 1, 1, 4, 4, 4, 4, 4, 4, 8, 8, 16, 16, 32,
            32, 64, 64 };

    static int SpeexWBSubModeSz[] = { 0, 36, 112, 192, 352, 0, 0, 0 };

    // public java.lang.Object[] getControls()
    // {
    // if (controls == null)
    // {
    // controls = new Control[1];
    // controls[0] = new com.sun.media.controls.SilenceSuppressionAdapter(this,
    // false, false);
    // }
    // return (Object[]) controls;
    // }

    public JavaDecoder()
    {
        inputFormats = new Format[] { new AudioFormat(Constants.SPEEX_RTP,
                8000, 8, 1, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED) };

        supportedInputFormats = new AudioFormat[] { new AudioFormat(
                Constants.SPEEX_RTP, 8000, 8, 1, Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED) };

        defaultOutputFormats = new AudioFormat[] { new AudioFormat(
                AudioFormat.LINEAR) };

        PLUGIN_NAME = "Speex Decoder";
    }

    @Override
    public void close()
    {
    }

    @Override
    protected Format[] getMatchingOutputFormats(Format in)
    {
        AudioFormat af = (AudioFormat) in;

        supportedOutputFormats = new AudioFormat[] { new AudioFormat(
                AudioFormat.LINEAR, af.getSampleRate(), 16, af.getChannels(),
                AudioFormat.LITTLE_ENDIAN, // isBigEndian(),
                AudioFormat.SIGNED // isSigned());
        ) };

        return supportedOutputFormats;

    }

    private void initConverter(AudioFormat inFormat)
    {
        lastFormat = inFormat;

        decoder = new SpeexDecoder();
        decoder.init(0, (int) inFormat.getSampleRate(), inFormat.getChannels(),
                false);
    }

    @Override
    public void open()
    {
    }

    public int process(Buffer inputBuffer, Buffer outputBuffer)
    {
        if (!checkInputBuffer(inputBuffer))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(inputBuffer))
        {
            propagateEOM(outputBuffer);
            return BUFFER_PROCESSED_OK;
        }

        byte[] inData = (byte[]) inputBuffer.getData();

        int inpLength = inputBuffer.getLength();

        int outLength = 0;

        int inOffset = inputBuffer.getOffset();
        int outOffset = outputBuffer.getOffset();

        Format newFormat = inputBuffer.getFormat();

        if (lastFormat != newFormat)
        {
            initConverter((AudioFormat) newFormat);
        }

        try
        {
            decoder.processData(inData, inOffset, inpLength);
            outLength = decoder.getProcessedDataByteSize();

            byte[] outData = validateByteArraySize(outputBuffer, outLength);

            decoder.getProcessedData(outData, outOffset);
        } catch (StreamCorruptedException ex)
        {
            ex.printStackTrace();
        }

        updateOutput(outputBuffer, outputFormat, outLength, 0);

        return BUFFER_PROCESSED_OK;
    }
}
