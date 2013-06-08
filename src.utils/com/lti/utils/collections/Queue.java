package com.lti.utils.collections;

import java.util.*;

/**
 * Implementation of a FIFO.
 *
 * @author Ken Larson
 */
public class Queue<T>
{
    private List<T> v = new ArrayList<T>();

    public T dequeue()
    {
        // if (v.size() == 0)
        // throw new ArrayIndexOutOfBoundsException("Queue empty");
        final T o = v.get(0);
        v.remove(0);
        return o;
    }

    public void enqueue(T o)
    {
        v.add(o);
    }

    public boolean isEmpty()
    {
        return v.size() == 0;
    }

    public T peek()
    {
        if (v.size() == 0)
            return null;
        return v.get(0);
    }

    public void removeAllElements()
    {
        v.clear();
    }

    public int size()
    {
        return v.size();
    }
}
