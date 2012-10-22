package net.sf.fmj.media.rtp;

public class RTCPSDES
{
    public static String toString(RTCPSDES chunks[])
    {
        String s = "";
        for (int i = 0; i < chunks.length; i++)
            s = s + chunks[i];

        return s;
    }

    public int ssrc;

    public RTCPSDESItem items[];

    public RTCPSDES()
    {
    }

    @Override
    public String toString()
    {
        return "\t\tSource Description for sync source " + ssrc + ":\n"
                + RTCPSDESItem.toString(items);
    }
}
