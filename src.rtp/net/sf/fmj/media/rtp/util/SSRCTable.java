package net.sf.fmj.media.rtp.util;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author Lyubomir Marinov
 */
public class SSRCTable<T>
{
    static final int INCR = 16;

    Object[] objList;
    int[] ssrcList;
    int total;

    public SSRCTable()
    {
        ssrcList = new int[16];
        objList = new Object[ssrcList.length];
        total = 0;
    }

    public Enumeration<T> elements()
    {
        // The method does not access any state of this instance. Consequently,
        // it does not need to be synchronized. The synchronized keyword was
        // removed because the method was seen to participate in a deadlock and
        // because the presence or absence of the synchronized keyword will not
        // make a difference if this SSRCTable is modified while iterating over
        // the returned Enumeration anyway.
        return
            new Enumeration<T>()
            {
                private int next = 0;

                @Override
                public boolean hasMoreElements()
                {
                    return next < total;
                }

                @Override
                public T nextElement()
                {
                    synchronized (SSRCTable.this)
                    {
                        if (next < total)
                        {
                            @SuppressWarnings("unchecked")
                            T t = (T) objList[next++];
    
                            return t;
                        }
                    }
                    throw new NoSuchElementException("SSRCTable Enumeration");
                }
            };
    }

    public synchronized T get(int ssrc)
    {
        int i = indexOf(ssrc);

        if (i < 0)
        {
            return null;
        }
        else
        {
            @SuppressWarnings("unchecked")
            T t = (T) objList[i];

            return t;
        }
    }

    public synchronized int getSSRC(T obj)
    {
        for (int i = 0; i < total; i++)
        {
            if (objList[i] == obj)
                return ssrcList[i];
        }
        return 0;
    }

    private int indexOf(int ssrc)
    {
        if (total <= 3)
        {
            if (total > 0 && ssrcList[0] == ssrc)
                return 0;
            if (total > 1 && ssrcList[1] == ssrc)
                return 1;
            return total <= 2 || ssrcList[2] != ssrc ? -1 : 2;
        }
        if (ssrcList[0] == ssrc)
            return 0;
        if (ssrcList[total - 1] == ssrc)
            return total - 1;
        int i = 0;
        int j = total - 1;
        do
        {
            int x = (j - i) / 2 + i;
            if (ssrcList[x] == ssrc)
                return x;
            if (ssrc > ssrcList[x])
                i = x + 1;
            else if (ssrc < ssrcList[x])
                j = x;
        } while (i < j);
        return -1;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Gets the synchronization source identifiers (SSRCs) which are the keys
     * associated with values in this <tt>SSRCTable</tt>.
     * <p>
     * The method name is inspired by {@link Map#keys()} and
     * {@link Collection#toArray(Object[])}.
     * </p>
     *
     * @param array the array into which the SSRCs associated with
     * <tt>Object</tt>s in this <tt>SSRCTable</tt> are to be returned if the
     * <tt>length</tt> of <tt>array</tt> is greater than or equal to the number
     * of the SSRCs in question
     * @return the SSRCs associated with <tt>Object</tt>s in this
     * <tt>SSRCTable</tt>. If the number of SSRCs is less than or equal to the
     * <tt>length</tt> of <tt>array</tt>, the SSRCs are written into
     * <tt>array</tt> and <tt>array</tt> is returned. Otherwise, a new array is
     * allocated.
     */
    public synchronized int[] keysToArray(int[] array)
    {
        int length = size();

        if (array == null || array.length < length)
            array = new int[length];
        System.arraycopy(ssrcList, 0, array, 0, length);
        if (length < array.length)
            Arrays.fill(array, length, array.length, 0);
        return array;
    }

    public synchronized void put(int ssrc, T obj)
    {
        if (total == 0)
        {
            ssrcList[0] = ssrc;
            objList[0] = obj;
            total = 1;
            return;
        }
        int i;
        for (i = 0; i < total; i++)
        {
            if (ssrcList[i] < ssrc)
                continue;
            if (ssrcList[i] == ssrc)
            {
                objList[i] = obj;
                return;
            }
            break;
        }

        int[] sl = ssrcList;
        Object[] ol = objList;
        if (total == ssrcList.length)
        {
            sl = new int[ssrcList.length + INCR];
            ol = new Object[objList.length + INCR];
        }

        if (ssrcList != sl && i > 0)
        {
            System.arraycopy(ssrcList, 0, sl, 0, i);
            System.arraycopy(objList, 0, ol, 0, i);
        }

        if (i < total)
        {
            System.arraycopy(ssrcList, i, sl, i+1, total-i);
            System.arraycopy(objList, i, ol, i+1, total-i);
        }

        ssrcList = sl;
        objList = ol;

        ssrcList[i] = ssrc;
        objList[i] = obj;
        total++;
    }

    public synchronized T remove(int ssrc)
    {
        int i;
        if ((i = indexOf(ssrc)) < 0)
            return null;

        @SuppressWarnings("unchecked")
        T res = (T) objList[i];

        for (; i < total - 1; i++)
        {
            ssrcList[i] = ssrcList[i + 1];
            objList[i] = objList[i + 1];
        }

        ssrcList[total - 1] = 0;
        objList[total - 1] = null;
        total--;
        return res;
    }

    public synchronized void removeAll()
    {
        for (int i = 0; i < total; i++)
        {
            ssrcList[i] = 0;
            objList[i] = null;
        }

        total = 0;
    }

    public synchronized void removeObj(T obj)
    {
        if (obj == null)
            return;
        int i;
        for (i = 0; i < total; i++)
            if (objList[i] == obj)
                break;

        if (i >= total)
            return;
        for (; i < total - 1; i++)
        {
            ssrcList[i] = ssrcList[i + 1];
            objList[i] = objList[i + 1];
        }

        ssrcList[total - 1] = 0;
        objList[total - 1] = null;
        total--;
    }

    public int size()
    {
        return total;
    }

    /**
     * Gets the <tt>Object</tt>s which are the values associated with
     * synchronization source identifiers (SSRCs) in this <tt>SSRCTable</tt>.
     * <p>
     * The method name is inspired by {@link Map#values()} and
     * {@link Collection#toArray(Object[])}.
     * </p>
     *
     * @param array the array into which the <tt>Object</tt>s associated with
     * SSRCs in this <tt>SSRCTable</tt> are to be returned if the <tt>length</tt>
     * of <tt>array</tt> is greater than or equal to the number of the
     * <tt>Object</tt>s in question
     * @return the <tt>Object</tt>s associated with SSRCs in this
     * <tt>SSRCTable</tt>. If the number of <tt>Object</tt>s is less than or
     * equal to the <tt>length</tt> of <tt>array</tt>, the <tt>Object</tt>s are
     * written into <tt>array</tt> and <tt>array</tt> is returned. Otherwise, a
     * new array is allocated.
     */
    @SuppressWarnings("unchecked")
    public synchronized T[] valuesToArray(T[] array)
    {
        Class<?> componentType;
        int length = size();

        if (array == null)
            componentType = Object.class;
        else if (array.length < length)
            componentType = array.getClass().getComponentType();
        else
            componentType = null;
        if (componentType != null)
            array = (T[]) Array.newInstance(componentType, length);
        System.arraycopy(objList, 0, array, 0, length);
        if (length < array.length)
            Arrays.fill(array, length, array.length, null);
        return array;
    }
}
