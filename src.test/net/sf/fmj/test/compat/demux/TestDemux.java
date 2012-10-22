package net.sf.fmj.test.compat.demux;

import javax.media.protocol.*;

import net.sf.fmj.test.tracing.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TestDemux extends TracingDemultiplexer
{
    public static TestDemux instance;

    public TestDemux()
    {
        super();
        instance = this;
    }

    @Override
    public ContentDescriptor[] getSupportedInputContentDescriptors()
    {
        return new ContentDescriptor[] { new ContentDescriptor("audio.test") };
    }
}
