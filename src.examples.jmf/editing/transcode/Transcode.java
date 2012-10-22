package editing.transcode;

/*
 * @(#)Transcode.java	1.6 01/03/13
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

import java.awt.*;
import java.util.Vector;
import java.io.File;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.control.QualityControl;
import javax.media.Format;
import javax.media.format.*;
import javax.media.datasink.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import java.io.IOException;



/**
 * A sample program to transcode an input source to an output location
 * with different data formats.
 */
public class Transcode implements ControllerListener, DataSinkListener {

    /**
     * Given a source media locator, destination media locator and
     * an array of formats, this method will transcode the source to
     * the dest into the specified formats. 
     */
    public boolean doIt(MediaLocator inML, MediaLocator outML, Format fmts[],
		int start, int end) {

	Processor p;

	try {
	    System.err.println("- create processor for: " + inML);
	    p = Manager.createProcessor(inML);
	} catch (Exception e) {
	    System.err.println("Yikes!  Cannot create a processor from the given url: " + e);
	    return false;
	}

	p.addControllerListener(this);

	// Put the Processor into configured state.
	p.configure();
	if (!waitForState(p, Processor.Configured)) {
	    System.err.println("Failed to configure the processor.");
	    return false;
	}

	// Set the output content descriptor based on the media locator.
	setContentDescriptor(p, outML);

	// Program the tracks to the given output formats.
	if (!setTrackFormats(p, fmts))
	    return false;

	// We are done with programming the processor.  Let's just
	// realize the it.
	p.realize();
	if (!waitForState(p, Controller.Realized)) {
	    System.err.println("Failed to realize the processor.");
	    return false;
	}

	// Set the JPEG quality to .5.
	setJPEGQuality(p, 0.5f);

	// Now, we'll need to create a DataSink.
	DataSink dsink;
	if ((dsink = createDataSink(p, outML)) == null) {
	    System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
	    return false;
	}

	dsink.addDataSinkListener(this);
	fileDone = false;

	// Set the start time if there's one set.
	if (start > 0)
	    p.setMediaTime(new Time((double)start));

	// Set the stop time if there's one set.
	if (end > 0)
	    p.setStopTime(new Time((double)end));

	System.err.println("start transcoding...");

	// OK, we can now start the actual transcoding.
	try {
	    p.start();
	    dsink.start();
	} catch (IOException e) {
	    System.err.println("IO error during transcoding");
	    return false;
	}

	// Wait for EndOfStream event.
	waitForFileDone();

	// Cleanup.
	try {
	    dsink.close();
	} catch (Exception e) {}
	p.removeControllerListener(this);

	System.err.println("...done transcoding.");

	return true;
    }


    /**
     * Set the content descriptor based on the given output MediaLocator.
     */
    void setContentDescriptor(Processor p, MediaLocator outML) {

 	ContentDescriptor cd;

	// If the output file maps to a content type,
	// we'll try to set it on the processor.

	if ((cd = fileExtToCD(outML.getRemainder())) != null) {

	    System.err.println("- set content descriptor to: " + cd);

	    if ((p.setContentDescriptor(cd)) == null) {

		// The processor does not support the output content
		// type.  But we can set the content type to RAW and 
		// see if any DataSink supports it.

		p.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
	    }
	}
    }


    /**
     * Set the target transcode format on the processor.
     */
    boolean setTrackFormats(Processor p, Format fmts[]) {

	if (fmts.length == 0)
	    return true;

	TrackControl tcs[];

	if ((tcs = p.getTrackControls()) == null) {
	    // The processor does not support any track control.
	    System.err.println("The Processor cannot transcode the tracks to the given formats");
	    return false;
	}

 	for (int i = 0; i < fmts.length; i++) {	

	    System.err.println("- set track format to: " + fmts[i]);

	    if (!setEachTrackFormat(p, tcs, fmts[i])) {
		System.err.println("Cannot transcode any track to: " + fmts[i]);
		return false;
	    }
	}

	return true;
    }


    /**
     * We'll loop through the tracks and try to find a track
     * that can be converted to the given format.
     */
    boolean setEachTrackFormat(Processor p, TrackControl tcs[], Format fmt) {

	Format supported[];
	Format f;

	for (int i = 0; i < tcs.length; i++) {

	    supported = tcs[i].getSupportedFormats();

	    if (supported == null)
		continue;

	    for (int j = 0; j < supported.length; j++) {

		if (fmt.matches(supported[j]) &&
		    (f = fmt.intersects(supported[j])) != null &&
		    tcs[i].setFormat(f) != null) {

		    // Success.
		    return true;
		}
	    }
	}

	return false;
    }


