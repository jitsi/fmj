package net.sf.fmj.media.rtp;

import java.util.*;

public final class TrueRandom
{
    private static final Random random = new Random();

    public static int nextInt()
    {
        return random.nextInt();
    }

    public static long nextLong()
    {
        return random.nextLong();
    }

    public TrueRandom()
    {
    }
}
