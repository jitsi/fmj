package net.sf.fmj.gui.customslider;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import net.sf.fmj.gui.controlpanel.images.*;

/**
 * A slider look-and-feel that is nice(ish).
 *
 * @author Warren Bloomer
 *
 */
public class CustomSliderUI extends BasicSliderUI
{
    public static ComponentUI createUI(JComponent component)
    {
        return new CustomSliderUI((JSlider) component);
    }

    private final ImageIcon vertIcon;

    private final ImageIcon horizIcon;
    private Dimension vertIconSize = new Dimension(20, 20);

    private Dimension horizIconSize = new Dimension(20, 20);

    private CustomSliderUI(JSlider component)
    {
        super(component);

        horizIcon = Images.get(Images.SLIDER_THUMB_HORIZ);
        horizIconSize = new Dimension(horizIcon.getIconWidth(),
                horizIcon.getIconHeight());

        vertIcon = Images.get(Images.SLIDER_THUMB_VERT);
        vertIconSize = new Dimension(vertIcon.getIconWidth(),
                vertIcon.getIconHeight());
    }

    @Override
    protected Dimension getThumbSize()
    {
        int orientation = slider.getOrientation();
        if (orientation == SwingConstants.HORIZONTAL)
        {
            // horizThumbIcon = horizIcon;
            return horizIconSize;
        } else
        {
            // vertThumbIcon = vertIcon;
            return vertIconSize;
        }
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);
    }

    @Override
    public void paintFocus(Graphics g)
    {
        if (((CustomSlider) slider).getPaintFocus())
        {
            super.paintFocus(g);
        }
    }

    @Override
    public void paintThumb(Graphics g)
    {
        // super.paintThumb(g);

        Graphics2D g2d = (Graphics2D) g;

        int orientation = slider.getOrientation();

        Image image;

        switch (orientation)
        {
        case SwingConstants.VERTICAL:
            image = vertIcon.getImage();
            break;

        case SwingConstants.HORIZONTAL:
        default:
            image = horizIcon.getImage();
        }

        g2d.drawImage(image, thumbRect.x, thumbRect.y, thumbRect.width,
                thumbRect.height, null);
    }

    @Override
    public void paintTrack(Graphics g)
    {
        // super.paintTrack(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.LIGHT_GRAY);

        // logger.fine("trackLength: " + getTrackLength() + " + trackRect: " +
        // trackRect);
        // logger.fine("thumbOverhang: " + getThumbOverhang() +
        // ", trackBuffer: " + trackBuffer);

        float x = trackRect.x;
        float y = trackRect.y;
        float w = trackRect.width;
        float h = trackRect.height;

        int orientation = slider.getOrientation();
        if (orientation == SwingConstants.HORIZONTAL)
        {
            x -= trackBuffer;
            w += (2 * trackBuffer) - 1;
        } else
        {
            y -= trackBuffer;
            h += (2 * trackBuffer) - 1;
        }

        // if ()
        float arcw = 20;
        float arch = 20;
        RoundRectangle2D rectangle = new RoundRectangle2D.Float(x, y, w, h,
                arcw, arch);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fill(rectangle);

        g2d.setColor(getShadowColor());
        if (orientation == SwingConstants.HORIZONTAL)
        {
            rectangle.setFrame(x, y, w - 1, h - 1);
        } else
        {
            rectangle.setFrame(x, y, w - 1, h - 1);
        }
        g2d.draw(rectangle);
    }
}
