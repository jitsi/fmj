package org.rubycoder.gsm;

public class GSMEncoder
{
    private static final byte GSM_MAGIC = 0x0d;
    private static final int[] FAC = { 18431, 20479, 22527, 24575, 26623,
            28671, 30719, 32767 };

    private static int add(int a, int b)
    {
        int sum = a + b;
        return saturate(sum);
    }

    private static int asl(int a, int n)
    {
        if (n >= 16)
        {
            return 0;
        }
        if (n <= -16)
        {
            return (a < 0 ? -1 : 0);
        }
        if (n < 0)
        {
            return asr(a, -n);
        }
        return (a << n);
    }

    private static int asr(int a, int n)
    {
        if (n >= 16)
        {
            return (a < 0 ? -1 : 0);
        }
        if (n <= -16)
        {
            return 0;
        }
        if (n < 0)
        {
            return (a << -n);// &0xffff;
        }
        return (a >> n);
    }

    private static void Coefficients_40_159(int LARpp_j[], int LARp[])
    {
        int i;

        for (i = 0; i < 8; i++)
        {
            LARp[i] = LARpp_j[i];
        }
    }

    private static void LARp_to_rp(int LARp[])
    {
        int i;
        int temp;

        for (i = 0; i < 8; i++)
        {
            if (LARp[i] < 0)
            {
                temp = ((LARp[i] == MIN_WORD) ? MAX_WORD : -LARp[i]);
                LARp[i] = (-((temp < 11059) ? temp << 1
                        : ((temp < 20070) ? temp + 11059 : add((temp >> 2),
                                26112))));
            } else
            {
                temp = LARp[i];
                LARp[i] = ((temp < 11059) ? temp << 1
                        : ((temp < 20070) ? temp + 11059 : add((temp >> 2),
                                26112)));
            }
        }
    }

    public static void main(String args[])
    {
        GSMEncoder encoder = new GSMEncoder();
        int[] s = new int[160];
        encoder.encode(s);

    }

    private static int mult_r(int a, int b)
    {
        if (b == MIN_WORD && a == MIN_WORD)
        {
            return MAX_WORD;
        } else
        {
            int prod = a * b + 16384;
            // prod >>= 15;
            return saturate(prod >> 15);// &0xffff;
            // return (prod & 0xffff);
        }
    }

    public static void print(String name, int data[])
    {
        System.out.print("[" + name + ":");
        for (int i = 0; i < data.length; i++)
        {
            System.out.print("" + data[i]);
            if (i < data.length - 1)
            {
                System.out.print(",");
            } else
            {
                System.out.println("]");
            }
        }
    }

    public static void print(String name, int data)
    {
        System.out.println("[" + name + ":" + data + "]");
    }

    private static int saturate(int x)
    {
        return (x < MIN_WORD ? MIN_WORD : (x > MAX_WORD ? MAX_WORD : x));
    }

    private static int sub(int a, int b)
    {
        int diff = a - b;
        return saturate(diff);
    }

    private final int[] gsm_DLB = { 6554, 16384, 26214, 32767 };
    private static final byte[] bitoff = { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4,
            4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0 };
    private final int[] gsm_NRFAC = { 29128, 26215, 23832, 21846, 20165, 18725,
            17476, 16384 };
    private static final int MIN_WORD = -32767 - 1;
    private static final int MAX_WORD = 32767;
    private static final int MIN_LONGWORD = (-2147483647 - 1);
    private static final int MAX_LONGWORD = 2147483647;
    private final int[] dp0 = new int[280];
    private final int[] u = new int[8];
    private final int[][] LARpp = new int[2][8];
    private int j;
    private final int[] e = new int[50];
    private int z1;
    private int L_z2;
    private int mp;

    private int dpOffset;

    private int dppOffset;

    private int bcOffset;

    private int ncOffset;

    private int xmaxcOffset;

    private int eOffset;

    private int xmcOffset;

    private int dOffset;

    private int mcoffset = 0;

    private int abs(int a)
    {
        return ((a) < 0 ? ((a) == MIN_WORD ? MAX_WORD : -(a)) : (a));
    }

