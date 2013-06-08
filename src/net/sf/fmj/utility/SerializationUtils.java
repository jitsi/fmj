package net.sf.fmj.utility;

import java.io.*;

import com.lti.utils.*;

/**
 * Utilities for serializing/de-serializing objects using standard Java
 * serialization.
 *
 * @author Ken Larson
 *
 */
public class SerializationUtils
{
    public static javax.media.Format deserialize(String s) throws IOException,
            ClassNotFoundException
    {
        final byte[] ba = StringUtils.hexStringToByteArray(s);
        final ByteArrayInputStream inbuf = new ByteArrayInputStream(ba);
        final ObjectInputStream input = new ObjectInputStream(inbuf);
        final Object oRead = input.readObject();
        input.close();
        inbuf.close();
        return (javax.media.Format) oRead;

    }

    public static String serialize(javax.media.Format f) throws IOException
    {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(f);
        output.close();
        buffer.close();
        return StringUtils.byteArrayToHexString(buffer.toByteArray());

    }
}
