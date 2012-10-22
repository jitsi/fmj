package net.sf.fmj.test.compat.playerbean;

import java.beans.*;

import javax.media.bean.playerbean.*;

import junit.framework.*;

public class MediaPlayerBeanInfoTest extends TestCase
{
    private static PropertyDescriptor buildPropertyDescriptor(Class clazz,
            String name, String displayName, Class propertyEditorClass,
            boolean bound) throws IntrospectionException
    {
        final PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
        pd.setDisplayName(displayName);
        pd.setPropertyEditorClass(propertyEditorClass);
        pd.setBound(bound);
        return pd;
    }

    private static boolean eq(PropertyDescriptor a, PropertyDescriptor b)
    {
        if (a == b)
            return true;
        if (!a.getName().equals(b.getName()))
        {
            System.out.println("getName");
            return false;
        }
        if (!a.getDisplayName().equals(b.getDisplayName()))
        {
            System.out.println("getDisplayName");
            return false;
        }
        if (!nullSafeEquals(a.getPropertyEditorClass(),
                b.getPropertyEditorClass()))
        {
            System.out.println("getPropertyEditorClass");
            return false;
        }
        if (!a.getPropertyType().equals(b.getPropertyType()))
        {
            System.out.println("getPropertyType");
            return false;
        }
        if (!a.getReadMethod().equals(b.getReadMethod()))
        {
            System.out.println("getReadMethod");
            return false;
        }
        if (!a.getShortDescription().equals(b.getShortDescription()))
        {
            System.out.println("getShortDescription");
            return false;
        }
        if (!a.getWriteMethod().equals(b.getWriteMethod()))
        {
            System.out.println("getWriteMethod");
            return false;
        }

        if (a.isBound() != b.isBound())
        {
            System.out.println("isBound");
            return false;
        }

        if (a.isConstrained() != b.isConstrained())
        {
            System.out.println("isConstrained");
            return false;
        }

        if (a.isExpert() != b.isExpert())
        {
            System.out.println("isExpert");
            return false;
        }

        if (a.isHidden() != b.isHidden())
        {
            System.out.println("isHidden");
            return false;
        }

        if (a.isPreferred() != b.isPreferred())
        {
            System.out.println("isPreferred");
            return false;
        }

        return true;
    }

    private static boolean nullSafeEquals(Object a, Object b)
    {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    private static final void print(PropertyDescriptor pd)
    {
        System.out.println(pd.getDisplayName());
        System.out.println(pd.getName());
        System.out.println(pd.getShortDescription());
        System.out.println(pd.getPropertyEditorClass());
        System.out.println(pd.getPropertyType());
        System.out.println(pd.getReadMethod());
        System.out.println(pd.getWriteMethod());
        // System.out.println(pd.getValue());// TODO
        System.out.println();
    }

    public void testMediaPlayerBeanInfo() throws IntrospectionException
    {
        MediaPlayerBeanInfo info = new MediaPlayerBeanInfo();
        PropertyDescriptor[] pds = info.getPropertyDescriptors();

        PropertyDescriptor[] pds2 = new PropertyDescriptor[] {
                buildPropertyDescriptor(MediaPlayer.class, "mediaLocation",
                        "media location", MediaPlayerMediaLocationEditor.class,
                        true),
                buildPropertyDescriptor(MediaPlayer.class,
                        "controlPanelVisible", "show control panel", null, true),
                buildPropertyDescriptor(MediaPlayer.class,
                        "cachingControlVisible", "show caching control", null,
                        true),
                buildPropertyDescriptor(MediaPlayer.class, "fixedAspectRatio",
                        "fixedAspectRatio", null, true),
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

        for (int i = 0; i < pds.length; ++i)
        {
            if (i >= pds2.length)
            {
                print(pds[i]);
                continue;
            }
            PropertyDescriptor a = pds[i];
            PropertyDescriptor b = pds2[i];
            if (!a.equals(b))
            {
                print(a);
                print(b);
                System.out.println("eq: " + eq(a, b));
                assertTrue(false);
            }
        }

        assertEquals(info.getDefaultPropertyIndex(), 1);
        for (int i = 0; i < 20; ++i)
            assertEquals(info.getIcon(i), null);

        final BeanDescriptor bd = info.getBeanDescriptor();
        assertEquals(bd.getDisplayName(), "MediaPlayer Bean");
        assertEquals(bd.getName(), "MediaPlayer");
        assertEquals(bd.getShortDescription(), "MediaPlayer Bean");
        assertEquals(bd.getBeanClass(), MediaPlayer.class);
        assertEquals(bd.getCustomizerClass(), null);
        assertEquals(bd.isExpert(), false);
        assertEquals(bd.isHidden(), false);
        assertEquals(bd.isPreferred(), false);

        try
        {
            EventSetDescriptor[] eds = info.getEventSetDescriptors();
            for (int i = 0; i < eds.length; ++i)
            {
                EventSetDescriptor ed = eds[i];
                System.out.println(ed.getAddListenerMethod());
                System.out.println(ed.getDisplayName());
                System.out.println();
            }
            assertTrue(false);
        } catch (Throwable e)
        { // TODO: do we really want to emulate this behavior?
        }

    }
}
