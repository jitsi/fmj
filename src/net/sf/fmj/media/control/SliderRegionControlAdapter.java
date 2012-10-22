package net.sf.fmj.media.control;

import java.awt.*;

import javax.media.*;

public class SliderRegionControlAdapter extends AtomicControlAdapter implements
        SliderRegionControl
{
    long min, max;
    boolean enable;

    public SliderRegionControlAdapter()
    {
        super(null, true, null);
        enable = true;
    }

    public SliderRegionControlAdapter(Component c, boolean def, Control parent)
    {
        super(c, def, parent);
    }

    public long getMaxValue()
    {
        return max;
    }

    public long getMinValue()
    {
        return min;
    }

    public boolean isEnable()
    {
        return enable;
    }

    public void setEnable(boolean f)
    {
        enable = f;
    }

    public long setMaxValue(long value)
    {
        // this.max = value / 1000000L;
        this.max = value;
        informListeners();
        return max;
    }

    public long setMinValue(long value)
    {
        // this.min = value / 1000000L;
        this.min = value;
        informListeners();
        return min;
    }
}
