package net.sf.fmj.utility;

/**
 * Utility for measuring FPS, used for benchmarking and optimization.
 *
 * @author Ken Larson
 *
 */
public class FPSCounter
{
    private int frames;
    private long start;

    public double getFPS()
    {
        long now = System.currentTimeMillis();
        return 1000.0 * frames / (now - start);
    }

    public int getNumFrames()
    {
        return frames;
    }

    public void nextFrame()
    {
        if (start == 0)
            start = System.currentTimeMillis();

        ++frames;
    }

    public void reset()
    {
        start = 0;
        frames = 0;
    }

    @Override
    public String toString()
    {
        return "FPS: " + getFPS();
    }
}
