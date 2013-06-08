package javax.media;

import java.util.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/CaptureDeviceInfo.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 * @author Lyubomir Marinov
 */
public class CaptureDeviceInfo implements java.io.Serializable
{
    protected Format[] formats;
    protected MediaLocator locator;
    protected String name;

    public CaptureDeviceInfo()
    {
        super();
    }

    public CaptureDeviceInfo(String name, MediaLocator locator, Format[] formats)
    {
        this.name = name;
        this.locator = locator;
        this.formats = formats;
    }

    /**
     * Determines whether a specific object is equal by value to this object.
     *
     * @param obj the object to compare by value to this object
     * @return <tt>true</tt> if the specified <tt>obj</tt> is equal by value to
     * this object; otherwise, <tt>false</tt>
     */
    @Override
    public boolean equals(Object obj)
    {
        if (null == obj)
            return false;
        if (this == obj)
            return true;

        if (!(obj instanceof CaptureDeviceInfo))
            return false;

        CaptureDeviceInfo cdi = (CaptureDeviceInfo) obj;

        // name
        String name = getName();
        String cdiName = cdi.getName();

        if (name == null)
        {
            if (cdiName != null)
                return false;
        }
        else if (!name.equals(cdiName))
            return false;

        // locator
        MediaLocator locator = getLocator();
        MediaLocator cdiLocator = cdi.getLocator();

        if (locator == null)
        {
            if (cdiLocator != null)
                return false;
        }
        else if (!locator.equals(cdiLocator))
            return false;

        // formats
        Format[] formats = getFormats();
        Format[] cdiFormats = cdi.getFormats();

        return Arrays.equals(formats, cdiFormats);
    }

    public Format[] getFormats()
    {
        return formats;
    }

    public MediaLocator getLocator()
    {
        return locator;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Gets a hash code value for this object for the benefit of hashtables.
     *
     * @return a hash code value for this object for the benefit of hashtables
     */
    @Override
    public int hashCode()
    {
        int hashCode = 0;

        // name
        String name = getName();

        if (name != null)
            hashCode += name.hashCode();

        // locator
        MediaLocator locator = getLocator();

        if (locator != null)
            hashCode += locator.hashCode();

        // formats
        Format[] formats = getFormats();

        if (formats != null)
        {
            for (Format format : formats)
                if (format != null)
                    hashCode += format.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString()
    {
        final StringBuffer b = new StringBuffer();
        b.append(name);
        b.append(" : ");
        b.append(locator);
        b.append("\n");
        if (formats != null)
        {
            for (int i = 0; i < formats.length; ++i)
            {
                b.append(formats[i]);
                b.append("\n");
            }
        }
        return b.toString();
    }
}
