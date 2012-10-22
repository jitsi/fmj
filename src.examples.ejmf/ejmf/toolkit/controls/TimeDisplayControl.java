package ejmf.toolkit.controls;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.StartEvent;
import javax.media.StopEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.util.SourcedTimer;
import ejmf.toolkit.util.SourcedTimerEvent;
import ejmf.toolkit.util.SourcedTimerListener;
import ejmf.toolkit.util.TimeSource;

/**
* A read-only Control that displays media time.
* <p>
* Creates a SourcedTimer to generate ticks that trigger
* Control to look at media time, convert it to a string
* and display it.
* <p>
* Registers as SourcedTimerListener and ControllerListener.
* As ControllerListener, TimerDisplayControl listens for
* start/restart and stop events to correctly turn SourcedTimer
* on and off.
*
* @see ejmf.toolkit.util.SourcedTimer
* @see ejmf.toolkit.util.SourcedTimerListener
* @see ejmf.toolkit.util.TimerSource
*/

public class TimeDisplayControl implements Control, 
		SourcedTimerListener, 
		TimeSource,
		ControllerListener {

    private JLabel 		timerField = new JLabel("0:00:00", JLabel.RIGHT);
    private JPanel		controlComponent = new JPanel();

    private String		timeVal;

    private SourcedTimer	timer;
    private Controller		controller;
    private long		divisor;

	/** 
	* Create a TimeDisplayControl for displaying the
	* current media time of the Controller passed as an
	* argument.
	* @param controller The Controller with which to associate 
	* control.
	*/
    public TimeDisplayControl(Controller controller) {
	this.controller = controller;

	// Create a timer and listener for 'ticks'
	timer = new SourcedTimer(this);
	timer.addSourcedTimerListener(this);

	// SourcedTimer units divided by divisor
	// equals seconds.
	divisor = timer.getConversionDivisor();

	controller.addControllerListener(this);

	setUpControlComponent();

	// Initialize time label
        timeVal = convertTime(getTime(), divisor);
	loadTime();
    }

	/** 
	* Create a TimeDisplayControl for displaying the
	* current media time of the Controller passed as an
	* argument.
	* <p>
	* The <tt>timer</tt> is used as the SourcedTimer.
	* This form of the constructor allows reuse of an existing
	* SourcedTimer.
	*
	* @param controller	javax.media.Controller
	* @param timer		ejmf.toolkit.util.SourcedTimer
	*	
	* @see ejmf.toolkit.util.SourcedTimer
	*/
    public TimeDisplayControl(Controller controller, SourcedTimer timer) {
	this.controller = controller;

	// Create a timer and listener for 'ticks'
	this.timer = timer;
	timer.addSourcedTimerListener(this);

	// SourcedTimer units divided by divisor
	// equals seconds.
	divisor = timer.getConversionDivisor();

	controller.addControllerListener(this);

	setUpControlComponent();

	// Initialize time label
        timeVal = convertTime(getTime(), divisor);
	loadTime();
    }

	/** 
	* Return the control component.
	*
	* @return java.awt.Component
	*/
    public Component getControlComponent() {
	return controlComponent;
    }

	/**
	* Build border and title the control component.
	*/
    private void setUpControlComponent() {
        JLabel timeLabel = new JLabel("Media Time:", JLabel.RIGHT);
        JPanel mainPanel = new JPanel();
        int GAP = 10;

        Border emptyBorder  = new EmptyBorder(GAP,GAP,GAP,GAP);

        Border etchedBorder = new CompoundBorder(
            new EtchedBorder(), emptyBorder);

        Border titledBorder = new TitledBorder(
            etchedBorder, "Time Display Control");

        mainPanel.setBorder(titledBorder);
        mainPanel.setLayout( new BorderLayout(GAP, GAP) );
        mainPanel.add(timeLabel, BorderLayout.CENTER);
        mainPanel.add(timerField, BorderLayout.EAST);

        controlComponent.add(mainPanel);
    }


    	/**
 	* Input value expected in nanoseconds and converted	
	* to a String in h:mm:ss format.
        *
	* @param longTime	time in unit such that <tt>longTime/d</tt>
	* 			results in seconds.
	* @param		value such that <tt>longTime/d</tt> 
	* 			results in seconds.
	*
	* @return java.awt.String representation of converted time
     	*/
    private String convertTime(long longTime, long d) {
        int hr = 0;
        int min = 0;
        long sec = longTime / d;

        min = (int) (sec / 60);

        if (min > 0) {
    	    if (min > 60) {
    	        hr = min / 60;
    	        min = min % 60;
    	    }
    	    sec = sec % 60;
    	}

        String t = hr + ":";
	if (min < 10) {
	  t = t + 0 + min + ":";
	} else {
	  t = t + min + ":";
	}

        if (sec < 10) {
	  t = t + 0 + sec;
	} else {
	  t = t + sec;
	}
        return t;
     }

    
    // All but writing to text field is done before
    // call to loadTime. We don't want to do any conversion
    // on event thread.
	/*
	* Display the current media time.
	*/

    private void loadTime() {
	timerField.setText(timeVal);
    }

	/* 
	* Write time to GUI. Convenience class for	
	* execution on AWT thread.
	*/
    class LoadTimeThread implements Runnable {
	public void run() {
	    loadTime();
	}
    }

    //////////// SourcedTimerListener interface  ////////////

	/**
	* Respond to 'tick' from SourcedTimer.
	* <p>
	* @param e	a SourcedTimerEvent containing TimeSource
	* 		that generated tick.	
	*/
    public void timerUpdate(SourcedTimerEvent e) {
	timeVal = convertTime(getTime(), divisor);
	SwingUtilities.invokeLater(new LoadTimeThread());
    }

    ///////// ControllerListener Interface /////////////

	/**
	* Listen for start/stop events and start/stop 
	* SourceTimer in response.
	* @param e A controller event used to determine start/stop	
	* state of timer.
	*/
    public void controllerUpdate(ControllerEvent e) {
	if (e instanceof StopEvent ||
	    e instanceof ControllerErrorEvent) {
	    timer.stop();
	} else if (e instanceof StartEvent) {
	    timer.start();
	}
    }

    ////////// TimeSource Interface //////////////

	/**
	* Report media time from associated Controller.	
	* <p>
	* @return media time in nanoseconds
	*/
    public long getTime() {
    	return controller.getMediaNanoseconds();
    }

	/**
	* Report value such that <tt>getTime()/number</tt> equals seconds.
	* <p>
	* @return a long such that <tt>getTime()/number</tt> equals
	* 			seconds.
	*/
    public long getConversionDivisor() {
        return TimeSource.NANOS_PER_SEC;
    }

}
