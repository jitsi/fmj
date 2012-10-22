package ejmf.toolkit.gui.multiimage;

import javax.swing.ImageIcon;

/**
 * Stores an ImageIcon and the amount of time the ImageIcon is to
 * be displayed.  For use with a MultiImageComponent.
 *
 * @see        MultiImageComponent
 *
 * @author     Steve Talley
 */
public class MultiImageFrame {
    /**
     * An ImageIcon.
     */
    public ImageIcon icon;
    /**
     * The time in nanoseconds to display the image.
     */
    public long delay;

    /**
     * Construct a MultiImageFrame with the given ImageIcon and
     * delay.
     */
    public MultiImageFrame(ImageIcon icon, long delay) {
        this.icon = icon;
        this.delay = delay;
    }
}
