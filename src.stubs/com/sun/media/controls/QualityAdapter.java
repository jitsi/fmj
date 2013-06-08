package com.sun.media.controls;

import java.awt.*;

import javax.media.control.*;

/**
 * TODO: Stub
 *
 * @author Ken Larson
 *
 */
public class QualityAdapter implements QualityControl
{
    protected float preferredValue;
    protected float minValue;
    protected float maxValue;
    protected float value;
    protected boolean settable;
    protected boolean isTSsupported;

    public QualityAdapter(float value, float value2, float value3,
            boolean settable)
    {
        super();
        preferredValue = value;
        minValue = value2;
        maxValue = value3;
        this.settable = settable;
    }

    public QualityAdapter(float value, float value2, float value3,
            boolean ssupported, boolean settable)
    {
        super();
        preferredValue = value;
        minValue = value2;
        maxValue = value3;
        isTSsupported = ssupported;
        this.settable = settable;
    }

    public Component getControlComponent()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public float getPreferredQuality()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public float getQuality()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public boolean isTemporalSpatialTradeoffSupported()
    {
        throw new UnsupportedOperationException(); // TODO
    }

    public float setQuality(float newQuality)
    {
        throw new UnsupportedOperationException(); // TODO
    }

}
