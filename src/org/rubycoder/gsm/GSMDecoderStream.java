package org.rubycoder.gsm;

// $Id: GSMDecoderStream.java,v 1.3 2012/04/03 07:31:54 lyub0m1r Exp $

import java.io.*;

// will extend InputStream eventually
class GSMDecoderStream extends InputStream
{
    // make the 8k lookup table
    private static int[] lookup_table;

    private static int[] create_lookup_table()
    {
        int i;
        int pcm;
        int[] res = new int[8192];

        for (i = 0; i < 8192; i++)
        {
            pcm = i - 4096;
            // pcm in the range [-4096,4095]
            pcm <<= 3;
            // pcm in the range [-32768,32760]
            // (this is high as the gsm signal can go as the
            // last 3 bits set are fixed at zero)
            // res[i] = lin2mu(pcm)+128;
            // res[i] = (int)(lin2mu(pcm))&0xff;
            res[i] = (lin2mu(pcm)) & 0xff;
            // System.out.println("res["+i+":"+pcm+"]=="+res[i]);
        }

        return res;
    }

    private static byte lin2mu(int lin)
    {
        int s, e, f; // sign, exp, mant
        int topp;
        lin >>= 2;
        if (lin < 0)
        {
            lin = -lin;
            s = 1;
        } else
            s = 0;
        for (int n = 0; n < 8; n++)
        {
            topp = (0x20 << (n + 1)) - 0x20;
            if (lin < topp)
            {
                e = n;
                f = (lin - (0x20 << e) + 0x20) >>> (e + 1);
                // System.out.print(" s=" + s + " e=" + e + " f=" + f );
                return (byte) (((s << 7) | (e << 4) | (f & 0x0F)) ^ 0xFF);
            }
        }
        return (byte) (s << 8);
    }

    // the InputStream methods

    private InputStream GSMStream;

    private int gsm_index = 0;
    private final byte[] gsm_frame;
    private final int[] buffer;
    private int buffer_index = 0;
    private int buffer_size = 0;

    private final GSMDecoder theDecoder;

    public GSMDecoderStream(InputStream is)
    {
        if (lookup_table == null)
            lookup_table = create_lookup_table();
        gsm_index = 0;
        GSMStream = is;
        buffer = new int[160];
        buffer_index = 0;
        buffer_size = 0;
        gsm_frame = new byte[33];
        theDecoder = new GSMDecoder();
    }

    @Override
    public int available() throws IOException
    {
        int avail;

        // how many frames are still available?
        try
        {
            avail = GSMStream.available() / 33;
        } catch (IOException ioe)
        {
            throw new IOException("Recieved IO Exception from source stream.");
        } catch (NullPointerException npe)
        {
            throw new IOException("Source stream not open.");
        }
        // each frame is 160 bytes long
        avail *= 160;
        // how much is in the buffer?
        avail += buffer_size - buffer_index;

        return avail;
    }

    @Override
    public void close()
    {
        GSMStream = null;
    }

    @Override
    public synchronized void mark(int size)
    {
        return;
    }

    @Override
    public boolean markSupported()
    {
        return false;
    }

    @Override
    public final int read()
    {
        int res;
        // System.out.println("read()");
        int read_count = 0;
        if (buffer_index >= buffer_size)
        {
            // can't handle any less than 33 bytes
            // System.out.println("reading a frame");
            try
            {
                read_count = GSMStream.read(gsm_frame);
            } catch (IOException ioe)
            {
                System.out.println("got io exception");
                close();
                return -1;
            } catch (NullPointerException npe)
            {
                return -1;
            }
            if (read_count < 33)
            {
                // System.out.println("got less than 33 bytes");
                close();
                return -1;
            }
            // System.out.println("got "+read_count+" bytes from source");
            try
            {
                theDecoder.decode(gsm_frame, buffer);
            } catch (InvalidGSMFrameException igfe)
            {
                System.out.println("invalid frame");
                close();
                return -1;
            }
            buffer_index = 0;
            buffer_size = 160;
        }

        res = lookup_table[(buffer[buffer_index++] >> 3) + 4096];
        return res;
    }

    @Override
    public final int read(byte output[])
    {
        return read(output, 0, output.length);
    }

    @Override
    public final int read(byte output[], int start, int length)
    {
        // System.out.println("read(..,"+start+","+length+");");
        int val, i;
        if (GSMStream == null)
            return -1;
        for (i = start; i < length; i++)
        {
            read:
            {
                // System.out.println("read()");
                int read_count = 0;
                if (buffer_index >= buffer_size)
                {
                    // can't handle any less than 33 bytes
                    // System.out.println("reading a frame");
                    gsm_index = 0;
                    while (gsm_index < 33)
                    {
                        try
                        {
                            read_count = GSMStream.read(gsm_frame, gsm_index,
                                    33 - gsm_index);
                        } catch (IOException ioe)
                        {
                            System.out.println("got io exception");
                            close();
                            val = -1;
                            break read;
                        } catch (NullPointerException npe)
                        {
                            val = -1;
                            break read;
                        }
                        // System.out.println("got "+read_count+" bytes from source");
                        // if (read_count<33) {
                        // System.out.println("got less than 33 bytes");
                        // close();
                        // val = -1;
                        // break read;
                        // }
                        if (read_count < 0)
                        {
                            System.out.println("got eof");
                            close();
                            val = -1;
                            break read;
                        }
                        gsm_index += read_count;
                    }
                    try
                    {
                        theDecoder.decode(gsm_frame, buffer);
                    } catch (InvalidGSMFrameException igfe)
                    {
                        System.out.println("invalid frame");
                        close();
                        val = -1;
                        break read;
                    }
                    buffer_index = 0;
                    buffer_size = 160;
                }

                val = lookup_table[(buffer[buffer_index++] >> 3) + 4096];
            }
            // return res;
            // val = read();

            if (val < 0)
            {
                // System.out.println("got negative on read");
                break;
            }
            output[i] = (byte) (val);
            // System.out.println("output["+i+"]="+output[i]);
        }
        // System.out.println("read "+i+" bytes.");
        return i;
    }

    @Override
    public void reset()
    {
        return;
    }

    public void skip(int n)
    {
        // not implemented yet
        return;
    }

}
