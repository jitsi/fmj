package net.sf.fmj.media.rtp;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

public class BufferControlImpl implements BufferControl
{
    private class BufferControlPanel extends Panel
    {
        Button bb;
        Choice bchoice;
        TextField bsize;
        TextField btext;
        Panel buffersize;
        Button tb;
        Choice tchoice;
        Panel threshold;
        TextField tsize;
        TextField ttext;

        public BufferControlPanel()
        {
            super(new BorderLayout());
            buffersize = null;
            threshold = null;
            btext = null;
            bchoice = null;
            tchoice = null;
            ttext = null;
            tb = null;
            buffersize = new Panel(new FlowLayout());
            buffersize.add(new Label("BufferSize"));
            bsize = new TextField(15);
            updateBuffer(getBufferLength());
            bsize.setEnabled(false);
            buffersize.add(bsize);
            buffersize.add(new Label("Update"));
            buffersize.add(bchoice = new Choice());
            bchoice.add("DEFAULT");
            bchoice.add("MAX");
            bchoice.add("User Defined");
            bchoice.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if (e.getItem().equals("User Defined"))
                        btext.setEnabled(true);
                    else
                        btext.setEnabled(false);
                }

            });
            buffersize.add(new Label("If User Defined, Enter here:"));
            buffersize.add(btext = new TextField(10));
            btext.setEnabled(false);
            buffersize.add(bb = new Button("Commit"));
            bb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    buffersizeUpdate();
                }

            });
            threshold = new Panel(new FlowLayout());
            threshold.add(new Label("Threshold"));
            tsize = new TextField(15);
            updateThreshold(getMinimumThreshold());
            tsize.setEnabled(false);
            threshold.add(tsize);
            threshold.add(new Label("Update"));
            threshold.add(tchoice = new Choice());
            tchoice.add("DEFAULT");
            tchoice.add("MAX");
            tchoice.add("User Defined");
            tchoice.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    if (e.getItem().equals("User Defined"))
                        ttext.setEnabled(true);
                    else
                        ttext.setEnabled(false);
                }

            });
            threshold.add(new Label("If User Defined, Enter here:"));
            threshold.add(ttext = new TextField(10));
            ttext.setEnabled(false);
            threshold.add(tb = new Button("Commit"));
            tb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    thresholdUpdate();
                }

            });
            add(buffersize, "North");
            add(new Label(
                    "Actual buffer & threshold sizes (in millisec) not displayed until media type is determined"),
                    "Center");
            add(threshold, "South");
            setVisible(true);
        }

        private void buffersizeUpdate()
        {
            String s = bchoice.getSelectedItem();
            long b = -1L;
            if (s.equals("MAX"))
                b = -2L;
            else if (s.equals("DEFAULT"))
            {
                b = -1L;
            } else
            {
                s = btext.getText();
                b = Long.parseLong(s);
            }
            b = setBufferLength(b);
            updateBuffer(b);
        }

        private void thresholdUpdate()
        {
            String s = tchoice.getSelectedItem();
            long t = -1L;
            if (s.equals("DEFAULT"))
                t = -1L;
            else if (s.equals("MAX"))
            {
                t = -2L;
            } else
            {
                s = ttext.getText();
                t = Long.parseLong(s);
            }
            t = setMinimumThreshold(t);
            updateThreshold(t);
        }

        public void updateBuffer(long b)
        {
            if (b != 0x7fffffffL && b != -2L && b != -1L)
                bsize.setText(Long.toString(b));
        }

        public void updateThreshold(long d)
        {
            if (d != 0x7fffffffL && d != -2L && d != -1L)
                tsize.setText(Long.toString(d));
        }
    }

    private static final int AUDIO_DEFAULT_BUFFER = 250;
    private static final int AUDIO_DEFAULT_THRESHOLD = AUDIO_DEFAULT_BUFFER / 2;
    private static final int AUDIO_MAX_BUFFER = 4000;
    private static final int AUDIO_MAX_THRESHOLD = AUDIO_MAX_BUFFER / 2;
    private static final int NOT_SPECIFIED = 0x7fffffff;
    private static final int VIDEO_DEFAULT_BUFFER = 135;
    private static final int VIDEO_DEFAULT_THRESHOLD = 0;
    private static final int VIDEO_MAX_BUFFER = 4000;
    private static final int VIDEO_MAX_THRESHOLD = 0;

    BufferControlPanel controlComp = null;
    private long currBuffer = NOT_SPECIFIED;
    private long currThreshold = NOT_SPECIFIED;
    private long defBuffer = NOT_SPECIFIED;
    private long defThreshold = NOT_SPECIFIED;
    private boolean inited = false;
    private long maxBuffer = NOT_SPECIFIED;
    private long maxThreshold = NOT_SPECIFIED;
    private Vector<RTPSourceStream> sourcestreamlist
        = new Vector<RTPSourceStream>(1);
    boolean threshold_enabled = true;

    public BufferControlImpl()
    {
    }

    protected void addSourceStream(RTPSourceStream s)
    {
        sourcestreamlist.addElement(s);
        s.setBufferControl(this);
    }

    public long getBufferLength()
    {
        return currBuffer;
    }

    public Component getControlComponent()
    {
        if (controlComp == null)
            controlComp = new BufferControlPanel();
        return controlComp;
    }

    public boolean getEnabledThreshold()
    {
        return threshold_enabled;
    }

    public long getMinimumThreshold()
    {
        return currThreshold;
    }

    protected void initBufferControl(Format f)
    {
        if (f instanceof AudioFormat)
        {
            defBuffer = (defBuffer != NOT_SPECIFIED)
                    ? currBuffer : AUDIO_DEFAULT_BUFFER;
            defThreshold = (defThreshold != NOT_SPECIFIED)
                    ? currThreshold : AUDIO_DEFAULT_THRESHOLD;
            maxBuffer = (maxBuffer != NOT_SPECIFIED)
                    ? maxBuffer : AUDIO_MAX_BUFFER;
            maxThreshold = (maxThreshold != NOT_SPECIFIED)
                    ? maxThreshold : AUDIO_MAX_THRESHOLD;
            currBuffer = (currBuffer != NOT_SPECIFIED)
                    ? currBuffer : defBuffer;
            currThreshold = (currThreshold != NOT_SPECIFIED)
                    ? currThreshold : defThreshold;
        }
        else if (f instanceof VideoFormat)
        {
            defBuffer = (defBuffer != NOT_SPECIFIED)
                    ? currBuffer : VIDEO_DEFAULT_BUFFER;
            defThreshold = (defThreshold != NOT_SPECIFIED)
                    ? currThreshold : VIDEO_DEFAULT_THRESHOLD;
            maxBuffer = (maxBuffer != NOT_SPECIFIED)
                    ? maxBuffer : VIDEO_MAX_BUFFER;
            maxThreshold = (maxThreshold != NOT_SPECIFIED)
                    ? maxThreshold : VIDEO_MAX_THRESHOLD;
            currBuffer = (currBuffer != NOT_SPECIFIED)
                    ? currBuffer : defBuffer;
            currThreshold = (currThreshold != NOT_SPECIFIED)
                    ? currThreshold : defThreshold;
        }
        if (currBuffer == MAX_VALUE)
            currBuffer = maxBuffer;
        if (currBuffer == DEFAULT_VALUE)
            currBuffer = defBuffer;
        if (currThreshold == MAX_VALUE)
            currThreshold = maxThreshold;
        if (currThreshold == DEFAULT_VALUE)
            currThreshold = defThreshold;
        if (controlComp != null)
        {
            controlComp.updateBuffer(currBuffer);
            controlComp.updateThreshold(currThreshold);
        }
        inited = true;
    }

    protected void removeSourceStream(RTPSourceStream s)
    {
        sourcestreamlist.removeElement(s);
    }

    public long setBufferLength(long time)
    {
        if (!inited)
        {
            currBuffer = time;
            return time;
        }
        if (time == DEFAULT_VALUE)
            time = defBuffer;
        if (time == MAX_VALUE)
            time = maxBuffer;
        if (time < currThreshold)
            return currBuffer;
        if (time >= maxBuffer)
            currBuffer = maxBuffer;
        else if (time <= 0L || time == defBuffer)
            currBuffer = defBuffer;
        else
            currBuffer = time;
        for (int i = 0; i < sourcestreamlist.size(); i++)
            sourcestreamlist.elementAt(i).updateBuffer(currBuffer);

        if (controlComp != null)
            controlComp.updateBuffer(currBuffer);
        return currBuffer;
    }

    public void setEnabledThreshold(boolean b)
    {
        threshold_enabled = b;
    }

    public long setMinimumThreshold(long t)
    {
        if (!inited)
        {
            currThreshold = t;
            return t;
        }
        if (t == DEFAULT_VALUE)
            t = defThreshold;
        if (t == MAX_VALUE)
            t = maxThreshold;
        if (t > currBuffer)
            return currThreshold;
        if (t >= maxThreshold)
            currThreshold = maxThreshold;
        else if (t == defThreshold)
            currThreshold = defThreshold;
        else
            currThreshold = t;
        if (t < 0L)
            currThreshold = 0L;
        for (int i = 0; i < sourcestreamlist.size(); i++)
            sourcestreamlist.elementAt(i).updateThreshold(currThreshold);

        if (controlComp != null)
            controlComp.updateThreshold(currThreshold);
        return currThreshold;
    }
}
