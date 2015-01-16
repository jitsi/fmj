package javax.media;

/**
 * A <tt>Buffer</tt> is a media-data container that carries media data from one
 * processing stage to the next inside of a <tt>Player</tt> or
 * <tt>Processor</tt>. <tt>Buffer</tt> objects are also used to carry data
 * between a buffer stream and its handler.
 * <p>
 * A <tt>Buffer</tt> object maintains information such as the time stamp,
 * length, and <tt>Format</tt> of the data it carries, as well as any header
 * information that might be required to process the media data.
 *
 * @see PlugIn
 * @see javax.media.protocol.PushBufferStream
 * @see javax.media.protocol.PullBufferStream
 */
public class Buffer
{
    /**
     * The time stamp of the data held in this <tt>Buffer</tt>, in nanoseconds.
     * The time stamp marks the time when presentation of this <tt>Buffer</tt>
     * is to begin. If the start time for this <tt>Buffer</tt> is not known,
     * this time stamp is set to <tt>TIME_UNKNOWN</tt>.
     */
    protected long timeStamp = TIME_UNKNOWN;

    /**
     * The RTP time stamp of the data in this <tt>Buffer</tt>.
     */
    protected long rtpTimeStamp = TIME_UNKNOWN;

    /**
     * The duration of the data held in this <tt>Buffer</tt>, in nanoseconds.
     * The duration specifies how long it will take for this <tt>Buffer</tt> to
     * be presented when the playback rate is 1.0. If the duration for this
     * <tt>Buffer</tt> is not known, it is set to <tt>TIME_UNKNOWN</tt>.
     */
    protected long duration = TIME_UNKNOWN;

    /**
     * The <tt>Format</tt> of the chunk of data in this <tt>Buffer</tt>.
     */
    protected Format format = null;

    /**
     * A flag mask that describes the boolean attributes enabled for this
     * <tt>Buffer</tt>. This mask is set to the logical sum of all of the flags
     * that are set.
     *
     * @see #FLAG_EOM
     * @see #FLAG_DISCARD
     * @see #FLAG_SILENCE
     * @see #FLAG_SID
     * @see #FLAG_KEY_FRAME
     * @see #FLAG_NO_DROP
     * @see #FLAG_NO_WAIT
     * @see #FLAG_NO_SYNC
     * @see #FLAG_RELATIVE_TIME
     * @see #FLAG_SYSTEM_TIME
     * @see #FLAG_RTP_TIME
     * @see #FLAG_FLUSH
     * @see #FLAG_SYSTEM_MARKER
     * @see #FLAG_RTP_MARKER
     */
    protected int flags = 0;

    /**
     * The object that actually holds the media data chunk for this
     * <tt>Buffer</tt>. It can be an array type (such as byte[]) or any other
     * type of object. Use <tt>instanceOf</tt> to determine what type it is.
     */
    protected Object data = null;

    /**
     * Header information (such as RTP header) for this data chunk. It can be of
     * any type. Use <tt>instanceOf</tt> to determine what type it is.
     */
    protected Object header = null;

    /**
     * For array data type, states how many samples are valid in the array. (The
     * array might be larger than the actual media length).
     */
    protected int length = 0;

    /**
     * For array data type, points to the starting point (offset) into the array
     * where the valid data begins.
     */
    protected int offset = 0;

    /**
     * The sequence number of this <tt>Buffer</tt>. The sequence number of
     * adjacent <tt>Buffer</tt> objects in a sequence should differ by 1:
     * positive 1 if the media is playing forward, negative 1 if the media is
     * played in reverse. If the sequence number is not known, SEQUENCE_UNKNOWN
     * is specified.
     */
    protected long sequenceNumber = SEQUENCE_UNKNOWN;

    /**
     * The <tt>RTPHeaderExtension</tt> for this <tt>Buffer</tt>. Currently only
     * a single instance per <tt>Buffer</tt> is allowed.
     */
    private RTPHeaderExtension headerExtension = null;

    /**
     * Indicates that this <tt>Buffer</tt> marks the end of media for the data
     * stream. The Buffer might or might not contain valid data to be processed.
     * The length and data attributes need to be examined to determine whether
     * or not this <tt>Buffer</tt> contains valid data.
     */
    public final static int FLAG_EOM = (1 << 0);

