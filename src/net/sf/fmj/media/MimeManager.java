package net.sf.fmj.media;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import net.sf.fmj.registry.*;
import net.sf.fmj.utility.*;

/**
 * Intended to function as Sun's version in com.sun.media. Contains many other
 * mime types that Sun's does not have.
 *
 * @author Ken Larson
 *
 */
public class MimeManager // not final so we can extend it to implement one in
// com.sun.media
{
    private static final Logger logger = LoggerSingleton.logger;

    private static final MimeTable defaultMimeTable = new MimeTable();

    static
    {
        // Standard ones from JMF:

        put("mvr", "application/mvr");
        put("aif", "audio/x_aiff"); // reordered so it won't be the default in
                                    // reverse lookup
        put("aiff", "audio/x_aiff");
        put("midi", "audio/midi");
        put("jmx", "application/x_jmx");
        put("mpv", "video/mpeg"); // reordered so it won't be the default in
                                  // reverse lookup
        put("mpg", "video/mpeg");
        // put("aif", "audio/x_aiff");
        put("wav", "audio/x_wav");
        put("mp3", "audio/mpeg");
        put("mpa", "audio/mpeg"); // reordered so it won't be the default in
                                  // reverse lookup
        put("mp2", "audio/mpeg");
        // put("mpa", "audio/mpeg");
        put("spl", "application/futuresplash");
        put("viv", "video/vivo");
        put("au", "audio/basic");
        put("g729", "audio/g729");
        put("mov", "video/quicktime");
        put("avi", "video/x_msvideo");
        put("g728", "audio/g728");
        put("cda", "audio/cdaudio");
        put("g729a", "audio/g729a");
        put("gsm", "audio/x_gsm");
        put("mid", "audio/midi");
        // put("mpv", "video/mpeg");
        put("swf", "application/x-shockwave-flash");
        put("rmf", "audio/rmf");

        boolean jmfDefaults = false;

        try
        {
            jmfDefaults = System.getProperty(
                    "net.sf.fmj.utility.JmfRegistry.JMFDefaults", "false")
                    .equals("true");
        } catch (SecurityException e)
        { // we must be an applet.
        }

        // end of JMF-standard types. Now, extensions that are added by FMJ
        if (!jmfDefaults)
        {
            // see http://wiki.xiph.org/index.php/MIME_Types_and_File_Extensions
            // for ogg extensions.

            put("ogg", "audio/ogg"); // this is somewhat problematic, since .ogg
                                     // extension is often used for audio-only
                                     // files, but is also used for video files.
            put("ogx", "application/ogg");
            put("oga", "audio/ogg");
            put("ogv", "video/ogg");

            // include other types from the xiph wiki, regardless of whether we
            // actually support them:
            put("spx", "audio/ogg");
            put("flac", "application/flac");
            put("anx", "application/annodex");
            put("axa", "audio/annodex");
            put("axv", "video/annodex");
            put("xspf", "application/xspf+xml ");

            // microsoft types, see http://support.microsoft.com/kb/288102

            put("asf", "video/x-ms-asf");
            put("asx", "video/x-ms-asf");
            put("wma", "audio/x-ms-wma");
            put("wax", "audio/x-ms-wax");
            put("wmv", "video/x-ms-wmv"); // this is incorrectly specified as
                                          // audio/... on the above link. Gnome
                                          // desktop has this as video/x-ms-asf
            put("wvx", "video/x-ms-wvx");
            put("wm", "video/x-ms-wm");
            put("wmx", "video/x-ms-wmx");
            put("wmz", "application/x-ms-wmz");
            put("wmd", "application/x-ms-wmd");

            // mpeg4:
            put("mpeg4", "video/mpeg"); // TODO: video/mpeg4?
            put("mp4", "video/mpeg"); // TODO: video/mpeg4?
            put("3gp", "video/3gpp");
            put("3g2", "video/3gpp");
            put("h264", "video/mp4");
            put("m4v", "video/mp4v");

            // mpeg2ps:
            put("m2v", "video/mp2p");
            put("vob", "video/mp2p");

            // mpegts
            put("ts", "video/x-mpegts");

            // mpeg1
            put("mpeg", "video/mpeg");
            put("m1v", "video/mpeg");

            // mjpeg
            put("mjpg", "video/x-mjpeg");
            put("mjpeg", "video/x-mjpeg");

            // flash:
            put("flv", "video/x-flv");

            // flic:
            put("fli", "video/fli");
            put("flc", "video/flc");
            put("flx", "video/flc");

            // matroska:
            put("mkv", "video/x-matroska");
            put("mka", "audio/x-matroska");

            // musepack
            put("mpc", "audio/x-musepack");
            put("mp+", "audio/x-musepack");
            put("mpp", "audio/x-musepack");

            // real:
            put("rm", "application/vnd.rn-realmedia");
            put("ra", "application/vnd.rn-realmedia");

            // other formats:
            put("dv", "video/x-dv");
            put("dif", "video/x-dv");
            put("aac", "audio/X-HX-AAC-ADTS");
            put("mj2", "video/mj2");
            put("mjp2", "video/mj2");
            put("mtv", "video/x-amv");
            put("amv", "video/x-amv");
            put("nsv", "application/x-nsv-vp3-mp3");
            put("nuv", "video/x-nuv");
            put("nuv", "application/mxf");
            put("shn", "application/x-shorten");
            put("tta", "audio/x-tta");
            put("voc", "audio/x-voc");
            put("wv", "audio/x-wavpack");
            put("wvp", "audio/x-wavpack");

            // fmj mimetypes created for ffmpeg
            put("4xm", "video/x-4xm");
            put("aud", "video/x-wsaud");
            put("apc", "audio/x-apc");
            put("avs", "video/x-avs");
            put("c93", "video/x-c93");
            put("cin", "video/x-dsicin");
            put("cin", "video/x-idcin");
            put("cpk", "video/x-film-cpk");
            put("dts", "audio/x-raw-dts");
            put("dxa", "video/x-dxa");
            put("gxf", "video/x-gxf");
            // put("mjpg", "video/x-ingenient"); // mjpeg variant. extension
            // used already above
            put("mm", "video/x-mm");
            put("mve", "video/x-wc3-movie");
            put("mve", "video/x-mve");
            put("roq", "video/x-roq");
            put("seq", "video/x-seq");
            put("smk", "video/x-smk");
            put("sol", "audio/x-sol");
            put("str", "audio/x-psxstr");
            put("thp", "video/x-thp");
            put("txd", "video/x-txd");
            put("uv2", "video/x-ea");
            put("vc1", "video/x-raw-vc1");
            put("vid", "video/x-bethsoft-vid");
            put("vmd", "video/x-vmd");
            put("vqa", "video/x-wsvqa");
            put("wve", "video/x-ea");
            put("yuv", "video/x-raw-yuv");

            // FMJ custom types (non-standard)

            put("mmr", "multipart/x-mixed-replace"); // this allows us to store
                                                     // these streams in files
                                                     // and play them back.
            put("xmv", "video/xml"); // FMJ's XML movie format, for testing

        }

    }

    public static final boolean addMimeType(String fileExtension,
            String mimeType)
    {
        fileExtension = nullSafeToLowerCase(fileExtension);
        mimeType = nullSafeToLowerCase(mimeType);

        if (defaultMimeTable.getMimeType(fileExtension) != null)
        {
            logger.warning("Cannot override default mime-table entries");
            return false;
        }

        Registry.getInstance().addMimeType(fileExtension, mimeType);
        return true;
    }

    public static void commit()
    {
        try
        {
            Registry.getInstance().commit();
        } catch (IOException e)
        {
            logger.log(Level.WARNING, "" + e, e);
        }
    }

    public static final String getDefaultExtension(String mimeType)
    {
        mimeType = nullSafeToLowerCase(mimeType);
        final String result = Registry.getInstance().getDefaultExtension(
                mimeType);
        if (result != null)
            return result;
        return defaultMimeTable.getDefaultExtension(mimeType);

    }

    public static final Hashtable getDefaultMimeTable()
    {
        return defaultMimeTable.getMimeTable();
    }

    public static final List<String> getExtensions(String mimeType)
    {
        mimeType = nullSafeToLowerCase(mimeType);
        final List<String> result = new ArrayList<String>();
        result.addAll(defaultMimeTable.getExtensions(mimeType));
        result.addAll(Registry.getInstance().getExtensions(mimeType));
        return result;

    }

    public static final Hashtable<String, String> getMimeTable()
    {
        final Hashtable<String, String> result = new Hashtable<String, String>();
        result.putAll(defaultMimeTable.getMimeTable());
        result.putAll(Registry.getInstance().getMimeTable());
        return result;
    }

    public static final String getMimeType(String fileExtension)
    {
        fileExtension = nullSafeToLowerCase(fileExtension);
        String result = Registry.getInstance().getMimeType(fileExtension);
        if (result != null)
            return result;
        result = defaultMimeTable.getMimeType(fileExtension);
        return result;
    }

    private static final String nullSafeToLowerCase(String s)
    {
        if (s == null)
            return s;
        return s.toLowerCase();
    }

    private static void put(String ext, String type)
    {
        ext = nullSafeToLowerCase(ext);
        type = nullSafeToLowerCase(type);
        defaultMimeTable.addMimeType(ext, type);

    }

    public static final boolean removeMimeType(String fileExtension)
    {
        fileExtension = nullSafeToLowerCase(fileExtension);
        return Registry.getInstance().removeMimeType(fileExtension);

    }

    protected MimeManager()
    {
        super();
    }

}
