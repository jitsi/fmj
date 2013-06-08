package net.sf.fmj.media.codec.audio;

import javax.media.*;
import javax.media.format.*;

/*
 * This audio codec does the rate conversion between any two of
 * 8k, 11.025k, 16k, 22.05k, 32k, 44.1k and 48k.
 */

public class RateConverter extends AudioCodec
{
    public RateConverter()
    {
        inputFormats = new Format[] { new AudioFormat(AudioFormat.LINEAR) };
    }

    private int doByteCvrt(Buffer in, int inLen, int inOffset, Buffer out,
            int outLen, int step, double ratio)
    {
        byte[] inData = (byte[]) in.getData();
        byte[] outData = validateByteArraySize(out, outLen);
        int outOffset = 0;

        out.setData(outData);
        out.setFormat(outputFormat);
        out.setOffset(0);
        out.setLength(outLen);

        // start to convert
        double sum = 0.0;
        int inPtr = inOffset;
        int outPtr = outOffset;
        int inEnd = inOffset + inLen;

        if (ratio == 1.0)
        { // no format changed
            System.arraycopy(inData, inOffset, outData, outOffset, inLen);
            return BUFFER_PROCESSED_OK;
        }

        if (ratio > 1.0)
        { // subsampling
            while ((inPtr <= (inEnd - step)) && (outPtr <= (outLen - step)))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                }

                sum += ratio;
                while (sum > 0.0)
                {
                    inPtr += step;
                    sum -= 1.0;
                }

            }
        } else
        { // ratio < 1.0, up sampling
            byte[] d = new byte[step];
            while (inPtr <= (inEnd - step))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                    d[i] = inData[inPtr + i];
                }

                while ((sum += ratio) < 1.0)
                {
                    if (outPtr <= (outLen - step))
                        for (int i = 0; i < step; i++)
                        {
                            outData[outPtr++] = d[i];
                        }
                }

                sum -= 1.0;
                inPtr += step;
            }
        }

        return BUFFER_PROCESSED_OK;

    }

    private int doIntCvrt(Buffer in, int inLen, int inOffset, Buffer out,
            int outLen, int step, double ratio)
    {
        int[] inData = (int[]) in.getData();
        int[] outData = validateIntArraySize(out, outLen);
        int outOffset = 0;

        out.setData(outData);
        out.setFormat(outputFormat);
        out.setOffset(0);
        out.setLength(outLen);

        // start to convert
        double sum = 0.0;
        int inPtr = inOffset;
        int outPtr = outOffset;
        int inEnd = inOffset + inLen;

        if (ratio == 1.0)
        { // no format changed
            System.arraycopy(inData, inOffset, outData, outOffset, inLen);
            return BUFFER_PROCESSED_OK;
        }

        if (ratio > 1.0)
        { // subsampling
            while ((inPtr <= (inEnd - step)) && (outPtr <= (outLen - step)))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                }

                sum += ratio;
                while (sum > 0.0)
                {
                    inPtr += step;
                    sum -= 1.0;
                }

            }
        } else
        { // ratio < 1.0, up sampling
            int[] d = new int[step];
            while (inPtr <= (inEnd - step))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                    d[i] = inData[inPtr + i];
                }

                while ((sum += ratio) < 1.0)
                {
                    if (outPtr <= (outLen - step))
                        for (int i = 0; i < step; i++)
                            outData[outPtr++] = d[i];
                }

                sum -= 1.0;
                inPtr += step;
            }
        }

        return BUFFER_PROCESSED_OK;

    }

    private int doShortCvrt(Buffer in, int inLen, int inOffset, Buffer out,
            int outLen, int step, double ratio)
    {
        short[] inData = (short[]) in.getData();
        short[] outData = validateShortArraySize(out, outLen);
        int outOffset = 0;

        out.setData(outData);
        out.setFormat(outputFormat);
        out.setOffset(0);
        out.setLength(outLen);

        // start to convert
        double sum = 0.0;
        int inPtr = inOffset;
        int outPtr = outOffset;
        int inEnd = inOffset + inLen;

        if (ratio == 1.0)
        { // no format changed
            System.arraycopy(inData, inOffset, outData, outOffset, inLen);
            return BUFFER_PROCESSED_OK;
        }

        if (ratio > 1.0)
        { // subsampling
            while ((inPtr <= (inEnd - step)) && (outPtr <= (outLen - step)))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                }

                sum += ratio;
                while (sum > 0.0)
                {
                    inPtr += step;
                    sum -= 1.0;
                }

            }
        } else
        { // ratio < 1.0, up sampling
            short[] d = new short[step];
            while (inPtr <= (inEnd - step))
            {
                for (int i = 0; i < step; i++)
                {
                    outData[outPtr++] = inData[inPtr + i];
                    d[i] = inData[inPtr + i];
                }

                while ((sum += ratio) < 1.0)
                {
                    if (outPtr <= (outLen - step))
                        for (int i = 0; i < step; i++)
                            outData[outPtr++] = d[i];
                }

                sum -= 1.0;
                inPtr += step;
            }
        }

        return BUFFER_PROCESSED_OK;

    }

    public String getName()
    {
        return "Rate Conversion";
    }

    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
        {
            return new Format[] { new AudioFormat(AudioFormat.LINEAR) };

        }

        if (input instanceof AudioFormat)
        {
            // supported rate: 8000, 11025, 16k, 22.5k, 32k, 44.1k, 48k
            AudioFormat af = (AudioFormat) input;
            int ssize = af.getSampleSizeInBits();
            int chnl = af.getChannels();
            int endian = af.getEndian();
            int signed = af.getSigned();

            outputFormats = new Format[] {
                    new AudioFormat(AudioFormat.LINEAR, 8000.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 11025.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 16000.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 22050.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 32000.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 44100.0, ssize, chnl,
                            endian, signed),
                    new AudioFormat(AudioFormat.LINEAR, 48000.0, ssize, chnl,
                            endian, signed), };
        } else
        {
            outputFormats = new Format[0];
        }

        return outputFormats;
    }

    public synchronized int process(Buffer in, Buffer out)
    {
        if (!checkInputBuffer(in))
        {
            // System.out.println("return due to  bad input buffer");
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(in))
        {
            propagateEOM(out);
            return BUFFER_PROCESSED_OK;
        }

        int inOffset = in.getOffset();
        int inLen = in.getLength();

        // TODO: may the rate change in the middle?
        double inRate = ((AudioFormat) inputFormat).getSampleRate();
        double outRate = ((AudioFormat) outputFormat).getSampleRate();
        int chnl = ((AudioFormat) inputFormat).getChannels();
        int bsize = ((AudioFormat) inputFormat).getSampleSizeInBits() / 8;
        int step = 0;

        if (chnl == 2)
        {
            if (bsize == 2)
                step = 4;
            else
                step = 2;
        } else
        {
            if (bsize == 2)
                step = 2;
            else
                step = 1;
        }

        if (outRate == 0.0 || inRate == 0.0)
        {
            return BUFFER_PROCESSED_FAILED;
        }

        double ratio = inRate / outRate;

        int outLen = (int) ((inLen - inOffset) * outRate / inRate + 0.5);
        switch (step)
        {
        case 2: // make sure outLen is even
            if (outLen % 2 == 1)
                outLen++;
            break;
        case 4: // make sure outLen is the multiply of 4
            if (outLen % 4 != 0)
                outLen = (outLen / 4 + 1) << 2;
            break;
        }

        if (inputFormat.getDataType() == Format.byteArray)
        {
            return doByteCvrt(in, inLen, inOffset, out, outLen, step, ratio);
        } else if (inputFormat.getDataType() == Format.shortArray)
        {
            return doShortCvrt(in, inLen, inOffset, out, outLen, step, ratio);
        } else if (inputFormat.getDataType() == Format.intArray)
        {
            return doIntCvrt(in, inLen, inOffset, out, outLen, step, ratio);
        }

        return BUFFER_PROCESSED_FAILED;
    }

}