    /**
     * Indicates that the media data in this <tt>Buffer</tt> should be ignored.
     */
    public final static int FLAG_DISCARD = (1 << 1);

    /**
     * Indicates that this <tt>Buffer</tt> contains only silence frames.
     */
    public final static int FLAG_SILENCE = (1 << 2);

    /**
     * Indicates that this <tt>Buffer</tt> contains only SID (silence
     * information description) frames.
     */
    public final static int FLAG_SID = (1 << 3);

    /**
     * Indicates that this <tt>Buffer</tt> starts with a key frame.
     */
    public final static int FLAG_KEY_FRAME = (1 << 4);

    /**
     * Indicates that this <tt>Buffer</tt> will not be dropped even if the frame
     * is behind the presentation schedule.
     */
    public final static int FLAG_NO_DROP = (1 << 5);

    /**
     * Indicates that this <tt>Buffer</tt> will not be waited on even if the
     * frame is ahead of the presentation schedule.
     */
    public final static int FLAG_NO_WAIT = (1 << 6);

    /**
     * Indicates that this <tt>Buffer</tt> is not to be presented in sync with
     * the scheduled presentation time. In other words, the <tt>Buffer</tt> will
     * not be dropped or waited on if it's behind or ahead of schedule.
     */
    public final static int FLAG_NO_SYNC = (FLAG_NO_DROP | FLAG_NO_WAIT);

    /**
     * Indicates that the <tt>Buffer</tt> carries a time stamp that's relative
     * to the SystemTimeBase. This flag is generally set for data transferred
     * from hardware capture DataSources that uses the system clock.
     */
    public final static int FLAG_SYSTEM_TIME = (1 << 7);

    /**
     * Indicates that the <tt>Buffer</tt> carries a time stamp that's in
     * relative time units. This means that individual time stamps are not
     * measured against any identifiable absolute origin--only the difference
     * between the time stamps of two consecutive buffers carries useful
     * information. (This is the time difference between the two packets.)
     */
    public final static int FLAG_RELATIVE_TIME = (1 << 8);

    /**
     * This is a marker bit used by the system. When this flag is set, it marks
     * a zero-length <tt>Buffer</tt> generated by the system to flush the data
     * path. Do not attempt to use or overwrite this flag.
     */
    public final static int FLAG_FLUSH = (1 << 9);

    /**
     * This is a marker bit used by the system. Do not attempt to use or
     * overwrite this flag.
     */
    public final static int FLAG_SYSTEM_MARKER = (1 << 10);

    /**
     * This is a marker bit for RTP. Indicates that the <tt>Buffer</tt> is the
     * last packet of a video frame.
     *
     */
    public final static int FLAG_RTP_MARKER = (1 << 11);

    /**
     * Indicates that the <tt>Buffer</tt> carries a time stamp that's in RTP
     * (NTP) time units.
     */
    public final static int FLAG_RTP_TIME = (1 << 12);

    /**
     * Indicates that some buffer queue in the data flow path from where this
     * buffer comes from is overflown. When such condition occurs, the
     * processing element should attempt to speed up the processing of this
     * buffer object to reduce the overflow.
     */
    public final static int FLAG_BUF_OVERFLOWN = (1 << 13);

    /**
     * Indicates that some buffer queue in the data flow path from where this
     * buffer comes from is underflown. When such condition occurs, the
     * processing element should attempt to speed up the processing of this
     * buffer object to reduce the underflow.
     */
    public final static int FLAG_BUF_UNDERFLOWN = (1 << 14);

    /**
     * Indicates that the data is arriving from a live (real-time) source.
     */
    public final static int FLAG_LIVE_DATA = (1 << 15);

    /**
     * Indicates that FEC data should not be decoded for this <tt>Buffer</tt>
     */
    public final static int FLAG_SKIP_FEC = (1 << 16);

    /**
     * The <tt>getTimeStamp</tt> method return this value if the time stamp of
     * the media is not known.
     */
    public final static long TIME_UNKNOWN = -1L;

    /**
     * The <tt>getSequenceNumber</tt> method returns this value if the sequence
     * number is not known.
     */
    public final static long SEQUENCE_UNKNOWN = Long.MAX_VALUE - 1;

