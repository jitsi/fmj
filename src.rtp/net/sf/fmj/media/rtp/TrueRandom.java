package net.sf.fmj.media.rtp;

public final class TrueRandom
{
    public static long rand()
    {
        return System.currentTimeMillis();
    }

    public TrueRandom()
    {
    }
}
