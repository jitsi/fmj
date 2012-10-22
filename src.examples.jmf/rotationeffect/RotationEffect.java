package rotationeffect;

/*
 * @(#)RotationEffect.java	1.3 01/04/27
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */


import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

public class RotationEffect implements Effect {
    Format inputFormat;
    Format outputFormat;
    Format[] inputFormats;
    Format[] outputFormats;

    double angle = 0.0;
    double[] sinTable;
    double[] cosTable;
    double[] rateTable;
    private int count = 0;
    private int num;

    public RotationEffect() {
        this(20);
    }

    public RotationEffect(int num) {
        if ( num <= 0 )
            this.num = 20;
        else
            this.num = num;
        this.angle = 2.0*3.1415926/this.num;
        buildTable();

        inputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

        outputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

    }

    // methods for interface Codec
    public Format[] getSupportedInputFormats() {
	return inputFormats;
    }

    public Format [] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return outputFormats;
        }
        
        if (matches(input, inputFormats) != null) {
            return new Format[] { outputFormats[0].intersects(input) };
        } else {
            return new Format[0];
        }
    }

    public Format setInputFormat(Format input) {
	inputFormat = input;
	return input;
    }

    public Format setOutputFormat(Format output) {
        if (output == null || matches(output, outputFormats) == null)
            return null;
        RGBFormat incoming = (RGBFormat) output;
        
        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null)
            return null;
        if (maxDataLength < size.width * size.height * 3)
            maxDataLength = size.width * size.height * 3;
        if (lineStride < size.width * 3)
            lineStride = size.width * 3;
        if (flipped != Format.FALSE)
            flipped = Format.FALSE;
        
        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                                                        maxDataLength,
                                                        null,
                                                        frameRate,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        lineStride,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED));

        //System.out.println("final outputformat = " + outputFormat);
        return outputFormat;
    }


    public int process(Buffer inBuffer, Buffer outBuffer) {
        int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
        validateByteArraySize(outBuffer, outputDataLength);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setFlags(inBuffer.getFlags());

        byte [] inData = (byte[]) inBuffer.getData();
        byte [] outData = (byte[]) outBuffer.getData();
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        int pixStrideIn = vfIn.getPixelStride();
        int lineStrideIn = vfIn.getLineStride();

        int iw = sizeIn.width;
        int ih = sizeIn.height;
        int cx = iw/2;
        int cy = ih/2;
        int ip = 0;
        int op = 0;
        int x, y;

        double vsin, vcos, ratio;
        if ( outData.length < iw*ih*3 ) {
            System.out.println("the buffer is not full");
            return BUFFER_PROCESSED_FAILED;
        }
            
        // System.out.println("count = " + count);
        vsin = sinTable[count];
        vcos = cosTable[count];
        ratio = 1.0;//rateTable[count];
        // System.out.println("vsin = " + vsin + " vcos = " + vcos);

        for ( int j = -cy; j < ih-cy; j++ )
            for ( int i = -cx; i < iw-cx; i++ ) {
                x = (int)((vcos * i - vsin * j)*ratio + cx + 0.5);
                y = (int)((vsin * i + vcos * j)*ratio + cy + 0.5);
                
                if ( x < 0 || x >= iw || y < 0 || y >= ih) {
                    outData[op++] = 0;
                    outData[op++] = 0;
                    outData[op++] = 0;
                } else {
                    ip = lineStrideIn * y + x * pixStrideIn;
                    outData[op++] = inData[ip++];
                    outData[op++] = inData[ip++];
                    outData[op++] = inData[ip++];
                }
            }
        

        count ++;
        if ( count >= num )
            count = 0;
        
        return BUFFER_PROCESSED_OK;
        
    }
    
    // methods for interface PlugIn
    public String getName() {
        return "Rotation Effect";
    }

    public void open() {
    }

    public void close() {
    }

    public void reset() {
    }

    // methods for interface javax.media.Controls
    public Object getControl(String controlType) {
	return null;
    }

    public Object[] getControls() {
	return null;
    }


    // Utility methods.
    Format matches(Format in, Format outs[]) {
	for (int i = 0; i < outs.length; i++) {
	    if (in.matches(outs[i]))
		return outs[i];
	}
	
	return null;
    }
    
    
    byte[] validateByteArraySize(Buffer buffer,int newSize) {
        Object objectArray=buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[]) {     // is correct type AND not null
            typedArray=(byte[])objectArray;
            if (typedArray.length >= newSize ) { // is sufficient capacity
                return typedArray;
            }

            byte[] tempArray=new byte[newSize];  // re-alloc array
            System.arraycopy(typedArray,0,tempArray,0,typedArray.length);
            typedArray = tempArray;
        } else {
            typedArray = new byte[newSize];
        }

        buffer.setData(typedArray);
        return typedArray;
    }

    private void buildTable() {
        double aa ;
        sinTable = new double[num];
        cosTable = new double[num];
        rateTable = new double[num];
        for (int i = 0; i < num; i++)
            rateTable[i] = 1.0;
        for ( int i = 0; i < num; i++) {
            aa = i * angle;
            sinTable[i] = Math.sin(aa);
            cosTable[i] = Math.cos(aa);
        }

        for ( int i = 0; i < num/2; i++) 
            rateTable[i] = (1.0+0.15*i);
    
        for ( int i = num-1; i >= num/2; i--)
            rateTable[i] = (1.0 + 0.15*(num-1-i));
    }
}
