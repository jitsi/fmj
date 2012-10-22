package rtp.toolsrx;

/*
 * @(#)AVReceiver.java	1.1 01/03/12
 *
 * Copyright (c) 2001 Sun Microsystems, Inc. All Rights Reserved.
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


import java.awt.*;
import java.net.*;
import java.util.*;

import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.*;
import javax.media.protocol.*;
import javax.media.control.BufferControl;
import com.sun.media.rtp.RTPSessionMgr;

public class AVReceiver implements ReceiveStreamListener, SessionListener, 
	ControllerListener, RemoteListener
{
    Vector mgrs;
    Vector playerWindows = null;

    boolean dataReceived = false;
    Object dataSync = new Object();

    Vector targets;
    Rx rx;
    
    public AVReceiver( Rx rx, Vector targets) {
	this.rx= rx;
	this.targets = targets;

	initialize();
    }    

    private void initialize() {
	mgrs = new Vector( targets.size());

        playerWindows = new Vector();

        // Open the RTP sessions.
        for( int i= 0; i < targets.size(); i++) {		
      	    Target target= (Target) targets.elementAt( i);

	    addTarget( target.ip, target.port, target.localPort);		
	}
    }
	
    public boolean isDone() {
	return playerWindows.size() == 0;
    }

    public void addTarget( String senderAddress,
			   String senderPort,
			   String localPort) {
        try {
	    InetAddress ipAddr;
	    SessionAddress localAddr;
	    SessionAddress destAddr;

	    RTPManager mgr = RTPManager.newInstance();
	    mgr.addSessionListener(this);
	    mgr.addReceiveStreamListener(this);
	    mgr.addRemoteListener( this);

	    ipAddr = InetAddress.getByName( senderAddress);

	    int local_port= new Integer( localPort).intValue();
		
	    if( ipAddr.isMulticastAddress()) {
	        // local and remote address pairs are identical:
	        localAddr= new SessionAddress( ipAddr,
	    				       local_port,
	       				       6);
		    
		destAddr = new SessionAddress( ipAddr,
					       local_port,
					       6);
	    } else {
	        localAddr= new SessionAddress( InetAddress.getLocalHost(),
	      	  		               local_port);

		int remotePort= new Integer( senderPort).intValue();
		    
                destAddr = new SessionAddress( ipAddr, remotePort);
	    }
			
	    mgr.initialize( localAddr);

	    // You can try out some other buffer size to see
	    // if you can get better smoothness.
	    BufferControl bc = (BufferControl)mgr.getControl("javax.media.control.BufferControl");
	    if (bc != null) {
	        bc.setBufferLength(350);
	    }
		    
    	    mgr.addTarget(destAddr);

	    mgrs.addElement( mgr);	    
        } catch (Exception e){
            System.err.println("Cannot create the RTP Session: " + e.getMessage());
            return;
        }	
    }
    
    public void removeTarget( String address, String port) {
	for( int i= 0; i < playerWindows.size(); i++) {
	    PlayerWindow pw= (PlayerWindow)playerWindows.elementAt( i);

	    System.out.println( pw.senderAddress + ":" + pw.senderPort);
	
	    if( address.equals( pw.senderAddress) && port.equals( pw.senderPort)) {
	        try {
		    pw.close();
		} catch (Exception e) {
		}

	        playerWindows.removeElement( pw);		

		break;
	    }	    
	}

	for (int i = 0; i < mgrs.size(); i++) {
	    RTPSessionMgr mgr= (RTPSessionMgr) mgrs.elementAt( i);

	    SessionAddress addr= mgr.getRemoteSessionAddress();

	    String dataPort= addr.getDataPort() + "";
	    
            if( addr.getDataHostAddress().equals( address)
		&& port.equals( dataPort)) {
		
                mgr.removeTarget( addr, "Closing session from AVReceiver");
                mgr.dispose();

		mgrs.removeElement( mgr);
		
		break;
	    }
	}
    }
    
    /**
     * Close the players and the rtp  managers.
     */
    protected void close() {
	for( int i= 0; i < playerWindows.size(); i++) {
	    try {
		((PlayerWindow)playerWindows.elementAt(i)).close();
	    } catch (Exception e) {}
	}

	playerWindows.removeAllElements();

	// close the RTP session.
	for (int i = 0; i < mgrs.size(); i++) {
	    RTPManager mgr= (RTPManager) mgrs.elementAt( i);
	    
            mgr.removeTargets( "Closing session from AVReceiver");
            mgr.dispose();
	    mgr = null;
	}
    }

    PlayerWindow find(Player p) {
	for (int i = 0; i < playerWindows.size(); i++) {
	    PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
	    
	    if (pw.player == p) {
		return pw;
	    }
	}
	return null;
    }


    PlayerWindow find(ReceiveStream strm) {
	for (int i = 0; i < playerWindows.size(); i++) {
	    PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
	    if (pw.stream == strm)
		return pw;
	}
	return null;
    }


    /**
     * SessionListener.
     */
    public synchronized void update(SessionEvent evt) {
	if (evt instanceof NewParticipantEvent) {
	    Participant p = ((NewParticipantEvent)evt).getParticipant();
	    System.err.println("  - A new participant had just joined: " + p.getCNAME());
	}
    }


    /**
     * ReceiveStreamListener
     */
    public synchronized void update( ReceiveStreamEvent evt) {
	RTPManager mgr = (RTPManager)evt.getSource();

	Participant participant = evt.getParticipant();	// could be null.
	ReceiveStream stream = evt.getReceiveStream();  // could be null.

	String timestamp= getTimestamp();
	
	if (evt instanceof RemotePayloadChangeEvent) {
     
	    System.err.println("  - Received an RTP PayloadChangeEvent.");
	    System.err.println("Sorry, cannot handle payload change.");
	    System.exit(0);

	} else if (evt instanceof NewReceiveStreamEvent) {
	    try {
		stream = ((NewReceiveStreamEvent)evt).getReceiveStream();
		DataSource ds = stream.getDataSource();

		// Find out the formats.
		RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
		if (ctl != null) {
		    System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
		} else {
		    System.err.println("  - Recevied new RTP stream");
		}
		
		if (participant == null) {
		    System.err.println("      The sender of this stream had yet to be identified.");
		} else {
		    System.err.println("      The stream comes from: " + participant.getCNAME()); 
		}

		// create a player by passing datasource to the Media Manager
		Player p = javax.media.Manager.createPlayer(ds);
		
		if (p == null) {
		    return;
		}
		
		p.addControllerListener(this);
		p.realize();
		PlayerWindow pw = new PlayerWindow(p, stream);
		playerWindows.addElement(pw);

		// Notify intialize() that a new stream had arrived.
		synchronized (dataSync) {
		    dataReceived = true;
		    dataSync.notifyAll();
		}

	    } catch (Exception e) {
		System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
		return;
	    }        
	} else if (evt instanceof StreamMappedEvent) {
	    if (stream != null && stream.getDataSource() != null) {
		DataSource ds = stream.getDataSource();
		// Find out the formats.
		RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
		System.err.println("  - The previously unidentified stream ");

		
		if (ctl != null) {
		    System.err.println("      " + ctl.getFormat());
		}
		
		System.err.println("      had now been identified as sent by: " + participant.getCNAME());

		RTPSessionMgr rtpManager= (RTPSessionMgr) evt.getSessionManager();

		SessionAddress addr= rtpManager.getRemoteSessionAddress();

		if( addr != null) {
		    PlayerWindow pw = find(stream);		

		    pw.setTitle( addr.getDataHostAddress(),
				 addr.getDataPort());
		}
	    }
	} else if (evt instanceof ByeEvent) {
	    StringBuffer sb= new StringBuffer();

	    sb.append( timestamp + " BYE");

	    String reason= ((ByeEvent) evt).getReason();
	    
	    sb.append( " from " + participant.getCNAME());
	    sb.append( " ssrc=" + stream.getSSRC());
	    sb.append( " reason='" + reason + "'");

	    rx.rtcpReport( sb.toString());
		
	    PlayerWindow pw = find(stream);
	     
	    if (pw != null) {
		pw.close();
		playerWindows.removeElement(pw);
	    }
	}
    }

    public void update( RemoteEvent event) {	
	String timestamp= getTimestamp();       
 
	if( event instanceof ReceiverReportEvent) {
	    ReceiverReport rr= ((ReceiverReportEvent) event).getReport();

	    StringBuffer sb= new StringBuffer();

	    sb.append( timestamp + " RR");

	    if( rr != null) {
		Participant participant= rr.getParticipant();

		if( participant != null) {
		    sb.append( " from " + participant.getCNAME());
		    sb.append( " ssrc=" + rr.getSSRC());
		} else {
		    sb.append( " ssrc=" + rr.getSSRC());
		}

		rx.rtcpReport( sb.toString());
	    }	    	    
	} else if( event instanceof SenderReportEvent) {
	    SenderReport sr= ((SenderReportEvent) event).getReport();

	    StringBuffer sb= new StringBuffer();

	    sb.append( timestamp + " SR");

	    if( sr != null) {
		Participant participant= sr.getParticipant();

		if( participant != null) {
		    sb.append( " from " + participant.getCNAME());
		    sb.append( " ssrc=" + sr.getSSRC());
		} else {
		    sb.append( " ssrc=" + sr.getSSRC());
		}

		rx.rtcpReport( sb.toString());
	    }	    	    
	} else {
	    System.out.println( "RemoteEvent: " + event);
	}
    }

    private String getTimestamp() {
	String timestamp;

	Calendar calendar= Calendar.getInstance();

	int hour= calendar.get( Calendar.HOUR_OF_DAY);

	String hourStr= formatTime( hour);

	int minute= calendar.get( Calendar.MINUTE);

	String minuteStr= formatTime( minute);

	int second= calendar.get( Calendar.SECOND);
	
	String secondStr= formatTime( second);
		
	timestamp= hourStr + ":" + minuteStr + ":" + secondStr;	

	return timestamp;
    }

    private String formatTime( int time) {	
	String timeStr;
	
	if( time < 10) {
	    timeStr= "0" + time;
	} else {
	    timeStr= "" + time;
	}

	return timeStr;
    }
    
    /**
     * ControllerListener for the Players.
     */
    public synchronized void controllerUpdate(ControllerEvent ce) {

	Player p = (Player)ce.getSourceController();

	if (p == null)
	    return;

	// Get this when the internal players are realized.
	if (ce instanceof RealizeCompleteEvent) {
	    PlayerWindow pw = find(p);
	    if (pw == null) {
		// Some strange happened.
		System.err.println("Internal error!");
		System.exit(-1);
	    }
	    pw.initialize();
	    pw.setVisible(true);
	    p.start();
	}

	if (ce instanceof ControllerErrorEvent) {
	    p.removeControllerListener(this);
	    PlayerWindow pw = find(p);
	    if (pw != null) {
		pw.close();	
		playerWindows.removeElement(pw);
	    }

	    System.err.println("AVReceiver internal error: " + ce);
	}
    }


    /**
     * GUI classes for the Player.
     */
    class PlayerWindow extends Frame {
	Player player;
	ReceiveStream stream;
	String senderPort;
	String senderAddress;

	PlayerWindow(Player p, ReceiveStream strm) {
	    player = p;
	    stream = strm;

	    super.setTitle( "Unkown Sender");
	}

	public void setTitle( String address,
			      int    port) {
	    senderAddress= address;
	    senderPort= port + "";
	    
	    String title= senderAddress + ":" + senderPort;
			
	    super.setTitle( title);
	}

	public void initialize() {
	    add(new PlayerPanel(player));
	}

	public void close() {
	    player.close();
	    setVisible(false);
	    dispose();
	}

	public void addNotify() {
	    super.addNotify();
	    pack();
	}
    }

    /**
     * GUI classes for the Player.
     */
    class PlayerPanel extends Panel {

	Component vc, cc;

	PlayerPanel(Player p) {
	    setLayout(new BorderLayout());
	    if ((vc = p.getVisualComponent()) != null)
		add("Center", vc);
	    if ((cc = p.getControlPanelComponent()) != null)
		add("South", cc);
	}

	public Dimension getPreferredSize() {
	    int w = 0, h = 0;
	    if (vc != null) {
		Dimension size = vc.getPreferredSize();
		w = size.width;
		h = size.height;
	    }
	    if (cc != null) {
		Dimension size = cc.getPreferredSize();
		if (w == 0)
		    w = size.width;
		h += size.height;
	    }
	    if (w < 160)
		w = 160;
	    return new Dimension(w, h);
	}
    }
}




