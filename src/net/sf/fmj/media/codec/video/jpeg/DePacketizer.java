package net.sf.fmj.media.codec.video.jpeg;

import java.awt.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;

import javax.media.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.utility.*;

import com.lti.utils.*;

/**
 * JPEG/RTP depacketizer Codec. FMJ's functional equivalent of
 * com.sun.media.codec.video.jpeg.DePacketizer. Reassembles JPEG RTP packets
 * into JPEG frames, as per RFC 2035 - RTP Payload Format for JPEG Video. See
 * http://www.rfc-archive.org/getrfc.php?rfc=2035 TODO: support restart markers
 * TODO: support q table headers TODO: lunarphases.mov: when received, JMF puts
 * extra stuff on the end that we don't.
 *
 * @author Ken Larson
 * @author Martin Harvan
 */
public class DePacketizer extends AbstractCodec implements Codec
{
    /**
     * Compares buffers by the fragment offset. Assumes buffers have enough data
     * in them for a JpegRTPHeader.
     *
     * @author Ken Larson
     */
    private static class BufferFragmentOffsetComparator
        implements Comparator<Buffer>
    {
        public int compare(Buffer a, Buffer b)
        {
            if (a == null && b == null)
                return 0;
            if (a == null) // then a < b, return -1
                return -1;
            if (b == null)
                return 1; // a > b

            final JpegRTPHeader jpegRtpHeaderA = JpegRTPHeader.parse(
                    (byte[]) a.getData(), a.getOffset());
            final JpegRTPHeader jpegRtpHeaderB = JpegRTPHeader.parse(
                    (byte[]) b.getData(), b.getOffset());

            return jpegRtpHeaderA.getFragmentOffset()
                    - jpegRtpHeaderB.getFragmentOffset();

        }
    }

    /**
     * Used to assemble fragments with the same timestamp into a single frame.
     *
     * @author Ken Larson
     */
    static class FrameAssembler
    {
        private final List<Buffer> list = new ArrayList<Buffer>();
        private boolean rtpMarker; // have we received the RTP marker that
                                   // signifies the end of a frame?

        /**
         * Is the frame complete?
         */
        public boolean complete()
        {
            if (!rtpMarker)
                return false; // need an rtp marker to signify end

            if (list.size() <= 0)
                return false; // need at least one fragments with data beyond
                              // the header

            if (!contiguous())
                return false; // missing fragments. TODO: theoretically we could
                              // display a degraded image, but for now we'll
                              // only display complete ones.

            // TODO: if some of the last ones come in after the marker, we'll
            // see blank squares in the lower right.
            return true;
        }

        /**
         * @return false if any fragments are missing. Does not detect fragments
         *         missing at the end.
         */
        private boolean contiguous()
        {
            int expect = 0; // next expected offset.

            for (Buffer b : list)
            {
                final JpegRTPHeader jpegRtpHeader = parseJpegRTPHeader(b);
                int otherOffset = 0;
                if (jpegRtpHeader.getType() > 63)
                    otherOffset += 4;
                if (jpegRtpHeader.getQ() >= 128)
                {
                    int length = 0;
                    int j = b.getOffset() + JpegRTPHeader.HEADER_SIZE
                            + otherOffset + 2; // the size of the offset to the
                                               // length of custom tables
                    byte[] data = (byte[]) b.getData();
                    length = data[j++] & 0xFF;
                    length <<= 8;
                    length |= data[j] & 0xFF;
                    length += 4; // add 4 bytes that are not included in the
                                 // length of custom qtables
                    otherOffset += length;
                }
                if (jpegRtpHeader.getFragmentOffset() != expect)
                    return false;
                expect += b.getLength() - JpegRTPHeader.HEADER_SIZE
                        - otherOffset;

            }
            return true;
        }

