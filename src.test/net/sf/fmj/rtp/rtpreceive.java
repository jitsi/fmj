package net.sf.fmj.rtp;

import java.awt.*;
import java.net.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.swing.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.rtp.*;

public class rtpreceive implements SessionListener, ReceiveStreamListener
{
    private static BitRateControl bitratecontrol = null;
    private static RTPSourceStream rtpDataStream = null;
    private static FrameProcessingControl fpc = null;
    private static FrameRateControl frc = null;

    private static boolean isVideo = false;

    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println("Usage: rtpreceive <targetIP> <targetPort>");
            System.exit(0);
        }

        try
        {
            RegistryDefaults.setDefaultFlags(RegistryDefaults.FMJ);

            // create a clean registry
            RegistryDefaults.unRegisterAll(RegistryDefaults.ALL);
            RegistryDefaults.registerAll(RegistryDefaults.FMJ);
            PlugInManager.commit();

            rtpreceive listener = new rtpreceive();

            javax.media.rtp.RTPManager rtpManager = javax.media.rtp.RTPManager
                    .newInstance();
            rtpManager.addSessionListener(listener);
            rtpManager.addReceiveStreamListener(listener);

            javax.media.rtp.SessionAddress local = new javax.media.rtp.SessionAddress(
                    InetAddress.getLocalHost(), Integer.valueOf(args[1])
                            .intValue());
            javax.media.rtp.SessionAddress target = new javax.media.rtp.SessionAddress(
                    InetAddress.getByName(args[0]), Integer.valueOf(args[1])
                            .intValue());

            rtpManager.initialize(local);
            rtpManager.addTarget(target);

            System.out.println("\n>>>>>>  WAITING FOR INCOMING RTP STREAMS");
            while (2 > 1)
            {
                Thread.sleep(1000);

                if (null != rtpDataStream)
                {
                    String aString = "";

                    if (!isVideo)
                    {
                        aString += "rtp audio receive: ";
                    } else
                    {
                        aString += "rtp video receive: ";
                    }

                    if (null != bitratecontrol)
                    {
                        aString += "bitrate=" + bitratecontrol.getBitRate()
                                + " ";
                    }

                    // if ( !isVideo )
                    // {
                    // int jitter = (int)rtpDataStream.getJitter() >> 3;
                    // aString += "jitter=" + jitter + " ms ";
                    // }
                    // else
                    // {
                    // int jitter = (int)rtpDataStream.getJitter() / 90;
                    // aString += "jitter=" + jitter + " ms ";
                    // }

                    if (null != fpc)
                    {
                        aString += "overrun=" + fpc.getFramesDropped() + " ";
                    }

                    if (!isVideo)
                    {
                        // aString += "buffer " +
                        // rtpDataStream.getJitterBufferItemCount() + " / " +
                        // rtpDataStream.getJitterBufferSize() + " ";
                    } else
                    {
                        aString += "fps=" + frc.getFrameRate();
                    }

                    // ReceptionStats stats = rtpDataStream.getReceptionStats();
                    // if ( null != stats )
                    // {
                    // aString += "\n    pdu=" + stats.getPDUProcessed() +
                    // " lost=" + stats.getPDUlost() + " mis=" +
                    // stats.getPDUMisOrd() + " inv=" + stats.getPDUInvalid() +
                    // " dup=" + stats.getPDUDuplicate();
                    // }

                    if (aString.length() > 0)
                    {
                        System.out.println(aString);
                    }
                }
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void update(javax.media.rtp.event.ReceiveStreamEvent event)
    {
        System.out.println("### " + event);

        if (event instanceof NewReceiveStreamEvent)
        {
            try
            {
                ReceiveStream stream = ((NewReceiveStreamEvent) event)
                        .getReceiveStream();
                System.out.println("### stream=" + stream.getClass().getName());

                DataSource ds = stream.getDataSource();
                System.out.println("### ds=" + ds.getClass().getName());

                Object[] controls = ds.getControls();
                for (int i = 0; i < controls.length; i++)
                {
                    if (controls[i] instanceof RTPControl)
                    {
                        Format format = ((RTPControl) controls[i]).getFormat();
                        System.out.println("### format=" + format);

                        if (format instanceof VideoFormat)
                        {
                            isVideo = true;
                        }
                    }
                }

                for (int i = 0; i < controls.length; i++)
                {
                    if (controls[i] instanceof BufferControl)
                    {
                        if (!isVideo)
                        {
                            BufferControl bc = (BufferControl) controls[i];
                            System.out.println("### found BufferControl="
                                    + bc.getClass());

                            System.out.println("### buffer length is "
                                    + bc.getBufferLength());
                            bc.setBufferLength(40 * 8);
                            System.out.println("### buffer length is "
                                    + bc.getBufferLength());
                        }
                    } else if (controls[i] instanceof BitRateControl)
                    {
                        bitratecontrol = (BitRateControl) controls[i];
                        System.out.println("### found BitRateControl="
                                + bitratecontrol.getClass());
                    }
                }

                if (ds instanceof net.sf.fmj.media.protocol.RTPSource)
                {
                    PushBufferStream pbs = ((PushBufferDataSource) ds)
                            .getStreams()[0];
                    if (pbs instanceof RTPSourceStream)
                    {
                        rtpDataStream = (RTPSourceStream) pbs;
                        System.out.println("### rtpDataStream="
                                + rtpDataStream.getClass());
                    }
                }

                Player player = javax.media.Manager.createRealizedPlayer(ds);
                System.out.println("### player=" + player.getClass().getName());

                if (!isVideo)
                {
                    Object[] pcontrols = player.getControls();
                    for (int i = 0; i < pcontrols.length; i++)
                    {
                        if (pcontrols[i] instanceof BufferControl)
                        {
                            BufferControl bc = (BufferControl) pcontrols[i];
                            System.out.println("### found BufferControl="
                                    + bc.getClass());

                            System.out.println("### buffer length is "
                                    + bc.getBufferLength());
                            bc.setBufferLength(1000);
                            System.out.println("### buffer length is "
                                    + bc.getBufferLength());
                        } else if (pcontrols[i] instanceof FrameProcessingControl)
                        {
                            fpc = (FrameProcessingControl) pcontrols[i];
                            System.out
                                    .println("### found FrameProcessingControl="
                                            + fpc.getClass());
                        }
                    }
                } else
                {
                    Object[] pcontrols = player.getControls();
                    for (int i = 0; i < pcontrols.length; i++)
                    {
                        if (pcontrols[i] instanceof FrameRateControl)
                        {
                            frc = (FrameRateControl) pcontrols[i];
                            System.out.println("### found FrameRateControl="
                                    + frc.getClass());
                        }
                    }
                }

                player.start();

                Component vc = player.getVisualComponent();
                if (null != vc)
                {
                    System.out.println("### visual component is " + vc);

                    JFrame aFrame = new JFrame();
                    JPanel aPanel = new JPanel();
                    aPanel.setBounds(0, 0, 176, 144);
                    aPanel.add(vc);
                    aFrame.add(aPanel);

                    aPanel.setBackground(Color.gray);

                    vc.setVisible(true);
                    aPanel.setVisible(true);
                    aFrame.setVisible(true);
                    aFrame.pack();
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void update(javax.media.rtp.event.SessionEvent event)
    {
        System.out.println("### " + event);

        if (event instanceof NewParticipantEvent)
        {
            Participant participant = ((NewParticipantEvent) event)
                    .getParticipant();
            System.out.println("### " + participant.getCNAME()
                    + " joined the session");
        }
    }
}
