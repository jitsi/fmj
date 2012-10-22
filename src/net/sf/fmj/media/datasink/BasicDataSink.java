package net.sf.fmj.media.datasink;

import java.util.*;

import javax.media.datasink.*;

public abstract class BasicDataSink implements javax.media.DataSink
{
    protected Vector listeners = new Vector(1);

    public void addDataSinkListener(DataSinkListener dsl)
    {
        if (dsl != null)
            if (!listeners.contains(dsl))
                listeners.addElement(dsl);
    }

    protected void removeAllListeners()
    {
        listeners.removeAllElements();
    }

    public void removeDataSinkListener(DataSinkListener dsl)
    {
        if (dsl != null)
            listeners.removeElement(dsl);
    }

    protected final void sendDataSinkErrorEvent(String reason)
    {
        sendEvent(new DataSinkErrorEvent(this, reason));
    }

    protected final void sendEndofStreamEvent()
    {
        sendEvent(new EndOfStreamEvent(this));
    }

    protected void sendEvent(DataSinkEvent event)
    {
        if (!listeners.isEmpty())
        {
            synchronized (listeners)
            {
                Enumeration list = listeners.elements();
                while (list.hasMoreElements())
                {
                    DataSinkListener listener = (DataSinkListener) list
                            .nextElement();
                    listener.dataSinkUpdate(event);
                }
            }
        }
    }
}
