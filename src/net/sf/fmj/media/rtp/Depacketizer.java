package net.sf.fmj.media.rtp;

import javax.media.*;

public interface Depacketizer extends PlugIn
{
    public static final int DEPACKETIZER = 6;

    public abstract Format[] getSupportedInputFormats();

    public abstract Format parse(Buffer buffer);

    public abstract Format setInputFormat(Format format);
}
