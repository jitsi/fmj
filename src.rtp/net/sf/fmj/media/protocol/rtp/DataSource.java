package net.sf.fmj.media.protocol.rtp;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.protocol.*;
import net.sf.fmj.media.rtp.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class DataSource extends BasicPushBufferDataSource implements
        Streamable, RTPSource
{
    class MyRTPControl extends RTPControlImpl
    {
        @Override
        public String getCNAME()
        {
            if (mgr == null)
                return null;
            SSRCInfo info = mgr.getSSRCInfo(ssrc);
            if (info != null)
                return info.getCNAME();
            return null;
        }

        @Override
        public int getSSRC()
        {
            return ssrc;
        }
    }

    /**
     * The name of the class {@link javax.media.rtp.RTPControl}.
     * {@link Class#getName()} will very likely cache the value it returns so
     * invoking the method in question multiple times should not be much of a
     * performance issue. Anyway, utilizing a constant is somewhat more flexible
     * because it allows the flexibility to choose whether to invoke the method
     * at all.
     */
    public static final String RTP_CONTROL_CLASS_NAME
        = RTPControl.class.getName();

    static int SSRC_UNDEFINED = 0; // RTPMediaLocator.SSRC_UNDEFINED
    private final RTPSourceStream[] srcStreams = new RTPSourceStream[1];

    Player streamplayer = null;
    RTPSessionMgr mgr = null;
    RTPControl rtpcontrol = new MyRTPControl();
    DataSource childsrc = null;

    int ssrc = SSRC_UNDEFINED;

    public DataSource()
    {
        // setContentType(ContentDescriptor.RAW);
        setContentType("rtp");
    }

    /**
     * Opens a connection to the source described by the URL.
     * <p>
     * Connect initiates communmication with the source.
     *
     * @exception IOException
     *                thrown if the connect has IO trouble.
     */
    @Override
    public void connect() throws IOException
    {
        // start the RTPSessionManager by calling startSession()
        // this will throw an IOException if there is any problem
        /*
         * if (mgr != null){ mgr.startSession(); }
         */
        if (srcStreams != null)
        {
            for (RTPSourceStream srcStream : srcStreams)
            {
                if (srcStream != null)
                    srcStream.connect();
            }
        }
        connected = true;
    }

    /**
     * Close the connection to the source described by the URL.
     * <p>
     * Disconnect frees resources used to maintain a connection to the source.
     * If no resources are in use, disconnect is ignored. Implies a stop, if
     * stop hasn't already been called.
     */
    @Override
    public void disconnect()
    {
        // once we have disconnected, set boolean to false
        // If this datasource was created by using the RTPAPI and not
        // via Manager, we dont want to disconnect this source i.e. we
        // dont want to closeSession() on RTPSM. In this case, the
        // datasource will not have a manager set to it. In this case,
        // the datasource cannot really be disconnected until the
        // session manager is closed by using the RTPAPI
        if (srcStreams != null)
        {
            for (RTPSourceStream srcStream : srcStreams)
                srcStream.close();
        }
        
//        if (mgr != null)
//        {
//            // close the RTPSourceStream
//            mgr.removeDataSource(this);
//            mgr.closeSession();
//            mgr = null; // to fix bug 4174773, multiple stream problem 9/18/98
//            started = false;
//            connected = false;
//        }
    }

    /**
     * A method to flush the data buffers int the DataSource.
     */
    public void flush()
    {
        srcStreams[0].reset();
    }

    public String getCNAME()
    {
        if (mgr == null)
            return null;
        SSRCInfo info = mgr.getSSRCInfo(ssrc);
        if (info != null)
            return info.getCNAME();
        return null;
    }

    /**
     * Returns a control over this instance of a specific runtime type.
     *
     * @param type the runtime type of the control over this instance to be
     * returned
     * @return a control of the specific runtime {@code type} if such a control
     * is provider by this instance; otherwise, {@code null}.
     */
    @Override
    public Object getControl(String type)
    {
        // XXX Class.forName(String) is (very) expensive (in terms of CPU at
        // least). I will optimize the implementation for the case of
        // javax.media.rtp.RTPControl because that is the only control the class
        // net.sf.fmj.media.protocol.rtp.DataSource provides. If extenders
        // provide other controls, they should better not rely on the default
        // implementation in performance sensitive scenarios.
        Class<?> clazz;

        if (RTP_CONTROL_CLASS_NAME.equals(type))
        {
            clazz = RTPControl.class;
        }
        else
        {
            try
            {
                clazz = Class.forName(type);
            }
            catch (ClassNotFoundException e)
            {
                return null;
            }
        }

        return getControl(clazz);
    }

    /**
     * Returns a control over this instance of a specific runtime type.
     *
     * @param clazz the runtime type of the control over this instance to be
     * returned
     * @return a control of the specific runtime type {@code clazz} if such a
     * control is provider by this instance; otherwise, {@code null}.
     */
    public <T> T getControl(Class<T> clazz)
    {
        for (Object control : getControls())
        {
            if (clazz.isInstance(control))
            {
                @SuppressWarnings("unchecked")
                T t = (T) control;

                return t;
            }
        }
        return null;
    }

    /**
     * Returns a one element array of {@link #rtpcontrol} object.
     *
     * @return a one element array of {@code rtpcontrol} object.
     */
    @Override
    public Object[] getControls()
    {
        // return a one element array of rtpcontrol object
        return new RTPControl[] { rtpcontrol };
    }

    public RTPSessionMgr getMgr()
    {
        return mgr;
    }

    public Player getPlayer()
    {
        return streamplayer;
    }

    public int getSSRC()
    {
        return ssrc;
    }

    /**
     * Obtain the collection of streams that this source manages. The collection
     * of streams is entirely content dependent. The mime-type of this
     * DataSource provides the only indication of what streams can be available
     * on this connection.
     *
     * @return collection of streams for this source.
     */
    @Override
    public PushBufferStream[] getStreams()
    {
        if (!connected)
            return null;
        return srcStreams;
    }

    public boolean isPrefetchable()
    {
        return false;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void prebuffer()
    {
        started = true;
        srcStreams[0].prebuffer();
    }

    public void setBufferListener(BufferListener listener)
    {
        srcStreams[0].setBufferListener(listener);
    }

    public void setBufferWhenStopped(boolean flag)
    {
        srcStreams[0].setBufferWhenStopped(flag);
    }

    public void setChild(DataSource source)
    {
        childsrc = source;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public void setControl(Object control)
    {
        rtpcontrol = (RTPControl) control;
    }

    @Override
    public void setLocator(MediaLocator mrl)
    {
        super.setLocator(mrl);
    }

    public void setMgr(RTPSessionMgr mgr)
    {
        // System.out.println("manager being set to " + mgr);
        this.mgr = mgr;
    }

    public void setPlayer(Player player)
    {
        streamplayer = player;
    }

    public void setSourceStream(RTPSourceStream stream)
    {
        if (srcStreams != null)
            srcStreams[0] = stream;
    }

    public void setSSRC(int ssrc)
    {
        this.ssrc = ssrc;
    }

    /**
     * Initiates data-transfer. Start must be called before data is available.
     * Connect must be called before start.
     *
     * @exception IOException
     *                thrown if the source has IO trouble at startup time.
     */
    @Override
    public void start() throws IOException
    {
        super.start();
        if (childsrc != null)
            childsrc.start();
        if (srcStreams != null)
        {
            for (RTPSourceStream srcStream : srcStreams)
                srcStream.start();
        }
    }

    /**
     * Stops data-transfer. If the source has not already been connected and
     * started, stop does nothing.
     */
    @Override
    public void stop() throws IOException
    {
        super.stop();
        // stop your child source as well
        if (childsrc != null)
            childsrc.stop();
        if (srcStreams != null)
        {
            for (RTPSourceStream srcStream : srcStreams)
                srcStream.stop();
        }
    }
}
