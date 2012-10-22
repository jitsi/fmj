package ejmf.toolkit.controls;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.RateChangeEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Provides a generic Control over a Controller's rate.  The
 * graphical interface is a simple rate TextField.  When the user
 * hits enter, the rate in the TextField will be set in the
 * Player.
 *
 */
public class RateControl
    implements ActionListener, ControllerListener, Control
{
    private JPanel controlComponent = new JPanel();
    private JTextField rateField = new JTextField(6);
    private Controller controller;

    /**
     * Construct a RateControl object for the given Controller.
     */
    public RateControl(Controller controller) {
        super();
        this.controller = controller;

        //  Set up GUI
        setUpControlComponent();

        //  Load rate
	SwingUtilities.invokeLater(new LoadRateThread());

        //  Add listeners
        rateField.addActionListener(this);
        controller.addControllerListener(this);
    }

	/* setUpControlComponent does GUI work to
	* initialize text field and its border.
	*/
    private void setUpControlComponent() {
        JLabel rateLabel = new JLabel("Rate:", JLabel.RIGHT);
        JPanel mainPanel = new JPanel();
        int GAP = 10;

        Border emptyBorder  = new EmptyBorder(GAP,GAP,GAP,GAP);

        Border etchedBorder = new CompoundBorder(
            new EtchedBorder(), emptyBorder);

        Border titledBorder = new TitledBorder(
            etchedBorder, "Rate Control");

        mainPanel.setBorder(titledBorder);
        mainPanel.setLayout( new BorderLayout(GAP, GAP) );
        mainPanel.add(rateLabel, BorderLayout.CENTER);
        mainPanel.add(rateField, BorderLayout.EAST);

        controlComponent.add(mainPanel);
    }

    	/**
     	* Listens for changes in the Rate TextField.
	* @param e An ActionEvent fired by activity
	* in text field.
     	*/
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() != rateField ) {
            return;
        }

        float rate;

        try {
            String rateString = rateField.getText();
            rate = Float.valueOf(rateString).floatValue();
            controller.setRate(rate);
        } catch(NumberFormatException ex) {}

	loadRate();
    }

    /**
     * Listens for changes in the Controller's rate, so that it
     * can be reflected in the Rate TextField.
     *
     * @param      e
     *             The generated ControllerEvent.  This event is
     *             ignored if it is not a RateChangeEvent.
     */
    public void controllerUpdate(ControllerEvent e) {
        if( e.getSourceController() == controller &&
            e instanceof RateChangeEvent)
        {
	    SwingUtilities.invokeLater(new LoadRateThread());
        }
    }

	/*
	* Convenience thread for execution of GUI code
	* on AWT queue.	
	*/
    class LoadRateThread implements Runnable {
	public void run() {
            loadRate();
	}
    }

	/* Isolate GUI manipulation so that it can be
	* executed by invokeLater.
	*/
    private void loadRate() {
        rateField.setText( Float.toString( controller.getRate() ) );
    }

    /**
     * For implementation of the Control interface.
     *
     * @return     the Control Component for this object.
     */
    public Component getControlComponent() {
        return controlComponent;
    }
}
