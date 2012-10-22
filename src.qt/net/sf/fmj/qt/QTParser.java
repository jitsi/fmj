package net.sf.fmj.qt;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PullDataSource;
import javax.media.util.ImageToBuffer;

import quicktime.QTException;

import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;


/**
 * 
 * @author Ken Larson
 *
 */
public class QTParser extends AbstractDemultiplexer 
{
	private static final Logger logger = LoggerSingleton.logger;

	
	private static final boolean ENABLE_AUDIO = false;	
	
	
	// if USE_DATASOURCE_URL_ONLY is true, this is a bit of a hack - we don't really use the DataSource, we just grab its URL.  So arbitrary data sources won't work.
	private final boolean USE_DATASOURCE_URL_ONLY = true;
	
	private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] {
			new ContentDescriptor(FileTypeDescriptor.MSVIDEO),	// .avi
			new ContentDescriptor(FileTypeDescriptor.QUICKTIME),	// .mov
			// TODO: others
			// TODO: query dynamically.
			
	};
	
	private QTSnapper qtSnapper;
	
	
	public QTParser()
	{
	}
	
	private static final Object QT_SYNC_OBJ = new Boolean(true);	// synchronize on this before using the libraries, to prevent threading problems.
	
	private PullDataSource source;
	
	private PullSourceStreamTrack[] tracks;
	
	
	@Override
	public ContentDescriptor[] getSupportedInputContentDescriptors()
	{
		return supportedInputContentDescriptors;
	}

	
	@Override
	public Track[] getTracks() throws IOException, BadHeaderException
	{
		return tracks;
	}

	
	@Override
	public void setSource(DataSource source) throws IOException, IncompatibleSourceException
	{
		final String protocol = source.getLocator().getProtocol();
		
		if (USE_DATASOURCE_URL_ONLY)
		{
			if (!(protocol.equals("file")) && !(protocol.equals("http")))
				throw new IncompatibleSourceException();

		}
		else
		{
		
		if (!(source instanceof PullDataSource))
			throw new IncompatibleSourceException();
		}
		
		this.source = (PullDataSource) source;
		
	}
	
	

	
	// @Override
	@Override
	public void open() throws ResourceUnavailableException
	{
		synchronized (QT_SYNC_OBJ)
		{
			try
			{
				final String urlStr;

				if (USE_DATASOURCE_URL_ONLY)
				{
					// just use the file URL from the datasource
					// FMJ supports relative file URLs, but not sure if qt does.  So we'll rewrite the URL here:
					// TODO: perhaps we should only do this if qt has a problem.
					if (source.getLocator().getProtocol().equals("file"))
						urlStr = URLUtils.createUrlStr(new File(URLUtils.extractValidPathFromFileUrl(source.getLocator().toExternalForm())));
					else
						urlStr = source.getLocator().toExternalForm(); 
					qtSnapper = new QTSnapper(urlStr);
				} else
				{
					throw new RuntimeException();
					
				}

				

				VideoTrack videoTrack = null;
				AudioTrack audioTrack = null;

				videoTrack = new VideoTrack();
				
				if (audioTrack == null && videoTrack == null)
					throw new ResourceUnavailableException("No audio or video track found");
				else if (audioTrack != null && videoTrack != null)
					tracks = new PullSourceStreamTrack[] { videoTrack, audioTrack };
				else if (audioTrack != null)
					tracks = new PullSourceStreamTrack[] { audioTrack };
				else
					tracks = new PullSourceStreamTrack[] { videoTrack };

			} catch (Exception e)
			{
				logger.log(Level.WARNING, "" + e, e);
				throw new ResourceUnavailableException("" + e);
			}
		}

		super.open();

	}

	@Override
	public void close()
	{
		synchronized (QT_SYNC_OBJ)
		{
			if (tracks != null)
			{
				for (int i = 0; i < tracks.length; ++i)
				{
					if (tracks[i] != null)
					{
						tracks[i].deallocate();
						tracks[i] = null;
					}
				}
				tracks = null;
			}


		}
		super.close();
	}

	// @Override
	@Override
	public void start() throws IOException
	{
	}
	
	// TODO: should we stop data source in stop?
