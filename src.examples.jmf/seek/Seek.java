package seek;
/*
 * @(#)Seek.java	1.2 01/03/13
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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Duration;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.SizeChangeEvent;
import javax.media.Time;
import javax.media.control.FramePositioningControl;
import javax.media.protocol.DataSource;


/**
 * Sample program to demonstrate FramePositioningControl.
 */
public class Seek extends Frame implements ControllerListener, ActionListener {

    Player p;
    FramePositioningControl fpc;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    int totalFrames = FramePositioningControl.FRAME_UNKNOWN;

    Panel cntlPanel;
    Button fwdButton;
    Button bwdButton;
    Button rndButton;

    /**
     * Given a DataSource, create a player and use that player
     * as a player to playback the media.
     */
    public boolean open(DataSource ds) {

	System.err.println("create player for: " + ds.getContentType());

	try {
	    p = Manager.createPlayer(ds);
	} catch (Exception e) {
	    System.err.println("Failed to create a player from the given DataSource: " + e);
	    return false;
	}

	p.addControllerListener(this);

	p.realize();
	if (!waitForState(Controller.Realized)) {
	    System.err.println("Failed to realize the player.");
	    return false;
	}

	// Try to retrieve a FramePositioningControl from the player.
	fpc = (FramePositioningControl)p.getControl("javax.media.control.FramePositioningControl");

	if (fpc == null) {
	    System.err.println("The player does not support FramePositioningControl.");
	    System.err.println("There's no reason to go on for the purpose of this demo.");
	    return false;
	}

	Time duration = p.getDuration();

	if (duration != Duration.DURATION_UNKNOWN) {
	    System.err.println("Movie duration: " + duration.getSeconds());

	    totalFrames = fpc.mapTimeToFrame(duration);
	    if (totalFrames != FramePositioningControl.FRAME_UNKNOWN)
		System.err.println("Total # of video frames in the movies: " + totalFrames);
	    else
		System.err.println("The FramePositiongControl does not support mapTimeToFrame.");

	} else {
	    System.err.println("Movie duration: unknown"); 
	}
	
	// Prefetch the player.
	p.prefetch();
	if (!waitForState(Controller.Prefetched)) {
	    System.err.println("Failed to prefetch the player.");
	    return false;
	}

	// Display the visual & control component if there's one.

	setLayout(new BorderLayout());

	cntlPanel = new Panel();

	fwdButton = new Button("Forward");
	bwdButton = new Button("Backward");
	rndButton = new Button("Random");

	fwdButton.addActionListener(this);
	bwdButton.addActionListener(this);
	rndButton.addActionListener(this);

	cntlPanel.add(fwdButton);
	cntlPanel.add(bwdButton);
	cntlPanel.add(rndButton);

	Component vc;
	if ((vc = p.getVisualComponent()) != null) {
	    add("Center", vc);
	}

	add("South", cntlPanel);

	setVisible(true);

	return true;
    }


    public void addNotify() {
	super.addNotify();
	pack();
    }


    /**
     * Block until the player has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {}
	}
	return stateTransitionOK;
    }


    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();
	if (command.equals("Forward")) {
	    int dest = fpc.skip(1);
	    System.err.println("Step forward " + dest + " frame.");
	} else if (command.equals("Backward")) {
	    int dest = fpc.skip(-1);
	    System.err.println("Step backward " + dest + " frame.");
	} else if (command.equals("Random")) {
	    if (totalFrames == FramePositioningControl.FRAME_UNKNOWN)
		System.err.println("Cannot jump to a random frame.");
	    else {
		int randomFrame = (int)(totalFrames * Math.random());
		randomFrame = fpc.seek(randomFrame);
		System.err.println("Jump to a random frame: " + randomFrame);
	    }
	}

	int currentFrame = fpc.mapTimeToFrame(p.getMediaTime());
	if (currentFrame != FramePositioningControl.FRAME_UNKNOWN)
	    System.err.println("Current frame: " + currentFrame);
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
	    p.setMediaTime(new Time(0));
	    //p.start();
	    //p.close();
	    //System.exit(0);
	} else if (evt instanceof SizeChangeEvent) {
	}
    }



    /**
     * Main program
     */
    public static void main(String [] args) {

	if (args.length == 0) {
	    prUsage();
	    System.exit(0);
 	}

	MediaLocator ml;

	if ((ml = new MediaLocator(args[0])) == null) {
	    System.err.println("Cannot build media locator from: " + args[0]);
	    prUsage();
	    System.exit(0);
	}

	DataSource ds = null;

	// Create a DataSource given the media locator.
	try {
	    ds = Manager.createDataSource(ml);
	} catch (Exception e) {
	    System.err.println("Cannot create DataSource from: " + ml);
	    System.exit(0);
	}

	Seek seek = new Seek();
	if (!seek.open(ds))
	    System.exit(0);
    }

    static void prUsage() {
	System.err.println("Usage: java Seek <url>");
    }
}
