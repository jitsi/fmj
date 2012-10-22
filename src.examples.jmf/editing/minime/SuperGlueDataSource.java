package editing.minime;

/*
 * %W% %E%
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
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
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * Concatenates individual movie clips and applies transition effects
 * Its output is a DataSource which can be connected to a processor
 * which is in turn connected to a data sync.
 */
public class SuperGlueDataSource extends PushBufferDataSource {

    String []          locators;
    Time []            beginTime;
    Time []            endTime;
    Time []            effectDuration;
    String []          effectVideo;
    String []          effectAudio;
    long               audioEffectDurationInSamples;
    float              audioEffectDiff = 0.0f;
    float              audioEffectCurrent = 0.0f;
    Dimension          videoSize;
    SuperGlueStream [] outputStreams;
    Format []          outputFormats;
    int                nInputLocators;
    int                currentLocator = 0;
    int                nInputStreams;
    int                nEOS;

    DataSource         inputSourceCurrent;
    PushBufferStream [] inputStreamsCurrent;
    Format []          inputFormatsCurrent;
    Processor          inputProcessorCurrent;
    BTH []             bthCurrent;
    int                eomCountCurrent;

    DataSource         inputSourceNext;
    PushBufferStream [] inputStreamsNext;
    Format []          inputFormatsNext;
    Processor          inputProcessorNext;
    BTH []             bthNext;
    Buffer []          bufferNext;
    int                eomCountNext;
    Time               concatMediaTime;
    long               overlapTimeStamp;
    long               overlapDuration = (long) 2e+9;  // todo , this is temporary
    long               totalDuration = 0;
    
    Integer            stateLock = new Integer(0);
    boolean            failed = false;
    MediaLocator       mediaLocator = new MediaLocator("custom:superglue");
    boolean            connected = false;
    ProgressListener   progressListener = null;

    // This code is hardwired to work only for 44100 16 bit stereo.
    // If your input file cannot be converted to 44100 linear, this wont work
    AudioFormat        preferredAudioFormat = new AudioFormat(AudioFormat.LINEAR,
							      44100,
							      16, 2,
							      (MiniME.getSaveType() == 1) /*QuickTime*/
							       ? AudioFormat.BIG_ENDIAN
							       : AudioFormat.LITTLE_ENDIAN,
							      AudioFormat.SIGNED, 
							      16,
							      AudioFormat.NOT_SPECIFIED,
							      Format.byteArray);
    RGBFormat          preferredVideoFormat; // depends on the size of the input videos

    // locators : Array of urls
    // beginTime : Array of corresponding begin times [segment for each file]
    // endTime   : Array of corresponding end times
    // effectDuration : Duration of transition Should not be more than the segment
    //                    sizes of the individual files
    // effectVideo  : Video effects to apply during transitions. [only two supported now]
    // effectAudio  : Audio effects to apply during transitions. [only 1 supported now]
    // videoSize    : Size of the input video frames - all input files should have same size
    public SuperGlueDataSource(String locators[],
			       Time beginTime[],
			       Time endTime[],
			       Time effectDuration[],
			       String effectVideo[],
			       String effectAudio[],
			       Dimension videoSize) {
	this.locators = locators;
	this.beginTime = beginTime;
	this.endTime = endTime;
	this.effectDuration = effectDuration;
	this.effectVideo = effectVideo;
	this.effectAudio = effectAudio;
	this.videoSize = videoSize;
    }

