package net.sf.fmj.media.codec.audio;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.utility.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RateConverterTest extends TestCase
{
    final int[] endianValues = new int[] { AudioFormat.BIG_ENDIAN,
            AudioFormat.LITTLE_ENDIAN };

    final int[] signedValues = new int[] { AudioFormat.SIGNED,
            AudioFormat.UNSIGNED };

    private void dump(byte[] data, int len)
    {
        for (int i = 0; i < len; ++i)
        {
            if (i > 0)
                System.out.print(", ");
            System.out.print(data[i]);
        }
        System.out.println();
    }

    void test(Format inputFormat, Format outputFormat, byte[] inputBufferData,
            byte[] targetOutputBufferData)
    {
        RateConverter c = new RateConverter();
        c.setInputFormat(inputFormat);
        c.setOutputFormat(outputFormat);

        try
        {
            c.open();
        } catch (ResourceUnavailableException ex)
        {
            throw new RuntimeException(ex);
        }
        Buffer b = new Buffer();
        b.setFormat(inputFormat);
        b.setData(inputBufferData);
        b.setOffset(0);
        b.setLength(inputBufferData.length);
        Buffer outputBuffer = new Buffer();

        assertEquals(c.process(b, outputBuffer), PlugIn.BUFFER_PROCESSED_OK);

        assertEquals(outputBuffer.getOffset(), 0);
        byte[] outputBufferData = (byte[]) outputBuffer.getData();

        if (outputBuffer.getLength() != targetOutputBufferData.length)
        {
            System.out.print("output:");
            dump(outputBufferData, outputBuffer.getLength());
            System.out.print("target:");
            dump(targetOutputBufferData, targetOutputBufferData.length);
        }
        assertEquals(outputBuffer.getLength(), targetOutputBufferData.length);
        assertEquals(outputBuffer.getFlags(), 0);
        assertEquals(outputBuffer.getFormat(), outputFormat);

        boolean eq = true;
        for (int i = 0; i < outputBuffer.getLength(); ++i)
        {
            if (outputBufferData[i] != targetOutputBufferData[i])
            {
                eq = false;
                break;
            }
        }

        if (!eq)
        {
            Buffer bClone = (Buffer) outputBuffer.clone();
            bClone.setData(targetOutputBufferData);
            System.err.println("Target: "
                    + LoggingStringUtils.bufferToStr(bClone));
            System.err.println("Actual: "
                    + LoggingStringUtils.bufferToStr(outputBuffer));
        }

        for (int i = 0; i < outputBuffer.getLength(); ++i)
        {
            if (outputBufferData[i] != targetOutputBufferData[i])
            {
                System.out.print("output:");
                dump(outputBufferData, outputBuffer.getLength());
                System.out.print("target:");
                dump(targetOutputBufferData, targetOutputBufferData.length);
            }
            assertEquals(outputBufferData[i], targetOutputBufferData[i]);
        }

        c.close();
    }

    /** test non-changing conversion, 8-bit signed */
    public void test1()
    {
        for (int i = 0; i < 256; ++i)
        {
            byte[] inputBufferData = new byte[] { (byte) i };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, inputBufferData);
        }
    }

    /** Test sample rate conversion - 3:2 */
    public void test10()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8, 10, 12, 14 };
            byte[] targetOutputBufferData = new byte[] { 3, 5, 9, 11 };

            // (2 + (4/2)) / 1.5 = 2.67 = 3 rounded
            // ((4/2) + 6) / 1.5 = 5.33 = 5 rounded
            // (8 + (10/2)) / 1.5 = 8.67 = 9 rounded
            // (10/2) + 12 / 1.5 = 11.33 = 11 rounded

            test(new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 3:2 */
    public void test11()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8, 10, 12, 14, 16 };
            byte[] targetOutputBufferData = new byte[] { 3, 5, 9, 11, 15 };

            // (2 + (4/2)) / 1.5 = 2.67 = 3 rounded
            // ((4/2) + 6) / 1.5 = 5.33 = 5 rounded
            // (8 + (10/2)) / 1.5 = 8.67 = 9 rounded
            // (10/2) + 12 / 1.5 = 11.33 = 11 rounded
            // (14 * (16/2)) / 1.5 = 14.67 = 15 rounded

            test(new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 2:3 */
    public void test12()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4 };
            byte[] targetOutputBufferData = new byte[] { 2, 3, 4 };

            test(new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 2:3 */
    public void test13()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6 };
            byte[] targetOutputBufferData = new byte[] { 2, 3, 4, 6 };

            test(new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 2:3 */
    public void test14()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8 };
            byte[] targetOutputBufferData = new byte[] { 2, 3, 4, 6, 7, 8 };

            test(new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 3:5 */
    public void test14_5()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6 };
            byte[] targetOutputBufferData = new byte[] { 2, 3, 4, 5, 6 };

            test(new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 5000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test mono to stereo */
    public void test15()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8 };
            byte[] targetOutputBufferData = new byte[] { 2, 2, 4, 4, 6, 6, 8, 8 };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 2, -1,
                            AudioFormat.SIGNED, 16, 4000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test stereo to mono */
    public void test16()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8 };
            byte[] targetOutputBufferData = new byte[] { 2, 6 };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 2, -1,
                    AudioFormat.SIGNED, 16, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test unsigned to signed */
    public void test17()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 127, (byte) 128,
                    (byte) 129, (byte) 255 };
            byte[] targetOutputBufferData = new byte[] { -128, -1, 0, 1, 127 };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.UNSIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test signed to unsigned */
    public void test18()
    {
        {
            byte[] inputBufferData = new byte[] { -128, -1, 0, 1, 127 };
            byte[] targetOutputBufferData = new byte[] { 0, 127, (byte) 128,
                    (byte) 129, (byte) 255 };
            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.UNSIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test little to big endian. */
    public void test19()
    {
        for (int signed : signedValues)
        {
            byte[] inputBufferData = new byte[] { 1, 2, 3, 4 };
            byte[] targetOutputBufferData = new byte[] { 2, 1, 4, 3 };
            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                    AudioFormat.LITTLE_ENDIAN, signed, 16, 4000.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    4000.0, 16, 1, AudioFormat.BIG_ENDIAN, signed, 16, 4000.0,
                    Format.byteArray), inputBufferData, targetOutputBufferData);
        }
    }

    /** test non-changing conversion, 8-bit signed */
    public void test2()
    {
        for (int i = 0; i < 256; ++i)
        {
            byte[] inputBufferData = new byte[] { 0, (byte) i };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, inputBufferData);
        }
    }

    /** Test big to little endian. */
    public void test20()
    {
        for (int signed : signedValues)
        {
            byte[] inputBufferData = new byte[] { 1, 2, 3, 4 };
            byte[] targetOutputBufferData = new byte[] { 2, 1, 4, 3 };
            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                    AudioFormat.BIG_ENDIAN, signed, 16, 4000.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    4000.0, 16, 1, AudioFormat.LITTLE_ENDIAN, signed, 16,
                    4000.0, Format.byteArray), inputBufferData,
                    targetOutputBufferData);
        }
    }

    /** Test16-bit to 8-bit, stereo */
    public void test21()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 1, 0, 2 };
            byte[] targetOutputBufferData = new byte[] { 1, 2 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 2,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 32, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    44100.0, 8, 2, AudioFormat.LITTLE_ENDIAN,
                    AudioFormat.SIGNED, 16, 44100.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test32-bit to 16-bit, mono */
    public void test22()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 0, 1, 2 };
            byte[] targetOutputBufferData = new byte[] { 1, 2 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 1,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 32, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    44100.0, 16, 1, AudioFormat.LITTLE_ENDIAN,
                    AudioFormat.SIGNED, 16, 44100.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test32-bit to 16-bit, stereo */
    public void test23()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 0, 3, 4, 0, 0, 7, 8 };
            byte[] targetOutputBufferData = new byte[] { 3, 4, 7, 8 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 2,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 64, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    44100.0, 16, 2, AudioFormat.LITTLE_ENDIAN,
                    AudioFormat.SIGNED, 32, 44100.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test32-bit to 16-bit, stereo - LE to BE */
    public void test24()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 0, 3, 4, 0, 0, 7, 8 };
            byte[] targetOutputBufferData = new byte[] { 4, 3, 8, 7 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 2,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 64, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    44100.0, 16, 2, AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED,
                    32, 44100.0, Format.byteArray), inputBufferData,
                    targetOutputBufferData);
        }
    }

    /** Test32-bit to 16-bit - LE to BE, stereo to mono */
    public void test25()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 0, 3, 4, 0, 0, 3, 4 };
            byte[] targetOutputBufferData = new byte[] { 4, 3 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 2,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 64, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    44100.0, 16, 1, AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED,
                    16, 44100.0, Format.byteArray), inputBufferData,
                    targetOutputBufferData);
        }
    }

    /** Test32-bit to 16-bit - downsampling from 44100 to 22050 */
    public void test27()
    {
        {
            byte[] inputBufferData = new byte[] { 0, 0, 3, 4, 0, 0, 7, 8, 0, 0,
                    3, 4, 0, 0, 7, 8 };
            byte[] targetOutputBufferData = new byte[] { 3, 4, 7, 8 };

            test(new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 2,
                    AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 64, 44100.0,
                    Format.byteArray), new AudioFormat(AudioFormat.LINEAR,
                    22050.0, 16, 2, AudioFormat.LITTLE_ENDIAN,
                    AudioFormat.SIGNED, 32, 22050.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test non-changing conversion, 16-bit, iterate through endian, signed. */
    public void test3()
    {
        for (int i = 0; i < 256; ++i)
        {
            final byte[] inputBufferData = new byte[] { 0, (byte) i, (byte) i,
                    0 };

            for (int k = 0; k < endianValues.length; ++k)
            {
                final int endian = endianValues[k];

                for (int m = 0; m < signedValues.length; ++m)
                {
                    final int signed = signedValues[m];

                    test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                            endian, signed, 16, 4000.0, Format.byteArray),
                            new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                                    endian, signed, 16, 4000.0,
                                    Format.byteArray), inputBufferData,
                            inputBufferData);
                }
            }
        }
    }

    /**
     * Test non-changing conversion, iterate through sample sizes, endian,
     * signed.
     */
    public void test4()
    {
        final int[] sampleSizesInBits = new int[] { 8, 16 };

        for (int i = 0; i < 256; ++i)
        {
            final byte[] inputBufferData = new byte[] { 0, (byte) i, 0,
                    (byte) i };

            for (int j = 0; j < sampleSizesInBits.length; ++j)
            {
                final int sampleSizeInBits = sampleSizesInBits[j];
                for (int k = 0; k < endianValues.length; ++k)
                {
                    final int endian = endianValues[k];
                    for (int m = 0; m < signedValues.length; ++m)
                    {
                        final int signed = signedValues[m];

                        test(new AudioFormat(AudioFormat.LINEAR, 4000.0,
                                sampleSizeInBits, 1, endian, signed,
                                sampleSizeInBits, 4000.0, Format.byteArray),
                                new AudioFormat(AudioFormat.LINEAR, 4000.0,
                                        sampleSizeInBits, 1, endian, signed,
                                        sampleSizeInBits, 4000.0,
                                        Format.byteArray), inputBufferData,
                                inputBufferData);
                    }
                }
            }
        }
    }

    /** Test converting 16-bit to 8-bit, vary endian-ness */
    public void test5()
    {
        for (int i = 0; i < 256; ++i)
        {
            final byte[] inputBufferData = new byte[] { 0, (byte) i, (byte) i,
                    0 };

            for (int k = 0; k < endianValues.length; ++k)
            {
                final int endian = endianValues[k];

                final byte[] outputBufferTargetData;
                if (endian == AudioFormat.LITTLE_ENDIAN)
                    outputBufferTargetData = new byte[] { (byte) i, 0 };
                else
                    outputBufferTargetData = new byte[] { 0, (byte) i };

                for (int m = 0; m < signedValues.length; ++m)
                {
                    final int signed = signedValues[m];

                    test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                            endian, signed, 16, 4000.0, Format.byteArray),
                            new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1,
                                    endian, signed, 8, 4000.0, Format.byteArray),
                            inputBufferData, outputBufferTargetData);
                }
            }
        }
    }

    /** Test conversion of 8-bit to 16-bit, iterate through endian. */
    public void test6()
    {
        for (int i = 0; i < 256; ++i)
        {
            final byte[] inputBufferData = new byte[] { (byte) i, 0 };

            for (int k = 0; k < endianValues.length; ++k)
            {
                final int endian = endianValues[k];

                final byte[] outputBufferTargetData;
                if (endian == AudioFormat.BIG_ENDIAN)
                    outputBufferTargetData = new byte[] { (byte) i, 0, 0, 0 };
                else
                    outputBufferTargetData = new byte[] { 0, (byte) i, 0, 0 };

                for (int m = 0; m < signedValues.length; ++m)
                {
                    final int signed = signedValues[m];

                    test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1,
                            endian, signed, 8, 4000.0, Format.byteArray),
                            new AudioFormat(AudioFormat.LINEAR, 4000.0, 16, 1,
                                    endian, signed, 16, 4000.0,
                                    Format.byteArray), inputBufferData,
                            outputBufferTargetData);
                }
            }
        }
    }

    // stereo-to mono just drops 1 channel, instead of averaging, so the
    // following test won't work:
    // /** Test32-bit to 16-bit - LE to BE, stereo to mono */
    // public void test26()
    // {
    // {
    // byte[] inputBufferData = new byte[] {0, 0, 3, 4, 0, 0, 5, 6};
    // byte[] targetOutputBufferData = new byte[] {5, 4};
    //
    //
    // test( new AudioFormat(AudioFormat.LINEAR, 44100.0, 32, 2,
    // AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED, 64, 44100.0,
    // Format.byteArray),
    // new AudioFormat(AudioFormat.LINEAR, 44100.0, 16, 1,
    // AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED, 16, 44100.0,
    // Format.byteArray),
    // inputBufferData,
    // targetOutputBufferData
    // );
    // }
    // }

    // TODO: test sample rate conversion
    /** Test sample rate conversion - halving */
    public void test7()
    {
        for (int i = 0; i < 256; ++i)
        {
            byte[] inputBufferData = new byte[] { (byte) i, (byte) i, 0, 2 };
            byte[] targetOutputBufferData = new byte[] { (byte) i, 1 };

            test(new AudioFormat(AudioFormat.LINEAR, 8000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 8000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - doubling */
    public void test8()
    {
        for (int i = 0; i < 256; ++i)
        {
            byte[] inputBufferData = new byte[] { (byte) i, 0, 2 };
            byte[] targetOutputBufferData = new byte[] { (byte) i, (byte) i, 0,
                    0, 2, 2 };

            test(new AudioFormat(AudioFormat.LINEAR, 4000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 4000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 8000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 8000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }

    /** Test sample rate conversion - 3:2 */
    public void test9()
    {
        {
            byte[] inputBufferData = new byte[] { 2, 4, 6, 8, 10, 12 };
            byte[] targetOutputBufferData = new byte[] { 3, 5, 9, 11 };

            // (2 + (4/2)) / 1.5 = 2.67 = 3 rounded
            // ((4/2) + 6) / 1.5 = 5.33 = 5 rounded
            // (8 + (10/2)) / 1.5 = 8.67 = 9 rounded
            // (10/2) + 12 / 1.5 = 11.33 = 11 rounded

            test(new AudioFormat(AudioFormat.LINEAR, 3000.0, 8, 1, -1,
                    AudioFormat.SIGNED, 8, 3000.0, Format.byteArray),
                    new AudioFormat(AudioFormat.LINEAR, 2000.0, 8, 1, -1,
                            AudioFormat.SIGNED, 8, 2000.0, Format.byteArray),
                    inputBufferData, targetOutputBufferData);
        }
    }
}
