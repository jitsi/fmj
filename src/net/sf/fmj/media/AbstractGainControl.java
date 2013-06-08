package net.sf.fmj.media;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.media.*;

/**
 * Base class to help implement {@link GainControl}.
 *
 * @author Ken Larson
 *
 */
public abstract class AbstractGainControl implements GainControl
{
    protected static float dBToLevel(float db)
    {
        return (float) Math.pow(10.0, db / 20.0); // from GainControl javadoc
    }

    protected static float levelToDb(float level)
    {
        return (float) (Math.log10(level) * 20.0); // opposite of dBToLevel.
    }

    private final List<GainChangeListener> listeners = new ArrayList<GainChangeListener>();

    // implementation of mute via set/get level:
    private boolean mute;

    private float savedLevelDuringMute;

    public AbstractGainControl()
    {
        super();
    }

    public void addGainChangeListener(GainChangeListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    // TODO: need a visual component.
    // private StandardGainControl standardGainControl;
    public Component getControlComponent()
    {
        return null;
        // if (standardGainControl == null)
        // {
        // standardGainControl = new StandardGainControl();
        // // TODO
        // }
        //
        // return standardGainControl.getControlComponent();
    }

    public float getDB()
    {
        return levelToDb(getLevel());
    }

    public boolean getMute()
    {
        return mute;
    }

    // subclasses should return this from getLevel() if getMute() is true, and
    // they are using the implementation of mute in this class.
    protected float getSavedLevelDuringMute()
    {
        return savedLevelDuringMute;
    }

    protected void notifyListenersGainChangeEvent()
    {
        final GainChangeEvent event = new GainChangeEvent(this, getMute(),
                getDB(), getLevel());
        notifyListenersGainChangeEvent(event);
    }

    protected void notifyListenersGainChangeEvent(GainChangeEvent event)
    {
        final List<GainChangeListener> listenersCopy = new ArrayList<GainChangeListener>();

        synchronized (listeners)
        {
            listenersCopy.addAll(listeners);
        }

        for (int i = 0; i < listenersCopy.size(); ++i)
        {
            final GainChangeListener listener = listenersCopy.get(i);
            listener.gainChange(event);
        }
    }

    public void removeGainChangeListener(GainChangeListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }

    }

    // default implementation via get/set level:
    public float setDB(float gain)
    {
        setLevel(dBToLevel(gain));

        final float result = getDB();

        notifyListenersGainChangeEvent(); // TODO: don't notify if no change

        return result;
    }

    public void setMute(boolean mute)
    {
        if (mute == this.mute)
            return;
        if (mute)
        {
            savedLevelDuringMute = getLevel();
            setLevel(0.f);
            this.mute = true;
        } else
        {
            setLevel(savedLevelDuringMute);
            this.mute = false;
        }

        notifyListenersGainChangeEvent();

    }

}
