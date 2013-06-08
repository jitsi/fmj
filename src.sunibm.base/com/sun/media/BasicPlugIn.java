package com.sun.media;

import java.util.*;

import javax.media.*;

/**
 * In progress.
 *
 * @author Ken Larson
 * @deprecated Don't use this unless you really have to.
 *
 */
@Deprecated
public abstract class BasicPlugIn implements PlugIn
{
    // TODO: what is the "native" stuff all about?
    // the native value seems to be a native pointer cast to a long.
    // the data for the Buffer is still apparently a java object like byte[],
    // Is it possible that getInputData(Buffer inBuffer) and inBuffer.getData
    // return different values if it is a "native" buffer?
    // the JFFMPEG code seems to imply this.
    // Sun does appear to have a subclass of Buffer, ExtBuffer. ExtBuffer uses
    // something
    // called NBA, which appears to require a native library to instantiate.
    // for now, this class will not support native buffers.
    // see
    // http://archives.java.sun.com/cgi-bin/wa?A2=ind0105&L=jmf-interest&D=1&F=P&S=&m=6503&P=26257

    public static Class<?> getClassForName(String className)
            throws ClassNotFoundException
    {
        return Class.forName(className); // not sure why this method exists.
                                         // Maybe there
                                         // is a special classloader?
    }

    public static Format matches(Format in, Format[] outs)
    {
        for (int i = 0; i < outs.length; ++i)
        {
            if (in.matches(outs[i]))
                return outs[i];
        }
        return null;
    }

    public static boolean plugInExists(String name, int type)
    {
        if (name == null)
            throw new NullPointerException();

        final Vector v = PlugInManager.getPlugInList(null, null, type);
        for (int i = 0; i < v.size(); ++i)
        {
            final String s = (String) v.get(i);
            if (name.equals(s))
                return true;
        }
        return false;
    }

    protected Object[] controls = new Object[0];

    public BasicPlugIn()
    {
        super();
    }

    public abstract void close();

    protected void error()
    {
        throw new RuntimeException(getClass().getName() + " PlugIn error");
    }

    public Object getControl(String controlType)
    {
        final Class<?> clazz;
        try
        {
            clazz = Class.forName(controlType);
        } catch (Exception e)
        {
            return null;
        }

        final Object[] controls = getControls(); // we cannot access
                                                 // this.controls directly,
                                                 // because subclass may have
                                                 // overridden getControls

        for (int i = 0; i < controls.length; ++i)
        {
            final Object control = controls[i];
            if (clazz.isInstance(control))
                return control;
        }

        return null;
    }

    public Object[] getControls()
    {
        return controls;
    }

    protected Object getInputData(Buffer inBuffer)
    {
        return inBuffer.getData(); // TODO: does this do something special for
                                   // native data?
    }

    public abstract String getName();

    protected final long getNativeData(Object data)
    {
        return 0; // TODO
    }

    protected Object getOutputData(Buffer buffer)
    {
        return buffer.getData(); // TODO: does this do something special for
                                 // native data?
    }

    public abstract void open() throws ResourceUnavailableException;

    public abstract void reset();

    protected byte[] validateByteArraySize(Buffer buffer, int newSize)
    {
        final Object data = buffer.getData();

        // see if it is the right type, cast it if it is:
        final byte[] dataCast;
        if (data != null && data.getClass() == byte[].class)
            dataCast = (byte[]) data;
        else
            dataCast = null;

        if (dataCast != null && dataCast.length >= newSize)
        {
            // existing data is right type and long enough
            return dataCast;
        } else
        { // reallocate
            final byte[] newData = new byte[newSize];

            if (dataCast != null)
            { // copy existing data
                System.arraycopy(dataCast, 0, newData, 0, dataCast.length);
            }

            buffer.setData(newData);
            return newData;
        }

    }

    protected Object validateData(Buffer buffer, int length, boolean allowNative)
    {
        // TODO: allowNative?
        final Class<?> dataType = buffer.getFormat().getDataType();
        if (dataType == Format.byteArray)
            return validateByteArraySize(buffer, length);
        else if (dataType == Format.shortArray)
            return validateShortArraySize(buffer, length);
        else if (dataType == Format.intArray)
            return validateIntArraySize(buffer, length);
        else
            return null;

    }

    protected int[] validateIntArraySize(Buffer buffer, int newSize)
    {
        final Object data = buffer.getData();

        // see if it is the right type, cast it if it is:
        final int[] dataCast;
        if (data != null && data.getClass() == int[].class)
            dataCast = (int[]) data;
        else
            dataCast = null;

        if (dataCast != null && dataCast.length >= newSize)
        {
            // existing data is right type and long enough
            return dataCast;
        } else
        { // reallocate
            final int[] newData = new int[newSize];

            if (dataCast != null)
            { // copy existing data
                System.arraycopy(dataCast, 0, newData, 0, dataCast.length);
            }

            buffer.setData(newData);
            return newData;
        }
    }

    protected short[] validateShortArraySize(Buffer buffer, int newSize)
    {
        final Object data = buffer.getData();

        // see if it is the right type, cast it if it is:
        final short[] dataCast;
        if (data != null && data.getClass() == short[].class)
            dataCast = (short[]) data;
        else
            dataCast = null;

        if (dataCast != null && dataCast.length >= newSize)
        {
            // existing data is right type and long enough
            return dataCast;
        } else
        { // reallocate
            final short[] newData = new short[newSize];

            if (dataCast != null)
            { // copy existing data
                System.arraycopy(dataCast, 0, newData, 0, dataCast.length);
            }

            buffer.setData(newData);
            return newData;
        }
    }

}
