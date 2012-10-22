package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.event.*;

import net.sf.fmj.media.rtp.util.*;

public class RTPSinkStream implements BufferTransferHandler
{
    private RTPMediaThread thread;
    Buffer current;
    boolean started;
    Object startReq;
    RTPTransmitter transmitter;
    RTPRawSender sender;
    SendSSRCInfo info;
    static AudioFormat mpegAudio = new AudioFormat("mpegaudio/rtp");
    static VideoFormat mpegVideo = new VideoFormat("mpeg/rtp");
    long startTime;
    long startPT;
    int rate;
    boolean mpegBFrame;
    boolean mpegPFrame;
    boolean bufSizeSet;
    long audioPT;
    static int THRESHOLD = 80;
    static int LEEWAY = 5;

    public RTPSinkStream()
    {
        thread = null;
        current = new Buffer();
        started = false;
        startReq = new Integer(0);
        transmitter = null;
        sender = null;
        info = null;
        startTime = 0L;
        startPT = -1L;
        mpegBFrame = false;
        mpegPFrame = false;
        bufSizeSet = false;
        audioPT = 0L;
    }

    protected void close()
    {
        stop();
    }

    protected void setSSRCInfo(SendSSRCInfo info)
    {
        this.info = info;
    }

    protected void setTransmitter(RTPTransmitter t)
    {
        transmitter = t;
        if (transmitter != null)
            sender = transmitter.getSender();
    }

    public void start()
    {
        if (started)
            return;
        started = true;
        synchronized (startReq)
        {
            startReq.notifyAll();
        }
    }

    public void startStream()
    {
    }

    public void stop()
    {
        started = false;
        startPT = -1L;
        synchronized (startReq)
        {
            startReq.notifyAll();
        }
    }

    public void transferData(PushBufferStream stream)
    {
        try
        {
            synchronized (startReq)
            {
                while (!started)
                {
                    startPT = -1L;
                    startReq.wait();
                }
            }
            stream.read(current);
            if (!current.getFormat().matches(info.myformat))
            {
                int payload = transmitter.cache.sm.formatinfo
                        .getPayload(current.getFormat());
                if (payload == -1)
                    return;
                LocalPayloadChangeEvent evt = new LocalPayloadChangeEvent(
                        transmitter.cache.sm, info,
                        ((SSRCInfo) (info)).payloadType, payload);
                transmitter.cache.eventhandler.postEvent(evt);
                info.payloadType = payload;
                info.myformat = current.getFormat();
            }
            if (info.myformat instanceof VideoFormat)
                transmitVideo();
            else if (info.myformat instanceof AudioFormat)
                transmitAudio();
        } catch (Exception e)
        {
        }
    }

    private void transmitAudio()
    {
        if (current.isEOM() || current.isDiscard())
        {
            startPT = -1L;
            return;
        }
        if (startPT == -1L)
        {
            startTime = System.currentTimeMillis();
            startPT = current.getTimeStamp() <= 0L ? 0L : current
                    .getTimeStamp() / 0xf4240L;
            audioPT = startPT;
        }
        if ((current.getFlags() & 0x60) == 0)
        {
            if (mpegAudio.matches(current.getFormat()))
                audioPT = current.getTimeStamp() / 0xf4240L;
            else
                audioPT += ((AudioFormat) info.myformat)
                        .computeDuration(current.getLength()) / 0xf4240L;
            waitForPT(startTime, startPT, audioPT);
        }
        transmitter.TransmitPacket(current, info);
    }

    private void transmitVideo()
    {
        if (current.isEOM() || current.isDiscard())
        {
            startPT = -1L;
            mpegBFrame = false;
            mpegPFrame = false;
            return;
        }
        if (startPT == -1L)
        {
            startTime = System.currentTimeMillis();
            startPT = current.getTimeStamp() / 0xf4240L;
        }
        if (current.getTimeStamp() > 0L && (current.getFlags() & 0x60) == 0
                && (current.getFlags() & 0x800) != 0)
            if (mpegVideo.matches(info.myformat))
            {
                byte payload[] = (byte[]) current.getData();
                int offset = current.getOffset();
                int ptype = payload[offset + 2] & 7;
                if (ptype > 2)
                    mpegBFrame = true;
                else if (ptype == 2)
                    mpegPFrame = true;
                if (ptype > 2 || ptype == 2 && !mpegBFrame || ptype == 1
                        && !(mpegBFrame | mpegPFrame))
                    waitForPT(startTime, startPT,
                            current.getTimeStamp() / 0xf4240L);
            } else
            {
                waitForPT(startTime, startPT, current.getTimeStamp() / 0xf4240L);
            }
        transmitter.TransmitPacket(current, info);
    }

    private void waitForPT(long start, long startPT, long pt)
    {
        for (long delay = pt - startPT - (System.currentTimeMillis() - start); delay > LEEWAY; delay = pt
                - startPT - (System.currentTimeMillis() - start))
        {
            if (delay > THRESHOLD)
                delay = THRESHOLD;
            try
            {
                Thread.currentThread();
                Thread.sleep(delay);
                continue;
            } catch (Exception e)
            {
            }
            break;
        }
    }
}
