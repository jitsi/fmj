package ejmf.toolkit.multiplayer;

import java.awt.Dimension;

import javax.swing.JSlider;

/**
* A visual representation of a Track.
*/

public class TrackSlider extends JSlider {
    private int 		_defaultTrackHeight = 16;
    private int			trackHeight = _defaultTrackHeight;
    private long 		displayBeginTime,
				displayEndTime;

    private Track		track = null;
	
	/**
	* Create a TrackSlider for Track.	
	*
	* @param track Track whose data slider will reflect
	*/
    public TrackSlider(Track track) {
	super(HORIZONTAL, 0, 900, 0);
	this.track = track;
	setEnabled(false);
	setExtent(0);
    }

	/**
	* @return track number of Track slider is displaying.
	*/
    public int getTrackNumber() {
	return track.getTrackNumber();
    }

	/**	
	* Identfiy this UI for UIManager.
	* @return string representation of UI class ID
	*/
    public String getUIClassID() {
	return "TrackSliderUI";
    }

	/**	
	* To simplify GUI so we were able to concentrate
	* on JMF code, the slider is a fixed width.
	* @return fixed dimesion of a TrackSlider
	*/
    public Dimension getPreferredSize() {
	return new Dimension(900, trackHeight);
    }

	/**	
	* This method is over-ridden and rendered a no-op
	* to prevent slider from painting a pointed tip
	* on thumb.
	*/
    public void setPaintTicks(boolean flag) {
	// Need to squash this so that rectangular
	// thumb is drawn...BasicSliderUI wants to
	// draw a pointy thumb is ticks are set. 
    }
}