        /**
         * Assumes that complete() has been called and returns true.
         */
        public int copyToBuffer(Buffer bDest)
        {
            if (!rtpMarker)
                throw new IllegalStateException();
            if (list.size() <= 0)
                throw new IllegalStateException();

            // TODO: perhaps what we should do is copy the header if there is
            // not one.
            // The test samples coming from JMStudio had headers in them, so the
            // JPEG could not be
            // parsed if (another) header was prepended.
            final Buffer bFirst = list.get(0);
            final boolean prependHeader = !hasJPEGHeaders(
                    (byte[]) bFirst.getData(), bFirst.getOffset()
                            + JpegRTPHeader.HEADER_SIZE, bFirst.getLength()
                            - JpegRTPHeader.HEADER_SIZE);
            final int MAX_HEADER = prependHeader ? 1024 : 0; // TODO: what is
                                                             // actual max size
                                                             // for the header?
                                                             // Seems to be
                                                             // fixed size,
                                                             // 600-700 bytes.
            final int MAX_TRAILER = 2;
            final int frameLength = frameLength();
            final byte[] data;
            int inputOffset = bFirst.getOffset();
            int dri = 0;
            byte[] lqt = null;
            byte[] cqt = null;
            byte[] inputData = (byte[]) bFirst.getData();
            if (bDest.getData() != null
                    && ((byte[]) bDest.getData()).length >= (frameLength
                            + MAX_HEADER + MAX_TRAILER))
            {
                data = (byte[]) bDest.getData(); // reuse existing byte array
                // zero out:
                zeroData(data);

            } else
            {
                data = new byte[frameLength + MAX_HEADER + MAX_TRAILER]; // allocate
                                                                         // new
                                                                         // one
                                                                         // -
                                                                         // existing
                                                                         // one
                                                                         // not
                                                                         // there
                                                                         // or
                                                                         // too
                                                                         // short
            }
            // System.out.println(Arrays.toString(inputData));
            // System.out.println("Offset:"+inputOffset);

            /*
             * System.out.println("InputData"); for (byte b:inputData){ String s
             * = Integer.toHexString(b&0xFF);
             * System.out.print((s.length()<2?"0"+s:s)+"-"); }
             * System.out.println("");
             */

            int offsetAfterHeaders = 0;
            if (prependHeader)
            {
                // put initial SOI marker manually, we tell RFC2035.MakeHeaders
                // not to do it:
                data[offsetAfterHeaders++] = (byte) 0xff;
                data[offsetAfterHeaders++] = (byte) 0xd8;

                // part of the header with "JFIF" in it, not generated by the
                // code in RFC2035.
                offsetAfterHeaders = buildJFIFHeader(data, offsetAfterHeaders);

                final JpegRTPHeader jpegRtpHeaderFirst = parseJpegRTPHeader(bFirst);
                inputOffset += JpegRTPHeader.HEADER_SIZE;

                if (jpegRtpHeaderFirst.getType() >= 64
                        && jpegRtpHeaderFirst.getType() <= 127)
                { // For types 64-127 there has to be restart header

                    dri = inputData[inputOffset++] & 0xFF;
                    dri <<= 8;
                    dri |= inputData[inputOffset++] & 0xFF;
                    inputOffset += 2; // skip rest of the Restart Marker header
                                      // (2 bytes)
                }
                if (jpegRtpHeaderFirst.getQ() > 127)
                {
                    inputOffset += 2; // skip MBZ and Precision fields
                    int length = inputData[inputOffset++] & 0xFF;
                    length <<= 8;
                    length |= inputData[inputOffset++] & 0xFF;
                    // Most usual qtable size is 64 bytes, so length is usually
                    // 128, for other sizes we would put half to lqt and second
                    // half to cqt..

                    // lqt = Arrays.copyOfRange(inputData, inputOffset,
                    // inputOffset + length / 2); //Java6 only code
                    lqt = ArrayUtility.copyOfRange(inputData, inputOffset,
                            inputOffset + length / 2);
                    inputOffset += length / 2; // TODO what if the length is
                                               // odd?
                    // (It should not happen though)
                    // cqt = Arrays.copyOfRange(inputData, inputOffset,
                    // inputOffset+ length / 2); //Java6 only code
                    cqt = ArrayUtility.copyOfRange(inputData, inputOffset,
                            inputOffset + length / 2);
                    inputOffset += length / 2;
                }
                // TODO add special handling if q>128, but first we should
                // handle Reset Header
                offsetAfterHeaders = RFC2035.MakeHeaders(false, data,
                        offsetAfterHeaders, jpegRtpHeaderFirst.getType(),
                        jpegRtpHeaderFirst.getQ(),
                        jpegRtpHeaderFirst.getWidthInBlocks(),
                        jpegRtpHeaderFirst.getHeightInBlocks(), lqt, cqt, dri);

                /*
                 * System.out.println("OutputData"); for (byte b:data){ String s
                 * = Integer.toHexString(b&0xFF);
                 * System.out.print((s.length()<2?"0"+s:s)+"-"); }
                 * System.out.println("");
                 */
            }
            if (TRACE)
                System.out.println("offsetAfterHeaders=" + offsetAfterHeaders);
            for (Buffer b : list)
            {
                final JpegRTPHeader jpegRtpHeader = parseJpegRTPHeader(b);

                // if (TRACE) System.out.println("Copying, length=" +
                // (b.getLength() - JpegRTPHeader.HEADER_SIZE) + ":");
                // if (TRACE) System.out.println(dump((byte[]) b.getData(),
                // b.getOffset() + JpegRTPHeader.HEADER_SIZE, (b.getLength() -
                // JpegRTPHeader.HEADER_SIZE) > MAX_DUMP_SIZE ? MAX_DUMP_SIZE :
                // (b.getLength() - JpegRTPHeader.HEADER_SIZE)));
                // if (TRACE) System.out.println("End copying.");

                System.arraycopy(b.getData(), b.getOffset()
                        + JpegRTPHeader.HEADER_SIZE, data, offsetAfterHeaders
                        + jpegRtpHeader.getFragmentOffset(), b.getLength()
                        - JpegRTPHeader.HEADER_SIZE);
            }

            final boolean appendEOI = !hasJPEGTrailer(data, offsetAfterHeaders
                    + frameLength, MAX_TRAILER); // no need to append if it is
                                                 // already there.
            int trailing = 0;
            if (appendEOI)
            {
                data[offsetAfterHeaders + frameLength + trailing++] = (byte) 0xff;
                data[offsetAfterHeaders + frameLength + trailing++] = (byte) 0xd9;
            }

            bDest.setData(data);
            bDest.setLength(offsetAfterHeaders + frameLength + trailing);
            bDest.setOffset(0);
            bDest.setRtpTimeStamp(bFirst.getRtpTimeStamp());
            bDest.setTimeStamp(bFirst.getTimeStamp()); // TODO: is the source
                                                       // buffer timestamp in
                                                       // same units?
            return offsetAfterHeaders;
        }

