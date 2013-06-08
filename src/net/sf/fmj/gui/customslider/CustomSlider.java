package net.sf.fmj.gui.customslider;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * A slider with a nice look-and-feel.
 *
 * @author Warren Bloomer
 *
 */
public class CustomSlider extends JSlider
{
    /**
     * A listener for state change events on the slider
     */
    private class SliderListener implements ChangeListener
    {
        private void sendValue()
        {
            int newValue = getValue();

            // only send if value has changed
            if (newValue != value)
            {
                value = newValue;
                // TODO send to listeners
            }
        }

        public void stateChanged(ChangeEvent event)
        {
            sendValue();
        }

    }

    private static final long serialVersionUID = 014L;

    /** whether to track the slider */
    // private boolean trackSlider = false;

    private boolean paintFocus = false;

    private int value = -1;

    private HashSet linearListeners;

    /**
     * Constructor
     *
     */
    public CustomSlider()
    {
        initialize();
    }

    /**
     * Returns the Linear listeners.
     *
     * @return the Linear listeners
     */
    private HashSet getLinearListeners()
    {
        if (linearListeners == null)
        {
            linearListeners = new HashSet();
        }
        return linearListeners;
    }

    public boolean getPaintFocus()
    {
        return paintFocus;
    }

    private void initialize()
    {
        this.setUI(CustomSliderUI.createUI(this));
        this.setName("Slider");
        this.setBackground(Color.WHITE);
        this.setOpaque(false);

        this.setPaintTrack(true);
        this.setPaintTicks(false);
        this.setSnapToTicks(true);

        this.addChangeListener(new SliderListener());
    }

    public void setPaintFocus(boolean paintFocus)
    {
        this.paintFocus = paintFocus;
    }
}
