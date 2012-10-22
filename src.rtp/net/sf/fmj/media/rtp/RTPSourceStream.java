package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.protocol.*;
import net.sf.fmj.media.protocol.rtp.DataSource;
import net.sf.fmj.media.rtp.util.*;

public class RTPSourceStream extends BasicSourceStream implements
        PushBufferStream, Runnable
{
    class PktQue
    {
        int FUDGE;

        int DEFAULT_AUD_PKT_SIZE;

        int DEFAULT_MILLISECS_PER_PKT;

        int DEFAULT_PKTS_TO_BUFFER;

        int MIN_BUF_CHECK;

        int BUF_CHECK_INTERVAL;

        int pktsEst;

        int framesEst;

        int fps;

        int pktsPerFrame;

        int sizePerPkt;

        int maxPktsToBuffer;

        int sockBufSize;

        int tooMuchBufferingCount;

        long lastPktSeq;

        long lastCheckTime;

        Buffer fill[];

        Buffer free[];

        int headFill;

        int tailFill;

        int headFree;

        int tailFree;

        protected int size;

        public PktQue(int i)
        {
            FUDGE = 5;
            DEFAULT_AUD_PKT_SIZE = 256;
            DEFAULT_MILLISECS_PER_PKT = 30;
            // damencho increase packets to buffer was 30
            DEFAULT_PKTS_TO_BUFFER = 90;
            MIN_BUF_CHECK = 10000;
            BUF_CHECK_INTERVAL = 7000;
            framesEst = 0;
            fps = 15;
            pktsPerFrame = DEFAULT_VIDEO_RATE;
            sizePerPkt = DEFAULT_AUD_PKT_SIZE;
            maxPktsToBuffer = 0;
            sockBufSize = 0;
            tooMuchBufferingCount = 0;
            lastPktSeq = 0L;
            lastCheckTime = 0L;
            allocBuffers(i);
        }

        public synchronized void addPkt(Buffer buffer)
        {
            long l = -1L;
            long l1 = -1L;
            long l2 = buffer.getSequenceNumber();
            if (moreFilled())
            {
                l = fill[headFill].getSequenceNumber();
                int i = tailFill - 1;
                if (i < 0)
                    i = size - 1;
                l1 = fill[i].getSequenceNumber();
            }
            if (l == -1L && l1 == -1L)
                append(buffer);
            else if (l2 < l)
                prepend(buffer);
            else if (l < l2 && l2 < l1)
                insert(buffer);
            else if (l2 > l1)
                append(buffer);
            else
                returnFree(buffer);
        }

        private void allocBuffers(int i)
        {
            fill = new Buffer[i];
            free = new Buffer[i];
            for (int j = 0; j < i - 1; j++)
                free[j] = new Buffer();

            size = i;
            headFill = tailFill = 0;
            headFree = 0;
            tailFree = size - 1;
        }

        private synchronized void append(Buffer buffer)
        {
            fill[tailFill] = buffer;
            tailFill++;
            if (tailFill >= size)
                tailFill = 0;
        }

        private synchronized void cutByHalf()
        {
            int i = size / 2;
            if (i <= 0)
                return;
            Buffer abuffer[] = new Buffer[size / 2];
            Buffer abuffer1[] = new Buffer[size / 2];
            int j = totalPkts();
            int k;
            for (k = 0; k < i && k < j; k++)
                abuffer[k] = get();

            j = i - k - (size - j - totalFree());
            headFill = 0;
            tailFill = k;
            for (int l = 0; l <= j; l++)
                abuffer1[l] = new Buffer();

            headFree = 0;
            tailFree = j;
            fill = abuffer;
            free = abuffer1;
            size = i;
        }

        public synchronized void dropFirstPkt()
        {
            // System.out.println("Drop first packet!");
            Buffer buffer = get();
            lastSeqSent = buffer.getSequenceNumber();
            returnFree(buffer);
        }

        public synchronized void dropMpegPkt()
        {
            int i = headFill;
            int j = -1;
            int k = -1;
            while (i != tailFill)
            {
                Buffer buffer = fill[i];
                byte abyte0[] = (byte[]) buffer.getData();
                int l = buffer.getOffset();
                int i1 = abyte0[l + 2] & 7;
                if (i1 > 2)
                {
                    k = i;
                    break;
                }
                if (i1 == 2 && j == -1)
                    j = i;
                if (++i >= size)
                    i = 0;
            }
            if (k == -1)
                i = j != -1 ? j : headFill;
            Buffer buffer1 = fill[i];
            if (i == 0)
                lastSeqSent = buffer1.getSequenceNumber();
            removeAt(i);
        }

        public void dropPkt()
        {
            while (!moreFilled())
                try
                {
                    wait();
                } catch (Exception exception)
                {
                }
            //boris grozev
            if(stats != null)
                stats.update(RTPStats.PDUDROP);
            if (format instanceof AudioFormat)
                dropFirstPkt();
            else if (RTPSourceStream.mpegVideo.matches(format))
                dropMpegPkt();
            else
                dropFirstPkt();
        }

        private synchronized Buffer get()
        {
            Buffer buffer = fill[headFill];
            fill[headFill] = null;
            headFill++;
            if (headFill >= size)
                headFill = 0;
            return buffer;
        }

        public synchronized long getFirstSeq()
        {
            if (!moreFilled())
                return -1L;
            else
                return fill[headFill].getSequenceNumber();
        }

        public synchronized Buffer getFree()
        {
            Buffer buffer = free[headFree];
            free[headFree] = null;
            headFree++;
            if (headFree >= size)
                headFree = 0;
            return buffer;
        }

        public synchronized Buffer getPkt()
        {
            while (!moreFilled())
                try
                {
                    wait();
                } catch (Exception exception)
                {
                }
            return get();
        }

        private synchronized void grow(int i)
        {
            Buffer abuffer[] = new Buffer[i];
            Buffer abuffer1[] = new Buffer[i];
            int j1 = totalPkts();
            int k1 = totalFree();
            int j = headFill;
            for (int l = 0; j != tailFill; l++)
            {
                abuffer[l] = fill[j];
                if (++j >= size)
                    j = 0;
            }

            headFill = 0;
            tailFill = j1;
            fill = abuffer;
            j = headFree;
            for (int i1 = 0; j != tailFree; i1++)
            {
                abuffer1[i1] = free[j];
                if (++j >= size)
                    j = 0;
            }

            headFree = 0;
            tailFree = k1;
            for (int k = i - size; k > 0; k--)
            {
                abuffer1[tailFree] = new Buffer();
                tailFree++;
            }

            free = abuffer1;
            size = i;
        }

        private synchronized void insert(Buffer buffer)
        {
            int i;
            for (i = headFill; i != tailFill;)
            {
                if (fill[i].getSequenceNumber() > buffer.getSequenceNumber())
                    break;
                if (++i >= size)
                    i = 0;
            }

            if (i != tailFill)
            {
                tailFill++;
                if (tailFill >= size)
                    tailFill = 0;
                int k;
                int j = k = tailFill;
                do
                {
                    if (--k < 0)
                        k = size - 1;
                    fill[j] = fill[k];
                    j = k;
                } while (j != i);
                fill[i] = buffer;
            }
        }

        public void monitorQueueSize(Buffer buffer,
                RTPRawReceiver rtprawreceiver)
        {
            sizePerPkt = (sizePerPkt + buffer.getLength()) / 2;
            if (format instanceof VideoFormat)
            {
                if (lastPktSeq + 1L == buffer.getSequenceNumber())
                    pktsEst++;
                else
                    pktsEst = 1;
                lastPktSeq = buffer.getSequenceNumber();
                if (RTPSourceStream.mpegVideo.matches(format))
                {
                    byte abyte0[] = (byte[]) buffer.getData();
                    int k = buffer.getOffset();
                    int k1 = abyte0[k + 2] & 7;
                    if (k1 < 3 && (buffer.getFlags() & 0x800) != 0)
                    {
                        pktsPerFrame = (pktsPerFrame + pktsEst) / 2;
                        pktsEst = 0;
                    }
                    fps = 30;
                    // damencho
                } else if (RTPSourceStream.h264Video.matches(format))
                {
                    pktsPerFrame = 300;// 800;
                    fps = 15;
                }
                if ((buffer.getFlags() & 0x800) != 0)
                {
                    pktsPerFrame = (pktsPerFrame + pktsEst) / 2;
                    pktsEst = 0;
                    framesEst++;
                    long l = System.currentTimeMillis();
                    if (l - lastCheckTime >= 1000L)
                    {
                        lastCheckTime = l;
                        fps = (fps + framesEst) / 2;
                        framesEst = 0;
                        if (fps > 30)
                            fps = 30;
                    }
                }
                int i;
                if (bc != null)
                {
                    i = (int) ((bc.getBufferLength() * fps) / 1000L);
                    if (i <= 0)
                        i = 1;
                    i = pktsPerFrame * i;
                    threshold = (int) (((bc.getMinimumThreshold() * fps) / 1000L) * pktsPerFrame);
                    if (threshold <= i / 2)
                        ;
                    threshold = i / 2;
                } else
                {
                    i = DEFAULT_PKTS_TO_BUFFER;
                }

                // damencho we need bigger buffers fo h264
                if (RTPSourceStream.h264Video.matches(format))
                {
                    maxPktsToBuffer = 200;
                } else
                {
                    if (maxPktsToBuffer > 0)
                        maxPktsToBuffer = (maxPktsToBuffer + i) / 2;
                    else
                        maxPktsToBuffer = i;
                }

                int i1 = totalPkts();
                if (size > MIN_BUF_CHECK && i1 < size / 4)
                {
                    if (!prebuffering
                            && tooMuchBufferingCount++ > pktsPerFrame * fps
                                    * BUF_CHECK_INTERVAL)
                    {
                        cutByHalf();
                        tooMuchBufferingCount = 0;
                    }
                } else if (i1 >= size / 2 && size < maxPktsToBuffer)
                {
                    i = size + size / 2;
                    if (i > maxPktsToBuffer)
                        i = maxPktsToBuffer;
                    grow(i + FUDGE);

                    Log.comment("RTP video buffer size: " + size + " pkts, "
                            + i * sizePerPkt + " bytes.\n");
                    tooMuchBufferingCount = 0;
                } else
                {
                    tooMuchBufferingCount = 0;
                }
                int l1 = (i * sizePerPkt) / 2;
                if (rtprawreceiver != null && l1 > sockBufSize)
                {
                    rtprawreceiver.setRecvBufSize(l1);
                    if (rtprawreceiver.getRecvBufSize() < l1)
                        sockBufSize = 0x7fffffff;
                    else
                        sockBufSize = l1;

                    Log.comment("RTP video socket buffer size: "
                            + rtprawreceiver.getRecvBufSize() + " bytes.\n");
                }
            } else if (format instanceof AudioFormat)
            {
                if (sizePerPkt <= 0)
                    sizePerPkt = DEFAULT_AUD_PKT_SIZE;
                if (bc != null)
                {
                    int j;
                    if (RTPSourceStream.mpegAudio.matches(format))
                        j = sizePerPkt / 4;
                    else
                        j = DEFAULT_MILLISECS_PER_PKT;
                    int j1 = (int) (bc.getBufferLength() / j);
                    threshold = (int) (bc.getMinimumThreshold() / j);
                    if (threshold <= j1 / 2)
                        ;
                    threshold = j1 / 2;
                    if (j1 > size)
                    {
                        grow(j1);
                        Log.comment("RTP audio buffer size: " + size
                                + " pkts, " + j1 * sizePerPkt + " bytes.\n");
                    }
                    int i2 = (j1 * sizePerPkt) / 2;
                    if (rtprawreceiver != null && i2 > sockBufSize)
                    {
                        rtprawreceiver.setRecvBufSize(i2);
                        if (rtprawreceiver.getRecvBufSize() < i2)
                            sockBufSize = 0x7fffffff;
                        else
                            sockBufSize = i2;
                        Log.comment("RTP audio socket buffer size: "
                                + rtprawreceiver.getRecvBufSize() + " bytes.\n");
                    }
                }
            }
        }

        private boolean moreFilled()
        {
            return headFill != tailFill;
        }

        private boolean noMoreFree()
        {
            return headFree == tailFree;
        }

        private synchronized void prepend(Buffer buffer)
        {
            if (headFill == tailFill)
                return;
            headFill--;
            if (headFill < 0)
                headFill = size - 1;
            fill[headFill] = buffer;
        }

        private void removeAt(int i)
        {
            Buffer buffer = fill[i];
            if (i == headFill)
            {
                headFill++;
                if (headFill >= size)
                    headFill = 0;
            } else if (i == tailFill)
            {
                tailFill--;
                if (tailFill < 0)
                    tailFill = size - 1;
            } else
            {
                int j = i;
                do
                {
                    if (--j < 0)
                        j = size - 1;
                    fill[i] = fill[j];
                    i = j;
                } while (i != headFill);
                headFill++;
                if (headFill >= size)
                    headFill = 0;
            }
            returnFree(buffer);
        }

        public synchronized void reset()
        {
            for (; moreFilled(); returnFree(get()))
                ;
            tooMuchBufferingCount = 0;
            notifyAll();
        }

        private synchronized void returnFree(Buffer buffer)
        {
            free[tailFree] = buffer;
            tailFree++;
            if (tailFree >= size)
                tailFree = 0;
        }

        public int totalFree()
        {
            return tailFree < headFree ? size - (headFree - tailFree)
                    : tailFree - headFree;
        }

        public int totalPkts()
        {
            return tailFill < headFill ? size - (headFill - tailFill)
                    : tailFill - headFill;
        }
    }

    private DataSource dsource;

    private Format format;

    BufferTransferHandler handler;

    boolean started;

    boolean killed;

    boolean replenish;

    PktQue pktQ;

    Object startReq;

    private RTPMediaThread thread;

    private boolean hasRead;

    private int DEFAULT_AUDIO_RATE;

    private int DEFAULT_VIDEO_RATE;

    private BufferControlImpl bc;

    private long lastSeqRecv;

    private long lastSeqSent;

    private static final int NOT_SPECIFIED = -1;

    private BufferListener listener;

    private int threshold;

    private boolean prebuffering;

    private boolean prebufferNotice;

    private boolean bufferWhenStopped;
    static AudioFormat mpegAudio = new AudioFormat("mpegaudio/rtp");
    static VideoFormat mpegVideo = new VideoFormat("mpeg/rtp");
    // damencho
    static VideoFormat h264Video = new VideoFormat("h264/rtp");

    //boris grozev: log discarded packets here
    private RTPStats stats = null;

    public RTPSourceStream(DataSource datasource)
    {
        format = null;
        handler = null;
        started = false;
        killed = false;
        replenish = true;
        startReq = new Object();
        thread = null;
        hasRead = false;
        DEFAULT_AUDIO_RATE = 8000;
        DEFAULT_VIDEO_RATE = 15;
        bc = null;
        lastSeqRecv = -1L;
        lastSeqSent = -1L;
        listener = null;
        threshold = 0;
        prebuffering = false;
        prebufferNotice = false;
        bufferWhenStopped = true;
        dsource = datasource;
        datasource.setSourceStream(this);
        pktQ = new PktQue(4);
        createThread();
    }

    public void add(Buffer buffer, boolean flag, RTPRawReceiver rtprawreceiver)
    {
        if (!started && !bufferWhenStopped)
            return;

        if (lastSeqRecv - buffer.getSequenceNumber() > 256L)
            pktQ.reset();
        lastSeqRecv = buffer.getSequenceNumber();
        boolean flag1 = false;
        synchronized (pktQ)
        {
            pktQ.monitorQueueSize(buffer, rtprawreceiver);
            if (pktQ.noMoreFree())
            {
                long l = pktQ.getFirstSeq();
                if (l != -1L && buffer.getSequenceNumber() < l)
                    return;
                pktQ.dropPkt();
            }
        }
        if (pktQ.totalFree() <= 1)
            flag1 = true;
        Buffer buffer1 = pktQ.getFree();

        byte abyte0[] = (byte[]) buffer.getData();
        byte abyte1[] = (byte[]) buffer1.getData();
        if (abyte1 == null || abyte1.length < abyte0.length)
            abyte1 = new byte[abyte0.length];
        System.arraycopy(abyte0, buffer.getOffset(), abyte1,
                buffer.getOffset(), buffer.getLength());
        buffer1.copy(buffer);
        buffer1.setData(abyte1);
        if (flag1)
            buffer1.setFlags(buffer1.getFlags() | 0x2000 | 0x20);
        else
            buffer1.setFlags(buffer1.getFlags() | 0x20);

        pktQ.addPkt(buffer1);
        synchronized (pktQ)
        {
            if (started && prebufferNotice && listener != null
                    && pktQ.totalPkts() >= threshold)
            {
                listener.minThresholdReached(dsource);
                prebufferNotice = false;
                prebuffering = false;
                synchronized (startReq)
                {
                    startReq.notifyAll();
                }
            }
            if (replenish && (format instanceof AudioFormat))
            {
                if (pktQ.totalPkts() >= pktQ.size / 2)
                {
                    replenish = false;
                    pktQ.notifyAll();
                }
            } else
            {
                pktQ.notifyAll();
            }
        }
    }

    public void close()
    {
        if (killed)
            return;
        stop();
        killed = true;
        synchronized (startReq)
        {
            startReq.notifyAll();
        }
        synchronized (pktQ)
        {
            pktQ.notifyAll();
        }
        thread = null;
        if (bc != null)
            bc.removeSourceStream(this);
    }

    public void connect()
    {
        killed = false;
        createThread();
    }

    private void createThread()
    {
        if (thread != null)
            return;
        thread = new RTPMediaThread(this, "RTPStream");
        thread.useControlPriority();
        thread.start();
    }

    public Format getFormat()
    {
        return format;
    }

    public void prebuffer()
    {
        synchronized (pktQ)
        {
            prebuffering = true;
            prebufferNotice = true;
        }
    }

    public void read(Buffer buffer)
    {
        if (pktQ.totalPkts() == 0)
        {
            buffer.setDiscard(true);
            return;
        }
        Buffer buffer1 = pktQ.getPkt();
        lastSeqSent = buffer1.getSequenceNumber();
        Object obj = buffer.getData();
        Object obj1 = buffer.getHeader();
        buffer.copy(buffer1);
        buffer1.setData(obj);
        buffer1.setHeader(obj1);
        pktQ.returnFree(buffer1);
        synchronized (pktQ)
        {
            hasRead = true;
            if (format instanceof AudioFormat)
            {
                if (pktQ.totalPkts() > 0)
                    pktQ.notifyAll();
                else
                    replenish = true;
            } else
            {
                pktQ.notifyAll();
            }
        }
    }

    public void reset()
    {
        pktQ.reset();
        lastSeqSent = -1L;
    }

    public void run()
    {
        while (true)
            try
            {
                synchronized (startReq)
                {
                    while ((!started || prebuffering) && !killed)
                        startReq.wait();
                }
                synchronized (pktQ)
                {
                    do
                    {
                        if (!hasRead && !killed)
                            pktQ.wait();
                        hasRead = false;
                    } while (pktQ.totalPkts() <= 0 && !killed);
                }
                if (killed)
                    break;
                if (handler != null)
                    handler.transferData(this);
            } catch (InterruptedException interruptedexception)
            {
                Log.error("Thread " + interruptedexception.getMessage());
            }
    }

    public void setBufferControl(BufferControl buffercontrol)
    {
        bc = (BufferControlImpl) buffercontrol;
        updateBuffer(bc.getBufferLength());
        updateThreshold(bc.getMinimumThreshold());
    }

    public void setBufferListener(BufferListener bufferlistener)
    {
        listener = bufferlistener;
    }

    public void setBufferWhenStopped(boolean flag)
    {
        bufferWhenStopped = flag;
    }

    void setContentDescriptor(String s)
    {
        super.contentDescriptor = new ContentDescriptor(s);
    }

    protected void setFormat(Format format1)
    {
        format = format1;
    }

    public void setTransferHandler(BufferTransferHandler buffertransferhandler)
    {
        handler = buffertransferhandler;
    }

    public void start()
    {
        synchronized (startReq)
        {
            started = true;
            startReq.notifyAll();
        }
    }

    public void stop()
    {
        synchronized (startReq)
        {
            started = false;
            prebuffering = false;
            if (!bufferWhenStopped)
                reset();
        }
    }

    public long updateBuffer(long l)
    {
        return l;
    }

    public long updateThreshold(long l)
    {
        return l;
    }

    public void setStats(RTPStats rtpStats)
    {
        stats = rtpStats;
    }
}
