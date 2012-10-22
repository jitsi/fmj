package ejmf.toolkit.util;
import java.util.Vector;
/**
 * Encapsulation of data read from a MixFile
 * @see ejmf.toolkit.util.MixFile
 */

public class MixFileData {

    private Vector	vector;

    public MixFileData() {
	vector = new Vector();
    }

	/**
	* Return MixTrackData corresponding to the
 	* <emphasis>i</emphasis>th track.
        *
	* @return An instance of ejmf.toolkit.util.MixTrackData.
        */
    public MixTrackData getMixTrackData(int i) {
	return (MixTrackData) vector.elementAt(i);
    }

	/**
	* Add a new instance of MixTrackData
        */
    public void addTrackData(MixTrackData mtd) {
	vector.addElement(mtd);
    }

	/**
	*  Return number of Tracks reported in the MixFileData
        *  object.
	*/
    public int getNumberOfTracks() {
	return vector.size();
    }
}
