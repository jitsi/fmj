package net.sf.fmj.media.protocol.javasound;

import java.util.*;

import javax.media.format.*;

/**
 * Used to sort audio formats by quality
 * 
 * @author Ken Larson
 * 
 */
public class AudioFormatComparator implements Comparator
{
    public int compare(Object a, Object b)
    {
        // null-safety: not strictly necessary, but defensive:
        if (a == null && b == null)
            return 0;
        if (a == null) // then a < b, return -1
            return -1;
        if (b == null)
            return 1; // a > b

        final AudioFormat aCast = (AudioFormat) a;
        final AudioFormat bCast = (AudioFormat) b;

        if (aCast.getSampleRate() > bCast.getSampleRate())
            return 1;
        else if (aCast.getSampleRate() < bCast.getSampleRate())
            return -1;

        if (aCast.getChannels() > bCast.getChannels())
            return 1;
        else if (aCast.getChannels() < bCast.getChannels())
            return -1;

        if (aCast.getSampleSizeInBits() > bCast.getSampleSizeInBits())
            return 1;
        else if (aCast.getSampleSizeInBits() < bCast.getSampleSizeInBits())
            return -1;

        // endian and signed do not affect quality, don't bother to compare.

        return 0;

    }

}