    private void APCM_quantization(int[] xM, int[] xMc, int[] mant_out,
            int[] exp_out, int[] xmaxc_out)
    {
        int i;
        boolean itest;

        int xmax, xmaxc, temp, temp1, temp2;
        int[] exp = new int[1];
        int[] mant = new int[1];

        /*
         * Find the maximum absolute value xmax of xM[0..12].
         */

        xmax = 0;
        for (i = 0; i <= 12; i++)
        {
            temp = xM[i];
            temp = abs(temp);
            if (temp > xmax)
                xmax = temp;
        }

        /*
         * Qantizing and coding of xmax to get xmaxc.
         */

        exp[0] = 0;
        temp = sasr(xmax, 9);
        itest = false;

        for (i = 0; i <= 5; i++)
        {
            itest |= (temp <= 0);
            temp = sasr(temp, 1);

            assert (exp[0] <= 5);
            if (!itest)
            {
                exp[0]++; /* exp = add (exp, 1) */

            }
        }

        assert (exp[0] <= 6 && exp[0] >= 0);
        temp = exp[0] + 5;

        assert (temp <= 11 && temp >= 0);

        xmaxc = add(sasr(xmax, temp), exp[0] << 3);
        /*
         * Quantizing and coding of the xM[0..12] RPE sequence to get the
         * xMc[0..12]
         */

        APCM_quantization_xmaxc_to_exp_mant(xmaxc, exp, mant);

        /*
         * This computation uses the fact that the decoded version of xmaxc can
         * be calculated by using the exponent and the mantissa part of xmaxc
         * (logarithmic table). So, this method avoids any division and uses
         * only a scaling of the RPE samples by a function of the exponent. A
         * direct multiplication by the inverse of the mantissa (NRFAC[0..7]
         * found in table 4.5) gives the 3 bit coded version xMc[0..12] of the
         * RPE samples.
         */

        /*
         * Direct computation of xMc[0..12] using table 4.5
         */

        assert (exp[0] <= 4096 && exp[0] >= -4096);
        assert (mant[0] >= 0 && mant[0] <= 7);

        temp1 = 6 - exp[0]; /* normalization by the exponent */
        temp2 = gsm_NRFAC[mant[0]]; /* inverse mantissa */

        for (i = 0; i <= 12; i++)
        {
            assert (temp1 >= 0 && temp1 < 16);

            temp = xM[i] << temp1;
            temp = gsm_mult(temp, temp2);
            temp = sasr(temp, 12);
            xMc[xmcOffset + i] = temp + 4;

        }

        /*
         * NOTE: This equation is used to make all the xMc[i] positive.
         */

        mant_out[0] = mant[0];
        exp_out[0] = exp[0];
        xmaxc_out[xmaxcOffset] = xmaxc;

    }

    private void APCM_quantization_xmaxc_to_exp_mant(int xmaxc, int[] exp_out,
            int[] mant_out)
    {
        int exp, mant;

        /*
         * Compute exponent and mantissa of the decoded version of xmaxc
         */

        exp = 0;
        if (xmaxc > 15)
            exp = sasr(xmaxc, 3) - 1;
        mant = xmaxc - (exp << 3);

        if (mant == 0)
        {
            exp = -4;
            mant = 7;
        } else
        {
            while (mant <= 7)
            {
                mant = mant << 1 | 1;
                exp--;
            }
            mant -= 8;
        }

        assert (exp >= -4 && exp <= 6);
        assert (mant >= 0 && mant <= 7);

        exp_out[0] = exp;
        mant_out[0] = mant;
    }

    private void APCMInverseQuantization(int xMc[], int exp, int mant,
            int xMp[])
    {
        int i;
        int temp, temp1, temp2, temp3;
        int xmpi = 0;

        assert (mant >= 0 && mant <= 7);
        int xmci = xmcOffset;
        temp1 = FAC[mant]; /* see 4.2-15 for mant */
        temp2 = sub(6, exp); /* see 4.2-15 for exp */
        temp3 = asl(1, sub(temp2, 1));

        for (i = 13; i-- > 0;)
        {
            assert (xMc[xmci] <= 7 && xMc[xmci] >= 0); /* 3 bit unsigned */

            /* temp = gsm_sub( *xMc++ << 1, 7 ); */
            temp = (xMc[xmci++] << 1) - 7; /* restore sign */
            assert (temp <= 7 && temp >= -7); /* 4 bit signed */

            temp <<= 12; /* 16 bit signed */
            temp = mult_r(temp1, temp);
            temp = add(temp, temp3);
            xMp[xmpi++] = asr(temp, temp2);
        }
    }

    private void Autocorrelation(int[] s, int[] l_acf)
    {
        int k, i;
        int si = dOffset;
        assert (dOffset == 0);
        int temp, smax, scalauto;

        smax = 0;
        for (k = 0; k <= 159; k++)
        {
            temp = abs(s[si + k]);
            if (temp > smax)
                smax = temp;
        }

        /*
         * Computation of the scaling factor.
         */
        if (smax == 0)
            scalauto = 0;
        else
        {
            assert (smax > 0);
            scalauto = 4 - gsm_norm(smax << 16);/* sub(4,..) */
        }

        /*
         * Scaling of the array s[0...159]
         */

        if (scalauto > 0)
        {
            switch (scalauto)
            {
            case 1:
                for (k = 0; k <= 159; k++)
                    s[k] = mult_r(s[k], 16384);
                break;
            case 2:
                for (k = 0; k <= 159; k++)
                    s[k] = mult_r(s[k], 16384 >> (2 - 1));
                break;
            case 3:
                for (k = 0; k <= 159; k++)
                    s[k] = mult_r(s[k], 16384 >> (3 - 1));
                break;
            case 4:
                for (k = 0; k <= 159; k++)
                    s[k] = mult_r(s[k], 16384 >> (4 - 1));
                break;

            }
        }
        int spi = 0;
        int sl = s[spi];
        for (k = 9; k > 0; k--)
            l_acf[k - 1] = 0;

        for (int j = 0; j < 8; j++)
        {
            for (int x = 0; x <= j; x++)
            {
                l_acf[x] += (sl * s[spi - x]);
            }
            if (j < 7)
                sl = s[++spi];
        }

        for (i = 8; i <= 159; i++)
        {
            sl = s[++spi];
            for (int j = 0; j <= 8; j++)
            {
                l_acf[j] += (sl * s[spi - j]);
            }
        }
        for (k = 9; k > 0; k--)
            l_acf[k - 1] <<= 1;

        /*
         * Rescaling of the array s[0..159]
         */
        if (scalauto > 0)
        {
            assert (scalauto <= 4);
            for (k = 160; k > 0; k--)
                s[si++] <<= scalauto;
        }
    }

