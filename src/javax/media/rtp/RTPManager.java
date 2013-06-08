package javax.media.rtp;

import java.util.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.Controls;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.rtcp.*;

import net.sf.fmj.utility.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/RTPManager.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 */
public abstract class RTPManager implements Controls
{
    // Sun's does a reflection trick, much like PlugInManager, etc. We don't do
    // that here.

    private static final Logger logger = LoggerSingleton.logger;

    // returns a vector of string
    public static Vector<String> getRTPManagerList()
    {
        final Vector<String> result = new Vector<String>();

        // mgodehardt: disabled this, why is this need, there is no
        // media.rtp.RTPSessionMgr
        // kenlars99: this is useless, was only here to match JMF's behavior
        // exactly.
        // result.add("media.rtp.RTPSessionMgr");

        for (Object prefix : PackageManager.getProtocolPrefixList())
        {
            result.add(prefix + ".media.rtp.RTPSessionMgr");
        }

        return result;
    }

    public static RTPManager newInstance()
    {
        for (String className : getRTPManagerList())
        {
            try
            {
                logger.finer("Trying RTPManager class: " + className);
                final Class<?> clazz = Class.forName(className);
                return (RTPManager) clazz.newInstance();
            } catch (ClassNotFoundException e)
            {
                logger.finer("RTPManager.newInstance: ClassNotFoundException: "
                        + className);
                continue;
            } catch (Exception e)
            {
                logger.log(Level.WARNING, "" + e, e);
                continue;
            }
        }
        return null;
    }

    public RTPManager()
    {
        super();
    }

    public abstract void addFormat(Format format, int payload);

    public abstract void addReceiveStreamListener(ReceiveStreamListener listener);

    public abstract void addRemoteListener(RemoteListener listener);

    public abstract void addSendStreamListener(SendStreamListener listener);

    public abstract void addSessionListener(SessionListener listener);

    public abstract void addTarget(SessionAddress remoteAddress)
            throws InvalidSessionAddressException, java.io.IOException;

    public abstract SendStream createSendStream(DataSource dataSource,
            int streamIndex) throws UnsupportedFormatException,
            java.io.IOException;

    public abstract void dispose();

    public abstract Vector getActiveParticipants();

    public abstract Vector getAllParticipants();

    public abstract GlobalReceptionStats getGlobalReceptionStats();

    public abstract GlobalTransmissionStats getGlobalTransmissionStats();

    public abstract LocalParticipant getLocalParticipant();

    public abstract Vector getPassiveParticipants();

    public abstract Vector getReceiveStreams();

    public abstract Vector getRemoteParticipants();

    public abstract Vector getSendStreams();

    public abstract void initialize(RTPConnector connector);

    public abstract void initialize(SessionAddress localAddress)
            throws InvalidSessionAddressException, java.io.IOException;

    public abstract void initialize(SessionAddress[] localAddresses,
            SourceDescription[] sourceDescription,
            double rtcpBandwidthFraction, double rtcpSenderBandwidthFraction,
            EncryptionInfo encryptionInfo)
            throws InvalidSessionAddressException, java.io.IOException;

    public abstract void removeReceiveStreamListener(
            ReceiveStreamListener listener);

    public abstract void removeRemoteListener(RemoteListener listener);

    public abstract void removeSendStreamListener(SendStreamListener listener);

    public abstract void removeSessionListener(SessionListener listener);

    public abstract void removeTarget(SessionAddress remoteAddress,
            String reason) throws InvalidSessionAddressException;

    public abstract void removeTargets(String reason);
}