    public void connect() throws java.io.IOException {
	// check the input parameters for some validity.
        if (connected)
            return;

	totalDuration = endTime[1].getNanoseconds()
	                - beginTime[1].getNanoseconds()
	                + endTime[0].getNanoseconds()
	                - beginTime[0].getNanoseconds()
	                - effectDuration[0].getNanoseconds();
	
	// create a processor for the first locator with the 
	// required output format and get the datasource
	//System.err.println("Creating first processor");
	if (locators != null && locators.length > 0 &&
	    createInputDataSource(locators[currentLocator])) {
	    // number of video clips we are concatenating
	    nInputLocators = locators.length;
	    nInputStreams = inputStreamsNext.length;
	    // first source, so move it to *Current
	    inputSourceCurrent = inputSourceNext;
	    inputSourceNext = null;
	    inputStreamsCurrent = inputStreamsNext;
	    inputStreamsNext = null;
	    inputProcessorCurrent = inputProcessorNext;
	    inputProcessorNext = null;

	    overlapDuration = effectDuration[currentLocator].getNanoseconds();
	    overlapTimeStamp = endTime[currentLocator].getNanoseconds() - overlapDuration;
	    audioEffectDurationInSamples = timeToSamples(overlapDuration);
	    bthCurrent = new BTH[nInputStreams];
	    outputStreams = new SuperGlueStream[nInputStreams];

	    inputFormatsCurrent = new Format[nInputStreams];
	    outputFormats = new Format[nInputStreams];
	    bufferNext = new Buffer[nInputStreams];

	    // Set up the transfer handler for the input and
	    // create the output streams with the format.
	    for (int i = 0; i < nInputStreams; i++) {
		inputStreamsCurrent[i].setTransferHandler(bthCurrent[i] =
							  new BTH(inputStreamsCurrent[i],
								  beginTime[currentLocator],
								  endTime[currentLocator]));
		inputFormatsCurrent[i] = inputStreamsCurrent[i].getFormat();
		if (inputFormatsCurrent[i] instanceof AudioFormat) {
		    outputFormats[i] = inputFormatsCurrent[i];
		} else if (inputFormatsCurrent[i] instanceof RGBFormat) {
		    outputFormats[i] = preferredVideoFormat;
		} else {
		    throw new IOException("Incompatible streams encountered");
		}
		outputStreams[i] = new SuperGlueStream(outputFormats[i]);
	    }
	    // Dont set media time - bug in JMF 2.0, fixed in next release
	    //inputProcessorCurrent.setMediaTime(beginTime[currentLocator]);
	    inputProcessorCurrent.start();
	} else 
	    throw new IOException("Could not create processor for " + locators[currentLocator]);
	if (locators != null && locators.length > 1) {
	    //System.err.println("Creating second processor");
	    if (!createInputDataSource(locators[currentLocator + 1])) 
	        throw new IOException("Could not create processor for " +
				      locators[currentLocator + 1]);
	    bthNext = new BTH[nInputStreams];

	    inputFormatsNext = new Format[nInputStreams];

	    // Set up the transfer handler for the input and
	    // create the output streams with the format.
	    for (int i = 0; i < nInputStreams; i++) {
		inputStreamsNext[i].setTransferHandler(bthNext[i] =
						       new BTH(inputStreamsNext[i],
							       beginTime[currentLocator+1],
							       endTime[currentLocator+1]));
		inputFormatsNext[i] = inputStreamsNext[i].getFormat();
	    }
	}
	connected = true;
    }

    public Time getDuration() {
	return new Time(totalDuration);
    }

    public void setProgressListener(ProgressListener pl) {
	progressListener = pl;
    }

    // Concatenation progress
    public long getCurrentTime() {
	long currentTime = Long.MAX_VALUE;
	if (outputStreams != null) {
	    
	    for (int i = 0; i < outputStreams.length; i++) {
		if (outputStreams[i] != null) {
		    if (outputStreams[i].currentTime < currentTime)
			currentTime = outputStreams[i].currentTime;
		}
	    }
	}
	if (currentTime == Long.MAX_VALUE)
	    return 0;
	else
	    return currentTime;
    }

    void updateTime() {
	if (progressListener != null) {
	    progressListener.updateProgress(getCurrentTime(), totalDuration);
	}
    }

    public PushBufferStream [] getStreams() {
	if (outputStreams == null)
	    System.err.println("getStreams() shouldn't be called before connect");
	return outputStreams;
    }

    public void start() throws java.io.IOException {
	if (!connected)
	    throw new IOException("Not connected");
	if (inputSourceCurrent != null)
	    inputSourceCurrent.start();	
	if (inputSourceNext != null)
	    inputSourceNext.start();
    }

