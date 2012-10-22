package net.sf.fmj.media.control;

import javax.media.*;

/**
 * Adapter class for ProgressControl.
 */
public class ProgressControlAdapter extends AtomicControlAdapter implements
        ProgressControl
{
    /*************************************************************************
     * VARIABLES
     *************************************************************************/

    Control[] controls = null;
    StringControl frc = null;
    StringControl brc = null;
    StringControl vpc = null;
    StringControl apc = null;
    StringControl ac = null;
    StringControl vc = null;

    /*************************************************************************
     * METHODS
     *************************************************************************/
    /**
     * Takes in the list of controls to use as progress controls.
     */
    public ProgressControlAdapter(StringControl frameRate,
            StringControl bitRate, StringControl videoProps,
            StringControl audioProps, StringControl videoCodec,
            StringControl audioCodec)
    {
        super(null, true, null);
        frc = frameRate;
        brc = bitRate;
        vpc = videoProps;
        apc = audioProps;
        vc = videoCodec;
        ac = audioCodec;
    }

    /*************************************************************************
     * ProgressControl implementation
     *************************************************************************/
    public StringControl getAudioCodec()
    {
        return ac;
    }

    /**
     * Returns the audio properties control.
     */
    public StringControl getAudioProperties()
    {
        return apc;
    }

    /**
     * Returns the bit rate control.
     */
    public StringControl getBitRate()
    {
        return brc;
    }

    /**
     * Returns an array that contains all the progress controls.
     */
    public Control[] getControls()
    {
        if (controls == null)
        {
            controls = new Control[6];
            controls[0] = frc;
            controls[1] = brc;
            controls[2] = vpc;
            controls[3] = apc;
            controls[4] = ac;
            controls[5] = vc;
        }
        return controls;
    }

    /**
     * Returns the frame rate control.
     */
    public StringControl getFrameRate()
    {
        return frc;
    }

    public StringControl getVideoCodec()
    {
        return vc;
    }

    /*************************************************************************
     * GroupControl implementation
     *************************************************************************/

    /**
     * Returns the video properties control.
     */
    public StringControl getVideoProperties()
    {
        return vpc;
    }
}
