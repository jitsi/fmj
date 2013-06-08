package net.sf.fmj.media.protocol.javasound;

import java.util.*;

import javax.media.format.*;

/**
 * Used to sort audio formats by quality
 *
 * @author Ken Larson
 *
 */
public class AudioFormatComparator implements Comparator<AudioFormat>
{
    public int compare(AudioFormat a, AudioFormat b)
    {
        // null-safety: not strictly necessary, but defensive:
        if (a == null && b == null)
            return 0;
        if (a == null) // then a < b, return -1
            return -1;
        if (b == null)
            return 1; // a > b

        if (a.getSampleRate() > b.getSampleRate())
            return 1;
        else if (a.getSampleRate() < b.getSampleRate())
            return -1;

        if (a.getChannels() > b.getChannels())
            return 1;
        else if (a.getChannels() < b.getChannels())
            return -1;

        if (a.getSampleSizeInBits() > b.getSampleSizeInBits())
            return 1;
        else if (a.getSampleSizeInBits() < b.getSampleSizeInBits())
            return -1;

        // endian and signed do not affect quality, don't bother to compare.

        return 0;

    }

}
