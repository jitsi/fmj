package net.sf.fmj.media.handler;

import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.ClockStoppedException;
import javax.media.Codec;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.InternalErrorEvent;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.SourceCloneable;

import net.sf.fmj.ejmf.toolkit.media.AbstractPlayer;
import net.sf.fmj.media.codec.JavaSoundCodec;
import net.sf.fmj.media.parser.JavaSoundParser;
import net.sf.fmj.media.renderer.audio.JavaSoundRenderer;

/**
 * Experimental handler for WAV files.
 * Really, we should have a demux and the unknown handler should be able to handle this.
 * The one thing that the unknown handler/filter graph would have to deal with is this issue
 * of residual data in the codec.
 * 
 * @author Ken Larson
 *
 */
public abstract class JavaSoundHandler extends AbstractPlayer
{
	private static final Logger logger = LoggerSingleton.logger;

	private boolean prefetchNeeded = true;
	private Time duration;
	
	private static final boolean TRACE = true;
	
	private JavaSoundParser parser;
	
	private Track track;
	private JavaSoundRenderer renderer;
	private JavaSoundCodec codec;
	
    public void setSource(DataSource source) throws IncompatibleSourceException
	{
    	if (!(source instanceof PullDataSource))
    		throw new IncompatibleSourceException();
       	if (!(source instanceof SourceCloneable))
    		throw new IncompatibleSourceException();

    	
    	final PullDataSource pds = (PullDataSource) source;
    	
    	
    	
    	if (TRACE) logger.fine("DataSource: " + source);
  
      	// TODO: do this in prefetch or realize?
    	  
    	parser = new JavaSoundParser();
    	
    	try
		{
    		parser.open();
			parser.setSource(pds);
			parser.start();
			final Track[] tracks = parser.getTracks();
			if (tracks.length != 1)
				throw new IncompatibleSourceException("Expected exactly 1 track: " + tracks.length);
			track = parser.getTracks()[0];	// TODO: what if multiple tracks?
			logger.fine("Format: " + track.getFormat());
			
			codec = new JavaSoundCodec();
			
			
			

			
		} catch (IncompatibleSourceException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw e;
		} catch (IOException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IncompatibleSourceException(e.getMessage());
		} catch (ResourceUnavailableException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IncompatibleSourceException(e.getMessage());
		} catch (BadHeaderException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			throw new IncompatibleSourceException(e.getMessage());
		}
    	
		
		renderer = new JavaSoundRenderer();
    

	 
		
	    super.setSource(source);
	    
	}
    
	//@Override
	public void doPlayerClose()
	{
		// TODO
		logger.info("Handler.doPlayerClose");
	}

	//@Override
	public boolean doPlayerDeallocate()
	{
		logger.info("Handler.doPlayerDeallocate");
		return true;
	}

	//@Override
	public boolean doPlayerPrefetch()
	{
		if( ! prefetchNeeded ) return true;
		 
		duration = getSource().getDuration();	
		prefetchNeeded = false;
 
		return true;
	}

	//@Override
	public boolean doPlayerRealize()
	{
	    
		return true;
	}
	



	//@Override
	public void doPlayerSetMediaTime(Time t)
	{
		logger.info("Handler.doPlayerSetMediaTime" + t);
	}

	//@Override
	public float doPlayerSetRate(float rate)
	{
		logger.info("Handler.doPlayerSetRate " + rate);
		return 0;
	}

	//@Override
	public boolean doPlayerStop()
	{
		logger.info("Handler.doPlayerStop");

		return true;
	}

	//@Override
	public boolean doPlayerSyncStart(Time t)
	{
		logger.info("Handler.doPlayerSyncStart" + t);

		playSoundAsync();

		return true;
	}

	//@Override
	public Time getPlayerDuration()
	{
        if( getState() < Realized ) {
            return DURATION_UNKNOWN;
        } else

        if( getState() < Prefetched ) {
            return duration;
        } else
        
        	return DURATION_UNKNOWN;	// TODO
        //return new Time( mic.getDuration() );
	}

	//@Override
	public Time getPlayerStartLatency()
	{
		return new Time(0);
	}

	//@Override
	public Component getVisualComponent()
	{
		return null;
	}
	

	
	
	
	public void playSoundAsync()
	{
		PlaySoundThread thread = new PlaySoundThread();
		thread.start();
	}
	/** Background thread to play a sound. */
	private class PlaySoundThread extends Thread
	{
		
		public PlaySoundThread()
		{
			super("PlaySoundThread");
			
		}

