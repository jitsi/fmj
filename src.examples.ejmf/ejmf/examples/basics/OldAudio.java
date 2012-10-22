package ejmf.examples.basics;

import java.applet.Applet;
import java.applet.AudioClip;

/**
* Non-JMF example of playing an audio (.au) file
* using Applet methods
*/
public class OldAudio extends Applet {

    private AudioClip	clip;

    public void start() {
	clip.loop();
    }

    public void stop() {
	clip.stop();
    }

    public void init() {
        String 	media;

        if((media = getParameter("MEDIA")) == null) {
            System.err.println("Invalid MEDIA file parameter");
            return;
        }

	clip = getAudioClip(getCodeBase(), media);
    }
}
