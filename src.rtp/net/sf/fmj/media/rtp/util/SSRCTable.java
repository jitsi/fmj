package net.sf.fmj.media.rtp.util;

import java.util.*;

public class SSRCTable
{
    static final int INCR = 16;
    int ssrcList[];
    Object objList[];
    int total;

    public SSRCTable()
    {
        ssrcList = new int[16];
        objList = new Object[16];
        total = 0;
    }

    public synchronized Enumeration elements()
    {
        return new Enumeration()
        {
            int next;

            {
                next = 0;
            }

            public boolean hasMoreElements()
            {
                return next < total;
            }

            public Object nextElement()
            {
                synchronized (SSRCTable.this)
                {
                    if (next < total)
                    {
                        Object obj = objList[next++];
                        return obj;
                    }
                }
                throw new NoSuchElementException("SSRCTable Enumeration");
            }
        };
    }

    public synchronized Object get(int ssrc)
    {
        int i;
        if ((i = indexOf(ssrc)) < 0)
            return null;
        else
            return objList[i];
    }

    public synchronized int getSSRC(Object obj)
    {
        for (int i = 0; i < total; i++)
            if (objList[i] == obj)
                return ssrcList[i];

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
        return total == 0;
    }

    public synchronized void put(int ssrc, Object obj)
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

        if (total == ssrcList.length)
        {
            int sl[] = new int[ssrcList.length + 16];
            Object ol[] = new Object[objList.length + 16];
            if (i > 0)
            {
                System.arraycopy(ssrcList, 0, sl, 0, total);
                System.arraycopy((objList), 0, (ol), 0, total);
            }
            ssrcList = sl;
            objList = ol;
        }
        for (int x = total - 1; x >= i; x--)
        {
            ssrcList[x + 1] = ssrcList[x];
            objList[x + 1] = objList[x];
        }

        ssrcList[i] = ssrc;
        objList[i] = obj;
        total++;
    }

    public synchronized Object remove(int ssrc)
    {
        int i;
        if ((i = indexOf(ssrc)) < 0)
            return null;
        Object res = objList[i];
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

    public synchronized void removeObj(Object obj)
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
}
