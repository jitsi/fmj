package net.sf.fmj.media.codec.video.jpeg;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.stream.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.util.*;
import net.sf.fmj.utility.*;

import org.w3c.dom.*;

/**
 * JPEG/RTP packetizer codec. Replacement for
 * com.sun.media.codec.video.jpeg.Packetizer.
 *
 * @author Ken Larson
 * @author Martin Harvan
 */
public class Packetizer extends AbstractPacketizer
{
    private class FC implements FormatControl, Owned
    {
        public Component getControlComponent()
        {
            return null;
        }

        public Format getFormat()
        {
            return outputVideoFormat;
        }

        public Object getOwner()
        {
            return Packetizer.this;
        }

        public Format[] getSupportedFormats()
        {
            return null;
        }

        public boolean isEnabled()
        {
            return true;
        }

        public void setEnabled(boolean enabled)
        {
        }

        public Format setFormat(Format format)
        {
            outputVideoFormat = format;

            synchronized (imageBuffer)
            {
                offscreenImage = null;
                imageGraphics = null;
            }

            return outputVideoFormat;
        }
    }

    class JPEGQualityControl implements QualityControl, Owned
    {
        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public Object getOwner()
        {
            return Packetizer.this;
        }

        public float getPreferredQuality()
        {
            return 0.75f;
        }

        public float getQuality()
        {
            return (quality / 100.0f);
        }

        public boolean isTemporalSpatialTradeoffSupported()
        {
            return true;
        }

        public float setQuality(float newQuality)
        {
            quality = Math.round(newQuality * 100.0f);

            // clamp the value
            if (quality > 99)
            {
                quality = 99;
            } else if (quality < 1)
            {
                quality = 1;
            }

            qtable = createQTable(quality);
            param.setEncodeTables(qtable, huffmanDCTables, huffmanACTables);

            return quality;
        }
    }

    private static Node createDri(Node n, int interval)
    {
        IIOMetadataNode dri = new IIOMetadataNode("dri");
        dri.setAttribute("interval", Integer.toString(interval));
        NodeList nl = n.getChildNodes();
        nl.item(1).insertBefore(dri, nl.item(1).getFirstChild());
        return n;
    }

    private static Node find(Node n, String s)
    {
        String[] names = s.split("/");
        String[] current = names[0].split(":");
        if (names == null)
            return null;
        if (names.length == 1)
            return n;
        String newS = "";
        for (int i = 1; i < names.length; i++)
        {
            newS += names[i] + (i == names.length - 1 ? "" : "/");
        }
        if (n.getNodeName().equalsIgnoreCase(current[0])
                && (!(current.length > 1) || current[1].equalsIgnoreCase(n
                        .getNodeValue())))
            return find(n, newS);
        for (int i = 0; i < n.getChildNodes().getLength(); i++)
        {
            Node child = n.getChildNodes().item(i);
            if (child.getNodeName().equalsIgnoreCase(names[0])
                    && (!(current.length > 1) || current[1].equalsIgnoreCase(n
                            .getNodeValue())))
                return find(child, newS);
        }
        return null;
    }

