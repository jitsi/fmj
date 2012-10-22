package net.sf.fmj.test.compat.misc;

import javax.media.*;
import javax.media.protocol.*;

import junit.framework.*;

/**
 * Unit test for ProcessorModel.
 * 
 * @author Ken Larson
 * 
 */
public class ProcessorModelTest extends TestCase
{
    public void testProcessorModel()
    {
        {
            final ProcessorModel p = new ProcessorModel();
            for (int i = 0; i < 4; ++i)
                assertEquals(p.getTrackCount(i), -1);

            assertEquals(p.getOutputTrackFormat(0), null);
            assertEquals(p.getInputLocator(), null);
            assertTrue(p.isFormatAcceptable(0, new Format("xyz")));

        }

        {
            final ProcessorModel p = new ProcessorModel(new Format[] {
                    new Format("abc"), new Format("xyz") },
                    new ContentDescriptor("abc"));
            for (int i = 0; i < 4; ++i)
                assertEquals(p.getTrackCount(i), 2);

            assertTrue(p.getOutputTrackFormat(0).equals(new Format("abc")));
            assertTrue(p.getOutputTrackFormat(1).equals(new Format("xyz")));
            assertEquals(p.getOutputTrackFormat(2), null);
            assertTrue(p.isFormatAcceptable(0, new Format("abc")));
            assertTrue(p.isFormatAcceptable(0, new Format(null)));
            assertFalse(p.isFormatAcceptable(0, new Format("xyz")));
            assertTrue(p.isFormatAcceptable(2, new Format(null)));
            try
            {
                p.isFormatAcceptable(0, null);
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }

            final StringBuffer b = new StringBuffer();
            assertTrue(p.isFormatAcceptable(0, new Format("abc")
            {
                // @Override
                @Override
                public boolean matches(Format arg0)
                {
                    b.append("matches called");
                    return super.matches(arg0);
                }
            }));

            assertEquals(b.toString(), "matches called");
        }

    }
}
