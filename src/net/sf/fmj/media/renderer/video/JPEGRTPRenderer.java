package net.sf.fmj.media.renderer.video;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.*;
import javax.imageio.stream.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.renderer.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.codec.video.jpeg.*;
import net.sf.fmj.utility.*;

/**
 *
 * Fast JPEG/RTP processing, depacketize and renders the JPEG/RTP stream
 *
 * @author mgodehardt
 *
 */
public class JPEGRTPRenderer extends AbstractVideoRenderer implements
        VideoRenderer
{
    private class JPEGRTPFrame
    {
        // container
        public JPEGRTPFrame firstItem;
        public long timestamp;
        public boolean hasRTPMarker;
        public int count;
        public int dataLength;

        // item
        public JPEGRTPFrame nextItem;
        public Buffer itemData;
        public long fragmentOffset;

        byte[] jpegHeader = {
                (byte) 0xff,
                (byte) 0xd8, // SOI
                (byte) 0xff,
                (byte) 0xe0, // APP0
                (byte) 0x00,
                (byte) 0x10,
                (byte) 0x4a,
                (byte) 0x46,
                (byte) 0x49,
                (byte) 0x46,
                (byte) 0x00, // JFIF
                (byte) 0x01,
                (byte) 0x02, // version 1.2
                (byte) 0x00, // No units, aspect ratio only specified
                (byte) 0x00,
                (byte) 0x01, // Integer horizontal pixel density
                (byte) 0x00,
                (byte) 0x01, // Integer vertical pixel density
                (byte) 0x00,
                (byte) 0x00, // thumbnail width and height
                (byte) 0xff,
                (byte) 0xc0, // SOF0
                (byte) 0x00,
                (byte) 0x11,
                (byte) 0x08,
                (byte) 0x00,
                (byte) 0x90, // height
                (byte) 0x00,
                (byte) 0xb0, // width
                (byte) 0x03, // 3 components
                (byte) 0x01,
                (byte) 0x22,
                (byte) 0x00, // comp #1
                (byte) 0x02,
                (byte) 0x11,
                (byte) 0x01, // comp #2
                (byte) 0x03,
                (byte) 0x11,
                (byte) 0x01, // comp #3
                (byte) 0xff, (byte) 0xda, (byte) 0x00, (byte) 0x0c,
                (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x02,
                (byte) 0x11, (byte) 0x03, (byte) 0x11, (byte) 0x00,
                (byte) 0x3f, (byte) 0x00 };

        public JPEGRTPFrame(Buffer buffer)
        {
            itemData = buffer;
        }

        public JPEGRTPFrame(long timestamp)
        {
            this.timestamp = timestamp;
        }

        public void add(Buffer buffer)
        {
            JPEGRTPFrame aNewItem = new JPEGRTPFrame((Buffer) buffer.clone());

            if ((buffer.getFlags() & Buffer.FLAG_RTP_MARKER) > 0)
            {
                hasRTPMarker = true;
            }
            count++;

            if (null == firstItem)
            {
                firstItem = aNewItem;
            } else
            {
                JPEGRTPFrame aItem = firstItem;
                while (aItem.nextItem != null)
                {
                    aItem = aItem.nextItem;
                }

                aItem.nextItem = aNewItem;
            }

            byte[] data = (byte[]) buffer.getData();

            aNewItem.fragmentOffset = 0;
            for (int i = 0; i < 3; i++)
            {
                aNewItem.fragmentOffset <<= 8;
                aNewItem.fragmentOffset += data[i + 13] & 0xff;
            }

            // /System.out.println(">>> added packet seq=" +
            // buffer.getSequenceNumber() + " len=" + buffer.getLength() +
            // " ofs=" + aNewItem.fragmentOffset);
            // /dump((byte[])buffer.getData(), buffer.getLength() +
            // buffer.getOffset());
        }

        public void clear(long timestamp)
        {
            firstItem = null;
            this.timestamp = timestamp;
            hasRTPMarker = false;
            count = 0;
            dataLength = 0;
        }

        public byte[] getData()
        {
            byte[] frame = new byte[jpegHeader.length + dataLength + 2];
            System.arraycopy(jpegHeader, 0, frame, 0, jpegHeader.length);

            JPEGRTPFrame aItem = firstItem;
            long expectedFragmentOffset = 0;
            int offset = jpegHeader.length;

            // copy data from packets into frame
            while (aItem != null)
            {
                aItem = firstItem;
                while (aItem != null)
                {
                    if (aItem.fragmentOffset == expectedFragmentOffset)
                    {
                        break;
                    }
                    aItem = aItem.nextItem;
                }

                if (null != aItem)
                {
                    int len = aItem.itemData.getLength() - 8;

                    byte[] data = (byte[]) aItem.itemData.getData();
                    System.arraycopy(data, aItem.itemData.getOffset() + 8,
                            frame, offset, len);

                    offset += len;
                    expectedFragmentOffset += len;
                }
            }

            byte[] packetData = (byte[]) firstItem.itemData.getData();

            // insert width and height
            int width = packetData[firstItem.itemData.getOffset() + 6] << 3;
            int height = packetData[firstItem.itemData.getOffset() + 7] << 3;

            frame[25] = (byte) ((height >> 8) & 0xFF);
            frame[26] = (byte) (height & 0xFF);

            frame[27] = (byte) ((width >> 8) & 0xFF);
            frame[28] = (byte) (width & 0xFF);

            // add EOI
            frame[frame.length - 2] = (byte) 0xFF;
            frame[frame.length - 1] = (byte) 0xD9;

            // init tables
            int q = packetData[firstItem.itemData.getOffset() + 5];
            if (null == decoder)
            {
                initDecoder(q);
            }

            // did the quality change
            if ((quality != -1) && (q != quality))
            {
                initDecoder(q);
            }
            quality = q;

            if (null == itsImage)
            {
                itsImage = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);
            }

            return frame;
        }

        public boolean isComplete()
        {
            if (hasRTPMarker)
            {
                JPEGRTPFrame aItem = firstItem;
                long expectedFragmentOffset = 0;
                dataLength = 0;

                // check if packets are continous
                while (aItem != null)
                {
                    aItem = firstItem;
                    while (aItem != null)
                    {
                        if (aItem.fragmentOffset == expectedFragmentOffset)
                        {
                            break;
                        }
                        aItem = aItem.nextItem;
                    }

                    if (null != aItem)
                    {
                        int len = aItem.itemData.getLength() - 8;
                        dataLength += len;

                        if ((aItem.itemData.getFlags() & Buffer.FLAG_RTP_MARKER) > 0)
                        {
                            return true;
                        }

                        expectedFragmentOffset += len;
                    }
                }
            }
            return false;
        }
    }

    private class VideoFrameRateControl implements FrameRateControl, Owned
    {
        public java.awt.Component getControlComponent()
        {
            return null;
        }

        public float getFrameRate()
        {
            return frameRate;
        }

        public float getMaxSupportedFrameRate()
        {
            return -1;
        }

        public Object getOwner()
        {
            return JPEGRTPRenderer.this;
        }

        public float getPreferredFrameRate()
        {
            return -1;
        }

        public float setFrameRate(float newFrameRate)
        {
            return -1;
        }
    }

    private static final Logger logger = LoggerSingleton.logger;

    private static final boolean TRACE = true;

    private final Format[] supportedInputFormats = new Format[] { new VideoFormat(
            VideoFormat.JPEG_RTP, null, -1, Format.byteArray, -1.0f) };

    private JVideoComponent component = new JVideoComponent();
    private JPEGRTPFrame currentFrame;
    private ImageReader decoder;
    private JPEGImageReadParam param;
    private JPEGHuffmanTable[] huffmanDCTables;
    private JPEGHuffmanTable[] huffmanACTables;

    private JPEGQTable[] qtable;
    private int quality = -1; // last seen quality
    private float frameRate = -1;

    private int framesProcessed;

    private long lastTimestamp;

    private BufferedImage itsImage;

    // ctor
    public JPEGRTPRenderer()
    {
        addControl(this);
        addControl(new VideoFrameRateControl());
    }

    @Override
    public void close()
    {
        if (null != decoder)
        {
            decoder.dispose();
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
    public int doProcess(Buffer buffer)
    {
        long timestamp = buffer.getTimeStamp();
        if (null == currentFrame)
        {
            currentFrame = new JPEGRTPFrame(timestamp);
        }

        if (timestamp < currentFrame.timestamp) // drop packets for older frames
        {
            if (TRACE)
                logger.fine("JPEGRTPRenderer: dropping packet ts=" + timestamp);
        } else if (timestamp > currentFrame.timestamp) // packets of a new frame
        {
            if (TRACE)
                logger.fine("JPEGRTPRenderer: dropping current frame ts="
                        + currentFrame.timestamp + ", got new packet ts="
                        + timestamp);

            currentFrame.clear(timestamp);
            currentFrame.add(buffer);
        } else
        // packet for the current frame
        {
            currentFrame.add(buffer);
        }

        if (currentFrame.isComplete())
        {
            byte[] data = currentFrame.getData();
            // /dump(data, data.length);

            // /if (TRACE) logger.fine("JPEGRTPRenderer: frame complete ts=" +
            // currentFrame.timestamp + " data len=" + data.length);
            currentFrame = null;

            try
            {
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ImageInputStream stream = ImageIO.createImageInputStream(in);

                decoder.setInput(stream, false, false);
                param.setDestination(itsImage);
                decoder.read(0, param);

                component.setImage(itsImage);

                stream.close();
                in.close();

                // mgodehardt: will measure the real framerate
                long currentTimestamp = System.nanoTime();
                if (-1 == lastTimestamp)
                {
                    lastTimestamp = currentTimestamp;
                }

                framesProcessed++;

                if ((currentTimestamp - lastTimestamp) > 1000000000L)
                {
                    float diffTime = (float) (currentTimestamp - lastTimestamp) / 1000000L;
                    frameRate = framesProcessed * (1000.0f / diffTime);

                    framesProcessed = 0;
                    lastTimestamp = currentTimestamp;
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return BUFFER_PROCESSED_OK;
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
    public Component getComponent()
    {
        return component;
    }

    // @Override
    @Override
    public String getName()
    {
        return "JPEG/RTP Renderer";
    }

    // @Override
    @Override
    public Format[] getSupportedInputFormats()
    {
        return supportedInputFormats;
    }

    private void initDecoder(int q)
    {
        if (null != decoder)
        {
            decoder.dispose();
        }

        decoder = ImageIO.getImageReadersByFormatName("JPEG").next();
        param = new JPEGImageReadParam();
        huffmanACTables = createACHuffmanTables();
        huffmanDCTables = createDCHuffmanTables();
        qtable = createQTable(q);
        param.setDecodeTables(qtable, huffmanDCTables, huffmanACTables);
    }

    // @Override
    @Override
    public Format setInputFormat(Format format)
    {
        VideoFormat chosenFormat = (VideoFormat) super.setInputFormat(format);
        if (chosenFormat != null)
        {
            getComponent().setPreferredSize(chosenFormat.getSize());
        }
        return chosenFormat;
    }
}