        /**
         * Total length of all fragments. Does not include JPEG header. Assumes
         * that complete() has been called and returns true.
         */
        public int frameLength()
        {
            if (!rtpMarker)
                throw new IllegalStateException();
            if (list.size() <= 0)
                throw new IllegalStateException();

            // calculate from offset and length of last buffer:
            final Buffer b = list.get(list.size() - 1);
            final JpegRTPHeader jpegRtpHeader = parseJpegRTPHeader(b);
            // Observed: the frame with the marker has valid offset,
            return jpegRtpHeader.getFragmentOffset() + b.getLength()
                    - JpegRTPHeader.HEADER_SIZE;

        }

        /**
         * Convenience method.
         */
        private JpegRTPHeader parseJpegRTPHeader(Buffer b)
        {
            return JpegRTPHeader.parse((byte[]) b.getData(), b.getOffset());
        }

        /**
         * Add the buffer (which contains a fragment) to the assembler. Should
         * be a clone of a real buffer, since the buffer will be kept around.
         *
         * @param buffer
         *            clone of real buffer
         */
        public void put(Buffer buffer)
        {
            if (!rtpMarker)
            {
                rtpMarker = (buffer.getFlags() & Buffer.FLAG_RTP_MARKER) != 0;
            }
            if (buffer.getLength() <= JpegRTPHeader.HEADER_SIZE)
                return; // no actual data in buffer, no need to keep. Typically
                        // happens when RTP marker is set.

            // TODO: interestingly, when JMF sends the RTP marker, it occurs in
            // a fragment with no data - not even
            // a header. However, looking at the buffer, the header is there,
            // but buffer.getLength() returns zero.
            // the header has the correct offset of the "end" of the frame. This
            // would be useful since we can then
            // determine whether we have missing trailing fragments.
            if (TRACE)
                System.out.println("adding buffer seq="
                        + buffer.getSequenceNumber() + " ts="
                        + buffer.getTimeStamp());

            list.add(buffer);
            Collections.sort(list, bufferFragmentOffsetComparator); // TODO:
                                                                    // incremental
                                                                    // sort, or
                                                                    // bubble
                                                                    // sort -
                                                                    // since the
                                                                    // list is
                                                                    // probably
                                                                    // already
                                                                    // sorted.
        }
    }

