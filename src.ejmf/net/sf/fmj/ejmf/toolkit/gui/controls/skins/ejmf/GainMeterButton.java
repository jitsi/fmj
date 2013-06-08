package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

import javax.swing.*;

import net.sf.fmj.ejmf.toolkit.gui.controls.*;

/**
 * The gain meter button for StandardControlPanel.
 *
 * This class draws the default gain meter button for StandardControls: A small
 * speaker-shaped icon with "waves" emanating from the front.
 * <p>
 * A read X is drawn atop the speaker when the gain control is muted. As the
 * gain increase, more "waves" are drawn. As the gain decreases, fewer "waves"
 * are drawn.
 * <p>
 * GainMeterButton implements GainMeter in order to support these operations.
 *
 * @see net.sf.fmj.ejmf.toolkit.gui.controls.GainMeter
 */

public class GainMeterButton extends BasicControlButton implements GainMeter
{
    protected int intLevel;
    protected boolean muted;

    /**
     * Create a GainMeterButton
     */
    public GainMeterButton()
    {
        super();
    }

    /**
     * Create a GainMeterButton with initial values.
     */
    public GainMeterButton(float level, boolean muted)
    {
        setLevel(level);
        setMute(muted);
    }

    protected int getResolution()
    {
        return 5;
    }

    public int mapToMeterLevel(float gain)
    {
        int nticks = getResolution();
        int val = (int) (gain * 10);
        int divisor = 10 / nticks;
        return val / divisor;
    }

    // //////////////////////////////////////////////

    // //// Define abstract methods from AbstractGainMeterButton /////////
    @Override
    public void paint(Graphics g)
    {
        Color origColor;
        boolean isPressed, isEnabled;
        int w, h, size;

        w = getSize().width;
        h = getSize().height;
        origColor = g.getColor();
        isPressed = getModel().isPressed();
        isEnabled = isEnabled();

        g.setColor(getBackground());
        g.fillRect(1, 1, w - 2, h - 2);

        if (isPressed)
        {
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawRect(0, 0, w - 1, h - 1);
        } else
        {
            g.drawLine(0, 0, 0, h - 1);
            g.drawLine(1, 0, w - 2, 0);

            g.setColor(UIManager.getColor("controlHighlight")); // inner 3D
                                                                // border
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);

            g.setColor(UIManager.getColor("controlShadow")); // inner 3D border
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, 1, w - 2, h - 3);

            g.setColor(UIManager.getColor("controlDkShadow")); // black drop
                                                               // shadow
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.drawLine(w - 1, h - 1, w - 1, 0);
        }

        if (h < 5 || w < 5)
        {
            g.setColor(origColor);
            return;
        }

        if (isPressed)
        {
            g.translate(1, 1);
        }

        size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);
        paintIcon(g, (w - size) / 2, (h - size) / 2, size, isEnabled);

        // Reset the Graphics back to it's original settings
        if (isPressed)
        {
            g.translate(-1, -1);
        }
        g.setColor(origColor);

    }

    @Override
    protected void paintIcon(Graphics g, int x, int y, int size,
            boolean isEnabled)
    {
        int xtrans = muted ? x : x - 3;
        g.translate(xtrans, y);

        // Speaker looks like:
        /*
         * . .. ... .... ....... ....... ....... .... ... .. .
         */
        // ...more or less.

        int midLen = size / 2;
        int magLen = size / 5;
        // Draw magnet part of speaker
        for (int i = 0; i < size / 5; i++)
            g.drawLine(i, midLen - magLen, i, midLen + magLen);

        // Draw cone part of speaker
        for (int i = 0; i < size; i++)
            g.drawLine(i, midLen - i, i, midLen + i);

        // Draw 'sound waves' emanating from speaker
        // int narcs = intLevel / 2;
        int narcs = intLevel;
        if (!muted)
            for (int i = 0; i < narcs; i++)
                g.drawArc(size, midLen - (i + 1), i * 2, i * 2 + 1, 90, -180);

        // Draw red X
        if (muted)
        {
            g.translate(-2, -2);
            Color oldColor = g.getColor();
            g.setColor(Color.red);
            g.drawLine(0, 0, size + 2, size + 2);
            g.drawLine(0, size + 2, size + 2, 0);
            g.setColor(oldColor);
            g.translate(2, 2);
        }
        g.translate(-xtrans, -y);
    }

    /**
     * Set gain level of gain meter. The input argument is a gain level that
     * gets mapped to a number of "waves" emanating from front of speaker.
     *
     * @param level
     *            is a gain level value that gets mapped to a display value
     */
    public void setLevel(float level)
    {
        intLevel = mapToMeterLevel(level);
        updateView();
    }

    /**
     * Set mute state of button
     *
     * @param muted
     *            true to draw button to reflect muted state, false otherwise.
     */
    public void setMute(boolean muted)
    {
        this.muted = muted;
        updateView();
    }

    /**
     * Redraws GainMeter GUI component as necessary in response to change in
     * gain value.
     */
    public void updateView()
    {
        repaint();
    }
}
