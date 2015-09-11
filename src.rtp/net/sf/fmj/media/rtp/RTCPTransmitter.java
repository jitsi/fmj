package net.sf.fmj.media.rtp;

/**
 * The <tt>RTCPTransmitter</tt> is owned by an <tt>RTCPReporter</tt> and its
 * purpose is to generate and transmit RTCP reports based on stats that it
 * collects.
 *
 * Created by gpolitis on 8/25/15.
 */
public interface RTCPTransmitter
{
    void bye(String reason);

    void close();

    /**
     * Runs in the reporting thread and it invokes the report() method of the
     * <tt>RTCPTerminationStrategy</tt> of all the <tt>MediaStream</tt>s of the
     * associated <tt>RTPTranslator</tt>.
     */
    void report();

    void setSSRCInfo(SSRCInfo info);

    SSRCInfo getSSRCInfo();

    SSRCCache getCache();

    RTCPRawSender getSender();
}
