package net.sf.fmj.media.renderer.video;

import java.awt.*;
import java.awt.image.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;
import javax.media.renderer.*;
import javax.swing.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;
import net.sf.fmj.utility.*;

/**
 *
 * The simplest possible Swing Renderer. Same as SimpleAWTRenderer, but uses a
 * JComponent
 *
 * @author Ken Larson
 *
 */
public class SimpleSwingRenderer extends AbstractVideoRenderer implements
        VideoRenderer
{
    /**
     * Component used for rendering video images.
     */
    private class SwingVideoComponent extends JComponent
    {
        private Image image;

        private boolean scale = true;

        private BufferedImage biCompatible;

        public SwingVideoComponent()
        {
            setDoubleBuffered(false);
        }

        private BufferedImage getCompatibleBufferedImage()
        {
            if (biCompatible == null
                    || biCompatible.getWidth() != image.getWidth(null)
                    || biCompatible.getHeight() != image.getHeight(null))
                biCompatible = this.getGraphicsConfiguration()
                        .createCompatibleImage(image.getWidth(null),
                                image.getHeight(null));
            return biCompatible;
        }

        @Override
        public Dimension getPreferredSize()
        {
            if (inputFormat == null)
            {
                return super.getPreferredSize();
            }
            VideoFormat videoFormat = (VideoFormat) inputFormat;
            return videoFormat.getSize();
        }

        private Rectangle getVideoRect(final boolean scale)
        {
            final int x, y;
            final int w, h;
            final Dimension preferredSize = getPreferredSize();
            final Dimension size = getSize();

            if (!scale)
            {
                if (preferredSize.width <= size.width)
                {
                    x = (size.width - preferredSize.width) / 2;
                    w = preferredSize.width;
                } else
                {
                    x = 0;
                    w = preferredSize.width;
                }

                if (preferredSize.height <= size.height)
                {
                    y = (size.height - preferredSize.height) / 2;
                    h = preferredSize.height;
                } else
                {
                    y = 0;
                    h = preferredSize.height;
                }
            } else
            {
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
            }
            return new Rectangle(x, y, w, h);
        }

        @Override
        public void paint(Graphics g)
        {
            if (image != null)
            {
                Rectangle rect = getVideoRect(scale);

                // long start = System.currentTimeMillis();

                try
                {
                    if (biCompatible == null) // try drawing directly, unless we
                                              // already know that won't work
                                              // (biCompatible is set)
                    {
                        g.drawImage(image, rect.x, rect.y, rect.width,
                                rect.height, null);
                        return;
                    }
                } catch (java.awt.image.ImagingOpException e)
                {
                    // some images do not seem to be able to be scaled directly.
                    // this appears to only happen when the image has a
                    // ComponentColorModel.
                    // the graphics destination appears to use a
                    // DirectColorModel, and for scaling,
                    // they are incompatible. Civil uses ComponentColorModel in
                    // many cases, so
                    // this is generally when we have this problem.

                    // fall through and convert manually.

                    // no idea why AWT/Java2d doesn't just do this for us behind
                    // the scenes.
                }

                getCompatibleBufferedImage();
                biCompatible.getGraphics().drawImage(image, 0, 0,
                        image.getWidth(null), image.getHeight(null), null);

                g.drawImage(biCompatible, rect.x, rect.y, rect.width,
                        rect.height, null);

            }
            // else {
            // Dimension size = getSize();
            // g.setColor(getBackground());
            // g.fillRect(0, 0, size.width, size.height);
            // }
        }

        public void setImage(Image image)
        {
            this.image = image;
            if (PAINT_IMMEDIATELY)
                paintImmediately(getVideoRect(scale)); // TODO: this has to be
                                                       // done in swing thread.
            else
                repaint();

        }

        @Override
        public void update(Graphics g)
        {
            paint(g);
        }
    }

    private static final Logger logger = LoggerSingleton.logger;
    private static final boolean PAINT_IMMEDIATELY = false;

    private static final boolean TRACE_FPS = false;

    private final Format[] supportedInputFormats = new Format[] {
            // RGB, 32-bit, Masks=16711680:65280:255, LineStride=-1, class [I
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),

            // RGB, 32-bit, Masks=255:65280:16711680, LineStride=-1, class [I
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),

            new RGBFormat(null, -1, Format.byteArray, -1.0f, 32, 1, 2, 3),

            new RGBFormat(null, -1, Format.byteArray, -1.0f, 32, 3, 2, 1),

            new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 1, 2, 3),

            new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 3, 2, 1),

            // rgb565, rgb555, bgr565, bgr555
            new RGBFormat(null, -1, Format.shortArray, -1.0f, 16, -1, -1, -1,
                    1, -1, 0, -1),

            // rgb8,bgr8
            new RGBFormat(null, -1, Format.byteArray, -1.0f, 8, -1, -1, -1, 1,
                    -1, 0, -1), };

    private SwingVideoComponent component = new SwingVideoComponent();

    private Object[] controls = new Object[] { this };
    private BufferToImage bufferToImage;

    private final FPSCounter fpsCounter = new FPSCounter();

    @Override
    public int doProcess(Buffer buffer)
    {
        if (buffer.isEOM())
        {
            logger.warning(this.getClass().getSimpleName()
                    + "passed buffer with EOM flag"); // normally not supposed
                                                      // to happen, is it?
            return BUFFER_PROCESSED_OK;
        }

        // if (buffer.isDiscard())
        // return BUFFER_PROCESSED_OK; // TODO: where do we check for this?

        if (buffer.getData() == null)
        {
            return BUFFER_PROCESSED_FAILED; // TODO: check for EOM?
        }

        java.awt.Image image = bufferToImage.createImage(buffer);
        component.setImage(image);

        if (TRACE_FPS)
        {
            fpsCounter.nextFrame();
            if (fpsCounter.getNumFrames() >= 50)
            {
                System.out.println(fpsCounter);
                fpsCounter.reset();
            }
        }

        return BUFFER_PROCESSED_OK;
    }

    @Override
    public Component getComponent()
    {
        return component;
    }

    @Override
    public Object[] getControls()
    {
        return controls;
    }

    // @Override
    @Override
    public String getName()
    {
        return "Simple Swing Renderer";
    }

    // @Override
    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }

    // @Override
    @Override
    public Format setInputFormat(Format format)
    {
        // logger.fine("FORMAT: " + MediaCGUtils.formatToStr(format));
        // TODO: check VideoFormat and compatibility
        bufferToImage = new BufferToImage((VideoFormat) format);
        return super.setInputFormat(format);
    }

}
