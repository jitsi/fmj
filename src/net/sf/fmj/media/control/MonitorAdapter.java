package net.sf.fmj.media.control;

import java.awt.*;
import java.awt.event.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;

public class MonitorAdapter implements MonitorControl, Owned
{
    protected CodecChain cc = null;
    protected boolean enabled = false;
    protected boolean closed = false;
    protected Component visualComponent = null;
    protected Component controlComponent = null;
    protected Checkbox cbEnabled = null;
    protected Format format = null;
    protected float inFrameRate = 0f;
    protected float previewFrameRate = 30f;
    protected long lastPreviewTime = 0;
    protected long previewInterval = 1000000000L / 30;
    protected MouseListener ml = null;
    protected PopupMenu rateMenu = null;
    protected LoopThread loopThread;

    protected int[] frameRates = { 0, 1, 2, 5, 7, 10, 15, 20, 30, 60, 90 };

    CircularBuffer bufferQ;
    Object owner;

    // For comparing formats.
    static VideoFormat mpegVideo = new VideoFormat(VideoFormat.MPEG_RTP);

    public MonitorAdapter(Format f, Object owner)
    {
        format = f;
        this.owner = owner;
    }

    private void addPopupMenu(Component visual)
    {
        MenuItem mi;
        ActionListener rateSelect;
        visualComponent = visual;
        rateMenu = new PopupMenu("Monitor Rate");

        rateSelect = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                String action = ae.getActionCommand();
                int space = action.indexOf(" ");
                String rateString = action.substring(0, space);
                try
                {
                    int rate = Integer.parseInt(rateString);
                    setPreviewFrameRate(rate);
                } catch (Throwable t)
                {
                    if (t instanceof ThreadDeath)
                        throw (ThreadDeath) t;
                }
            }
        };

        visual.add(rateMenu);
        int lastAdded = 0;

        for (int i = 0; i < frameRates.length; i++)
        {
            if (frameRates[i] < inFrameRate)
            {
                mi = new MenuItem(frameRates[i] + " fps");
                rateMenu.add(mi);
                mi.addActionListener(rateSelect);
                lastAdded = frameRates[i];
            }
        }

        if (lastAdded < inFrameRate)
        {
            mi = new MenuItem(inFrameRate + " fps");
            rateMenu.add(mi);
            mi.addActionListener(rateSelect);
        }

        visual.addMouseListener(ml = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                if (me.isPopupTrigger())
                    rateMenu.show(visualComponent, me.getX(), me.getY());
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                if (me.isPopupTrigger())
                    rateMenu.show(visualComponent, me.getX(), me.getY());
            }

            @Override
            public void mouseReleased(MouseEvent me)
            {
                if (me.isPopupTrigger())
                    rateMenu.show(visualComponent, me.getX(), me.getY());
            }
        });
    }

    public void close()
    {
        if (cc == null)
            return;

        loopThread.kill();
        synchronized (bufferQ)
        {
            closed = true;
            bufferQ.notifyAll();
        }

        cc.close();
        cc = null;
    }

    private Object copyData(Object in)
    {
        if (in instanceof byte[])
        {
            byte[] out = new byte[((byte[]) in).length];
            System.arraycopy(in, 0, out, 0, out.length);
            return out;
        }
        if (in instanceof short[])
        {
            short[] out = new short[((short[]) in).length];
            System.arraycopy(in, 0, out, 0, out.length);
            return out;
        }
        if (in instanceof int[])
        {
            int[] out = new int[((int[]) in).length];
            System.arraycopy(in, 0, out, 0, out.length);
            return out;
        }
        return in;
    }

    public boolean doProcess()
    {
        Buffer buffer;

        // Grab a filled buffer from the queue.
        synchronized (bufferQ)
        {
            while (!bufferQ.canRead() && enabled && !closed)
            {
                try
                {
                    bufferQ.wait();
                } catch (Exception e)
                {
                }
            }

            if (closed)
                return false;
            else if (!enabled)
                return true;

            buffer = bufferQ.read();
        }

        boolean toDisplay = false;

        if (buffer.getFormat() instanceof AudioFormat)
        {
            toDisplay = true;
        } else
        {
            long time = buffer.getTimeStamp();
            if (time >= lastPreviewTime + previewInterval
                    || time <= lastPreviewTime)
            {
                if (mpegVideo.matches(format))
                {
                    /*
                     * Only set toDisplay true on MPEG I frames. This way
                     * VideoCodecChain can treat the stream as a raw format and
                     * avoid overloading the CPU by sending every frame to the
                     * decoder. Downside is it limits the framerate that can be
                     * set for the monitor.
                     */
                    byte[] payload = (byte[]) buffer.getData();
                    int offset = buffer.getOffset();
                    int ptype = payload[offset + 2] & 0x07;
                    if (ptype == 1)
                    {
                        lastPreviewTime = time;
                        toDisplay = true;
                    }
                } else
                {
                    lastPreviewTime = time;
                    toDisplay = true;
                }
            } else
                toDisplay = false;
        }

        // Use the codec chain to process the data.
        cc.process(buffer, toDisplay);

        // Return the processed buffer back to the queue.
        synchronized (bufferQ)
        {
            bufferQ.readReport();
            bufferQ.notifyAll();
        }

        return true;
    }

    @Override
    public void finalize()
    {
        if (visualComponent != null)
        {
            visualComponent.remove(rateMenu);
            visualComponent.removeMouseListener(ml);
        }
    }

    public Component getControlComponent()
    {
        /*
         * if (controlComponent == null) { if (cc != null) visualComponent =
         * getVisualComponent(); if (visualComponent == null) return null;
         * southPanel = new Panel(); listBox = new List(6); for (int i = 0; i <=
         * inFrameRate; i++) { listBox.add(i + " fps"); } southPanel.add(new
         * Label("Monitor Frame Rate : ")); southPanel.add(listBox);
         *
         * preferredSize = visualComponent.getPreferredSize();
         * preferredSize.height += southPanel.getPreferredSize().height;
         *
         * controlComponent = new Panel(new BorderLayout()) { public Dimension
         * getPreferredSize() { return preferredSize; } };
         *
         * controlComponent.add("Center", visualComponent);
         * controlComponent.add("South", southPanel);
         *
         * }
         */

        if (controlComponent != null)
            return controlComponent;

        if (cc == null && !open())
            return null;

        controlComponent = cc.getControlComponent();

        // Create Audio Monitor Panel
        if (format instanceof AudioFormat && controlComponent != null)
        {
            Container controlPanel = new Panel();
            controlPanel.setLayout(new BorderLayout());
            cbEnabled = new Checkbox("Monitor Audio");
            controlPanel.add("West", cbEnabled);
            controlPanel.add("Center", controlComponent);
            controlComponent = controlPanel;
            controlPanel.setBackground(java.awt.Color.lightGray);
        }

        if (format instanceof VideoFormat && controlComponent != null)
        {
            Container controlPanel = new Panel();
            controlPanel.setLayout(new BorderLayout());
            cbEnabled = new Checkbox("Monitor Video");
            controlPanel.add("South", cbEnabled);
            controlPanel.add("Center", controlComponent);
            addPopupMenu(controlComponent);
            controlComponent = controlPanel;
            controlPanel.setBackground(java.awt.Color.lightGray);
        }

        if (cbEnabled != null)
        {
            cbEnabled.setState(isEnabled());
            cbEnabled.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    MonitorAdapter.this.setEnabled(cbEnabled.getState());
                }
            });
        }
        //

        return controlComponent;
    }

    public Object getOwner()
    {
        return owner;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    protected boolean open()
    {
        try
        {
            if (format instanceof VideoFormat)
            {
                VideoFormat vf = (VideoFormat) format;
                cc = new VideoCodecChain(vf);
                inFrameRate = vf.getFrameRate();
                if (inFrameRate < 0) // if unspecified
                    inFrameRate = 30f;
                inFrameRate = ((int) (inFrameRate * 10 + 0.5)) / 10f;
            } else if (format instanceof AudioFormat)
            {
                cc = new AudioCodecChain((AudioFormat) format);
            }

        } catch (UnsupportedFormatException e)
        {
            Log.warning("Failed to initialize the monitor control: " + e);
            return false;
        }

        if (cc == null)
            return false;

        bufferQ = new CircularBuffer(2);
        loopThread = new MonitorThread(this);

        return true;
    }

    public void process(Buffer input)
    {
        if (input == null || previewFrameRate <= 0 || format == null
                || input.isEOM() || input.isDiscard()
                || (input.getFlags() & Buffer.FLAG_FLUSH) != 0)
        {
            return;
        }

        if (!format.matches(input.getFormat()))
        {
            return;
        }

        Buffer buffer = null;

        // Grab an empty buffer from the queue.
        synchronized (bufferQ)
        {
            while (!bufferQ.canWrite() && enabled && !closed)
            {
                try
                {
                    bufferQ.wait();
                } catch (Exception e)
                {
                }
            }

            if (!enabled || closed)
                return;

            buffer = bufferQ.getEmptyBuffer();
        }

        // expensive copy
        buffer.setData(copyData(input.getData()));
        buffer.setFlags(input.getFlags());
        buffer.setFormat(input.getFormat());
        buffer.setSequenceNumber(input.getSequenceNumber());
        buffer.setHeader(input.getHeader());
        buffer.setLength(input.getLength());
        buffer.setOffset(input.getOffset());
        buffer.setRtpTimeStamp(input.getRtpTimeStamp());
        buffer.setHeaderExtension(input.getHeaderExtension());
        buffer.setTimeStamp(input.getTimeStamp());

        // Put the buffer into the queue.
        synchronized (bufferQ)
        {
            bufferQ.writeReport();
            bufferQ.notifyAll();
        }
    }

    public void reset()
    {
        if (cc != null)
            cc.reset();
    }

    public boolean setEnabled(boolean on)
    {
        if (on)
        {
            if (cc == null)
            {
                if (!open())
                    return false;
            } else
                cc.reset();
            if (!cc.prefetch())
                return false;

            // Flush the bufferQ.
            synchronized (bufferQ)
            {
                while (bufferQ.canRead())
                {
                    bufferQ.read();
                    bufferQ.readReport();
                }
            }

            enabled = true;
            loopThread.start();
        } else if (!on && cc != null)
        {
            loopThread.pause();
            // In case we are blocked at the process call.
            synchronized (bufferQ)
            {
                enabled = false;
                bufferQ.notifyAll();
            }
            cc.deallocate();
        }

        return enabled;
    }

    public float setPreviewFrameRate(float value)
    {
        if (value > inFrameRate)
            value = inFrameRate;
        previewFrameRate = value;
        previewInterval = (long) (1E+9 / value);
        return value;
    }

    // ////////////////////////////////
    //
    // Inner class
    // ////////////////////////////////

}

class MonitorThread extends LoopThread
{
    MonitorAdapter ad;

    public MonitorThread(MonitorAdapter ad)
    {
        setName(getName() + " : MonitorAdapter");
        useVideoPriority();
        this.ad = ad;
    }

    @Override
    protected boolean process()
    {
        return ad.doProcess();
    }
}
