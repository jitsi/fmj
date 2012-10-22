package ejmf.toolkit.gui.multiimage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

/**
 * A conveniece class for creating a multi-image (.mti) file.
 * <p>
 * Usage:
 * <blockquote><pre>
 * java MakeMultiImage <out-file> <frame-delay> <in-files...>
 * </pre></blockquote>
 * The frame-delay is in nanoseconds.
 *
 * @see        MultiImageComponent
 * @see        MultiImageFrame
 * @see        ShowMultiImage
 *
 * @author     Steve Talley
 */
public class MakeMultiImage {
    /**
     * The usage message when run from the command line
     */
    public static String usage =
        "Usage: java MakeMultiImage <out-file> <frame-delay-nanos> <in-files...>";

    /**
     * Creates a multi-image (.mit) file based on the given frame
     * delay and input files.  The input files may be of any
     * format which the JDK can use to create an Image.
     *
     * @param      args[]
     *             See the above usage
     *
     */
    public static void main(String args[]) {
        if(args.length < 3) {
            System.out.println(usage);
            System.exit(0); 
        }

        //  Get the delay (in nanoseconds) from the command line
        long delay = Long.valueOf(args[1]).longValue();

        DataOutputStream bufferOut = null;
        DataOutputStream fileOut = null;

        try {
            ByteArrayOutputStream bout;

            //  Initialize the buffer stream
            bufferOut = new DataOutputStream(
                bout = new ByteArrayOutputStream() );

            //  Initialize the file stream
            fileOut = new DataOutputStream(
                new FileOutputStream(
                    new File(args[0]) ) );

            int maxw = 0;
            int maxh = 0;

            //  For every file on the command line...
            for(int i = 2; i < args.length; i++ )
            {
                //  Read in the file
                File f = new File(args[i]);
                byte[] b = fileToByteArray(f);

                //  Create an image and store the maximum dimensions
                ImageIcon icon = new ImageIcon(b);
                int w = icon.getIconWidth();
                int h = icon.getIconHeight();
                if( w > maxw ) maxw = w;
                if( h > maxh ) maxh = h;

                //  Write the frame header and the frame to a byte array
                bufferOut.writeLong(b.length);
                bufferOut.writeLong(delay);
                bufferOut.write(b,0,b.length);
            } 

            //  Write out the file header
            fileOut.writeInt(maxw);
            fileOut.writeInt(maxh);
            fileOut.writeLong((long)(delay * args.length));

            //  Write out the file itself
            fileOut.write( bout.toByteArray() );

            try { bout.close(); } catch(IOException e) {}
        }
        
        catch(IOException e) {
            e.printStackTrace();
            System.err.println("Could not read/write data");
            System.exit(1);
        }
        
        finally {
            try { bufferOut.close(); } catch(IOException e) {}
            try { fileOut.close(); } catch(IOException e) {}
        }

        //  This is necessary since creating an ImageIcon without
        //  displaying it creates a non-daemon thread
        System.exit(0);
    }

    /**
     * Opens a file and loads the contents into a byte array.
     *
     * @param      f
     *             The file to open
     *
     * @return     A byte array containing the contents of the file.
     *
     * @exception  IOException
     *             If the file could not be read.
     */
    public static byte[] fileToByteArray(File f)
        throws IOException
    { 
        byte[] b = new byte[(int)f.length()];
        FileInputStream is = new FileInputStream(f);
        is.read(b);
        is.close();
        return b;
    }
}
