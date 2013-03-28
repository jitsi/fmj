package net.sf.fmj.media.rtp;

import java.util.*;

public final class TrueRandom
{
    private static Random random;

    static
    {
        random = new Random();
    }
    public static long rand()
    {
        return random.nextLong();
    }

    public TrueRandom()
    {
    }
}