    /**
     * Keeps track of multiple FrameAssemblers for different timestamps. This is
     * needed because packets may arrive out of order.
     *
     * @author Ken Larson
     */
    private static class FrameAssemblerCollection
    {
        private Map<Long /* timestamp */, FrameAssembler> frameAssemblers
            = new HashMap<Long, FrameAssembler>();

        public void clear()
        {
            frameAssemblers.clear();
        }

        public FrameAssembler findOrAdd(long timestamp)
        {
            if (TRACE)
                System.out.println("========== " + frameAssemblers.size());
            Long timestampObj = Long.valueOf(timestamp);
            FrameAssembler result = frameAssemblers.get(timestampObj);
            if (result == null)
            {
                result = new FrameAssembler();
                frameAssemblers.put(timestampObj, result);
            }
            return result;
        }

        public long getOldestTimestamp()
        {
            long oldestSoFar = -1;

            final Iterator<Long> i = frameAssemblers.keySet().iterator();
            while (i.hasNext())
            {
                final Long ts = i.next();
                if (oldestSoFar < 0 || ts.longValue() < oldestSoFar)
                    oldestSoFar = ts.longValue();
            }
            return oldestSoFar;

        }

        public void remove(long timestamp)
        {
            frameAssemblers.remove(Long.valueOf(timestamp));
        }

        public void removeAllButNewestN(int n)
        {
            while (frameAssemblers.size() > n)
            {
                final long oldestTimestamp = getOldestTimestamp();
                if (oldestTimestamp < 0)
                    throw new RuntimeException();
                Long key = Long.valueOf(oldestTimestamp);
                FrameAssembler a = frameAssemblers.get(key);
                String completeIncomplete = a.complete() ? "complete"
                        : "incomplete";
                if (TRACE)
                {
                    System.out.println("Discarding " + completeIncomplete
                            + " frame (not in newest " + n + ", ts="
                            + oldestTimestamp);
                }
                frameAssemblers.remove(key);
            }

        }

        public void removeOlderThan(long timestamp)
        {
            final Iterator<Entry<Long, FrameAssembler>> i
                = frameAssemblers.entrySet().iterator();
            while (i.hasNext())
            {
                final Entry<Long, FrameAssembler> e = i.next();
                final Long entryTimestamp = e.getKey();
                if (entryTimestamp.longValue() < timestamp)
                {
                    if (TRACE)
                    {
                        System.out
                                .println("Discarding incomplete frame older than "
                                        + timestamp + ", ts=" + entryTimestamp);
                    }
                    i.remove();
                }
            }

        }
    }

    private static final boolean COMPARE_WITH_BASELINE = false;

