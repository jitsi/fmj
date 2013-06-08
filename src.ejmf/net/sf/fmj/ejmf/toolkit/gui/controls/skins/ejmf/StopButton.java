package net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf;

import java.awt.*;

import javax.swing.*;

public class StopButton extends BasicControlButton
{
    /**
     * Create a StopButton
     */
    public StopButton()
    {
        super();
    }

    /**
     * Paint a small square into BasicControlButton
     *
     * @param g
     *            Graphics into which rectangles are drawn.
     * @param x
     *            Original translation (x) to point in button where where square
     *            is drawn.
     * @param y
     *            Original translation (y) to point in button where where square
     *            is drawn.
     * @param size
     *            Size of square.
     * @param isEnabled
     *            If true, square is drawn enabled (i.e. black), otherwise, they
     *            are offset by (1,1) and drawn with UIManager's controlShadow
     *            color.
     */
    @Override
    protected void paintIcon(Graphics g, int x, int y, int size,
            boolean isEnabled)
    {
        g.translate(x, y);
        if (isEnabled)
            g.fillRect(0, 0, size, size);
        else
        {
            g.translate(1, 1);
            Color oldColor = g.getColor();
            g.setColor(UIManager.getColor("controlShadow"));
            g.fill3DRect(0, 0, size, size, false);
            g.setColor(oldColor);
            g.translate(-1, -1);
        }
        g.translate(-x, -y);
    }
}
