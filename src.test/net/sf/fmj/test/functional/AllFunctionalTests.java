package net.sf.fmj.test.functional;

import junit.framework.*;

/**
 * 
 * @author Warren Bloomer
 * @author Ken Larson
 * 
 */
public class AllFunctionalTests
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for net.sf.fmj.test.functional");
        // $JUnit-BEGIN$
        suite.addTestSuite(ControllerTester.class);
        suite.addTestSuite(RegistryTest.class);
        // $JUnit-END$
        return suite;
    }

}