    private static final boolean TRACE = false;
    private static final boolean EXIT_AFTER_ONE_FRAME = false; // for testing
                                                               // only.

    private static final int MAX_ACTIVE_FRAME_ASSEMBLERS = 3;

    private final Format[] supportedInputFormats = new Format[] { new VideoFormat(
            VideoFormat.JPEG_RTP, null, -1, Format.byteArray, -1.0f), };

    private final Format[] supportedOutputFormats = new Format[] { new JPEGFormat(), };

    private Codec baselineCodec; // when debugging/testing, we can set this to
                                 // an instance of
                                 // com.sun.media.codec.video.jpeg.DePacketizer
                                 // and compare the results.

    private static final BufferFragmentOffsetComparator bufferFragmentOffsetComparator = new BufferFragmentOffsetComparator();

    private static final int MAX_DUMP_SIZE = 200000;

    //
    /**
     * info on JFIF header at
     * http://www.obrador.com/essentialjpeg/headerinfo.htm
     *
     * @return new offset
     */
    private static int buildJFIFHeader(byte[] data, int offset)
    {
        // example:
        // ffe000104a46494600010100000100010000

        // JFIF marker (0xFFE0)
        data[offset++] = (byte) 0xff;
        data[offset++] = (byte) 0xe0;

        // * length -- two bytes
        data[offset++] = (byte) 0x00;
        data[offset++] = (byte) 0x10;

        // * identifier -- five bytes: 4A, 46, 49, 46, 00 (the ASCII code
        // equivalent of a zero terminated "JFIF" string)
        data[offset++] = (byte) 0x4a;
        data[offset++] = (byte) 0x46;
        data[offset++] = (byte) 0x49;
        data[offset++] = (byte) 0x46;
        data[offset++] = (byte) 0x00;

        // * version -- two bytes: often 01, 02
        // o the most significant byte is used for major revisions
        // o the least significant byte for minor revisions
        //
        data[offset++] = (byte) 0x01;
        data[offset++] = (byte) 0x01;

        // * units -- one byte: Units for the X and Y densities
        // o 0 => no units, X and Y specify the pixel aspect ratio
        // o 1 => X and Y are dots per inch
        // o 2 => X and Y are dots per cm
        data[offset++] = (byte) 0x00;

        // * Xdensity -- two bytes
        data[offset++] = (byte) 0x00;
        data[offset++] = (byte) 0x01;

        // * Ydensity -- two bytes
        data[offset++] = (byte) 0x00;
        data[offset++] = (byte) 0x01;

        // * Xthumbnail -- one byte: 0 = no thumbnail
        data[offset++] = (byte) 0x00;

        // * Ythumbnail -- one byte: 0 = no thumbnail
        data[offset++] = (byte) 0x00;

        return offset;
    }

    /**
     * Debugging only. Dumps as hex to std out.
     */
    private static void dump(Buffer b, String name)
    {
        if (TRACE)
            System.out.println(name + ", length=" + b.getLength() + " :");
        if (TRACE)
            System.out.println(dump(
                    (byte[]) b.getData(),
                    b.getOffset(),
                    b.getLength() > MAX_DUMP_SIZE ? MAX_DUMP_SIZE : b
                            .getLength()));

    }

    /**
     * Debugging only. In this version, len is the length to dump to the string,
     * not the length minus offset as in StringUtils. Returns the string instead
     * of pringing to std out.
     */
    private static String dump(byte[] data, int offset, int len)
    {
        return StringUtils.dump(data, offset, offset + len);

    }

    /**
     * Checks to see if the data begins with SOI. Refers to JPEG headers, not
     * JPEGRTP headers.
     */
    private static boolean hasJPEGHeaders(byte[] data, int offset, int len)
    {
        if (len < 2)
            throw new IllegalArgumentException();
        if (data[offset++] != (byte) 0xff)
            return false;
        if (data[offset++] != (byte) 0xd8)
            return false;

        return true; // starts with SOI
    }

