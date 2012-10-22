package net.sf.fmj.media.control;

/**
 * A StringControl holds a string value and can be used to display status
 * information pertaining to the player. In most cases this will be a read-only
 * control.
 */
public interface StringControl extends AtomicControl
{
    String getTitle();

    /**
     * Returns the string value for this control.
     */
    String getValue();

    String setTitle(String title);

    /**
     * ??? Sets the string value for this control. Returns the actual string
     * that was set.
     */
    String setValue(String value);
}
