package editing.minime;
/*
 * %W% %E%
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.StreamWriterControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MiniME extends JPanel implements ActionListener, DataSinkListener, ProgressListener {

    String [] mediaFiles;
    VideoCutPanel vcPanel1;
    VideoCutPanel vcPanel2;
    VideoCutPanel vcPanel3;
    Dimension preferredSize = new Dimension(800, 360);
    Processor processor;
    JComboBox cbEffect;
    JComboBox cbDuration;
    JButton buttonExit;
    JButton buttonGo;
    JProgressBar jProgress;
    String outputFile = "file:/tmp/test.mov";
    Image texture;
    DataSink filewriter = null;
    
    public MiniME(String [] mediaFiles) {
	JPanel panel;
	JPanel panel2;
	JLabel label;
	
	this.mediaFiles = mediaFiles;
	setLayout(new BorderLayout());
	setOpaque(false);
	panel = new JPanel();
	panel.setLayout( new FlowLayout() );
	panel.setOpaque(false);
	vcPanel1 = new VideoCutPanel(mediaFiles, true);
	vcPanel2 = new VideoCutPanel(mediaFiles, true);
	vcPanel3 = new VideoCutPanel(mediaFiles, false);
	panel.add(vcPanel1);
	panel.add(new JLabel(" + "));
	panel.add(vcPanel2);
	panel.add(new JLabel(" = "));
	panel.add(vcPanel3);
	add("North", panel);

	panel = new JPanel();
	panel.setLayout(new GridLayout(3, 2));
	panel.setOpaque(false);
	JPanel centerPanel = new JPanel( new BorderLayout() );
	centerPanel.setBorder(BorderFactory.createEtchedBorder());
	centerPanel.setOpaque(false);
	JPanel miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( new JLabel("Duration (seconds) "));
	panel.add(miniPanel);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( cbDuration = new JComboBox() );
	panel.add(miniPanel);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( new JLabel("Video Effect ") );
	panel.add(miniPanel);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( cbEffect = new JComboBox() );
	panel.add(miniPanel);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( new JLabel("Progress ") );
	panel.add(miniPanel);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); miniPanel.setOpaque(false);
	miniPanel.add( jProgress = new JProgressBar(0, 1000) );
	panel.add(miniPanel);

	panel2 = new JPanel();
	panel2.setLayout( new FlowLayout() );
	panel2.add(panel);
	panel2.setOpaque(false);
	
	centerPanel.add("North", panel2);

	panel = new JPanel();
	panel.setOpaque(false);
	miniPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); miniPanel.setOpaque(false);
	miniPanel.add( buttonGo = new JButton(" GO! ") );
	panel.add(miniPanel);
	centerPanel.add("South", panel);
	
	for (int i = 1; i < 10; i++) {
	    cbDuration.addItem("" + i);
	}

	cbEffect.addItem("Scroll");
	cbEffect.addItem("Fade");

	add("Center", centerPanel);

	buttonGo.addActionListener(this);
	texture = Toolkit.getDefaultToolkit().getImage("texture.jpg");
    }

    public void paint(Graphics g) {
	if (texture != null) {
	    int width = texture.getWidth(this);
	    int height = texture.getHeight(this);
	    if (width > 0 && height > 0)
		for (int y = 0; y < preferredSize.height + height; y += height) {
		    for (int x = 0; x < preferredSize.width + width; x += width) {
			g.drawImage(texture, x, y, this);
		    }
		}
	}
	super.paint(g);
    }
	    
    public void actionPerformed(ActionEvent ae) {

	String mediaFile1 = vcPanel1.getMediaFile();
	String mediaFile2 = vcPanel2.getMediaFile();
	Time beginTime1 = vcPanel1.getBeginTime();
	Time beginTime2 = vcPanel2.getBeginTime();
	Time endTime1 = vcPanel1.getEndTime();
	Time endTime2 = vcPanel2.getEndTime();

	String sDuration = (String) cbDuration.getSelectedItem();
	long duration = (long) Integer.parseInt(sDuration) * 1000000000L;
	vcPanel3.setURL(null);
	if (mediaFile1 == null || mediaFile2 == null ||
	    beginTime1 == null || beginTime2 == null ||
	    endTime1 == null || endTime2 == null)
	    return;

	vcPanel1.stop();
	vcPanel2.stop();
	SuperGlueDataSource sgds = new SuperGlueDataSource(
				    new String [] { mediaFile1, mediaFile2 },
				    new Time [] { beginTime1, beginTime2 },
				    new Time [] { endTime1, endTime2 },
				    new Time [] { new Time(duration), new Time(duration) },
				    new String[] { (String) cbEffect.getSelectedItem() },
				    new String[0],
				    new Dimension(160, 120));
	buttonGo.setEnabled(false);
	try {
	    sgds.connect();
	    sgds.setProgressListener(this);
	    Processor p = Manager.createProcessor(sgds);
	    boolean success = waitForState(p, Processor.Configured);

	    if (!success) {
		System.err.println("Error configuring output processor");
		buttonGo.setEnabled(true);
		return;
	    }

	    p.setContentDescriptor(new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME));
	    success = waitForState(p, Processor.Realized);
	    if (!success) {
		System.err.println("Could not realize output processor");
		buttonGo.setEnabled(true);
	    }
	    DataSource ds = p.getDataOutput();
	    doSave(p, ds);
	} catch (Exception ex) {
	    buttonGo.setEnabled(true);
	    System.err.println("Exception creating processor: " + ex);
	}
    }

    public void updateProgress(long current, long duration) {
	jProgress.setMaximum((int) (duration / 1000000));
	jProgress.setValue((int) (current / 1000000));
    }
    
    private void doSave(Processor processor, DataSource ds) {
	try {
	    filewriter = Manager.createDataSink(ds, new MediaLocator(outputFile));
	    StreamWriterControl fwc = (StreamWriterControl)
		filewriter.getControl("javax.media.control.StreamWriterControl");
	    filewriter.open();
	    
	    // Not creating the PlayerWindow for the processor
	    // as it is currently not very useful. The media
	    // time should reflect how many frames have been
	    // read and saved to the file and not mediaTime
	    // corresponding to rate 1.0
	    //processorWindow = new PlayerWindow(processor, "Transcoding Processor",
	    //			       cbAutoStart.getState());

	    this.processor = processor;
	} catch (NoDataSinkException e) {
	    System.err.println("NoDataSinkException");
	    return;
	} catch (IOException e) {
	    System.err.println("IOException: " + e.getMessage());
	    return;
	} catch (SecurityException e) {
	    System.err.println("SecurityException: " + e.getMessage());
	    return;
	}

	processor.addControllerListener(new EOMListener(processor));
	filewriter.addDataSinkListener(this);

	// now start the filewriter and datasource
	try {
	    filewriter.start();
	    ds.start();
	    processor.start();
	    System.err.println("Started everything");
	} catch (IOException e) {
	    System.err.println("Error starting file writer");
	    //System.exit(-1);
	}
    }

    public synchronized void dataSinkUpdate(DataSinkEvent event) {
	if (event instanceof EndOfStreamEvent) {
	    buttonGo.setEnabled(true);
	    filewriter.close();
	}
	jProgress.setMaximum(100);
	jProgress.setValue(100);
	vcPanel3.setURL(outputFile);
    }
    
    Integer            stateLock = new Integer(0);
    boolean            failed = false;

    class StateListener implements ControllerListener {
	
	public void controllerUpdate(ControllerEvent ce) {
	    if (ce instanceof ControllerClosedEvent)
		setFailed();

	    if (ce instanceof ControllerEvent)
		synchronized (getStateLock()) {
		    getStateLock().notifyAll();
		}
	}
    }

    class EOMListener implements ControllerListener {

	Processor p;

	public EOMListener(Processor p) {
	    this.p = p;
	}

	public void controllerUpdate(ControllerEvent ce) {
	    if (ce instanceof EndOfMediaEvent) {
		p.close();
	    }
	}
    }


    private synchronized boolean waitForState(Processor p, int state) {
	StateListener sl = new StateListener();
	p.addControllerListener(sl);
	failed = false;

	if (state == Processor.Configured) {
	    p.configure();
	} else if (state == Processor.Realized) {
	    p.realize();
	}

	while (p.getState() < state && !failed) {
	    synchronized (getStateLock()) {
		try {
		    getStateLock().wait();
		} catch (InterruptedException ie) {
		    return false;
		}
	    }
	}	
	p.removeControllerListener(sl);
	return !failed;
    }

    public static int getSaveType() {
	return 1;
    }

    Integer getStateLock() {
	return stateLock;
    }

    void setFailed() {
	failed = true;
    }

    public Dimension getPreferredSize() {
	return preferredSize;
    }

    public static void main(String [] args) {
	JFrame f = new JFrame("MiniME - Mini Media Editor");
	f.getContentPane().add(new MiniME(args));
	f.setSize(800, 420);
	f.addWindowListener( new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		System.exit(0);
	    }
	} );
	f.setVisible(true);
    }
}
    