    private static boolean hasJPEGTrailer(byte[] data, int offset, int len)
    {
        if (len < 2)
            throw new IllegalArgumentException();
        if (data[offset++] != (byte) 0xff)
            return false;
        if (data[offset++] != (byte) 0xd9)
            return false;

        return true; // ends with EOI
    }

    private static void zeroData(byte[] data)
    {
        int len = data.length;
        for (int i = 0; i < len; ++i)
        {
            data[i] = 0;
        }
    }

    private long lastRTPtimestamp = -1;
    private long lastTimestamp;

    private final FrameAssemblerCollection frameAssemblers = new FrameAssemblerCollection();

    /**
     * Because packets can come out of order, it is possible that some packets
     * for a newer frame may arrive while an older frame is still incomplete.
     * However, in the case where we get nothing but incomplete frames, we don't
     * want to keep all of them around forever.
     */

    public DePacketizer()
    {
        if (COMPARE_WITH_BASELINE)
        {
            try
            {
                baselineCodec = (Codec) Class.forName(
                        "com.sun.media.codec.video.jpeg.DePacketizer")
                        .newInstance();
            } catch (Exception e)
            {
                System.out
                        .println("Unable to instantiate com.sun.media.codec.video.jpeg.DePacketizer"); // will
                                                                                                       // happen
                                                                                                       // if
                                                                                                       // JMF
                                                                                                       // not
                                                                                                       // in
                                                                                                       // classpath.
            }
        }
    }

    @Override
    public void close()
    {
        if (baselineCodec != null)
            baselineCodec.close();

        super.close();

        frameAssemblers.clear();

    }

    @Override
    public Object getControl(String controlType)
    {
        if (baselineCodec != null)
        {
            return baselineCodec.getControl(controlType);
        } else
        {
            return super.getControl(controlType);
        }
    }

    @Override
    public Object[] getControls()
    {
        if (baselineCodec != null)
            return baselineCodec.getControls();
        else
            return super.getControls();
    }

    @Override
    public String getName()
    {
        return "JPEG DePacketizer";
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return supportedOutputFormats;
        VideoFormat inputCast = (VideoFormat) input;
        final Dimension HARD_CODED_SIZE = new java.awt.Dimension(320, 240);
        final Format[] result = new Format[] { new JPEGFormat(
                inputCast.getSize() != null ? inputCast.getSize()
                        : HARD_CODED_SIZE, -1, Format.byteArray, -1.0f, -1, -1) };

        if (baselineCodec != null)
        {
            final Format[] baselineResult = baselineCodec
                    .getSupportedOutputFormats(input);
            System.out.println("input:  "
                    + LoggingStringUtils.formatToStr(input));
            for (int i = 0; i < baselineResult.length; ++i)
                System.out.println("output: "
                        + LoggingStringUtils.formatToStr(baselineResult[0]));
        }
        // TODO: JMF returns a format with dimensions of 320x240 - not sure
        // where this comes from,
        // seems like it might be hard-coded. We'll do the same, otherwise we
        // can get exceptions
        // downstream.

        // mgodehardt: RTPVideoStream is now fetching the Dimension from the RTP
        // stream, if this fails, the 320 x 240
        // is a fallback

        return result;
    }

    @Override
    public void open() throws ResourceUnavailableException
    {
        if (baselineCodec != null)
            baselineCodec.open();
        super.open();
    }

