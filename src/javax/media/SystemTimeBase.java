package javax.media;

/**
 * <tt>SystemTimeBase</tt> implements the default <tt>TimeBase</tt>.
 *
 * @see TimeBase
 */
final public class SystemTimeBase implements TimeBase
{
    // Pick some offset (start-up time) so the system time won't be
    // so huge. The huge numbers overflow floating point operations
    // in some cases.
    static long offset = System.currentTimeMillis() * 1000000L;

    public long getNanoseconds()
    {
        return (System.currentTimeMillis() * 1000000L) - offset;
    }

    public Time getTime()
    {
        return new Time(getNanoseconds());
    }
}
