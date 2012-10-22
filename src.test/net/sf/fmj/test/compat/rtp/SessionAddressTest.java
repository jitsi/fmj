package net.sf.fmj.test.compat.rtp;

import java.net.*;

import javax.media.rtp.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class SessionAddressTest extends TestCase
{
    public void testSessionAddress() throws Exception
    {
        {
            SessionAddress a = new SessionAddress();
            assertEquals(a.getControlAddress(), null);
            try
            {
                a.getControlHostAddress();
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }
            assertEquals(a.getControlPort(), -1);
            assertEquals(a.getDataAddress(), null);
            try
            {
                a.getDataHostAddress();
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }
            assertEquals(a.getDataPort(), -1);
            assertEquals(a.getTimeToLive(), 0);
            assertEquals(a.toString(),
                    "DataAddress: null\nControlAddress: null\nDataPort: -1\nControlPort: -1");

            try
            {
                a.equals(new SessionAddress());
                assertTrue(false);
            } catch (NullPointerException e)
            {
            }

        }

        {
            InetAddress ia = InetAddress.getLocalHost();
            SessionAddress a = new SessionAddress(ia, 1234);
            assertTrue(a.getControlAddress() == ia);
            assertEquals(a.getControlHostAddress(), ia.getHostAddress());

            assertEquals(a.getControlPort(), 1235);
            assertTrue(a.getDataAddress() == ia);
            assertEquals(a.getDataHostAddress(), ia.getHostAddress());

            assertEquals(a.getDataPort(), 1234);
            assertEquals(a.getTimeToLive(), 0);
            assertEquals(a.toString(), "DataAddress: " + ia.toString()
                    + "\nControlAddress: " + ia.toString()
                    + "\nDataPort: 1234\nControlPort: 1235");

            SessionAddress a2 = new SessionAddress(ia, 1234);
            assertEquals(a, a2);

            assertTrue(InetAddress.getLocalHost() != ia);
            SessionAddress a3 = new SessionAddress(InetAddress.getLocalHost(),
                    1234);
            assertEquals(a, a3);

            SessionAddress a4 = new SessionAddress(ia, 1235);
            assertFalse(a.equals(a4));
        }

        {
            InetAddress ia = InetAddress.getLocalHost();
            SessionAddress a = new SessionAddress(ia, 1234, 100);
            assertTrue(a.getControlAddress() == ia);
            assertEquals(a.getControlHostAddress(), ia.getHostAddress());

            assertEquals(a.getControlPort(), 1235);
            assertTrue(a.getDataAddress() == ia);
            assertEquals(a.getDataHostAddress(), ia.getHostAddress());

            assertEquals(a.getDataPort(), 1234);
            assertEquals(a.getTimeToLive(), 100);
            assertEquals(a.toString(), "DataAddress: " + ia.toString()
                    + "\nControlAddress: " + ia.toString()
                    + "\nDataPort: 1234\nControlPort: 1235");

            SessionAddress a3 = new SessionAddress(InetAddress.getLocalHost(),
                    1234, 100);
            assertEquals(a, a3);

            SessionAddress a4 = new SessionAddress(InetAddress.getLocalHost(),
                    1234, 101);
            assertEquals(a, a4);

        }

        {
            InetAddress ia = InetAddress.getLocalHost();
            InetAddress ia2 = InetAddress.getByName("www.google.com");
            SessionAddress a = new SessionAddress(ia, 1234, ia2, 9999);
            assertTrue(a.getControlAddress() == ia2);
            assertEquals(a.getControlHostAddress(), ia2.getHostAddress());

            assertEquals(a.getControlPort(), 9999);
            assertTrue(a.getDataAddress() == ia);
            assertEquals(a.getDataHostAddress(), ia.getHostAddress());

            assertEquals(a.getDataPort(), 1234);
            assertEquals(a.getTimeToLive(), 0);
            assertEquals(a.toString(), "DataAddress: " + ia.toString()
                    + "\nControlAddress: " + ia2.toString()
                    + "\nDataPort: 1234\nControlPort: 9999");

        }
    }
}
