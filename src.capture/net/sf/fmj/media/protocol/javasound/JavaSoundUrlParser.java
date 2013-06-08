package net.sf.fmj.media.protocol.javasound;

import java.util.regex.*;

import javax.media.*;
import javax.media.format.*;

/**
 *
 * @author Ken Larson
 *
 */
public class JavaSoundUrlParser
{
    // for javasound url syntax, see
    // http://archives.java.sun.com/cgi-bin/wa?A2=ind9906&L=jmf-interest&P=4678
    // javasound://<rate>/<sizeInBits>/<channels>/[big|little]/[signed|unsig

    private static final Pattern pattern = Pattern.compile("javasound://" +

    "(([0-9]+)" + // rate - optional
            "(/([0-9]+)" + // size in bits - optional
            "(/([0-9]+)" + // channels - optional
            "(/(big|little)" + // endian - optional
            "(/(signed|unsigned)" + // signed/unsigned - optional
            ")?)?)?)?)?"

    );

    public static AudioFormat parse(String url)
            throws JavaSoundUrlParserException
    {
        if (url == null)
            throw new JavaSoundUrlParserException(new NullPointerException());

        if (!url.startsWith("javasound://"))
            throw new JavaSoundUrlParserException(
                    "Expected URL to start with: " + "javasound://");
        Matcher m = pattern.matcher(url);
        if (!m.matches())
            throw new JavaSoundUrlParserException(
                    "URL does not match regular expression for javasound URLs");

        int groupCount = m.groupCount();

        double rate = Format.NOT_SPECIFIED;
        int bits = Format.NOT_SPECIFIED;
        int channels = Format.NOT_SPECIFIED;
        int endian = Format.NOT_SPECIFIED;
        int signed = Format.NOT_SPECIFIED;

        // System.out.println("url: " + url);
        // for (int i = 0; i < 10; ++i)
        // System.out.println(" " + i + " " + m.group(i));

        try
        {
            if (m.group(2) != null && !m.group(2).equals(""))
                rate = Double.parseDouble(m.group(2));
            if (m.group(4) != null && !m.group(4).equals(""))
                bits = Integer.parseInt(m.group(4));
            if (m.group(6) != null && !m.group(6).equals(""))
                channels = Integer.parseInt(m.group(6));
            if (m.group(8) != null && !m.group(8).equals(""))
                endian = m.group(8).equals("big") ? AudioFormat.BIG_ENDIAN
                        : AudioFormat.LITTLE_ENDIAN;
            if (m.group(10) != null && !m.group(10).equals(""))
                signed = m.group(10).equals("signed") ? AudioFormat.SIGNED
                        : AudioFormat.UNSIGNED;

        } catch (NumberFormatException e)
        {
            throw new JavaSoundUrlParserException("Invalid number", e);
        }
        return new AudioFormat(AudioFormat.LINEAR, rate, bits, channels,
                endian, signed);

    }
}
