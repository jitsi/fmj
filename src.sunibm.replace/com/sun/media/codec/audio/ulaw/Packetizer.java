package com.sun.media.codec.audio.ulaw;

/**
 * 
 * @author Ken Larson
 * 
 */
public class Packetizer extends net.sf.fmj.media.codec.audio.ulaw.Packetizer
{
    protected String PLUGIN_NAME = "ULAW Packetizer"; // TODO: hacked for
                                                      // SIP-Communicator

    protected int packetSize; // TODO: hacked for SIP-Communicator

    @Override
    public String getName()
    {
        return PLUGIN_NAME;
    }
}