    private static void outputMetadata(Node node, String delimiter)
    {
        System.out.println(delimiter + node.getNodeName());
        delimiter = "  " + delimiter;

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node n = list.item(i);
            if (n.hasChildNodes())
                outputMetadata(n, delimiter);
            System.out.println(delimiter + n.getNodeName());
            if (list.item(i).hasAttributes())
            {
                NamedNodeMap nnm = list.item(i).getAttributes();
                String ndel = "  " + delimiter + "-A:";
                for (int j = 0; j < nnm.getLength(); j++)
                {
                    System.out.println(ndel + nnm.item(j).getNodeName() + ":"
                            + nnm.item(j).getNodeValue());
                }
            }

        }
    }

    private static Node setSamplingFactor(Node n, int hSampleFactor,
            int vSampleFactor)
    {
        Node markerSeq = n.getChildNodes().item(1);
        // markerSeq.
        Node lookingfor = find(markerSeq,
                "markerSequence/sof/componentSpec/HsamplingFactor:1");
        lookingfor.getAttributes().getNamedItem("HsamplingFactor")
                .setNodeValue(Integer.toString(hSampleFactor));
        lookingfor.getAttributes().getNamedItem("VsamplingFactor")
                .setNodeValue(Integer.toString(vSampleFactor));

        return n;
    }

    int j = 0;
    private byte typeSpecific = 0; // not interlaced
    private byte type = 1; // YUV420 JPEGFormat.DEC_420
    private int quality = 75; // quality
    private int currentQuality; // used during process
    private ImageWriter encoder;
    private JPEGHuffmanTable[] huffmanDCTables;
    private JPEGHuffmanTable[] huffmanACTables;
    private JPEGImageWriteParam param;
    private int dri = 0;

    private Buffer temporary = new Buffer();
    private ByteArrayOutputStream os = new ByteArrayOutputStream();
    private MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(
            os);

    // mgodehardt: max MTU in EthernetII is 1500
    // for UDP transport we have these protocols IP/UDP/RTP/JPEG (header sizes
    // 20,8,12,8)
    // the PACKET_SIZE is the size of the JPEG protocol, so we add IP/UDP/RTP
    // (40 bytes )

    private int[] lumaQtable = RFC2035.jpeg_luma_quantizer_normal;

    private int[] chromaQtable = RFC2035.jpeg_chroma_quantizer_normal;
    private static final Logger logger = Logger.getLogger(Packetizer.class
            .getName());

    private BufferToImage bufferToImage;
    private final Format[] supportedInputFormats = new Format[] {
            new RGBFormat(null, -1, Format.byteArray, -1.0f, -1, -1, -1, -1),
            new RGBFormat(null, -1, Format.intArray, -1.0f, -1, -1, -1, -1), };

    private final Format[] supportedOutputFormats = new Format[] { new VideoFormat(
            VideoFormat.JPEG_RTP, null, -1, Format.byteArray, -1.0f), };
    private static final int PACKET_SIZE = 1000; // JMF is using this packet
                                                 // size
    private JPEGQTable[] qtable;

    private static final int RTP_JPEG_RESTART = 0x40;

    private Format outputVideoFormat;

    private VideoFormat currentFormat;

    private BufferedImage offscreenImage;

    private Graphics imageGraphics;

    private Buffer imageBuffer = new Buffer();

    public Packetizer()
    {
        super();
        this.inputFormats = supportedInputFormats;

        addControl(new JPEGQualityControl());
        addControl(new FC());
    }

    @Override
    public void close()
    {
        try
        {
            out.close();
            os.close();
            encoder.dispose(); //
        } catch (IOException e)
        {
            logger.throwing(getClass().getName(), "close", e.getCause());
        }

    }

    private JPEGHuffmanTable[] createACHuffmanTables()
    {
        JPEGHuffmanTable acChm = new JPEGHuffmanTable(RFC2035.chm_ac_codelens,
                RFC2035.chm_ac_symbols);
        JPEGHuffmanTable acLum = new JPEGHuffmanTable(RFC2035.lum_ac_codelens,
                RFC2035.lum_ac_symbols);
        JPEGHuffmanTable[] result = { acLum, acChm };
        return result;

    }

    private JPEGHuffmanTable[] createDCHuffmanTables()
    {
        JPEGHuffmanTable dcChm = new JPEGHuffmanTable(RFC2035.chm_dc_codelens,
                RFC2035.chm_dc_symbols);
        JPEGHuffmanTable dcLum = new JPEGHuffmanTable(RFC2035.lum_dc_codelens,
                RFC2035.lum_dc_symbols);
        JPEGHuffmanTable[] result = { dcLum, dcChm };
        return result;
    }

    private JPEGQTable[] createQTable(int q)
    {
        byte[] lumQ = new byte[64];
        byte[] chmQ = new byte[64];

        RFC2035.MakeTables(q, lumQ, chmQ, RFC2035.jpeg_luma_quantizer_normal,
                RFC2035.jpeg_chroma_quantizer_normal);

        JPEGQTable qtable_luma = new JPEGQTable(
                ArrayUtility.byteArrayToIntArray(lumQ));
        JPEGQTable qtable_chroma = new JPEGQTable(
                ArrayUtility.byteArrayToIntArray(chmQ));
        JPEGQTable[] result = { qtable_luma, qtable_chroma };
        return result;
    }

    @Override
    protected int doBuildPacketHeader(Buffer inputBuffer, byte[] packetBuffer)
    {
        // is this correct, inputBuffer has no format so we use inputFormat ?
        final VideoFormat format = (VideoFormat) inputFormat;
        int width = format.getSize().width;
        int height = format.getSize().height;
        int length = 0;

        if (null != currentFormat)
        {
            width = currentFormat.getSize().width;
            height = currentFormat.getSize().height;
        }

        // TODO: where do we enforce that the width and height are multiples of
        // 8?
        byte widthInBlocks = (byte) (width / 8);
        byte heightInBlocks = (byte) (height / 8);
        final JpegRTPHeader jpegRTPHeader = new JpegRTPHeader(typeSpecific,
                inputBuffer.getOffset(), type, (byte) currentQuality,
                widthInBlocks, heightInBlocks);
        final byte[] bytes = jpegRTPHeader.toBytes();
        System.arraycopy(bytes, 0, packetBuffer, length, bytes.length);
        length += bytes.length;

        // building of Restart Header. This header MUST be present if we are
        // using types 64-127
        // TODO how do we enforce this?
        /*
         * if (dri != 0) { byte[] data = JpegRTPHeader.createRstHeader(dri, 1,
         * 1, 0x3FFF); //that's what this header is initialized to in the
         * example in RFC2435 System.arraycopy(data, 0, packetBuffer, length,
         * data.length); length += data.length; }
         *
         * if (quality >= 128) { byte[] data =
         * JpegRTPHeader.createQHeader(lumaQtable.length+chromaQtable.length,
         * lumaQtable, chromaQtable); //TODO: do we send normal order tables or
         * zig-zag ones? System.arraycopy(data, 0, packetBuffer, length,
         * data.length); length += data.length;
         *
         * }
         */

        return length;
    }

    private void dump(byte[] data, int length)
    {
        int index = 0;
        while (index < length)
        {
            String aString = "";
            for (int i = 0; i < 16; i++)
            {
                String s = Integer.toHexString(data[index++] & 0xFF);
                aString += (s.length() < 2) ? ("0" + s) : s;
                aString += " ";

                if (index >= length)
                {
                    break;
                }
            }
            System.out.println(aString);
        }
        System.out.println(" ");
    }

    @Override
    public String getName()
    {
        return "JPEG/RTP Packetizer";
    }

    @Override
    public Format[] getSupportedOutputFormats(Format input)
    {
        if (input == null)
            return supportedOutputFormats;
        VideoFormat inputCast = (VideoFormat) input;
        final Format[] result = new Format[] { new VideoFormat(
                VideoFormat.JPEG_RTP, inputCast.getSize(), -1,
                Format.byteArray, -1.0f) };

        return result;
    }

    @Override
    public void open()
    {
        setPacketSize(PACKET_SIZE);
        setDoNotSpanInputBuffers(true);
        temporary.setOffset(0);
        encoder = ImageIO.getImageWritersByFormatName("JPEG").next();
        param = new JPEGImageWriteParam(null);
        huffmanACTables = createACHuffmanTables();
        huffmanDCTables = createDCHuffmanTables();
        qtable = createQTable(quality);
        param.setEncodeTables(qtable, huffmanDCTables, huffmanACTables);
        try
        {
            encoder.setOutput(out);
            encoder.prepareWriteSequence(null);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int process(Buffer input, Buffer output)
    {
        if (!checkInputBuffer(input))
        {
            return BUFFER_PROCESSED_FAILED;
        }

        if (isEOM(input))
        {
            propagateEOM(output); // TODO: what about data? can there be any?
            return BUFFER_PROCESSED_OK;
        }

        BufferedImage image = (BufferedImage) bufferToImage.createImage(input);

        try
        {
            if (temporary.getLength() == 0) // start of a new frame
            {
                // mogdehardt: quality and format should not change in a frame
                currentQuality = quality;
                currentFormat = (VideoFormat) outputVideoFormat;

                // video format size change ?
                if (null != currentFormat)
                {
                    if (input.getFormat() instanceof RGBFormat)
                    {
                        int width = currentFormat.getSize().width;
                        int height = currentFormat.getSize().height;

                        synchronized (imageBuffer)
                        {
                            if (null == offscreenImage)
                            {
                                byte[] tempData = new byte[width * height * 3];

                                RGBFormat videoFormat = (RGBFormat) input
                                        .getFormat();
                                RGBFormat newVideoFormat = new RGBFormat(
                                        new Dimension(width, height), -1, null,
                                        -1, -1, -1, -1, -1, -1, width
                                                * videoFormat.getPixelStride(),
                                        -1, -1);
                                RGBFormat vf = (RGBFormat) newVideoFormat
                                        .intersects(videoFormat);

                                imageBuffer.setData(tempData);
                                imageBuffer.setLength(tempData.length);
                                imageBuffer.setFormat(vf);

                                offscreenImage = (BufferedImage) bufferToImage
                                        .createImage(imageBuffer);
                                imageGraphics = offscreenImage.getGraphics();
                            }
                        }

                        imageGraphics.drawImage(image, 0, 0, width, height,
                                null);
                        image = offscreenImage;
                    }
                }

                // mgodehardt: now sends YUV420 (JPEG/RTP type 1) RFC 2435 Page
                // 8, format is JMF compatible

                // i think all these issues were solved ????

                // TODO: this is very inefficient - it allocates a new byte
                // array (or more) every time

                // TODO: trying to get good compression of safexmas.avi frames,
                // but they end up being
                // 10k each at 50% quality. JMF sends them at about 3k each with
                // 74% quality.
                // I think the reason is that JMF is probably encoding the YUV
                // in the jpeg, rather
                // than the 24-bit RGB that FMJ would use when using the
                // ffmpeg-java demux.

                // TODO: we should also use a JPEGFormat explicitly, and honor
                // those params.

                os.reset();

                /*
                 * if (quality >= 128) { //if quality>128 we want to use custom
                 * tables and we might or might not include them in the packet
                 * header (see doBuildHeader()) qtable[0] = new
                 * JPEGQTable(lumaQtable); //TODO where do we set these tables?
                 * qtable[1] = new JPEGQTable(chromaQtable);
                 * param.setEncodeTables(qtable, huffmanDCTables,
                 * huffmanACTables); }
                 */

                /*
                 * IIOMetadata meta = encoder.getDefaultImageMetadata(new
                 * ImageTypeSpecifier(image), param);
                 *
                 * if(dri!=0){
                 * meta.mergeTree("javax_imageio_jpeg_image_1.0",createDri
                 * (meta.getAsTree("javax_imageio_jpeg_image_1.0"),dri)); } if
                 * (type==0||type==64) { Node n =
                 * setSamplingFactor(meta.getAsTree
                 * ("javax_imageio_jpeg_image_1.0"),2,1);
                 * meta.mergeTree("javax_imageio_jpeg_image_1.0",n); }
                 */

                // outputMetadata(meta.getAsTree("javax_imageio_jpeg_image_1.0"),
                // "+--");

                // param.setCompressionMode(ImageWriteParam.MODE_COPY_FROM_METADATA);

                // mgodehardt: compressing image with same quality as specified
                // in the rtp jpeg header
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(currentQuality / 100.0f);

                // encodes image as jpeg
                encoder.write(null, new IIOImage(image, null, null), param);

                byte[] ba = os.toByteArray();
                // /System.out.println(">>>>>>>>>>>>> len=" + ba.length +
                // " quality=" + currentQuality + " " +
                // param.getCompressionQuality());

                // /dump(ba, ba.length);
                // /System.exit(0);
                ba = JpegStripper.removeHeaders(ba);
                // /dump(ba, ba.length);

                temporary.setData(ba);
                temporary.setLength(ba.length);
            }

            final int result = super.process(temporary, output); // TODO if
                                                                 // dri!=0 we
                                                                 // should
                                                                 // packetize it
                                                                 // in a way so
                                                                 // that there
                                                                 // is integral
                                                                 // number of
                                                                 // restart
                                                                 // intervals

            if (result == BUFFER_PROCESSED_OK) // if input is consumed, then it
                                               // must be the last part of the
                                               // frame.
            {
                temporary.setOffset(0);
                output.setFlags(output.getFlags() | Buffer.FLAG_RTP_MARKER);
                // /logger.fine("LAST PACKET IN FRAME, flags=" +
                // Integer.toHexString(output.getFlags()) + " ts=" +
                // output.getTimeStamp());
            } else
            {
                // /logger.fine("     PACKET IN FRAME, flags=" +
                // Integer.toHexString(output.getFlags()) + " ts=" +
                // output.getTimeStamp());
            }

            return result;
        } catch (IOException e)
        {
            e.printStackTrace();
            output.setDiscard(true);
            output.setLength(0);
            return BUFFER_PROCESSED_FAILED;
        }
    }

    @Override
    public Format setInputFormat(Format format)
    {
        final VideoFormat videoFormat = (VideoFormat) format;
        if (videoFormat.getSize() == null)
            return null; // must set a size.
        // logger.fine("FORMAT: " + MediaCGUtils.formatToStr(format));
        // TODO: check VideoFormat and compatibility
        bufferToImage = new BufferToImage((VideoFormat) format);
        return super.setInputFormat(format);
    }

    private void setType(int typeValue)
    {
        type = (byte) (typeValue | ((dri != 0) ? RTP_JPEG_RESTART : 0));
    }
}
