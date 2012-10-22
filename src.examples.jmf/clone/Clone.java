package clone;
/*
 * @(#)Clone.java	1.2 01/03/13
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
import java.awt.Component;
import java.awt.Frame;

import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.SizeChangeEvent;
import javax.media.protocol.DataSource;
import javax.media.protocol.SourceCloneable;


/**
 * Sample program to clone a data source using the
 * cloneable DataSource and playback the result. 
 */
public class Clone extends Frame implements ControllerListener {

    Player p;
    Object waitSync = new Object();
    boolean stateTransitionOK = true;


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

	// Realize the player.
	p.prefetch();
	if (!waitForState(Controller.Prefetched)) {
	    System.err.println("Failed to realize the player.");
	    return false;
	}

	// Display the visual & control component if there's one.

	setLayout(new BorderLayout());

	Component cc;

	Component vc;
	if ((vc = p.getVisualComponent()) != null) {
	    add("Center", vc);
	}

	if ((cc = p.getControlPanelComponent()) != null) {
	    add("South", cc);
	}

	// Start the player.
	p.start();

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
	    p.close();
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
	int copies = 1;

	if ((ml = new MediaLocator(args[0])) == null) {
	    System.err.println("Cannot build media locator from: " + args[0]);
	    prUsage();
	    System.exit(0);
	}

	if (args.length > 1) {
	    try {
		copies = new Integer(args[1]).intValue();
	    } catch (NumberFormatException e) {
		System.err.println("An invalid # of copies is specified: " + args[1]);
		System.err.println("Will default to 1.");
		copies = 1;
	    }
	}

	DataSource ds = null;

	try {
	    ds = Manager.createDataSource(ml);
	} catch (Exception e) {
	    System.err.println("Cannot create DataSource from: " + ml);
	    System.exit(0);
	}

	ds = Manager.createCloneableDataSource(ds);

	if (ds == null) {
	    System.err.println("Cannot clone the given DataSource");
	    System.exit(0);
	}

	Clone clone = new Clone();

	if (!clone.open(ds))
	    System.exit(0);

	for (int i = 1; i < copies; i++) {

	    clone = new Clone();
	    if (!clone.open(((SourceCloneable)ds).createClone()))
		System.exit(0);
	}
    }

    static void prUsage() {
	System.err.println("Usage: java Clone <url> <# of copies>");
    }
}
