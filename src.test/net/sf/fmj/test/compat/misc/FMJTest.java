package net.sf.fmj.test.compat.misc;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.bean.playerbean.*;
import javax.media.control.*;
import javax.media.datasink.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.*;

import junit.framework.*;
import net.sf.fmj.test.compat.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class FMJTest extends TestCase
{
    class MyController implements Controller
    {
        public void addControllerListener(ControllerListener arg0)
        {
        }

        public void close()
        {
        }

        public void deallocate()
        {
        }

        public Control getControl(String arg0)
        {
            return null;
        }

        public Control[] getControls()
        {
            return null;
        }

        public Time getDuration()
        {
            return null;
        }

        public long getMediaNanoseconds()
        {
            return 0;
        }

        public Time getMediaTime()
        {
            return null;
        }

        public float getRate()
        {
            return 0;
        }

        public Time getStartLatency()
        {
            return null;
        }

        public int getState()
        {
            return 0;
        }

        public Time getStopTime()
        {
            return null;
        }

        public Time getSyncTime()
        {
            return null;
        }

        public int getTargetState()
        {
            return 0;
        }

        public TimeBase getTimeBase()
        {
            return null;
        }

        public Time mapToTimeBase(Time arg0) throws ClockStoppedException
        {
            return null;
        }

        public void prefetch()
        {
        }

        public void realize()
        {
        }

        public void removeControllerListener(ControllerListener arg0)
        {
        }

        public void setMediaTime(Time arg0)
        {
        }

        public float setRate(float arg0)
        {
            return 0;
        }

        public void setStopTime(Time arg0)
        {
        }

        public void setTimeBase(TimeBase arg0)
                throws IncompatibleTimeBaseException
        {
        }

        public void stop()
        {
        }

        public void syncStart(Time arg0)
        {
        }
    }

    class MyDataSink implements DataSink
    {
        public void addDataSinkListener(DataSinkListener arg0)
        {
        }

        public void close()
        {
        }

        public String getContentType()
        {
            return null;
        }

        public Object getControl(String arg0)
        {
            return null;
        }

        public Object[] getControls()
        {
            return null;
        }

        public MediaLocator getOutputLocator()
        {
            return null;
        }

        public void open() throws IOException, SecurityException
        {
        }

        public void removeDataSinkListener(DataSinkListener arg0)
        {
        }

        public void setOutputLocator(MediaLocator arg0)
        {
        }

        public void setSource(DataSource arg0) throws IOException,
                IncompatibleSourceException
        {
        }

        public void start() throws IOException
        {
        }

        public void stop() throws IOException
        {
        }
    }

    class MySessionManager implements SessionManager
    {
        public void addFormat(Format arg0, int arg1)
        {
        }

        public void addPeer(SessionAddress arg0) throws IOException,
                InvalidSessionAddressException
        {
        }

        public void addReceiveStreamListener(ReceiveStreamListener arg0)
        {
        }

        public void addRemoteListener(RemoteListener arg0)
        {
        }

        public void addSendStreamListener(SendStreamListener arg0)
        {
        }

        public void addSessionListener(SessionListener arg0)
        {
        }

        public void closeSession(String arg0)
        {
        }

        public SendStream createSendStream(DataSource arg0, int arg1)
                throws UnsupportedFormatException, IOException
        {
            return null;
        }

        public SendStream createSendStream(int arg0, DataSource arg1, int arg2)
                throws UnsupportedFormatException, SSRCInUseException,
                IOException
        {
            return null;
        }

        public String generateCNAME()
        {
            return null;
        }

        public long generateSSRC()
        {
            return 0;
        }

        public Vector getActiveParticipants()
        {
            return null;
        }

        public Vector getAllParticipants()
        {
            return null;
        }

        public Object getControl(String arg0)
        {
            return null;
        }

        public Object[] getControls()
        {
            return null;
        }

        public long getDefaultSSRC()
        {
            return 0;
        }

        public GlobalReceptionStats getGlobalReceptionStats()
        {
            return null;
        }

        public GlobalTransmissionStats getGlobalTransmissionStats()
        {
            return null;
        }

        public LocalParticipant getLocalParticipant()
        {
            return null;
        }

        public SessionAddress getLocalSessionAddress()
        {
            return null;
        }

        public int getMulticastScope()
        {
            return 0;
        }

        public Vector getPassiveParticipants()
        {
            return null;
        }

        public Vector getPeers()
        {
            return null;
        }

        public Vector getReceiveStreams()
        {
            return null;
        }

        public Vector getRemoteParticipants()
        {
            return null;
        }

        public Vector getSendStreams()
        {
            return null;
        }

        public SessionAddress getSessionAddress()
        {
            return null;
        }

        public RTPStream getStream(long arg0)
        {
            return null;
        }

        public int initSession(SessionAddress arg0, long arg1,
                SourceDescription[] arg2, double arg3, double arg4)
                throws InvalidSessionAddressException
        {
            return 0;
        }

        public int initSession(SessionAddress arg0, SourceDescription[] arg1,
                double arg2, double arg3) throws InvalidSessionAddressException
        {
            return 0;
        }

        public void removeAllPeers()
        {
        }

        public void removePeer(SessionAddress arg0)
        {
        }

        public void removeReceiveStreamListener(ReceiveStreamListener arg0)
        {
        }

        public void removeRemoteListener(RemoteListener arg0)
        {
        }

        public void removeSendStreamListener(SendStreamListener arg0)
        {
        }

        public void removeSessionListener(SessionListener arg0)
        {
        }

        public void setMulticastScope(int arg0)
        {
        }

        public int startSession(int arg0, EncryptionInfo arg1)
                throws IOException
        {
            return 0;
        }

        public int startSession(SessionAddress arg0, int arg1,
                EncryptionInfo arg2) throws IOException,
                InvalidSessionAddressException
        {
            return 0;
        }

        public int startSession(SessionAddress arg0, SessionAddress arg1,
                SessionAddress arg2, EncryptionInfo arg3) throws IOException,
                InvalidSessionAddressException
        {
            return 0;
        }
    }

    private static boolean PRINT_ONLY = false;

    private static String generateCNAME()
    {
        // generates something like user@host
        final String hostname;
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }

        return System.getProperty("user.name") + '@' + hostname;

    }

    private void assertNotEquals(Object a, Object b)
    {
        if (a == null && b == null)
            assertFalse(true);
        else if (a == null || b == null)
            return;

        assertFalse(a.equals(b));
    }

    private void test(int a, int b)
    {
        if (PRINT_ONLY)
            System.out.println(a);
        else
            assertEquals(a, b);
    }

    private void test(long a, long b)
    {
        if (PRINT_ONLY)
            System.out.println(a);
        else
            assertEquals(a, b);

    }

    public void testAudioDeviceUnavailableEvent()
    {
        try
        {
            new AudioDeviceUnavailableEvent(null);
            assertTrue(false);
        } catch (IllegalArgumentException e)
        {
        }
    }

    public void testBuffer()
    {
        assertTrue(Buffer.SEQUENCE_UNKNOWN == Long.MAX_VALUE - 1);

        test(new Buffer().getFlags(), 0);
        test(new Buffer().getDuration(), -1L);
        assertEquals(new Buffer().getFormat(), null);
        test(new Buffer().getLength(), 0);
        test(new Buffer().getOffset(), 0);
        test(new Buffer().getSequenceNumber(), 9223372036854775806L);
        test(new Buffer().getSequenceNumber(), Buffer.SEQUENCE_UNKNOWN);
        test(new Buffer().getTimeStamp(), -1L);
        assertEquals(new Buffer().getData(), null);
        assertEquals(new Buffer().getHeader(), null);

        {
            final Buffer b1 = new Buffer();
            b1.setData(new byte[10]);
            b1.setFlags(1);
            Format f = new VideoFormat(VideoFormat.JPEG);
            b1.setFormat(f);
            assertTrue(f == b1.getFormat());
            b1.setHeader(new byte[4]);

            testBuffer(b1);

        }

        {
            final Buffer b1 = new Buffer();
            b1.setData(new int[10]);
            b1.setFlags(2);
            testBuffer(b1);

        }

        {
            final Buffer b1 = new Buffer();
            b1.setData(new short[10]);
            b1.setFlags(3);
            testBuffer(b1);
        }

        {
            final Buffer b1 = new Buffer();
            b1.setData(new long[10]); // tested float, double, String
            b1.setFlags(2);
            final Buffer b2 = (Buffer) b1.clone();
            assertTrue(b1.getData() == b2.getData()); // strange anomaly with
                                                      // types other than byte,
                                                      // int, short
            assertTrue(b2.getData() != null);
            assertTrue(b1.getFlags() == b2.getFlags());

            final Buffer b3 = new Buffer();
            b3.copy(b1);
            assertTrue(b1.getData() == b3.getData());
            assertTrue(b1.getFlags() == b3.getFlags());

            final Buffer b4 = new Buffer();
            b4.copy(b1, true);
            assertTrue(b1.getData() != b4.getData());
            assertTrue(b4.getData() != null);
            assertTrue(b1.getFlags() == b4.getFlags());

            final Buffer b5 = new Buffer();
            b5.copy(b1, false);
            assertTrue(b1.getData() == b5.getData());
            assertTrue(b1.getFlags() == b5.getFlags());
        }
    }

    public void testBuffer(Buffer b1)
    {
        final Buffer b2 = (Buffer) b1.clone();
        assertTrue(b1.getData() != b2.getData());
        assertTrue(b2.getData() != null);
        assertTrue(b1.getFlags() == b2.getFlags());
        assertTrue(b1.getFormat() == b2.getFormat());
        if (b1.getHeader() != null)
            assertTrue(b1.getHeader() != b2.getHeader());
        else
            assertTrue(null == b2.getHeader());

        // assertTrue(b2.getFormat() == null);

        final Buffer b3 = new Buffer();
        b3.copy(b1);
        assertTrue(b1.getData() == b3.getData());
        assertTrue(b1.getFlags() == b3.getFlags());
        assertTrue(b1.getFormat() == b3.getFormat());
        if (b1.getHeader() != null)
            assertTrue(b1.getHeader() == b3.getHeader());
        else
            assertTrue(null == b3.getHeader());

        final Buffer b4 = new Buffer();
        b4.copy(b1, true);
        assertTrue(b1.getData() != b4.getData());
        assertTrue(b4.getData() != null);
        assertTrue(b1.getFlags() == b4.getFlags());
        assertTrue(b1.getFormat() == b4.getFormat());
        if (b1.getHeader() != null)
            assertTrue(b1.getHeader() == b4.getHeader());
        else
            assertTrue(null == b4.getHeader());

        final Buffer b5 = new Buffer();
        b5.copy(b1, false);
        assertTrue(b1.getData() == b5.getData());
        assertTrue(b1.getFlags() == b5.getFlags());
        assertTrue(b1.getFormat() == b5.getFormat());
        if (b1.getHeader() != null)
            assertTrue(b1.getHeader() == b5.getHeader());
        else
            assertTrue(null == b5.getHeader());

    }

    public void testBufferControl()
    {
        assertEquals(BufferControl.DEFAULT_VALUE, -1);
        assertEquals(BufferControl.MAX_VALUE, -2);
    }

    public void testCaptureDeviceInfo()
    {
        assertFalse(new CaptureDeviceInfo().equals(new CaptureDeviceInfo()));
        assertFalse(new CaptureDeviceInfo("xyz", null, null)
                .equals(new CaptureDeviceInfo("xyz", null, null)));
        assertFalse(new CaptureDeviceInfo("xyz", new MediaLocator("abc"), null)
                .equals(new CaptureDeviceInfo("xyz", new MediaLocator("abc"),
                        null)));
        assertFalse(new CaptureDeviceInfo("xyz", new MediaLocator("abc"),
                new Format[] { new RGBFormat() }).equals(new CaptureDeviceInfo(
                "xyz", new MediaLocator("abc"),
                new Format[] { new RGBFormat() })));

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = new Format[] { new RGBFormat() };
            assertTrue(new CaptureDeviceInfo("xyz", m, f)
                    .equals(new CaptureDeviceInfo("xyz", m, f)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f1 = new Format[] { new RGBFormat() };
            Format[] f2 = new Format[] { new RGBFormat() };
            assertFalse(new CaptureDeviceInfo("xyz", m, f1)
                    .equals(new CaptureDeviceInfo("xyz", m, f2)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            RGBFormat r = new RGBFormat();
            Format[] f1 = new Format[] { r };
            Format[] f2 = new Format[] { r };
            assertFalse(new CaptureDeviceInfo("xyz", m, f1)
                    .equals(new CaptureDeviceInfo("xyz", m, f2)));
        }
        {
            MediaLocator m1 = new MediaLocator("abc");
            MediaLocator m2 = new MediaLocator("abc");
            Format[] f = new Format[] { new RGBFormat() };
            assertFalse(new CaptureDeviceInfo("xyz", m1, f)
                    .equals(new CaptureDeviceInfo("xyz", m2, f)));
        }

        {
            MediaLocator m = null;
            Format[] f = new Format[] { new RGBFormat() };
            assertFalse(new CaptureDeviceInfo("xyz", m, f)
                    .equals(new CaptureDeviceInfo("xyz", m, f)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = new Format[] {};
            assertTrue(new CaptureDeviceInfo("xyz", m, f)
                    .equals(new CaptureDeviceInfo("xyz", m, f)));
        }
        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = null;
            assertFalse(new CaptureDeviceInfo("xyz", m, f)
                    .equals(new CaptureDeviceInfo("xyz", m, f)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = new Format[] { new RGBFormat() };
            assertFalse(new CaptureDeviceInfo(null, m, f)
                    .equals(new CaptureDeviceInfo(null, m, f)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = new Format[] { new RGBFormat() };
            assertTrue(new CaptureDeviceInfo("", m, f)
                    .equals(new CaptureDeviceInfo("", m, f)));
        }

        {
            MediaLocator m = new MediaLocator("abc");
            Format[] f = new Format[] { new RGBFormat() };
            assertFalse(new CaptureDeviceInfo("", m, f).equals("test"));
        }

        assertTrue(new CaptureDeviceInfo().getFormats() == null);
        assertEquals(new CaptureDeviceInfo().toString(), "null : null\n");
        if (false)
        {
            System.out.println(new CaptureDeviceInfo().toString()); // null :
                                                                    // null
            System.out.println(new CaptureDeviceInfo("xyz", null, null)
                    .toString()); // xyz : null
            System.out.println(new CaptureDeviceInfo("xyz", new MediaLocator(
                    "abc"), null).toString()); // xyz : abc
            System.out.println(new CaptureDeviceInfo("xyz", new MediaLocator(
                    "abc"), new Format[] {}).toString()); // xyz : abc
            System.out.println(new CaptureDeviceInfo("xyz", new MediaLocator(
                    "abc"), new Format[] { new RGBFormat() }).toString());
            // xyz : abc
            // RGB, -1-bit, Masks=-1:-1:-1, PixelStride=-1, LineStride=-1
        }

    }

    public void testClock()
    {
        assertEquals(Clock.RESET.getNanoseconds(), 9223372036854775807L);
        assertEquals(Clock.RESET.getNanoseconds(), Long.MAX_VALUE);
    }

    public void testContentDescriptor()
    {
        assertTrue(new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN)
                .getContentType().equals("UnknownContent"));
        assertTrue(new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN)
                .getEncoding().equals("UnknownContent"));
        assertTrue(new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN)
                .toString().equals("UnknownContent"));

        assertTrue(new ContentDescriptor(ContentDescriptor.MIXED)
                .getContentType().equals("application.mixed-data"));
        assertTrue(new ContentDescriptor(ContentDescriptor.MIXED).toString()
                .equals("application.mixed-data"));
        assertTrue(ContentDescriptor.mimeTypeToPackageName("viddeo/mpexg")
                .equals("viddeo.mpexg"));
        assertTrue(ContentDescriptor.mimeTypeToPackageName("xyz").equals("xyz"));
        assertTrue(ContentDescriptor.mimeTypeToPackageName("a/b/c").equals(
                "a.b.c"));
        assertTrue(ContentDescriptor.mimeTypeToPackageName("a-b-c").equals(
                "a_b_c"));
        assertEquals(ContentDescriptor.mimeTypeToPackageName("!@#$%^&*()"),
                "__________");
        {
            byte[] b = new byte[256];
            for (int i = 0; i < 256; ++i)
            {
                b[i] = (byte) i;
            }
            String s = new String(b);
            final String sTarget = "______________________________________________..0123456789_______abcdefghijklmnopqrstuvwxyz______abcdefghijklmnopqrstuvwxyz_____________________________________________________________________________________________________________________________________";
            assertEquals(ContentDescriptor.mimeTypeToPackageName(s), sTarget);
        }

        assertEquals(new ContentDescriptor("abc").getDataType(),
                Format.byteArray);
        assertEquals(new ContentDescriptor("video.quicktime").getDataType(),
                Format.byteArray);

        assertEquals(new ContentDescriptor("text/plain").getContentType(),
                "text/plain");
        assertEquals(ContentDescriptor.MIXED, "application.mixed-data");

    }

    public void testController()
    {
        assertEquals(Controller.LATENCY_UNKNOWN.getNanoseconds(),
                Long.MAX_VALUE);
        assertEquals(Controller.Prefetched, 500);
        assertEquals(Controller.Prefetching, 400);
        assertEquals(Controller.Realized, 300);
        assertEquals(Controller.Realizing, 200);
        assertEquals(Controller.Started, 600);
        assertEquals(Controller.Unrealized, 100);
    }

    public void testControllerAdapter()
    {
        final StringBuffer b = new StringBuffer();

        ControllerAdapter a = new ControllerAdapter()
        {
            // @Override
            @Override
            public void stop(StopEvent arg0)
            {
                b.append("stop\n");
            }

            // @Override
            @Override
            public void stopByRequest(StopByRequestEvent arg0)
            {
                b.append("stopByRequest\n");
            }

        };

        a.controllerUpdate(new StopByRequestEvent(new MyController(), 0, 0, 0,
                null));
        assertEquals(b.toString(), "stop\nstopByRequest\n");

    }

    public void testDataSinkEvent()
    {
        // System.out.println(new DataSinkEvent(new MyDataSink()).toString());
    }

    public void testDuration()
    {
        assertTrue(Duration.DURATION_UNBOUNDED.getNanoseconds() == Long.MAX_VALUE);
        assertTrue(Duration.DURATION_UNKNOWN.getNanoseconds() == Long.MAX_VALUE - 1);
        assertTrue(Duration.DURATION_UNBOUNDED.getNanoseconds() == 9223372036854775807L);
        assertTrue(Duration.DURATION_UNKNOWN.getNanoseconds() == 9223372036854775806L);
        // assertEquals(new SystemTimeBase().getNanoseconds(), 0); // TODO: this
        // seems to be variable.
        // for (int i = 0; i < 100; ++i)
        // System.out.println(new SystemTimeBase().getNanoseconds());
        // for (int i = 0; i < 100; ++i)
        // System.out.println(System.nanoTime());

    }

    public void testEventStrings()
    {
        if (false)
        {
            assertEquals(
                    new AudioDeviceUnavailableEvent(new MediaPlayer())
                            .toString(),
                    "javax.media.AudioDeviceUnavailableEvent[source=javax.media.bean.playerbean.MediaPlayer[,0,0,0x0,invalid]]");
            assertEquals(
                    new CachingControlEvent(new MediaPlayer(), null, 0L)
                            .toString(),
                    "javax.media.CachingControlEvent[source=javax.media.bean.playerbean.MediaPlayer[,0,0,0x0,invalid],cachingControl=null,progress=0]");
            System.out.println(new ControllerClosedEvent(new MediaPlayer(),
                    "why?").toString()); // javax.media.ControllerClosedEvent[source=javax.media.bean.playerbean.MediaPlayer[,0,0,0x0,invalid]]
            System.out.println(new ControllerErrorEvent(new MediaPlayer(),
                    "why?").toString()); // javax.media.ControllerErrorEvent[source=javax.media.bean.playerbean.MediaPlayer[,0,0,0x0,invalid],message=why?]
        }
    }

    public void testFileTypeDescriptor()
    {
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME).toString(),
                "QuickTime");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO).toString(),
                "AVI");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.MPEG).toString(),
                "MPEG Video");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.VIVO).toString(),
                "Vivo");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO)
                        .toString(),
                "Basic Audio (au)");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.WAVE).toString(),
                "WAV");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.AIFF).toString(),
                "AIFF");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.MIDI).toString(),
                "MIDI");
        assertEquals(new FileTypeDescriptor(FileTypeDescriptor.RMF).toString(),
                "RMF");
        assertEquals(new FileTypeDescriptor(FileTypeDescriptor.GSM).toString(),
                "GSM");
        assertEquals(
                new FileTypeDescriptor(FileTypeDescriptor.MPEG_AUDIO)
                        .toString(),
                "MPEG Audio");

        assertEquals(
                new FileTypeDescriptor(ContentDescriptor.CONTENT_UNKNOWN)
                        .toString(),
                "UnknownContent");

        assertEquals(
                new FileTypeDescriptor(ContentDescriptor.MIXED).toString(),
                "application.mixed-data");

        assertEquals(new FileTypeDescriptor(ContentDescriptor.RAW).toString(),
                "raw");

        assertEquals(
                new FileTypeDescriptor(ContentDescriptor.RAW_RTP).toString(),
                "raw.rtp");
        assertEquals(new FileTypeDescriptor("xyz").toString(), "xyz");

    }

    public void testFramePositioningControl()
    {
        assertEquals(FramePositioningControl.FRAME_UNKNOWN, 2147483647);
        assertEquals(FramePositioningControl.FRAME_UNKNOWN, Integer.MAX_VALUE);
        assertEquals(FramePositioningControl.TIME_UNKNOWN.getNanoseconds(),
                Time.TIME_UNKNOWN.getNanoseconds());

    }

    public void testInterfaces() throws Exception
    {
        for (int i = 0; i < InterfaceClasses.ALL.length; ++i)
        {
            Class c = InterfaceClasses.ALL[i];
            if (!c.isInterface())
                System.err.println(c);
            assertTrue(c.isInterface());
        }

    }

    public void testManager()
    {
        assertEquals(Manager.UNKNOWN_CONTENT_NAME, "unknown");

    }

    public void testMediaLocator() throws MalformedURLException
    {
        try
        {
            new MediaLocator((String) null);
            assertTrue(false);
        } catch (NullPointerException e)
        {
        }

        assertEquals(
                new MediaLocator(new URL("http://www.yahoo.com"))
                        .toExternalForm(),
                "http://www.yahoo.com");
        assertEquals(new MediaLocator(new URL("http://www.yahoo.com")).getURL()
                .toExternalForm(), "http://www.yahoo.com");
        assertEquals(new MediaLocator("http://www.yahoo.com").getURL()
                .toExternalForm(), "http://www.yahoo.com");

        assertFalse(new MediaLocator("").equals(new MediaLocator(""))); // does
                                                                        // not
                                                                        // override
                                                                        // equals

        assertEquals(new MediaLocator("").getProtocol(), "");
        assertEquals(new MediaLocator("").getRemainder(), "");
        assertEquals(new MediaLocator(":").getProtocol(), "");
        assertEquals(new MediaLocator(":").getRemainder(), "");
        assertEquals(new MediaLocator("abcxyz").getProtocol(), "");
        assertEquals(new MediaLocator("abcxyz").getRemainder(), "");
        assertEquals(new MediaLocator("").toString(), "");
        assertEquals(new MediaLocator("abc").toString(), "abc");
        assertEquals(new MediaLocator("abc:xyz").toString(), "abc:xyz");
        assertEquals(new MediaLocator("abc:xyz").getProtocol(), "abc");
        assertEquals(new MediaLocator("abc:xyz").getRemainder(), "xyz");
        assertEquals(new MediaLocator("abc:xyz").toExternalForm(), "abc:xyz");
        assertEquals(new MediaLocator("abc").toExternalForm(), "abc");
        assertEquals(new MediaLocator("").toExternalForm(), "");
        assertEquals(new MediaLocator(":").toExternalForm(), ":");

    }

    public void testMpegAudioControl()
    {
        assertEquals(MpegAudioControl.LAYER_1, 1);
        assertEquals(MpegAudioControl.LAYER_2, 2);
        assertEquals(MpegAudioControl.LAYER_3, 4);
        assertEquals(MpegAudioControl.SAMPLING_RATE_16, 1);
        assertEquals(MpegAudioControl.SAMPLING_RATE_22_05, 2);
        assertEquals(MpegAudioControl.SAMPLING_RATE_24, 4);
        assertEquals(MpegAudioControl.SAMPLING_RATE_32, 8);
        assertEquals(MpegAudioControl.SAMPLING_RATE_44_1, 16);
        assertEquals(MpegAudioControl.SAMPLING_RATE_48, 32);
        assertEquals(MpegAudioControl.SINGLE_CHANNEL, 1);
        assertEquals(MpegAudioControl.TWO_CHANNELS_STEREO, 2);
        assertEquals(MpegAudioControl.TWO_CHANNELS_DUAL, 4);
        assertEquals(MpegAudioControl.THREE_CHANNELS_2_1, 4);
        assertEquals(MpegAudioControl.THREE_CHANNELS_3_0, 8);
        assertEquals(MpegAudioControl.FOUR_CHANNELS_2_0_2_0, 16);
        assertEquals(MpegAudioControl.FOUR_CHANNELS_2_2, 32);
        assertEquals(MpegAudioControl.FOUR_CHANNELS_3_1, 64);
        assertEquals(MpegAudioControl.FIVE_CHANNELS_3_0_2_0, 128);
        assertEquals(MpegAudioControl.FIVE_CHANNELS_3_2, 256);

    }

    public void testPlugIn()
    {
        assertEquals(PlugIn.BUFFER_PROCESSED_OK, 0);
        assertEquals(PlugIn.BUFFER_PROCESSED_FAILED, 1);
        assertEquals(PlugIn.INPUT_BUFFER_NOT_CONSUMED, 2);
        assertEquals(PlugIn.OUTPUT_BUFFER_NOT_FILLED, 4);
        assertEquals(PlugIn.PLUGIN_TERMINATED, 8);
    }

    public void testPortControl()
    {
        test(PortControl.MICROPHONE, 1);
        test(PortControl.LINE_IN, 2);
        test(PortControl.SPEAKER, 4);
        test(PortControl.HEADPHONE, 8);
        test(PortControl.LINE_OUT, 16);
        test(PortControl.COMPACT_DISC, 32);
        test(PortControl.SVIDEO, 64);
        test(PortControl.COMPOSITE_VIDEO, 128);
        test(PortControl.TV_TUNER, 256);
        test(PortControl.COMPOSITE_VIDEO_2, 512);

    }

    public void testPositionable()
    {
        assertEquals(Positionable.RoundUp, 1);
        assertEquals(Positionable.RoundDown, 2);
        assertEquals(Positionable.RoundNearest, 3);
    }

    public void testProcessor()
    {
        assertEquals(Processor.Configuring, 140);
        assertEquals(Processor.Configured, 180);
    }

    public void testRateRange()
    {
        assertTrue(new RateRange(0, -1, 1, true).isExact());
        assertTrue(new RateRange(0.f, -1.f, 1.f, true).getCurrentRate() == 0.f);
        assertTrue(new RateRange(0.f, -1.f, 1.f, true).getMaximumRate() == 1.f);
        assertTrue(new RateRange(0.f, -1.f, 1.f, true).getMinimumRate() == -1.f);

        assertTrue(new RateRange(0.f, -1.f, 1.f, true).setCurrentRate(1.f) == 1.f);
        assertTrue(new RateRange(0.f, -1.f, 1.f, true).setCurrentRate(2.f) == 2.f);

    }

    public void testRTPEvent()
    {
        if (false)
            System.out.println(new RTPEvent(new MySessionManager()).toString());

    }

    public void testSourceDescription()
    {
        assertEquals(SourceDescription.generateCNAME(), generateCNAME());
    }

    public void testSourceStream()
    {
        assertEquals(SourceStream.LENGTH_UNKNOWN, -1);
    }

    public void testTime()
    {
        assertTrue(Time.ONE_SECOND == 1000000000);
        assertTrue(Time.TIME_UNKNOWN.getNanoseconds() == 9223372036854775806L);
        assertTrue(Time.TIME_UNKNOWN.getNanoseconds() == Long.MAX_VALUE - 1);

        assertTrue(new Time(1.0).getNanoseconds() == Time.ONE_SECOND);
    }

    public void testTrack()
    {
        assertEquals(Track.FRAME_UNKNOWN, 2147483647);
        assertEquals(Track.FRAME_UNKNOWN, Integer.MAX_VALUE);
        assertEquals(Track.TIME_UNKNOWN.getNanoseconds(), 9223372036854775806L);
        assertEquals(Track.TIME_UNKNOWN.getNanoseconds(),
                Time.TIME_UNKNOWN.getNanoseconds());
    }

}
