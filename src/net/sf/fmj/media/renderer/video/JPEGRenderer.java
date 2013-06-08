package net.sf.fmj.media.renderer.video;

import java.awt.*;
import java.io.*;
import java.util.logging.*;

import javax.imageio.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.renderer.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 *
 * Renderer which renders JPEG directly. There is a comparable class in JMF,
 * hence this implementation. However, it seems like this is not really needed
 * if there is a JPEGDecoder Codec registered. However, the original
 * cross-platform JMF did not include such a Codec. Because this class does not
 * use BufferToImage, it is not subject to any of its limitations and will
 * render images that BufferToImage does not support yet. This is not really
 * anything good, it is only worth pointing out because it can be confusing when
 * testing JPEG playback.
 *
 * @author Ken Larson
 *
 */
public class JPEGRenderer extends AbstractVideoRenderer implements
        VideoRenderer
{
    private static final Logger logger = LoggerSingleton.logger;

    private boolean scale;

    private final Format[] supportedInputFormats = new Format[] { new JPEGFormat() };

    private JVideoComponent component = new JVideoComponent();

    private Object[] controls = new Object[] { this };

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
        if (buffer.getData() == null)
        {
            logger.warning("buffer.getData() == null, eom=" + buffer.isEOM());
            return BUFFER_PROCESSED_FAILED; // TODO: check for EOM?
        }

        if (buffer.getLength() == 0)
        {
            logger.warning("buffer.getLength() == 0, eom=" + buffer.isEOM());
            return BUFFER_PROCESSED_FAILED; // TODO: check for EOM?
        }

        if (buffer.isDiscard())
        {
            logger.warning("JPEGRenderer passed buffer with discard flag");
            return BUFFER_PROCESSED_FAILED;
        }

        final java.awt.Image image;
        try
        {
            image = ImageIO.read(new ByteArrayInputStream((byte[]) buffer
                    .getData(), buffer.getOffset(), buffer.getLength()));
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
            logger.log(
                    Level.WARNING,
                    "data: "
                            + StringUtils.byteArrayToHexString(
                                    (byte[]) buffer.getData(),
                                    buffer.getLength(), buffer.getOffset()));
            return BUFFER_PROCESSED_FAILED;
        }

        if (image == null)
        {
            logger.log(Level.WARNING,
                    "Failed to read image (ImageIO.read returned null).");
            logger.log(
                    Level.WARNING,
                    "data: "
                            + StringUtils.byteArrayToHexString(
                                    (byte[]) buffer.getData(),
                                    buffer.getLength(), buffer.getOffset()));
            return BUFFER_PROCESSED_FAILED;
        }

        try
        {
            component.setImage(image);
        } catch (Exception e)
        {
            logger.log(Level.WARNING, "" + e, e);
            logger.log(
                    Level.WARNING,
                    "data: "
                            + StringUtils.byteArrayToHexString(
                                    (byte[]) buffer.getData(),
                                    buffer.getLength(), buffer.getOffset()));
            return BUFFER_PROCESSED_FAILED;
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
        return "JPEG Renderer";
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
        VideoFormat chosenFormat = (VideoFormat) super.setInputFormat(format);
        if (chosenFormat != null)
        {
            getComponent().setPreferredSize(chosenFormat.getSize());
        }
        return chosenFormat;
    }
}
