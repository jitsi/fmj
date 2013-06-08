package javax.media.rtp;

import javax.media.*;
import javax.media.Controls;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.rtcp.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/SessionManager.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 * @deprecated
 */
@Deprecated
public interface SessionManager extends Controls
{
    public static final long SSRC_UNSPEC = 0L;

    public void addFormat(Format fmt, int payload);

    public void addPeer(SessionAddress peerAddress) throws java.io.IOException,
            InvalidSessionAddressException;

    public void addReceiveStreamListener(ReceiveStreamListener listener);

    public void addRemoteListener(RemoteListener listener);

    public void addSendStreamListener(SendStreamListener listener);

    public void addSessionListener(SessionListener listener);

    public void closeSession(String reason);

    public SendStream createSendStream(DataSource ds, int streamindex)
            throws UnsupportedFormatException, java.io.IOException;

    public SendStream createSendStream(int ssrc, DataSource ds, int streamindex)
            throws UnsupportedFormatException, SSRCInUseException,
            java.io.IOException;

    public String generateCNAME();

    public long generateSSRC();

    public java.util.Vector getActiveParticipants();

    public java.util.Vector getAllParticipants();

    public long getDefaultSSRC();

    public GlobalReceptionStats getGlobalReceptionStats();

    public GlobalTransmissionStats getGlobalTransmissionStats();

    public LocalParticipant getLocalParticipant();

    public SessionAddress getLocalSessionAddress();

    public int getMulticastScope();

    public java.util.Vector getPassiveParticipants();

    public java.util.Vector getPeers();

    public java.util.Vector getReceiveStreams();

    public java.util.Vector getRemoteParticipants();

    public java.util.Vector getSendStreams();

    public SessionAddress getSessionAddress();

    public RTPStream getStream(long filterssrc);

    public int initSession(SessionAddress localAddress, long defaultSSRC,
            SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
            double rtcp_sender_bw_fraction)
            throws InvalidSessionAddressException;

    public int initSession(SessionAddress localAddress,
            SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
            double rtcp_sender_bw_fraction)
            throws InvalidSessionAddressException;

    public void removeAllPeers();

    public void removePeer(SessionAddress peerAddress);

    public void removeReceiveStreamListener(ReceiveStreamListener listener);

    public void removeRemoteListener(RemoteListener listener);

    public void removeSendStreamListener(SendStreamListener listener);

    public void removeSessionListener(SessionListener listener);

    public void setMulticastScope(int multicastScope);

    public int startSession(int mcastScope, EncryptionInfo encryptionInfo)
            throws java.io.IOException;

    public int startSession(SessionAddress destAddress, int mcastScope,
            EncryptionInfo encryptionInfo) throws java.io.IOException,
            InvalidSessionAddressException;

    public int startSession(SessionAddress localReceiverAddress,
            SessionAddress localSenderAddress,
            SessionAddress remoteReceiverAddress, EncryptionInfo encryptionInfo)
            throws java.io.IOException, InvalidSessionAddressException;

}