package ejmf.toolkit.gui.controlpanel;


import java.awt.Component;
import java.awt.FlowLayout;

import javax.media.Player;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ejmf.toolkit.controls.AbstractListenerControl;

/**
* Root class form which custom Control Panels should
* extends. AbstractControlPanel provides layout
* a collection Controls provided by AbstractControls
* reference.
* <p>
* Subclasses must supply definitions of the following methods:
* <ul>
* <li>void addComponents(int flags);
* <li>AbstractControls createControls(Player player);
* </ul>
*/

public abstract class AbstractControlPanel extends JPanel {

    /**
     *  Value for start button control.
     */
    public final static int USE_START_CONTROL = 	1;
    /**
     *  Value for stop button control.
     */
    public final static int USE_STOP_CONTROL = 	2;
    /**
     * Value for reverse button control.
     */
    public final static int USE_REVERSE_CONTROL = 	4;
    /**
     * Value for pause button control.
     */
    public final static int USE_PAUSE_CONTROL = 	8;
    /**
     * Value for volume control.
     */
    public final static int USE_GAIN_CONTROL = 	16;
    /**
     * Value for fast forward control
     */
    public final static int USE_FF_CONTROL =		32;
    /**
     * Value for progress bar control.
     */
    public final static int USE_PROGRESS_CONTROL = 	64;

    /**
      * Value for volume meter/mute control
      */
    public final static int USE_GAINMETER_CONTROL = 128;

    private AbstractControls	  controls;	
    private Player                player;

	/** 
	* Create a Control panel with those Controls identified 
        * by <tt>flags</tt> argument associated with 
        * <tt>player</tt>.
	* @param player Playera with which control panel is associated.
	* @param flags OR'd value of USE values that determine which
	* control buttons are displayed.
  	*/
    protected AbstractControlPanel(Player player, int flags) {
	setControlPanelLayout();
    	setControlPanelBorder();
	this.player = player;
	controls = createControls(player);
	addComponents(flags);
    }

	/**	
	* Create a Control Panel with complete suite of Controls	
	* @param player Playera with which control panel is associated.
	*/
    protected AbstractControlPanel(Player player) {
	this(player, 0xffffffff);
    }

	/**
	* Set border of control panel. Subclasses should override
	* this method if they prefer a border different than
	* EtchedBorder.
	*/

    protected void setControlPanelBorder() {
	setBorder(BorderFactory.createEtchedBorder());
    }


	/** 
	* Set layout for control panel.
        * Subclasses override this method to
  	* change layout.
        * <p>
	* Default layout is FlowLayout. 
 	*/
    protected void setControlPanelLayout() {
	FlowLayout flow = new FlowLayout();
	flow.setHgap(5);
	setLayout(flow);
    }

	/**
	* Add appropriate Components to Control Panel.
	* @param flags Is the bit-wise OR of some number	
	* of Control identifiers, e.g. USE_START_CONTROL.
	* @param flags OR'd value of USE values that determine which
	* control buttons are displayed.
	*/
    protected abstract void addComponents(int flags);
	/**
	* Create the Controls to be displayed in the Control Panel.	
	* @param player Playera with which control panel is associated.
	*/
    protected abstract AbstractControls createControls(Player player);

	/**
	* Remove all control components from the Control panel
	* container.
	*/
    protected void removeControlComponents() {
	int count = getComponentCount();
	for (int i = 0; i <  count; i++) {
	    remove(i);
	}
    }

	/**
	* Remove the control component identified by <tt>c</tt> and
	* replace it with <tt>withComponent</tt>.
	* <p>
	* If default layout is changed by a subclass, this method may need to
	* be overridden.
	* @param c	Existing component
	* @param withComponent New component to be installed in control panel.
	*/
	
    protected void replaceControlComponent(Component c, Component withComponent)
    {
	int count = getComponentCount();
	for (int i = 0; i <  count; i++) {
	    if (c == getComponent(i)) {
		replaceControlComponent(withComponent, i);
		break;
            }
 	}
    }

	/**
	* Remove the control component at location <tt>atIndex</tt>
	* with the component named by <tt>withComponent</tt>.
	* <p>
	* If default layout is changed by a subclass, this method may need to
	* be overridden.
	* @param withComponent New component to be installed in control panel.
	* @param atIndex Position of component to replace.
	*/
    protected void replaceControlComponent(
				Component withComponent,
				int atIndex) {
	remove(atIndex);
	add(withComponent, atIndex);
    }

	/** 
	* Return Control with given name.
	*/
    public AbstractListenerControl getControl(String name) {
	return controls.getControl(name);
    }

	/**
	*  Return AbstractControls managed by this AbstractControlPanel.		*/	
    protected AbstractControls getControls() {
	return controls;
    }
}
