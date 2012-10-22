package jamp;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.net.URL;

import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.GainControl;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;

public class MainWindow extends Window implements MouseMotionListener,
    MouseListener, ControllerListener, Runnable {

    private int WIDTH = 320;
    private int HEIGHT = 24;
    protected String movieURL = null;
    protected Window videoPanel = null;
    protected Player player = null;
    protected boolean closing = false;
    protected Image image;
    protected Frame frame;
    protected boolean notOutside = true;
    protected GainControl gain = null;
    protected boolean inVolume = false;
    protected boolean inTime = false;
    protected Thread timeThread = null;
    protected Image doubleBuffer = null;
    protected Graphics dg = null;
    protected int timex = -1;
    protected String lastDir = null;
    
    private Point startPoint;
    private Point startLocation;

    public MainWindow(Frame frame, String [] args) {
        super(frame);
	this.frame = frame;
        parseArgs(args);
        createGUI();
        setBounds(200, 400, WIDTH, HEIGHT);
        setVisible(true);
        addNotify();
	// Load the command line movie, if any
        if (movieURL != null)
            loadMovie(movieURL);
	// Create the slider update thread
	timeThread = new Thread(this);
	timeThread.start();
    }

    /**
     * Parse the command line arguments.
     * Currently only a single movie URL is supported
     */ 
    private void parseArgs(String [] args) {
        if (args.length > 0)
            movieURL = args[0];
    }

    /**
     * Create a player for the URL and realize it.
     */
    private void loadMovie(String movieURL) {
	// Prepend a "file:" if no protocol is specified
	if (movieURL.indexOf(":") < 3)
	    movieURL = "file:" + movieURL;
	// Try to create a player
        try {
	    player = Manager.createPlayer(new MediaLocator(movieURL));
	    player.addControllerListener(this);
	    player.realize();
        } catch (Exception e) {
            System.out.println("Error creating player");
            return;
        }
    }

    private void centerVideoPanel() {
	Point controlLocation = getLocation();
	int xPos = (WIDTH - videoPanel.getSize().width) / 2;
	controlLocation.translate(xPos, - 5 - videoPanel.getSize().height);
	videoPanel.setLocation(controlLocation);
    }

    public void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof RealizeCompleteEvent) {
	    if (videoPanel == null)
		videoPanel = new Window(frame);
	    else
		videoPanel.removeAll();
	    // Get the player's visual component, if any, and put it in a Window above
	    // the control panel.
            Component vis = player.getVisualComponent();
            if (vis != null) {
                videoPanel.add(vis);
		videoPanel.setSize(vis.getPreferredSize());
		centerVideoPanel();
		videoPanel.setVisible(true);
	    }
	    // Start the player
            player.start();
        } else if (ce instanceof ControllerClosedEvent) {
            if (closing)
                System.exit(0);
            else {
		timex = -1;
		gain = null;
		// Get rid of the visual component
		if (videoPanel != null) {
		    videoPanel.dispose();
		    videoPanel = null;
		}
            }
	    player = null;
        } else if (ce instanceof EndOfMediaEvent) {
	    rewind();
	    play();
	} else if (ce instanceof PrefetchCompleteEvent) {
	    // Get the GainControl from the player, if any, to control sound volume
	    gain = (GainControl) player.getControl("javax.media.GainControl");
	    repaint();
	}
    }

    // Stop and close the player
    private void close() {
        if (player != null) {
            player.stop();
            player.close();
        }
    }

    // Start the player
    private void play() {
	if (player != null)
	    player.start();
    }

    // Stop the player
    private void pause() {
	if (player != null)
	    player.stop();
    }

    // Seek back to zero
    private void rewind() {
	if (player != null) {
	    player.setMediaTime(new Time(0));
	    if (player.getTargetState() < Player.Started)
		player.prefetch();
	}
    }

    // Close the player and open a new file
    private void eject() {
	Thread cl = new EjectThread();
	cl.start();
    }

    public void doEject() {
	FileDialog fd;
	close();
	// Block till we receive ControllerClosedEvent
	while (player != null) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException ie) {
	    }
	}
	fd = new FileDialog(frame, "Open File", FileDialog.LOAD);
	if (lastDir != null)
	    fd.setDirectory(lastDir);
	fd.show();
	lastDir = fd.getDirectory();
	String filename = fd.getFile();
	if (filename == null)
	    return;
	else {
	    loadMovie(lastDir + filename);
	}
	fd.dispose();
    }

    private void volumeDragged(int x) {
	if (gain == null)
	    return;
	// Determine volume level from mouse position in the control panel
	float level = (x - COMPONENTS[VOLUME][0]) / (float) GAINWIDTH;
	if (level < 0f) level = 0f;
	if (level > 1f) level = 1f;
	// Set the volume level through the GainControl from the player
	gain.setLevel(level);
	// Update the GUI
	repaint();
    }

    private void timeDragged(int x) {
	if (player == null)
	    return;
	// Determine the requested media time based on duration of the file
	// and position of the mouse in the control panel.
	long dura = player.getDuration().getNanoseconds();
	if (dura < 0 || dura > 3 * 3600 * 1000000000L)
	    return;
	long nano = (long) ((float) (x - COMPONENTS[MEDIATIME][0]) /
			    (COMPONENTS[MEDIATIME][2] -
			     COMPONENTS[MEDIATIME][0] + 1) * dura);
	if (nano < 0) nano = 0;
	if (nano > dura) nano = dura;
	// Set the media time
	player.setMediaTime(new Time(nano));
	if (player.getTargetState() < Player.Started)
	    player.prefetch();
	repaint();
    }

    private void maximize() {
	// Anyone wants to implement this?
	System.err.println("Maximize Not Implemented!");
    }

    /****************************************************************
     * Handle mouse clicks and drags in the control panel
     ****************************************************************/
    
    public void mouseDragged(MouseEvent parm1) {
	if (notOutside) {
	    if (inVolume)
		volumeDragged(parm1.getPoint().x);
	    else if (inTime)
		timeDragged(parm1.getPoint().x);
	    return;
	}
	// Drag the panel around
        startLocation = getLocation();
        setLocation(startLocation.x - startPoint.x + parm1.getPoint().x,
                    startLocation.y - startPoint.y + parm1.getPoint().y);
	if (videoPanel != null) {
	    centerVideoPanel();
	}	    
        Toolkit.getDefaultToolkit().sync();
        repaint();
    }

    public void mousePressed(MouseEvent parm1) {
        toFront();
        startPoint = parm1.getPoint();
	notOutside = false;
	inVolume = false;
	inTime = false;
	
	for (int i = 0; i < COMPONENTS.length; i++) {
	    if (startPoint.x >= COMPONENTS[i][0] &&
		startPoint.y >= COMPONENTS[i][1] &&
		startPoint.x <= COMPONENTS[i][2] &&
		startPoint.y <= COMPONENTS[i][3] ) {

		switch (i) {
		case PLAY:
		    play();
		    break;
		case PAUSE:
		    pause();
		    break;
		case REWIND:
		    rewind();
		    break;
		case EJECT:
		    eject();
		    break;
		case VOLUME:
		    inVolume = true;
		    volumeDragged(startPoint.x);
		    break;
		case MEDIATIME:
		    inTime = true;
		    timeDragged(startPoint.x);
		    break;
		case CLOSE:
		    closing = true;
		    close();
		    if (player == null)
			System.exit(0);
		    break;
		case MAXIMIZE:
		    maximize();
		}
		notOutside = true;
		break;
	    }
	}
    }

    /**
     * The media time slider update thread
     */
    public void run() {
	while (true) {
	    if (player != null) {
		long nano = player.getMediaTime().getNanoseconds();
		long dura = player.getDuration().getNanoseconds();
		if (dura >= 0 && dura < (long) 3 * 3600 * 1000000000L) {
		    
		    timex = (int) (((float) nano / dura) *
				   (COMPONENTS[MEDIATIME][2] -
				    COMPONENTS[MEDIATIME][0] + 1));
		    repaint();
		}
	    }
	    try {
		Thread.sleep(250);
	    } catch (InterruptedException ie) {
	    }
	}
    }

    /**
     * Locations of the icons on the control bar.
     */
    int COMPONENTS[][] = {
	{ 63, 6, 73, 18 },
	{ 64, 6, 83, 18 },
	{ 84, 6, 96, 18 },
	{ 97, 6, 111, 18 },
	{ 114, 7, 136, 17 },
	{ 141, 7, 211, 17 },
	{ 287, 7, 300, 17 },
	{302, 7, 314, 17 }
    };

    /**
     * Type numbers for the icons and controls.
     */
    static final int PLAY = 0;
    static final int PAUSE = 1;
    static final int REWIND = 2;
    static final int EJECT = 3;
    static final int VOLUME = 4;
    static final int MEDIATIME = 5;
    static final int MAXIMIZE = 6;
    static final int CLOSE = 7;

    static final int GAINWIDTH = 22;


    /****************************************************************
     * GUI Stuff
     ****************************************************************/
    
    private void createGUI() {
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
	MediaTracker mt = new MediaTracker(this);
	image = loadImage("/jamp.jpg", this, true);
	mt.addImage(image, 1);
	try {
	    mt.waitForID(1);
	} catch (InterruptedException ie) {
	}
    }

    public static Image loadImage ( String strFileName ) {
        Image		       image = null;
        URL		       url;
        Toolkit		       toolkit;
        Class		       classObject;
        Object                 objImageProducer;
        ImageProducer	       imageProducer;
        BufferedInputStream    streamImage;
        byte                   arrImageBytes [];

        toolkit = Toolkit.getDefaultToolkit ();

        try {
            classObject = Class.forName ( "jamp.MainWindow" );
            url = classObject.getResource ( strFileName );
            if ( url != null ) {
                objImageProducer = url.getContent ();
                if ( objImageProducer instanceof ImageProducer ) {
                    imageProducer = (ImageProducer) objImageProducer;
                    image = toolkit.createImage ( imageProducer );
                }
                else if ( objImageProducer instanceof BufferedInputStream ) {
                    streamImage = (BufferedInputStream) objImageProducer;
                    arrImageBytes = new byte [streamImage.available()];
                    streamImage.read ( arrImageBytes );
                    image = toolkit.createImage ( arrImageBytes );
                }
            }
        }
        catch ( Exception exception ) {
            exception.printStackTrace ();
        }

        return ( image );
    }

    public static Image loadImage (String strFileName,
				   Component component,
				   boolean boolWait) {
        Image          image;
        MediaTracker   trackerMedia;

        image = loadImage ( strFileName );

        if ( image != null  &&  boolWait == true ) {
            trackerMedia = new MediaTracker ( component );
            trackerMedia.addImage ( image, 1001 );
            try {
                trackerMedia.waitForID ( 1001 );
            }
            catch ( Exception exception ) {
            }
        }

        return ( image );
    }

    public void mouseReleased(MouseEvent parm1) { }

    public void mouseEntered(MouseEvent parm1) { }

    public void mouseExited(MouseEvent parm1) { }

    public void mouseMoved(MouseEvent parm1) { }

    public void mouseClicked(MouseEvent parm1) { }

    public void update(Graphics g) {
	paint(g);
    }
    
    public void paint(Graphics g) {
	if (doubleBuffer == null) {
	    doubleBuffer = createImage(WIDTH, HEIGHT);
	    dg = doubleBuffer.getGraphics();
	}
	dg.drawImage(image, 0, 0, this);
	if (gain != null) {
	    int cx = (int) (gain.getLevel() * GAINWIDTH +
			    COMPONENTS[VOLUME][0] + 0.5);
	    int sy = COMPONENTS[VOLUME][1];
	    int ey = COMPONENTS[VOLUME][3];
	    dg.setColor(Color.cyan.darker());
	    dg.drawLine(cx, sy + 1, cx, ey - 1);
	    dg.drawLine(cx - 1, sy + 2, cx - 1, ey - 2);
	    dg.drawLine(cx + 1, sy + 2, cx + 1, ey - 2);
	}
	if (player != null && timex >= 0) {
	    int cx = timex + COMPONENTS[MEDIATIME][0];
	    int sy = COMPONENTS[MEDIATIME][1];
	    int ey = COMPONENTS[MEDIATIME][3];
	    dg.setColor(Color.cyan.darker());
	    dg.drawLine(cx, sy + 1, cx, ey - 1);
	    dg.drawLine(cx - 1, sy + 2, cx - 1, ey - 2);
	    dg.drawLine(cx + 1, sy + 2, cx + 1, ey - 2);
	}
	if (player == null) {
	    dg.setColor(Color.red);
	    dg.drawLine(24, 12, 95, 12);
	    dg.drawLine(113, 12, 300, 12);
	}
	g.drawImage(doubleBuffer, 0, 0, this);
    }


    class EjectThread extends Thread {
	
	public EjectThread() {
	}
	
	public void run() {
	    doEject();
	}
    }
}
