package net.sf.fmj.test.functional;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.media.*;

import junit.framework.*;
import net.sf.fmj.utility.*;

/**
 * TODO write proper tests.
 * 
 * @author Warren Bloomer
 * 
 */
public class RegistryTest extends TestCase
{
    private static final Logger logger = LoggerSingleton.logger;

    public void testAll() throws Exception
    {
        x();
        y();
        z();

    }

    public void x()
    {
        Vector contentPrefixList = PackageManager.getContentPrefixList();
        logger.info("Got contentPrefixList: " + contentPrefixList);

        Vector protocolPrefixList = PackageManager.getProtocolPrefixList();
        logger.info("Got protocolPrefixList: " + contentPrefixList);
    }

    public void y()
    {
        String pluginName = "net.sf.fmj.media.renderer.audio.JavaSoundRenderer";
        try
        {
            Class cls = Class.forName(pluginName);
            Renderer plugin = (Renderer) cls.newInstance();
            Format[] in = plugin.getSupportedInputFormats();
            Format[] out = null; // plugin.getSupportedOutputFormats(null);

            logger.info("adding plugin, " + pluginName + "...");
            PlugInManager
                    .addPlugIn(pluginName, in, out, PlugInManager.RENDERER);
        } catch (Exception e)
        {
            logger.warning("Problem adding plugin, " + pluginName + ": " + e);
        }

        Vector codecs = PlugInManager.getPlugInList(null, null,
                PlugInManager.CODEC);
        logger.info("Got codecs: " + codecs);

        Vector demuxes = PlugInManager.getPlugInList(null, null,
                PlugInManager.DEMULTIPLEXER);
        logger.info("Got demuxes: " + codecs);

        Vector effects = PlugInManager.getPlugInList(null, null,
                PlugInManager.EFFECT);
        logger.info("Got effects: " + effects);

        Vector muxes = PlugInManager.getPlugInList(null, null,
                PlugInManager.MULTIPLEXER);
        logger.info("Got muxes: " + muxes);

        Vector renderers = PlugInManager.getPlugInList(null, null,
                PlugInManager.RENDERER);
        logger.info("Got renderers: " + renderers);
    }

    public void z() throws IOException
    {
        logger.info("Committing PackageManager...");
        PackageManager.commitContentPrefixList();
        PackageManager.commitProtocolPrefixList();

        logger.info("Committing PlugInManager...");
        PlugInManager.commit();
    }
}
