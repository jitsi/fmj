package com.sun.media.controls;

import java.awt.*;

import javax.media.control.*;

import com.sun.media.ui.*;

/**
 * TODO: Stub
 *
 * @author Ken Larson
 *
 */
public class KeyFrameAdapter implements KeyFrameControl
{
    private int preferred;
    private int value;
    private boolean settable;
    private final TextComp textComp = new TextComp();

    public KeyFrameAdapter(int preferred, boolean settable)
    {
        super();
        this.preferred = preferred;
        this.settable = settable;
    }

    public Component getControlComponent()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public int getKeyFrameInterval()
    {
        return value;
    }

    public int getPreferredKeyFrameInterval()
    {
        return preferred;
    }

    public int setKeyFrameInterval(int frames)
    {
        throw new UnsupportedOperationException(); // TODO
    }

}
