package ejmf.toolkit.multiplayer;

import java.util.Vector;

import javax.media.MediaLocator;
import javax.media.Player;

import ejmf.toolkit.util.MixFileData;
import ejmf.toolkit.util.MixTrackData;

/** 
 *  A type-safe vector of Track elements
 *
*/

public class TrackList extends Vector {

    private Vector	vector;

	/**
	* Create a TrackList with a given capacity.
	* 
	* @param numberOfTrack the number of Tracks in this TrackList
	*/
    public TrackList(int numberOfTracks) {
	vector = new Vector(numberOfTracks);
    }

	/**	
	* Reports Track at index <tt>i</tt> in TrackList.	
	* 
	* @return a Track
	*/
    public Track getTrack(int i) {
	return (Track) vector.elementAt(i);
    }

	/**	
	* Add Track to TrackList
	* @param track a Track
	*/
    public void addTrack(Track track) {
	vector.addElement(track);
    }

	/**
	* Report number of Tracks
	* @return number of Tracks
	*/
    public int getNumberOfTracks() {
	return vector.size();
    }

    /**
      * Given a Player find a the Track is occupies.
      *
      * @param player A java.media.Player
      * @return A Track associated with Player passed as argument.
      */
    public Track findTrack(Player player) {
	for (int i = 0; i < getNumberOfTracks(); i++) {
	    Track track = getTrack(i);
	    if (track.isAssigned() && 
	        player == track.getPlayer()) 
	    {
	 	return track;
   	    }
	}
	throw new IllegalArgumentException("No such Player");
    }

    /**
     *  Takes an array of strings and creates a TrackList.
     *  The strings are of the form:
     *  mediaLocator;startTime;playingTime
     *  <p>
     *  For example:
     *  D:\ejmf\classes\media\kickbutt.wav;0.3;15.945
     * <p>
     * This information corresponds to the data maintained
     * by a Track. 
     * @param rawmix A String[], each element formatted in MIX
     * file format.
     */
    public static TrackList parseTrackData(String[] rawmix) {
         TrackList trackList = new TrackList(rawmix.length);
         for (int i = 0; i < rawmix.length; i++) {
             String s = rawmix[i];
             int ix = s.indexOf(';');
             String mls = s.substring(0, ix);
             s = s.substring(ix+1);
             ix = s.indexOf(';');
             String startTime = s.substring(0, ix);
             String playingTime = s.substring(ix+1);

             try {
		Track track = Track.createTrack(i, 
				new MediaLocator(mls), 
				Double.valueOf(startTime).doubleValue(),
				Double.valueOf(playingTime).doubleValue());
                trackList.addTrack(track);
             } catch (Exception e) {
		 e.printStackTrace();
                 System.err.println("Can't create Player for " + mls);
                 continue;
             }
        }
        trackList.trimToSize();
        return trackList;
    } 

	/**
	* Create a TrackList from a MixFileData object.
	* @param mfd An ejmf.toolkit.util.MixFileData reference
	* @return a TrackLlist
	*/
    public static TrackList parseMixFileData(MixFileData mfd) {
	int n = mfd.getNumberOfTracks();
        TrackList trackList = new TrackList(n);
	for (int i = 0; i < n; i++) {
	    MixTrackData mtd = mfd.getMixTrackData(i);
            try {
		Track track = Track.createTrack(i, 
				new MediaLocator(mtd.mediaFileName), 
				mtd.startTime,
				mtd.playingTime);
                trackList.addTrack(track);
            } catch (Exception e) {
		e.printStackTrace();
                System.err.println(
		    "Can't create Player for " + mtd.mediaFileName);
                continue;
            }
    	}
        trackList.trimToSize();
        return trackList;
    }
}
