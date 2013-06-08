package net.sf.fmj.media.renderer.video;

import java.awt.*;

import javax.swing.*;

/**
 * JComponent used for rendering video images.
 *
 * TODO keep the aspect ratio provided by video format TODO provide controls for
 * scaling interpolation quality
 */
final public class JVideoComponent extends JComponent
{
    private Image image;
    // private VideoFormat inputFormat;

    // private Object interpolationHint =
    // RenderingHints.VALUE_INTERPOLATION_BICUBIC;

    // jmf scales without keeping the aspect ratio, TODO: make
    // scaleKeepAspectRatio accessible by application
    private boolean scaleKeepAspectRatio = false;

    public JVideoComponent()
    {
        setDoubleBuffered(false);
        setOpaque(false);
    }

    /*
     * public void setInputformat(VideoFormat format) { this.inputFormat =
     * format; }
     */

    public Image getImage()
    {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        // super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (scaleKeepAspectRatio)
        {
            if (image != null)
            {
                int x, y;
                int w, h;
                Dimension preferredSize = getPreferredSize();
                Dimension size = getSize();
                if ((float) size.width / preferredSize.width < (float) size.height
                        / preferredSize.height)
                {
                    w = size.width;
                    h = size.width * preferredSize.height / preferredSize.width;
                    x = 0;
                    y = (size.height - h) / 2;
                } else
                {
                    w = size.height * preferredSize.width
                            / preferredSize.height;
                    h = size.height;
                    x = (size.width - w) / 2;
                    y = 0;
                }
                // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                // RenderingHints.VALUE_ANTIALIAS_ON);
                // g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                // RenderingHints.VALUE_RENDER_QUALITY);
                // g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                // interpolationHint);
                g.drawImage(image, x, y, w, h, null);
            }
        } else
        {
            if (image != null)
            {
                Dimension size = getSize();
                g.drawImage(image, 0, 0, size.width, size.height, null);
            }
        }
    }

    /*
     * public Dimension getPreferredSize() { if (inputFormat == null) { return
     * super.getPreferredSize(); } VideoFormat videoFormat = (VideoFormat)
     * inputFormat; return videoFormat.getSize(); }
     */

    public void setImage(Image image)
    {
        this.image = image;
        repaint();
    }
}
