package net.sf.fmj.test.compat.misc;

import java.io.*;
import java.net.*;

import javax.media.*;

import junit.framework.*;

public class MediaLocatorTest extends TestCase
{
    public void testMediaLocator() throws MalformedURLException
    {
        File f = new File("test.txt");
        System.out.println(f.toURI());
        System.out.println(f.toURI().toURL());
        MediaLocator l = new MediaLocator(f.toURI().toURL());

    }
}
