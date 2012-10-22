package net.sf.fmj.test.compat.misc;

import java.util.*;

import javax.media.*;

import junit.framework.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class PackageManagerTest extends TestCase
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

    private void dump()
    {
        System.out.println("getContentPrefixList:");
        final Vector v = PackageManager.getContentPrefixList();
        for (int i = 0; i < v.size(); ++i)
        {
            System.out.println(v.get(i));
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

    public void testPackageManager_Content()
    {
        synchronized (PackageManager.class)
        {
            assertTrue(PackageManager.getContentPrefixList().contains("javax"));
            assertTrue(PackageManager.getContentPrefixList()
                    .contains("com.sun"));
            assertTrue(PackageManager.getContentPrefixList()
                    .contains("com.ibm"));

            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax", "com.sun", "com.ibm" });
                final Vector v = PackageManager.getContentPrefixList();
                v.remove("javax");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {"com.sun", "com.ibm"}); // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "com.sun", "com.ibm", "javax" });

                // dumpStringVector(PackageManager.getContentPrefixList());
            }

            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "com.sun", "com.ibm", "javax" });
                final Vector v = PackageManager.getContentPrefixList();
                v.remove("com.sun");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {"com.ibm", "javax"}); // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "com.ibm", "javax" });

                // dumpStringVector(PackageManager.getContentPrefixList());
            }

            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "com.ibm", "javax" });
                final Vector v = PackageManager.getContentPrefixList();
                v.remove("com.ibm");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {"javax"}); // this fails with the FMJ
                // JmfRegistry because it copies the data. This is an acceptable
                // difference between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax" });

                // dumpStringVector(PackageManager.getContentPrefixList());
            }

            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax" });
                final Vector v = PackageManager.getContentPrefixList();
                v.remove("javax");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {}); // this fails with the FMJ JmfRegistry
                // because it copies the data. This is an acceptable difference
                // between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax" });

                // dumpStringVector(PackageManager.getContentPrefixList());
            }

            {
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax" });
                final Vector v = PackageManager.getContentPrefixList();
                v.add("abc");
                // dumpStringVector(PackageManager.getContentPrefixList());
                // assertStringVectorEquals(PackageManager.getContentPrefixList(),
                // new String[] {"javax", "abc"}); // this fails with the FMJ
                // JmfRegistry because it copies the data. This is an acceptable
                // difference between JMF and FMJ.
                PackageManager.setContentPrefixList(v);
                assertStringVectorEquals(PackageManager.getContentPrefixList(),
                        new String[] { "javax", "abc" });

                // dumpStringVector(PackageManager.getContentPrefixList());
            }

            {
                final Vector v = new Vector();
                v.add("javax");
                v.add("com.sun");
                v.add("com.ibm");
                PackageManager.setContentPrefixList(v);
            }
        }

    }

    public void testPackageManager_Protocol()
    {
        synchronized (PackageManager.class)
        {
            assertTrue(PackageManager.getProtocolPrefixList().contains("javax"));
            assertTrue(PackageManager.getProtocolPrefixList().contains(
                    "com.sun"));
            assertTrue(PackageManager.getProtocolPrefixList().contains(
                    "com.ibm"));

            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "javax", "com.sun", "com.ibm" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.remove("javax");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {"com.sun", "com.ibm"}); // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "com.sun", "com.ibm", "javax" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
            }

            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "com.sun", "com.ibm", "javax" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.remove("com.sun");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {"com.ibm", "javax"}); // // this fails with the
                // FMJ JmfRegistry because it copies the data. This is an
                // acceptable difference between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "com.ibm", "javax" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
            }

            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "com.ibm", "javax" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.remove("com.ibm");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {"javax"}); // this fails with the FMJ
                // JmfRegistry because it copies the data. This is an acceptable
                // difference between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(),
                        new String[] { "javax" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
            }

            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(),
                        new String[] { "javax" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.remove("javax");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {}); // this fails with the FMJ JmfRegistry
                // because it copies the data. This is an acceptable difference
                // between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(),
                        new String[] { "javax" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
            }

            {
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(),
                        new String[] { "javax" });
                final Vector v = PackageManager.getProtocolPrefixList();
                v.add("abc");
                // dumpStringVector(PackageManager.getProtocolPrefixList());
                // assertStringVectorEquals(PackageManager.getProtocolPrefixList(),
                // new String[] {"javax", "abc"}); // this fails with the FMJ
                // JmfRegistry because it copies the data. This is an acceptable
                // difference between JMF and FMJ.
                PackageManager.setProtocolPrefixList(v);
                assertStringVectorEquals(
                        PackageManager.getProtocolPrefixList(), new String[] {
                                "javax", "abc" });

                // dumpStringVector(PackageManager.getProtocolPrefixList());
            }

            {
                final Vector v = new Vector();
                v.add("javax");
                v.add("com.sun");
                v.add("com.ibm");
                PackageManager.setProtocolPrefixList(v);

            }

        }

    }
}
