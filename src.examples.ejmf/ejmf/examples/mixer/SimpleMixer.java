package ejmf.examples.mixer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;

import ejmf.toolkit.gui.ViewingPanel;
import ejmf.toolkit.multiplayer.MultiPlayer;
import ejmf.toolkit.multiplayer.MultiPlayerListener;
import ejmf.toolkit.multiplayer.TimerMultiPlayerControl;
import ejmf.toolkit.multiplayer.Track;
import ejmf.toolkit.multiplayer.TrackList;
import ejmf.toolkit.multiplayer.TrackModel;
import ejmf.toolkit.multiplayer.TrackPanel;
import ejmf.toolkit.multiplayer.TrackTable;
import ejmf.toolkit.util.EJMFProperties;
import ejmf.toolkit.util.ExtensionFilter;
import ejmf.toolkit.util.HTMLFileFilter;
import ejmf.toolkit.util.MixFileFilter;
import ejmf.toolkit.util.Utility;


/**
* A SimpleMixer displays a collection of tracks using two
* views. One view is a table that contains media locator for
* a Player and media's start and playing time. The latter two
* fields may be manipulated in the table.
* <p>
* The second view is a slider for each track. As the slider is
* manipulated, the media start time is changed.
* <p>
* A SimpleMixer may also display a ViewingPanel in which the visual
* component of each Track's Player appears.
* <p>
* The SimpleMixer interface provides a way to open and close media
* files, as well as start and stop the play of media.
*/

