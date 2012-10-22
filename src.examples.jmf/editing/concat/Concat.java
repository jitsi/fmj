package editing.concat;
/*
 * @(#)Concat.java	1.2 01/03/13
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
 * A sample program to concat multiple input media files into one 
 * contiguous output file.  Whenever necessary, transocding may
 * occur automatically.
 */
public class Concat implements ControllerListener, DataSinkListener {

    // Type enumerators.
    static int AUDIO = 0;
    static int VIDEO = AUDIO + 1;
    static int MEDIA_TYPES = VIDEO + 1;

    int totalTracks;

    boolean transcodeMsg = false;
    String TRANSCODE_MSG = "The given inputs require transcoding to have a common format for concatenation.";


    /**
     * Main program
     */
    public static void main(String [] args) {

	Vector inputURL = new Vector();
	String outputURL = null;

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
	    } else {
		inputURL.addElement(args[i]);
	    }
	    i++;
	}

	if (inputURL.size() == 0) {
	    System.err.println("No input url is specified");
	    prUsage();
	}

	if (outputURL == null) {
	    System.err.println("No output url is specified");
	    prUsage();
	}

	// Generate the input and output media locators.
	MediaLocator iml[] = new MediaLocator[inputURL.size()];
	MediaLocator oml;

	for (i = 0; i < inputURL.size(); i++) {
	    if ((iml[i] = createMediaLocator((String)inputURL.elementAt(i))) == null) {
		System.err.println("Cannot build media locator from: " + inputURL);
		System.exit(0);
	    }
	}

	if ((oml = createMediaLocator(outputURL)) == null) {
	    System.err.println("Cannot build media locator from: " + outputURL);
	    System.exit(0);
	}

	Concat concat  = new Concat();

	if (!concat.doIt(iml, oml)) {
	    System.err.println("Failed to concatenate the inputs");
	}

	System.exit(0);
    }


    /**
     * Given an array of input media locators and an output locator,
     * this method will concatenate the input media files to generate
     * a single concatentated output.
     */
    public boolean doIt(MediaLocator inML[], MediaLocator outML) {

	// Guess the output content descriptor from the file extension.
 	ContentDescriptor cd;

	if ((cd = fileExtToCD(outML.getRemainder())) == null) {
	    System.err.println("Couldn't figure out from the file extension the type of output needed!");
	    return false;
	}

	// Build the ProcInfo data structure for each processor.
	ProcInfo pInfo[] = new ProcInfo[inML.length];

	for (int i = 0; i < inML.length; i++) {
	    pInfo[i] = new ProcInfo();
	    pInfo[i].ml = inML[i];

	    try {
		System.err.println("- Create processor for: " + inML[i]);
		pInfo[i].p = Manager.createProcessor(inML[i]);
	    } catch (Exception e) {
		System.err.println("Yikes!  Cannot create a processor from the given url: " + e);
		return false;
	    }
	}

	// Try to match the tracks from different processors.
	if (!matchTracks(pInfo, cd)) {
	    System.err.println("Failed to match the tracks.");
	    return false;
	}

	// Program each processors to perform the necessary transcoding
	// to concatenate the tracks.
	if (!buildTracks(pInfo)) {
	    System.err.println("Failed to build processors for the inputs.");
	    return false;
	}

	// Generate a super glue data source from the processors.
	SuperGlueDataSource ds = new SuperGlueDataSource(pInfo);

	// Create the processor to generate the final output.
	Processor p;
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
	// realize it.
	if (!waitForState(p, Controller.Realized)) {
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

	System.err.println("- Start concatenation...");

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

	System.err.println("  ...done concatenation.");

	return true;
    }


    /**
     * Try to match all the tracks and find common formats to concatenate
     * the tracks.  A database of results will be generated.
     */
    public boolean matchTracks(ProcInfo pInfo[], ContentDescriptor cd) {

	TrackControl tcs[];

	Vector aTracks, vTracks;
	int aIdx, vIdx;
	int i, j, type;
	TrackInfo tInfo;

	// Build the ProcInfo data structure for each processor.
	// Sparate out the audio from video tracks.
	for (i = 0; i < pInfo.length; i++) {

	    if (!waitForState(pInfo[i].p, Processor.Configured)) {
		System.err.println("- Failed to configure the processor.");
		return false;
	    }

	    tcs = pInfo[i].p.getTrackControls();

	    pInfo[i].tracksByType = new TrackInfo[MEDIA_TYPES][];
	    for (type = AUDIO; type < MEDIA_TYPES; type++) {
		pInfo[i].tracksByType[type] = new TrackInfo[tcs.length];
	    }
	    pInfo[i].numTracksByType = new int[MEDIA_TYPES];
	    aIdx = vIdx = 0;

	    // Separate the audio and video tracks.
	    for (j = 0; j < tcs.length; j++) {
	 	if (tcs[j].getFormat() instanceof AudioFormat) {
		    tInfo = new TrackInfo();
		    tInfo.idx = j;
		    tInfo.tc = tcs[j];
		    pInfo[i].tracksByType[AUDIO][aIdx++] = tInfo;
		} else if (tcs[j].getFormat() instanceof VideoFormat) {
		    tInfo = new TrackInfo();
		    tInfo.idx = j;
		    tInfo.tc = tcs[j];
		    pInfo[i].tracksByType[VIDEO][vIdx++] = tInfo;
		}
	    }

	    pInfo[i].numTracksByType[AUDIO] = aIdx;
	    pInfo[i].numTracksByType[VIDEO] = vIdx;
	    pInfo[i].p.setContentDescriptor(cd);
	}


	// Different movies has different number of tracks.  Obviously,
	// we cannot concatenate all the tracks of 3-track movie with a 
	// 2-track one.  We'll concatenate up to the smallest # of tracks
	// of all the movies.  We'll also need to disable the unused tracks.

	int total[] = new int[MEDIA_TYPES];
	
	for (type = AUDIO; type < MEDIA_TYPES; type++) {
	    total[type] = pInfo[0].numTracksByType[type];
	}

	for (i = 1; i < pInfo.length; i++) {
	    for (type = AUDIO; type < MEDIA_TYPES; type++) {
		if (pInfo[i].numTracksByType[type] < total[type])
		    total[type] = pInfo[i].numTracksByType[type];
	    }
	}

	if (total[AUDIO] < 1 && total[VIDEO] < 1) {
	    System.err.println("There is no audio or video tracks to concatenate.");
	    return false;
	}

	totalTracks = 0;
	for (type = AUDIO; type < MEDIA_TYPES; type++)
	    totalTracks += total[type];

	// Disable all the unused tracks.

	for (i = 0; i < pInfo.length; i++) {
	    for (type = AUDIO; type < MEDIA_TYPES; type++) {
		for (j = total[type]; j < pInfo[i].numTracksByType[type]; j++) {
		    tInfo = pInfo[i].tracksByType[type][j];
		    disableTrack(pInfo[i], tInfo);
		    System.err.println("- Disable the following track since the other input media do not have a matching type.");
		    System.err.println("  " + tInfo.tc.getFormat());
		}
		pInfo[i].numTracksByType[type] = total[type];
	    }
	}

	// Try to find common formats to concatenate the tracks.

	// Deal with the tracks by type.
	for (type = AUDIO; type < MEDIA_TYPES; type++) {
	    for (i = 0; i < total[type]; i++) {
		if (!tryMatch(pInfo, type, i)) {
		    System.err.println("- Cannot transcode the tracks to a common format for concatenation!  Sorry.");
		    return false;
		}
	    }
	}

	return true;
    }


    /**
     * Disable a track.
     */
    void disableTrack(ProcInfo pInfo, TrackInfo remove) {
	remove.tc.setEnabled(false);
	remove.disabled = true;

	// Shift all the stream indexes to match.
	TrackInfo ti;
	for (int type = AUDIO; type < MEDIA_TYPES; type++) {
	    for (int j = 0; j < pInfo.numTracksByType[type]; j++) {
		ti = pInfo.tracksByType[type][j];
		if (ti.idx >= remove.idx)
		    ti.idx--;
	    }
	}
    }


    /**
     * With the given processor info generated from matchTracks, build each
     * of the processors. 
     */
    public boolean buildTracks(ProcInfo pInfo[]) {

	ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
	Processor p;

	for (int i = 0; i < pInfo.length; i++) {
	    p = pInfo[i].p;
	    p.setContentDescriptor(cd);

	    // We are done with programming the processor.  Let's just
	    // realize the it.
	    if (!waitForState(p, Controller.Realized)) {
		System.err.println("- Failed to realize the processor.");
		return false;
	    }

	    // Set the JPEG quality to .5.
	    setJPEGQuality(p, 0.5f);

	    PushBufferStream pbs[];
	    TrackInfo tInfo;
	    int trackID;

	    // Cheating.  I should have checked the type of DataSource
	    // returned.
	    pInfo[i].ds = (PushBufferDataSource)p.getDataOutput();
	    pbs = (PushBufferStream [])pInfo[i].ds.getStreams();

	    // Find the matching data stream for the given track for audio.

	    for (int type = AUDIO; type < MEDIA_TYPES; type++) {
		for (trackID = 0; trackID < pInfo[i].numTracksByType[type]; trackID++) {
		    tInfo = pInfo[i].tracksByType[type][trackID];
		    tInfo.pbs = pbs[tInfo.idx];
		}
	    }
	}

	return true;
    }


    /**
     * Try matching the data formats and find common ones for concatenation.
     */
    public boolean tryMatch(ProcInfo pInfo[], int type, int trackID) {

	TrackControl tc = pInfo[0].tracksByType[type][trackID].tc;
	Format origFmt = tc.getFormat();
	Format newFmt, oldFmt;
	Format supported[] = tc.getSupportedFormats(); 

	for (int i = 0; i < supported.length; i++) {

	    if (supported[i] instanceof AudioFormat) {
		// If it's not the original format, then for audio, we'll
		// only do linear since it's more accurate to compute the
		// audio times.
		if (!supported[i].matches(tc.getFormat()) &&
		    !supported[i].getEncoding().equalsIgnoreCase(AudioFormat.LINEAR))
		    continue;
	    }

	    if (tryTranscode(pInfo, 1, type, trackID, supported[i])) {

		// We've found the right format to transcode all the
		// tracks to.  We'll set it on the corresponding
		// TrackControl on each processor.

		for (int j = 0; j < pInfo.length; j++) {
		    tc = pInfo[j].tracksByType[type][trackID].tc;
		    oldFmt = tc.getFormat();
		    newFmt = supported[i];

		    // Check if it requires transcoding.
		    if (!oldFmt.matches(newFmt)) {
			if (!transcodeMsg) {
			    transcodeMsg = true;
			    System.err.println(TRANSCODE_MSG);
			}

			System.err.println("- Transcoding: " + pInfo[j].ml);
			System.err.println("  " + oldFmt);
			System.err.println("           to:");
			System.err.println("  " + newFmt);
		    } 

		    // For video, check if it requires scaling.
		    if (oldFmt instanceof VideoFormat) {
			Dimension newSize = ((VideoFormat)origFmt).getSize();
			Dimension oldSize = ((VideoFormat)oldFmt).getSize();

			if (oldSize != null && !oldSize.equals(newSize)) {
			    // It requires scaling.

			    if (!transcodeMsg) {
				transcodeMsg = true;
				System.err.println(TRANSCODE_MSG);
			    }
			    System.err.println("- Scaling: " + pInfo[j].ml);
			    System.err.println("  from: " + oldSize.width + 
						" x " + oldSize.height);
			    System.err.println("  to: " + newSize.width + 
						" x " + newSize.height);
			    newFmt = (new VideoFormat(null, 
					newSize, 
					Format.NOT_SPECIFIED,
					null,
					Format.NOT_SPECIFIED)).intersects(newFmt);
			}
		    }
		    tc.setFormat(newFmt);
		}

		return true;
	    }
	}

	return false;

    }


    /**
     * Try different transcoded formats for concatenation.
     */
    public boolean tryTranscode(ProcInfo pInfo[], int procID, int type, 
		int trackID, Format candidate) {

	if (procID >= pInfo.length)
	    return true;

	boolean matched = false;
	TrackControl tc = pInfo[procID].tracksByType[type][trackID].tc;
	Format supported[] = tc.getSupportedFormats(); 

	for (int i = 0; i < supported.length; i++) {
	    if (candidate.matches(supported[i]) &&
		tryTranscode(pInfo, procID+1, type, trackID, candidate)) {
		matched = true;
		break;
	    }
	}

	return matched;
    }


    /**
     * Utility function to check for raw (linear) audio.
     */
    boolean isRawAudio(TrackInfo tInfo) {
	Format fmt = tInfo.tc.getFormat();
	return (fmt instanceof AudioFormat) &&
		fmt.getEncoding().equalsIgnoreCase(AudioFormat.LINEAR);
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
	    System.err.println("Failed to concatenate the files.");
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
	System.err.println("Usage: java Concat -o <output> <input> ...");
	System.err.println("     <output>: input URL or file name");
	System.err.println("     <input>: output URL or file name");
	System.exit(0);
    }


    ////////////////////////////////////////
    //
    // Inner classes.
    ////////////////////////////////////////

    /**
     * Utility data structure for a track.
     */
    public class TrackInfo {
	public TrackControl tc;
	public PushBufferStream pbs;
	public int idx;
	public boolean done;
	public boolean disabled;
    }


    /**
     * Utility data structure for a processor.
     */
    public class ProcInfo {
	public MediaLocator ml;
	public Processor p;
	public PushBufferDataSource ds;
	public TrackInfo tracksByType[][];	// grouped by types
	public int numTracksByType[];	// grouped by types
	public int numTracks;
    }


    /**
     * The customed DataSource to glue the output DataSources from
     * other processors.
     */
    boolean masterFound = false;	// Master Time boolean

    class SuperGlueDataSource extends PushBufferDataSource {

	ProcInfo pInfo[];
	int current;
	SuperGlueStream streams[];

	public SuperGlueDataSource(ProcInfo pInfo[]) {
	    this.pInfo = pInfo;
	    streams = new SuperGlueStream[totalTracks];
	    for (int i = 0; i < totalTracks; i++)
		streams[i] = new SuperGlueStream(this);
	    current = 0;
	    setStreams(pInfo[current]);
	}

	void setStreams(ProcInfo pInfo) {
	    int j = 0;
	    masterFound = false;
	    for (int type = AUDIO; type < MEDIA_TYPES; type++) {
		for (int i = 0; i < pInfo.numTracksByType[type]; i++) {
		    if (!masterFound && 
			isRawAudio(pInfo.tracksByType[type][i])) {
			streams[j].setStream(pInfo.tracksByType[type][i], true);
			masterFound = true;
		    } else
			streams[j].setStream(pInfo.tracksByType[type][i], false);
		    j++;
		}
	    }
	}

	public void connect() throws java.io.IOException {
	}

	public PushBufferStream [] getStreams() {
	    return streams;
	}

	public void start() throws java.io.IOException {
	    pInfo[current].p.start();
	    pInfo[current].ds.start();
	}

	public void stop() throws java.io.IOException {
	}

	synchronized boolean handleEOM(TrackInfo tInfo) {
	    boolean lastProcessor = (current >= pInfo.length - 1);

	    // Check to see if all the tracks are done for the
	    // current processor.
	    for (int type = AUDIO; type < MEDIA_TYPES; type++) {
		for (int i = 0; i < pInfo[current].numTracksByType[type]; i++) {
		    if (!pInfo[current].tracksByType[type][i].done)
			return lastProcessor;
		}
	    }

	    // We have finished processing all the tracks for the
	    // current processor.
	    try {
		pInfo[current].p.stop();
		pInfo[current].ds.stop();
	    } catch (Exception e) {}

	    if (lastProcessor) {
		// We are done with all processors.
		return lastProcessor;
	    }

	    // Cannot find a track to keep as master time.
	    // We'll sync up with the movie duration.
	    if (!masterFound &&
		pInfo[current].p.getDuration() != Duration.DURATION_UNKNOWN) {
		masterTime += pInfo[current].p.getDuration().getNanoseconds();
	    }

	    // Move on to the next processor.
	    current++;
	    setStreams(pInfo[current]);
	    try {
		start();
	    } catch (Exception e) {}
	    return lastProcessor;
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
	    return Duration.DURATION_UNKNOWN;
	}
    
	public void disconnect() {
	}

	public String getContentType() {
	    return ContentDescriptor.RAW;
	}

	public MediaLocator getLocator() {
	    return pInfo[current].ml;
	}

	public void setLocator(MediaLocator ml) {
	    System.err.println("Not interested in a media locator");
	}
    }


    /**
     * Utility Source stream for the SuperGlueDataSource.
     */

    // Time of the master track.
    long masterTime = 0;

    // Total length of the audio processed.
    long masterAudioLen = 0;
    
    class SuperGlueStream implements PushBufferStream, BufferTransferHandler {

	SuperGlueDataSource ds;
	TrackInfo tInfo;
	PushBufferStream pbs;
	BufferTransferHandler bth;
	boolean useAsMaster = false;
	long timeStamp = 0;
	long lastTS = 0;

	public SuperGlueStream(SuperGlueDataSource ds) {
	    this.ds = ds;
	}

	public void setStream(TrackInfo tInfo, boolean useAsMaster) {
	    this.tInfo = tInfo;
	    this.useAsMaster = useAsMaster;
	    if (pbs != null)
		pbs.setTransferHandler(null);
	    pbs = tInfo.pbs;

	    // Sync up all media at the beginning of the file.
	    if (masterTime > 0)
		timeStamp = masterTime;
	    lastTS = 0;

	    pbs.setTransferHandler(this);
	}

	public void read(Buffer buffer) throws IOException {
	    pbs.read(buffer);

	    // Remap the time stamps so it won't wrap around
	    // while changing to a new file.
	    if (buffer.getTimeStamp() != Buffer.TIME_UNKNOWN) {
		long diff = buffer.getTimeStamp() - lastTS;
		lastTS = buffer.getTimeStamp();
		if (diff > 0)
		    timeStamp += diff;
		buffer.setTimeStamp(timeStamp);
	    }

	    // If this track is to be used as the master time base,
	    // we'll need to compute the master time based on this track.
	    if (useAsMaster) {
		if (buffer.getFormat() instanceof AudioFormat) {
		    AudioFormat af = (AudioFormat)buffer.getFormat();
		    masterAudioLen += buffer.getLength();
		    long t = af.computeDuration(masterAudioLen);
		    if (t > 0) {
			masterTime = t;
		    } else {
			masterTime = buffer.getTimeStamp();
		    }
		} else {
		    masterTime = buffer.getTimeStamp();
		}
	    }

	    if (buffer.isEOM()) {
		tInfo.done = true;
		if (!ds.handleEOM(tInfo)) {
		    // This is not the last processor to be done.
		    // We'll need to un-set the EOM flag.
		    buffer.setEOM(false);
		    buffer.setDiscard(true);
		}
	    }
	}
	
	public ContentDescriptor getContentDescriptor() {
	    return new ContentDescriptor(ContentDescriptor.RAW);
	}

	public boolean endOfStream() {
	    return false;
	}

	public long getContentLength() {
	    return LENGTH_UNKNOWN;
	}

	public Format getFormat() {
	    return tInfo.tc.getFormat();
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
	   if (bth != null)
		bth.transferData(this);
	}

    } // class SuperGlueStream
}

