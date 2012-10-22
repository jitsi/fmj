package net.sf.fmj.qt;

// QTSnapper.java
// Andrew Davison, May 2005, ad@fivedots.coe.psu.ac.th

/* The specified quicktime movie is loaded, and the video track 
 identified along with its media informatiion.

 A call to getFrame() extracts a single sample (frame) from 
 the movie into a BufferedImage of type
 BufferedImage.TYPE_3BYTE_BGR, and dimensions 
 FORMAT_SIZE x FORMAT_SIZE. The image has the current time
 in hours:minutes.seconds.milliseconds written on top of it.

 The original dimensions of the image in the movie can be 
 retrieved by calling getImageWidth() and getImageHeight().

 Based on MovieFrameExtractor
 by Chris W. Johnson, February 2002
 http://lists.apple.com/archives/quicktime-java/2002/Feb/msg00062.html

 This version of QTSnapper keeps a count of the current sample,
 which is incremented each tme that getFrame() is called. This
 means that the next frame in the movie is retrieved when getFrame()
 is called. When the end of the movie is reached, the count is
 reset to 1.

 On a test Win98 machine, the reported frame rate is about 16 FPS, 
 although TimeBehavior is asking for 25 FPS.
 */

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import net.sf.fmj.utility.LoggerSingleton;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.qd.QDGraphics;
import quicktime.std.StdQTConstants;
import quicktime.std.image.CodecComponent;
import quicktime.std.image.DSequence;
import quicktime.std.image.ImageDescription;
import quicktime.std.movies.Movie;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.DataRef;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.MediaSample;
import quicktime.util.RawEncodedImage;

/**
 * Adapted from http://fivedots.coe.psu.ac.th/~ad/jg/ch285/index.html See also
 * http://www.onjava.com/pub/a/onjava/2005/06/01/kgpjava_part2.html
 * 
 * @author Andrew Davison (original)
 * @author Ken Larson (modifications for FMJ)
 * 
 */
public class QTSnapper
{
	private static final Logger logger = LoggerSingleton.logger;

	private boolean isSessionOpen = false;

	private final Movie movie;
	private final Track videoTrack;
	private final Media vidMedia;
	private MediaSample mediaSample; // current sample/frame
	private final int rowInts; // no. of ints in each row of a frame
	private final int pixData[]; // a pixel array
	private final int numSamples; // number of samples in the movie
	private final int duration;
	private final int timeScale;	// duration is measured in 1/timeScale seconds.
	private int sampIdx; // current sample index
	private final QDGraphics qdGraphics; // off-screen drawing surface
	private final DSequence dSeq; // decompressed image sequence
	private final int width; // frame width
	private final int height; // frame height
	private final BufferedImage img;

	// frame rate variables
	private final long startTime;
	private long numFramesMade;
	private static final DecimalFormat frameDf = new DecimalFormat("0.#"); // 1 dp

	/**
	 * Load the quicktime movie from fnm, obtain its video track and media
	 * information. Set up the graphics environment, pixel array, and
	 * BufferedImage objects for later.
	 */
	public QTSnapper(String theURL) throws QTException, QTSnapperException

	{
		logger.info("Loading movie: " + theURL + "...");

		// open a QuickTime session
		QTSession.open();
		isSessionOpen = true;

		// open the movie
		final DataRef urlMovie = new DataRef(theURL);
		
		// create the movie 
		movie = Movie.fromDataRef (urlMovie,StdQTConstants.newMovieActive);
 

		// extrack the video track from the movie
		videoTrack = movie.getIndTrackType(1, StdQTConstants.videoMediaType, StdQTConstants.movieTrackMediaType);
		if (videoTrack == null)
		{
			throw new QTSnapperException("Sorry, not a video");
		}

		// get the media data struct. used by the video track
		vidMedia = videoTrack.getMedia();
		numSamples = vidMedia.getSampleCount();
		duration = vidMedia.getDuration();
		timeScale = vidMedia.getTimeScale();

		sampIdx = 1; // get the first sample in the track
		mediaSample = vidMedia.getSample(0, vidMedia.sampleNumToMediaTime(sampIdx).time, 1);

		// store the width and height of the image in the media sample
		final ImageDescription imgDesc = (ImageDescription) mediaSample.description;
		width = imgDesc.getWidth();
		height = imgDesc.getHeight();

		// set up a drawing environment for coverting future frame images
		qdGraphics = new QDGraphics(imgDesc.getBounds());
		dSeq = new DSequence(imgDesc, qdGraphics, imgDesc.getBounds(), null, null, StdQTConstants.codecFlagUseImageBuffer, StdQTConstants.codecLosslessQuality,
				CodecComponent.bestFidelityCodec);
		// the sample will be decompressed into qdGraphics

		// set up an array for storing the pixel data
		rowInts = qdGraphics.getPixMap().getRowBytes() >>> 2;
		// divide by four to get number of 32-bit ints.
		pixData = new int[rowInts * height];

		// configure the BufferedImage objects
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// initialize the frame rate variables
		startTime = System.currentTimeMillis();
		numFramesMade = 0;

//		// TODO: audio
//	    Track audioTrack = movie.getIndTrackType (1, StdQTConstants.audioMediaCharacteristic, StdQTConstants.movieTrackCharacteristic);
//	    if (audioTrack != null)
//	    {
//		    System.out.println("audioTrack: " + audioTrack);
//		    Media audMedia = audioTrack.getMedia();
//		    System.out.println("audMedia: " + audMedia);
//		    
//		    MediaSample audMediaSample = audMedia.getSample(0, audMedia.sampleNumToMediaTime(sampIdx).time, 100);
//		    System.out.println("audMediaSample: " + audMediaSample);
//		    SoundDescription soundDescription = (SoundDescription) audMediaSample.description;
//		    System.out.println("soundDescription: " + soundDescription);
//		    
//		    // see http://lists.apple.com/archives/quicktime-api/2004/Oct/msg00094.html
//		   
//
//	    }
		
	} // end of QTSnapper()