    /**
     * Setting the encoding quality to the specified value on the JPEG encoder.
     * 0.5 is a good default.
     */
    void setJPEGQuality(Player p, float val) {

	Control cs[] = p.getControls();
	QualityControl qc = null;
	VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);

	// Loop through the controls to find the Quality control for
 	// the JPEG encoder.
	for (int i = 0; i < cs.length; i++) {

	    if (cs[i] instanceof QualityControl &&
		cs[i] instanceof Owned) {
		Object owner = ((Owned)cs[i]).getOwner();

		// Check to see if the owner is a Codec.
		// Then check for the output format.
		if (owner instanceof Codec) {
		    Format fmts[] = ((Codec)owner).getSupportedOutputFormats(null);
		    for (int j = 0; j < fmts.length; j++) {
			if (fmts[j].matches(jpegFmt)) {
			    qc = (QualityControl)cs[i];
	    		    qc.setQuality(val);
			    System.err.println("- Set quality to " + 
					val + " on " + qc);
			    break;
			}
		    }
		}
		if (qc != null)
		    break;
	    }
	}
    }


    /**
     * Create the DataSink.
     */
    DataSink createDataSink(Processor p, MediaLocator outML) {

	DataSource ds;

	if ((ds = p.getDataOutput()) == null) {
	    System.err.println("Something is really wrong: the processor does not have an output DataSource");
	    return null;
	}

	DataSink dsink;

	try {
	    System.err.println("- create DataSink for: " + outML);
	    dsink = Manager.createDataSink(ds, outML);
	    dsink.open();
	} catch (Exception e) {
	    System.err.println("Cannot create the DataSink: " + e);
	    return null;
	}

	return dsink;
    }


    Object waitSync = new Object();
    boolean stateTransitionOK = true;

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }


    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {

	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof EndOfMediaEvent) {
	    evt.getSourceController().close();
	} else if (evt instanceof MediaTimeSetEvent) {
	    System.err.println("- mediaTime set: " + 
		((MediaTimeSetEvent)evt).getMediaTime().getSeconds());
	} else if (evt instanceof StopAtTimeEvent) {
	    System.err.println("- stop at time: " +
		((StopAtTimeEvent)evt).getMediaTime().getSeconds());
	    evt.getSourceController().close();
	}
    }


    Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;

    /**
     * Block until file writing is done. 
     */
    boolean waitForFileDone() {
	System.err.print("  ");
	synchronized (waitFileSync) {
	    try {
		while (!fileDone) {
		    waitFileSync.wait(1000);
		    System.err.print(".");
		}
	    } catch (Exception e) {}
	}
	System.err.println("");
	return fileSuccess;
    }


    /**
     * Event handler for the file writer.
     */
    public void dataSinkUpdate(DataSinkEvent evt) {

	if (evt instanceof EndOfStreamEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		waitFileSync.notifyAll();
	    }
	} else if (evt instanceof DataSinkErrorEvent) {
	    synchronized (waitFileSync) {
		fileDone = true;
		fileSuccess = false;
		waitFileSync.notifyAll();
	    }
	}
    }


    /**
     * Convert a file name to a content type.  The extension is parsed
     * to determine the content type.
     */
    ContentDescriptor fileExtToCD(String name) {

	String ext;
	int p;

	// Extract the file extension.
	if ((p = name.lastIndexOf('.')) < 0)
	    return null;

	ext = (name.substring(p + 1)).toLowerCase();

	String type;

	// Use the MimeManager to get the mime type from the file extension.
	if ( ext.equals("mp3")) {
	    type = FileTypeDescriptor.MPEG_AUDIO;
	} else {
	    if ((type = com.sun.media.MimeManager.getMimeType(ext)) == null)
		return null;
	    type = ContentDescriptor.mimeTypeToPackageName(type);
	}

	return new FileTypeDescriptor(type);
    }


    /**
     * Main program
     */
    public static void main(String [] args) {

	String inputURL = null, outputURL = null;
	int mediaStart = -1, mediaEnd = -1;
	Vector audFmt = new Vector(), vidFmt = new Vector();

	if (args.length == 0)
	    prUsage();

	// Parse the arguments.
	int i = 0;
	while (i < args.length) {

	    if (args[i].equals("-v")) {
		i++;
		if (i >= args.length)
		    prUsage();
		vidFmt.addElement(args[i]);
	    } else if (args[i].equals("-a")) {
		i++;
		if (i >= args.length)
		    prUsage();
		audFmt.addElement(args[i]);
	    } else if (args[i].equals("-o")) {
		i++;
		if (i >= args.length)
		    prUsage();
		outputURL = args[i];
	    } else if (args[i].equals("-s")) {
		i++;
		if (i >= args.length)
		    prUsage();
		Integer integer = Integer.valueOf(args[i]);
		if (integer != null)
		    mediaStart = integer.intValue();
	    } else if (args[i].equals("-e")) {
		i++;
		if (i >= args.length)
		    prUsage();
		Integer integer = Integer.valueOf(args[i]);
		if (integer != null)
		    mediaEnd = integer.intValue();
	    } else {
		inputURL = args[i];
	    }
	    i++;
	}

	if (inputURL == null) {
	    System.err.println("No input url is specified");
	    prUsage();
	}

	if (outputURL == null) {
	    System.err.println("No output url is specified");
	    prUsage();
	}

	int j = 0;
	Format fmts[] = new Format[audFmt.size() + vidFmt.size()];
	Format fmt;

	// Parse the audio format spec. into real AudioFormat's.
	for (i = 0; i < audFmt.size(); i++) {

	    if ((fmt = parseAudioFormat((String)audFmt.elementAt(i))) == null) {
		System.err.println("Invalid audio format specification: " +
						(String)audFmt.elementAt(i));
		prUsage();
	    }
	    fmts[j++] = fmt;
	}

	// Parse the video format spec. into real VideoFormat's.
	for (i = 0; i < vidFmt.size(); i++) {

	    if ((fmt = parseVideoFormat((String)vidFmt.elementAt(i))) == null) {
		System.err.println("Invalid video format specification: " +
						(String)vidFmt.elementAt(i));
		prUsage();
	    }
	    fmts[j++] = fmt;
	}

	// Generate the input and output media locators.
	MediaLocator iml, oml;

	if ((iml = createMediaLocator(inputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + inputURL);
	    System.exit(0);
	}

	if ((oml = createMediaLocator(outputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + outputURL);
	    System.exit(0);
	}

	// Trancode with the specified parameters.
	Transcode transcode  = new Transcode();

	if (!transcode.doIt(iml, oml, fmts, mediaStart, mediaEnd)) {
	    System.err.println("Transcoding failed");
	}

	System.exit(0);
    }


    /**
     * Create a media locator from the given string.
     */
    static MediaLocator createMediaLocator(String url) {

	MediaLocator ml;

	if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
	    return ml;

	if (url.startsWith(File.separator)) {
	    if ((ml = new MediaLocator("file:" + url)) != null)
		return ml;
	} else {
	    String file = "file:" + System.getProperty("user.dir") + File.separator + url;
	    if ((ml = new MediaLocator(file)) != null)
		return ml;
	}

	return null;
    }


    /**
     * Parse the audio format specifier and generate an AudioFormat.
     * A valid audio format specifier is of the form:
     * [encoding]:[rate]:[sizeInBits]:[channels]:[big|little]:[signed|unsigned]
     */
    static Format parseAudioFormat(String fmtStr) {

	int rate, bits, channels, endian, signed;

	String encodeStr = null, rateStr = null, 
		bitsStr = null, channelsStr = null,
		endianStr = null, signedStr = null;

	// Parser the media locator to extract the requested format.

	if (fmtStr != null && fmtStr.length() > 0) {
	    while (fmtStr.length() > 1 && fmtStr.charAt(0) == ':')
		fmtStr = fmtStr.substring(1);

	    // Now see if there's a encode rate specified.
	    int off = fmtStr.indexOf(':');
	    if (off == -1) {
		if (!fmtStr.equals(""))
		    encodeStr = fmtStr;
	    } else {
		encodeStr = fmtStr.substring(0, off);
		fmtStr = fmtStr.substring(off + 1);
		// Now see if there's a sample rate specified
		off = fmtStr.indexOf(':');
		if (off == -1) {
		    if (!fmtStr.equals(""))
			rateStr = fmtStr;
		} else {
		    rateStr = fmtStr.substring(0, off);
		    fmtStr = fmtStr.substring(off + 1);
		    // Now see if there's a size specified
		    off = fmtStr.indexOf(':');
		    if (off == -1) {
			if (!fmtStr.equals(""))
			    bitsStr = fmtStr;
		    } else {
			bitsStr = fmtStr.substring(0, off);
			fmtStr = fmtStr.substring(off + 1);
			// Now see if there's channels specified.
			off = fmtStr.indexOf(':');
			if (off == -1) {
			    if (!fmtStr.equals(""))
				channelsStr = fmtStr;
			} else {
			    channelsStr = fmtStr.substring(0, off);
			    fmtStr = fmtStr.substring(off + 1);
			    // Now see if there's endian specified.
			    off = fmtStr.indexOf(':');
			    if (off == -1) {
				if (!fmtStr.equals(""))
			            endianStr = fmtStr.substring(off + 1);
			    } else {
			    	endianStr = fmtStr.substring(0, off);
				if (!fmtStr.equals(""))
			            signedStr = fmtStr.substring(off + 1);
			    }
			}
		   }
		}
	    }
	}

	// Sample Rate
	rate = AudioFormat.NOT_SPECIFIED;
	if (rateStr != null) {
	    try {
		Integer integer = Integer.valueOf(rateStr);
		if (integer != null)
		    rate = integer.intValue();
	    } catch (Throwable t) { }
	}

	// Sample Size
	bits = AudioFormat.NOT_SPECIFIED;
	if (bitsStr != null) {
	    try {
		Integer integer = Integer.valueOf(bitsStr);
		if (integer != null)
		    bits = integer.intValue();
	    } catch (Throwable t) { }
	}

	// # of channels
	channels = AudioFormat.NOT_SPECIFIED;
	if (channelsStr != null) {
	    try {
		Integer integer = Integer.valueOf(channelsStr);
		if (integer != null)
		    channels = integer.intValue();
	    } catch (Throwable t) { }
	}

	// Endian
    	endian = AudioFormat.NOT_SPECIFIED;
	if (endianStr != null) {
	    if (endianStr.equalsIgnoreCase("big"))
    		endian = AudioFormat.BIG_ENDIAN;
	    else if (endianStr.equalsIgnoreCase("little"))
		endian = AudioFormat.LITTLE_ENDIAN;
	}

	// Signed
    	signed = AudioFormat.NOT_SPECIFIED;
	if (signedStr != null) {
	    if (signedStr.equalsIgnoreCase("signed"))
		signed = AudioFormat.SIGNED;
	    else if (signedStr.equalsIgnoreCase("unsigned"))
    		signed = AudioFormat.UNSIGNED;
	}

	return new AudioFormat(encodeStr, rate, bits, channels, endian, signed);
    }


    /**
     * Parse the video format specifier and generate an VideoFormat.
     * A valid video format specifier is of the form:
     * [encoding]:[widthXheight]
     */
    static Format parseVideoFormat(String fmtStr) {

	String encodeStr = null, sizeStr = null;

	// Parser the media locator to extract the requested format.

	if (fmtStr != null && fmtStr.length() > 0) {
	    while (fmtStr.length() > 1 && fmtStr.charAt(0) == ':')
		fmtStr = fmtStr.substring(1);

	    // Now see if there's a encode rate specified.
	    int off = fmtStr.indexOf(':');
	    if (off == -1) {
		if (!fmtStr.equals(""))
		    encodeStr = fmtStr;
	    } else {
		encodeStr = fmtStr.substring(0, off);
		sizeStr = fmtStr.substring(off + 1);
	    }
	}

	if (encodeStr == null || encodeStr.equals(""))
	    prUsage();

	if (sizeStr == null)
	    return new VideoFormat(encodeStr);

	int width = 320, height = 240;

	int off = sizeStr.indexOf('X');
	if (off == -1)
	    off = sizeStr.indexOf('x');

	if (off == -1) {
	    System.err.println("Video dimension is not correctly specified: " + sizeStr);
	    prUsage();
	} else {
	    String widthStr = sizeStr.substring(0, off);
	    String heightStr = sizeStr.substring(off + 1);

	    try {
		Integer integer = Integer.valueOf(widthStr);
		if (integer != null)
		    width = integer.intValue();
		integer = Integer.valueOf(heightStr);
		if (integer != null)
		    height = integer.intValue();
	    } catch (Throwable t) { 
		prUsage();
	    }

	    return new VideoFormat(encodeStr, 
				new Dimension(width, height),
				VideoFormat.NOT_SPECIFIED, // maxDataLen
				null, 			   // data class
				VideoFormat.NOT_SPECIFIED  // FrameRate
				);
	}

	return null;
    }


    static void prUsage() {
	System.err.println("Usage: java Transcode -o <output> -a <audio format> -v <video format> -s <start time> -e <end time> <input>");
	System.err.println("     <output>: input URL or file name");
	System.err.println("     <input>: output URL or file name");
	System.err.println("     <audio format>: [encoding]:[rate]:[sizeInBits]:[channels]:[big|little]:[signed|unsigned]");
	System.err.println("     <video format>: [encoding]:[widthXheight]");
	System.err.println("     <start time>: start time in seconds");
	System.err.println("     <end time>: end time in seconds");
	System.exit(0);
    }
}
