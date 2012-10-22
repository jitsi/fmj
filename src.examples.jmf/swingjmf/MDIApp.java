package swingjmf;

/*
 * @(#)MDIApp.java	1.3 01/03/13
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
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class MDIApp extends Frame {

    /*************************************************************************
     * MAIN PROGRAM / STATIC METHODS
     *************************************************************************/
    
    public static void main(String args[]) {
	MDIApp mdi = new MDIApp();
    }

    static void Fatal(String s) {
    	System.err.println(s);
	//MessageBox mb = new MessageBox("JMF Error", s);
    }    

    /*************************************************************************
     * VARIABLES
     *************************************************************************/
    
    JMFrame jmframe = null;
    JDesktopPane desktop;
    FileDialog fd = null;
    CheckboxMenuItem cbAutoLoop = null;
    Player player = null;
    Player newPlayer = null;
    String filename;
    
    /*************************************************************************
     * METHODS
     *************************************************************************/
    
    public MDIApp() {
	super("Java Media Player");

	// Add the desktop pane
	setLayout( new BorderLayout() );
	desktop = new JDesktopPane();
	desktop.setDoubleBuffered(true);
	add("Center", desktop);
	setMenuBar(createMenuBar());
	setSize(640, 480);
	setVisible(true);
	
	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	} catch (Exception e) {
	    System.err.println("Could not initialize java.awt Metal lnf");
	}
	addWindowListener( new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		System.exit(0);
	    }
	} );

	Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true));
    }
    
    private MenuBar createMenuBar() {
	ActionListener al = new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Open")) {
		    if (fd == null) {
			fd = new FileDialog(MDIApp.this, "Open File",
						       FileDialog.LOAD);
			fd.setDirectory("/movies");
		    }
		    fd.show();
		    if (fd.getFile() != null) {
			String filename = fd.getDirectory() + fd.getFile();
			openFile("file:" + filename);
		    }
		} else if (command.equals("Exit")) {
		    dispose();
		    System.exit(0);
		}
	    }
	};

	MenuItem item;
	MenuBar mb = new MenuBar();
	// File Menu
	Menu mnFile = new Menu("File");
	mnFile.add(item = new MenuItem("Open"));
	item.addActionListener(al);
	mnFile.add(item = new MenuItem("Exit"));
	item.addActionListener(al);

	// Options Menu	
	Menu mnOptions = new Menu("Options");
	cbAutoLoop = new CheckboxMenuItem("Auto replay");
	cbAutoLoop.setState(true);
	mnOptions.add(cbAutoLoop);
	
	mb.add(mnFile);
	mb.add(mnOptions);
	return mb;
    }			

    /**
     * Open a media file.
     */
    public void openFile(String filename) {
	String mediaFile = filename;
	Player player = null;
	// URL for our media file
	URL url = null;
	try {
	    // Create an url from the file name and the url to the
	    // document containing this applet.
	    if ((url = new URL(mediaFile)) == null) {
		Fatal("Can't build URL for " + mediaFile);
		return;
	    }
	    
	    // Create an instance of a player for this media
	    try {
		player = Manager.createPlayer(url);
	    } catch (NoPlayerException e) {
		Fatal("Error: " + e);
	    }
	} catch (MalformedURLException e) {
	    Fatal("Error:" + e);
	} catch (IOException e) {
	    Fatal("Error:" + e);
	}
	if (player != null) {
	    this.filename = filename;
	    JMFrame jmframe = new JMFrame(player, filename);
	    desktop.add(jmframe);
	}
    }
}

class JMFrame extends JInternalFrame implements ControllerListener {
    Player mplayer;
    Component visual = null;
    Component control = null;
    int videoWidth = 0;
    int videoHeight = 0;
    int controlHeight = 30;
    int insetWidth = 10;
    int insetHeight = 30;
    boolean firstTime = true;
    
    public JMFrame(Player player, String title) {
	super(title, true, true, true, true);
	getContentPane().setLayout( new BorderLayout() );
	setSize(320, 10);
	setLocation(50, 50);
	setVisible(true);
	mplayer = player;
	mplayer.addControllerListener((ControllerListener) this);
	mplayer.realize();
	addInternalFrameListener( new InternalFrameAdapter() {
	    public void internalFrameClosing(InternalFrameEvent ife) {
		mplayer.close();
	    }
	} );
		    
    }
    
    public void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof RealizeCompleteEvent) {
	    mplayer.prefetch();
	} else if (ce instanceof PrefetchCompleteEvent) {
	    if (visual != null)
		return;
	    
	    if ((visual = mplayer.getVisualComponent()) != null) {
		Dimension size = visual.getPreferredSize();
		videoWidth = size.width;
		videoHeight = size.height;
		getContentPane().add("Center", visual);
	    } else
		videoWidth = 320;
	    if ((control = mplayer.getControlPanelComponent()) != null) {
		controlHeight = control.getPreferredSize().height;
		getContentPane().add("South", control);
	    }
	    setSize(videoWidth + insetWidth,
		    videoHeight + controlHeight + insetHeight);
	    validate();
	    mplayer.start();
	} else if (ce instanceof EndOfMediaEvent) {
	    mplayer.setMediaTime(new Time(0));
	    mplayer.start();
	}
    }
}