	/**
	 * stopMovie() and getFrame() are synchronized so that it's not possible to
	 * terminate the QuickTime session while a frame is being copied from the
	 * movie.
	 */
	public synchronized void stopMovie()
	{
		if (isSessionOpen)
		{
			// report frame rate details
			final long duration = System.currentTimeMillis() - startTime;
			final double frameRate = ((double) numFramesMade * 1000.0) / duration;
			logger.info("FPS: " + frameDf.format(frameRate)); // 1 dp

			QTSession.close();
			isSessionOpen = false;
		}
	} // end of stopMovie()

	/**
	 * Move to the next frame in the movie.
	 * 
	 * The frame is the next one in the movie, which is counted off using
	 * sampIdx (which goes 1 to numSamples, then repeats).
	 */
	public synchronized void next() throws QTException

	{
		if (!isSessionOpen)
			return;

		if (sampIdx > numSamples) // start back with the first sample
			return; // eom

		// get the sample starting at the specified index time
		ti = vidMedia.sampleNumToMediaTime(sampIdx);
		mediaSample = vidMedia.getSample(0, ti.time, 1);
		sampIdx++;

		writeToBufferedImage(mediaSample, img);

		numFramesMade++; // count another frame generated

		
	} 
	
	private TimeInfo ti;	// timeInfo for current frame.
	
	/**
	 * Return current single sample (a frame) from the movie as a BufferedImage
	 * object.
	 */
	public synchronized BufferedImage getFrame()
	{
		if (!isSessionOpen)
			return null;
		if (sampIdx > numSamples) // start back with the first sample
			return null; // EOM
		return img;
	}
	
	/**
	 * Get the timestamp for the current frame.  Measured in 1/getTimeScale() seconds.
	 */
	public synchronized int getFrameTime()
	{	
		if (sampIdx > numSamples) // start back with the first sample
			return -1;	// EOM.
		return ti.time;
	}

	/**
	 * Write the contents of the sample into the BufferedImage
	 * 
	 * The data passes through many stages. The basic steps are to get a 'raw'
	 * image from the current media sample (frame), then write it as a pixel
	 * array into the DataBuffer part of the BufferedImage.
	 */
	private void writeToBufferedImage(MediaSample mediaSample, BufferedImage img) throws QTException

	{
		// extract the raw image from the sample
		final RawEncodedImage rawIm = RawEncodedImage.fromQTHandle(mediaSample.data);
		dSeq.decompressFrameS(rawIm, 0);
		// the raw image is rendered into qdGraphics, being decompressed
		// as it goes
		rawIm.disposeQTObject();

		/*
		 * Make the data buffer of the img BufferedImage accessible via a pixel
		 * array (imgPixelData[]).
		 */
		final WritableRaster tile = img.getWritableTile(0, 0);
		int[] imgPixelData = ((DataBufferInt) tile.getDataBuffer()).getData();

		// pull a raw image from qdGraphics (this one will be decompressed)
		final RawEncodedImage rawIm2 = qdGraphics.getPixMap().getPixelData();

		/*
		 * Transfer the raw image into the img BufferedImage via a
		 * imgPixelData[] pixel array. This is simple if the size of the raw
		 * image matches the destination pixel array (see the else branch),
		 * otherwise, only a subset is copied over (see the if branch).
		 */
		if (rowInts != width)
		{
			/*
			 * Copy raw image to pixData[], then a subset of pixData[] into
			 * imgPixelData[].
			 */
			rawIm2.copyToArray(0, pixData, 0, pixData.length);
			int pixIndex = 0, destPixIndex = 0;
			for (int row = 0; row < height; row++)
			{
				int cLimit = pixIndex + width;
				for (int cIndex = pixIndex; cIndex < cLimit; cIndex++)
					imgPixelData[destPixIndex++] = pixData[cIndex];
				pixIndex += rowInts;
			}
		} else
		{	// copy raw image to imgPixelData[] in one step
			rawIm2.copyToArray(0, imgPixelData, 0, width * height);
		}
		img.releaseWritableTile(0, 0); // release Raster access to image
	} // end of writeToBufferedImage()

	public int getImageWidth()
	{
		return width;
	}

	public int getImageHeight()
	{
		return height;
	}

	/**
	 * Duration in seconds would be duration / timeScale.
	 */
	public int getDuration()
	{
		return duration;
	}

	public int getTimeScale()
	{
		return timeScale;
	}

} // end of QTSnapper class

