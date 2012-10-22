package net.sf.fmj.test.compat.formats;

import java.lang.reflect.*;

import javax.media.*;
import javax.media.format.*;

import junit.framework.*;
import net.sf.fmj.codegen.*;

import com.sun.media.format.*;

/**
 * 
 * Make sure encoding codes for equality comparisons are specific values.
 * 
 * @author Ken Larson
 * 
 */
public class FormatEncodingCodeValuesTest extends TestCase
{
    public void gen() throws Exception
    {
        gen(Format.class);
        gen(RGBFormat.class);
        gen(YUVFormat.class);
        gen(VideoFormat.class);
        gen(AudioFormat.class);
        gen(H261Format.class);
        gen(H263Format.class);
        gen(IndexedColorFormat.class);
        gen(JPEGFormat.class);
        gen(WavAudioFormat.class);
    }

    void gen(Class formatClass) throws Exception
    {
        for (int i = 0; i < formatClass.getFields().length; ++i)
        {
            final Field field = formatClass.getFields()[i];

            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            if (field.getType() != String.class)
                continue;

            final String value = (String) field.get(null);

            System.out.println("assertEquals(getEncodingCode("
                    + formatClass.getName() + "." + field.getName() + "), "
                    + CGUtils.toLiteral(getEncodingCode(value)) + ");");
        }

    }

    private long getEncodingCode(String s) throws Exception
    {
        final Format f = new Format(null);

        return FormatPrivateTest.getEncodingCode(f, s);
    }

