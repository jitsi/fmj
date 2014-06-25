package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.*;

/**
 * Implements <tt>JitterBufferBehaviour</tt> for video media data. It realizes
 * a jitter buffer which is adaptive.
 *
 * @author Lyubomir Marinov
 */
class VideoJitterBufferBehaviour
    extends BasicJitterBufferBehaviour
{
    private static final int BUF_CHECK_INTERVAL = 7000;

    // damencho: The original value was 30 and we increased it.
    /**
     * The default number of RTP packets to buffer in the case of video.
     */
    private static final int DEFAULT_PKTS_TO_BUFFER = 90;

    private static final int DEFAULT_VIDEO_RATE = 15;

    private final static int FUDGE = 5;

    // damencho
    private static final VideoFormat H264 = new VideoFormat("h264/rtp");

    private static final int MIN_BUF_CHECK = 10000;

    private static final VideoFormat MPEG = new VideoFormat("mpeg/rtp");

    private int fps = 15;

    private int framesEst = 0;

    private long lastCheckTime = 0L;

    private long lastPktSeq = 0L;

    private int maxPktsToBuffer = 0;

    private int pktsEst;

    private int pktsPerFrame = DEFAULT_VIDEO_RATE;

    private int tooMuchBufferingCount = 0;

    private final int MIN_SIZE;

    /**
     * Initializes a new <tt>VideoJitterBufferBehaviour</tt> instance for the
     * purposes of a specific <tt>RTPSourceStream</tt>.
     *
     * @param stream the <tt>RTPSourceStream</tt> which has requested the
     * initialization of the new instance
     */
    public VideoJitterBufferBehaviour(RTPSourceStream stream)
    {
        super(stream);

        MIN_SIZE
                = com.sun.media.util.Registry.getInt(
                "video_jitter_buffer_MIN_SIZE",
                4);

        if (q.getCapacity() < MIN_SIZE)
            grow(MIN_SIZE);
    }

    private void cutByHalf()
    {
        int capacity = q.getCapacity() / 2;

        if (capacity > 0)
            q.setCapacity(capacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dropFirstPkt()
    {
        if (MPEG.matches(stream.getFormat()))
            dropMpegPkt();
        else
            super.dropFirstPkt();
    }

    /**
     * Removes an element from the queue and releases it to be reused. The
     * element is chosen in a way specific to MPEG.
     */
    private void dropMpegPkt()
    {
        int i = 0;
        int j = -1;
        int k = -1;
        for (int count = q.getFillCount(); i < count; i++)
        {
            Buffer buffer = q.getFill(i);
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
        }
        if (k == -1)
            i = j != -1 ? j : 0;
        q.dropFill(i);
    }

    /**
     * {@inheritDoc}
     *
     * <tt>VideoJitterBufferBehaviour</tt> always returns <tt>true</tt> to
     * indicate that it implements an adaptive jitter buffer.
     */
    @Override
    public boolean isAdaptive()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int monitorQSize(Buffer buffer)
    {
        super.monitorQSize(buffer);

        if (lastPktSeq + 1L == buffer.getSequenceNumber())
            pktsEst++;
        else
            pktsEst = 1;
        lastPktSeq = buffer.getSequenceNumber();
        Format format = stream.getFormat();
        if (MPEG.matches(format))
        {
            byte abyte0[] = (byte[]) buffer.getData();
            int k = buffer.getOffset();
            int k1 = abyte0[k + 2] & 7;
            if (k1 < 3 && (buffer.getFlags() & Buffer.FLAG_RTP_MARKER) != 0)
            {
                pktsPerFrame = (pktsPerFrame + pktsEst) / 2;
                pktsEst = 0;
            }
            fps = 30;
            // damencho
        } else if (H264.matches(format))
        {
            pktsPerFrame = 300;// 800;
            fps = 15;
        }
        if ((buffer.getFlags() & Buffer.FLAG_RTP_MARKER) != 0)
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
        BufferControl bc = getBufferControl();
        int aprxBufferLengthInPkts;
        if (bc != null)
        {
            aprxBufferLengthInPkts = (int) ((bc.getBufferLength() * fps) / 1000L);
            if (aprxBufferLengthInPkts <= 0)
                aprxBufferLengthInPkts = 1;
            aprxBufferLengthInPkts = pktsPerFrame * aprxBufferLengthInPkts;
        } else
        {
            aprxBufferLengthInPkts = DEFAULT_PKTS_TO_BUFFER;
        }

        // damencho: We need bigger buffers for H.264.
        if (H264.matches(format))
        {
            maxPktsToBuffer = 200;
        } else
        {
            if (maxPktsToBuffer > 0)
                maxPktsToBuffer = (maxPktsToBuffer + aprxBufferLengthInPkts) / 2;
            else
                maxPktsToBuffer = aprxBufferLengthInPkts;
        }

        int size = q.getCapacity();
        int i1 = q.getFillCount();
        if (size > MIN_BUF_CHECK && i1 < size / 4)
        {
            if (tooMuchBufferingCount++
                    > pktsPerFrame * fps * BUF_CHECK_INTERVAL)
            {
                cutByHalf();
                tooMuchBufferingCount = 0;
            }
        }
        else if (i1 >= size / 2 && size < maxPktsToBuffer)
        {
            aprxBufferLengthInPkts = size + size / 2;
            if (aprxBufferLengthInPkts > maxPktsToBuffer)
                aprxBufferLengthInPkts = maxPktsToBuffer;
            q.setCapacity(aprxBufferLengthInPkts + FUDGE);
            size = q.getCapacity();
            Log.comment(
                    "RTP video buffer size: " + size + " pkts, "
                        + aprxBufferLengthInPkts * stats.getSizePerPacket()
                        + " bytes.\n");
            tooMuchBufferingCount = 0;
        }
        else
            tooMuchBufferingCount = 0;
        return aprxBufferLengthInPkts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        super.reset();

        tooMuchBufferingCount = 0;
    }
}
