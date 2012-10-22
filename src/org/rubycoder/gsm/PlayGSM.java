package org.rubycoder.gsm;

// $Id: PlayGSM.java,v 1.3 2012/04/03 07:31:54 lyub0m1r Exp $

import java.io.*;
import java.net.*;

import sun.audio.*;

public class PlayGSM
{
    public static void main(String argv[])
    {
        if (argv.length != 1)
        {
            System.out.println("Usage: PlayGSM <url>");
            System.exit(0);
        }
        stream(argv[0]);
    }

    private static void stream(String u)
    {
        URL url = null;

        try
        {
            url = new URL(u);
        } catch (MalformedURLException mue)
        {
            System.out.println("The URL is invalid.");
            System.exit(1);
        }

        InputStream gsmStream = null;
        InputStream auStream = null;

        try
        {
            gsmStream = url.openStream();
        } catch (IOException ioe)
        {
            System.err.println("IO exception occured.");
            System.exit(1);
        }

        auStream = new GSMDecoderStream(gsmStream);

        int i;
        int x;
        byte[] b = new byte[1];

        /*
         * try { while ((i=auStream.read(b))!=-1) { System.out.println(b[0]); }
         * } catch (IOException ioe) { }
         */

        AudioPlayer.player.start(auStream);

    }

}
