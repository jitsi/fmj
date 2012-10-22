package ejmf.toolkit.multiplayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ejmf.toolkit.gui.TickPanel;
import ejmf.toolkit.util.Debug;

/**
 * A TrackPanel object is a view onto a TrackModel.
 * It displays each Track in the model as a TrackSlider.
 * <p>
 * TrackPanel listens to the TrackModel and each of
 * the TrackSliders.
 */
public class TrackPanel extends JPanel 
	implements TrackModelListener, ChangeListener {

    private TrackPane	trackPane;
    private TrackModel	trackModel;

	/**
	* Create TrackPanel from a TrackModel.
	* A TrackSlider is created for each Track and
	* and adds itself as a listener on the TrackSlider.
	*
	* @param trackModel whose Tracks data will be displayed
	* by the TrackSliders.
	*/
    public TrackPanel(TrackModel trackModel) {
	this.trackModel = trackModel;
	TrackList trackList = trackModel.getTrackList();

	for (int i = 0; i < trackList.getNumberOfTracks(); i++) {
	    Track track = trackList.getTrack(i);
	    TrackSlider slider = new TrackSlider(track);
	    track.setTrackSlider(slider);
	    slider.addChangeListener(this);
	}
	setLayout(new BorderLayout());
	setBorder(new EmptyBorder(22, 0, 5, 0));
	add(trackPane = new TrackPane(trackList), BorderLayout.CENTER);
	trackModel.addTrackModelListener(this);
    }

    public Dimension getPreferredSize() {
	Dimension d = trackPane.getPreferredSize();
	Insets insets = getInsets();
        return new Dimension(d.width + insets.left + insets.right,
        		     d.height + insets.top + insets.bottom);
    }

    public Dimension getMaximumSize() {
	return getPreferredSize();
    }

	/** 
	* This method is called in response to changes
	* in TrackModel.
	* @param tme A TrackModelEvent describing update
        */
    public void trackModelUpdate(TrackModelEvent tme) {
	Debug.printObject("TrackPanel:trackModelUpdate: " + 
				tme.getClass().getName());
	TrackModel trackModel = tme.getTrackModel();
	TrackList trackList = trackModel.getTrackList();
	Track track = trackList.getTrack(tme.getIndex());
	TrackSlider trackSlider = track.getTrackSlider();

	Debug.printStack(new Exception());
	    if (tme instanceof TrackModelSetStartTimeEvent) {
	        TrackModelSetStartTimeEvent e = 	
		    (TrackModelSetStartTimeEvent) tme;
		int newVal = (int)e.getValue();
		if (newVal == trackSlider.getValue()) {
		    return;
		}
	        trackSlider.setValue(newVal);
	    } 
	    else if (tme instanceof TrackModelSetPlayingTimeEvent) {
	        TrackModelSetPlayingTimeEvent e = 	
		    (TrackModelSetPlayingTimeEvent) tme;
		int newExtent = (int)e.getValue();
		if (newExtent == trackSlider.getExtent()) {
		    return;
		}
	        trackSlider.setExtent(newExtent);
	    }
    }

    class UpdateTrackView implements Runnable {
	TrackSlider 		slider;
	TrackModelEvent		tme;

	public UpdateTrackView(TrackSlider slider, TrackModelEvent tme) {
	    this.slider = slider;
	    this.tme = tme;
	}

 	public void run() {
	    Debug.printObject("UpdateTrackView:run tme = " +
			tme.getClass().getName());
	    
	    if (tme instanceof TrackModelSetStartTimeEvent) {
	        TrackModelSetStartTimeEvent e = 	
		    (TrackModelSetStartTimeEvent) tme;
	        slider.setValue((int)e.getValue());
	    } 
	    else if (tme instanceof TrackModelSetPlayingTimeEvent) {
	        TrackModelSetPlayingTimeEvent e = 	
		    (TrackModelSetPlayingTimeEvent) tme;
	        slider.setExtent((int)e.getValue());
	    }
	    Debug.printObject("exit UpdateTrackView:run" );
	}
    }

    /**
     *  Respond to changes in TrackSliders
     *  and update TrackModel accordingly.
	* @param e A ChangeEvent originating from one of TrackPanel's
	* TrackSliders.
     */
    public void stateChanged(ChangeEvent e) {
	TrackSlider track = (TrackSlider) e.getSource();
	trackModel.setStartTime(track.getTrackNumber(), 
		(double) track.getValue());
    }
}
    
/*
* Pane in which TrackSliders are displayed.
*/
class TrackPane extends JScrollPane {

    private TrackList 		trackList = null;
    private JPanel 		panel;
    private GridLayout		gl;

    public TrackPane(TrackList trackList) {
	this.trackList = trackList;
	TrackView trackView = new TrackView(this, trackList);
	setViewportView(trackView);
	setHorizontalScrollBarPolicy(
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	setVerticalScrollBarPolicy(
		ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    }

    public Dimension getPreferredSize() {
	return new Dimension(300, super.getPreferredSize().height);
    }

    public Dimension getMaximumSize() {
	return getPreferredSize();
    }
}

/*
* Viewport for TrackPane
*/
class TrackView extends JViewport {
    private JPanel 		panel;
    private TrackList 		trackList;
    private GridLayout		gl;

    private TrackSlider		t0;		// A representative slider
    private TrackPane		trackPane;
    
    public TrackView(TrackPane trackPane, TrackList trackList) {
	this.trackList = trackList;
	this.trackPane = trackPane;
	JPanel panel = new JPanel();
	panel.setBorder(new EmptyBorder(0, 0, 0, 0));
	panel.setOpaque(false);
	panel.setLayout(gl = 
		new GridLayout(trackList.getNumberOfTracks() + 1, 1));
	gl.setVgap(1);
 
 	t0 = trackList.getTrack(0).getTrackSlider();
	
        panel.add(new TickPanel(t0.getMinimum(), 
				t0.getMaximum(), 10, t0.getPreferredSize()));

	for (int i = 0; i < trackList.getNumberOfTracks(); i++) {
	    Track track = trackList.getTrack(i);
	    TrackSlider trackSlider = track.getTrackSlider();
	    panel.add(trackSlider);
        }
        setView(panel);
    }

    public Dimension getExtentSize() {
	return trackPane.getPreferredSize();
    }
}