    public void stop() throws java.io.IOException {
	if (inputSourceCurrent != null)
	    inputSourceCurrent.stop();	
	if (inputSourceNext != null)
	    inputSourceNext.stop();
    }

    synchronized void handleEOM(PushBufferStream pbs) {
	int i;
	boolean current = false;
	boolean next = false;
	
	for (i = 0; i < nInputStreams; i++) {
	    if (inputStreamsCurrent[i] == pbs) {
	        //System.err.println("Got EOM from " + pbs);
		current = true;
		break;
	    } else if (inputStreamsNext != null && inputStreamsNext[i] == pbs) {
	        //System.err.println("Got EOM from next " + pbs);
		next = true;
		break;
	    }
	}
	
	if (current) {
	    // Add to number of streams that have delivered EOM
	    eomCountCurrent++;
	    // If all then...
	    if (eomCountCurrent >= nInputStreams) {
		// Stop and close current processor
		inputProcessorCurrent.stop();
		inputProcessorCurrent.close();

                if (inputStreamsNext == null) {
                    doneAllStreams();
                    return;
                }
		// Transfer next stuff to current ...
		inputStreamsCurrent = inputStreamsNext;
		inputSourceCurrent = inputSourceNext;
		inputProcessorCurrent = inputProcessorNext;
		bthCurrent = bthNext;
		inputFormatsCurrent = inputFormatsNext;
                // Could occur earlier if transition effects are enabled
                eomCountCurrent = eomCountNext;

		// todo update this if more than 2 media files to be concatenated
		overlapDuration = 0;
		overlapTimeStamp = Long.MAX_VALUE;//endTime[currentLocator+1].getNanoseconds();

		if (inputProcessorCurrent.getTargetState() != Controller.Started) {
		    // Dont set media time - bug in JMF 2.0, fixed in next release
		    //inputProcessorCurrent.setMediaTime(beginTime[currentLocator+1]);
		    inputProcessorCurrent.start();
		}
		
                eomCountNext = 0;
		currentLocator++;
		// ... and done if all input locators are done
		if (currentLocator + 1 >= nInputLocators) {
		    inputStreamsNext = null;
		    inputSourceNext = null;
		    inputProcessorNext = null;
		    bthNext = null;
		    inputFormatsNext = null;
		    bufferNext = null;
		    return;
		} else {
		    // If there are more input locators, then create a new processor
		    if (!createInputDataSource(locators[currentLocator + 1])) {
			System.err.println("Couldn't create processor for " +
					   locators[currentLocator + 1]);
			
			bthNext = new BTH[nInputStreams];

			inputFormatsNext = new Format[nInputStreams];

			// Set up the transfer handler for the input
			for (i = 0; i < nInputStreams; i++) {
			    inputStreamsNext[i].setTransferHandler(bthNext[i] =
						      new BTH(inputStreamsNext[i],
							      beginTime[currentLocator+1],
							      endTime[currentLocator+1]));
			}
		    }
		}
	    }
	} else if (next) {
	    eomCountNext++;
	    if (eomCountNext >= nInputStreams) {
		System.err.println("EOM of next data source is bad!!!");
	    }
	} else {
	    System.err.println("Who the hell stream is this?");
	}
    }

    void doneAllStreams() {
	Buffer buffer = new Buffer();
	updateTime();
	buffer.setEOM(true);
	for (int i = 0; i < outputStreams.length; i++) {
	    buffer.setFormat(outputStreams[i].getFormat());
	    outputStreams[i].pushData(buffer);
	}
	connected = false;
    }

    int fromNextStream(BufferTransferHandler bth) {
	try {
	    if (bthNext == null)
		return -1;
	    for (int i=0; i < bthNext.length; i++) {
		if (bth == bthNext[i])
		    return i;
	    }
	    return -1;
	} catch (Exception e) {
	    return -1;
	}
    }

    void sleep(int millis) {
	try {
	    Thread.sleep(millis);
	} catch (InterruptedException ie) {
	}
    }