		public void run()
		{
			playSoundSync();
		}
	}

	
	private void playSoundSync()
	{
		{
			
			final Format rendererInputFormat;
			final javax.media.format.AudioFormat format;
			final Buffer initialBuffer;
			
			if (codec != null)
			{
				format = (javax.media.format.AudioFormat) track.getFormat();
				logger.fine("codec input format=" + format);
				
				codec.setInputFormat(format);
				final Format[] supportedOutputFormats = codec.getSupportedOutputFormats(format);
				if (supportedOutputFormats.length == 0)
				{
					logger.warning("No supported output formats for the codec");
					return;
				}
				final Format codecOutputFormat = supportedOutputFormats[0];
				codec.setOutputFormat(codecOutputFormat);
				rendererInputFormat = codecOutputFormat;
				
				logger.fine("codecOutputFormat=" + codecOutputFormat);
	
				try
				{
					codec.open();
				} catch (ResourceUnavailableException e2)
				{
					logger.log(Level.WARNING, "" + e2, e2);	// TODO
					return;
				}
				
				initialBuffer = new Buffer();
			}
			else
			{
				format = (javax.media.format.AudioFormat) track.getFormat();
				rendererInputFormat = format;
				initialBuffer = new Buffer();
			}
			

			renderer.setInputFormat(rendererInputFormat);
			
			try
			{
				renderer.open();
			} catch (ResourceUnavailableException e1)
			{
				logger.log(Level.WARNING, "" + e1, e1);
				return;	 	// TODO
			}
			renderer.start();
			
			// TODO: we are feeding the renderer with data from the raw input stream, rather than the AudioInputStream.
			// in the case of formats like MP3, will this work?
			
			final Buffer buffer = initialBuffer;//is2.getBuffer();	// this allows us to pick up any unread data from TrackInputStream reading
			Buffer buffer2 = new Buffer();//is.getBuffer();	// this allows us to pick up any unread data from TrackInputStream reading
			while (!buffer.isEOM())
			{
				track.readFrame(buffer);
				logger.fine("read buffer from track in loop: " + buffer.getLength() + " " + bufferToString((byte[]) buffer.getData()));
				if (buffer.getFormat() == null)
					buffer.setFormat(format);
				
				if (buffer.isDiscard())
					continue;
				
				if (codec != null)
				{
					int codecResult = codec.process(buffer, buffer2);
					if (codecResult == Codec.OUTPUT_BUFFER_NOT_FILLED)
					{
						logger.fine("Codec.OUTPUT_BUFFER_NOT_FILLED");
						continue;
						// TODO: 
					}
					else if (codecResult == Codec.BUFFER_PROCESSED_FAILED)
					{
						logger.warning("Codec.BUFFER_PROCESSED_FAILED");
						return;
					}
					if (buffer2.getFormat() == null)
						buffer2.setFormat(rendererInputFormat);

					
					logger.fine("got buffer from codec: " + buffer2.getLength());
				}
				else
					buffer2 = buffer;
				
				final int result = renderer.process(buffer2);
				if (result == Renderer.BUFFER_PROCESSED_FAILED)
				{	logger.warning("Renderer.BUFFER_PROCESSED_FAILED");
					return;
				}
				// TODO: handle errors, incomplete processing
				
			}
			
			if (codec != null)
			{
				logger.fine("Codec still contains data, continuing processing");
				while (!buffer2.isEOM())
				{
					// we must have data we still have to get out of the codec
					buffer.setLength(0);
					
					int codecResult = codec.process(buffer, buffer2);
					if (codecResult == Codec.OUTPUT_BUFFER_NOT_FILLED)
					{
						logger.fine("Codec.OUTPUT_BUFFER_NOT_FILLED");
						continue;
						// TODO: 
					}
					else if (codecResult == Codec.BUFFER_PROCESSED_FAILED)
					{
						logger.warning("Codec.BUFFER_PROCESSED_FAILED");
						return;
					}
					if (buffer2.getFormat() == null)
						buffer2.setFormat(rendererInputFormat);

					
					logger.fine("got buffer from codec: " + buffer2.getLength());
					
					final int result = renderer.process(buffer2);
					if (result == Renderer.BUFFER_PROCESSED_FAILED)
					{	logger.warning("Renderer.BUFFER_PROCESSED_FAILED");
						return;
					}
					
				}
			}
			
			logger.fine("end of media");
			try 
			{
	            endOfMedia();
	        } catch(ClockStoppedException e) 
	        {
	            postEvent( new InternalErrorEvent(JavaSoundHandler.this, "Controller not in Started state at EOM") );
	        }


		}

	}

	
	private static final String bufferToString(byte[] buf)
	{
		if (buf == null)
			return "null";
		
		StringBuffer b = new StringBuffer();
		int len = buf.length;
		if (len > 5)
			len = 5;
		for (int i = 0; i < len; ++i)
		{
			String byteStr = Integer.toHexString(buf[i] & 0xff);
			if (byteStr.length() == 1)
				byteStr = "0" + byteStr;
			b.append(byteStr);
		}
		return b.toString();
	}
	

}
