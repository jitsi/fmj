package net.sf.fmj.test.compat.sun;

import javax.media.*;
import javax.media.format.*;
import javax.media.renderer.*;

import junit.framework.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;
import com.sun.media.renderer.video.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AWTRendererTest extends TestCase
{
    public void testAWTRenderer()
    {
        VideoRenderer r = new AWTRenderer();
        // System.out.println(r.getName());
        assertEquals(r.getName(), "AWT Renderer");
        // System.out.println(r.getBounds());
        assertTrue(r.getBounds() == null);
        // System.out.println(r.getComponent());
        assertTrue(r.getComponent() != null);

        // System.out.println(r.getControls());
        Object[] controls = r.getControls();
        assertEquals(controls.length, 1);
        assertEquals(controls[0], r);
        // for (int i = 0; i < controls.length; ++i)
        // { Object control = controls[i];
        // System.out.println("\t" + control.getClass());
        // System.out.println("\t" + control);
        // System.out.println("\t" + (control == r));
        //
        // }

        final Format[] supportedInputFormats;

        if (OSUtils.isMacOSX()
                && !ClasspathChecker.checkManagerImplementation()) // TODO: why
                                                                   // do we get
                                                                   // two of the
                                                                   // same on
                                                                   // the mac
                                                                   // using JMF?
        {
            supportedInputFormats = new Format[] {
                    // RGB, 32-bit, Masks=16711680:65280:255, LineStride=-1,
                    // class [I
                    new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                            0xff0000, 0xff00, 0xff, 1, -1, 0, -1),

                    // TODO: why do we get two of the same on the mac using JMF?
                    new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                            0xff0000, 0xff00, 0xff, 1, -1, 0, -1),

            };
        } else
        {
            supportedInputFormats = new Format[] {
                    // RGB, 32-bit, Masks=16711680:65280:255, LineStride=-1,
                    // class [I
                    new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                            0xff0000, 0xff00, 0xff, 1, -1, 0, -1),

                    // RGB, 32-bit, Masks=255:65280:16711680, LineStride=-1,
                    // class [I
                    new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                            0xff00, 0xff0000, 1, -1, 0, -1)

            };
        }

        final Format[] formats = r.getSupportedInputFormats();
        assertEquals(formats.length, supportedInputFormats.length);

        for (int i = 0; i < formats.length; ++i)
        {
            Format format = formats[i];
            // System.out.println("\t" + format);
            // if (!format.equals(supportedInputFormats[i]))
            // System.err.println("Not equal: " + format + " " +
            // supportedInputFormats[i]);
            assertEquals(format, supportedInputFormats[i]);

        }

    }
}