    public void testAll() throws Exception
    {
        // gen();

        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.CINEPAK),
                9398884L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.JPEG),
                11209063L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.JPEG_RTP),
                188056875248944L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.MPEG),
                11995495L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.MPEG_RTP),
                201251014782256L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.H261),
                10560913L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.H261_RTP),
                177182722698544L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.H263),
                10560915L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.H263_RTP),
                177182756252976L);
        assertEquals(
                getEncodingCode(javax.media.format.VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.RGB),
                207330L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.YUV),
                236918L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.IRGB),
                10955234L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.SMC),
                211811L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.RLE),
                207653L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.RPZA),
                13307553L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPG),
                11971623L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPEGA),
                11971617L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.MJPEGB),
                11971618L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO32),
                10970322L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO41),
                10970385L);
        assertEquals(getEncodingCode(javax.media.format.VideoFormat.INDEO50),
                10970448L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.LINEAR),
                47944718450L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.ULAW),
                14076023L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.ULAW_RTP),
                236156482432304L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.ALAW),
                8833143L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.IMA4),
                10934356L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.IMA4_MS),
                2866375883635L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSADPCM),
                3147700570349L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.DVI),
                150953L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.DVI_RTP),
                2532575227184L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G723),
                10318995L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G723_RTP),
                173124012158256L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G728),
                10319000L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G728_RTP),
                173124096044336L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729),
                10319001L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729_RTP),
                173124112821552L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729A),
                660416097L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.G729A_RTP),
                11079943513386288L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM),
                163053L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM_MS),
                42743430003L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.GSM_RTP),
                2735579540784L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MAC3),
                11933907L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MAC6),
                11933910L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.TRUESPEECH),
                951058165990316264L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSNAUDIO),
                201466746129007L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.MPEGLAYER3),
                824324187402689683L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC8),
                986256949875448024L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC10),
                7780212570900018256L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC16),
                7780212570900018262L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREAC20),
                7780212570900018320L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREMETAVOICE),
                2985208465502148837L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREMETASOUND),
                2985208465451867044L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWARERT29H),
                -128485452267051416L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREVR12),
                7780212570905584722L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWAREVR18),
                7780212570905584728L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWARETQ40),
                7780212570905056528L);
        assertEquals(
                getEncodingCode(javax.media.format.AudioFormat.VOXWARETQ60),
                7780212570905056656L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MSRT24),
                49187341460L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MPEG),
                12880065248774767L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.MPEG_RTP),
                6476693354317819184L);
        assertEquals(getEncodingCode(javax.media.format.AudioFormat.DOLBYAC3),
                161607319951571L);
        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(VideoFormat.CINEPAK), 9398884L);
        assertEquals(getEncodingCode(VideoFormat.JPEG), 11209063L);
        assertEquals(getEncodingCode(VideoFormat.JPEG_RTP), 188056875248944L);
        assertEquals(getEncodingCode(VideoFormat.MPEG), 11995495L);
        assertEquals(getEncodingCode(VideoFormat.MPEG_RTP), 201251014782256L);
        assertEquals(getEncodingCode(VideoFormat.H261), 10560913L);
        assertEquals(getEncodingCode(VideoFormat.H261_RTP), 177182722698544L);
        assertEquals(getEncodingCode(VideoFormat.H263), 10560915L);
        assertEquals(getEncodingCode(VideoFormat.H263_RTP), 177182756252976L);
        assertEquals(getEncodingCode(VideoFormat.H263_1998_RTP),
                7263538286046424368L);
        assertEquals(getEncodingCode(VideoFormat.RGB), 207330L);
        assertEquals(getEncodingCode(VideoFormat.YUV), 236918L);
        assertEquals(getEncodingCode(VideoFormat.IRGB), 10955234L);
        assertEquals(getEncodingCode(VideoFormat.SMC), 211811L);
        assertEquals(getEncodingCode(VideoFormat.RLE), 207653L);
        assertEquals(getEncodingCode(VideoFormat.RPZA), 13307553L);
        assertEquals(getEncodingCode(VideoFormat.MJPG), 11971623L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGA), 11971617L);
        assertEquals(getEncodingCode(VideoFormat.MJPEGB), 11971618L);
        assertEquals(getEncodingCode(VideoFormat.INDEO32), 10970322L);
        assertEquals(getEncodingCode(VideoFormat.INDEO41), 10970385L);
        assertEquals(getEncodingCode(VideoFormat.INDEO50), 10970448L);
        assertEquals(getEncodingCode(AudioFormat.LINEAR), 47944718450L);
        assertEquals(getEncodingCode(AudioFormat.ULAW), 14076023L);
        assertEquals(getEncodingCode(AudioFormat.ULAW_RTP), 236156482432304L);
        assertEquals(getEncodingCode(AudioFormat.ALAW), 8833143L);
        assertEquals(getEncodingCode(AudioFormat.IMA4), 10934356L);
        assertEquals(getEncodingCode(AudioFormat.IMA4_MS), 2866375883635L);
        assertEquals(getEncodingCode(AudioFormat.MSADPCM), 3147700570349L);
        assertEquals(getEncodingCode(AudioFormat.DVI), 150953L);
        assertEquals(getEncodingCode(AudioFormat.DVI_RTP), 2532575227184L);
        assertEquals(getEncodingCode(AudioFormat.G723), 10318995L);
        assertEquals(getEncodingCode(AudioFormat.G723_RTP), 173124012158256L);
        assertEquals(getEncodingCode(AudioFormat.G728), 10319000L);
        assertEquals(getEncodingCode(AudioFormat.G728_RTP), 173124096044336L);
        assertEquals(getEncodingCode(AudioFormat.G729), 10319001L);
        assertEquals(getEncodingCode(AudioFormat.G729_RTP), 173124112821552L);
        assertEquals(getEncodingCode(AudioFormat.G729A), 660416097L);
        assertEquals(getEncodingCode(AudioFormat.G729A_RTP), 11079943513386288L);
        assertEquals(getEncodingCode(AudioFormat.GSM), 163053L);
        assertEquals(getEncodingCode(AudioFormat.GSM_MS), 42743430003L);
        assertEquals(getEncodingCode(AudioFormat.GSM_RTP), 2735579540784L);
        assertEquals(getEncodingCode(AudioFormat.MAC3), 11933907L);
        assertEquals(getEncodingCode(AudioFormat.MAC6), 11933910L);
        assertEquals(getEncodingCode(AudioFormat.TRUESPEECH),
                951058165990316264L);
        assertEquals(getEncodingCode(AudioFormat.MSNAUDIO), 201466746129007L);
        assertEquals(getEncodingCode(AudioFormat.MPEGLAYER3),
                824324187402689683L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREAC8),
                986256949875448024L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREAC10),
                7780212570900018256L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREAC16),
                7780212570900018262L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREAC20),
                7780212570900018320L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREMETAVOICE),
                2985208465502148837L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREMETASOUND),
                2985208465451867044L);
        assertEquals(getEncodingCode(AudioFormat.VOXWARERT29H),
                -128485452267051416L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREVR12),
                7780212570905584722L);
        assertEquals(getEncodingCode(AudioFormat.VOXWAREVR18),
                7780212570905584728L);
        assertEquals(getEncodingCode(AudioFormat.VOXWARETQ40),
                7780212570905056528L);
        assertEquals(getEncodingCode(AudioFormat.VOXWARETQ60),
                7780212570905056656L);
        assertEquals(getEncodingCode(AudioFormat.MSRT24), 49187341460L);
        assertEquals(getEncodingCode(AudioFormat.MPEG), 12880065248774767L);
        assertEquals(getEncodingCode(AudioFormat.MPEG_RTP),
                6476693354317819184L);
        assertEquals(getEncodingCode(AudioFormat.DOLBYAC3), 161607319951571L);

    }
}
