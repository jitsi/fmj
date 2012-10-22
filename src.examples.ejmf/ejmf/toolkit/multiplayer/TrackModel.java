package ejmf.toolkit.multiplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import ejmf.toolkit.util.Debug;
import ejmf.toolkit.util.ExtensionFilter;
import ejmf.toolkit.util.HTMLFileFilter;
import ejmf.toolkit.util.MixFile;
import ejmf.toolkit.util.MixFileData;
import ejmf.toolkit.util.MixTrackData;
import ejmf.toolkit.util.Utility;

/**
* A model of a collection of Tracks. This model is specifically
* designed to serve as a model for a JTable.
* <p>
* The model maintains information about Tracks in a TrackList.
* <p>
* Objects interested in changes to model can register as a 
* TrackModelListener. When a change occurs in the model, listeners
* are notified with a TrackModelEvent delivered to their
* trackModelUpdate method. There are four types of TrackModel
* events: assignment, playing time change, start time change, and	
* deassignment.
*
* @see com.java.swing.AbstractTableModel
* @see com.java.swing.JTable
* @see ejmf.toolkit.multiPlayer.TrackList
* @see ejmf.toolkit.multiPlayer.TrackModelEvent
*/

public class TrackModel extends AbstractTableModel {

    private TrackList		trackList;
    private String[]		columnNames;
    private int			availableCount;

    private EventListenerList	listeners = null;

	/**
	* Create a TrackModel with table column names and
	* a TrackList.
	* @param columnNames A String[] identifying table columns
	* @param trackList A TrackList 
	*/
    public TrackModel(String[] columnNames, TrackList trackList) {
	this.trackList = trackList;
	this.columnNames = columnNames;
	availableCount = trackList.getNumberOfTracks();
    }

	/**
	* Return TrackList associated with TrackModel.
	*/
    public TrackList getTrackList() {
	return trackList;
    }

	/**	
	* Return Track at index specified by argument.
	* @param index Index into TrackList.	
	* @return a Track at index passed as argument
	*/
    public Track getTrack(int index) {
	return trackList.getTrack(index);
    }

	/**
	* Populate a Track with information about a Player. 
	* 
	* @param player Player whose media is maintained by this
	* track.
	* @param ml	MediaLocator associated with Player.
	*/
    public void assignTrack(Player player, MediaLocator ml) {
	int index = getAvailableTrack();
	setMediaLocator(index, ml);
	setPlayer(index, player);
	synchronized (this) {
	    availableCount--;
	}
	fireTrackModelUpdate(new TrackModelAssignEvent(this, index));
    }

	/** 
	* Assign a new Track. Suppress update notification of obj.
    	*
	* @param fileName media file name from which Player will be built.
	* @param stTime	Offset at which media begins to play.
	* @param plTime Length of time media is played.
	* @exception IOException thrown if DataSource can not
	* be connected to.
	* @exception NoPlayerException thrown if no handler exists
	* for Player
	* @exception IllegalArgumentException thrown if file name argument
	* can not be converted to media locator.
	*/
    public void 
	assignTrack(String fileName, double stTime, double plTime)
			throws IllegalArgumentException,
				IOException,
				NoPlayerException
    {
	int index = getAvailableTrack();

	MediaLocator ml = Utility.appArgToMediaLocator(fileName);
	if (ml == null) {
	    throw new IllegalArgumentException("Bad media file name");
	}
	// Increment up front so that if listeners act in response
	// to event generated below, they will get correct number
	// of tracks.
	synchronized (this) {
	    availableCount--;
	}
	
	Player player = Manager.createPlayer(ml);
	setMediaLocator(index, ml);
	setPlayer(index, player);
	setStartTime(index, stTime);
	setPlayingTime(index, plTime);
	fireTrackModelUpdate(new TrackModelAssignEvent(this, index));
    }

