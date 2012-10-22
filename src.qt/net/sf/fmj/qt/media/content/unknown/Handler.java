package net.sf.fmj.qt.media.content.unknown;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.ClockStoppedException;
import javax.media.IncompatibleSourceException;
import javax.media.Time;
import javax.media.protocol.DataSource;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sf.fmj.ejmf.toolkit.media.AbstractPlayer;
import net.sf.fmj.gui.controlpanelfactory.ControlPanelFactorySingleton;
import net.sf.fmj.qt.utils.QTSessionCheck;
import net.sf.fmj.utility.LoggerSingleton;
import quicktime.QTException;
import quicktime.app.view.MoviePlayer;
import quicktime.app.view.QTFactory;
import quicktime.app.view.QTJComponent;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.ExtremesCallBack;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.AudioMediaHandler;
import quicktime.std.movies.media.DataRef;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.MediaHandler;
import quicktime.std.movies.media.VisualMediaHandler;

import com.lti.utils.synchronization.CloseableThread;

/**
 * 
 * Handler for Quicktime for Java, which bypasses most of JMF (parsers, codecs).
 * @author Ken Larson
 *
 */
public class Handler extends AbstractPlayer
{
	private static final Logger logger = LoggerSingleton.logger;

	private boolean prefetchNeeded = true;
	
	private static final boolean TRACE = true;
	
	private Movie m;
	private MovieController mc;	
	private QTJComponent qtcMovieController = null;
	private QTJComponent qtcMovie = null;
	

        
	// This will resize the window to the size of the new movie
	public void createNewMovieFromURL (String theURL) throws QTException
	{
		logger.fine(theURL);
		// create the DataRef that contains the information about where the movie is
		DataRef urlMovie = new DataRef(theURL);
		
		// create the movie 
		m = Movie.fromDataRef (urlMovie,StdQTConstants.newMovieActive);
		
		// determine whether we have audio, video, or both:
		boolean hasAudio = false;
		boolean hasVideo = false;
		
		for (int i = 1; i <= m.getTrackCount(); ++i)
		{
			final Track track = m.getTrack(i);
			final Media media = track.getMedia();
			final MediaHandler mediaHandler = media.getHandler();
			// MediaHandler can be instanceof both AudioMediaHandler and VisualMediaHandler, for example MPEGMediaHandler.
			if (mediaHandler instanceof AudioMediaHandler)
			{
				hasAudio = true;
			}
			if (mediaHandler instanceof VisualMediaHandler)
			{	hasVideo = true;
			}
		}

		if (hasAudio)
			setGainControl(new QTGainControl(m));
		
		if (hasVideo)
		{
	        if (qtcMovie == null)
	        {
        		qtcMovie = QTFactory.makeQTJComponent(new MoviePlayer(m));
	           
	        } else {
	        		qtcMovie.setMoviePlayer(new MoviePlayer(m));
	        }
		}
        
		
		startTaskThread();
        
        // create the movie controller
        //mc = new MovieController (m);
       
        // TODO: how can we create a control-panel component - just the playback bar?
        //mc.setVisible(false);	// hides the playback bar
        
        // create and add a QTComponent if we haven't done so yet, otherwise set qtc's movie controller
//        if (qtcMovieController == null)
//        {
//            qtcMovieController = QTFactory.makeQTComponent(mc);
//           
//        } else {
//            qtcMovieController.setMovieController(mc);
//        }

	}
	
	private TaskThread taskThread;
	
	private void startTaskThread()
	{
		if (taskThread == null)
		{
			taskThread = new TaskThread(m);
			taskThread.start();
		}
	}
	
	private void stopTaskThread()
	{
		if (taskThread != null)
		{	taskThread.close();
			taskThread = null;
		}
		
	}
	
	
	// see http://www.onjava.com/pub/a/onjava/2003/06/04/qtj_reintro.html
	private static class TaskThread extends CloseableThread
	{
		private final Movie m;
		public TaskThread(Movie m) {
			super("TaskThread for Movie");
			this.m = m;
		}

		public void run()
		{
			try {
				while (!isClosing())
				{
					m.task(100);
					Thread.sleep(100);
				}
			} catch (QTException e) {
				logger.log(Level.WARNING, "" + e, e);
			}
			catch (InterruptedException e) {
				
			}
			finally
			{
				setClosed();
			}
			
			
		}
		
	}
	
	
	private boolean stopTriggerNotificationRegistered = false; // prevent multiple registrations, if the user clicks stop and play again.
	
