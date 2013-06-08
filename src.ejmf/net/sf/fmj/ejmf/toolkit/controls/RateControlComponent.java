package net.sf.fmj.ejmf.toolkit.controls;

import java.awt.*;
import java.awt.event.*;

import javax.media.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Graphical interface only: Provides a generic Control over a Controller's
 * rate. The graphical interface is a simple rate TextField. When the user hits
 * enter, the rate in the TextField will be set in the Player.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * @author Steve Talley & Rob Gordon
 *
 */
public class RateControlComponent extends JPanel implements ActionListener,
        ControllerListener
{
    /*
     * Convenience thread for execution of GUI code on AWT queue.
     */
    class LoadRateThread implements Runnable
    {
        public void run()
        {
            loadRate();
        }
    }

    private JTextField rateField = new JTextField(6);

    private Controller controller;

    /**
     * Construct a RateControl object for the given Controller.
     */
    public RateControlComponent(Controller controller)
    {
        super();
        this.controller = controller;

        // Set up GUI
        setUpControlComponent();

        // Load rate
        SwingUtilities.invokeLater(new LoadRateThread());

        // Add listeners
        rateField.addActionListener(this);
        controller.addControllerListener(this);
    }

    /**
     * Listens for changes in the Rate TextField.
     *
     * @param e
     *            An ActionEvent fired by activity in text field.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() != rateField)
        {
            return;
        }

        float rate;

        try
        {
            String rateString = rateField.getText();
            rate = Float.valueOf(rateString).floatValue();
            controller.setRate(rate);
        } catch (NumberFormatException ex)
        {
        }

        loadRate();
    }

    /**
     * Listens for changes in the Controller's rate, so that it can be reflected
     * in the Rate TextField.
     *
     * @param e
     *            The generated ControllerEvent. This event is ignored if it is
     *            not a RateChangeEvent.
     */
    public void controllerUpdate(ControllerEvent e)
    {
        if (e.getSourceController() == controller
                && e instanceof RateChangeEvent)
        {
            SwingUtilities.invokeLater(new LoadRateThread());
        }
    }

    /*
     * Isolate GUI manipulation so that it can be executed by invokeLater.
     */
    private void loadRate()
    {
        rateField.setText(Float.toString(controller.getRate()));
    }

    /*
     * setUpControlComponent does GUI work to initialize text field and its
     * border.
     */
    private void setUpControlComponent()
    {
        JLabel rateLabel = new JLabel("Rate:", SwingConstants.RIGHT);
        JPanel mainPanel = new JPanel();
        int GAP = 10;

        Border emptyBorder = new EmptyBorder(GAP, GAP, GAP, GAP);

        Border etchedBorder = new CompoundBorder(new EtchedBorder(),
                emptyBorder);

        Border titledBorder = new TitledBorder(etchedBorder, "Rate Control");

        mainPanel.setBorder(titledBorder);
        mainPanel.setLayout(new BorderLayout(GAP, GAP));
        mainPanel.add(rateLabel, BorderLayout.CENTER);
        mainPanel.add(rateField, BorderLayout.EAST);

        this.add(mainPanel);
    }

}
