package net.sf.fmj.media;

import java.util.*;

import javax.media.*;
import javax.media.datasink.*;

/**
 * Abstract base class to implement DataSink.
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractDataSink implements DataSink
{
    private final List listeners = new ArrayList(); // of DataSinkListener

    protected MediaLocator outputLocator;

    public void addDataSinkListener(DataSinkListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    public MediaLocator getOutputLocator()
    {
        return outputLocator;
    }

    protected void notifyDataSinkListeners(DataSinkEvent event)
    {
        final List listenersCopy = new ArrayList();

        synchronized (listeners)
        {
            listenersCopy.addAll(listeners);
        }

        for (int i = 0; i < listenersCopy.size(); ++i)
        {
            DataSinkListener listener = (DataSinkListener) listenersCopy.get(i);
            listener.dataSinkUpdate(event);
        }
    }

    public void removeDataSinkListener(DataSinkListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    public void setOutputLocator(MediaLocator output)
    {
        this.outputLocator = output;
    }

}
