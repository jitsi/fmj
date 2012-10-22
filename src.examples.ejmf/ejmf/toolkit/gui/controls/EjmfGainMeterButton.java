package ejmf.toolkit.gui.controls;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import ejmf.toolkit.util.Utility;

/**
* An EjmfGainMeterButton conveys two pieces of information.
* First, it can on or off to reflect the mute state of its
* Control. This is supported with setMute method.
* <p>
* Second, it can support multiple level displays with the setLevel method.
* The level is float value that the EjmfGainMeterButton maps into 
* display state. Specifically, it displays some number of "waves" 
* emanating from a speaker. The number of waves indicates the gain level.
* <p>
* EjmfGainMeterButton relies of $EJMF_HOME/classes/lib/ejmf.properties
* for its icon images.
* 
*/
public class EjmfGainMeterButton extends EjmfControlButton
		implements GainMeter 	
{
    private static final int N_LEVELS	= 6;

    private ImageIcon[] speakerIcons;
    private ImageIcon[] mutedSpeakerIcons;
    private ImageIcon[] pressedSpeakerIcons;
    private ImageIcon[] rolloverSpeakerIcons;

	/**	
	* Create a EjmfGainMeterButton for EJMF control panel.
	*/
    public EjmfGainMeterButton() {
	speakerIcons = new ImageIcon[N_LEVELS];
	mutedSpeakerIcons = new ImageIcon[N_LEVELS];
	pressedSpeakerIcons = new ImageIcon[N_LEVELS];
	rolloverSpeakerIcons = new ImageIcon[N_LEVELS];

	for (int i = 0; i < speakerIcons.length; i++) { 
	    String istr = (new Integer(i)).toString();
	    speakerIcons[i] = Utility.getImageResource(
				"vol" + istr + "_image");
	    mutedSpeakerIcons[i] = Utility.getImageResource(
				"muted" + istr + "_image");
	    rolloverSpeakerIcons[i] = Utility.getImageResource(
				"rolloverSpeaker" + istr + "_image");
	    pressedSpeakerIcons[i] = Utility.getImageResource(
				"pressedSpeaker" + istr + "_image");
	}
	setIcon(speakerIcons[0]);
	setEnabled(true);

	updateView();
    }

	/**
	* Create a gain meter with initial values.
	*
	* @param level initial gain level
	* @param muted initial muted state
   	*/
    public EjmfGainMeterButton(float level, boolean muted) {
	this();
	setLevel(level);
        setMute(muted);
    }

    private int intLevel;
    private boolean muted;

	/** Set the display level. The input argument is a gain level	
	* value. It is mapped to a supported integer value using
	* <tt>mapToMeterLevel</tt>.
	*
	* @param level A gain level value that is mapped to a
	* corresponding integer value supported by the gain meter.
	*/
    public void setLevel(float level) {
	intLevel = mapToMeterLevel(level);
	updateView();
    }

	/** Set the muted state.
	* @param if <tt>muted</tt> is true, button is displayed	
	* in muted state. Otherwise, button is displayed in normal
	* state.
	*/
    public void setMute(boolean muted) {
	this.muted = muted;      
	updateView();
    }

	/**
	* Convert gain level to some internally
	* sensible value.
	* @param level a gain level return by, e.g. GainControl.getLevel.
	* @return An integer value that corresponds to a displayable	
	* state of the gain meter.
	*/
    public int mapToMeterLevel(float level) {
        if (level == 1.0f)
	    level = 0.99f; // Avoid array index out of bounds
        return (int)(level * speakerIcons.length);
    }

	/**
	* Force a redraw of the gain meter button.
	*/
    public void updateView() {
	if (muted) {
	    setIcon(mutedSpeakerIcons[intLevel]);
	} else { 
	    setIcon(speakerIcons[intLevel]);
	}
	setRolloverIcon(rolloverSpeakerIcons[intLevel]);
	setPressedIcon(pressedSpeakerIcons[intLevel]);
    }

	/**
	* Ensure the button will fit largest speaker icon.
	* @return The size of gain component.
	*/
    public Dimension getPreferredSize() {
	return new Dimension(
		mutedSpeakerIcons[N_LEVELS-1].getIconWidth()+4,
		mutedSpeakerIcons[N_LEVELS-1].getIconHeight()+4);
    }
}