    /**
     * Clone a buffer.
     */
    @Override
    public Object clone()
    {
        Buffer buf = new Buffer();
        Object data = getData();

        if (data != null)
        {
            if (data instanceof byte[])
                buf.data = ((byte[]) data).clone();
            else if (data instanceof int[])
                buf.data = ((int[]) data).clone();
            else if (data instanceof short[])
                buf.data = ((short[]) data).clone();
            else
                buf.data = data;
        }
        if (header != null)
        {
            if (header instanceof byte[])
                buf.header = ((byte[]) header).clone();
            else if (header instanceof int[])
                buf.header = ((int[]) header).clone();
            else if (header instanceof short[])
                buf.header = ((short[]) header).clone();
            else
                buf.header = header;
        }
        buf.format = format;
        buf.length = length;
        buf.offset = offset;
        buf.timeStamp = timeStamp;
        buf.rtpTimeStamp = rtpTimeStamp;
        buf.headerExtension = headerExtension;
        buf.duration = duration;
        buf.sequenceNumber = sequenceNumber;
        buf.flags = flags;

        return buf;
    }

    /**
     * Copy the attributes from the specified <tt>Buffer</tt> into this
     * <tt>Buffer</tt>
     *
     * @param buffer
     *            The input <tt>Buffer</tt> the copy the attributes from.
     */
    public void copy(Buffer buffer)
    {
        copy(buffer, false);
    }

    /**
     * Copy the attributes from the specified <tt>Buffer</tt> into this
     * <tt>Buffer</tt>. If swapData is true, the data values are swapped between
     * the buffers, otherwise the data value is copied.
     *
     * @param buffer
     *            The input <tt>Buffer</tt> the copy the attributes from.
     * @param swapData
     *            Specifies whether the data objects are to be swapped.
     */
    public void copy(Buffer buffer, boolean swapData)
    {
        if (swapData)
        {
            Object temp = data;
            data = buffer.data;
            buffer.data = temp;
        } else
        {
            data = buffer.data;
        }
        header = buffer.header;
        format = buffer.format;
        length = buffer.length;
        offset = buffer.offset;
        timeStamp = buffer.timeStamp;
        rtpTimeStamp = buffer.rtpTimeStamp;
        headerExtension = buffer.headerExtension;
        duration = buffer.duration;
        sequenceNumber = buffer.sequenceNumber;
        flags = buffer.flags;
    }

    /**
     * Gets the internal data object that holds the media chunk contained in
     * this <tt>Buffer</tt>.
     *
     * @return The data object that holds the media chunk for this
     *         <tt>Buffer</tt>. It can be an array type (such as byte[]) or any
     *         other type of object. Use <tt>instanceOf</tt> to determine what
     *         type it is.
     * @see #data
     */
    public Object getData()
    {
        return data;
    }

    /**
     * Gets the duration of this <tt>Buffer</tt>.
     *
     * @return The <tt>Buffer</tt> duration, in nanoseconds.
     * @see #duration
     */
    public long getDuration()
    {
        return duration;
    }

    /**
     * Gets the mask of the flags set for this <tt>Buffer</tt>. The integer
     * value of the mask is equal to the logical sum of the flags that are set.
     *
     * @see #FLAG_EOM
     * @see #FLAG_DISCARD
     * @see #FLAG_SILENCE
     * @see #FLAG_SID
     * @see #FLAG_KEY_FRAME
     * @see #FLAG_NO_DROP
     * @see #FLAG_NO_WAIT
     * @see #FLAG_NO_SYNC
     * @see #FLAG_RELATIVE_TIME
     * @see #FLAG_FLUSH
     * @see #FLAG_SYSTEM_MARKER
     * @see #FLAG_RTP_MARKER
     */
    public int getFlags()
    {
        return flags;
    }

    /**
     * Get the <tt>Format</tt> of the data in this <tt>Buffer</tt>.
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * Gets the header information for the media chunk contained in this
     * <tt>Buffer</tt>.
     *
     * @return The object that holds the header information. Use
     *         <tt>instanceOf</tt> to determine what type the header object is.
     * @see #header
     */
    public Object getHeader()
    {
        return header;
    }

