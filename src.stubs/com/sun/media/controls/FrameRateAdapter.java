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
public class FrameRateAdapter implements FrameRateControl
{
    protected float value;
    protected float min;
    protected float max;

    protected final TextComp textComp = new TextComp();
    protected boolean settable;
    protected Object owner;

    public FrameRateAdapter(float initialFrameRate, float minFrameRate,
            float maxFrameRate, boolean settable)
    {
        value = initialFrameRate;
        min = minFrameRate;
        max = maxFrameRate;
        this.settable = settable;
    }

    public FrameRateAdapter(Object owner, float initialFrameRate,
            float minFrameRate, float maxFrameRate, boolean settable)
    {
        this.owner = owner;
        value = initialFrameRate;
        min = minFrameRate;
        max = maxFrameRate;
        this.settable = settable;
    }

    public Component getControlComponent()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public float getFrameRate()
    {
        return value;
    }

    public float getMaxSupportedFrameRate()
    {
        return max;
    }

    public Object getOwner()
    {
        return owner;
    }

    public float getPreferredFrameRate()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public float setFrameRate(float newFrameRate)
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public void setOwner(Object owner)
    {
        this.owner = owner;
    }

}