    void doAudioEffect(byte [] dest, byte [] src,
		       int destOffset, int srcOffset,
		       int count, int endian) {
	short sDest, sSrc, result;
	
	if (audioEffectDiff == 0)
	    audioEffectDiff = 4.0f / audioEffectDurationInSamples;
	if (endian == AudioFormat.LITTLE_ENDIAN) {
	    for (int i = 0; i < count; i+=4) {
		sDest = (short) ((dest[i+destOffset] & 0xFF) | ((dest[i+destOffset+1] & 0xFF) << 8));
		sSrc = (short) ((src[i+srcOffset] & 0xFF) | ((src[i+srcOffset+1] & 0xFF) << 8));
		result = (short) (sDest * (1 - audioEffectCurrent) + sSrc * audioEffectCurrent);
		dest[i+destOffset] = (byte) (result & 0xFF);
		dest[i+destOffset+1] = (byte) ((result >> 8) & 0xFF);
		sDest = (short) ((dest[i+destOffset+2] & 0xFF) | ((dest[i+destOffset+3] & 0xFF) << 8));
		sSrc = (short) ((src[i+srcOffset+2] & 0xFF) | ((src[i+srcOffset+3] & 0xFF) << 8));
		result = (short) (sDest * (1 - audioEffectCurrent) + sSrc * audioEffectCurrent);
		dest[i+destOffset+2] = (byte) (result & 0xFF);
		dest[i+destOffset+3] = (byte) ((result >> 8) & 0xFF);
		audioEffectCurrent += audioEffectDiff;
	    }
	} else {
	    for (int i = 0; i < count; i+=4) {
		sDest = (short) ((dest[i+destOffset+1] & 0xFF) | ((dest[i+destOffset+0] & 0xFF) << 8));
		sSrc = (short) ((src[i+srcOffset+1] & 0xFF) | ((src[i+srcOffset+0] & 0xFF) << 8));
		result = (short) (sDest * (1 - audioEffectCurrent) + sSrc * audioEffectCurrent);
		dest[i+destOffset+1] = (byte) (result & 0xFF);
		dest[i+destOffset+0] = (byte) ((result >> 8) & 0xFF);
		sDest = (short) ((dest[i+destOffset+3] & 0xFF) | ((dest[i+destOffset+2] & 0xFF) << 8));
		sSrc = (short) ((src[i+srcOffset+3] & 0xFF) | ((src[i+srcOffset+2] & 0xFF) << 8));
		result = (short) (sDest * (1 - audioEffectCurrent) + sSrc * audioEffectCurrent);
		dest[i+destOffset+3] = (byte) (result & 0xFF);
		dest[i+destOffset+2] = (byte) ((result >> 8) & 0xFF);
		audioEffectCurrent += audioEffectDiff;
	    }
	}
    }

    void doVideoEffect(Buffer dest, Buffer source /*next*/,
		       float effectRatio) {
	//System.err.println("Doing effect at " + effectRatio);
	// Assume wipe
	byte[] destData = (byte[]) dest.getData();
	byte[] srcData = (byte[]) source.getData();
	RGBFormat rgb = (RGBFormat) dest.getFormat();
	int videoHeight = rgb.getSize().height;
	int stride = rgb.getLineStride();
	if (effectRatio > 1.0f)
	    effectRatio = 1.0f;
	if (effectRatio <= 0.0f)
	    return;
	if (effectVideo[currentLocator].equalsIgnoreCase("Scroll")) {
	    int nInLines = (int) (videoHeight * effectRatio);
	    if (nInLines == 0)
		return;
	    System.arraycopy(destData, nInLines * stride,
			     destData, 0,
			     (videoHeight - nInLines) * stride);
	    System.arraycopy(srcData, 0,
			     destData, (videoHeight - nInLines) * stride,
			     nInLines * stride);
	} else if (effectVideo[currentLocator].equalsIgnoreCase("Fade")) {
	    for (int y = 0; y < videoHeight * stride; y++) {
		destData[y] = (byte)
		    ((destData[y] & 0xFF) * (1.0 - effectRatio) +
		     (srcData[y] & 0xFF) * effectRatio);
	    }
	}
    }
    
