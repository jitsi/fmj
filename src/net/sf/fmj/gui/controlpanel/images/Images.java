package net.sf.fmj.gui.controlpanel.images;

import java.util.*;

import javax.swing.*;

/**
 *
 * @author Warren Bloomer
 *
 */
public final class Images
{
    public static final String SLIDER_THUMB_HORIZ = "slider_thumb_horiz.png";
    public static final String SLIDER_THUMB_VERT = "slider_thumb_vert.png";

    public static final String MEDIA_PLAY = "Play24.gif";
    public static final String MEDIA_STOP = "Stop24.gif";
    public static final String MEDIA_PAUSE = "Pause24.gif";
    public static final String MEDIA_REWIND = "Rewind24.gif";
    public static final String MEDIA_FASTFORWARD = "FastForward24.gif";
    public static final String MEDIA_STEPBACK = "StepBack24.gif";
    public static final String MEDIA_STEPFORWARD = "StepForward24.gif";
    public static final String MUTE_OFF = "Volume24.gif";
    public static final String MUTE_ON = "VolumeOff24.gif";

    private static final Images singleton = new Images();

    private static final String basePath = '/' + Images.class.getPackage()
            .getName().replace('.', '/') + '/';

    public static void flush()
    {
        singleton.doFlush();
    }

    public static ImageIcon get(String name)
    {
        return singleton.doGet(name);
    }

    public static void main(String[] args)
    {
        System.out.println("" + basePath);
    }

    private final Map<String,ImageIcon> images
        = new HashMap<String,ImageIcon>();

    private void doFlush()
    {
        images.clear();
    }

    private ImageIcon doGet(String imageName)
    {
        ImageIcon icon = (ImageIcon) images.get(imageName);
        if (icon == null)
        {
            icon = new ImageIcon(getClass().getResource(basePath + imageName));
            images.put(imageName, icon);
        }

        return icon;
    }
}
