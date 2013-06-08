package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class ComException extends Exception
{
    public ComException(int hr)
    {
        super("hr=" + hr + " (0x" + Integer.toHexString(hr) + ")");
    }
}