    private void startNextProcessor() {
	if ( inputProcessorNext != null &&
	     inputProcessorNext.getTargetState() != Controller.Started) {
	    // Dont set media time - bug in JMF 2.0, fixed in next release
	    //inputProcessorNext.setMediaTime(beginTime[currentLocator+1]);
	    inputProcessorNext.start();
	    //System.err.println("Starting next proc");
	}
    }

    public int timeToSamples(long nanos) {
	int samples = (int) ((SPS * nanos) / NPS);
	return (samples & ~3);
    }

    public long samplesToTime(int samples) {
	long nanos = (samples * NPS) / SPS;
	return nanos;
    }

    void pushData(Buffer buffer, BufferTransferHandler bth,
			       PushBufferStream pbs) {
	Format format = buffer.getFormat();
	int index;
	// Check if this buffer is from the next data source
	// Keep the buffer around until its length becomes zero.
	// This happens when a corresponding buffer from the current
	//  datasource comes in and consumes the buffer.
	try {   // UGLY HACK!!!
	    if ((index = fromNextStream(bth)) >= 0) {
		bufferNext[index] = buffer;
		while (bufferNext[index].getLength() != 0) {
		    sleep(50);
		}
		return;
	    }
	} catch (Exception e) {
	}

	long timeStamp = buffer.getTimeStamp();
	// VideoFormat
	if (format instanceof VideoFormat) {
	    if (timeStamp > overlapTimeStamp) {
		// Start the next processor if its not already
		startNextProcessor();
		float effectRatio = (float) (timeStamp - overlapTimeStamp) /
		                                             overlapDuration;
		boolean done = false;
		// This is an inefficient loop
		while (!done) {
		    for (int k = 0; k < bufferNext.length; k++) {
			if ( bufferNext[k] != null &&
			     bufferNext[k].getFormat() != null &&
			     bufferNext[k].getFormat() instanceof VideoFormat &&
			     bufferNext[k].getLength() > 0 ) {
			    
			    doVideoEffect(buffer, bufferNext[k], effectRatio);
			    bufferNext[k].setLength(0);
			    done = true;
			    break;
			}
		    }
		    
		    if (!done) {
			sleep(50);
		    }
		}
	    } // else just continue to dump the buffer
	// AudioFormat
	} else {
	    if (timeStamp > overlapTimeStamp ||
		timeStamp + samplesToTime(buffer.getLength()) > overlapTimeStamp) {
		// Start the next processor if its not already
		startNextProcessor();
		int mergeCount = buffer.getLength();
		int mergeStart = buffer.getOffset();
		if (timeStamp < overlapTimeStamp) {
		    mergeStart += timeToSamples(overlapTimeStamp - timeStamp);
		    mergeCount -= timeToSamples(overlapTimeStamp - timeStamp);
		}
		//System.err.println("Waiting for " + mergeCount + " audio samples from next");
		boolean done = false;
		while (!done) {
		    for (int k = 0; k < bufferNext.length; k++) {
			if ( bufferNext[k] != null &&
			     bufferNext[k].getFormat() != null &&
			     bufferNext[k].getFormat() instanceof AudioFormat &&
			     bufferNext[k].getLength() > 0 ) {
			    // discard the next processors buffers
			    if (bufferNext[k].getLength() >= mergeCount) {
				doAudioEffect((byte[])buffer.getData(), (byte[])bufferNext[k].getData(),
					      mergeStart, bufferNext[k].getOffset(),
					      mergeCount,
					      ((AudioFormat)buffer.getFormat()).getEndian());
				bufferNext[k].setLength(bufferNext[k].getLength() - mergeCount);
				bufferNext[k].setOffset(bufferNext[k].getOffset() + mergeCount);
				mergeCount = 0;
				done = true;
			    } else {
				doAudioEffect((byte[])buffer.getData(), (byte[])bufferNext[k].getData(),
					      mergeStart, bufferNext[k].getOffset(),
					      bufferNext[k].getLength(),
					      ((AudioFormat)buffer.getFormat()).getEndian());
				mergeCount -= bufferNext[k].getLength();
				mergeStart += bufferNext[k].getLength();
				bufferNext[k].setLength(0);
			    }
			    //System.err.println("mergecount = " + mergeCount);
			}
		    }
		    if (!done) {
			sleep(50);
		    }
		}
	    }
	}
	
	for (int i = 0; i < outputStreams.length; i++) {
	    if ((outputStreams[i].getFormat() instanceof VideoFormat &&
		 format instanceof VideoFormat) ||
		(outputStreams[i].getFormat() instanceof AudioFormat &&
		 format instanceof AudioFormat) ) {
		//System.err.println("before pushdata");
		outputStreams[i].pushData(buffer);
		updateTime();
		//System.err.println("after pushdata");
		return;
	    }
	}
	System.err.println("!!! " + format + " didn't match any stream ");
    }

