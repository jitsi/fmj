package javax.media;

/**
 * A <tt>Format</tt> abstracts an exact media format. It carries no
 * encoding-specific parameters or timing information global to the
 * presentation.
 * <p>
 * <h3>Comparing different formats</h3>
 * Not all of the attributes in a <tt>Format</tt> object have to be specified.
 * This enables selected attributes to be specified, making it possible to
 * locate a supported <tt>Format</tt> that meets certain requirements without
 * needing to find an exact match.
 * <p>
 * Two methods are provided for comparing <tt>Formats</tt>. The <tt>equals</tt>
 * method returns <tt>true</tt> if two <tt>Format</tt> objects are exactly the
 * same--they're the same type and all of their attributes are the same. The
 * <tt>matches</tt> method relaxes the comparison, comparing only the attributes
 * that are explicitly specified in the <tt>Format</tt> you are comparing.
 */
public class Format implements java.lang.Cloneable, java.io.Serializable
{
    public static final int NOT_SPECIFIED = -1;
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    protected String encoding;

    /**
     * The data object required by the <tt>Format</tt> is an integer array.
     */
    public static final Class<?> intArray = (new int[0]).getClass();

    /**
     * The data object required by the <tt>Format</tt> is a short array.
     */
    public static final Class<?> shortArray = (new short[0]).getClass();

    /**
     * The data object required by the <tt>Format</tt> is a byte array.
     */
    public static final Class<?> byteArray = (new byte[0]).getClass();

    /**
     * The data object required by the <tt>Format</tt> is an array of
     * <tt>Format</tt> objects.
     */
    public static final Class<?> formatArray = (new Format[0]).getClass();

    protected Class<?> dataType = byteArray;
    protected Class<?> clz = getClass(); // Cache the to optimize on
    // equals, matches & intersect.

    private long encodingCode = 0;

