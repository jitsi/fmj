import java.io.*;
import java.net.*;

import javax.media.*;

/**
 *
 * from http://javasolution.blogspot.com/2007/04/sound-over-ip-with-jmf-rtp.html
 *
 */
public class SimpleVoiceReceiver
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String url = "rtp://192.168.1.1:22224/audio/16";

        MediaLocator mrl = new MediaLocator(url);

        // Create a player for this rtp session
        Player player = null;
        try
        {
            player = Manager.createPlayer(mrl);
        } catch (NoPlayerException e)
        {
            e.printStackTrace();
            System.exit(-1);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        if (player != null)
        {
            System.out.println("Player created.");
            player.realize();
            // wait for realizing
            while (player.getState() != Controller.Realized)
            {
                try
                {
                    Thread.sleep(10);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("Starting player");
            player.start();
        } else
        {
            System.err.println("Player doesn't created.");
            System.exit(-1);
        }

        System.out.println("Exiting.");
    }

}