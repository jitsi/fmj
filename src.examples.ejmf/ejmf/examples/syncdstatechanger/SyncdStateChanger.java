package ejmf.examples.syncdstatechanger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.media.Controller;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.IncompatibleTimeBaseException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Time;
import javax.media.TransitionEvent;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.util.BorderConstants;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
 * <addtext>
 *
 * @see            <addtext>
 * @version        <addtext>
 * @author         <addtext>
 */
public class SyncdStateChanger extends PlayerDriver
    implements ControllerListener, ActionListener {
    
    /**
     * Keeps track of the current state of the Player
     */
    private int currentState;

    private ImageIcon redBall;
    private ImageIcon greenBall;

    private JRadioButton unrealized;
    private JRadioButton realizing;
    private JRadioButton realized;
    private JRadioButton prefetching;
    private JRadioButton prefetched;
    private JRadioButton started;

    private ButtonGroup buttongroup;

    private JPanel topPanel;
    private JPanel radioPanel;
    private JPanel eventPanel;
        
    private JTextArea eventMonitor;
    
    private Border statesBorder;
    private Border eventBorder;

    private PlayerPanel playerpanel;
    private Player player;

    private Vector slaves = new Vector();
    private Vector slaveMedia = new Vector();

    public SyncdStateChanger(Vector slaveMedia) {
        this.slaveMedia = slaveMedia;
    }

    public static void main(String args[]) {
        Vector slaveMedia = new Vector();

        //  Build the list of slave media
        for(int i = 1; i < args.length; i++) {
            MediaLocator locator
                = Utility.appArgToMediaLocator(args[i]);

            slaveMedia.addElement(locator);
        } 

        main(new SyncdStateChanger(slaveMedia), args);
    }

    public void init() {
        //  Build the list of slave media
        for(int i = 0; ; i++) {
            String param = "SLAVE" + i;
            String media = getParameter(param);
            if(media == null) break;

            MediaLocator locator
                = Utility.appletArgToMediaLocator(this, media);

            slaveMedia.addElement(locator);
        }

        super.init();
    }

    public void begin() {
        playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

        StateWaiter waiter = new StateWaiter(player);
        waiter.blockingRealize();

        // Add ourselves as a listener to the player's events
        player.addControllerListener(this);

        loadSlaves();

        constructGUI();
    }

    private void loadSlaves() {
        for(int i = 0, n = slaveMedia.size(); i < n; i++) {
            try {
                Player slave;

                MediaLocator locator =
                    (MediaLocator)slaveMedia.elementAt(i);

                //  Create the Slave Player
                slave = Manager.createPlayer(locator);

                //  Realize the slave
                new StateWaiter(slave).blockingRealize();

                //  Set the slave's TimeBase
                player.addController(slave);

                // Add ourselves as a listener to the slave's events
                slave.addControllerListener(this);

                // Display the slave's visual component
                Component vis = slave.getVisualComponent();
                if( vis != null ) {
                    JFrame frame = new JFrame("Slave " + i);
                    frame.getContentPane().add(vis);
                    frame.pack();
                    frame.setVisible(true);
                }

                // Add the slave to the list
                slaves.addElement(slave);
            }

            catch(IOException e) {
                System.err.println("Could not connect to media.  Skipping...");
            }

            catch(NoPlayerException e) {
                System.err.println("Player not found for media.  Skipping...");
            }

            catch(IncompatibleTimeBaseException e) {
                System.err.println(
                    "Could not set TimeBase on slave Player.  Skipping..." );
            }
        } 
    }
    
    private void constructGUI() {
        //  Load images
        redBall     = Utility.getImageResource("redBall");
        greenBall = Utility.getImageResource("greenBall");

        //  Construct Components
        topPanel   = new JPanel();
        radioPanel = new JPanel();
        eventPanel = new JPanel();
        eventMonitor = new JTextArea(10,10);
        radioPanel.setLayout( new GridLayout(1,6) );
        eventPanel.setLayout( new BorderLayout() );
        buttongroup = new ButtonGroup();

        //  Set up TextArea
        eventMonitor.setEditable(false);

        setUpRadioButtons();
        setUpBorders();
        setUpListeners();

        radioPanel.add(unrealized);
        radioPanel.add(realizing);
        radioPanel.add(realized);
        radioPanel.add(prefetching);
        radioPanel.add(prefetched);
        radioPanel.add(started);

        JScrollPane scrollPane =
            new JScrollPane(
                eventMonitor,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        eventPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mediaPanel = playerpanel.getMediaPanel();
        mediaPanel.setBorder(null);
        topPanel.add(mediaPanel,BorderLayout.CENTER);

        // Set the current state
        setState();

        playerpanel.removeLoadingLabel();
        playerpanel.setLayout( new BoxLayout(playerpanel,BoxLayout.Y_AXIS) );
        playerpanel.add(topPanel);
        playerpanel.add(radioPanel);
        playerpanel.add(eventPanel);

        final Component vis =
            player.getVisualComponent();

        if( vis != null ) {
            mediaPanel.add(vis);
        }
    }

    private JRadioButton newImageRadioButton(
        String text,
        Icon normal,
        Icon selected,
        boolean isSelected,
        ButtonGroup group) {
        
        JRadioButton b = new JRadioButton(text,normal,isSelected);
        b.setSelectedIcon(selected);
        b.setPressedIcon(normal);
        b.setVerticalTextPosition(SwingConstants.BOTTOM);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        group.add(b);
        return b;
    }

    private void setUpRadioButtons() {
        unrealized  = newImageRadioButton(
            Utility.stateToString(Controller.Unrealized),
                redBall, greenBall, false, buttongroup);
        unrealized.setHorizontalAlignment(JRadioButton.CENTER);

        realizing   = newImageRadioButton(
            Utility.stateToString(Controller.Realizing),
                redBall, greenBall, false, buttongroup);
        realizing.setHorizontalAlignment(JRadioButton.CENTER);

        realized    = newImageRadioButton(
            Utility.stateToString(Controller.Realized),
                redBall, greenBall, false, buttongroup);
        realized.setHorizontalAlignment(JRadioButton.CENTER);

        prefetching = newImageRadioButton(
            Utility.stateToString(Controller.Prefetching),
                redBall, greenBall, false, buttongroup);
        prefetching.setHorizontalAlignment(JRadioButton.CENTER);

        prefetched  = newImageRadioButton(
            Utility.stateToString(Controller.Prefetched),
                redBall, greenBall, false, buttongroup);
        prefetched.setHorizontalAlignment(JRadioButton.CENTER);

        started     = newImageRadioButton(
            Utility.stateToString(Controller.Started),
                redBall, greenBall, false, buttongroup);
        started.setHorizontalAlignment(JRadioButton.CENTER);
    }

    private void setUpListeners() {
        realizing.addActionListener(this);
        realized.addActionListener(this);
        prefetching.addActionListener(this);
        prefetched.addActionListener(this);
        started.addActionListener(this);
    }

    /**
     * Overrides PlayerPanel.setUpBorders() to provide custom borders
     */
    protected void setUpBorders() {
        statesBorder = new TitledBorder(
            BorderConstants.etchedBorder, "Player State" );

        eventBorder  = new TitledBorder(
            BorderConstants.etchedBorder, "Events" );

        playerpanel.mediaBorder.setTitle("Master Player");
        topPanel.setBorder(playerpanel.mediaBorder);
        radioPanel.setBorder(statesBorder);
        eventPanel.setBorder(eventBorder);
        playerpanel.setBorder(BorderConstants.emptyBorder);
    }

    /**
     * This function must be defined in order to implement the
     * ActionListener interface.  Listens for ActionEvents from
     * the player state buttons, and sets the player state
     * accordingly.
     *
     * @param          event
     *                 the ActionEvent
     */
    public void actionPerformed( ActionEvent event ) {
        Object o = event.getSource();

        if( o instanceof JRadioButton ) {
            String userEvent =
                "User requested transition from " +
                    Utility.stateToString(currentState) + " to ";

            if( o == realizing || o == realized ) {
                if(currentState != Controller.Realized) {
                    writeEvent("\n" + userEvent +
                        Utility.stateToString(Controller.Realized) + "\n");
                }

                switch(currentState) {
                    case Controller.Unrealized:
                        player.realize();
                        break;
                    case Controller.Started:
                        player.stop();
                    case Controller.Prefetching:
                    case Controller.Prefetched:
                        player.deallocate();
                        break;
                    default:
                        setState(Controller.Realized);
                }
            } else

            if( o == prefetching || o == prefetched ) {
                if(currentState != Controller.Prefetched) {
                    writeEvent("\n" + userEvent +
                        Utility.stateToString(Controller.Prefetched) + "\n");
                }

                switch(currentState) {
                    case Controller.Unrealized:
                    case Controller.Realizing:
                    case Controller.Realized:
                        player.prefetch();
                        break;
                    case Controller.Started:
                        player.stop();
                        break;
                    default:
                        setState(Controller.Prefetched);
                }
            } else

            if( o == started ) {
                if(currentState != Controller.Started) {
                    writeEvent("\n" + userEvent +
                        Utility.stateToString(Controller.Started) + "\n");
                    player.start();
                }
            }
        }
    }

    public void setState() {
        setState( player.getState() );
    }

    private void setState(int state) {
        switch(state) {
            case Controller.Unrealized:
                unrealized.setSelected(true);
                unrealized.requestFocus();
            break;

            case Controller.Realizing:
                realizing.setSelected(true);
                realizing.requestFocus();
            break;

            case Controller.Realized:
                realized.setSelected(true);
                realized.requestFocus();
            break;

            case Controller.Prefetching:
                prefetching.setSelected(true);
                prefetching.requestFocus();
            break;

            case Controller.Prefetched:
                prefetched.setSelected(true);
                prefetched.requestFocus();
            break;

            case Controller.Started:
                started.setSelected(true);
                started.requestFocus();
            break;

            default:
                return;
        }

        //  Are we moving forward?
        //  Disable inapplicable states
        if(state > currentState) {
            switch(state) {
                case Controller.Started:
                case Controller.Prefetched:  
                    prefetching.setEnabled(false);
                case Controller.Prefetching:  
                case Controller.Realized:    
                    realizing.setEnabled(false);
                case Controller.Realizing:   
                    unrealized.setEnabled(false);
            }
        } else

        //  Or are we moving backward?
        //  Re-enable newly-applicable states
        if(state < currentState) {
            switch(state) {
                case Controller.Realized:    
                    prefetching.setEnabled(true);
            }
        }

        currentState = state;
    }

    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event.
     *
     * @param       event
     *              the ControllerEvent
     */
    public void controllerUpdate(final ControllerEvent event) {
        
        final Controller source = event.getSourceController();
        final boolean isSlave = slaves.contains(source);

        if( event instanceof TransitionEvent ||
            event instanceof ControllerErrorEvent )
        {
            Runnable r = new Runnable() {
                public void run() {
                    if(isSlave) {
                        int index = slaves.indexOf(source);
                        writeEvent("[slave" + index + "] " + event.toString());
                    } else {
                        setState();
                        writeEvent("[master] " + event.toString());
                    }
                }
            };

            SwingUtilities.invokeLater(r);
        }

        //  Return if this is a slave
        if(isSlave) return;

        if(event instanceof EndOfMediaEvent) {
            // End of the media -- rewind
            player.setMediaTime(new Time(0));
        }
    }

    /**
     * Post an event to the event monitor text area
     *
     * @param          event
     *                 the String to post
     */
    public void writeEvent(String event) {
        eventMonitor.append(event + "\n");
    }
}