    boolean createInputDataSource(String mediaFile) {
	Processor proc = null;
	try {
	    MediaLocator ml = new MediaLocator(mediaFile);
	    proc = Manager.createProcessor(ml);
	    if (proc != null) {
		boolean reachedState = waitForState(proc, Processor.Configured);
		if (!reachedState)
		    return false;
		    
		TrackControl [] tracks = proc.getTrackControls();
		// get the formats, check the type and set the new
		// formats. Realize the proc and get the datasource
		// Set the local variables with datasource/stream/format
		// information
		for (int i = 0; i < tracks.length; i++) {
		    Format format = tracks[i].getFormat();
		    Format [] supported = tracks[i].getSupportedFormats();
		    if (format instanceof AudioFormat) {
		        for (int j = 0; j < supported.length; j++) {
		            if (supported[j].matches(preferredAudioFormat)) {
		                preferredAudioFormat = (AudioFormat) supported[j];
		                break;
		            }
		        }
			tracks[i].setFormat(preferredAudioFormat);
		    } else if (format instanceof VideoFormat) {
			Dimension size = ((VideoFormat)format).getSize();
			if (MiniME.getSaveType() == 1) {
			    
			    preferredVideoFormat = new RGBFormat(size,
								 size.width * size.height * 3,
								 Format.byteArray,
								 ((VideoFormat)format).getFrameRate(),
								 24,
								 1, 2, 3);
			} else {
			    preferredVideoFormat = new RGBFormat(size,
								 size.width * size.height * 3,
								 Format.byteArray,
								 ((VideoFormat)format).getFrameRate(),
								 24,
								 3, 2, 1,
								 3, size.width * 3,
								 RGBFormat.TRUE,
								 Format.NOT_SPECIFIED);
			} 
			for (int j = 0; j < supported.length; j++) {
			    if (supported[j].matches(preferredVideoFormat)) {
			        preferredVideoFormat = (RGBFormat) supported[j];
			        break;
			    }
			}
			tracks[i].setFormat(preferredVideoFormat);
		    } else {
			System.err.println("Bad track in processor for " + mediaFile);
		    }
		}
		proc.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
		reachedState = waitForState(proc, Controller.Realized);
		if (!reachedState)
		    return false;
		DataSource ds = proc.getDataOutput();
		if (ds == null)
		    return false;
		inputSourceNext = ds;
		ds.connect();
		inputStreamsNext = ((PushBufferDataSource)ds).getStreams();
		inputProcessorNext = proc;
		return true;
	    }
	} catch (MalformedURLException mue) {
	    System.err.println("URL is fishy!");
	} catch (IOException ioe) {
	    System.err.println("IOException creating processor!");
	} catch (NoProcessorException npe) {
	    System.err.println("Could not create processor!");
	}

	return false;
    }

    
    /****************************************************************
     * INNER CLASSES
     ****************************************************************/

    // BufferTransferHandler for input streams
    // Warning !!!  Hardcoded to 44100 16bit Stereo
    final int SPS = 44100 * 2 * 2; // Samples per second
    final long NPS = (long) 1E+9; // Nanoseconds per second
    
