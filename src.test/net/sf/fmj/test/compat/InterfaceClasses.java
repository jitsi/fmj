package net.sf.fmj.test.compat;

/**
 * 
 * @author Ken Larson
 * 
 */
public class InterfaceClasses
{
    public static final Class[] ALL = new Class[] {
            javax.media.protocol.BufferTransferHandler.class,
            javax.media.protocol.CachedStream.class,
            javax.media.protocol.CaptureDevice.class,
            javax.media.Clock.class,
            javax.media.Controller.class, // (also extends javax.media.Duration)
            javax.media.Player.class, // (also extends javax.media.MediaHandler)
            javax.media.Processor.class,
            javax.media.Control.class,
            javax.media.control.BitRateControl.class,
            javax.media.control.BufferControl.class,
            javax.media.CachingControl.class,
            javax.media.ExtendedCachingControl.class,
            javax.media.control.FormatControl.class,
            javax.media.control.TrackControl.class, // (also extends
                                                    // javax.media.Controls)
            javax.media.control.FrameGrabbingControl.class,
            javax.media.control.FramePositioningControl.class,
            javax.media.control.FrameProcessingControl.class,
            javax.media.control.FrameRateControl.class,
            javax.media.GainControl.class,
            javax.media.control.H261Control.class,
            javax.media.control.H263Control.class,
            javax.media.control.KeyFrameControl.class,
            javax.media.control.MonitorControl.class,
            javax.media.control.MpegAudioControl.class,
            javax.media.control.PacketSizeControl.class,
            javax.media.control.PortControl.class,
            javax.media.control.QualityControl.class,
            javax.media.rtp.RTPControl.class,
            javax.media.control.RtspControl.class,
            javax.media.control.SilenceSuppressionControl.class,
            javax.media.control.StreamWriterControl.class,
            javax.media.ControllerListener.class,
            javax.media.Controls.class,
            javax.media.protocol.Controls.class,
            javax.media.protocol.SourceStream.class,
            javax.media.protocol.PullBufferStream.class,
            javax.media.protocol.PullSourceStream.class,
            javax.media.protocol.PushBufferStream.class,
            javax.media.protocol.PushSourceStream.class,
            javax.media.DataSink.class, // (also extends
                                        // javax.media.MediaHandler)
            javax.media.PlugIn.class,
            javax.media.Codec.class,
            javax.media.Effect.class,
            javax.media.Demultiplexer.class, // (also extends
                                             // javax.media.Duration,
                                             // javax.media.MediaHandler)
            javax.media.Multiplexer.class,
            javax.media.Renderer.class,
            javax.media.renderer.VideoRenderer.class,
            javax.media.rtp.SessionManager.class,
            javax.media.control.TrackControl.class, // (also extends
                                                    // javax.media.control.FormatControl)
            javax.media.rtp.DataChannel.class,
            javax.media.datasink.DataSinkListener.class,
            javax.media.DownloadProgressListener.class,
            javax.media.Drainable.class,
            javax.media.Duration.class,
            javax.media.Controller.class, // (also extends javax.media.Clock)
            javax.media.Player.class, // (also extends javax.media.MediaHandler)
            javax.media.Processor.class,
            javax.media.Demultiplexer.class, // (also extends
                                             // javax.media.MediaHandler,
                                             // javax.media.PlugIn)
            javax.media.Track.class,
            // java.util.EventListener.class,
            javax.media.rtp.ReceiveStreamListener.class,
            javax.media.rtp.RemoteListener.class,
            javax.media.rtp.SendStreamListener.class,
            javax.media.rtp.SessionListener.class,
            javax.media.rtp.rtcp.Feedback.class,
            javax.media.GainChangeListener.class,
            javax.media.rtp.GlobalReceptionStats.class,
            javax.media.rtp.GlobalTransmissionStats.class,
            javax.media.MediaHandler.class,
            javax.media.DataSink.class, // (also extends javax.media.Controls)
            javax.media.Demultiplexer.class, // (also extends
                                             // javax.media.Duration,
                                             // javax.media.PlugIn)
            javax.media.MediaProxy.class,
            javax.media.DataSinkProxy.class,
            javax.media.Player.class, // (also extends javax.media.Controller)
            javax.media.Processor.class,
            javax.media.rtp.OutputDataStream.class, javax.media.Owned.class,
            javax.media.rtp.Participant.class,
            javax.media.rtp.LocalParticipant.class,
            javax.media.rtp.RemoteParticipant.class,
            javax.media.protocol.Positionable.class,
            javax.media.Prefetchable.class,
            javax.media.protocol.RateConfiguration.class,
            javax.media.protocol.RateConfigureable.class,
            javax.media.rtp.ReceptionStats.class,
            javax.media.rtp.rtcp.Report.class,
            javax.media.rtp.rtcp.ReceiverReport.class,
            javax.media.rtp.rtcp.SenderReport.class,
            javax.media.rtp.RTPConnector.class,
            javax.media.rtp.RTPStream.class,
            javax.media.rtp.ReceiveStream.class,
            javax.media.rtp.SendStream.class,
            javax.media.protocol.Seekable.class,
            javax.media.protocol.SourceCloneable.class,
            javax.media.protocol.SourceTransferHandler.class,
            javax.media.TimeBase.class, javax.media.TrackListener.class,
            javax.media.rtp.TransmissionStats.class,
            javax.media.renderer.VisualContainer.class };
}
