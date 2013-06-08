/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rubycoder.gsm;

/**
 *
 * @author kane
 */
public class InvalidGSMFrameException extends Exception
{
    /**
     * Creates a new instance of <tt>InvalidGSMFrameException</tt> without
     * detail message.
     */
    public InvalidGSMFrameException()
    {
    }

    /**
     * Constructs an instance of <tt>InvalidGSMFrameException</tt> with the
     * specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public InvalidGSMFrameException(String msg)
    {
        super(msg);
    }
}
