package net.sf.fmj.test.compat.formats;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.codegen.*;

import com.sun.media.format.*;

/**
 * 
 * @author Ken Larson
 * 
 */
public class SerializableTest extends TestCase
{
    /**
     * Class for computing and caching field/constructor/method signatures
     * during serialVersionUID calculation.
     */
    private static class MemberSignature
    {
        public final Member member;
        public final String name;
        public final String signature;

        public MemberSignature(Constructor cons)
        {
            member = cons;
            name = cons.getName();
            signature = getMethodSignature(cons.getParameterTypes(), Void.TYPE);
        }

        public MemberSignature(Field field)
        {
            member = field;
            name = field.getName();
            signature = getClassSignature(field.getType());
        }

        public MemberSignature(Method meth)
        {
            member = meth;
            name = meth.getName();
            signature = getMethodSignature(meth.getParameterTypes(),
                    meth.getReturnType());
        }
    }

    private static final boolean TRACE_UID = false;

    public static final Format[] formats = new Format[] {
            new VideoFormat(null, null, -1, null, -1.0f),

            new VideoFormat(null, null, -1, null, -1.0f),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat(null, null, -1, null, -1.0f),
            new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat(null, null, -1, null, -1.0f),
            new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat(null, null, -1, null, -1.0f),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new VideoFormat(null, null, -1, null, -1.0f),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new VideoFormat(null, null, -1, null, -1.0f),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("h263", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("h263/rtp", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, -1, -1),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, -1, -1),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("alaw", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("ima4", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("gsm", 8000.0, -1, 1, -1, -1, 264, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new AudioFormat("g723", 8000.0, -1, 1, -1, -1, 192, -1.0,
                    Format.byteArray),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("jpeg/rtp", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("mpeg", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("mpeg/rtp", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("h263", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("h263/rtp", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("alaw", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("ima4", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("gsm", 8000.0, -1, 1, -1, -1, 264, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat("g723", 8000.0, -1, 1, -1, -1, 192, -1.0,
                    Format.byteArray),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("jpeg/rtp", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("mpeg", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new VideoFormat("mpeg/rtp", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f, -1,
                    -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, -1, 0),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00, 0xff,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00, 0xff0000,
                    -1, -1, -1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 1, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1, -1, -1,
                    -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
            new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff, 0xffffffff,
                    0xffffffff, -1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new VideoFormat("cvid", new java.awt.Dimension(320, 200), 16222,
                    Format.byteArray, 1.3414634f),
            new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
                    0xff0000, 1, -1, 0, -1),
            new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
            new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
            new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
            new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),

    };

    /**
     * Computes the default serial version UID value for the given class.
     */
    private static long computeDefaultSUID(Class cl)
    {
        if (!Serializable.class.isAssignableFrom(cl) || Proxy.isProxyClass(cl))
        {
            return 0L;
        }

        try
        {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);

            dout.writeUTF(cl.getName());

            int classMods = cl.getModifiers()
                    & (Modifier.PUBLIC | Modifier.FINAL | Modifier.INTERFACE | Modifier.ABSTRACT);

            /*
             * compensate for javac bug in which ABSTRACT bit was set for an
             * interface only if the interface declared methods
             */
            Method[] methods = cl.getDeclaredMethods();
            if ((classMods & Modifier.INTERFACE) != 0)
            {
                classMods = (methods.length > 0) ? (classMods | Modifier.ABSTRACT)
                        : (classMods & ~Modifier.ABSTRACT);
            }
            if (TRACE_UID)
                System.out.println("classMods=" + classMods);
            dout.writeInt(classMods);

            if (!cl.isArray())
            {
                /*
                 * compensate for change in 1.2FCS in which
                 * Class.getInterfaces() was modified to return Cloneable and
                 * Serializable for array classes.
                 */
                Class[] interfaces = cl.getInterfaces();
                String[] ifaceNames = new String[interfaces.length];
                for (int i = 0; i < interfaces.length; i++)
                {
                    ifaceNames[i] = interfaces[i].getName();
                }
                Arrays.sort(ifaceNames);
                for (int i = 0; i < ifaceNames.length; i++)
                {
                    if (TRACE_UID)
                        System.out.println(ifaceNames[i]);
                    dout.writeUTF(ifaceNames[i]);
                }
            }

            Field[] fields = cl.getDeclaredFields();
            MemberSignature[] fieldSigs = new MemberSignature[fields.length];
            for (int i = 0; i < fields.length; i++)
            {
                fieldSigs[i] = new MemberSignature(fields[i]);
            }
            Arrays.sort(fieldSigs, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    String name1 = ((MemberSignature) o1).name;
                    String name2 = ((MemberSignature) o2).name;
                    return name1.compareTo(name2);
                }
            });
            for (int i = 0; i < fieldSigs.length; i++)
            {
                MemberSignature sig = fieldSigs[i];
                int mods = sig.member.getModifiers()
                        & (Modifier.PUBLIC | Modifier.PRIVATE
                                | Modifier.PROTECTED | Modifier.STATIC
                                | Modifier.FINAL | Modifier.VOLATILE | Modifier.TRANSIENT);
                if (((mods & Modifier.PRIVATE) == 0)
                        || ((mods & (Modifier.STATIC | Modifier.TRANSIENT)) == 0))
                {
                    if (TRACE_UID)
                        System.out.println(sig.name + " " + sig.signature + " "
                                + mods);
                    dout.writeUTF(sig.name);
                    dout.writeInt(mods);
                    dout.writeUTF(sig.signature);
                }
            }

            if (hasStaticInitializer(cl))
            {
                if (TRACE_UID)
                    System.out.println("STATICINIT");
                dout.writeUTF("<clinit>");
                dout.writeInt(Modifier.STATIC);
                dout.writeUTF("()V");
            }

            Constructor[] cons = cl.getDeclaredConstructors();
            MemberSignature[] consSigs = new MemberSignature[cons.length];
            for (int i = 0; i < cons.length; i++)
            {
                consSigs[i] = new MemberSignature(cons[i]);
            }
            Arrays.sort(consSigs, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    String sig1 = ((MemberSignature) o1).signature;
                    String sig2 = ((MemberSignature) o2).signature;
                    return sig1.compareTo(sig2);
                }
            });
            for (int i = 0; i < consSigs.length; i++)
            {
                MemberSignature sig = consSigs[i];
                int mods = sig.member.getModifiers()
                        & (Modifier.PUBLIC | Modifier.PRIVATE
                                | Modifier.PROTECTED | Modifier.STATIC
                                | Modifier.FINAL | Modifier.SYNCHRONIZED
                                | Modifier.NATIVE | Modifier.ABSTRACT | Modifier.STRICT);
                if ((mods & Modifier.PRIVATE) == 0)
                {
                    if (TRACE_UID)
                        System.out.println("<init>" + sig.signature + " "
                                + mods);
                    dout.writeUTF("<init>");
                    dout.writeInt(mods);
                    dout.writeUTF(sig.signature.replace('/', '.'));
                }
            }

            MemberSignature[] methSigs = new MemberSignature[methods.length];
            for (int i = 0; i < methods.length; i++)
            {
                methSigs[i] = new MemberSignature(methods[i]);
            }
            Arrays.sort(methSigs, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    MemberSignature ms1 = (MemberSignature) o1;
                    MemberSignature ms2 = (MemberSignature) o2;
                    int comp = ms1.name.compareTo(ms2.name);
                    if (comp == 0)
                    {
                        comp = ms1.signature.compareTo(ms2.signature);
                    }
                    return comp;
                }
            });
            for (int i = 0; i < methSigs.length; i++)
            {
                MemberSignature sig = methSigs[i];
                int mods = sig.member.getModifiers()
                        & (Modifier.PUBLIC | Modifier.PRIVATE
                                | Modifier.PROTECTED | Modifier.STATIC
                                | Modifier.FINAL | Modifier.SYNCHRONIZED
                                | Modifier.NATIVE | Modifier.ABSTRACT | Modifier.STRICT);
                if ((mods & Modifier.PRIVATE) == 0)
                {
                    if (TRACE_UID)
                        System.out.println(sig.name + " " + sig.signature + " "
                                + mods);
                    dout.writeUTF(sig.name);
                    dout.writeInt(mods);
                    dout.writeUTF(sig.signature.replace('/', '.'));
                }
            }

            dout.flush();

            MessageDigest md = MessageDigest.getInstance("SHA");
            if (TRACE_UID)
                System.out.println(dump(bout.toByteArray()));

            byte[] hashBytes = md.digest(bout.toByteArray());
            long hash = 0;
            for (int i = Math.min(hashBytes.length, 8) - 1; i >= 0; i--)
            {
                hash = (hash << 8) | (hashBytes[i] & 0xFF);
            }
            return hash;
        } catch (IOException ex)
        {
            throw new InternalError();
        } catch (NoSuchAlgorithmException ex)
        {
            throw new SecurityException(ex.getMessage());
        }
    }

    private static String diff(String a, String b)
    {
        StringBuffer buf = new StringBuffer();
        int len = a.length() > b.length() ? a.length() : b.length();
        for (int i = 0; i < len; ++i)
        {
            if (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i))
                buf.append(' ');
            else
                buf.append('^');
        }
        return buf.toString();

    }

    public static String dump(byte[] bytes)
    {
        return CGUtils.dump(bytes, 0, bytes.length);
    }

    /**
     * Returns JVM type signature for given class.
     */
    static String getClassSignature(Class cl)
    {
        StringBuffer sbuf = new StringBuffer();
        while (cl.isArray())
        {
            sbuf.append('[');
            cl = cl.getComponentType();
        }
        if (cl.isPrimitive())
        {
            if (cl == Integer.TYPE)
            {
                sbuf.append('I');
            } else if (cl == Byte.TYPE)
            {
                sbuf.append('B');
            } else if (cl == Long.TYPE)
            {
                sbuf.append('J');
            } else if (cl == Float.TYPE)
            {
                sbuf.append('F');
            } else if (cl == Double.TYPE)
            {
                sbuf.append('D');
            } else if (cl == Short.TYPE)
            {
                sbuf.append('S');
            } else if (cl == Character.TYPE)
            {
                sbuf.append('C');
            } else if (cl == Boolean.TYPE)
            {
                sbuf.append('Z');
            } else if (cl == Void.TYPE)
            {
                sbuf.append('V');
            } else
            {
                throw new InternalError();
            }
        } else
        {
            sbuf.append('L' + cl.getName().replace('.', '/') + ';');
        }
        return sbuf.toString();
    }

    /**
     * Returns JVM type signature for given list of parameters and return type.
     */
    private static String getMethodSignature(Class[] paramTypes, Class retType)
    {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append('(');
        for (int i = 0; i < paramTypes.length; i++)
        {
            sbuf.append(getClassSignature(paramTypes[i]));
        }
        sbuf.append(')');
        sbuf.append(getClassSignature(retType));
        return sbuf.toString();
    }

    private static boolean hasStaticInitializer(Class clazz)
    {
        return true; // TODO
    }

    private void assertEquals(double a, double b)
    {
        assertTrue(a == b);
    }

    void gen() throws Exception
    {
        for (int i = 0; i < formats.length; ++i)
        {
            test(formats[i], "");
        }
    }

    private long getSerialVersionUID(Class clazz)
    {
        return ObjectStreamClass.lookup(clazz).getSerialVersionUID();

    }

    public void test(Serializable f, String byteArrayStr) throws Exception
    {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(f);
        output.close();
        buffer.close();
        // System.out.println(dump(buffer.toByteArray()));
        String actual = CGUtils.byteArrayToHexString(buffer.toByteArray());
        if (!actual.equals(byteArrayStr))
        {
            System.out.println("test(" + MediaCGUtils.formatToStr((Format) f)
                    + ", " + CGUtils.toLiteral(actual) + ");");
            // System.out.println("target: " + byteArrayStr);
            // System.out.println("actual: " + actual);
            // System.out.println("diff:   " + diff(byteArrayStr, actual));
            //
            // System.out.println("target:");
            // System.out.println(dump(hexStringToByteArray(byteArrayStr)));
            //
            // System.out.println("actual:");
            // System.out.println(dump(buffer.toByteArray()));

            assertEquals(actual, byteArrayStr);

        }

        // now read it in and check:

        {
            final ByteArrayInputStream inbuf = new ByteArrayInputStream(
                    buffer.toByteArray());
            final ObjectInputStream input = new ObjectInputStream(inbuf);
            final Object oRead = input.readObject();
            if (!(oRead instanceof IndexedColorFormat)) // equals fails because
                                                        // the byte arrays have
                                                        // to be ==, not equals.
            {
                if (!f.equals(oRead))
                {
                    System.out.println("target: "
                            + MediaCGUtils.formatToStr((Format) f));
                    System.out.println("actual: "
                            + MediaCGUtils.formatToStr((Format) oRead));
                }
                assertEquals(f, oRead);
            }
            input.close();
            inbuf.close();
        }

    }

    public void testFormats() throws Exception
    {
        ObjectStreamClass o = ObjectStreamClass.lookup(Format.class);
        // System.out.println(o.getSerialVersionUID());

        assertEquals(o.getSerialVersionUID(), 5612854984030969319L);

        // System.out.println(computeDefaultSUID(WavAudioFormat.class));

        assertEquals(computeDefaultSUID(VideoFormat.class),
                8024602108723869163L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.VideoFormat.class)
                        .getSerialVersionUID(), 3595293544666171102L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.RGBFormat.class)
                        .getSerialVersionUID(), 4947771287350170953L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.JPEGFormat.class)
                        .getSerialVersionUID(), 5931444563402252753L);
        assertEquals(
                ObjectStreamClass.lookup(
                        javax.media.format.IndexedColorFormat.class)
                        .getSerialVersionUID(), 1122845796602684323L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.YUVFormat.class)
                        .getSerialVersionUID(), 3432899013627630538L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.H261Format.class)
                        .getSerialVersionUID(), 8854949905215088659L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.AudioFormat.class)
                        .getSerialVersionUID(), -9207570564778637264L);
        assertEquals(
                ObjectStreamClass.lookup(javax.media.format.H263Format.class)
                        .getSerialVersionUID(), 2058148783428473772L);
        assertEquals(ObjectStreamClass.lookup(WavAudioFormat.class)
                .getSerialVersionUID(), -4347278319609717618L);

        assertEquals(new Format("abc").getDataType(), Format.byteArray);

        test(new Format("abc"),
                "aced0005737200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00014c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003616263"

        );

        test(new VideoFormat("abc"),
                "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003616263bf800000ffffffff70"

        );

        assertEquals(new RGBFormat().getEncoding(), "rgb");
        assertEquals(new RGBFormat().getBitsPerPixel(), -1);
        assertEquals(new RGBFormat().getBlueMask(), -1);
        assertEquals(new RGBFormat().getDataType(), null);
        assertEquals(new RGBFormat().getEndian(), -1);
        assertEquals(new RGBFormat().getFlipped(), -1);
        assertEquals(new RGBFormat().getFrameRate(), -1.f);
        assertEquals(new RGBFormat().getGreenMask(), -1);
        assertEquals(new RGBFormat().getLineStride(), -1);
        assertEquals(new RGBFormat().getMaxDataLength(), -1L);
        assertEquals(new RGBFormat().getPixelStride(), -1);
        assertEquals(new RGBFormat().getRedMask(), -1);
        assertEquals(new RGBFormat().getSize(), null);

        test(new RGBFormat(),
                "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"

        );

        test(new JPEGFormat(),
                "aced00057372001d6a617661782e6d656469612e666f726d61742e4a504547466f726d61745250b9677e8361d102000249000a646563696d6174696f6e49000771466163746f727872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70ffffffffffffffff"

        );

        test(new IndexedColorFormat(new Dimension(1, 1), 2000,
                Format.byteArray, 3.f, 1, 2, new byte[] { 0, 0 }, new byte[] {
                        0, 0 }, new byte[] { 0, 0 }),
                "aced0005737200256a617661782e6d656469612e666f726d61742e496e6465786564436f6c6f72466f726d61740f95264d8c0b23a302000549000a6c696e655374726964654900076d617053697a655b000a626c756556616c7565737400025b425b000b677265656e56616c75657371007e00015b000972656456616c75657371007e00017872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00054c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046972676240400000000007d0737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000010000000100000001000000027571007e00090000000200007571007e00090000000200007571007e0009000000020000"

        );

        test(new YUVFormat(),
                "aced00057372001c6a617661782e6d656469612e666f726d61742e595556466f726d61742fa41b76f11053ca0200064900076f6666736574554900076f6666736574564900076f666673657459490008737472696465555649000773747269646559490007797576547970657872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003797576bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffff"

        );

        test(new H261Format(),
                "aced00057372001d6a617661782e6d656469612e666f726d61742e48323631466f726d61747ae31a05f43968130200014900167374696c6c496d6167655472616e736d697373696f6e7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000468323631bf800000ffffffff70ffffffff"

        );

        assertEquals(new AudioFormat("abc").getEncoding(), "abc");
        assertEquals(new AudioFormat("abc").getFrameRate(), -1.0);
        assertEquals(new AudioFormat("abc").getSampleRate(), -1.0);
        assertEquals(new AudioFormat("abc").getChannels(), -1);
        assertEquals(new AudioFormat("abc").getDataType(), byte[].class);
        assertEquals(new AudioFormat("abc").getEndian(), -1);
        assertEquals(new AudioFormat("abc").getFrameSizeInBits(), -1);
        assertEquals(new AudioFormat("abc").getSampleSizeInBits(), -1);
        assertEquals(new AudioFormat("abc").getSigned(), -1);

        test(new AudioFormat("abc"),
                "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003616263ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff"

        );

        assertEquals(new H263Format().getHrDB(), -1);
        test(new H263Format(),
                "aced00057372001d6a617661782e6d656469612e666f726d61742e48323633466f726d61741c900369fb2813ac020006490012616476616e63656450726564696374696f6e49001061726974686d65746963436f64696e674900116572726f72436f6d70656e736174696f6e4900046872444249000870624672616d6573490012756e72657374726963746564566563746f727872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000468323633bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffff"

        );

        test(new WavAudioFormat("abc"),
                "aced000573720023636f6d2e73756e2e6d656469612e666f726d61742e576176417564696f466f726d6174c3ab5d66b3e7dc8e0200024900156176657261676542797465735065725365636f6e645b0013636f64656353706563696669634865616465727400025b427872001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003616263ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffffffffffff70"

        );
    }

    public void testMoreFormats() throws Exception
    {
        if (false)
        {
            gen();
        } else
        {
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c4157ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c415700000001ffffffffbff0000000000000ffffffff0000000000bff000000000000040bf40000000000000000008ffffffff");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new VideoFormat(null, null, -1, null, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070bf800000ffffffff70");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("h263", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000468323633bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("h263/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008683236332f727470bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new RGBFormat(null, -1, Format.byteArray, -1.0f, 24,
                    0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003726762bf800000ffffffff7000000018ffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffffffffffff0000ff00ffffffff0000000100ff0000");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffffffffffff0000ff00ffffffff00000001000000ff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400076d73616470636dffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c4157ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("alaw", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004616c6177ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400076476692f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000467373233ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008673732332f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000367736dffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000767736d2f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000667736d2f6d73ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("ima4", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004696d6134ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740007696d61342f6d73ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffff00000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffff00000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c415700000001ffffffffbff0000000000000000000080000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008554c41572f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000d6d706567617564696f2f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("gsm", 8000.0, -1, 1, -1, -1, 264, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000367736d00000001ffffffffbff0000000000000000001080000000000bff000000000000040bf400000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new AudioFormat("g723", 8000.0, -1, 1, -1, -1, 192, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046737323300000001ffffffffbff0000000000000000000c00000000000bff000000000000040bf400000000000ffffffffffffffff");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("jpeg/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400086a7065672f727470bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("mpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046d706567bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("mpeg/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400086d7065672f727470bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("h263", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000468323633bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("h263/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008683236332f727470bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.byteArray, -1.0f, 24,
                    0xffffffff, 0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740003726762bf800000ffffffff7000000018ffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffffffffffff0000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffffffffffff0000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("msadpcm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400076d73616470636dffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c4157ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("alaw", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004616c6177ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("dvi/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400076476692f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000467373233ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008673732332f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000367736dffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000767736d2f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("gsm/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000667736d2f6d73ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("ima4", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004696d6134ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("ima4/ms", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740007696d61342f6d73ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000010ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000001ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e45415200000002ffffffffbff0000000000000ffffffff0000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffff00000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("LINEAR", -1.0, 16, -1, 0, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffff00000000bff0000000000000ffffffff0000000000bff0000000000000bff00000000000000000001000000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("ULAW", -1.0, 8, 1, -1, -1, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c415700000001ffffffffbff0000000000000000000080000000000bff0000000000000bff000000000000000000008ffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("ULAW/rtp", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740008554c41572f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1, 1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000a6d7065676c6179657233ffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 16000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040cf400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 22050.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 24000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040d7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 32000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040df400000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 44100.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e5888000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio", 48000.0, -1, -1, -1, 1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400096d706567617564696fffffffffffffffffbff0000000000000ffffffff0000000000bff000000000000040e7700000000000ffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("mpegaudio/rtp", -1.0, -1, -1, -1, -1, -1,
                    -1.0, Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000d6d706567617564696f2f727470ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("gsm", 8000.0, -1, 1, -1, -1, 264, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000367736d00000001ffffffffbff0000000000000000001080000000000bff000000000000040bf400000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat("g723", 8000.0, -1, 1, -1, -1, 192, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046737323300000001ffffffffbff0000000000000000000c00000000000bff000000000000040bf400000000000ffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("jpeg/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400086a7065672f727470bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("mpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046d706567bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new VideoFormat("mpeg/rtp", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400086d7065672f727470bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xf800, 0x7e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000007e0ffffffffffffffff0000f800");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000001ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xffffffff, 0xffffffff,
                    0xffffffff, 2, 640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000010ffffffffffffffffffffffffffffffff0000028000000002ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x7c00, 0x3e0, 0x1f,
                    -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff0000001fffffffffffffffff000003e0ffffffffffffffff00007c00");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffffffffffff000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, -1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffff00000000ffffffffffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000018ffffffffffffffffffffffffffffffff000003c000000003ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffffffffffff00000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffffffffffff00000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000100000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000018ffffffffffffffffffffffffffffffff000003c000000003ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffffffffffff00000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffffffffffff00000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000000000002000003c00000000300000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000018ffffffffffffffffffffffffffffffff000003c000000003ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffffffffffff00000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffffffffffff00000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000100000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0xffffffff, 0xffffffff,
                    0xffffffff, 3, 960, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000018ffffffffffffffffffffffffffffffff000003c000000003ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffffffffffff00000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffffffffffff00000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000000000002000003c00000000300000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffffffffffff0000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffffffffffff0000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000010000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff0000, 0xff00,
                    0xff, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff000000ffffffffffffffffff0000ff00ffffffffffffffff00ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffffffffffff0000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffffffffffff0000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffffffffffff0000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffffffffffff0000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000010000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 1, 320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000014000000001ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xff, 0xff00,
                    0xff0000, -1, -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00ff0000ffffffffffffffff0000ff00ffffffffffffffff000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffffffffffff0000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffffffffffff0000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000014000000001000000ff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffffffffffff00000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffffffffffff00000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffff0000000100000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x1, 0x2, 0x3, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000003ffffffffffffffff00000002ffffffffffffffff00000001");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffffffffffff00000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffffffffffff00000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x1, 0x2, 0x3, 4, 1280,
                    0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000003ffffffff0000000000000002000005000000000400000001");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffffffffffff00000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffffffffffff00000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffff0000000100000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x3, 0x2, 0x1, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000001ffffffffffffffff00000002ffffffffffffffff00000003");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffffffffffff00000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffffffffffff00000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x3, 0x2, 0x1, 4, 1280,
                    0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000001ffffffff0000000000000002000005000000000400000003");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffffffffffff00000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffffffffffff00000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffff0000000100000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x2, 0x3, 0x4, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000004ffffffffffffffff00000003ffffffffffffffff00000002");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffffffffffff00000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffffffffffff00000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x2, 0x3, 0x4, 4, 1280,
                    0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000004ffffffff0000000000000003000005000000000400000002");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffffffffffff00000003000005000000000400000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffffffffffff00000003000005000000000400000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000001ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffff0000000100000003000005000000000400000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0xffffffff, 0xffffffff,
                    0xffffffff, 4, 1280, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020ffffffffffffffffffffffffffffffff0000050000000004ffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0x4, 0x3, 0x2, -1,
                    -1, -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffff00000002ffffffffffffffff00000003ffffffffffffffff00000004");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffffffffffff00000003000005000000000400000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    -1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffffffffffff00000003000005000000000400000004");
            test(new RGBFormat(null, -1, null, -1.0f, -1, 0xffffffff,
                    0xffffffff, 0xffffffff, -1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e000070740003726762bf800000ffffffff70ffffffffffffffffffffffff00000000ffffffffffffffffffffffffffffffff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 256000,
                    Format.byteArray, 1.3414634f, 32, 0x4, 0x3, 0x2, 4, 1280,
                    0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130003e800737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000000002ffffffff0000000000000003000005000000000400000004");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000100000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000100000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000000000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000000000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000100000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000100000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000000000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000000000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000010000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000010000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000007e000000140000000010000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0xf800, 0x7e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000007e000000280000000020000f800");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000001000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.shortArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b53ef832e06e55db0fa02000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001fffffffff00000000000003e0000001400000000100007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 1, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000001000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000100000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 128000,
                    Format.byteArray, 1.3414634f, 16, 0x7c00, 0x3e0, 0x1f, 2,
                    640, 0, 0),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130001f400737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140000000100000001f0000000000000000000003e0000002800000000200007c00");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000100000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000100000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000000000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x1, 0x2, 0x3, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000003ffffffff0000000000000002000003c00000000300000001");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000100000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 1,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000100000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000000000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 192000,
                    Format.byteArray, 1.3414634f, 24, 0x3, 0x2, 0x1, 3, 960, 0,
                    -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400037267623fabb5130002ee00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000001800000001ffffffff0000000000000002000003c00000000300000003");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000010000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 1, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000010000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff0000, 0xff00, 0xff, 1,
                    320, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c80000014000000020000000ffffffffff000000000000ff00000001400000000100ff0000");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new VideoFormat("cvid", new java.awt.Dimension(320, 200),
                    16222, Format.byteArray, 1.3414634f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004637669643fabb51300003f5e737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c800000140");
            test(new VideoFormat("cvid", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e0020000787074000463766964bf800000ffffffff70");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new RGBFormat(new java.awt.Dimension(320, 200), 64000,
                    Format.intArray, 1.3414634f, 32, 0xff, 0xff00, 0xff0000, 0,
                    0, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a502000078707400037267623fabb5130000fa00737200126a6176612e6177742e44696d656e73696f6e418ed9d7ac5f441402000249000668656967687449000577696474687870000000c8000001400000002000ff0000ffffffff000000000000ff000000000000000000000000ff");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("ULAW", 8000.0, 8, 1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c415700000001ffffffffbff0000000000000ffffffff0000000000bff000000000000040bf40000000000000000008ffffffff");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
                    0xff00, 0xff, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff7000000020000000ffffffffff000000000000ff00ffffffff0000000100ff0000");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff,
                    0xff00, 0xff0000, 1, -1, 0, -1),
                    "aced00057372001c6a617661782e6d656469612e666f726d61742e524742466f726d617444aa03c57c62554902000849000c62697473506572506978656c490008626c75654d61736b490006656e6469616e490007666c6970706564490009677265656e4d61736b49000a6c696e6553747269646549000b706978656c5374726964654900077265644d61736b7872001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00044c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b494dba602676eab2a50200007870740003726762bf800000ffffffff700000002000ff0000ffffffff000000000000ff00ffffffff00000001000000ff");
            test(new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0, null),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e00007070ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new VideoFormat("jpeg", null, -1, Format.byteArray, -1.0f),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e566964656f466f726d617431e50c6e211506de0200034600096672616d655261746549000d6d6178446174614c656e6774684c000473697a657400144c6a6176612f6177742f44696d656e73696f6e3b787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00034c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400046a706567bf800000ffffffff70");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c4157ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e454152ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
            test(new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e00200007870740004554c4157ffffffffffffffffbff0000000000000ffffffff0000000000bff0000000000000bff0000000000000ffffffffffffffff");
            test(new AudioFormat("LINEAR", 22050.0, 8, 1, 1, 0, 8, -1.0,
                    Format.byteArray),
                    "aced00057372001e6a617661782e6d656469612e666f726d61742e417564696f466f726d61748038235aea06c83002000a4900086368616e6e656c73490006656e6469616e4400096672616d655261746549000f6672616d6553697a65496e426974735a0004696e69744900066d617267696e44000a6d756c7469706c69657244000a73616d706c655261746549001073616d706c6553697a65496e426974734900067369676e6564787200126a617661782e6d656469612e466f726d61744de4dddeaaf25de70200044a000c656e636f64696e67436f64654c0003636c7a7400114c6a6176612f6c616e672f436c6173733b4c0008646174615479706571007e00024c0008656e636f64696e677400124c6a6176612f6c616e672f537472696e673b787000000000000000007671007e0000767200025b42acf317f8060854e002000078707400064c494e4541520000000100000001bff0000000000000000000080000000000bff000000000000040d58880000000000000000800000000");
        }
    }

}