    @Override
    public int process(Buffer input, Buffer output)
    {
        // TODO: check/propagate EOM

        // Flags to look out for:
        // public static final int FLAG_RTP_MARKER = 2048;
        // public static final int FLAG_RTP_TIME = 4096; // TODO: what does this
        // mean?

        if (!input.isDiscard())
        {
            if (baselineCodec != null)
            {
                final int baselineResult = baselineCodec.process(input, output);
                // if (TRACE) System.out.println("result=" + baselineResult +
                // " output.getFlags()=" +
                // Integer.toHexString(output.getFlags()) +
                // " output.getLength()=" + output.getLength());
            }

            final JpegRTPHeader jpegRtpHeader = input.getLength() >= JpegRTPHeader.HEADER_SIZE ? JpegRTPHeader
                    .parse((byte[]) input.getData(), input.getOffset()) : null;
            final long timestamp = input.getTimeStamp();
            final boolean rtpMarker = (input.getFlags() & Buffer.FLAG_RTP_MARKER) != 0;
            // if (TRACE)
            // System.out.println("ts=" + input.getTimeStamp() + " flags=" +
            // Integer.toHexString(input.getFlags()) + " offset=" +
            // input.getOffset() + " length=" + input.getLength() +
            // " jpegRtpHeader: " + jpegRtpHeader);

            FrameAssembler assembler = frameAssemblers.findOrAdd(timestamp);

            // mgodehardt: input buffer is reused everytime, so we must clone
            // him
            assembler.put((Buffer) input.clone());
            // /dump(input, "Input");

            if (assembler.complete())
            {
                Buffer bComplete = baselineCodec == null ? output
                        : new Buffer();
                final int offsetAfterHeaders = assembler
                        .copyToBuffer(bComplete);

                frameAssemblers.remove(timestamp);
                frameAssemblers.removeOlderThan(timestamp); // we have a
                                                            // complete frame,
                                                            // so any earlier
                                                            // fragments are not
                                                            // needed, as they
                                                            // are for older
                                                            // (incomplete)
                                                            // frames.

                if (TRACE)
                {
                    System.out
                            .println("COMPLETE: ts=" + timestamp
                                    + " bComplete.getLength()="
                                    + bComplete.getLength());
                }

                if (lastRTPtimestamp == -1)
                {
                    lastRTPtimestamp = input.getTimeStamp();
                    lastTimestamp = System.nanoTime();
                }
                if (TRACE)
                    System.out.println("### "
                            + ((System.nanoTime() - lastTimestamp) / 1000000L)
                            + " "
                            + ((input.getTimeStamp() - lastRTPtimestamp) / 90));

                // /dump(bComplete, "bComplete");
                // /dump(output, "output");

                // TODO: the length of the FMJ buffer is generally shorter than
                // that of the JMF buffer. This is probably because
                // there is trailing garbage, which JMF knows how to remove, and
                // FMJ does not currently.

                // flags 0x12 // TODO: JMF is setting these flags, should we? or
                // are they residual data?
                // bComplete.setDiscard(false); // not necessary, this flag
                // should be clear on entry.
                if (EXIT_AFTER_ONE_FRAME)
                {
                    System.exit(0);
                }
                return BUFFER_PROCESSED_OK;
            } else
            {
                frameAssemblers
                        .removeAllButNewestN(MAX_ACTIVE_FRAME_ASSEMBLERS); // weed
                                                                           // out
                                                                           // incomplete
                                                                           // frames
                                                                           // that
                                                                           // build
                                                                           // up.
                output.setDiscard(true);
                return OUTPUT_BUFFER_NOT_FILLED;
            }
        } else
        {
            output.setDiscard(true);
            return OUTPUT_BUFFER_NOT_FILLED;
        }

        // TODO: copy over other flags, like EOM?
    }

    @Override
    public void reset()
    {
        if (baselineCodec != null)
            baselineCodec.reset();
        super.reset();
        frameAssemblers.clear();
    }

    @Override
    public Format setInputFormat(Format format)
    {
        if (baselineCodec != null)
        {
            super.setInputFormat(format);
            return baselineCodec.setInputFormat(format);
        } else
        {
            return super.setInputFormat(format);
        }

    }

    @Override
    public Format setOutputFormat(Format format)
    {
        if (baselineCodec != null)
        {
            super.setOutputFormat(format);
            return baselineCodec.setOutputFormat(format);
        } else
        {
            return super.setOutputFormat(format);
        }
    }
}