	/**
	* Deassign Track at index in the TrackModel.
	* @param index Index of Track to be deassigned.
	*/
    public void deassignTrack(int index) {
	Debug.printObject("enter deassignTrack : " + index);
	// Decrement now so that if listeners act in response
	// to event generated below, they will get correct number
	// of tracks.
	synchronized (this) {
	    availableCount++;	
	}
	Track track = getTrack(index);
	track.close();
	setMediaLocator(index, new MediaLocator("unassigned"));
	setPlayingTime(index, 0);
	setStartTime(index, 0);
	fireTrackModelUpdate(new TrackModelDeassignEvent(this, index));
	Debug.printObject("exit deassignTrack : " + index);
    }
  
	/**
	* Deassign Track associated with Player in the TrackModel.
	*
	* @param player	Player from which Track is derived.
	*/
    public void deassignTrack(Player player) {
	try {
	    int index = findTrack(player);
	    deassignTrack(index);
	} catch (IllegalArgumentException e) {}
    }

	/**	
	* Deassign all Tracks in TrackList maintained by TrackModel.
	*/
    public void clear() {
	for (int i = 0; i < getNumberOfTracks(); i++) {
	    if (getTrack(i).isAssigned()) {
	    	deassignTrack(i);
	    }
	}
    }

	/**
	* Set the media locator for the Track at <tt>index</tt>.
	* 	
	* @param index	Index of Track.	
	* @param ml	New MediaLocator value.	
	*/
    public void setMediaLocator(int index, MediaLocator ml) {
	Track track = getTrack(index);
	track.setMediaLocator(ml);
 	try {
	    setValueAt(ml.getURL().getFile(), index, 1);
	} catch (Exception e) {}
    }

	/**
	* Set the playing time of the <tt>index</tt> Track.
        *
	* @param index Track number.
	* @param seconds A String representing a seconds value.
	*/

    public void setPlayingTime(int index, String seconds) {
	double time = Double.valueOf(seconds).doubleValue();
	Track track = getTrack(index);

	track.setPlayingTime(time);
	setValueAt(seconds, index, 3);
	fireTrackModelUpdate(
	    new TrackModelSetPlayingTimeEvent(this, index, time));
    }

	/**
	* Set the playing time of the <tt>index</tt> Track.
        *
	* @param index Track number.
	* @param seconds A double representing a seconds value.
	*/
    public void setPlayingTime(int index, double seconds) {
	setPlayingTime(index, String.valueOf(seconds));
    }

	/**
	* Set the start time of the <tt>index</tt> Track.
        *
	* @param index Track number.
	* @param seconds A String representing a seconds value.
	*/

    public void setStartTime(int index, String seconds) {
	double time = Double.valueOf(seconds).doubleValue();
	Track track = getTrack(index);	

	track.setStartTime(time);
	setValueAt(seconds, index, 2);
	fireTrackModelUpdate(
	    new TrackModelSetStartTimeEvent(this, index, time));
    }

	/**
	* Set the start time of the <tt>index</tt> Track.
        *
	* @param index Track number.
	* @param seconds A double representing a seconds value.
	*/
    protected void setStartTime(int index, double seconds) {
	setStartTime(index, String.valueOf(seconds));
    }

	/**	
	* Initialize the Player assoicated with a Track.
	*
	* @param index 	Index of Track with which Player is
	* to be associated.
	* @param player	Player to associated with Track.
	*/
    public void setPlayer(int index, Player player) {
	Debug.printObject("setPlayer " + index);
	Track track = getTrack(index);	
	track.setPlayer(player);
	// By this time duration is available
	double playtime = track.getPlayingTime();
	setPlayingTime(index, playtime);
    }
	
	/**
	* Get next Track that does not have a Player associated	
	* with it.	
	* 
	* @return The index of available Track.
	*/
    public int getAvailableTrack() {
	for (int i = 0; i < trackList.getNumberOfTracks(); i++) {
	    Track track = trackList.getTrack(i);
	    if (track.isAvailable())
	   	return i;
	}
	return -1;
    }

