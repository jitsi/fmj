package net.sf.fmj.test.compat.rtp;

import java.util.*;

import javax.media.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class RTPManagerTest extends TestCase
{
    private void assertStringVectorEquals(Vector v1, String[] a)
    {
        assertEquals(v1 == null, a == null);
        if (v1 == null)
            return;

        assertEquals(v1.size(), a.length);
        for (int j = 0; j < v1.size(); ++j)
        {
            final String s1 = (String) v1.get(j);
            final String s2 = a[j];
            assertEquals(s1, s2);
        }
    }

    private void assertStringVectorEquals(Vector v1, Vector v2)
    {
        assertEquals(v1 == null, v2 == null);
        if (v1 == null)
            return;

        assertEquals(v1.size(), v2.size());
        for (int j = 0; j < v1.size(); ++j)
        {
            final String s1 = (String) v1.get(j);
            final String s2 = (String) v2.get(j);
            assertEquals(s1, s2);
        }
    }

    private void dumpStringVector(Vector v1)
    {
        for (int i = 0; i < v1.size(); ++i)
        {
            String s = (String) v1.get(i);
            System.out.println(s);
        }
    }

    // @Override
    @Override
    protected void setUp() throws Exception
    {
        // duplicated from the test suite set up so we can run this test alone

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

        super.setUp();
    }

    public void testRTPManager()
    {
        synchronized (PackageManager.class) // to prevent conflicts with other
                                            // tests that modify the package
                                            // manager
        {
            {
                Vector v = javax.media.rtp.RTPManager.getRTPManagerList();
                // dumpStringVector(v);
                assertStringVectorEquals(
                        javax.media.rtp.RTPManager.getRTPManagerList(),
                        new String[] { "media.rtp.RTPSessionMgr",
                                "javax.media.rtp.RTPSessionMgr",
                                "com.sun.media.rtp.RTPSessionMgr",
                                "com.ibm.media.rtp.RTPSessionMgr" });
            }
            // show that getRTPManagerList is not using the content prefix list:
            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax", "com.sun", "com.ibm" });
                final Vector v = PackageManager.getContentPrefixList();
                v.remove("com.sun");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {"com.ibm", "javax"}); // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax", "com.ibm" });

                // dumpStringVector(PackageManager.getContentPrefixList());
                // dumpStringVector(javax.media.rtp.RTPManager.getRTPManagerList());
                assertStringVectorEquals(
                        javax.media.rtp.RTPManager.getRTPManagerList(),
                        new String[] { "media.rtp.RTPSessionMgr",
                                "javax.media.rtp.RTPSessionMgr",
                                "com.sun.media.rtp.RTPSessionMgr",
                                "com.ibm.media.rtp.RTPSessionMgr" });
            }

            // show that getRTPManagerList is using the protocol prefix list:
            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "javax", "com.sun", "com.ibm" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.remove("com.sun");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {"com.ibm", "javax"}); // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "javax", "com.ibm" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // dumpStringVector(javax.media.rtp.RTPManager.getRTPManagerList());
                assertStringVectorEquals(
                        javax.media.rtp.RTPManager.getRTPManagerList(),
                        new String[] { "media.rtp.RTPSessionMgr",
                                "javax.media.rtp.RTPSessionMgr",
                                "com.ibm.media.rtp.RTPSessionMgr" });
            }
        }
    }
}