	private void registerStopTriggerNotification() throws QTException
	{
		if (stopTriggerNotificationRegistered)
			return;
        logger.finer("Registering QT stop trigger");
        new ExtremesCallBack(m.getTimeBase(), StdQTConstants.triggerAtStop) {

			@Override
			public void execute()
			{
				logger.info("QT stop trigger notification");
				stopTriggerNotificationRegistered = false;
				
                final Thread thread = new Thread("QT EOS Thread") {
                    @Override
        			public void run() {

                    	// if we don't do it at all, and the GUI calls  player.setMediaTime(new Time(0));
                    	// we end up playing it again.
        				try
        				{
        					m.stop();
        				} catch (StdQTException e)
        				{
        					logger.log(Level.SEVERE, "" + e , e);
        				}
        				try
        				{
        					endOfMedia();
        				} catch (ClockStoppedException e)
        				{
        					logger.log(Level.WARNING, "" + e , e);
        				}
          	
                    }
                };

                getThreadQueue().addThread(thread);
                

			}
        	
        }.callMeWhen();
        
        stopTriggerNotificationRegistered = true;
	}

	
    public void setSource(DataSource source) throws IncompatibleSourceException
	{
    	
    		if (TRACE) logger.fine("DataSource: " + source);
    		
    		try {
    				QTSessionCheck.check();
    			
				createNewMovieFromURL(source.getLocator().toExternalForm());
			} catch (QTException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new IncompatibleSourceException();
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new IncompatibleSourceException();
			}
	 
		
	    super.setSource(source);
	    
	}
    
	//@Override
	public void doPlayerClose()
	{
		// TODO
		logger.info("Handler.doPlayerClose");
		stopTaskThread();
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
		logger.info("Handler.doPlayerSetMediaTime (sec): " + t.getSeconds());
		
		try
		{
			m.setTime(new TimeRecord(m.getTimeScale(), (long) (t.getSeconds() * m.getTimeScale())));
		} catch (StdQTException e)
		{
			// TODO handle
			logger.log(Level.WARNING, "" + e, e);
		} catch (QTException e)
		{
			// TODO handle
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	//@Override
	public float doPlayerSetRate(float rate)
	{
		logger.fine("Handler.doPlayerSetRate " + rate);
		try
		{
			if (false)
				m.setRate(rate);	// TODO: setting this to 1.0 starts the movie
			return rate;
		} catch (StdQTException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			return getRate();
		}
	}

	//@Override
	public boolean doPlayerStop()
	{
		logger.info("Handler.doPlayerStop");
		try {
			if (m != null)
				m.stop();
		} catch (QTException err) {
			logger.log(Level.WARNING, "" + err, err);
			return false;
		}
		return true;
	}

	//@Override
	public boolean doPlayerSyncStart(Time t)
	{
		logger.info("Handler.doPlayerSyncStart (sec): " + t.getSeconds());
		try {
			registerStopTriggerNotification();
			m.start();
		} catch (QTException e) {
			logger.log(Level.WARNING, "" + e, e);
			return false;
		}

		return true;
	}

	//@Override
	public Time getPlayerDuration()
	{
        if (getState() < Realized)
		{
			return DURATION_UNKNOWN;
		} 
        else if (m != null)
		{
			try
			{
				final double totalSeconds = (double) m.getDuration() / (double) m.getTimeScale();
				return new Time(totalSeconds);

			} catch (StdQTException e)
			{
				logger.log(Level.WARNING, "" + e, e);
				return DURATION_UNKNOWN;
			}

		} 
        else
        {
			return DURATION_UNKNOWN;	
        }
	}

	//@Override
	public synchronized Time getMediaTime()
	{
    		if (getState() < Realized)
		{
			return super.getMediaTime();
		} 
        	else if (m != null)
		{
			try
			{
				final double totalSeconds = (double) m.getTime() / (double) m.getTimeScale();
				return new Time(totalSeconds);

			} catch (StdQTException e)
			{
				logger.log(Level.WARNING, "" + e, e);
				return super.getMediaTime();
			}

		} 
        	else
        	{
        		return super.getMediaTime();
        	}
	}



	// @Override
	public Time getPlayerStartLatency()
	{
		return new Time(0);
	}

	//@Override
	public Component getVisualComponent()
	{
		if (qtcMovie == null)
			return null;
		// by putting it in a panel, we prevent it from scaling if put into a different sized container.
		final JPanel result = new JPanel();

		final JComponent jComponent = qtcMovie.asJComponent();
		result.add(jComponent);
		result.setPreferredSize(jComponent.getPreferredSize());
		
		return result;

	}



//	@Override
//	public Component getControlPanelComponent()
//	{
//		if (qtcMovieController == null)
//			return null;
//		return qtcMovieController.asComponent();
//	}
	
	// until we figure out how to isolate QT's control panel component (get it without the movie panel),
	// we'll use FMJ's control panel.
    public Component getControlPanelComponent() {
        Component c = super.getControlPanelComponent();

        if( c == null ) {
            c = ControlPanelFactorySingleton.getInstance().getControlPanelComponent(this);
            setControlPanelComponent(c);
        }

        return c;
    }

}
