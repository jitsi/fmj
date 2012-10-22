package net.sf.fmj.test.compat.plugins;

import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;

/**
 * Copy of javax.media.pim.PlugInManagerInitializer, used to test.
 * 
 * @author Ken Larson
 * 
 */
class PlugInManagerInitializer
{
    public static void init()
    {
        // PlugInManager.DEMULTIPLEXER:
        PlugInManager.addPlugIn("com.ibm.media.parser.video.MpegParser",
                new Format[] { new ContentDescriptor("audio.mpeg"),
                        new ContentDescriptor("video.mpeg"),
                        new ContentDescriptor("audio.mpeg"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.audio.WavParser",
                new Format[] { new ContentDescriptor("audio.x_wav"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.audio.AuParser",
                new Format[] { new ContentDescriptor("audio.basic"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.audio.AiffParser",
                new Format[] { new ContentDescriptor("audio.x_aiff"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.audio.GsmParser",
                new Format[] { new ContentDescriptor("audio.x_gsm"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.RawStreamParser",
                new Format[] { new ContentDescriptor("raw"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.RawBufferParser",
                new Format[] { new ContentDescriptor("raw"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.RawPullStreamParser",
                new Format[] { new ContentDescriptor("raw"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.RawPullBufferParser",
                new Format[] { new ContentDescriptor("raw"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.video.QuicktimeParser",
                new Format[] { new ContentDescriptor("video.quicktime"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.parser.video.AviParser",
                new Format[] { new ContentDescriptor("video.x_msvideo"), },
                new Format[] {}, javax.media.PlugInManager.DEMULTIPLEXER);

        // PlugInManager.CODEC:
        PlugInManager.addPlugIn("com.sun.media.codec.audio.mpa.JavaDecoder",
                new Format[] {
                        new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.sun.media.codec.video.cinepak.JavaDecoder",
                new Format[] { new VideoFormat("cvid", null, -1,
                        Format.byteArray, -1.0f), },
                new Format[] { new RGBFormat(null, -1, Format.intArray, -1.0f,
                        32, 0xff, 0xff00, 0xff0000, 1, -1, 0, -1), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.video.h263.JavaDecoder",
                new Format[] {
                        new VideoFormat("h263", null, -1, Format.byteArray,
                                -1.0f),
                        new VideoFormat("h263/rtp", null, -1, Format.byteArray,
                                -1.0f), }, new Format[] { new RGBFormat(null,
                        -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                        0xffffffff, -1, -1, -1, -1), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.sun.media.codec.video.colorspace.JavaRGBConverter",
                new Format[] { new RGBFormat(null, -1, null, -1.0f, -1,
                        0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1), },
                new Format[] { new RGBFormat(null, -1, null, -1.0f, -1,
                        0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.sun.media.codec.video.colorspace.JavaRGBToYUV",
                new Format[] {
                        new RGBFormat(null, -1, Format.byteArray, -1.0f, 24,
                                0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1,
                                -1),
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff0000, 0xff00, 0xff, 1, -1, -1, -1),
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff, 0xff00, 0xff0000, 1, -1, -1, -1), },
                new Format[] { new YUVFormat(null, -1, Format.byteArray, -1.0f,
                        2, -1, -1, -1, -1, -1), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.PCMToPCM",
                new Format[] {
                        new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                                Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                                Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.rc.RCModule",
                new Format[] {
                        new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                                Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                                Format.byteArray), }, new Format[] {
                        new AudioFormat("LINEAR", 8000.0, 16, 2, 0, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", 8000.0, 16, 1, 0, 1, -1,
                                -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.audio.rc.RateCvrt",
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.sun.media.codec.audio.msadpcm.JavaDecoder",
                new Format[] { new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.ulaw.JavaDecoder",
                new Format[] { new AudioFormat("ULAW", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.alaw.JavaDecoder",
                new Format[] { new AudioFormat("alaw", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.dvi.JavaDecoder",
                new Format[] { new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.g723.JavaDecoder",
                new Format[] {
                        new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1, -1.0,
                                Format.byteArray),
                        new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1, -1,
                                -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.JavaDecoder",
                new Format[] {
                        new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1, -1.0,
                                Format.byteArray),
                        new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1, -1,
                                -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.JavaDecoder_ms",
                new Format[] { new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.ima4.JavaDecoder",
                new Format[] { new AudioFormat("ima4", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.ibm.media.codec.audio.ima4.JavaDecoder_ms",
                new Format[] { new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.ulaw.JavaEncoder",
                new Format[] {
                        new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                                Format.byteArray),
                        new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                                Format.byteArray), },
                new Format[] { new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.dvi.JavaEncoder",
                new Format[] { new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                        -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("dvi/rtp", -1.0, 4, 1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.JavaEncoder",
                new Format[] { new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                        -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1,
                        -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.JavaEncoder_ms",
                new Format[] { new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1,
                        -1.0, Format.byteArray), },
                new Format[] { new com.sun.media.format.WavAudioFormat(
                        "gsm/ms", -1.0, -1, -1, -1, -1, -1, -1, -1.0f,
                        Format.byteArray, null), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.ima4.JavaEncoder",
                new Format[] { new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("ima4", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn(
                "com.ibm.media.codec.audio.ima4.JavaEncoder_ms",
                new Format[] { new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1,
                        -1, -1.0, Format.byteArray), },
                new Format[] { new com.sun.media.format.WavAudioFormat(
                        "ima4/ms", -1.0, -1, -1, -1, -1, -1, -1, -1.0f,
                        Format.byteArray, null), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.audio.ulaw.Packetizer",
                new Format[] { new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8,
                        -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("ULAW/rtp", -1.0, 8, 1, -1, -1,
                        8, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.audio.ulaw.DePacketizer",
                new Format[] { new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1,
                        -1, -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("ULAW", -1.0, -1, -1, -1, -1,
                        -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.audio.mpa.Packetizer",
                new Format[] {
                        new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1,
                                -1, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("mpegaudio/rtp", -1.0, -1, -1,
                        -1, -1, -1, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.audio.mpa.DePacketizer",
                new Format[] { new AudioFormat("mpegaudio/rtp", -1.0, -1, -1,
                        -1, -1, -1, -1.0, Format.byteArray), }, new Format[] {
                        new AudioFormat("mpegaudio", 44100.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 48000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 32000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 22050.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 24000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 16000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 11025.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 12000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpegaudio", 8000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 44100.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 48000.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 32000.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 22050.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 24000.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 16000.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 11025.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 12000.0, 16, -1, 1, 1,
                                -1, -1.0, Format.byteArray),
                        new AudioFormat("mpeglayer3", 8000.0, 16, -1, 1, 1, -1,
                                -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.Packetizer",
                new Format[] { new AudioFormat("gsm", 8000.0, -1, 1, -1, -1,
                        264, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("gsm/rtp", 8000.0, -1, 1, -1,
                        -1, 264, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.ibm.media.codec.audio.g723.Packetizer",
                new Format[] { new AudioFormat("g723", 8000.0, -1, 1, -1, -1,
                        192, -1.0, Format.byteArray), },
                new Format[] { new AudioFormat("g723/rtp", 8000.0, -1, 1, -1,
                        -1, 192, -1.0, Format.byteArray), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.video.jpeg.Packetizer",
                new Format[] { new VideoFormat("jpeg", null, -1,
                        Format.byteArray, -1.0f), },
                new Format[] { new VideoFormat("jpeg/rtp", null, -1,
                        Format.byteArray, -1.0f), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.video.jpeg.DePacketizer",
                new Format[] { new VideoFormat("jpeg/rtp", null, -1,
                        Format.byteArray, -1.0f), },
                new Format[] { new VideoFormat("jpeg", null, -1,
                        Format.byteArray, -1.0f), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.video.mpeg.Packetizer",
                new Format[] { new VideoFormat("mpeg", null, -1,
                        Format.byteArray, -1.0f), },
                new Format[] { new VideoFormat("mpeg/rtp", null, -1,
                        Format.byteArray, -1.0f), },
                javax.media.PlugInManager.CODEC);
        PlugInManager.addPlugIn("com.sun.media.codec.video.mpeg.DePacketizer",
                new Format[] { new VideoFormat("mpeg/rtp", null, -1,
                        Format.byteArray, -1.0f), },
                new Format[] { new VideoFormat("mpeg", null, -1,
                        Format.byteArray, -1.0f), },
                javax.media.PlugInManager.CODEC);

        // PlugInManager.EFFECT:

        // PlugInManager.RENDERER:
        PlugInManager.addPlugIn(
                "com.sun.media.renderer.audio.JavaSoundRenderer", new Format[] {
                        new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1,
                                -1.0, Format.byteArray),
                        new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                                Format.byteArray), }, new Format[] {},
                javax.media.PlugInManager.RENDERER);
        PlugInManager.addPlugIn(
                "com.sun.media.renderer.audio.SunAudioRenderer",
                new Format[] { new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1,
                        -1, -1.0, Format.byteArray), }, new Format[] {},
                javax.media.PlugInManager.RENDERER);
        PlugInManager.addPlugIn("com.sun.media.renderer.video.AWTRenderer",
                new Format[] {
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff0000, 0xff00, 0xff, 1, -1, 0, -1),
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff, 0xff00, 0xff0000, 1, -1, 0, -1), },
                new Format[] {}, javax.media.PlugInManager.RENDERER);
        PlugInManager.addPlugIn(
                "com.sun.media.renderer.video.LightWeightRenderer",
                new Format[] {
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff0000, 0xff00, 0xff, 1, -1, 0, -1),
                        new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
                                0xff, 0xff00, 0xff0000, 1, -1, 0, -1), },
                new Format[] {}, javax.media.PlugInManager.RENDERER);
        PlugInManager.addPlugIn("com.sun.media.renderer.video.JPEGRenderer",
                new Format[] { new VideoFormat("jpeg", null, -1,
                        Format.byteArray, -1.0f), }, new Format[] {},
                javax.media.PlugInManager.RENDERER);

        // PlugInManager.MULTIPLEXER:
        PlugInManager.addPlugIn("com.sun.media.multiplexer.RawBufferMux",
                new Format[] {},
                new Format[] { new ContentDescriptor("raw"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.RawSyncBufferMux",
                new Format[] {},
                new Format[] { new ContentDescriptor("raw"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.RTPSyncBufferMux",
                new Format[] {},
                new Format[] { new ContentDescriptor("raw.rtp"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.GSMMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "audio.x_gsm"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager
                .addPlugIn("com.sun.media.multiplexer.audio.MPEGMux",
                        new Format[] {}, new Format[] { new FileTypeDescriptor(
                                "audio.mpeg"), },
                        javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.WAVMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "audio.x_wav"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.AIFFMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "audio.x_aiff"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.AUMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "audio.basic"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.video.AVIMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "video.x_msvideo"), },
                javax.media.PlugInManager.MULTIPLEXER);
        PlugInManager.addPlugIn("com.sun.media.multiplexer.video.QuicktimeMux",
                new Format[] {}, new Format[] { new FileTypeDescriptor(
                        "video.quicktime"), },
                javax.media.PlugInManager.MULTIPLEXER);

    }
}
