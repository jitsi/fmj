package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class IMediaSeeking extends IUnknown
{
    // TODO: should these be defined in their own classes?
    // typedef enum AM_SEEKING_SeekingFlags
    // {
    public static final int AM_SEEKING_NoPositioning = 0x00; // No change

    public static final int AM_SEEKING_AbsolutePositioning = 0x01; // Position
                                                                   // is
                                                                   // supplied
                                                                   // and is
                                                                   // absolute
    public static final int AM_SEEKING_RelativePositioning = 0x02; // Position
                                                                   // is
                                                                   // supplied
                                                                   // and is
                                                                   // relative

    public static final int AM_SEEKING_IncrementalPositioning = 0x03; // (Stop)
                                                                      // position
                                                                      // relative
                                                                      // to
                                                                      // current
    // Useful for seeking when paused (use +1)
    public static final int AM_SEEKING_PositioningBitsMask = 0x03; // Useful
                                                                   // mask

    public static final int AM_SEEKING_SeekToKeyFrame = 0x04; // Just seek to
                                                              // key frame
                                                              // (performance
                                                              // gain)
    public static final int AM_SEEKING_ReturnTime = 0x08; // Plug the media time
                                                          // equivalents back
                                                          // into the supplied
                                                          // LONGLONGs

    public static final int AM_SEEKING_Segment = 0x10; // At end just do
                                                       // EC_ENDOFSEGMENT,
    // don't do EndOfStream
    public static final int AM_SEEKING_NoFlush = 0x20; // Don't flush
    // } AM_SEEKING_SEEKING_FLAGS;
    // typedef enum AM_SEEKING_SeekingCapabilities
    // {
    public static final int AM_SEEKING_CanSeekAbsolute = 0x001;
    public static final int AM_SEEKING_CanSeekForwards = 0x002;
    public static final int AM_SEEKING_CanSeekBackwards = 0x004;
    public static final int AM_SEEKING_CanGetCurrentPos = 0x008;
    public static final int AM_SEEKING_CanGetStopPos = 0x010;

    public static final int AM_SEEKING_CanGetDuration = 0x020;
    public static final int AM_SEEKING_CanPlayBackwards = 0x040;

    public static final int AM_SEEKING_CanDoSegments = 0x080;
    public static final int AM_SEEKING_Source = 0x100; // Doesn't pass thru used
                                                       // to

    // count segment ends
    // } AM_SEEKING_SEEKING_CAPABILITIES;
    static native GUID Init_IID(GUID guid);

    public IMediaSeeking(long ptr)
    {
        super(ptr);
    }

    public native int GetCurrentPosition(long[] pCurrent);

    public native int GetDuration(long[] pDuration);

    public native int GetPositions(long[] pCurrent, long[] pStop);

    public native int GetRate(double[] dRate);

    public native int SetPositions(long[] pCurrent, int currentFlags,
            long[] pStop, int stopFlags);

    public native int SetRate(double dRate);
}
