package net.sf.fmj.media.multiplexer;

import java.io.*;

/**
 * Enhances PipedInputStream to have a larger buffer.
 *
 * @author Ken Larson
 *
 */
public class BigPipedInputStream extends PipedInputStream
{
    public BigPipedInputStream(int size)
    {
        super();
        this.buffer = new byte[size];
    }

}