public class SimpleMixer extends JPanel  
	implements MultiPlayerListener {

    private int 		numberOfTracks;
    private TrackList		trackList;
    private JMenuBar		menuBar;

    private TrackModel		trackModel;
    private TrackTable		trackTable;

    // File Menu
    private JMenuItem		openItem;
    private JMenuItem		closeItem;
    private JMenuItem		saveItem;
    private JMenuItem		loadItem;

    // Control Menu
    private JMenuItem		playItem;
    private JMenuItem		restartItem;
    private JMenuItem		stopItem;
    private JMenuItem		queryItem;

    // Options Menu
    private JCheckBoxMenuItem	showItem;

    private ViewingPanel	viewingPanel;
    private MultiPlayer		multiPlayer = null;

    private EJMFProperties	props = null;
 
    private String columnNames[] = 
	{ "Track#", "Media", "Start Time", "Play Time" };

	/** 
	* Create a SimpleMixer for a given number of tracks.
	* @param numberOfTracks Number of tracks displayed and	
	* controlled by SimpleMixer.	
	*/
    public SimpleMixer(int numberOfTracks) {
	this.numberOfTracks = numberOfTracks;

	/*
	 * Create a TrackList of empty Tracks based
	 * on input argument.
	 */
	trackList = new TrackList(numberOfTracks);
	for (int i = 0; i < numberOfTracks; i++) {
	    Track track  = new Track(i);
	    trackList.addTrack(track);
	}

	menuBar = createMenuBar();

	// The MIXER_HOME property is needed from properties file.
	// It is used as root directory for load/save of MIX files.
	try {
	    props = new EJMFProperties();
	} catch (Exception e) {
	    e.printStackTrace();
	    props = null;
        }

	trackModel = new TrackModel(columnNames, trackList);

	// DefaultMultiPlayerControl version has two problems:
	// 1. On Sun, bug in setStopTime causes playing time to
	//    be truncated.
        // 2. On Intel, inability to share audio prevents > 1 player
        //    to have allocated the audio card when prefetched.
		
	multiPlayer = new MultiPlayer(trackModel,
		new TimerMultiPlayerControl(trackList));
	multiPlayer.addMultiPlayerListener(this);

	trackTable = new TrackTable(trackModel);

	JTableHeader header = trackTable.getTableHeader();

	TrackPanel trackPanel = new TrackPanel(trackModel);

	JPanel tablePanel = new JPanel();
	tablePanel.setLayout(new BorderLayout());
	// Header must be explicitly added to Container.
	tablePanel.add(header, BorderLayout.NORTH);
	tablePanel.add(trackTable, BorderLayout.SOUTH);

	JPanel modelViewPanel = new JPanel();
	modelViewPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
	modelViewPanel.add(tablePanel);
	modelViewPanel.add(trackPanel);

	setLayout(new BorderLayout());
	add(modelViewPanel, BorderLayout.NORTH);

    }

	/**	
	* @return SimpleMixer's menu bar
	*/
    public JMenuBar getMenuBar() {
	return menuBar;
    }

	//
	// Create a menu bar containing : File Control Options
	//
    protected JMenuBar createMenuBar() {
	JMenuBar menuBar = new JMenuBar();
	JMenuItem mi;

	JMenu file = (JMenu) menuBar.add(new JMenu("File"));
	file.setMnemonic('F');

        openItem = (JMenuItem) file.add(new JMenuItem("Open..."));
	openItem.setMnemonic('O');
	openItem.addActionListener(new OpenItemListener());

        closeItem = (JMenuItem) file.add(new JMenuItem("Close"));
	closeItem.setMnemonic('C');
	closeItem.addActionListener(new CloseItemListener());

	file.addSeparator();

        loadItem = (JMenuItem) file.add(new JMenuItem("Load..."));
	loadItem.setMnemonic('L');
	loadItem.addActionListener(new LoadItemListener());

	saveItem = (JMenuItem) file.add(new JMenuItem("Save As..."));
	saveItem.setMnemonic('S');
	saveItem.addActionListener(new SaveItemListener());

	file.addSeparator();

        mi = (JMenuItem) file.add(new JMenuItem("Exit"));
	mi.setMnemonic('x');
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	  	System.exit(0);
	    }
  	});

	JMenu control = (JMenu) menuBar.add(new JMenu("Control"));
	file.setMnemonic('C');

        playItem = (JMenuItem) control.add(new JMenuItem("Play"));
	playItem.setMnemonic('P');
	playItem.addActionListener(new PlayItemListener());

        stopItem = (JMenuItem) control.add(new JMenuItem("Stop"));
	stopItem.setMnemonic('S');
	stopItem.addActionListener(new StopItemListener());

	control.addSeparator();

        queryItem = (JMenuItem) control.add(new JMenuItem("Query"));
	queryItem.setMnemonic('Q');
	queryItem.addActionListener(new QueryItemListener());

	JMenu options = (JMenu) menuBar.add(new JMenu("Options"));
	file.setMnemonic('O');

        showItem = (JCheckBoxMenuItem) options.add(
			new JCheckBoxMenuItem("Show Visual"));
	showItem.setMnemonic('S');
	showItem.addActionListener(new ShowItemListener());

   	setFileMenuState(numberOfTracks);
	setControlMenuState(MultiPlayer.NOTINITIALIZED);
	return menuBar;
    }

	/**
	* @return the number of tracks associated with this SimpleMixer
	*/
    public int getNumberOfTracks() {
	return numberOfTracks;
    }

	/**	
	* Get a track associated with the given index.
	* @param index Index of desired track
	* @return a Track.
	* @see ejmf.toolkit.mulitplayer.Track
	*/
    public Track getTrack(int index) throws IllegalArgumentException {
	if (index < numberOfTracks)
	   return trackModel.getTrack(index);
	else
	   throw new IllegalArgumentException("Bad track number");
    }

    //
    //  Menu state is set based on number of available tracks
    //
    private void setFileMenuState(int avail) {
	openItem.setEnabled(avail > 0);
	closeItem.setEnabled(avail < numberOfTracks);
	saveItem.setEnabled(avail < numberOfTracks);
    }

    //
    // Set state of Control menu
    //
    private void setControlMenuState(int state) {
	boolean canPlay = (state == MultiPlayer.STOPPED ||
			   state == MultiPlayer.INITIALIZED) &&
			  (trackModel.getNumberOfAvailableTracks() < 
				getNumberOfTracks());
	playItem.setEnabled(canPlay);
	stopItem.setEnabled(!canPlay);
    }

    // Some dutiful menu item listeners.

    //
    // Open a media file. 
    //
	
    class OpenItemListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    File mediaFile = null;

	    JFileChooser chooser = new JFileChooser(getChooserRoot());
	    chooser.addChoosableFileFilter(new MixFileFilter());

	    int rc = chooser.showOpenDialog(SimpleMixer.this);

	    if (rc == JFileChooser.APPROVE_OPTION) {
		mediaFile = chooser.getSelectedFile();
		if (mediaFile != null) {
		    MediaLocator ml = null;
		    String filePath = null;	
		    try {
			filePath = mediaFile.getCanonicalPath();
		    } catch (IOException ex) {
			JOptionPane.showMessageDialog( 
		    	    SimpleMixer.this,
		    	    "Bad path name",
		    	    "MultiPlayer Error",
		    	    JOptionPane.ERROR_MESSAGE);
			return;
		    }
	            ml = Utility.appArgToMediaLocator(filePath);
		    if (ml == null) {
			JOptionPane.showMessageDialog( 
		    	    SimpleMixer.this,
		    	    "Could not create MediaLocator from file name",
		    	    "MultiPlayer Error",
		    	    JOptionPane.ERROR_MESSAGE);
			return;
		    }
	            try {
		        Player player = Manager.createPlayer(ml);
		        trackModel.assignTrack(player, ml);
		    } catch (Exception ex) {
			JOptionPane.showMessageDialog( 
		    	    SimpleMixer.this,
		    	    "Could not create Player from MediaLocator",
		    	    "MultiPlayer Error",
		    	    JOptionPane.ERROR_MESSAGE);
			return;
		    }
	    	    setFileMenuState(trackModel.getNumberOfAvailableTracks());
	    	    setControlMenuState(multiPlayer.getState());
		}
	    }
	    else {
		// generic file chooser error dialog
		return;
	    }
        }
    }

    private String getChooserRoot() {
	// Start file choose in current directory
  	// unless MIXER_HOME property is defined.
	String dir = System.getProperty("user.dir");
	if (props != null) {
	    String home = props.getProperty("MIXER_HOME");
	    if (home != null)
		//dir = home + System.getProperty("file.separator") + "media";
		// You would think JFileChooser would want file.separator
		// but it only displays last part of directory name if
		// it sees backwards slash.
		dir = home + "/" + "classes/media";
	}
	return dir;

    }
    
    //
    // Close current track.
    //
    class CloseItemListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    // get selected row, close up track
	    int row = trackTable.getSelectedRow();

	    // Don't deassign if already available.
	    if (trackModel.getTrack(row).isAvailable() == true)
		return;

	    trackModel.deassignTrack(row);
	    setFileMenuState(trackModel.getNumberOfAvailableTracks());
	    setControlMenuState(multiPlayer.getState());
	}
    }

    //
    // Save track info to a MIX file
    //
    class SaveItemListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    File trackFile = null;
	    JFileChooser chooser = new JFileChooser(getChooserRoot());
	    chooser.addChoosableFileFilter(new MixFileFilter());
	    chooser.addChoosableFileFilter(new HTMLFileFilter());

	    int rc = chooser.showSaveDialog(SimpleMixer.this);

	    if (rc == JFileChooser.APPROVE_OPTION) {
		trackFile = chooser.getSelectedFile();
		FileFilter ff = chooser.getFileFilter();
		if (ff instanceof ExtensionFilter) {
		    ExtensionFilter xff = 
		        (ExtensionFilter) chooser.getFileFilter();
	            trackModel.write(xff, trackFile);
		} else { // generate default MIX file 
		    trackModel.write(new MixFileFilter(), trackFile);
		}
	    }
	}
    }

    // 
    // Load  track info from a MIX file using JFileChooser
    //
    class LoadItemListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    File trackFile = null;
	    JFileChooser chooser = new JFileChooser(getChooserRoot());
	    chooser.addChoosableFileFilter(new MixFileFilter());

	    int rc = chooser.showDialog(SimpleMixer.this, "Load");

	    if (rc == JFileChooser.APPROVE_OPTION) {
		trackFile = chooser.getSelectedFile();
		if (trackFile == null)
		    return;

		trackModel.clear();
		rc = trackModel.read(trackFile);
		if (rc < 0) {
		    JOptionPane.showMessageDialog( 
			SimpleMixer.this,
			"Illegal data in mix file: " + trackFile.getAbsolutePath(),
			"Read Error",
			JOptionPane.ERROR_MESSAGE);
		} else {
	    	    setFileMenuState(trackModel.getNumberOfAvailableTracks());
	    	    setControlMenuState(multiPlayer.getState());
		}
	    }
	    else {
		// generic file chooser error dialog
		return;
	    }
        }
    }


    //
    // Play multiple players.
    //
    class PlayItemListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    multiPlayer.start();
	}
    }

    //
    // Responds to Query selection in Control menu.
    // Calls out to MultiPlayer.query for reporting  
    // of Player state information.

    class QueryItemListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    if (multiPlayer != null)
		multiPlayer.query();
        }
    }


    //
    // Stop MultiPlayer
    //
    class StopItemListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	        multiPlayer.stop();
	}
    }

    //
    // Show viewing area for display of video
    //
    class ShowItemListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (showItem.isSelected() == true) {
		if (viewingPanel == null) {
		    viewingPanel = new ViewingPanel("EJMF Mixer Viewer");
		    multiPlayer.setViewingPanel(viewingPanel);
		    multiPlayer.displayPlayers();
		}

		SimpleMixer.this.add(viewingPanel, BorderLayout.SOUTH);
            }
	    if (showItem.isSelected() == false) {
		SimpleMixer.this.remove(viewingPanel);
            }
	}
    }


    /** Called in response to state changes in MultiPlayer  
     *  Updates the state of Control menu items.
     *  
     * @param One of MultiPlatyer's states.
     */
    public void multiPlayerUpdate(int state) {
	setControlMenuState(state);
    }
}
