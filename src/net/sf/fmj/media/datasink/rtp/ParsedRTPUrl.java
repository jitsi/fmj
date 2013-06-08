package net.sf.fmj.media.datasink.rtp;

/**
 * Parsed RTP URL. Contains an array of ParsedRTPUrlElement, each of which
 * corresponds to a subcomponent of the URL (audio or video).
 *
 * @author Ken Larson
 *
 */
public class ParsedRTPUrl
{
    public final ParsedRTPUrlElement[] elements;

    public ParsedRTPUrl(ParsedRTPUrlElement e)
    {
        this(new ParsedRTPUrlElement[] { e });
    }

    public ParsedRTPUrl(ParsedRTPUrlElement e, ParsedRTPUrlElement e2)
    {
        this(new ParsedRTPUrlElement[] { e, e2 });
    }

    public ParsedRTPUrl(final ParsedRTPUrlElement[] elements)
    {
        super();
        this.elements = elements;
    }

    public ParsedRTPUrlElement find(String type)
    {
        for (int i = 0; i < elements.length; ++i)
        {
            if (elements[i].type.equals(type))
                return elements[i];
        }
        return null;
    }

    @Override
    public String toString()
    {
        if (elements == null)
            return "null";

        StringBuffer b = new StringBuffer();
        b.append("rtp://");

        for (int i = 0; i < elements.length; ++i)
        {
            if (i > 0)
                b.append("&");
            b.append(elements[i]);
        }
        return b.toString();
    }
}
