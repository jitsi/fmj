package net.sf.fmj.media.protocol.rtp;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.rtp.*;

import net.sf.fmj.media.protocol.*;
import net.sf.fmj.media.rtp.*;

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

    static int SSRC_UNDEFINED = 0; // RTPMediaLocator.SSRC_UNDEFINED
    private final RTPSourceStream[] srcStreams;

    Player streamplayer = null;
    RTPSessionMgr mgr = null;
    RTPControl rtpcontrol = null;
    DataSource childsrc = null;

    int ssrc = SSRC_UNDEFINED;

    public DataSource()
    {
        srcStreams = new RTPSourceStream[1];
        rtpcontrol = new MyRTPControl();
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
            for (int i = 0; i < srcStreams.length; i++)
            {
                if (srcStreams[i] != null)
                    srcStreams[i].connect();
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
            for (int i = 0; i < srcStreams.length; i++)
                srcStreams[i].close();
        }
        /*
         * if (mgr != null){ // close the RTPSourceStream
         * mgr.removeDataSource(this); mgr.closeSession(); mgr = null; // to fix
         * bug 4174773, multiple stream problem 9/18/98 started = false;
         * connected = false; return; }
         */
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
     * Returns <tt>null</tt> because no controls are implemented.
     *
     * @return <tt>null</tt>.
     */
    @Override
    public Object getControl(String type)
    {
        Class<?> cls;
        try
        {
            cls = Class.forName(type);
        } catch (ClassNotFoundException e)
        {
            return null;
        }
        Object cs[] = getControls();
        for (int i = 0; i < cs.length; i++)
        {
            if (cls.isInstance(cs[i]))
                return cs[i];
        }
        return null;
    }

    /**
     * Returns an zero length array because no controls are supported.
     *
     * @return a zero length <tt>Object</tt> array.
     */
    @Override
    public Object[] getControls()
    {
        // return a one element array of rtpcontrol object
        RTPControl[] controls = new RTPControl[1];
        controls[0] = rtpcontrol;
        return controls;
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
            for (int i = 0; i < srcStreams.length; i++)
                srcStreams[i].start();
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
            for (int i = 0; i < srcStreams.length; i++)
                srcStreams[i].stop();
        }
    }
}