    private void Calculation_of_the_LTP_parameters(int[] d, int[] dp,
            int[] bc_out, int[] nc_out)
    {
        int k, lambda;
        int Nc, bc;
        int[] wt = new int[40];

        int L_max, L_power;
        int R, S, dmax, scal;
        int temp;

        /*
         * Search of the optimum scaling of d[0..39].
         */
        dmax = 0;
        for (k = 0; k < 40; k++)
        {
            temp = d[dOffset + k];
            temp = abs(temp);
            if (temp > dmax)
                dmax = temp;
        }

        temp = 0;
        if (dmax == 0)
            scal = 0;
        else
        {
            assert (dmax > 0);
            temp = gsm_norm(dmax << 16);
        }

        if (temp > 6)
            scal = 0;
        else
            scal = 6 - temp;

        assert (scal >= 0);

        /*
         * Initialization of a working array wt
         */

        for (k = 0; k < 40; k++)
            wt[k] = sasr(d[dOffset + k], scal);

        /*
         * Search for the maximum cross-correlation and coding of the LTP lag
         */
        L_max = 0;
        Nc = 40; /* index for the maximum cross-correlation */

        for (lambda = 40; lambda <= 120; lambda++)
        {
            int L_result;
            L_result = wt[0] * dp[dpOffset - lambda];

            for (int i = 1; i < 40; i++)
            {
                L_result += wt[i] * dp[dpOffset + i - lambda];
            }

            if (L_result > L_max)
            {
                Nc = lambda;
                L_max = L_result;
            }
        }

        nc_out[ncOffset] = Nc;

        L_max <<= 1;

        /*
         * Rescaling of L_max
         */
        assert (scal <= 100 && scal >= -100);
        L_max >>= (6 - scal); /* sub(6, scal) */

        assert (Nc <= 120 && Nc >= 40);

        /*
         * Compute the power of the reconstructed short term residual signal
         * dp[..]
         */
        L_power = 0;
        for (k = 0; k <= 39; k++)
        {
            int L_temp;

            L_temp = sasr(dp[dpOffset + k - Nc], 3);
            L_power += L_temp * L_temp;
        }
        L_power <<= 1; /* from L_MULT */
        // System.out.println("L_max: "+L_max);

        /*
         * Normalization of L_max and L_power
         */
        if (L_max <= 0)
        {
            bc_out[bcOffset] = 0;
            return;
        }
        if (L_max >= L_power)
        {
            bc_out[bcOffset] = 3;
            return;
        }

        temp = gsm_norm(L_power);

        R = sasr(L_max << temp, 16);
        S = sasr(L_power << temp, 16);

        /*
         * Coding of the LTP gain
         */

        /*
         * Table 4.3a must be used to obtain the level DLB[i] for the
         * quantization of the LTP gain b to get the coded version bc.
         */
        for (bc = 0; bc <= 2; bc++)
            if (R <= gsm_mult(S, gsm_DLB[bc]))
                break;
        bc_out[bcOffset] = bc;

    }

    private void Coefficients_0_12(int LARpp_j_1[], int LARpp_j[], int LARp[])
    {
        int i;

        for (i = 0; i < 8; i++)
        {
            LARp[i] = add(sasr(LARpp_j_1[i], 2), sasr(LARpp_j[i], 2));
            LARp[i] = add(LARp[i], sasr(LARpp_j_1[i], 1));
        }
    }

    private void Coefficients_13_26(int LARpp_j_1[], int LARpp_j[], int LARp[])
    {
        int i;

        for (i = 0; i < 8; i++)
        {
            LARp[i] = add(sasr(LARpp_j_1[i], 1), sasr(LARpp_j[i], 1));
        }
    }

    private void Coefficients_27_39(int LARpp_j_1[], int LARpp_j[], int LARp[])
    {
        int i;

        for (i = 0; i < 8; i++)
        {
            LARp[i] = add(sasr(LARpp_j_1[i], 2), sasr(LARpp_j[i], 2));
            LARp[i] = add(LARp[i], sasr(LARpp_j[i], 1));
        }
    }