    /**
     * Gets the length of the valid data in this <tt>Buffer</tt> if the data is
     * held in an array.
     *
     * @return The length of the valid data in the data array that holds the
     *         media chunk for this <tt>Buffer</tt>.
     * @see #length
     */
    public int getLength()
    {
        return length;
    }

    /**
     * If the media chunk for this <tt>Buffer</tt> is held in an array, gets the
     * offset into the data array where the valid data begins.
     *
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * Gets the RTP time stamp of this <tt>Buffer</tt>.
     *
     * @return the RTP time stamp of this <tt>Buffer</tt>.
     */
    public long getRtpTimeStamp()
    {
        return rtpTimeStamp;
    }

    /**
     * Gets the sequence number of this <tt>Buffer</tt>.
     *
     * @return The sequence number of this <tt>Buffer</tt>.
     * @see #sequenceNumber
     */
    public long getSequenceNumber()
    {
        return sequenceNumber;
    }

    /**
     * Gets the time stamp of this <tt>Buffer</tt>.
     *
     * @return The <tt>Buffer</tt> time stamp, in nanoseconds.
     * @see #timeStamp
     */
    public long getTimeStamp()
    {
        return timeStamp;
    }

    /**
     * Checks whether or not this <tt>Buffer</tt> is to be discarded.
     * <p>
     * This method provides a convenient alternative to using <tt>getFlags</tt>
     * to check the DISCARD flag.
     *
     * @return <tt>true</tt> if the DISCARD flag is enabled, <tt>false</tt> if
     *         it is not.
     * @see #getFlags
     * @see #FLAG_DISCARD
     */
    public boolean isDiscard()
    {
        return (flags & FLAG_DISCARD) != 0;
    }

    /**
     * Checks whether or not this <tt>Buffer</tt> marks the end of the media
     * stream. Even it <tt>isEOM</tt> returns <tt>true</tt>, the <tt>Buffer</tt>
     * might still contain valid data--check the length of the <tt>Buffer</tt>.
     * <p>
     * This method provides a convenient alternative to using <tt>getFlags</tt>
     * to check the EOM flag.
     *
     * @return <tt>true</tt> if the EOM flag is enabled, <tt>false</tt> if it is
     *         not.
     * @see #getFlags
     * @see #FLAG_EOM
     */
    public boolean isEOM()
    {
        return (flags & FLAG_EOM) != 0;
    }

    /**
     * Sets the internal data object that holds the media chunk.
     *
     * @param data
     *            The data object that holds the media data chunk for this
     *            <tt>Buffer</tt>. It can be an array type (such as byte[]) or
     *            any other type of object.
     * @see #data
     */
    public void setData(Object data)
    {
        this.data = data;
    }

    /**
     * Sets the DISCARD flag for this <tt>Buffer</tt>. If the DISCARD flag is
     * enabled, this <tt>Buffer</tt> is to be discarded.
     * <p>
     * This method provides a convenient alternative to using <tt>setFlags</tt>
     * to enable or disable the DISCARD flag.
     *
     * @param discard
     *            A boolean value that contains the DISCARD status of the
     *            <tt>Buffer</tt>. Set to <tt>true</tt> to enable the EOM flag,
     *            <tt>false</tt> to disable the flag.
     * @see #setFlags
     * @see #FLAG_DISCARD
     */
    public void setDiscard(boolean discard)
    {
        if (discard)
            flags |= FLAG_DISCARD;
        else
            flags &= ~FLAG_DISCARD;
    }

    /**
     * Sets the duration of this <tt>Buffer</tt>.
     *
     * @param duration
     *            The duration for the <tt>Buffer</tt>, in nanoseconds.
     * @see #duration
     */
    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    /**
     * Sets the EOM flag for this <tt>Buffer</tt>. If the EOM flag is enabled,
     * this is the last <tt>Buffer</tt> in the media stream.
     * <p>
     * This method provides a convenient alternative to using <tt>setFlags</tt>
     * to enable or disable the EOM flag.
     *
     * @param eom
     *            A boolean value that contains the EOM status of the
     *            <tt>Buffer</tt>. Set to <tt>true</tt> to enable the EOM flag,
     *            <tt>false</tt> to disable the flag.
     * @see #setFlags
     * @see #FLAG_EOM
     */
    public void setEOM(boolean eom)
    {
        if (eom)
            flags |= FLAG_EOM;
        else
            flags &= ~FLAG_EOM;
    }