//	// @Override
//	public void stop()
//	{
//		try 
//		{
//			source.stop();
//		} catch (IOException e) 
//		{
//			logger.log(Level.WARNING, "" + e, e);
//		}
//	}
	
	
	
	@Override
	public boolean isPositionable()
	{
		return false;	// TODO
	}
	
//	@Override
//	public Time setPosition(Time where, int rounding)
//	{
//		synchronized (QT_SYNC_OBJ)
//		{	
//			
//		}
//	}

	
	@Override
	public boolean isRandomAccess()
	{
		return super.isRandomAccess();	// TODO: can we determine this from the data source?
	}
	
    public static VideoFormat convertCodecPixelFormat(QTSnapper qtSnapper)
    {
    	// resulting format based on what QTSnapper will return.  Depends on a bit of internal 
    	// knowledge of how QTSnapper and ImageToBuffer work.
    	
    	// TODO: we are ignoring any cropping here.
    	
    	//final Dimension size = new Dimension(128, 128);
		final Dimension size = new Dimension(qtSnapper.getImageWidth(), qtSnapper.getImageHeight());
		final int maxDataLength;
		final Class dataType;
		final int bitsPerPixel;
		final float frameRate = -1.f; // TODO
		
		final int red;
		final int green;
		final int blue;

		// QTSnapper returns BufferedImage.TYPE_INT_RGB
		final int bufferedImageType = BufferedImage.TYPE_INT_RGB;
		
		if (bufferedImageType == BufferedImage.TYPE_3BYTE_BGR)
		{
			maxDataLength = qtSnapper.getImageWidth() * qtSnapper.getImageHeight() * 3; 
			dataType = Format.byteArray;
			bitsPerPixel = 24;
			red = 1;
			green = 2;
			blue = 3;
		}
		else if (bufferedImageType == BufferedImage.TYPE_INT_BGR)
		{
			maxDataLength = qtSnapper.getImageWidth() * qtSnapper.getImageHeight(); 
			dataType = Format.intArray;
			bitsPerPixel = 32;	
			// TODO: test
			red = 0xFF;
			green = 0xFF00;	
			blue = 0xFF0000;
		}
		else if (bufferedImageType == BufferedImage.TYPE_INT_RGB)
		{
			maxDataLength = qtSnapper.getImageWidth() * qtSnapper.getImageHeight(); 
			dataType = Format.intArray;
			bitsPerPixel = 32;	
			red = 0xFF0000;
			green = 0xFF00;
			blue = 0xFF;
		}
		else if (bufferedImageType == BufferedImage.TYPE_INT_ARGB)
		{
			maxDataLength = qtSnapper.getImageWidth() * qtSnapper.getImageHeight(); 
			dataType = Format.intArray;
			bitsPerPixel = 32;	
			red = 0xFF0000;
			green = 0xFF00;
			blue = 0xFF;
			// just ignore alpha
		}
		else
			throw new IllegalArgumentException("Unsupported buffered image type: " + bufferedImageType);
			
		return new RGBFormat(size, maxDataLength, dataType, frameRate, bitsPerPixel, red, green, blue);
    	
    }
    

    
 

	private abstract class PullSourceStreamTrack extends AbstractTrack
	{
		public abstract void deallocate();

	}
	


	
	private class VideoTrack extends PullSourceStreamTrack
	{
		// TODO: track listener
		
		private final VideoFormat format;
		
		public VideoTrack() throws ResourceUnavailableException
		{
			super();
		
		
			synchronized (QT_SYNC_OBJ)
	    	{
			    // set format
				format = convertCodecPixelFormat(qtSnapper);
	    	}
		}
		
		
		@Override
		public void deallocate()
		{
		}

		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException
		{
			return 0;	// TODO
			
		}
		
		public boolean canSkipNanos()
		{
			return false;
		}

		@Override
		public Format getFormat()
		{
			return format;
		}

//		  TODO: from JAVADOC:
//		   This method might block if the data for a complete frame is not available. It might also block if the stream contains intervening data for a different interleaved Track. Once the other Track is read by a readFrame call from a different thread, this method can read the frame. If the intervening Track has been disabled, data for that Track is read and discarded.
//
//			Note: This scenario is necessary only if a PullDataSource Demultiplexer implementation wants to avoid buffering data locally and copying the data to the Buffer passed in as a parameter. Implementations might decide to buffer data and not block (if possible) and incur data copy overhead. 
		 
		@Override
		public void readFrame(Buffer buffer)
		{
			synchronized (QT_SYNC_OBJ)
			{
				BufferedImage bi;
				try
				{
					qtSnapper.next();
					bi = qtSnapper.getFrame();
				} catch (QTException e)
				{
					throw new RuntimeException(e);	// TODO: how to handle.
				}	
				if (bi != null)
				{
					final Buffer b = ImageToBuffer.createBuffer(bi, format.getFrameRate());

					buffer.setData(b.getData());
					buffer.setLength(b.getLength());
					buffer.setOffset(b.getOffset());
					buffer.setEOM(false);
					buffer.setDiscard(false);
					buffer.setTimeStamp((qtSnapper.getFrameTime() * 1000000000L) / qtSnapper.getTimeScale());	

				} else
				{
					buffer.setEOM(true);
					buffer.setLength(0);
					
				}
			}

		}

		
		@Override
		public Time mapFrameToTime(int frameNumber)
		{
			return TIME_UNKNOWN;	
		}

		@Override
		public int mapTimeToFrame(Time t)
		{	
			return FRAME_UNKNOWN;		
		}
		
	
		
		@Override
		public Time getDuration()
		{
			synchronized (QT_SYNC_OBJ)
	    	{
				if (qtSnapper.getDuration() <= 0 || qtSnapper.getTimeScale() <= 0)
					return Duration.DURATION_UNKNOWN;
				return new Time((qtSnapper.getDuration() * 1000000000L) / qtSnapper.getTimeScale());
	    	}
			
		}

	}

	
	private class AudioTrack extends PullSourceStreamTrack
	{
		// TODO: track listener
		
		private final AudioFormat format;
		
		public AudioTrack() throws ResourceUnavailableException
		{
			super();

			synchronized (QT_SYNC_OBJ)
	    	{
				format = null;	// TODO
	    	}
		    

		}
		
		
		@Override
		public void deallocate()
		{
		}

		// TODO: implement seeking using av_seek_frame
		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException
		{
			return 0;
			
		}
		
		public boolean canSkipNanos()
		{
			return false;
		}

		@Override
		public Format getFormat()
		{
			return format;
		}

//		  TODO: from JAVADOC:
//		   This method might block if the data for a complete frame is not available. It might also block if the stream contains intervening data for a different interleaved Track. Once the other Track is read by a readFrame call from a different thread, this method can read the frame. If the intervening Track has been disabled, data for that Track is read and discarded.
//
//			Note: This scenario is necessary only if a PullDataSource Demultiplexer implementation wants to avoid buffering data locally and copying the data to the Buffer passed in as a parameter. Implementations might decide to buffer data and not block (if possible) and incur data copy overhead. 
		 
		@Override
		public void readFrame(Buffer buffer)
		{
			synchronized (QT_SYNC_OBJ)
			{
				
				throw new UnsupportedOperationException();
			}
		}
		
		@Override
		public Time mapFrameToTime(int frameNumber)
		{
			return TIME_UNKNOWN;	
		}

		@Override
		public int mapTimeToFrame(Time t)
		{	
			return FRAME_UNKNOWN;		
		}

		@Override
		public Time getDuration()
		{
			return Duration.DURATION_UNKNOWN; // TODO
		}

	}
	
//	private static final double secondsToNanos(double secs)
//	{	return secs * 1000000000.0;
//	}
}
