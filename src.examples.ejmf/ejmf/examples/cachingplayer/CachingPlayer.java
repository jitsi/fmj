package ejmf.examples.cachingplayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.media.CachingControl;
import javax.media.CachingControlEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.util.BorderConstants;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;

/**
 * This example provides support for a Player's CachingControl.
 * When a CachingControlEvent is posted by the Player, the
 * CachingPlayer gets both the progress bar Component and the
 * progress control Component and displays them in a JFrame.
 *
 * @see            ejmf.toolkit.util.PlayerDriver
 * @see            ejmf.toolkit.genericplayer.GenericPlayer
 */
public class CachingPlayer extends PlayerDriver
    implements ControllerListener
{
    private static final String LENGTHMESSAGE = "Media Length:";
    private static final String RECVDMESSAGE = "Bytes Received:";

    private PlayerPanel playerpanel;
    private Player player;
    private JFrame cacheMonitor;
    private Component progressBar;
    private Component progressControl;
    private JLabel received;

    /**
     * Starts the media player
     */
    public static void main(String args[]) {
        main(new CachingPlayer(), args);
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        // Start Player
        player.start();
    }
    
    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event.
     *
     * @param          event
     *                 the media event
     */
    public void controllerUpdate(ControllerEvent event) {
        if( event instanceof CachingControlEvent ) {

            CachingControlEvent cEvent =
                (CachingControlEvent)event;

            final CachingControl cache =
                cEvent.getCachingControl();

            Runnable r = new Runnable() {
                public void run() {
                    updateCacheMonitor(cache);
                }
            };

            SwingUtilities.invokeLater(r);
        } else

        if( event instanceof PrefetchCompleteEvent ) {
            Runnable r = new Runnable() {
                public void run() {
                    if( progressControl != null ) {
                        progressControl.setEnabled(false);
                    }
                }
            };

            SwingUtilities.invokeLater(r);
        } else

        if( event instanceof RealizeCompleteEvent ) {

            Runnable r = new Runnable() {
                public void run() {
                    // Add Control Panel Component
                    playerpanel.addControlComponent();

                    // Add Visual Component
                    playerpanel.addVisualComponent();
                }
            };

            SwingUtilities.invokeLater(r);
        } else

        if( event instanceof EndOfMediaEvent ) {
            // End of the media -- rewind
            if( player.getRate() < 0 ) {
                player.setMediaTime( player.getDuration() );
            } else {
                player.setMediaTime(new Time(0));
            }
        }
    }

    public synchronized void updateCacheMonitor(CachingControl cache) {
        if( cacheMonitor == null ) {
            // Set up Caching cacheMonitor
            cacheMonitor = new JFrame("Caching Controls");
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout( new BorderLayout(10,10) );

            progressBar = cache.getProgressBarComponent();
            progressControl = cache.getControlComponent();

            if( progressBar != null ) {
                JPanel panel = new JPanel();
                panel.setBorder(
                    new TitledBorder(
                        BorderConstants.etchedBorder, "Progress Bar" ) );
                panel.add(progressBar);
                mainPanel.add(panel, BorderLayout.SOUTH);
            }

            if( progressControl != null ) {
                JPanel panel = new JPanel();
                panel.setBorder(
                    new TitledBorder(
                        BorderConstants.etchedBorder, "Progress Control" ) );
                panel.add(progressControl);
                mainPanel.add(panel, BorderLayout.NORTH);
            }

            JPanel panel = new JPanel();
            panel.setLayout(
                new GridLayout(2,2,10,10) );

            received = new JLabel();

            JLabel lengthLabel = new JLabel();
            long length = cache.getContentLength();

            if( length == CachingControl.LENGTH_UNKNOWN ) {
                lengthLabel.setText("Unknown");
            } else {
                lengthLabel.setText( "" + length );
            }

            JLabel mediaLength =
                new JLabel(LENGTHMESSAGE, JLabel.RIGHT);
            JLabel receivLabel =
                new JLabel(RECVDMESSAGE, JLabel.RIGHT);

            panel.add(mediaLength);
            panel.add(lengthLabel);
            panel.add(receivLabel);
            panel.add(received);
            panel.setBorder(
                new TitledBorder(
                    BorderConstants.etchedBorder, "Download Counter" ) );

            mainPanel.add(panel, BorderLayout.CENTER);
            mainPanel.setBorder(
                BorderConstants.emptyBorder);

            Container c = cacheMonitor.getContentPane();
            c.add(mainPanel);

            cacheMonitor.pack();
            cacheMonitor.setVisible(true);
        }

        received.setText(
            "" + cache.getContentProgress() );
    }
}