    /**
     * Constructs a <tt>Format</tt> that has the specified encoding type.
     *
     * @param encoding
     *            A <tt>String</tt> that contains the encoding type of the
     *            <tt>Format</tt> to be constructed.
     */
    public Format(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Constructs a <tt>Format</tt> that has the specified encoding and data
     * types.
     *
     * @param encoding
     *            A <tt>String</tt> that contains the encoding type of the
     *            <tt>Format</tt> to be constructed.
     * @param dataType
     *            The type of data object required by the <tt>Format</tt> to be
     *            constructed, such as: <tt>byteArray</tt>, <tt>intArray</tt>,
     *            or <tt>shortArray</tt>. For example, for a byte array the data
     *            type would be " <tt>Format.byteArray</tt>".
     */
    public Format(String encoding, Class<?> dataType)
    {
        this(encoding);
        this.dataType = dataType;
    }

    /**
     * Creates a clone of this <tt>Format</tt>.
     *
     * @return A clone of this format.
     */
    @Override
    public Object clone()
    {
        Format f = new Format(encoding);
        f.copy(this);
        return f;
    }

    /**
     * Copies the attributes from the specified <tt>Format</tt> into this
     * <tt>Format</tt>.
     *
     * @param f
     *            The <tt>Format</tt> to copy the attributes from.
     */
    protected void copy(Format f)
    {
        dataType = f.dataType;
    }

    /**
     * Checks whether or not the specified <tt>Format</tt> is the same as this
     * <tt>Format</tt>. To be equal, the two <tt>Formats</tt> must be of the
     * same type and all of their attributes must be the same.
     *
     * @param format
     *            The <tt>Format</tt> to compare with this one.
     * @return <tt>true</tt> if the specified <tt>Format</tt> is the same as
     *         this one, <tt>false</tt> if it is not.
     */
    @Override
    public boolean equals(Object format)
    {
        if (format == null || clz != ((Format) format).clz)
            return false;

        String otherEncoding = ((Format) format).encoding;
        Class<?> otherType = ((Format) format).dataType;

        return (dataType == otherType)
                && (encoding == otherEncoding || ((encoding != null && otherEncoding != null) && isSameEncoding((Format) format)));
    }

    /**
     * Gets the type of the data that this <tt>Format</tt> requires. For
     * example, for byte array it returns "<tt>byte[].class</tt>".
     *
     * @return The data type of this <tt>Format</tt>.
     */
    public Class<?> getDataType()
    {
        return dataType;
    }

    /**
     * Gets the uniquely-qualified encoding name for this <tt>Format</tt>.
     * <p>
     * In the reference implementation of JMF, these strings follow the
     * QuickTime codec strings.
     *
     * @return The encoding of the <tt>Format</tt>.
     */
    public String getEncoding()
    {
        return encoding;
    }

    private long getEncodingCode(String enc)
    {
        byte chars[] = enc.getBytes();
        byte b;
        long code = 0;
        for (int i = 0; i < enc.length(); i++)
        {
            b = chars[i];
            if (b > 96 && b < 123)
                b -= 32; // lower to upper
            b -= 32;
            if (b > 63)
                return -1;
            code = (code << 6) | b;
        }
        return code;
    }

    /**
     * Intersects the attributes of this format and the specified format to
     * create a new <tt>Format</tt> object. The two objects being intersected
     * should either be of the same type or one should be a subclass of the
     * other. The resulting object will be the same type as the subclass.
     * <P>
     * Common attributes are intersected as follows: If both objects have
     * NOT_SPECIFIED values for an attribute, the result will also have a
     * NOT_SPECIFIED value. If one of them has a NOT_SPECIFIED value then the
     * result will have the value that is specified in the other object. If both
     * objects have specified values then the value in this object will be used.
     * <P>
     * Attributes that are specific to the subclass will be carried forward to
     * the result.
     *
     * @param other
     *            The <tt>Format</tt> object to intersect with this
     *            <tt>Format</tt>.
     * @return A <tt>Format</tt> object with its attributes set to those
     *         attributes common to both <tt>Format</tt> objects.
     * @see #matches
     */
    public Format intersects(Format other)
    {
        Format res;
        if (clz.isAssignableFrom(other.clz))
            res = (Format) other.clone();
        else if (other.clz.isAssignableFrom(clz))
            res = (Format) clone();
        else
            return null;
        if (res.encoding == null)
            res.encoding = (encoding != null ? encoding : other.encoding);
        if (res.dataType == null)
            res.dataType = (dataType != null ? dataType : other.dataType);
        return res;
    }

    /**
     * Checks if the encodings of both format objects are the same. Its faster
     * than calling String.equalsIgnoreCase to compare the two encodings.
     *
     * @return true if the encodings are the same, false otherwise.
     */
    public boolean isSameEncoding(Format other)
    {
        if (encoding == null || other == null || other.encoding == null)
            return false;
        // Quick checks
        if (encoding == other.encoding)
            return true;
        if (encodingCode > 0 && other.encodingCode > 0)
            return encodingCode == other.encodingCode;

        // Works faster only for shorter strings of 10 chars or less.
        if (encoding.length() > 10)
            return encoding.equalsIgnoreCase(other.encoding);
        if (encodingCode == 0)
        {
            encodingCode = getEncodingCode(encoding);
        }
        // If the encoding code cannot be computed (out of bounds chars)
        // or in the off chance that its all spaces.
        if (encodingCode <= 0)
            return encoding.equalsIgnoreCase(other.encoding);

        if (other.encodingCode == 0)
            return other.isSameEncoding(this);
        else
            return encodingCode == other.encodingCode;
    }

    /**
     * Checks if the encoding of this format is same as the parameter. Its
     * faster than calling String.equalsIgnoreCase to compare the two encodings.
     *
     * @return true if the encodings are the same, false otherwise.
     */
    public boolean isSameEncoding(String encoding)
    {
        if (this.encoding == null || encoding == null)
            return false;
        // Quick check
        if (this.encoding == encoding)
            return true;
        // Works faster only for shorter strings of 10 chars or less.
        if (this.encoding.length() > 10)
            return this.encoding.equalsIgnoreCase(encoding);
        // Compute encoding code only once
        if (encodingCode == 0)
        {
            encodingCode = getEncodingCode(this.encoding);
        }
        // If the encoding code cannot be computed (out of bounds chars)
        if (encodingCode < 0)
            return this.encoding.equalsIgnoreCase(encoding);
        long otherEncodingCode = getEncodingCode(encoding);
        return encodingCode == otherEncodingCode;
    }

    /**
     * Checks whether or not the specified <tt>Format</tt> <EM>matches</EM> this
     * <tt>Format</tt>. Matches only compares the attributes that are defined in
     * the specified <tt>Format</tt>, unspecified attributes are ignored.
     * <p>
     * The two <tt>Format</tt> objects do not have to be of the same class to
     * match. For example, if "A" are "B" are being compared, a match is
     * possible if "A" is derived from "B" or "B" is derived from "A". (The
     * compared attributes must still match, or <tt>matches</tt> fails.)
     *
     * @param format
     *            The <tt>Format</tt> to compare with this one.
     * @return <tt>true</tt> if the specified <tt>Format</tt> matches this one,
     *         <tt>false</tt> if it does not.
     */
    public boolean matches(Format format)
    {
        if (format == null)
            return false;

        return (format.encoding == null || encoding == null || isSameEncoding(format))
                && (format.dataType == null || dataType == null || format.dataType == dataType)
                && (clz.isAssignableFrom(format.clz) || format.clz
                        .isAssignableFrom(clz));
    }

    /**
     * Generate a format that's less restrictive than this format but contains
     * the basic attributes that will make this resulting format useful for
     * format matching.
     *
     * @return A <tt>Format</tt> that's less restrictive than the this format.
     */
    public Format relax()
    {
        return (Format) clone();
    }

    /**
     * Gets a <tt>String</tt> representation of the <tt>Format</tt> attributes.
     * For example: "PCM, 44.1 KHz, Stereo, Signed".
     *
     * @return A <tt>String</tt> that describes the <tt>Format</tt> attributes.
     */
    @Override
    public String toString()
    {
        return getEncoding();
    }
}
