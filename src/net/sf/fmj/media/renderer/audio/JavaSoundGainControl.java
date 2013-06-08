package net.sf.fmj.media.renderer.audio;

import javax.sound.sampled.*;

import net.sf.fmj.media.*;

/**
 * GainControl for JavaSoundRenderer. See Sun's javadoc for GainControl. TODO:
 * need to make sure we meet the requirements in the javadoc under the section
 * "Decibel and Level Interactions".
 *
 * @author Ken Larson
 *
 */
class JavaSoundGainControl extends AbstractGainControl
{
    private final FloatControl masterGainControl;
    private final BooleanControl muteControl;
    private final float max; // max gain
    private final float min; // min gain
    private final float range; // max - min;
    private final boolean gainUnitsDb; // whether the underlying
                                       // masterGainControl uses db for units.

    public JavaSoundGainControl(final FloatControl masterGainControl,
            final BooleanControl muteControl)
    {
        super();
        this.masterGainControl = masterGainControl;
        this.muteControl = muteControl;

        if (masterGainControl != null)
        {
            min = masterGainControl.getMinimum();
            max = masterGainControl.getMaximum();
            gainUnitsDb = masterGainControl.getUnits().equals("dB"); // a bit of
                                                                     // a hack
                                                                     // since we
                                                                     // are
                                                                     // effectively
                                                                     // string-scraping
                                                                     // here.
        } else
        {
            min = max = 0.f;
            gainUnitsDb = false;
        }
        range = max - min;
    }

    @Override
    public float getDB()
    {
        if (masterGainControl == null)
            return 0.f;

        if (gainUnitsDb)
            return masterGainControl.getValue();
        else
            return levelToDb(getLevel());

    }

    /** Level is between 0 and 1 */
    public float getLevel()
    {
        if (masterGainControl == null)
            return 0.f;

        if (gainUnitsDb)
        {
            return dBToLevel(masterGainControl.getValue());
        } else
        {
            float value = masterGainControl.getValue();
            return (value - min) / range;
        }
    }

    @Override
    public boolean getMute()
    {
        if (muteControl == null)
            return false;
        return muteControl.getValue();
    }

    @Override
    public float setDB(float gain)
    {
        if (masterGainControl == null)
            return 0.f;

        if (gainUnitsDb)
            masterGainControl.setValue(gain);
        else
            setLevel(dBToLevel(gain));

        final float result = getDB();

        notifyListenersGainChangeEvent(); // TODO: don't notify if no change

        return result;
    }

    public float setLevel(float level)
    {
        if (masterGainControl == null)
            return 0.f;

        if (gainUnitsDb)
        {
            masterGainControl.setValue(levelToDb(level));
        } else
        {
            level = min + level * range;
            masterGainControl.setValue(level);
        }

        float result = getLevel();

        notifyListenersGainChangeEvent(); // TODO: don't notify if no change

        return result;
    }

    @Override
    public void setMute(boolean mute)
    {
        if (muteControl == null)
            return;

        muteControl.setValue(mute);

        notifyListenersGainChangeEvent(); // TODO: don't notify if no change

    }

}
