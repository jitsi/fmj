package net.sf.fmj.test.compat.sun;

import java.awt.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;

import com.ibm.media.codec.video.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class VideoCodecTest extends TestCase
{
    class MyVideoCodec extends VideoCodec
    {
        public VideoFormat[] accessDefaultOutputFormats()
        {
            return defaultOutputFormats;
        }

        public VideoFormat accessInputFormat()
        {
            return inputFormat;
        }

        public Format[] accessMatchingOutputFormats(Format in)
        {
            return getMatchingOutputFormats(in);
        }

        public VideoFormat accessOutputFormat()
        {
            return outputFormat;
        }

        public String accessPluginName()
        {
            return PLUGIN_NAME;
        }

        public VideoFormat[] accessSupportedInputFormats()
        {
            return supportedInputFormats;
        }

        public VideoFormat[] accessSupportedOutputFormats()
        {
            return supportedOutputFormats;
        }

        public void doUpdateOutput(Buffer outputBuffer, Format format,
                int length, int offset)
        {
            updateOutput(outputBuffer, format, length, offset);
        }

        public void forceSetOutputFormat(VideoFormat value)
        {
            outputFormat = value;
        }

        // @Override
        public int process(Buffer arg0, Buffer arg1)
        {
            return 0;
        }

        public void setDefaultOutputFormats(VideoFormat[] value)
        {
            defaultOutputFormats = value;
        }

        public void setPlugInName(String s)
        {
            PLUGIN_NAME = s;
        }

        public void setSupportedInputFormats(VideoFormat[] value)
        {
            supportedInputFormats = value;
        }

        public void setSupportedOutputFormats(VideoFormat[] value)
        {
            supportedOutputFormats = value;
        }

        @Override
        protected void videoResized()
        {
            System.out.println("videoResized");
        }

    }

    class TracingVideoCodec extends MyVideoCodec
    {
        // @Override
        @Override
        public boolean checkFormat(Format arg0)
        {
            System.out.println("checkFormat");
            return super.checkFormat(arg0);
        }

        // @Override
        @Override
        protected Format getInputFormat()
        {
            System.out.println("getInputFormat");
            return super.getInputFormat();
        }

        // @Override
        @Override
        protected Format[] getMatchingOutputFormats(Format arg0)
        {
            System.out.println("getMatchingOutputFormats " + arg0);
            return super.getMatchingOutputFormats(arg0);
        }

        // @Override
        @Override
        public String getName()
        {
            System.out.println("getName");
            return super.getName();
        }

        // @Override
        @Override
        protected Format getOutputFormat()
        {
            System.out.println("getOutputFormat");
            return super.getOutputFormat();
        }

        // @Override
        @Override
        public Format[] getSupportedInputFormats()
        {
            System.out.println("getSupportedInputFormats");
            return super.getSupportedInputFormats();
        }

        // @Override
        @Override
        public Format[] getSupportedOutputFormats(Format arg0)
        {
            System.out.println("getSupportedOutputFormats");
            return super.getSupportedOutputFormats(arg0);
        }

        // @Override
        @Override
        public int process(Buffer arg0, Buffer arg1)
        {
            System.out.println("process");
            return 0;
        }

        // @Override
        @Override
        public Format setInputFormat(Format arg0)
        {
            System.out.println("setInputFormat");
            return super.setInputFormat(arg0);
        }

        // @Override
        @Override
        public Format setOutputFormat(Format arg0)
        {
            System.out.println("setOutputFormat");
            return super.setOutputFormat(arg0);
        }

        // @Override
        @Override
        protected void updateOutput(Buffer arg0, Format arg1, int arg2, int arg3)
        {
            System.out.println("updateOutput");
            super.updateOutput(arg0, arg1, arg2, arg3);
        }

        // @Override
        @Override
        protected void videoResized()
        {
            System.out.println("videoResized");
            super.videoResized();
        }

    }

    public void testVideoCodec()
    {
        // if (true)
        // return;

        MyVideoCodec c = new MyVideoCodec();
        assertTrue(c.accessPluginName() == null);
        assertTrue(c.accessDefaultOutputFormats() == null);
        assertTrue(c.accessSupportedInputFormats() == null);
        assertTrue(c.accessSupportedOutputFormats() == null);
        assertTrue(c.accessInputFormat() == null);
        assertTrue(c.accessOutputFormat() == null);

        assertTrue(c.getName() == null);
        c.setPlugInName("abc");
        assertEquals(c.getName(), "abc");

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            // assertEquals(c.checkFormat(new VideoFormat("xyz", new
            // Dimension(1, 2), 1000, byte[].class, 2.f)), true);
            VideoFormat[] fs = new VideoFormat[] { f };
            c.setSupportedInputFormats(fs);
            assertTrue(c.getSupportedInputFormats() == fs);
        }

        // only works once setSupportedInputFormats is called
        c.getSupportedOutputFormats(new VideoFormat("xyz", new Dimension(1, 2),
                1000, byte[].class, 2.f));

        {
            Format f = new Format("xyz");
            VideoFormat result = (VideoFormat) c.setInputFormat(f); // this
                                                                    // calls
                                                                    // matches
            assertTrue(null == result);

        }

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setInputFormat(f); // this
                                                                    // calls
                                                                    // matches
            assertTrue(f == result);

        }
        assertTrue(c.accessSupportedOutputFormats() == null);

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), -1,
                    null, 2.f);
            VideoFormat result = (VideoFormat) c.setInputFormat(f); // this
                                                                    // calls
                                                                    // matches
            assertTrue(f == result);

        }

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    int[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setInputFormat(f); // this
                                                                    // calls
                                                                    // matches
            assertTrue(null == result);

        }

        c.getSupportedOutputFormats(new VideoFormat("xyz", new Dimension(1, 2),
                1000, byte[].class, 2.f));

        // TODO: need to set default output formats?
        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setOutputFormat(f);
            assertTrue(null == result);

        }

        c.getSupportedOutputFormats(new VideoFormat("xyz", new Dimension(1, 2),
                1000, byte[].class, 2.f));

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            // assertEquals(c.checkFormat(new VideoFormat("xyz", new
            // Dimension(1, 2), 1000, byte[].class, 2.f)), true);
            VideoFormat[] fs = new VideoFormat[] { f };
            c.setSupportedOutputFormats(fs);
            assertFalse(c.getSupportedOutputFormats(f) == fs);

            assertEquals(c.accessMatchingOutputFormats(f).length, 0);

        }

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setOutputFormat(f);
            assertTrue(null == result);

        }

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat[] fs = new VideoFormat[] { f };
            c.setDefaultOutputFormats(fs);
            assertTrue(c.accessDefaultOutputFormats() == fs);

        }

        assertTrue(c.accessDefaultOutputFormats() != null);
        assertTrue(c.accessSupportedInputFormats() != null);
        assertTrue(c.accessSupportedOutputFormats() != null);
        assertTrue(c.accessInputFormat() != null);
        assertTrue(c.accessOutputFormat() == null);
        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setOutputFormat(f);
            assertTrue(null == result);
            assertTrue(c.accessOutputFormat() == null);

            c.forceSetOutputFormat(f);
            assertTrue(c.accessOutputFormat() == f);
            assertTrue(c.checkFormat(new VideoFormat("ayz",
                    new Dimension(1, 2), 2000, null, 3.f)));
            // assertTrue(c.checkFormat(new VideoFormat("ayz", null, 2000,
            // int[].class, 3.f))); // NPE
            // assertTrue(c.checkFormat(new Format("ayz"))); // class cast
            // except

            assertTrue(c.checkFormat(f));
            {
                VideoFormat f2 = new VideoFormat("xyz", new Dimension(1, 2),
                        1000, byte[].class, 2.f);
                assertTrue(c.checkFormat(f2));
            }
            {
                VideoFormat f2 = new VideoFormat("xyc", new Dimension(1, 2),
                        1000, byte[].class, 2.f);
                assertTrue(c.checkFormat(f2));
            }
            // {
            // VideoFormat f2 = new VideoFormat("xyc", new Dimension(1, 3),
            // 1000, byte[].class, 2.f);
            // assertTrue(c.checkFormat(f2)); // calls videoResized
            // }
            assertTrue(c.accessOutputFormat() == f);
        }

        if (false)
        {
            TracingVideoCodec c2 = new TracingVideoCodec();

            {
                VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2),
                        1000, byte[].class, 2.f);
                // assertEquals(c.checkFormat(new VideoFormat("xyz", new
                // Dimension(1, 2), 1000, byte[].class, 2.f)), true);
                VideoFormat[] fs = new VideoFormat[] { f };
                c2.setSupportedInputFormats(fs);
                assertTrue(c2.getSupportedInputFormats() == fs);
            }

            System.out.println("-->");
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            c2.getSupportedOutputFormats(f);

            System.out.println("-->");
            c2.setInputFormat(f);
            System.out.println("-->");
            System.out.println("f: " + f);
            assertTrue(c2.setOutputFormat(f) == null);
            System.out.println("-->");
            assertTrue(c2.getOutputFormat() == null);

        }

    }

    public void testVideoCodec2()
    {
        MyVideoCodec c = new MyVideoCodec()
        {
            // @Override
            @Override
            protected Format[] getMatchingOutputFormats(Format arg0)
            {
                supportedOutputFormats = defaultOutputFormats;
                return defaultOutputFormats;
                // return super.getMatchingOutputFormats(arg0);
            }

        };
        assertTrue(c.accessPluginName() == null);
        assertTrue(c.accessDefaultOutputFormats() == null);
        assertTrue(c.accessSupportedInputFormats() == null);
        assertTrue(c.accessSupportedOutputFormats() == null);
        assertTrue(c.accessInputFormat() == null);
        assertTrue(c.accessOutputFormat() == null);

        assertTrue(c.getName() == null);
        c.setPlugInName("abc");
        assertEquals(c.getName(), "abc");

        c.setDefaultOutputFormats(new VideoFormat[] { new RGBFormat(
                new java.awt.Dimension(320, 200), 64000, Format.intArray,
                1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1, 320, 0, -1) });
        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            // assertEquals(c.checkFormat(new VideoFormat("xyz", new
            // Dimension(1, 2), 1000, byte[].class, 2.f)), true);
            VideoFormat[] fs = new VideoFormat[] { f };
            c.setSupportedInputFormats(fs);
            assertTrue(c.getSupportedInputFormats() == fs);
        }

        // only works once setSupportedInputFormats is called
        Format[] r = c.getSupportedOutputFormats(new VideoFormat("xyz",
                new Dimension(1, 2), 1000, byte[].class, 2.f));
        assertFalse(r == null);
        assertEquals(r.length, 1);
        assertTrue(c.accessSupportedOutputFormats().length == 1);

        {
            VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                    byte[].class, 2.f);
            VideoFormat result = (VideoFormat) c.setInputFormat(f); // this
                                                                    // calls
                                                                    // matches
            assertTrue(f == result);

        }

        assertTrue(c.accessSupportedOutputFormats() != null);
    }

    public void testVideoCodec3()
    {
        MyVideoCodec c = new MyVideoCodec();
        Buffer b = new Buffer();
        VideoFormat f = new VideoFormat("xyz", new Dimension(1, 2), 1000,
                byte[].class, 2.f);
        c.doUpdateOutput(b, f, 3333, 44);
        assertTrue(b.getFormat() == f);
        assertTrue(b.getLength() == 3333);
        assertTrue(b.getOffset() == 44);
        assertFalse(b.isDiscard());
        assertFalse(b.isEOM());

        c.doUpdateOutput(b, null, 3335, 45);
        assertTrue(b.getFormat() == null);
        assertTrue(b.getLength() == 3335);
        assertTrue(b.getOffset() == 45);

    }
}