    /**
     * Sets the flag mask for this <tt>Buffer</tt>. The integer value of the
     * mask is equal to the logical sum of the flags that are set.
     *
     * @see #FLAG_EOM
     * @see #FLAG_DISCARD
     * @see #FLAG_SILENCE
     * @see #FLAG_SID
     * @see #FLAG_KEY_FRAME
     * @see #FLAG_NO_DROP
     * @see #FLAG_NO_WAIT
     * @see #FLAG_NO_SYNC
     * @see #FLAG_RELATIVE_TIME
     * @see #FLAG_FLUSH
     * @see #FLAG_SYSTEM_MARKER
     * @see #FLAG_RTP_MARKER
     */
    public void setFlags(int flags)
    {
        this.flags = flags;
    }

    /**
     * Sets the <tt>Format</tt> of the data in this <tt>Buffer</tt>.
     *
     * @param format
     *            The <tt>Format</tt> of the data.
     */
    public void setFormat(Format format)
    {
        this.format = format;
    }

    /**
     * Sets the header information for the media chunk.
     *
     * @param header
     *            The header object that holds the media data chunk for this
     *            <tt>Buffer</tt>.
     * @see #header
     */
    public void setHeader(Object header)
    {
        this.header = header;
    }

    /**
     * Sets the length of the valid data stored in this <tt>Buffer</tt> if the
     * data is held in an array.
     *
     * @param length
     *            The length of the valid data in the data array that holds the
     *            media chunk for this <tt>Buffer</tt>.
     * @see #length
     */
    public void setLength(int length)
    {
        this.length = length;
    }

    /**
     * If the media chunk for this <tt>Buffer</tt> is held in an array, sets the
     * offset into the array where the valid data begins.
     *
     * @param offset
     *            The starting point for the valid data.
     * @see #offset
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    /**
     * Sets the RTP time stamp of this <tt>Buffer</tt>.
     *
     * @param rtpTimeStamp the value to set.
     */
    public void setRtpTimeStamp(long rtpTimeStamp)
    {
        this.rtpTimeStamp = rtpTimeStamp;
    }

    /**
     * Sets the sequence number of this <tt>Buffer</tt>. Sequence numbers
     * increase or decrease by 1 for each sequential <tt>Buffer</tt>, indicating
     * the order in which the data is to be processed. Can be used to identify
     * lost samples of data.
     *
     * @param number
     *            The sequence number for the <tt>Buffer</tt>.
     * @see #sequenceNumber
     */
    public void setSequenceNumber(long number)
    {
        sequenceNumber = number;
    }

    /**
     * Sets the time stamp of this <tt>Buffer</tt>.
     *
     * @param timeStamp
     *            The time stamp for the <tt>Buffer</tt>, in nanoseconds.
     * @see #timeStamp
     */
    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    /**
     * Gets the <tt>RTPHeaderExtension</tt> of this <tt>Buffer</tt>.
     * @return the <tt>RTPHeaderExtension</tt> of this <tt>Buffer</tt>.
     */
    public RTPHeaderExtension getHeaderExtension()
    {
        return headerExtension;
    }

    /**
     * Sets the <tt>RTPHeaderExtension</tt> of this <tt>Buffer</tt>.
     * @param headerExtension the <tt>RTPHeaderExtension</tt> to set.
     */
    public void setHeaderExtension(RTPHeaderExtension headerExtension)
    {
        this.headerExtension = headerExtension;
    }

    /**
     * A simple structure that represents an RTP header extension. Defined here
     * for ease of use as a <tt>Buffer</tt>'s header.
     */
    public static class RTPHeaderExtension
    {
        /**
         * The RTP header extension ID. Only 4 bits are used, so it should be
         * in [0,15].
         */
        public byte id;

        /**
         * The data of the extension.
         */
        public byte[] value;

        /**
         * Initializes a new <tt>RTPHeaderExtension</tt> with the given ID and
         * value.
         * @param id the ID of the extension.
         * @param value the value of the extension.
         */
        public RTPHeaderExtension(byte id, byte[] value)
        {
            if (value == null)
                throw new NullPointerException("value");
            this.id = id;
            this.value = value;
        }
    }
}
