package javax.media.bean.playerbean;

import java.beans.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/bean/playerbean/MediaPlayerBeanInfo.html"
 * target="_blank">this class in the JMF Javadoc</a>. Coding complete.
 *
 * @author Ken Larson
 *
 */
public class MediaPlayerBeanInfo extends java.beans.SimpleBeanInfo
{
    private static PropertyDescriptor buildPropertyDescriptor(Class<?> clazz,
            String name, String displayName, Class<?> propertyEditorClass,
            boolean bound) throws IntrospectionException
    {
        final PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
        pd.setDisplayName(displayName);
        pd.setPropertyEditorClass(propertyEditorClass);
        pd.setBound(bound);
        return pd;
    }

    private final PropertyDescriptor[] propertyDescriptors;

    public MediaPlayerBeanInfo()
    {
        try
        {
            propertyDescriptors = new PropertyDescriptor[] {
                    buildPropertyDescriptor(MediaPlayer.class, "mediaLocation",
                            "media location",
                            MediaPlayerMediaLocationEditor.class, true),
                    buildPropertyDescriptor(MediaPlayer.class,
                            "controlPanelVisible", "show control panel", null,
                            true),
                    buildPropertyDescriptor(MediaPlayer.class,
                            "cachingControlVisible", "show caching control",
                            null, true),
                    buildPropertyDescriptor(MediaPlayer.class,
                            "fixedAspectRatio", "fixedAspectRatio", null, true),
                    buildPropertyDescriptor(MediaPlayer.class, "playbackLoop",
                            "loop", null, true),
                    buildPropertyDescriptor(
                            MediaPlayer.class,
                            "volumeLevel",
                            "volume",
                            javax.media.bean.playerbean.MediaPlayerVolumePropertyEditor.class,
                            true),
                    buildPropertyDescriptor(MediaPlayer.class, "background",
                            "background", null, false),
                    buildPropertyDescriptor(MediaPlayer.class, "foreground",
                            "foreground", null, false),
                    buildPropertyDescriptor(MediaPlayer.class, "font", "font",
                            null, false), };
        } catch (IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
        final BeanDescriptor result = new BeanDescriptor(MediaPlayer.class);
        result.setName("MediaPlayer");
        result.setDisplayName("MediaPlayer Bean");
        result.setShortDescription("MediaPlayer Bean");
        return result;
    }

    @Override
    public int getDefaultPropertyIndex()
    {
        return 1;
    }

    @Override
    public java.beans.EventSetDescriptor[] getEventSetDescriptors()
    {
        throw new java.lang.Error(
                new java.beans.IntrospectionException(
                        "Method \"controllerUpdate\" should have argument \"ControllerUpdateEvent\"")); // TODO
                                                                                                        // -
                                                                                                        // can't
                                                                                                        // get
                                                                                                        // the
                                                                                                        // real
                                                                                                        // one
                                                                                                        // to
                                                                                                        // work.
        // Get:
        // java.lang.Error: java.beans.IntrospectionException: Method
        // "controllerUpdate" should have argument "ControllerUpdateEvent"
        // at
        // javax.media.bean.playerbean.MediaPlayerBeanInfo.getEventSetDescriptors(MediaPlayerBeanInfo.java:150)

        // TODO:
        // http://www.netbeans.org/servlets/ReadMsg?list=nbusers&msgNo=37579

    }

    @Override
    public java.awt.Image getIcon(int ic)
    {
        return null;
    }

    @Override
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
        return propertyDescriptors;
    }

}
