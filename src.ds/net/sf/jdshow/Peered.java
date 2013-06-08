package net.sf.jdshow;

/**
 * Base class, holds a native pointer to a peer object.
 *
 * @author Ken Larson
 */
public class Peered
{
    private long peerPtr; // use long to support 64-bit machines as well as 32

    public Peered(long ptr)
    {
        super();

        peerPtr = ptr;
    }

    public long getPeerPtr()
    {
        return peerPtr;
    }

}
