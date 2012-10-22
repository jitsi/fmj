package net.sf.fmj.test.compat;

import junit.framework.*;
import net.sf.fmj.test.compat.buffers.*;
import net.sf.fmj.test.compat.codec.*;
import net.sf.fmj.test.compat.datasources.*;
import net.sf.fmj.test.compat.demux.*;
import net.sf.fmj.test.compat.formats.*;
import net.sf.fmj.test.compat.generated.*;
import net.sf.fmj.test.compat.misc.*;
import net.sf.fmj.test.compat.playerbean.*;
import net.sf.fmj.test.compat.plugins.*;
import net.sf.fmj.test.compat.rtp.*;
import net.sf.fmj.test.compat.sun.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AllCompatabilityTests
{
    public static Test suite()
    {
        // set up properties so that
        // A) the registry will contain the same things that JMF does
        // B) we use a different file for the registry so it doesn't interfere
        // with normal use
        System.setProperty("net.sf.fmj.utility.JmfRegistry.disableLoad", "true");
        System.setProperty("net.sf.fmj.utility.JmfRegistry.JMFDefaults", "true");
        System.setProperty(
                "javax.media.pim.PlugInManagerInitializer.JMFDefaults", "true");
        System.setProperty("net.sf.fmj.utility.JmfRegistry.filename",
                ".fmj.registry.junit.xml");

        TestSuite suite = new TestSuite("Test for com.lti.fmj.test");

        // $JUnit-BEGIN$

        // buffers:
        suite.addTestSuite(BufferTest.class);
        suite.addTestSuite(BufferToImageTest_Int.class);
        suite.addTestSuite(BufferToImageTest.class);

        // datasources:
        suite.addTestSuite(DataSourceTest.class);
        suite.addTestSuite(URLDataSourceTest.class);

        // demux:
        suite.addTestSuite(DemuxTest.class);

        // formats:
        suite.addTestSuite(AudioFormatTest.class);
        suite.addTestSuite(FormatCrossFormatTest.class);
        suite.addTestSuite(FormatEncodingCodeTest.class);
        suite.addTestSuite(FormatEncodingCodeValuesTest.class);
        suite.addTestSuite(FormatMatchesIntersectsTest.class);
        suite.addTestSuite(FormatPrivateTest.class);
        suite.addTestSuite(FormatTest.class);
        suite.addTestSuite(H261FormatTest.class);
        suite.addTestSuite(H263FormatTest.class);
        suite.addTestSuite(IndexedColorFormatTest.class);
        suite.addTestSuite(JPEGFormatTest.class);
        suite.addTestSuite(RGBFormatTest.class);
        suite.addTestSuite(SerializableTest.class);
        suite.addTestSuite(VideoFormatTest.class);
        suite.addTestSuite(WavAudioFormatTest.class);
        suite.addTestSuite(YUVFormatTest.class);

        // generated:
        suite.addTestSuite(ConcreteClassesTest.class);
        suite.addTestSuite(ImplClassesTest.class);
        suite.addTestSuite(InterfaceClassesTest.class);

        // misc:
        suite.addTestSuite(CaptureDeviceManagerTest.class);
        suite.addTestSuite(FMJTest.class);
        suite.addTestSuite(InputSourceStreamTest.class);
        suite.addTestSuite(ManagerTest.class);
        suite.addTestSuite(PackageManagerTest.class);
        suite.addTestSuite(ProcessorModelTest.class);

        // plugins:
        suite.addTestSuite(PlugInManagerTest.class);

        // rtp:
        suite.addTestSuite(RTPHeaderTest.class);
        suite.addTestSuite(RTPManagerTest.class);
        suite.addTestSuite(RTPSessionMgrTest.class);
        suite.addTestSuite(SessionAddressTest.class);

        // playerbean:
        suite.addTestSuite(MediaPlayerBeanInfoTest.class);

        // sun:
        suite.addTestSuite(AviVideoFormatTest.class);
        suite.addTestSuite(AWTRendererTest.class);
        suite.addTestSuite(BasicCodecTest.class);
        suite.addTestSuite(BasicPlugInTest.class);
        suite.addTestSuite(VideoCodecTest.class);
        suite.addTestSuite(RawParserTest.class);
        suite.addTestSuite(RawPullStreamParserTest.class);
        suite.addTestSuite(RawPullBufferParserTest.class);
        suite.addTestSuite(MimeManagerTest.class);
        suite.addTestSuite(BitMapInfoTest.class);
        suite.addTestSuite(JavaSoundRendererTest.class);
        suite.addTestSuite(ULawDePacketizerTest.class);
        suite.addTestSuite(RawBufferMuxTest.class);
        suite.addTestSuite(RTPSyncBufferMuxTest.class);

        // codecs:
        suite.addTestSuite(AudioCodecTest.class);

        // $JUnit-END$
        return suite;
    }

}
