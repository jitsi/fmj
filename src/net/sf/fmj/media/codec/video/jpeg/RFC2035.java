package net.sf.fmj.media.codec.video.jpeg;

import net.sf.fmj.utility.*;

/**
 * Code from RFC 2035 - RTP Payload Format for JPEG Video. See
 * http://www.rfc-archive.org/getrfc.php?rfc=2035 Ported to Java from C by Ken
 * Larson. TODO: Obsoleted by RFC2435. See
 * http://rfc.sunsite.dk/rfc/rfc2435.html
 *
 * @author Ken Larson
 * @author Martin Harvan
 */
public class RFC2035
{
    // Appendix A
    //
    // The following code can be used to create a quantization table from a
    // Q factor:

    // kenlars99: the sample code appears to have a mistake. Several places on
    // the
    // web refer to the q tables being in "zigzag" order.
    // http://www.obrador.com/essentialjpeg/headerinfo.htm
    // says "the quantization tables are stored in zigzag format". The RFC does
    // not say this, and the code
    // does not appear to do it.
    // Some LGPL code (JPEGVideoRTPSource.cpp) provides different tables, which
    // we will use here:

    // The default 'luma' and 'chroma' quantizer tables, in zigzag order:
    public static final int[] jpeg_luma_quantizer_zigzag = new int[] {
            // luma table:
            16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18, 17, 16, 19, 24, 40, 26,
            24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61, 60, 57, 51, 56, 55,
            64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109, 81, 87, 95, 98,
            103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92, 101, 103, 99, };
    public static final int[] jpeg_chroma_quantizer_zigzag = new int[] {
            // chroma table:
            17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99, 66, 56, 66, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

    // non-zigzagged versions:
    /**
     * Table K.1 from JPEG spec.
     */

    // normal order version is needed for JPEGQTable constructor - it expects
    // qtable in natural order..
    public static final int[] jpeg_luma_quantizer_normal = new int[] { 16, 11,
            10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16,
            24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56,
            68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87,
            103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99 };

    /**
     * Table K.2 from JPEG spec.
     */
    public static final int[] jpeg_chroma_quantizer_normal = new int[] { 17,
            18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26,
            56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
            99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

    public static final short /* u_char */lum_dc_codelens[] = { 0, 1, 5, 1, 1,
            1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, };

    public static final short /* u_char */lum_dc_symbols[] = { 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, };

    // Appendix B
    //
    // The following routines can be used to create the JPEG marker segments
    // corresponding to the table-specification data that is absent from the
    // RTP/JPEG body.

    // I changed all the huffman table lengths and symbols/values to short
    // arrays.. JPEGHuffmanTable constructor expects short[]
    // also I removed all the redundant casts to byte. This should not affect
    // functionality.

    public static final short /* u_char */lum_ac_codelens[] = { 0, 2, 1, 3, 3,
            2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d, };

    public static final short /* u_char */lum_ac_symbols[] = { 0x01, 0x02,
            0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13,
            0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08,
            0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33, 0x62,
            0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x25, 0x26,
            0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a,
            0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54, 0x55,
            0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
            0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x83,
            0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95,
            0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7,
            0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9,
            0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2,
            0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2, 0xe3,
            0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3, 0xf4,
            0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, };

    public static final short /* u_char */chm_dc_codelens[] = { 0, 3, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, };

    public static final short /* u_char */chm_dc_symbols[] = { 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, };

    public static final short /* u_char */chm_ac_codelens[] = { 0, 2, 1, 2, 4,
            4, 3, 4, 7, 5, 4, 4, 0, 1, 2, (byte) 0x77, };

    public static final short /* u_char */chm_ac_symbols[] = { 0x00, 0x01,
            0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51,
            0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
            0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0, 0x15, 0x62, 0x72,
            0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19,
            0x1a, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 0x39,
            0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54,
            0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67,
            0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a,
            0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93,
            0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5,
            0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7,
            0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9,
            0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe2,
            0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2, 0xf3, 0xf4,
            0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa };

    /**
     * Creates ZigZag-ordered array from given regular-order array, can only be
     * used for arrays of 64 elements, since it uses xmax=8, ymax=8
     *
     * @param array
     *            array in normal order
     * @return array in zigzag order
     */
    public static int[] createZigZag(int[] array)
    {
        return createZigZag(array, 8, 8);
    }

    /**
     * Creates ZigZag-ordered array from regular-order array
     *
     * @param array
     *            array in normal order
     * @param xmax
     *            maximal x dimension of array
     * @param ymax
     *            maximal y dimension of array
     * @return array in zigzag order
     */
    public static int[] createZigZag(int[] array, int xmax, int ymax)
    {
        int[] zz = new int[array.length];
        if (array.length != xmax * ymax)
        {
            throw new IllegalArgumentException();
        }
        int ai = 0;
        int zzi = 0;
        int x = 0;
        int y = 0;
        zz[zzi] = array[ai];
        while (y * xmax + x < xmax * ymax - 1)
        {
            if (x < xmax - 1)
                x++;
            else
                y++;
            zzi++;
            ai = y * xmax + x;
            zz[zzi] = array[ai];

            while (x > 0 && y < ymax - 1)
            {
                x--;
                y++;
                zzi++;
                ai = y * xmax + x;
                zz[zzi] = array[ai];

            }

            // if (x<y += 1;
            if (y < ymax - 1)
                y++;
            else
                x++;
            zzi++;
            ai = y * xmax + x;
            zz[zzi] = array[ai];

            while (y > 0 && x < xmax - 1)
            {
                y--;
                x++;
                zzi++;
                ai = y * xmax + x;
                zz[zzi] = array[ai];
            }
        }

        return zz;
    }

    /**
     * Constructs the DRI header for constructed Jpeg file (DRI header should be
     * in form FF DD 00 04 DRI) DRI is 16-bits
     *
     * @param p
     *            data array to append the headear to
     * @param i
     *            initial offset of the data array
     * @param dri
     *            Restart interval
     * @return changed offset of the data array
     */
    private static int MakeDRIHeader(byte[] p, int i, int dri)
    {
        p[i++] = (byte) 0xFF;
        p[i++] = (byte) 0xDD;
        p[i++] = (byte) 0x00;
        p[i++] = (byte) 0x04;
        p[i++] = (byte) (dri >> 8);
        p[i++] = (byte) (dri & 0xFF);

        return i;
    }

    /**
     * The old MakeHeaders method. Can be called if we do not have custom lqt
     * and cqt tables and we do not have dri (dri == 0).
     *
     * @param includeSOI
     *            specifies wheter SOI should be prepended
     * @param p
     *            destination for created headers
     * @param i
     *            initial offset of p
     * @param type
     *            value of type parameter for the JPEG/RTP frame
     * @param q
     *            value of Q parameter for the JPEG/RTP frame
     * @param w
     *            width of JPEG/RTP frame (in blocks)
     * @param h
     *            height of JPEG/RTP frame (in blocks)
     * @return new offset value
     */
    public static int MakeHeaders(boolean includeSOI, byte[] /* u_char * */p,
            int i, int type, int q, int w, int h)
    { // Moved content of original MakeHeaders method into new method which
      // allows to use specified Qtables
      // if we send null luma and chroma it will create Qtables from q
      // parameter.
        return MakeHeaders(includeSOI, p, i, type, q, w, h, null, null, 0);
    }

    /**
     * Given an RTP/JPEG type code, q factor, width, and height, generate a
     * frame and scan headers that can be prepended to the RTP/JPEG data payload
     * to produce a JPEG compressed image in interchange format (except for
     * possible trailing garbage and absence of an EOI marker to terminate the
     * scan).
     *
     * @param p
     *            destination for the created data
     * @param i
     *            initial offset of p
     * @param includeSOI
     *            - kenlars99 - not in original RFC sample code, allows us to
     *            control whether to include the initial SOI marker (0xFFD8).
     *            Turn this off if there are other headers before these, such as
     *            the JFIF header. If false, caller is responsible for the
     *            initial SOI marker.
     * @param type
     *            value of type parameter for the JPEG/RTP frame
     * @param q
     *            value of q parameter for the JPEG/RTP frame
     * @param w
     *            width of JPEG/RTP frame (in blocks)
     * @param h
     *            height of JPEG/RTP frame (in blocks)
     * @param luma
     *            - kane77 - used to specify luma table if q>127, if null then
     *            tables are computed the normal way
     * @param chroma
     *            - kane77 - used to specify chroma table if q>127, if null then
     *            tables are computed the normal way
     * @param dri
     *            - kane77 - used to specify restart interval, if dri==0 then no
     *            DRI header is created.
     * @return new offset value
     */
    public static int MakeHeaders(boolean includeSOI, byte[] /* u_char * */p,
            int i, int type, int q, int w, int h, byte[] luma, byte[] chroma,
            int dri)
    {
        byte[] /* u_char */lqt;
        byte[] /* u_char */cqt;
        // byte[] /* u_char * */start = p;
        // TODO when q>127 the tables cannot be null, and we should make sure we
        // either have both or no tables at all
        if (luma == null && chroma == null)
        {
            lqt = new byte[64];
            cqt = new byte[64];
            MakeTables(q, lqt, cqt);
        } else
        {
            lqt = luma;
            cqt = chroma;
        }

        /* convert from blocks to pixels */
        w <<= 3;
        h <<= 3;

        if (includeSOI)
        {
            p[i++] = (byte) 0xff;
            p[i++] = (byte) 0xd8; /* SOI */
        }
        i = MakeQuantHeader(p, i, lqt, 0);

        i = MakeQuantHeader(p, i, cqt, 1);

        if (dri != 0)
        {
            i = MakeDRIHeader(p, i, dri);
        }

        i = MakeHuffmanHeader(p, i, lum_dc_codelens, lum_dc_codelens.length,
                lum_dc_symbols, lum_dc_symbols.length, 0, 0);
        i = MakeHuffmanHeader(p, i, lum_ac_codelens, lum_ac_codelens.length,
                lum_ac_symbols, lum_ac_symbols.length, 0, 1);
        i = MakeHuffmanHeader(p, i, chm_dc_codelens, chm_dc_codelens.length,
                chm_dc_symbols, chm_dc_symbols.length, 1, 0);
        i = MakeHuffmanHeader(p, i, chm_ac_codelens, chm_ac_codelens.length,
                chm_ac_symbols, chm_ac_symbols.length, 1, 1);

        p[i++] = (byte) 0xff;
        p[i++] = (byte) 0xc0; /* SOF */
        p[i++] = (byte) 0; /* length msb */
        p[i++] = (byte) 17; /* length lsb */
        p[i++] = (byte) 8; /* 8-bit precision */
        p[i++] = (byte) (h >> 8); /* height msb */
        p[i++] = (byte) h; /* height lsb */
        p[i++] = (byte) (w >> 8); /* width msb */
        p[i++] = (byte) w; /* wudth lsb */
        p[i++] = (byte) 3; /* number of components */
        p[i++] = (byte) 1; /* comp 0 */
        if (type == 0)
            p[i++] = (byte) 0x21; /* hsamp = 2, vsamp = 1 */
        else
            p[i++] = (byte) 0x22; /* hsamp = 2, vsamp = 2 */
        p[i++] = (byte) 0; /* quant table 0 */
        p[i++] = (byte) 2; /* comp 1 */
        p[i++] = (byte) 0x11; /* hsamp = 1, vsamp = 1 */
        p[i++] = (byte) 1; /* quant table 1 */
        p[i++] = (byte) 3; /* comp 2 */// TODO components seem to be numbered
                                       // starting with 1 in encoded image
        p[i++] = (byte) 0x11; /* hsamp = 1, vsamp = 1 */
        p[i++] = (byte) 1; /* quant table 1 */

        p[i++] = (byte) 0xff;
        p[i++] = (byte) 0xda; /* SOS */
        p[i++] = (byte) 0; /* length msb */
        p[i++] = (byte) 12; /* length lsb */
        p[i++] = (byte) 3; /* 3 components */
        p[i++] = (byte) 1; /* comp 0 */

        p[i++] = (byte) 0; /* huffman table 0 */
        p[i++] = (byte) 2; /* comp 1 */
        p[i++] = (byte) 0x11; /* huffman table 1 */
        p[i++] = (byte) 3; /* comp 2 */
        p[i++] = (byte) 0x11; /* huffman table 1 */
        p[i++] = (byte) 0; /* first DCT coeff */
        p[i++] = (byte) 63; /* last DCT coeff */
        p[i++] = (byte) 0; /* sucessive approx. */

        return i;
    }

    /**
     * Creates Huffman header for given tables. Huffman header starts with 0xFF
     * 0xC4 and 2 bytes for length of the header.
     *
     * @param p
     *            destination for the header data
     * @param i
     *            initial offset of p
     * @param codelens
     *            array of codewords
     * @param ncodes
     *            number of codewords
     * @param symbols
     *            array of symbols
     * @param nsymbols
     *            number of symbols
     * @param tableNo
     *            table number
     * @param tableClass
     *            class of table
     * @return new offset value
     */
    private static int /* u_char * */
    MakeHuffmanHeader(byte[] /* u_char * */p, int i,
            short[] /* u_char * */codelens, int ncodes,
            short[] /* u_char * */symbols, int nsymbols, int tableNo,
            int tableClass)
    {
        p[i++] = (byte) 0xff;
        p[i++] = (byte) 0xc4; /* DHT */
        p[i++] = (byte) 0; /* length msb */
        p[i++] = (byte) (3 + ncodes + nsymbols); /* length lsb */
        p[i++] = (byte) (tableClass << 4 | tableNo);
        byte[] shortCodelens = ArrayUtility.shortArrayToByteArray(codelens);
        System.arraycopy(shortCodelens, 0, p, i, ncodes);
        i += ncodes;
        byte[] shortSymbols = ArrayUtility.shortArrayToByteArray(symbols);
        System.arraycopy(shortSymbols, 0, p, i, nsymbols);
        i += nsymbols;
        return i;
    }

    /**
     * Creates quantization header for given qtable. Quantization table starts
     * with bytes 0xFF and 0xDB followed by 2 bytes that specify length of the
     * tables.
     *
     * @param p
     *            destination for the created header
     * @param i
     *            initial offset
     * @param qt
     *            qtable data
     * @param tableNo
     *            qtable number
     * @return new offset in p
     */
    public static int /* u_char * */
    MakeQuantHeader(byte[] /* u_char * */p, int i, byte[] /* u_char * */qt,
            int tableNo)
    {
        p[i++] = (byte) 0xff;
        p[i++] = (byte) 0xdb; /* DQT */
        p[i++] = (byte) 0; /* length msb */
        p[i++] = (byte) 67; /* length lsb */// TODO: this length is correct only
                                            // for table length of 64, what if
                                            // the table is of different length?
        p[i++] = (byte) tableNo;
        System.arraycopy(qt, 0, p, i, 64);
        i += 64;
        return i;
    }

    private static void MakeTables(int q, byte[] /* u_char * */lum_q,
            byte[] /* u_char * */chr_q)
    {
        MakeTables(q, lum_q, chr_q, jpeg_luma_quantizer_zigzag,
                jpeg_luma_quantizer_zigzag);
    }

    /**
     * Call MakeTables with the Q factor and two int[64] return arrays
     *
     * @param q
     *            Q factor
     * @param lum_q
     *            input luminance jpeg qtable
     * @param chr_q
     *            input chrominance jpeg qtable
     * @param jpeg_luma
     *            returned luminance qtable
     * @param jpeg_chroma
     *            returned chrominance qtable
     */
    public static void MakeTables(int q, byte[] /* u_char * */lum_q,
            byte[] /* u_char * */chr_q, int[] jpeg_luma, int[] jpeg_chroma)
    {
        int i;
        int factor = q;

        if (q < 1)
            factor = 1;
        if (q > 99)
            factor = 99;
        if (q < 50)
            q = 5000 / factor;
        else
            q = 200 - factor * 2;

        for (i = 0; i < 64; i++)
        {
            int lq = (jpeg_luma[i] * q + 50) / 100;
            int cq = (jpeg_chroma[i] * q + 50) / 100;

            /* Limit the quantizers to 1 <= q <= 255 */
            if (lq < 1)
                lq = 1;
            else if (lq > 255)
                lq = 255;
            lum_q[i] = (byte) lq;

            if (cq < 1)
                cq = 1;
            else if (cq > 255)
                cq = 255;
            chr_q[i] = (byte) cq;
        }
    }

}