    class BTH implements BufferTransferHandler {
	boolean endOfMedia = false;
	Buffer currentBuffer = new Buffer();
	boolean consumed = true;
	int id;
	PushBufferStream pbs;
	long beginTime, endTime;

	// BTH
	public BTH(PushBufferStream pbs, Time beginTime, Time endTime) {
	    this.pbs = pbs;
	    this.beginTime = beginTime.getNanoseconds();
	    this.endTime = endTime.getNanoseconds();
	}

	// BTH
	public synchronized void transferData(PushBufferStream pbs) {
	    if (this.pbs != pbs) {
		System.err.println("EEEK!!!");
	    }
	    //System.err.println("Transfer data.in " + this + " " + pbs.getFormat().getEncoding());
	    try {
		currentBuffer.setFlags(0);
		
		pbs.read(currentBuffer);
	    } catch (IOException ioe) {
		System.err.println("IOException reading input source stream");
		handleEOM(pbs);
		return;
	    }
	    // See if stream is close to endTime
	    long timeStamp = currentBuffer.getTimeStamp();
	    long expectedEndTime = endTime;

	    if (currentBuffer.getFormat() instanceof VideoFormat) {
		if (timeStamp > expectedEndTime) {
		    currentBuffer.setEOM(true);
		}
		if (timeStamp < beginTime) {
		    //System.err.println("Dropping unwanted video data");
		    return;
		}
	    } else {  // audio buffer
		Thread.yield();
		if (timeStamp < beginTime) {
		    int chopOff = timeToSamples(beginTime - timeStamp);
		    currentBuffer.setOffset(currentBuffer.getOffset() + chopOff);
		    currentBuffer.setLength(currentBuffer.getLength() - chopOff);
		}
		if (preferredVideoFormat != null) {
		    expectedEndTime = expectedEndTime +
			(long)(NPS / preferredVideoFormat.getFrameRate());
		}
		if (timeStamp > expectedEndTime) {
		    currentBuffer.setEOM(true);
		} else {
		    long diff = samplesToTime(currentBuffer.getLength());
		    if (timeStamp + diff <= beginTime) { // old data, discard
			//System.err.println("Dropping unwanted audio data");
			return;
		    }
		    if (timeStamp + diff > expectedEndTime) {
			int newLength = (int) timeToSamples(expectedEndTime - timeStamp);
			currentBuffer.setLength(newLength);
		    }
		}
	    }
	    if (currentBuffer.isEOM()) {
		if (!endOfMedia) {
		    //System.err.println("EOM from " + currentBuffer.getFormat().getEncoding());
		    endOfMedia = true;
		    handleEOM(pbs);
		}
	    } else {
		// Assume this is consumed by downstream before returning
		//System.err.println("Transfer data.out " + this + " " + pbs.getFormat().getEncoding());
		pushData(currentBuffer, this, pbs);
	    }
	    
	    currentBuffer.setLength(0);
	}
    } // class BTH

    // PushBufferStream for output streams

    class SuperGlueStream implements PushBufferStream {

	boolean eos = false;
	Format format;
	BufferTransferHandler transferHandler = null;
	Buffer pendingBuffer = new Buffer();
	boolean dataPending = false;
	long currentTime = 0;
	long timeDiff = -1;
	
	public SuperGlueStream(Format format) {
	    this.format = format;
	    if (format instanceof VideoFormat) {
	        timeDiff = (long)
		    (NPS / (((VideoFormat)format).getFrameRate()));
	    }
	}

