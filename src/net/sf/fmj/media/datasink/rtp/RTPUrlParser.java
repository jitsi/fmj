package net.sf.fmj.media.datasink.rtp;

import java.util.regex.*;

/**
 * Parses JMF RTP URLs into ParsedRTPUrl. May only contain 1 or 2 elements (see
 * examples). An element is like: 192.168.1.4:8000/audio/16
 *
 * @author Ken Larson
 *
 */
public class RTPUrlParser
{
    // example URL 1: rtp://224.2.231.36:22224/video&224.2.231.36:22226/audio
    // example URL 2: rtp://192.168.1.4:8000/audio/16

    private static final Pattern pattern = Pattern.compile("rtp://" +

    "([a-zA-Z_/\\.0-9]+)" + // host
            "(:([0-9]+))" + // port
            "(/(audio|video)" + // audio or video
            "(/([0-9]+))?)" + // ttl - optional

            "(\\&" +

            // same pattern again, but this one is optional

            "([a-zA-Z_/\\.0-9]+)" + // host
            "(:([0-9]+))" + // port
            "(/(audio|video)" + // audio or video
            "(/([0-9]+))?)" + // ttl - optional

            ")?");

    private static ParsedRTPUrlElement extract(Matcher m, int offset)
            throws RTPUrlParserException
    {
        ParsedRTPUrlElement e = new ParsedRTPUrlElement();
        try
        {
            e.host = m.group(offset + 1);
            e.port = Integer.parseInt(m.group(offset + 3));
            e.type = m.group(offset + 5);
            if (m.group(offset + 7) != null) // optional
                e.ttl = Integer.parseInt(m.group(offset + 7));
        } catch (NumberFormatException ex)
        {
            throw new RTPUrlParserException(ex);
        }
        return e;
    }

    public static ParsedRTPUrl parse(String url) throws RTPUrlParserException
    {
        Matcher m = pattern.matcher(url);
        if (!m.matches())
            throw new RTPUrlParserException(
                    "URL does not match regular expression for RTP URLs");
        ParsedRTPUrlElement e = extract(m, 0);

        if (m.group(9) == null) // if only 1 element
            return new ParsedRTPUrl(e);

        ParsedRTPUrlElement e2 = extract(m, 8);

        if (e2.type.equals(e.type))
            throw new RTPUrlParserException(
                    "Both elements of the RTP URL have type " + e.type);

        return new ParsedRTPUrl(e, e2);

    }

}
