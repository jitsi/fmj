package ejmf.toolkit.gui.multiimage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * A convenience class to allow a Multi-Image media file to be
 * tested.
 *
 * @see        MultiImageRenderer
 * @see        MultiImageFrame
 * @see        MakeMultiImage
 *
 * @author     Steve Talley
 */
public class ShowMultiImage extends JFrame {
    /**
     * The usage message when run from the command line
     */
    public static String usage =
        "Usage: java ShowMultiImage <in-file>";

    /**
     * Construct a MultiImageRenderer from the given frames and
     * begin playing in this JFrame.
     *
     * @param      frames
     *             The media to display
     */
    public ShowMultiImage(MultiImageFrame[] frames) {
        Container c = getContentPane();
        MultiImageRenderer m =
            new MultiImageRenderer(frames);
        c.add(m, BorderLayout.CENTER);
        pack();
        setVisible(true);
        m.start();
    }

    /**
     * Reads in a multi-image data from a file given on the
     * command line.  Constructs a ShowMultiImage object and
     * displays the media when complete.
     *
     * @param      args[]
     *             A multi-image (.mti) file
     */
    public static void main(String args[]) {
        if(args.length != 1) {
            System.out.println(usage);
            System.exit(0); 
        }

        Vector frameVector = new Vector();

        DataInputStream in = null;

        try {
            in = new DataInputStream(
                new FileInputStream(
                    new File(args[0]) ) );

            try {
                //  Read in the width/height/duration
                in.readInt();
                in.readInt();
                in.readLong();

                //  Read in the frames
                while(true) {
                    //  Read in the length of the frame
                    long length = in.readLong();

                    //  Read in the delay for the frame
                    long nanos = in.readLong();

                    //  Read in the frame image
                    byte[] b = new byte[(int)length];
                    in.readFully(b,0,(int)length);
                    ImageIcon icon = new ImageIcon(b);

                    //  Create a MultiImageFrame object and add to vector
                    MultiImageFrame m = new MultiImageFrame(icon, nanos);
                    frameVector.addElement(m);
                }
            } catch(EOFException e) {}
        }
        
        catch(IOException e) {
            System.err.println("Could not read in data");
            e.printStackTrace();
            System.exit(1);
        }
        
        finally {
            try { in.close(); } catch(IOException e) {}
        }

        //  Copy frames into array
        MultiImageFrame[] frames = new MultiImageFrame[frameVector.size()];
        frameVector.copyInto(frames);

        new ShowMultiImage(frames);
    }
}
