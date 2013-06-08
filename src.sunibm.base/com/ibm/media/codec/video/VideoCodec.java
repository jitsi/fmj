package com.ibm.media.codec.video;

import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.utility.*;

import com.sun.media.*;

/**
 *
 * @author Ken Larson
 * @deprecated Don't use this unless you really have to.
 */
@Deprecated
public abstract class VideoCodec extends BasicCodec
{
    private static final Logger logger = LoggerSingleton.logger;

    // Looking at FOBS' subclassing of this class, they
    // set the outputFormat when the inputFormat is set.
    // this makse sense, because the output format is going to
    // depend on the input format.
    // strangely, they set supportedInputFormats when getMatchingOutputFormats
    // is called,
    // I don't know if this is a standard side-effect (somehow I doubt it).
    // My guess is:
    // when the input format is set, the subclass needs to set inputFormat,
    // outputFormat,
    // and one or both of defaultOutputFormats, supportedOutputFormats.

    protected String PLUGIN_NAME;
    protected VideoFormat[] defaultOutputFormats; // this is returned if
                                                  // getSupportedOutputFormats
                                                  // is called with a parameter
                                                  // of null.
    protected VideoFormat[] supportedInputFormats;
    protected VideoFormat[] supportedOutputFormats;
    protected VideoFormat inputFormat;
    protected VideoFormat outputFormat;
    protected final boolean DEBUG = true;

    private static final boolean TRACE = false;

    public VideoCodec()
    {
        super();
    }

    @Override
    public boolean checkFormat(Format format)
    {
        // seems to be called for every frame
        if (TRACE)
            logger.fine("videoCodec.checkFormat("
                    + LoggingStringUtils.formatToStr(format) + ")");

        // this requires that outputFormat and format not be null, otherwise
        // throws an NPE
        // calls videoResized if size is different.
        // doesn't seem to care about other options
        if (!((VideoFormat) format).getSize().equals(outputFormat.getSize()))
        {
            videoResized();
        }

        return true; // TODO -
    }

    @Override
    protected Format getInputFormat()
    {
        if (TRACE)
            logger.fine("videoCodec.getInputFormat()");
        return inputFormat;
    }

    protected Format[] getMatchingOutputFormats(Format in)
    {
        // sets supportedOutputFormats and returns it? Or probably subclass does
        // this.
        if (TRACE)
            logger.fine("videoCodec.getMatchingOutputFormats("
                    + LoggingStringUtils.formatToStr(in) + ")");
        return new Format[0]; // this seems to be always overridden by
                              // subclasses
    }

    public String getName()
    {
        if (TRACE)
            logger.fine("videoCodec.getName()");
        return PLUGIN_NAME;
    }

    @Override
    protected Format getOutputFormat()
    {
        if (TRACE)
            logger.fine("videoCodec.getOutputFormat()");
        return outputFormat;
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        if (TRACE)
            logger.fine("videoCodec.getSupportedInputFormats()");
        return supportedInputFormats;
    }

    public Format[] getSupportedOutputFormats(Format in) // calls matches, on
                                                         // whatever is in
                                                         // supportedInputFormats,
                                                         // also calls
                                                         // getMatchingOutputFormats
    {
        // this gets called with null, for example, by the JMRegistry
        if (in == null)
            return defaultOutputFormats;

        if (TRACE)
            logger.fine("videoCodec.getSupportedOutputFormats("
                    + LoggingStringUtils.formatToStr(in) + ")");
        // TODO: test this code.
        final List<Format> result = new ArrayList<Format>();

        for (int i = 0; i < supportedInputFormats.length; ++i)
        {
            if (in.matches(supportedInputFormats[i]))
            {
                inputFormat = (VideoFormat) in;

                Format[] matching = getMatchingOutputFormats(in);
                for (int j = 0; j < matching.length; ++j)
                    result.add(matching[j]); // TODO: since we are calling
                                             // getMatchingOutputFormats with
                                             // in, not
                                             // supportedInputFormats[i], we can
                                             // take this out of the loop.
            }
        }

        return result.toArray(new Format[result.size()]);
    }

    @Override
    public Format setInputFormat(Format format) // often, but not always
                                                // overridden
    {
        if (TRACE)
            logger.fine("this=" + this);
        if (TRACE)
            logger.fine("videoCodec.setInputFormat("
                    + LoggingStringUtils.formatToStr(format) + ")");
        if (!(format instanceof VideoFormat))
            return null;
        for (int i = 0; i < supportedInputFormats.length; ++i)
        {
            if (format.matches(supportedInputFormats[i]))
            {
                inputFormat = (VideoFormat) format;
                return inputFormat;
            }
        }
        return null;
    }

    @Override
    public Format setOutputFormat(Format format)
    {
        if (TRACE)
            logger.fine("videoCodec.setInputFormat("
                    + LoggingStringUtils.formatToStr(format) + ")");
        if (!(format instanceof VideoFormat))
            return null;

        final Format[] formats = getMatchingOutputFormats(inputFormat);

        for (int i = 0; i < formats.length; ++i)
        {
            if (format.matches(formats[i]))
            {
                outputFormat = (VideoFormat) format;
                return outputFormat;
            }
        }
        return null;
    }

    @Override
    protected void updateOutput(Buffer outputBuffer, Format format, int length,
            int offset)
    { // TODO - does this call checkFormat?
        if (TRACE)
            logger.fine("videoCodec.updateOutput(" + outputBuffer + ", "
                    + LoggingStringUtils.formatToStr(format) + ", " + length
                    + ", " + offset + ")");
        // seems to be called for every frame
        // TODO: does this set the values in the outputBuffer to match format,
        // length, and offset?
        outputBuffer.setFormat(format);
        outputBuffer.setLength(length);
        outputBuffer.setOffset(offset);
    }

    protected void videoResized()
    {
        if (TRACE)
            logger.fine("videoCodec.videoResized()");
        // this is just a callback, called if checkformat is called with a
        // format of a different size.
        // does it actually do anything, or only in subclass code?
    }

}