	public void pushData(Buffer buffer) {
	    if (transferHandler == null)
		return;
	    pendingBuffer = buffer;
	    if (buffer.isEOM())
		eos = true;
	    // Set the time stamp
	    //pendingBuffer.setFormat(format);
	    pendingBuffer.setTimeStamp(currentTime);
	    if (timeDiff > 0) {
	        currentTime += timeDiff;
	        //System.err.println("Video time = " + (currentTime / 1000000));
	    } else {
	        long diff = pendingBuffer.getLength();
	        diff = (diff * NPS) / (SPS);
	        currentTime += diff;
	        //System.err.println("Audio time = " + (currentTime / 1000000));
	    }
	    dataPending = true;
	    //System.err.println("before transferData()");
	    transferHandler.transferData(this);
	    //System.err.println("after transferData()");
	    synchronized (this) {
		notifyAll();
	    }
	    //System.err.println("notified");
	    synchronized (this) {
		while (dataPending) {
		    try {
			wait();
		    } catch (InterruptedException ixe) {
		    }    
		}
	    }
	    //System.err.println("done pushData()");
	}

	public void sleep(int time) {
	    try {
		Thread.sleep(time);
	    } catch (InterruptedException ie) {
	    }
	}
	
	public void read(Buffer buffer) {
	    //System.err.println("entering read");
	    synchronized (this) {
		while (!dataPending) {
		    try {
			wait();
		    } catch (InterruptedException ie) {
		    }
		}
	    }
	    //System.err.println("... in read");
	    // Swap
	    Object data = buffer.getData();
	    Format format = buffer.getFormat();
	    Object header = buffer.getHeader();

	    buffer.setData(pendingBuffer.getData());
	    buffer.setFormat(pendingBuffer.getFormat());
	    buffer.setHeader(pendingBuffer.getHeader());
	    buffer.setTimeStamp(pendingBuffer.getTimeStamp());
	    buffer.setFlags(pendingBuffer.getFlags() | Buffer.FLAG_NO_SYNC);
	    buffer.setLength(pendingBuffer.getLength());
	    buffer.setOffset(pendingBuffer.getOffset());
	    buffer.setSequenceNumber(pendingBuffer.getSequenceNumber());
	    
	    pendingBuffer.setData(data);
	    pendingBuffer.setFormat(format);
	    pendingBuffer.setHeader(header);

	    dataPending = false;
	    synchronized (this) {
		notifyAll();
	    }
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
	    return format;
	}

	public void setTransferHandler(BufferTransferHandler bth) {
	    transferHandler = bth;
	}
	
	public Object getControl(String name) {
	    // No controls
	    return null;
	}

	public Object [] getControls() {
	    // No controls
	    return new Control[0];
	}
	
    } // class SuperGlueStream

    
    class StateListener implements ControllerListener {
	
	public void controllerUpdate(ControllerEvent ce) {
	    if (ce instanceof ControllerClosedEvent)
		setFailed();
	    
	    if (ce instanceof ControllerEvent)
		synchronized (getStateLock()) {
		    getStateLock().notifyAll();
		}
	}
    }


    /****************************************************************
     * Boring methods of SuperGlueDataSource
     ****************************************************************/
    

    private synchronized boolean waitForState(Processor p, int state) {
	StateListener sl = new StateListener();
	p.addControllerListener(sl);
	failed = false;

	if (state == Processor.Configured) {
	    p.configure();
	} else if (state == Processor.Realized) {
	    p.realize();
	}

	while (p.getState() < state && !failed) {
	    synchronized (getStateLock()) {
		try {
		    getStateLock().wait();
		} catch (InterruptedException ie) {
		    return false;
		}
	    }
	}	
	p.removeControllerListener(sl);
	return !failed;
    }
    
    Integer getStateLock() {
	return stateLock;
    }

    void setFailed() {
	failed = true;
    }
    
    public Object getControl(String name) {
	// No controls
	return null;
    }
    
    public Object [] getControls() {
	// No controls
	return new Control[0];
    }

    /*
    public Time getDuration() {
	return Duration.DURATION_UNKNOWN;
    }
    */
    
    public void disconnect() {
	connected = false;
	currentLocator = 0;
    }

    public String getContentType() {
	return ContentDescriptor.RAW;
    }

    public MediaLocator getLocator() {
	return mediaLocator;
    }

    public void setLocator(MediaLocator ml) {
	System.err.println("Not interested in a media locator");
    }
    
}
