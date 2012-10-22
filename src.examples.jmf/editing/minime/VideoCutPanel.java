package editing.minime;

/*
 * @(#)VideoCutPanel.java	1.2 99/08/05
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.StopAtTimeEvent;
import javax.media.Time;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VideoCutPanel extends JPanel
    implements ActionListener, ItemListener, ControllerListener, Runnable {

    String [] movies;
    Player player;
    JComboBox comboMovies;
    SelectionPanel2 selPanel;
    Component visual, control;
    int controlHeight = 20;
    Time beginTime, endTime;
    Time duration;
    int timerCount = 0;
    JPanel centerPanel, videoPanel;
    String mediaFile;
    JLabel labelTime;
    
    public VideoCutPanel(String [] movies, boolean complex) {
	BorderLayout bl;
	this.movies = movies;
	bl = new BorderLayout();
	bl.setHgap(5);
	bl.setVgap(5);
	setLayout(bl);
	setOpaque(false);
	setBorder(BorderFactory.createEtchedBorder());
	String [] moviesJustNames = new String[movies.length];
	for (int i=0; i < movies.length; i++) {
	    int k = movies[i].lastIndexOf("/");
	    if (k < 0) k = movies[i].lastIndexOf("\\");
	    moviesJustNames[i] = movies[i].substring(k + 1);
	}
	
	if (complex) {
	    comboMovies = new JComboBox(moviesJustNames);
	    comboMovies.setLightWeightPopupEnabled( false );
	    comboMovies.setMaximumRowCount(6);
	    comboMovies.addItemListener( this );
	    comboMovies.setSize(comboMovies.getPreferredSize().width, 200);
	    add("North", comboMovies);
	} else
	    add("North", new JLabel("Result", JLabel.CENTER));
	
	videoPanel = new JPanel();
	videoPanel.setOpaque(false);
	videoPanel.setLayout( new BorderLayout() );
	centerPanel = new JPanel();
	centerPanel.setOpaque(false);
	centerPanel.setLayout( null );
	centerPanel.add(videoPanel);
	add("Center", centerPanel);

	if (complex) {
	    JPanel southPanel = new JPanel();
	    southPanel.setOpaque(false);
	    southPanel.setLayout( new BorderLayout() );
	    labelTime = new JLabel("Selection", JLabel.CENTER);
	    labelTime.setFont(new Font("Dialog", Font.PLAIN, 10));
	    southPanel.add("South", labelTime);
	    selPanel = new SelectionPanel2( this );
	    selPanel.setVisible( true /* false */);
	    southPanel.add("North", selPanel);
	    add("South", southPanel);
	}
	
	Thread t = new Thread(this);
	t.start();
    }

    private void updateLabel() {
	if (labelTime == null)
	    return;
	String begin = formatTime(beginTime);
	String end = formatTime(endTime);
	labelTime.setText(begin + " - " + end);
    }

    private String formatTime ( Time time ) {
	long    nano;
	int     hours;
	int     minutes;
	int     seconds;
	int     hours10;
	int     minutes10;
	int     seconds10;
	long    nano10;
        String  strTime = new String ( "<unknown>" );

	if ( time == null  ||  time == Time.TIME_UNKNOWN  ||  time == javax.media.Duration.DURATION_UNKNOWN )
	    return ( strTime );

	nano = time.getNanoseconds();
	seconds = (int) (nano / Time.ONE_SECOND);
	hours = seconds / 3600;
	minutes = ( seconds - hours * 3600 ) / 60;
	seconds = seconds - hours * 3600 - minutes * 60;
	nano = (long) ((nano % Time.ONE_SECOND) / (Time.ONE_SECOND/100));

        hours10 = hours / 10;
        hours = hours % 10;
        minutes10 = minutes / 10;
        minutes = minutes % 10;
        seconds10 = seconds / 10;
        seconds = seconds % 10;
        nano10 = nano / 10;
        nano = nano % 10;

        strTime = new String ( "" + hours10 + hours + ":" + minutes10 +
			       minutes + ":" + seconds10 + seconds + "." + nano10 + nano );
	return ( strTime );
    }
    
    public String getMediaFile() {
	return mediaFile;
    }

    public Time getBeginTime() {
	return beginTime;
    }

    public Time getEndTime() {
	return endTime;
    }

    public void stop() {
	if (player != null)
	    player.stop();
    }
	
    public Dimension getPreferredSize() {
	Insets insets = getInsets();
	Dimension size = new Dimension(220 + insets.left + insets.right,
				       220 + insets.top + insets.bottom);
	return size;
    }

    public Insets getInsets() {
	Insets in = super.getInsets();
	in.left += 5;
	in.right += 5;
	in.top += 5;
	in.bottom += 5;
	return in;
    }

    public synchronized void setURL(String mediaFile) {
	if (player != null) {
	    if (visual != null)
		videoPanel.remove(visual);
	    if (control != null)
		videoPanel.remove(control);
	    player.removeControllerListener(this);
	    player.close();
	    player = null;
	    visual = null;
	    control = null;
	}
	if (mediaFile == null)
	    return;
	URL url = null;
	try {
	    // Create an url from the file name and the url to the
	    // document containing this applet.
	    if ((url = new URL(mediaFile)) == null) {
		System.err.println("Error creating url");
		return;
	    }
	    
	    // Create an instance of a player for this media
	    try {
		player = Manager.createPlayer(url);
	    } catch (NoPlayerException e) {
		System.err.println("Error creating player");
	    }
	} catch (MalformedURLException e) {
	    System.err.println(e);
	} catch (IOException e) {
	    System.err.println(e);
	}

	if (player != null) {
	    player.addControllerListener((ControllerListener) this);
	    player.realize();
	    this.mediaFile = mediaFile;
	}
    }
    
    public void actionPerformed(ActionEvent ae) {
	if (ae.getSource() == comboMovies)
	    setURL(movies[comboMovies.getSelectedIndex()]);
	//setURL((String) comboMovies.getSelectedItem());
	if (ae.getSource() == selPanel) {
	    if (player != null) {
		Time newBeginTime = 
		    new Time(selPanel.getStartTimeMillis() * 1000000);
		Time newEndTime = 
		    new Time(selPanel.getStopTimeMillis() * 1000000);
		if (newBeginTime.getSeconds() != beginTime.getSeconds()) {
		    if (player.getState() == Player.Started)
			player.stop();
		    player.setMediaTime(newBeginTime);
		    timerCount = 1000;
		    beginTime = newBeginTime;
		    updateLabel();
		}
		if (newEndTime.getSeconds() != endTime.getSeconds()) {
		    if (player.getState() == Player.Started)
			player.stop();
		    endTime = newEndTime;
		    player.setMediaTime(newEndTime);
		    timerCount = 1000;
		    updateLabel();
		}
	    }
	}
    }

    public void run() {
	while ( true ) {
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException ie) {
	    }
	    timerCount -= 100;
	    if (timerCount == 0 && player != null &&
		player.getState() != Controller.Started) {
		player.setMediaTime(beginTime);
		player.setStopTime(endTime);
		player.start();
	    }
	    if (timerCount < -1000)
		timerCount = 0;
	}
    }
    
    public void itemStateChanged(ItemEvent ie) {
	Object item = ie.getItem();
	if (item instanceof String) {
	    setURL(movies[comboMovies.getSelectedIndex()]);
	}
    }
    
    public void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof RealizeCompleteEvent) {
	    if (visual != null)
		return;
	    beginTime = new Time(0);
	    duration = endTime = player.getDuration();
	    if (selPanel != null) {
		selPanel.setStartTimeMillis(
			     beginTime.getNanoseconds() / 1000000);
		selPanel.setStopTimeMillis(
			     endTime.getNanoseconds() / 1000000);
	    }
	    updateLabel();
	    player.prefetch();
	} else if (ce instanceof PrefetchCompleteEvent) {
	    if (visual != null)
		return;
	    controlHeight = 0;
	    if ((visual = player.getVisualComponent()) != null) {
		Dimension size = visual.getPreferredSize();
		visual.setSize(size);
		videoPanel.add("Center", visual);
		if ((control = player.getControlPanelComponent()) != null) {
		    controlHeight = control.getPreferredSize().height;
		    videoPanel.add("South", control);
		    videoPanel.setSize(size.width, size.height + controlHeight);
		    Dimension cSize = centerPanel.getSize();
		    Dimension vSize = videoPanel.getSize();
		    videoPanel.setLocation((cSize.width - vSize.width) / 2,
					   (cSize.height - vSize.height) / 2);
		}
		videoPanel.invalidate();
		validate();
	    }
	    player.start();
	} else if (ce instanceof EndOfMediaEvent ||
		   ce instanceof StopAtTimeEvent) {
	    player.setMediaTime(beginTime);
	    player.setStopTime(endTime);
	    player.start();
	}
    }

    public static void main(String [] args) {
	VideoCutPanel vcp;
	Frame frame = new Frame("Test");
	//frame.setSize(512, 512);
	frame.setLayout( new FlowLayout() );
	frame.add(vcp = new VideoCutPanel(args, true));
	frame.setBackground(vcp.getBackground());
	frame.add(vcp = new VideoCutPanel(args, true));
	frame.pack();
	frame.setVisible(true);
    }
}