    private void DecodingOfTheCodedLogAreaRatios(int[] larc, int[] larpp)
    {
        int temp1;
        int larci = 0;
        int larppi = 0;

        // STEP(0, -32, 13107);
        temp1 = add(larc[larci++], -32) << 10;
        temp1 = sub(temp1, 0 << 1);
        temp1 = mult_r(13107, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(0, -32, 13107);
        temp1 = add(larc[larci++], -32) << 10;
        temp1 = sub(temp1, 0 << 1);
        temp1 = mult_r(13107, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(2048, -16, 13107);
        temp1 = add(larc[larci++], -16) << 10;
        temp1 = sub(temp1, 2048 << 1);
        temp1 = mult_r(13107, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(-2560, -16, 13107);
        temp1 = add(larc[larci++], -16) << 10;
        temp1 = sub(temp1, -2560 << 1);
        temp1 = mult_r(13107, temp1);
        larpp[larppi++] = add(temp1, temp1);

        // STEP(94, -8, 19223);
        temp1 = add(larc[larci++], -8) << 10;
        temp1 = sub(temp1, 94 << 1);
        temp1 = mult_r(19223, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(-1792, -8, 17476);
        temp1 = add(larc[larci++], -8) << 10;
        temp1 = sub(temp1, -1792 << 1);
        temp1 = mult_r(17476, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(-341, -4, 31454);
        temp1 = add(larc[larci++], -4) << 10;
        temp1 = sub(temp1, -341 << 1);
        temp1 = mult_r(31454, temp1);
        larpp[larppi++] = add(temp1, temp1);
        // STEP(-1144, -4, 29708);
        temp1 = add(larc[larci++], -4) << 10;
        temp1 = sub(temp1, -1144 << 1);
        temp1 = mult_r(29708, temp1);
        larpp[larppi++] = add(temp1, temp1);
    }

    public final void encode(byte c[], int s[])
    {
        int i = 0;

        int LARc[] = new int[8];
        int Nc[] = new int[4];
        int Mc[] = new int[4];
        int bc[] = new int[4];
        int xmaxc[] = new int[4];
        int xmc[] = new int[13 * 4];

        encoder(s, LARc, Nc, bc, Mc, xmaxc, xmc);

        c[i++] = (byte) ((GSM_MAGIC & 0xF) << 4 | LARc[0] >> 2 & 0xF);

        c[i++] = (byte) (((LARc[0] & 0x3) << 6) | (LARc[1] & 0x3F));
        c[i++] = (byte) (((LARc[2] & 0x1F) << 3) | ((LARc[3] >> 2) & 0x7));
        c[i++] = (byte) (((LARc[3] & 0x3) << 6) | ((LARc[4] & 0xF) << 2) | ((LARc[5] >> 2) & 0x3));
        c[i++] = (byte) (((LARc[5] & 0x3) << 6) | ((LARc[6] & 0x7) << 3) | (LARc[7] & 0x7));
        c[i++] = (byte) (((Nc[0] & 0x7F) << 1) | ((bc[0] >>> 1) & 0x1));
        c[i++] = (byte) (((bc[0] & 0x1) << 7) | ((Mc[0] & 0x3) << 5) | ((xmaxc[0] >> 1) & 0x1F));
        // System.out.println(bc[0]+" | "+Mc[0]+" | "+xmaxc[0]);
        c[i++] = (byte) (((xmaxc[0] & 0x1) << 7) | ((xmc[0] & 0x7) << 4)
                | ((xmc[1] & 0x7) << 1) | ((xmc[2] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[2] & 0x3) << 6) | ((xmc[3] & 0x7) << 3) | (xmc[4] & 0x7));
        c[i++] = (byte) (((xmc[5] & 0x7) << 5) /* 10 */
                | ((xmc[6] & 0x7) << 2) | ((xmc[7] >> 1) & 0x3));
        c[i++] = (byte) (((xmc[7] & 0x1) << 7) | ((xmc[8] & 0x7) << 4)
                | ((xmc[9] & 0x7) << 1) | ((xmc[10] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[10] & 0x3) << 6) | ((xmc[11] & 0x7) << 3) | (xmc[12] & 0x7));
        c[i++] = (byte) (((Nc[1] & 0x7F) << 1) | ((bc[1] >> 1) & 0x1));
        c[i++] = (byte) (((bc[1] & 0x1) << 7) | ((Mc[1] & 0x3) << 5) | ((xmaxc[1] >> 1) & 0x1F));
        c[i++] = (byte) (((xmaxc[1] & 0x1) << 7) | ((xmc[13] & 0x7) << 4)
                | ((xmc[14] & 0x7) << 1) | ((xmc[15] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[15] & 0x3) << 6) | ((xmc[16] & 0x7) << 3) | (xmc[17] & 0x7));
        c[i++] = (byte) (((xmc[18] & 0x7) << 5) | ((xmc[19] & 0x7) << 2) | ((xmc[20] >> 1) & 0x3));
        c[i++] = (byte) (((xmc[20] & 0x1) << 7) | ((xmc[21] & 0x7) << 4)
                | ((xmc[22] & 0x7) << 1) | ((xmc[23] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[23] & 0x3) << 6) | ((xmc[24] & 0x7) << 3) | (xmc[25] & 0x7));
        c[i++] = (byte) (((Nc[2] & 0x7F) << 1) /* 20 */
        | ((bc[2] >> 1) & 0x1));
        c[i++] = (byte) (((bc[2] & 0x1) << 7) | ((Mc[2] & 0x3) << 5) | ((xmaxc[2] >> 1) & 0x1F));
        c[i++] = (byte) (((xmaxc[2] & 0x1) << 7) | ((xmc[26] & 0x7) << 4)
                | ((xmc[27] & 0x7) << 1) | ((xmc[28] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[28] & 0x3) << 6) | ((xmc[29] & 0x7) << 3) | (xmc[30] & 0x7));
        c[i++] = (byte) (((xmc[31] & 0x7) << 5) | ((xmc[32] & 0x7) << 2) | ((xmc[33] >> 1) & 0x3));
        c[i++] = (byte) (((xmc[33] & 0x1) << 7) | ((xmc[34] & 0x7) << 4)
                | ((xmc[35] & 0x7) << 1) | ((xmc[36] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[36] & 0x3) << 6) | ((xmc[37] & 0x7) << 3) | (xmc[38] & 0x7));
        c[i++] = (byte) (((Nc[3] & 0x7F) << 1) | ((bc[3] >> 1) & 0x1));
        c[i++] = (byte) (((bc[3] & 0x1) << 7) | ((Mc[3] & 0x3) << 5) | ((xmaxc[3] >> 1) & 0x1F));
        c[i++] = (byte) (((xmaxc[3] & 0x1) << 7) | ((xmc[39] & 0x7) << 4)
                | ((xmc[40] & 0x7) << 1) | ((xmc[41] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[41] & 0x3) << 6) /* 30 */
                | ((xmc[42] & 0x7) << 3) | (xmc[43] & 0x7));
        c[i++] = (byte) (((xmc[44] & 0x7) << 5) | ((xmc[45] & 0x7) << 2) | ((xmc[46] >> 1) & 0x3));
        c[i++] = (byte) (((xmc[46] & 0x1) << 7) | ((xmc[47] & 0x7) << 4)
                | ((xmc[48] & 0x7) << 1) | ((xmc[49] >> 2) & 0x1));
        c[i++] = (byte) (((xmc[49] & 0x3) << 6) | ((xmc[50] & 0x7) << 3) | (xmc[51] & 0x7));
        // for (byte b : c) {
        // System.out.print(b + " ");
        // }
        // System.out.println("");
    }

    final int[] encode(int s[])
    {
        byte c[] = new byte[33];
        encode(c, s);
        return s;
    }

    private void encoder(int[] s, int[] LARc, int[] nc, int[] bc, int[] mc,
            int[] xmaxc, int[] xmc)
    {
        int k;
        int[] dp = dp0;
        int[] dpp = dp0;
        int so[] = new int[160];

        dpOffset = 120;
        dppOffset = 120;
        dOffset = 0;
        ncOffset = 0;
        bcOffset = 0;
        eOffset = 0;
        xmaxcOffset = 0;
        xmcOffset = 0;
        mcoffset = 0;
        GsmPreprocess(s, so);

        GsmLPCAnalysis(so, LARc);

        Gsm_Short_Term_Analysis_Filter(LARc, so);

        for (k = 0; k <= 3; k++, xmcOffset += 13)
        { // TODO START HERE!
            dOffset = k * 40;
            eOffset = 5;
            Gsm_Long_Term_Predictor(so, dp, e, dpp, nc, bc);
            Gsm_RPE_Encoding(e, xmaxc, mc, xmc);
            {
                int i;

                for (i = 0; i <= 39; i++)
                    dp[dpOffset + i] = add(e[5 + i], dpp[dppOffset + i]);
            }
            dpOffset += 40;
            dppOffset += 40;
            ncOffset++;
            bcOffset++;
            xmaxcOffset++;
            mcoffset++;
        }
        System.arraycopy(dp0, 0, dp0, 160, 120);
    }

    public void GSM()
    {
    }

    private int gsm_div(int num, int denum)
    {
        int L_num = num;
        int div = 0;
        int k = 15;
        // 220 "src/add.c"
        assert (num >= 0 && denum >= num);
        if (num == 0)
            return 0;

        while (k-- > 0)
        {
            div <<= 1;
            L_num <<= 1;

            if (L_num >= denum)
            {
                L_num -= denum;
                div++;
            }
        }

        return div;
    }

    private void Gsm_Long_Term_Predictor(int[] d, int[] dp, int[] e, int[] dpp,
            int[] nc, int[] bc)
    {
        // assert (dOffset != 0);
        // assert (dpOffset != 0);
        // assert (eOffset != 0);
        // assert (dppOffset != 0);
        // assert (ncOffset != 0);
        // assert (bcOffset != 0);

        Calculation_of_the_LTP_parameters(d, dp, bc, nc);

        Long_term_analysis_filtering(bc[bcOffset], nc[ncOffset], dp, d, dpp, e);
    }

    int gsm_mult(int a, int b)
    {
        if (a == (MIN_WORD) && b == (MIN_WORD))
            return MAX_WORD;
        else
            return ((a * b) >> (15));
    }

    private int gsm_norm(int a)
    {
        assert (a != 0);

        if (a < 0)
        {
            if (a <= -1073741824)
                return 0;
            a = ~a;
        }

        return (a & 0xffff0000) != 0 ? ((a & 0xff000000) != 0 ? -1
                + bitoff[0xFF & (a >> 24)] : 7 + bitoff[0xFF & (a >> 16)])
                : ((a & 0xff00) != 0 ? 15 + bitoff[0xFF & (a >> 8)]
                        : 23 + bitoff[0xFF & a]);
    }

    private void Gsm_RPE_Encoding(int[] e, int[] xmaxc, int[] mc, int[] xmc)
    {
        int[] x = new int[40];
        int[] xM = new int[13];
        int[] xMp = new int[13];
        int[] mant = new int[1];
        int[] exp = new int[1];

        Weighting_filter(e, x);
        RPE_grid_selection(x, xM, mc);
        APCM_quantization(xM, xmc, mant, exp, xmaxc);
        APCMInverseQuantization(xmc, exp[0], mant[0], xMp);
        RPE_grid_positioning(mc[mcoffset], xMp, e);
    }

    private void Gsm_Short_Term_Analysis_Filter(int[] larc, int[] s)
    {
        int[] LARpp_j = LARpp[j];
        int[] LARpp_j_1 = LARpp[j ^= 1];

        int[] larp = new int[8];

        DecodingOfTheCodedLogAreaRatios(larc, LARpp_j);

        Coefficients_0_12(LARpp_j_1, LARpp_j, larp);
        LARp_to_rp(larp);
        Short_term_analysis_filtering(larp, 13, s, 0);

        Coefficients_13_26(LARpp_j_1, LARpp_j, larp);
        LARp_to_rp(larp);
        Short_term_analysis_filtering(larp, 14, s, 13);

        Coefficients_27_39(LARpp_j_1, LARpp_j, larp);
        LARp_to_rp(larp);
        Short_term_analysis_filtering(larp, 13, s, 27);

        Coefficients_40_159(LARpp_j, larp);
        LARp_to_rp(larp);
        Short_term_analysis_filtering(larp, 120, s, 40);
    }

    private void GsmLPCAnalysis(int[] s, int[] LARc)
    {
        int L_ACF[] = new int[9];
        Autocorrelation(s, L_ACF);
        Reflection_coefficients(L_ACF, LARc);
        Transformation_to_Log_Area_Ratios(LARc);
        Quantization_and_coding(LARc);
    }

    final void GsmPreprocess(int[] s, int[] so)
    {
        int s1;
        int L_s2;

        int L_temp;

        int msp, lsp;
        int SO;

        int k = 160;
        int si = 0;
        int soi = 0;
        while (k-- > 0)
        {
            /*
             * 4.2.1 Downscaling of the input signal
             */
            SO = sasr(s[si], 3) << 2;
            si++;

            assert (SO >= -0x4000); /* downscaled by */
            assert (SO <= 0x3FFC); /* previous routine. */

            /*
             * 4.2.2 Offset compensation
             *
             * This part implements a high-pass filter and requires extended
             * arithmetic precision for the recursive part of this filter. The
             * input of this procedure is the array so[0...159] and the output
             * the array sof[ 0...159 ].
             */
            /*
             * Compute the non-recursive part
             */

            s1 = SO - z1; /* s1 = gsm_sub( *so, z1 ); */
            z1 = SO;

            assert (s1 != MIN_WORD);

            /*
             * Compute the recursive part
             */
            L_s2 = s1;
            L_s2 <<= 15;

            /*
             * Execution of a 31 bv 16 bits multiplication
             */

            msp = sasr(L_z2, 15);
            lsp = L_z2 - (msp << 15); /* gsm_L_sub(L_z2,(msp<<15)); */

            L_s2 += mult_r(lsp, 32735);
            L_temp = msp * 32735; /* GSM_L_MULT(msp,32735) >> 1; */
            L_z2 = (int) l_add(L_temp, L_s2); // TODO not sure about the cast..

            /*
             * Compute sof[k] with rounding
             */
            L_temp = (int) l_add(L_z2, 16384);

            /*
             * 4.2.3 Preemphasis
             */

            msp = mult_r(mp, -28180);
            mp = sasr(L_temp, 15);
            so[soi++] = add(mp, msp);

        }

    }

    private long l_add(int a, int b)
    {
        long utmp;
        return ((a) < 0 ? ((b) >= 0 ? (a) + (b) : (utmp = (long) -((a) + 1)
                + (long) -((b) + 1)) >= MAX_LONGWORD ? MIN_LONGWORD : -utmp - 2)
                : ((b) <= 0 ? (a) + (b)
                        : (utmp = (long) (a) + (long) (b)) >= MAX_LONGWORD ? MAX_LONGWORD
                                : utmp));
    }

    void Long_term_analysis_filtering(int bc, int nc, int[] dp, int[] d,
            int[] dpp, int[] e)
    {
        int k;

        switch (bc)
        {
        case 0:
            // STEP(3277);
            for (k = 0; k <= 39; k++)
            {
                dpp[dppOffset + k] = mult_r(3277, dp[dpOffset + k - nc]);
                e[eOffset + k] = sub(d[dOffset + k], dpp[dppOffset + k]);
            }
            break;
        case 1:
            // STEP(11469);
            for (k = 0; k <= 39; k++)
            {
                dpp[dppOffset + k] = mult_r(11469, dp[dpOffset + k - nc]);
                e[eOffset + k] = sub(d[dOffset + k], dpp[dppOffset + k]);
            }
            break;
        case 2:
            // STEP(21299);
            for (k = 0; k <= 39; k++)
            {
                dpp[dppOffset + k] = mult_r(21299, dp[dpOffset + k - nc]);
                e[eOffset + k] = sub(d[dOffset + k], dpp[dppOffset + k]);
            }
            break;
        case 3:
            // STEP(32767);
            for (k = 0; k <= 39; k++)
            {
                dpp[dppOffset + k] = mult_r(32767, dp[dpOffset + k - nc]);
                e[eOffset + k] = sub(d[dOffset + k], dpp[dppOffset + k]);
            }
            break;
        }

    }

    private void Quantization_and_coding(int[] lar)
    {
        int temp;

        int lari = 0;
        temp = gsm_mult(20480, lar[lari]);
        temp = add(temp, 0);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 31 ? 31 - -32 : (temp < -32 ? 0 : temp - -32);
        lari++;

        temp = gsm_mult(20480, lar[lari]);
        temp = add(temp, 0);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 31 ? 31 - -32 : (temp < -32 ? 0 : temp - -32);
        lari++;

        temp = gsm_mult(20480, lar[lari]);
        temp = add(temp, 2048);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 15 ? 15 - -16 : (temp < -16 ? 0 : temp - -16);
        lari++;

        temp = gsm_mult(20480, lar[lari]);
        temp = add(temp, -2560);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 15 ? 15 - -16 : (temp < -16 ? 0 : temp - -16);
        lari++;

        temp = gsm_mult(13964, lar[lari]);
        temp = add(temp, 94);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 7 ? 7 - -8 : (temp < -8 ? 0 : temp - -8);
        lari++;

        temp = gsm_mult(15360, lar[lari]);
        temp = add(temp, -1792);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 7 ? 7 - -8 : (temp < -8 ? 0 : temp - -8);
        lari++;

        temp = gsm_mult(8534, lar[lari]);
        temp = add(temp, -341);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 3 ? 3 - -4 : (temp < -4 ? 0 : temp - -4);
        lari++;

        temp = gsm_mult(9036, lar[lari]);
        temp = add(temp, -1144);
        temp = add(temp, 256);
        temp = sasr(temp, 9);
        lar[lari] = temp > 3 ? 3 - -4 : (temp < -4 ? 0 : temp - -4);
        lari++;

    }

    private void Reflection_coefficients(int[] l_acf, int[] r)
    {
        int i, m, n;
        int temp;
        int ri = 0;
        int[] ACF = new int[9]; /* 0..8 */
        int[] P = new int[9]; /* 0..8 */
        int[] K = new int[9]; /* 2..8 */

        /*
         * Schur recursion with 16 bits arithmetic.
         */

        if (l_acf[0] == 0)
        {
            for (i = 8; i > 0; i--)
                r[ri++] = 0;
            return;
        }

        assert (l_acf[0] != 0);
        temp = gsm_norm(l_acf[0]);

        assert (temp >= 0 && temp < 32);

        /* ? overflow ? */
        for (i = 0; i <= 8; i++)
            ACF[i] = sasr(l_acf[i] << temp, 16);

        /*
         * Initialize array P[..] and K[..] for the recursion.
         */

        for (i = 1; i <= 7; i++)
            K[i] = ACF[i];
        for (i = 0; i <= 8; i++)
            P[i] = ACF[i];

        /*
         * Compute reflection coefficients
         */
        for (n = 1; n <= 8; n++, ri++)
        {
            temp = P[1];
            temp = abs(temp);
            if (P[0] < temp)
            {
                for (i = n; i <= 8; i++)
                    r[ri++] = 0;
                return;
            }

            r[ri] = gsm_div(temp, P[0]);

            assert (r[ri] >= 0);
            if (P[1] > 0)
                r[ri] = -r[ri]; /* r[n] = sub(0, r[n]) */
            assert (r[ri] != MIN_WORD);
            if (n == 8)
                return;

            /*
             * Schur recursion
             */
            temp = mult_r(P[1], r[ri]);
            P[0] = add(P[0], temp);

            for (m = 1; m <= 8 - n; m++)
            {
                temp = mult_r(K[m], r[ri]);
                P[m] = add(P[m + 1], temp);

                temp = mult_r(P[m + 1], r[ri]);
                K[m] = add(K[m], temp);
            }
        }
    }

    private void RPE_grid_positioning(int Mc, int xMp[], int ep[])
    {
        int i = 13;

        int epo = eOffset;
        int po = 0;
        // in original sources there is weird
        switch (Mc)
        {
        case 3:
            ep[epo++] = 0;
        case 2:
            ep[epo++] = 0;
        case 1:
            ep[epo++] = 0;
        case 0:
            ep[epo++] = xMp[po++];
            i--;
        }

        do
        {
            ep[epo++] = 0;
            ep[epo++] = 0;
            ep[epo++] = xMp[po++];
        } while (--i > 0);

        while (++Mc < 4)
        {
            ep[epo++] = 0;
        }
    }

    private void RPE_grid_selection(int[] x, int[] xM, int[] Mc_out)
    {
        int i;
        int L_result, L_temp;
        int EM;
        int Mc;

        int L_common_0_3;

        EM = 0;
        Mc = 0;
        // 164 "src/rpe.c"
        L_result = 0;
        for (int j = 1; j <= 12; j++)
        {
            L_temp = sasr(x[(3 * j)], 2);
            L_result += L_temp * L_temp;
        }

        L_common_0_3 = L_result;

        L_temp = sasr(x[(0)], 2);
        L_result += L_temp * L_temp;
        L_result <<= 1;
        EM = L_result;

        L_result = 0;
        for (int j = 0; j <= 12; j++)
        {
            L_temp = sasr((x[1 + 3 * j]), (2));
            L_result += L_temp * L_temp;
        }

        L_result <<= 1;
        if (L_result > EM)
        {
            Mc = 1;
            EM = L_result;
        }

        L_result = 0;
        for (int j = 0; j <= 12; j++)
        {
            L_temp = sasr((x[2 + 3 * j]), (2));
            L_result += L_temp * L_temp;
        }

        L_result <<= 1;
        if (L_result > EM)
        {
            Mc = 2;
            EM = L_result;
        }

        L_result = L_common_0_3;
        L_temp = sasr(x[3 + 3 * 12], 2);
        L_result += L_temp * L_temp;

        L_result <<= 1;
        if (L_result > EM)
        {
            Mc = 3;
            EM = L_result;
        }

        for (i = 0; i <= 12; i++)
            xM[i] = x[Mc + 3 * i];
        Mc_out[mcoffset] = Mc;

    }

    private int sasr(int x, int by)
    {
        return ((x) >= 0 ? (x) >> (by) : (~(-((x) + 1) >> (by))));
    }

    private void Short_term_analysis_filtering(int[] rp, int k_n, int[] s,
            int offset)
    {
        int i;
        int di, zzz, ui, sav, rpi;
        int si = offset;

        for (; k_n-- > 0; si++)
        {
            sav = s[si];
            di = sav;

            for (i = 0; i < 8; i++)
            { /* YYY */

                ui = u[i];
                rpi = rp[i];

                u[i] = sav;

                zzz = mult_r(rpi, di);
                sav = add(ui, zzz);

                zzz = mult_r(rpi, ui);
                di = add(di, zzz);
            }
            s[si] = di;
        }

    }

    private void Transformation_to_Log_Area_Ratios(int[] r)
    {
        int temp;
        int i;
        int ri = 0;

        /*
         * Computation of the LAR[0..7] from the r[0..7]
         */
        for (i = 1; i <= 8; i++, ri++)
        {
            temp = r[ri];
            temp = abs(temp);
            assert (temp >= 0);

            if (temp < 22118)
            {
                temp >>= 1;
            } else if (temp < 31130)
            {
                assert (temp >= 11059);
                temp -= 11059;
            } else
            {
                assert (temp >= 26112);
                temp -= 26112;
                temp <<= 2;
            }

            r[ri] = r[ri] < 0 ? -temp : temp;
            assert (r[ri] != MIN_WORD);
        }
    }

    private void Weighting_filter(int[] e, int[] x)
    {
        int L_result;
        int k;
        int ei = eOffset - 5;

        for (k = 0; k <= 39; k++)
        {
            L_result = 8192 >> 1;
            L_result += (e[(ei + k)] * -134) + (e[ei + k + 1] * -374)
                    + (e[ei + k + 3] * 2054) + (e[ei + k + 4] * 5741)
                    + (e[ei + k + 5] * 8192) + (e[ei + k + 6] * 5741)
                    + (e[ei + k + 7] * 2054) + (e[ei + k + 9] * -374)
                    + (e[ei + k + 10] * -134);

            L_result = sasr(L_result, 13);
            x[k] = (L_result < MIN_WORD ? MIN_WORD
                    : (L_result > MAX_WORD ? MAX_WORD : L_result));
        }

    }
}