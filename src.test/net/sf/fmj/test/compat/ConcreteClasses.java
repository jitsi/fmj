package net.sf.fmj.test.compat;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ConcreteClasses
{
    public static final Class[] ALL = new Class[] {

            javax.media.Buffer.class,
            javax.media.util.BufferToImage.class,
            javax.media.CaptureDeviceInfo.class, // (implements
                                                 // java.io.Serializable)
            javax.media.CaptureDeviceManager.class,
            // java.awt.Component.class, // (implements
            // java.awt.image.ImageObserver.class, java.awt.MenuContainer.class,
            // java.io.Serializable)
            // java.awt.Container.class,
            javax.media.bean.playerbean.MediaPlayer.class, // (implements
                                                           // java.io.Externalizable.class,
                                                           // javax.media.Player)
            // java.awt.Panel.class,
            javax.media.bean.playerbean.MediaPlayerMediaLocationEditor.class, // (implements
                                                                              // java.awt.event.ActionListener.class,
                                                                              // java.awt.event.ItemListener.class,
                                                                              // java.beans.PropertyEditor)
            // java.awt.Window.class,
            // java.awt.Dialog.class,
            javax.media.bean.playerbean.MediaPlayerRTPDialog.class,
            javax.media.ControllerAdapter.class, // (implements
                                                 // javax.media.ControllerListener.class,
                                                 // java.util.EventListener)
            javax.media.protocol.DataSource.class, // (implements
                                                   // javax.media.protocol.Controls.class,
                                                   // javax.media.Duration)
            javax.media.protocol.PullBufferDataSource.class,
            javax.media.protocol.PullDataSource.class,
            javax.media.protocol.URLDataSource.class,
            javax.media.protocol.PushBufferDataSource.class,
            javax.media.protocol.PushDataSource.class,
            javax.media.rtp.RTPPushDataSource.class,
            javax.media.rtp.RTPSocket.class, // (implements
                                             // javax.media.rtp.DataChannel)
            javax.media.rtp.EncryptionInfo.class, // (implements
                                                  // java.io.Serializable)
            // java.util.EventObject.class, // (implements java.io.Serializable)
            javax.media.MediaEvent.class,
            javax.media.ControllerEvent.class,
            javax.media.AudioDeviceUnavailableEvent.class,
            javax.media.CachingControlEvent.class,
            javax.media.ControllerClosedEvent.class,
            javax.media.ControllerErrorEvent.class,
            javax.media.ConnectionErrorEvent.class,
            javax.media.InternalErrorEvent.class,
            javax.media.ResourceUnavailableEvent.class,
            javax.media.DataLostErrorEvent.class,
            javax.media.DurationUpdateEvent.class,
            javax.media.format.FormatChangeEvent.class,
            javax.media.SizeChangeEvent.class,
            javax.media.MediaTimeSetEvent.class,
            javax.media.RateChangeEvent.class,
            javax.media.StopTimeChangeEvent.class,
            javax.media.TransitionEvent.class,
            javax.media.ConfigureCompleteEvent.class,
            javax.media.PrefetchCompleteEvent.class,
            javax.media.RealizeCompleteEvent.class,
            javax.media.StartEvent.class,
            javax.media.StopEvent.class,
            javax.media.DataStarvedEvent.class,
            javax.media.DeallocateEvent.class,
            javax.media.EndOfMediaEvent.class,
            javax.media.RestartingEvent.class,
            javax.media.StopAtTimeEvent.class,
            javax.media.StopByRequestEvent.class,
            javax.media.datasink.DataSinkEvent.class,
            javax.media.datasink.DataSinkErrorEvent.class,
            javax.media.datasink.EndOfStreamEvent.class,
            javax.media.GainChangeEvent.class,
            javax.media.rtp.event.RTPEvent.class,
            javax.media.rtp.event.ReceiveStreamEvent.class,
            javax.media.rtp.event.ActiveReceiveStreamEvent.class,
            javax.media.rtp.event.ApplicationEvent.class,
            javax.media.rtp.event.InactiveReceiveStreamEvent.class,
            javax.media.rtp.event.NewReceiveStreamEvent.class,
            javax.media.rtp.event.RemotePayloadChangeEvent.class,
            javax.media.rtp.event.StreamMappedEvent.class,
            javax.media.rtp.event.TimeoutEvent.class,
            javax.media.rtp.event.ByeEvent.class,
            javax.media.rtp.event.RemoteEvent.class,
            javax.media.rtp.event.ReceiverReportEvent.class,
            javax.media.rtp.event.RemoteCollisionEvent.class,
            javax.media.rtp.event.SenderReportEvent.class,
            javax.media.rtp.event.SendStreamEvent.class,
            javax.media.rtp.event.ActiveSendStreamEvent.class,
            javax.media.rtp.event.InactiveSendStreamEvent.class,
            javax.media.rtp.event.LocalPayloadChangeEvent.class,
            javax.media.rtp.event.NewSendStreamEvent.class,
            javax.media.rtp.event.StreamClosedEvent.class,
            javax.media.rtp.event.SessionEvent.class,
            javax.media.rtp.event.LocalCollisionEvent.class,
            javax.media.rtp.event.NewParticipantEvent.class,
            javax.media.Format.class, // (implements java.lang.Cloneable.class,
                                      // java.io.Serializable)
            javax.media.format.AudioFormat.class,
            javax.media.protocol.ContentDescriptor.class,
            javax.media.protocol.FileTypeDescriptor.class,
            javax.media.format.VideoFormat.class,
            javax.media.format.H261Format.class,
            javax.media.format.H263Format.class,
            javax.media.format.IndexedColorFormat.class,
            javax.media.format.JPEGFormat.class,
            javax.media.format.RGBFormat.class,
            javax.media.format.YUVFormat.class,
            javax.media.util.ImageToBuffer.class,
            javax.media.protocol.InputSourceStream.class, // (implements
                                                          // javax.media.protocol.PullSourceStream)
            javax.media.Manager.class,
            javax.media.MediaLocator.class, // (implements java.io.Serializable)
            javax.media.bean.playerbean.MediaPlayerResource.class,
            javax.media.PackageManager.class,
            javax.media.PlugInManager.class,
            javax.media.ProcessorModel.class,
            // java.beans.PropertyEditorSupport.class, // (implements
            // java.beans.PropertyEditor)
            javax.media.bean.playerbean.MediaPlayerVolumePropertyEditor.class,
            javax.media.protocol.RateRange.class, // (implements
                                                  // java.io.Serializable)
            // java.util.ResourceBundle.class,
            // java.util.ListResourceBundle.class,
            javax.media.bean.playerbean.MediaPlayerInfoResBundle_en_US.class,
            javax.media.rtp.RTPHeader.class, // (implements
                                             // java.io.Serializable)
            javax.media.rtp.RTPManager.class, // (implements
                                              // javax.media.Controls)
            javax.media.rtp.SessionAddress.class, // (implements
                                                  // java.io.Serializable)
            // java.beans.SimpleBeanInfo.class, // (implements
            // java.beans.BeanInfo)
            javax.media.bean.playerbean.MediaPlayerBeanInfo.class,
            javax.media.rtp.rtcp.SourceDescription.class, // (implements
                                                          // java.io.Serializable)
            javax.media.SystemTimeBase.class, // (implements
                                              // javax.media.TimeBase)
            // java.lang.Throwable.class, // (implements java.io.Serializable)
            // java.lang.Error.class,
            javax.media.MediaError.class,
            javax.media.ClockStartedError.class,
            javax.media.NotConfiguredError.class,
            javax.media.NotPrefetchedError.class,
            javax.media.NotRealizedError.class,
            javax.media.StopTimeSetError.class,
            // java.lang.Exception.class,
            javax.media.MediaException.class,
            javax.media.BadHeaderException.class,
            javax.media.CannotRealizeException.class,
            javax.media.ClockStoppedException.class,
            javax.media.IncompatibleSourceException.class,
            javax.media.IncompatibleTimeBaseException.class,
            javax.media.NoDataSinkException.class,
            javax.media.NoDataSourceException.class,
            javax.media.NoPlayerException.class,
            javax.media.NoProcessorException.class,
            javax.media.ResourceUnavailableException.class,
            javax.media.format.UnsupportedFormatException.class,
            javax.media.UnsupportedPlugInException.class,
            javax.media.rtp.SessionManagerException.class,
            javax.media.rtp.InvalidSessionAddressException.class,
            javax.media.rtp.SSRCInUseException.class, javax.media.Time.class, // (implements
                                                                              // java.io.Serializable)

    // // classes not in javadoc:
    // javax.media.pm.PackageManager.class,
    // javax.media.cdm.CaptureDeviceManager.class,
    // javax.media.pim.PlugInManager.class,
    };

}
