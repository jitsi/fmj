package ejmf.toolkit.gui.tickertape;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * TickerTapeApplet is a simple applet to display a TickerTape.
 * Use the MESSAGE html tag to specify the message to be
 * displayed.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class TickerTapeApplet extends JApplet {
    TickerTape tickertape;
    JFrame frame;

    public TickerTapeApplet() {
        //  Fix AWT Event Queue check
        getRootPane().putClientProperty(
            "defeatSystemEventQueueCheck", Boolean.TRUE);
    }

    public void init() {
        String message;

        // Get the text to display
        if((message = getParameter("MESSAGE")) == null) {
            System.err.println("Invalid MESSAGE parameter");
            return;
        }

        frame = new JFrame("TickerTape");
        tickertape = new TickerTape(message);
        frame.getContentPane().add(tickertape);
        frame.pack();
    }

    public void start() {
        frame.setVisible(true);
        tickertape.start();
    }

    public void stop() {
        frame.setVisible(false);
        tickertape.stop();
    }

    public void destroy() {
        frame.dispose();
        tickertape = null;
    }
}
