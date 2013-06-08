package javax.media;

import java.io.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/MediaLocator.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 * @author Lyubomir Marinov
 */
public class MediaLocator implements Serializable
{
    private static final long serialVersionUID = -6747425113475481405L;

    private String locatorString;

    public MediaLocator(java.net.URL url)
    {
        this.locatorString = url.toExternalForm(); // TODO: is this correct?
    }

    public MediaLocator(String locatorString)
    {
        if (locatorString == null)
            throw new NullPointerException("locatorString");
        this.locatorString = locatorString;
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

        if (!(obj instanceof MediaLocator))
            return false;

        MediaLocator ml = (MediaLocator) obj;

        return
            (locatorString == null)
                ? (ml.locatorString == null)
                : locatorString.equals(ml.locatorString);
    }

    public String getProtocol()
    {
        int colonIndex = locatorString.indexOf(':');
        if (colonIndex < 0)
            return "";
        return locatorString.substring(0, colonIndex);
    }

    public String getRemainder()
    {
        int colonIndex = locatorString.indexOf(':');
        if (colonIndex < 0)
            return "";
        return locatorString.substring(colonIndex + 1);
    }

    public java.net.URL getURL() throws java.net.MalformedURLException
    {
        return new java.net.URL(locatorString);
    }
    /**
     * Gets a hash code value for this object for the benefit of hashtables.
     *
     * @return a hash code value for this object for the benefit of hashtables
     */
    @Override
    public int hashCode()
    {
        return (locatorString == null) ? 0 : locatorString.hashCode();
    }

    public String toExternalForm()
    {
        return locatorString;
    }

    @Override
    public String toString()
    {
        return locatorString;
    }
}
