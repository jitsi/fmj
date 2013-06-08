package net.sf.fmj.gui.controlpanel;

import javax.swing.*;

import net.sf.fmj.gui.controlpanel.images.*;

/**
 * Default skin.
 *
 * @author Ken Larson
 *
 */
public class DefaultSkin implements Skin
{
    public ImageIcon getFastForwardIcon()
    {
        return Images.get(Images.MEDIA_FASTFORWARD);
    }

    public ImageIcon getMuteOffIcon()
    {
        return Images.get(Images.MUTE_OFF);
    }

    public ImageIcon getMuteOnIcon()
    {
        return Images.get(Images.MUTE_ON);
    }

    public ImageIcon getPauseIcon()
    {
        return Images.get(Images.MEDIA_PAUSE);
    }

    public ImageIcon getPlayIcon()
    {
        return Images.get(Images.MEDIA_PLAY);
    }

    public ImageIcon getRewindIcon()
    {
        return Images.get(Images.MEDIA_REWIND);
    }

    public ImageIcon getStepBackwardIcon()
    {
        return Images.get(Images.MEDIA_STEPBACK);
    }

    public ImageIcon getStepForwardIcon()
    {
        return Images.get(Images.MEDIA_STEPFORWARD);
    }

    public ImageIcon getStopIcon()
    {
        return Images.get(Images.MEDIA_STOP);
    }

}