    /** Report number of tracks currently available or
     * unassigned.
     * @return The number of available Tracks
     */
    public int getNumberOfAvailableTracks() {
	return availableCount;
    }

    /** Report total number of tracks managed by this model.
	* @return number of Tracks managed by model.
     */
    public int getNumberOfTracks() {
	return trackList.getNumberOfTracks();
    }

    /** Report number of tracks that are currently assigned.
	* @return number of Tracks currently assigned a Player
     */
    public int getNumberOfAssignedTracks() {
	return getNumberOfTracks() - getNumberOfAvailableTracks();
    }

	/**
	* Fire an event in response to update of table cell.
	* Simply calls corresponding <tt>super</tt> method.	
	* 
	* @see javax.swing.table.AbstractTableModel;
	*/
    public void fireTableCellUpdated(int row, int col) {
	super.fireTableCellUpdated(row, col);
    }

	/**
	* Since we extend AbstractTableModel, we override
	* getColumnCount.
	* 
	* @see javax.swing.table.AbstractTableModel;
	*/
    public int getColumnCount() { return columnNames.length; }

	/**
	* Since we extend AbstractTableModel, we override
	* getColumnName.
	* 
	* @see javax.swing.table.AbstractTableModel;
	*/
    public String getColumnName(int index) {
	return columnNames[index];
    }

	/**
	* Report that start time and playing time columns are
	* editable.
	* @param row Identifies a Track within TrackModel
	* @param col Identifies a field within a Track.
	* @return true if start time or playing time column
	* is identified by <tt>col</tt>.
	* @see javax.swing.table.AbstractTableModel;
	*/
    public boolean isCellEditable(int row, int col) { 
	return (col == 2) || (col == 3);
    }

	/**
	* Since we extend AbstractTableModel, we override
	* getRowCount. The row count is the number of Tracks
	* in TrackModel.
	* 
	* @return The number of Tracks in TrackModel.
	* @see javax.swing.table.AbstractTableModel;
	*/
    public int getRowCount() { return trackList.getNumberOfTracks(); }

    // These values populate cells
	/** 
	* 
	* Return value within a cell identified by arguments.
	* @param row Identifies a Track within TrackModel
	* @param col Identifies a field within a Track.
	* @see javax.swing.table.AbstractTableModel;
	*/
    public Object getValueAt(int row, int col) {
	Track track = trackList.getTrack(row);
	switch (col) {
	    case 0: return new Integer(track.getTrackNumber());
	    case 1: return new String(track.getMediaLocator().toString());
	    case 2: return new Double(track.getStartTime());
	    case 3: return new Double(track.getPlayingTime());
	    default: return null;
	}
    }
	/**
	* Set the value in the table in the cell at (<tt>row, column</tt>).
	*	
	* @param aValue	The value to place in the cell.
	* @param row	The row index of the cell.	
	* @param col	The column index of the cell.
	* @see javax.swing.table.AbstractTableModel;
	*/
    public void setValueAt(Object aValue, int row, int col) {
	Track track = trackList.getTrack(row);
	switch (col) {
	    case 2: track.setStartTime(Double.valueOf((String) aValue).doubleValue());
		break;
	    case 3: track.setPlayingTime(Double.valueOf((String) aValue).doubleValue());
		break;
	    default:
		break;
	}
	fireTableCellUpdated(row, col);
    }
    
	/**
	* Return Class stored within a column.
	*
	* @param col A column index.
	* @see javax.swing.table.AbstractTableModel;
	*/
    public Class getColumnClass(int col) {
	return getValueAt(0, col).getClass();
    }

	/** 
	 * Generate output for current Tracks. 
	 * Type of ExtensionFilter determines format of output.
	 * HTMLFileFilter generates a series of PARAM tags.
         * Each PARAM looks like:
         * <PARAM NAME=TRACKDATAn VALUE=medialocator;startTime;playTime >
	 * where n is 0..n-1.
	 * Otherwise, a Mix file is generated.
	*/

