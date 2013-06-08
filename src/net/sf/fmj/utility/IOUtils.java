package net.sf.fmj.utility;

import java.io.*;

/**
 * I/O utilities.
 *
 * @author Ken Larson
 *
 */
public class IOUtils
{
    private static final int BUFFER_SIZE = 2048;

    public static void copyFile(File fileIn, File fileOut) throws IOException
    {
        InputStream is = new FileInputStream(fileIn);
        OutputStream os = new FileOutputStream(fileOut);
        copyStream(is, os);
        is.close();
        os.close();
    }

    public static void copyFile(String fileIn, String fileOut)
            throws IOException
    {
        copyFile(new File(fileIn), new File(fileOut));
    }

    public static void copyStream(InputStream is, OutputStream os)
            throws IOException
    {
        byte[] buf = new byte[BUFFER_SIZE];
        while (true)
        {
            int len = is.read(buf);
            if (len == -1)
                return;
            os.write(buf, 0, len);
        }
    }

    /** Closes is when finished. */
    public static byte[] readAll(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copyStream(is, baos);
        is.close();
        return baos.toByteArray();
    }

    /** TODO: only handles standard ASCII. */
    public static String readAll(java.io.Reader reader) throws IOException
    {
        final StringBuilder b = new StringBuilder();
        while (true)
        {
            int c = reader.read();
            if (c == -1)
                break;
            b.append((char) c);
        }
        return b.toString();
    }

    /** Assumes default file encoding. */
    public static String readAllToString(java.io.InputStream is)
            throws IOException
    {
        byte[] b = readAll(is);
        return new String(b);

    }

    public static String readAllToString(java.io.InputStream is, String encoding)
            throws IOException
    {
        byte[] b = readAll(is);
        return new String(b, encoding);

    }

    public static void readAllToStringBuffer(java.io.InputStream is,
            String encoding, StringBuffer b) throws IOException
    {
        b.append(readAllToString(is, encoding));
    }

    /** Assumes default file encoding. */
    public static void readAllToStringBuffer(java.io.InputStream is,
            StringBuffer b) throws IOException
    {
        b.append(readAllToString(is));
    }

    public static void readAllToStringBuilder(java.io.InputStream is,
            String encoding, StringBuilder b) throws IOException
    {
        b.append(readAllToString(is, encoding));
    }

    /** Assumes default file encoding. */
    public static void readAllToStringBuilder(java.io.InputStream is,
            StringBuilder b) throws IOException
    {
        b.append(readAllToString(is));
    }

    public static void writeStringToFile(String value, String path)
            throws IOException
    {
        final FileOutputStream fos = new FileOutputStream(path);
        fos.write(value.getBytes());
        fos.close();
    }

    private IOUtils()
    {
        super();
    }
}
