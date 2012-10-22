package editing.cut;

/*
 * @(#)Cut.java	1.3 01/03/13
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


import java.util.Vector;
import java.io.File;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.control.QualityControl;
import javax.media.control.FramePositioningControl;
import javax.media.Format;
import javax.media.format.*;
import javax.media.datasink.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import java.io.IOException;


/**
 * A sample program to cut an input file given the start and end points.
 */
public class Cut implements ControllerListener, DataSinkListener {

    /**
     * Main program
     */
    public static void main(String [] args) {

	String inputURL = null;
	String outputURL = null;
	long start[], end[];
	Vector startV = new Vector();
	Vector endV = new Vector();
	boolean frameMode = false;

	if (args.length == 0)
	    prUsage();

	// Parse the arguments.
	int i = 0;
	while (i < args.length) {

	    if (args[i].equals("-o")) {
		i++;
		if (i >= args.length)
		    prUsage();
		outputURL = args[i];
	    } else if (args[i].equals("-f")) {
		frameMode = true;
	    } else if (args[i].equals("-s")) {
		i++;
		if (i >= args.length)
		    prUsage();
		startV.addElement(new Long(args[i]));
	    } else if (args[i].equals("-e")) {
		i++;
		if (i >= args.length)
		    prUsage();
		endV.addElement(new Long(args[i]));

		// For every end point, there should be a matching
		// start point; unless is the first point.
		if (startV.size() != endV.size()) {
		    if (startV.size() == 0)
			startV.addElement(new Long(0));
		    else
			prUsage();
		}
	    } else {
		inputURL = args[i];
	    }
	    i++;
	}

	if (inputURL == null) {
	    System.err.println("No input url specified.");
	    prUsage();
	}

	if (outputURL == null) {
	    System.err.println("No output url specified.");
	    prUsage();
	}

	if (startV.size() == 0 && endV.size() == 0) {
	    System.err.println("No start and end point specified.");
	    prUsage();
	}

	// Pad the last end point if necessary.
	if (startV.size() > endV.size()) {
	   if (startV.size() == endV.size() + 1)
		endV.addElement(new Long(Long.MAX_VALUE));
	   else
		prUsage();
	}

	start = new long[startV.size()];
	end = new long[startV.size()];
	long prevEnd = 0;

	// Parse the start and end points.
	for (int j = 0; j < start.length; j++) {

	    start[j] = ((Long)startV.elementAt(j)).longValue();
	    end[j] = ((Long)endV.elementAt(j)).longValue();

	    if (prevEnd > start[j]) {
		System.err.println("Previous end point cannot be > the next start point.");
		prUsage();
	    } else if (start[j] >= end[j]) {
		System.err.println("Start point cannot be >= end point.");
		prUsage();
	    }

	    prevEnd = end[j];
	}

	if (frameMode) {
	    System.err.println("Start and end points are specified in frames.");
	} else {
	    // Times are in millseconds.  We'll turn them into nanoseconds.
	    for (int j = 0; j < start.length; j++) {
		start[j] *= 1000000;
		if (end[j] != Long.MAX_VALUE)
		    end[j] *= 1000000;
	    }
	}

	// Generate the input and output media locators.
	MediaLocator iml;
	MediaLocator oml;

	if ((iml = createMediaLocator(inputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + inputURL);
	    System.exit(0);
	}

	if ((oml = createMediaLocator(outputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + outputURL);
	    System.exit(0);
	}

	// Trancode with the specified parameters.
	Cut cut  = new Cut();

	if (!cut.doIt(iml, oml, start, end, frameMode)) {
	    System.err.println("Failed to cut the input");
	}

	System.exit(0);
    }


    /**
     * Given a source media locator, destination media locator and
     * a start and end point, this program cuts the pieces out.
     */
    public boolean doIt(MediaLocator inML, MediaLocator outML,
		long start[], long end[], boolean frameMode) {

	// Guess the output content descriptor from the file extension.
 	ContentDescriptor cd;

	if ((cd = fileExtToCD(outML.getRemainder())) == null) {
	    System.err.println("Couldn't figure out from the file extension the type of output needed!");
	    return false;
	}

	Processor p;

	try {
	    System.err.println("- Create processor for: " + inML);
	    p = Manager.createProcessor(inML);
	} catch (Exception e) {
	    System.err.println("Yikes!  Cannot create a processor from the given url: " + e);
	    return false;
	}

	System.err.println("- Configure the processor for: " + inML);
	if (!waitForState(p, Processor.Configured)) {
	    System.err.println("Failed to configure the processor.");
	    return false;
	}

	checkTrackFormats(p);

	System.err.println("- Realize the processor for: " + inML);
	if (!waitForState(p, Processor.Realized)) {
	    System.err.println("Failed to realize the processor.");
	    return false;
	}

	// Set the JPEG quality to .5.
	setJPEGQuality(p, 0.5f);

	// Translate frame # into time.
	if (frameMode) {
	    FramePositioningControl fpc = (FramePositioningControl)p.getControl("javax.media.control.FramePositioningControl");

	    if (fpc != null) {
		Time t;
		for (int i = 0; i < start.length; i++) {
		    t = fpc.mapFrameToTime((int)start[i]);
		    if (t == FramePositioningControl.TIME_UNKNOWN) {
			fpc = null;
			break;
		    } else
			start[i] = t.getNanoseconds();
		    if (end[i] == Long.MAX_VALUE)
			continue;
		    t = fpc.mapFrameToTime((int)end[i]);
		    if (t == FramePositioningControl.TIME_UNKNOWN) {
			fpc = null;
			break;
		    } else
			end[i] = t.getNanoseconds();
		}
	    }

	    if (fpc == null) {
		System.err.println("Sorry... the given input media type does not support frame positioning.");
		return false;
	    }
	}

	SuperCutDataSource ds = new SuperCutDataSource(p, inML, start, end);

	// Create the processor to generate the final output.
	try {
	    p = Manager.createProcessor(ds);
	} catch (Exception e) {
	    System.err.println("Failed to create a processor to concatenate the inputs.");
	    return false;
	}

	p.addControllerListener(this);

	// Put the Processor into configured state.
	if (!waitForState(p, Processor.Configured)) {
	    System.err.println("Failed to configure the processor.");
	    return false;
	}

	// Set the output content descriptor on the final processor.
	System.err.println("- Set output content descriptor to: " + cd);
	if ((p.setContentDescriptor(cd)) == null) {
	    System.err.println("Failed to set the output content descriptor on the processor.");
	    return false;
	}

	// We are done with programming the processor.  Let's just
	// realize and prefetch it.
	if (!waitForState(p, Controller.Prefetched)) {
	    System.err.println("Failed to realize the processor.");
	    return false;
	}

	// Now, we'll need to create a DataSink.
	DataSink dsink;
	if ((dsink = createDataSink(p, outML)) == null) {
	    System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
	    return false;
	}

	dsink.addDataSinkListener(this);
	fileDone = false;

	System.err.println("- Start cutting...");

	// OK, we can now start the actual concatenation.
	try {
	    p.start();
	    dsink.start();
	} catch (IOException e) {
	    System.err.println("IO error during concatenation");
	    return false;
	}

	// Wait for EndOfStream event.
	waitForFileDone();

	// Cleanup.
	try {
	    dsink.close();
	} catch (Exception e) {}
	p.removeControllerListener(this);

	System.err.println("  ...done cutting.");

	return true;
    }


    /**
     * Transcode the MPEG audio to linear and video to JPEG so
     * we can do the cutting.
     */
    void checkTrackFormats(Processor p) {

	TrackControl tc[] = p.getTrackControls();
	VideoFormat mpgVideo = new VideoFormat(VideoFormat.MPEG);
	AudioFormat rawAudio = new AudioFormat(AudioFormat.LINEAR);

	for (int i = 0; i < tc.length; i++) {
	    Format preferred = null;

	    if (tc[i].getFormat().matches(mpgVideo)) {
		preferred = new VideoFormat(VideoFormat.JPEG);
	    } else if (tc[i].getFormat() instanceof AudioFormat &&
		     !tc[i].getFormat().matches(rawAudio)) {
		preferred = rawAudio;
	    }

	    if (preferred != null) {
		Format supported[] = tc[i].getSupportedFormats();
		Format selected = null;

		for (int j = 0; j < supported.length; j++) {
		    if (supported[j].matches(preferred)) {
			selected = supported[j];
			break;
		    }
		}

		if (selected != null) {
		    System.err.println("  Transcode:");
		    System.err.println("     from: " + tc[i].getFormat());
		    System.err.println("     to: " + selected);
		    tc[i].setFormat(selected);
		}
	    }
	}
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
     * Utility function to check for raw (linear) audio.
     */
    boolean isRawAudio(Format fmt) {
	return (fmt instanceof AudioFormat) &&
		fmt.getEncoding().equalsIgnoreCase(AudioFormat.LINEAR);
    }

    
    /**
     * Utility class to block until a certain state had reached.
     */
    public class StateWaiter implements ControllerListener {

	Processor p;
	boolean error = false;

	StateWaiter(Processor p) {
	    this.p = p;
	    p.addControllerListener(this);
	}

	public synchronized boolean waitForState(int state) {

	    switch (state) {
	    case Processor.Configured:
		p.configure(); break;
	    case Processor.Realized:
		p.realize(); break;
	    case Processor.Prefetched:
		p.prefetch(); break;
	    case Processor.Started:
		p.start(); break;
	    }

	    while (p.getState() < state && !error) {
		try {
		    wait(1000);
		} catch (Exception e) {
		}
	    }
	    //p.removeControllerListener(this);
	    return !(error);
	}
	
	public void controllerUpdate(ControllerEvent ce) {
	    if (ce instanceof ControllerErrorEvent) {
		error = true;
	    }
	    synchronized (this) {
		notifyAll();
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
	    System.err.println("- Create DataSink for: " + outML);
	    dsink = Manager.createDataSink(ds, outML);
	    dsink.open();
	} catch (Exception e) {
	    System.err.println("Cannot create the DataSink: " + e);
	    return null;
	}

	return dsink;
    }


    /**
     * Block until the given processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state) {
	return (new StateWaiter(p)).waitForState(state);
    }


    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {

	if (evt instanceof ControllerErrorEvent) {
	    System.err.println("Failed to cut the file.");
	    System.exit(-1);
	} else if (evt instanceof EndOfMediaEvent) {
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


    static void prUsage() {
	System.err.println("Usage: java Cut -o <output> <input> [-f] -s <startTime> -e <endTime> ...");
	System.err.println("     <output>: input URL or file name");
	System.err.println("     <input>: output URL or file name");
	System.err.println("     <startTime>: start time in milliseconds");
	System.err.println("     <endTime>: end time in milliseconds");
	System.err.println("     -f: specify the times in video frames instead of milliseconds");
	System.exit(0);
    }


    ////////////////////////////////////////
    //
    // Inner classes.
    ////////////////////////////////////////

    /**
     * The customed DataSource to cut input.
     */
    class SuperCutDataSource extends PushBufferDataSource {

	Processor p;
	MediaLocator ml;
	PushBufferDataSource ds;
	SuperCutStream streams[];

	public SuperCutDataSource(Processor p, MediaLocator ml, 
			long start[], long end[]) {
	    this.p = p;
	    this.ml = ml;
	    this.ds = (PushBufferDataSource)p.getDataOutput();

	    TrackControl tcs[] = p.getTrackControls();
	    PushBufferStream pbs[] = ds.getStreams();

	    streams = new SuperCutStream[pbs.length];
	    for (int i = 0; i < pbs.length; i++) {
		streams[i] = new SuperCutStream(tcs[i], pbs[i], start, end);
	    }
	}

	public void connect() throws java.io.IOException {
	}

	public PushBufferStream [] getStreams() {
	    return streams;
	}

	public void start() throws java.io.IOException {
	    p.start();
	    ds.start();
	}

	public void stop() throws java.io.IOException {
	}

	public Object getControl(String name) {
	    // No controls
	    return null;
	}
    
	public Object [] getControls() {
	    // No controls
	    return new Control[0];
	}

	public Time getDuration() {
	    return ds.getDuration();
	}
    
	public void disconnect() {
	}

	public String getContentType() {
	    return ContentDescriptor.RAW;
	}

	public MediaLocator getLocator() {
	    return ml;
	}

	public void setLocator(MediaLocator ml) {
	    System.err.println("Not interested in a media locator");
	}
    }


    /**
     * Utility Source stream for the SuperCutDataSource.
     */
    class SuperCutStream implements PushBufferStream, BufferTransferHandler {

	TrackControl tc;
	PushBufferStream pbs;

	long start[], end[];
	boolean startReached[], endReached[];
	int idx = 0;

	BufferTransferHandler bth;
	long timeStamp = 0;
	long lastTS = 0;
	int audioLen = 0;
	int audioElapsed = 0;
	boolean eos = false;
	Format format;

	// Single buffer Queue.
	Buffer buffer;
	int bufferFilled = 0;

	public SuperCutStream(TrackControl tc, PushBufferStream pbs, 
			long start[], long end[]) {
	    this.tc = tc;
	    this.pbs = pbs;
	    this.start = start;
	    this.end = end;
	    startReached = new boolean[start.length];
	    endReached = new boolean[end.length];
	    for (int i = 0; i < start.length; i++) {
		startReached[i] = endReached[i] = false;
	    }
	    buffer = new Buffer();
	    pbs.setTransferHandler(this);
	}


	/**
	 * Called from the transferData to read data from the input.
	 */
	void processData() {

	    // We have a synchronized buffer Q of 1.
	    synchronized (buffer) {
		while (bufferFilled == 1) {
		    try {
			buffer.wait();
		    } catch (Exception e) {}
		}
	    }

	    // Read from the real source.
	    try {
		pbs.read(buffer);
	    } catch (IOException e) {}

	    format = buffer.getFormat();

	    if (idx >= end.length) {
		// We are done with all the end points.
		// Let's just generate an EOM to stop the processing.
		buffer.setOffset(0);
		buffer.setLength(0);
		buffer.setEOM(true);
	    }

	    if (buffer.isEOM())
		eos = true;

	    int len = buffer.getLength();

	    // Skip the buffers if it's to be cut.
	    if (checkTimeToSkip(buffer)) {
		// Update the audio len counter.
		if (isRawAudio(buffer.getFormat()))
		    audioLen += len;
		return;
	    }

	    // Update the audio len counter.
	    if (isRawAudio(buffer.getFormat()))
		audioLen += len;

	    // We can now allow the processor to read from our stream.
	    synchronized (buffer) {
		bufferFilled = 1;
		buffer.notifyAll();
	    }

	    // Notify the processor.
	    if (bth != null)
		bth.transferData(this);
	}


	/**
 	 * This is invoked from the consumer processor to read
	 * a frame from me.
	 */
	public void read(Buffer rdBuf) throws IOException {

	    /**
	     * Check if there's any buffer in the Q to read.
	     */
	    synchronized (buffer) {
		while (bufferFilled == 0) {
		    try {
			buffer.wait();
		    } catch (Exception e) {}
		}
	    }

	    // Copy the data from the queue.
	    Object oldData = rdBuf.getData();

	    rdBuf.copy(buffer);
	    buffer.setData(oldData);

	    // Remap the time stamps.

	    if (isRawAudio(rdBuf.getFormat())) {
		// Raw audio has a accurate to compute time.
		rdBuf.setTimeStamp(computeDuration(audioElapsed, rdBuf.getFormat()));
		audioElapsed += buffer.getLength();
	    } else if (rdBuf.getTimeStamp() != Buffer.TIME_UNKNOWN) {
		long diff = rdBuf.getTimeStamp() - lastTS;
		lastTS = rdBuf.getTimeStamp();
		if (diff > 0)
		    timeStamp += diff;
		rdBuf.setTimeStamp(timeStamp);
	    }

	    synchronized (buffer) {
		bufferFilled = 0;
		buffer.notifyAll();
	    }
	}
	

	/**
	 * Given a buffer, check to see if this should be included or 
	 * skipped based on the start and end times.
	 */
	boolean checkTimeToSkip(Buffer buf) {

	    if (idx >= startReached.length)
		return false;

	    if (!eos && !startReached[idx]) {
		if (!(startReached[idx] = checkStartTime(buf, start[idx]))) {
		    return true;
		}
	    }

	    if (!eos && !endReached[idx]) {
		if (endReached[idx] = checkEndTime(buf, end[idx])) {
		    idx++;	// move on to the next set of start & end pts.
		    return true;
		}
	    } else if (endReached[idx]) {
		if (!eos) {
		    return true;
		} else {
		    buf.setOffset(0);
		    buf.setLength(0);
		}
	    }

	    return false;
	}


	/**
	 * Check the buffer against the start time.
	 */
	boolean checkStartTime(Buffer buf, long startTS) {
	    if (isRawAudio(buf.getFormat())) {
		long ts = computeDuration(audioLen+buf.getLength(), 
					buf.getFormat());
		if (ts > startTS) {
		    int len = computeLength(ts - startTS, buf.getFormat());
		    buf.setOffset(buf.getOffset() + buf.getLength() - len);
		    buf.setLength(len);
		    lastTS = buf.getTimeStamp();
		    return true;
		}
	    } else if (buf.getTimeStamp() >= startTS) {
		if (buf.getFormat() instanceof VideoFormat) {
		    // The starting frame needs to be a key frame.
		    if ((buf.getFlags() & Buffer.FLAG_KEY_FRAME) != 0) {
			lastTS = buf.getTimeStamp();
			return true;
		    }
		} else {
		    lastTS = buf.getTimeStamp();
		    return true;
		}
	    }
	    return false;
        }


	/**
	 * Check the buffer against the end time.
	 */
	boolean checkEndTime(Buffer buf, long endTS) {
	    if (isRawAudio(buf.getFormat())) {
		if (computeDuration(audioLen, buf.getFormat()) >= endTS)
		    return true;
		else {
		    long ts = computeDuration(audioLen+buf.getLength(), 
					buf.getFormat());
		    if (ts >= endTS) {
			int len = computeLength(ts - endTS, buf.getFormat());
			buf.setLength(buf.getLength() - len);
			// We still need to process this last buffer.
		    }
		}
	    } else if (buf.getTimeStamp() > endTS) {
		return true;
	    }

	    return false;
	}


	/**
	 * Compute the duration based on the length and format of the audio.
	 */
	public long computeDuration(int len, Format fmt) {
	    if (!(fmt instanceof AudioFormat))
		return -1;
	    return ((AudioFormat)fmt).computeDuration(len);
	}


	/**
	 * Compute the length based on the duration and format of the audio.
	 */
	public int computeLength(long duration, Format fmt) {
	    if (!(fmt instanceof AudioFormat))
		return -1;
	    AudioFormat af = (AudioFormat)fmt;
	    // Multiplication is done is stages to avoid overflow.
	    return (int) ((((duration /1000) * (af.getChannels() * af.getSampleSizeInBits()))/1000) * af.getSampleRate() / 8000);
	}


	public ContentDescriptor getContentDescriptor() {
	    return new ContentDescriptor(ContentDescriptor.RAW);
	}

	public boolean endOfStream() {
	    return eos;
	}

	public long getContentLength() {
	    return LENGTH_UNKNOWN;
	}

	public Format getFormat() {
	    return tc.getFormat();
	}

	public void setTransferHandler(BufferTransferHandler bth) {
	    this.bth = bth;
	}
	
	public Object getControl(String name) {
	    // No controls
	    return null;
	}

	public Object [] getControls() {
	    // No controls
	    return new Control[0];
	}
	
	public synchronized void transferData(PushBufferStream pbs) {
	   processData();
	}

    } // class SuperCutStream
}

