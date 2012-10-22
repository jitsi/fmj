package net.sf.fmj.media.codec.audio.ulaw;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

/** @deprecated not used yet. Requires both FMJ and JMF in classpath. */
@Deprecated
public class MuLawCompatTest extends TestCase
{
    private byte encode(byte[] data, Codec encoder, Format f)
    {
        Buffer input = new Buffer();
        input.setFormat(f);
        input.setData(data);
        input.setLength(2);
        Buffer output = new Buffer();
        output.setData(new byte[1]);
        int res = encoder.process(input, output);
        assertEquals(res, PlugIn.BUFFER_PROCESSED_OK);
        final byte[] result = (byte[]) output.getData();
        final int resultLen = output.getLength();
        assertEquals(resultLen, 1);
        return result[0];
    }

    private Codec open(String className, Format f) throws Exception
    {
        Codec encoder = (Codec) Class.forName(className).newInstance();
        encoder.setInputFormat(f);
        encoder.setOutputFormat(encoder.getSupportedOutputFormats(f)[0]);
        encoder.open();
        return encoder;

    }

    public void test1() throws Exception
    {
        String className = "com.ibm.media.codec.audio.ulaw.JavaEncoder";
        String className2 = "net.sf.fmj.media.codec.audio.ulaw.Encoder";

        Format f = new AudioFormat(AudioFormat.LINEAR, -1.0, 16, 1,
                AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED, 16, -1.0,
                Format.byteArray);

        Codec encoder = open(className, f);
        Codec encoder2 = open(className2, f);

        // signed, big: all values with both bytes negative are wrong. Half of
        // the values are wrong.
        // signed, little: all values where first byte is negative is wrong.
        // Half are wrong.
        // unsigned, big: all values wrong
        // unsigned, little: all values wrong.

        byte[] data = new byte[2];

        int err = 0;
        for (int b0 = Byte.MIN_VALUE; b0 <= Byte.MAX_VALUE; ++b0)
        {
            for (int b1 = Byte.MIN_VALUE; b1 <= Byte.MAX_VALUE; ++b1)
            {
                data[0] = (byte) b0;
                data[1] = (byte) b1;
                byte res = encode(data, encoder, f);
                byte res2 = encode(data, encoder2, f);
                System.out.println("" + b0 + " " + b1 + ": " + res + " " + res2
                        + " " + (res != res2 ? "************" : ""));
                if (res != res2)
                    ++err;
                // assertEquals(res, res2);
            }
        }

        System.out.println("Errors: " + err);

    }
}
