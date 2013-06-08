package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 *
 * @author Ken Larson
 *
 */
public class EjmfSkin implements Skin
{
    public Component createFastForwardButton()
    {
        return new FastForwardButton();
    }

    public Component createGainMeterButton()
    {
        return new GainMeterButton();
    }

    public Component createPauseButton()
    {
        return new PauseButton();
    }

    public Component createProgressSlider()
    {
        return new ProgressSlider();
    }

    public Component createReverseButton()
    {
        return new ReverseButton();
    }

    public Component createStartButton()
    {
        return new StartButton();
    }

    public Component createStopButton()
    {
        return new StopButton();
    }

    public AbstractButton createVolumeControlButton_Decrease()
    {
        return new VolumeControlButton(VolumeControlButton.DECREASE);
    }

    public AbstractButton createVolumeControlButton_Increase()
    {
        return new VolumeControlButton(VolumeControlButton.INCREASE);
    }

}