    public void write(ExtensionFilter ff, File trackFile) {
	String fileName = trackFile.getAbsolutePath();
	String preamble = "";
	String postamble = "";
	String sep = " ";

	if (ff instanceof HTMLFileFilter) {
	    sep = ";";
	    postamble = ">";
	}
	try {
	    // If there is no extension, add one based
	    // on selected FileFilter
	    if (Utility.getExtension(trackFile) == null) {
	        fileName =  trackFile.getAbsolutePath() + ff.getExtension();
	    }
	    FileOutputStream fos = new FileOutputStream(fileName);
	    PrintWriter pw = new PrintWriter(fos);
	    for (int i = 0; i < getNumberOfTracks(); i++) {
	    	Track track = getTrack(i);
		if (track.isAvailable())
		    continue;
		if (ff instanceof HTMLFileFilter) {
	            preamble = "<PARAM NAME=TRACKDATA" + i + " VALUE=";
		} else {
		    preamble = "";
                }
	 	pw.print(preamble);
		pw.print(track.getMediaLocator().toString() + sep +
		    track.getStartTime() + sep +
		    track.getPlayingTime());
		pw.println(postamble);
	    }
	    pw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

	/**
	* Read contents of a MIX file and assign Tracks.
	* 
	* @param trackFile	A File reference of a MIX file.
	* @return 0 if file is successfully read, -1 otherwise.
	*/
    public int read(File trackFile) {
	Debug.printObject("enter TrackModel.read :  " + trackFile.getName());
	MixFile mixFile = new MixFile(trackFile);

	try {
	    MixFileData v = mixFile.read();

	    // For each entry in MIX file, assign a Track.

	    for (int i = 0; i < v.getNumberOfTracks(); i++) {
	        MixTrackData d = (MixTrackData) v.getMixTrackData(i);
	        assignTrack(d.mediaFileName, 
			d.startTime,
		        d.playingTime);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return -1;
	}
	Debug.printObject("return TrackModel.read :  " + trackFile.getName());
	return 0;
    }

    // TrackModelListener code and posting code
    
    	/** Add a TrackModelListener
     	*
	* @param tml A TrackModelListener
     	*/
     public void addTrackModelListener(TrackModelListener tml) {    
	if (listeners == null)
	    listeners = new EventListenerList();

	listeners.add(TrackModelListener.class, tml);
     }

    	/** Remove TrackModelListener identified by argument
     	*
	* @param tml A TrackModelListener
     	*/
     public void removeTrackModelListener(TrackModelListener tml) {    
	if (listeners != null)
	    listeners.remove(TrackModelListener.class, tml);
     }

	/**
	* Notify all listeners of a Track update.
	*	
	* @param tme	The event the occurred on the Track.
	* 
	*/
    protected void fireTrackModelUpdate(TrackModelEvent tme) {
	Debug.printObject("enter fireTrackModelEvent");
	if (listeners == null) {
	    Debug.printObject("exit fireTrackModelEvent: null listener list");
	    return;
	}
	Object[] l = listeners.getListenerList();
	for (int i = l.length-2; i>=0; i-=2) {
	    if (l[i]== TrackModelListener.class) 
		((TrackModelListener)l[i+1]).trackModelUpdate(tme);
	}
	Debug.printObject("exit fireTrackModelEvent");
    }

	/**
	* Find the index of the Track in the TrackModel
	* associated with the Player passed as an argument.	
	*	
	* @param A javax.media.Player
 	* @return Index in TrackModel of Track associated 
	* with Player.
	*/
   private int findTrack(Player player) 
			throws IllegalArgumentException 
   {
	TrackList tracks = getTrackList();
	return tracks.findTrack(player).getTrackNumber();
   }
}
