package net.sf.fmj.registry;

import java.io.*;
import java.util.*;

/**
 * Implements RegistryIO using Properties format.
 *
 * @author Ken Larson
 *
 */
class PropertiesRegistryIO implements RegistryIO
{
    private final RegistryContents contents;

    final String[] PLUGIN_TYPE_STRINGS = new String[] { "demux", "codec",
            "effect", "renderer", "mux" };

    final String CONTENT_PREFIX_STRING = "content-prefix";

    final String PROTOCOL_PREFIX_STRING = "protocol-prefix";

    private static final int MAX = 100;

    public PropertiesRegistryIO(RegistryContents contents)
    {
        super();
        this.contents = contents;
    }

    private void fromProperties(Properties p)
    {
        for (int i = 0; i < contents.plugins.length; ++i)
        {
            final String typeStr = PLUGIN_TYPE_STRINGS[i];
            final Vector<String> v = contents.plugins[i];
            for (int j = 0; j < MAX; ++j)
            {
                final String s = p.getProperty(typeStr + j);
                if (s != null && !s.equals(""))
                    v.add(s);

            }
        }

        {
            final Vector<String> v = contents.contentPrefixList;
            for (int j = 0; j < MAX; ++j)
            {
                final String s = p.getProperty(CONTENT_PREFIX_STRING + j);
                if (s != null && !s.equals(""))
                    v.add(s);
            }
        }

        {
            final Vector<String> v = contents.protocolPrefixList;
            for (int j = 0; j < MAX; ++j)
            {
                final String s = p.getProperty(PROTOCOL_PREFIX_STRING + j);
                if (s != null && !s.equals(""))
                    v.add(s);
            }
        }

    }

    public void load(InputStream is) throws IOException
    {
        final Properties p = new Properties();
        p.load(is);

        fromProperties(p);

    }

    private Properties toProperties()
    {
        final Properties p = new Properties();

        for (int i = 0; i < contents.plugins.length; ++i)
        {
            final String typeStr = PLUGIN_TYPE_STRINGS[i];
            final Vector<String> v = contents.plugins[i];
            for (int j = 0; j < v.size(); ++j)
            {
                p.setProperty(typeStr + j, v.get(j));
            }
        }

        {
            final Vector<String> v = contents.contentPrefixList;
            for (int j = 0; j < v.size(); ++j)
            {
                p.setProperty(CONTENT_PREFIX_STRING + j, v.get(j));
            }
        }

        {
            final Vector<String> v = contents.protocolPrefixList;
            for (int j = 0; j < v.size(); ++j)
            {
                p.setProperty(PROTOCOL_PREFIX_STRING + j, v.get(j));
            }
        }
        return p;
    }

    public void write(OutputStream os) throws IOException
    {
        final Properties p = toProperties();

        p.store(os, "FMJ registry");

    }

}
