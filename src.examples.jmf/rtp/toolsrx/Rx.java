package rtp.toolsrx;

/*
 * @(#)Rx.java	1.1 01/03/12
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Rx extends JFrame implements ActionListener, KeyListener,
                                          MouseListener, WindowListener {
    Vector targets;
    JList list;
    JButton rtcp;
    JButton startRx;
    JButton expiration;
    JButton statistics;
    JButton addTarget;
    JButton removeTarget;
    JTextField tf_remote_address;
    JTextField tf_remote_data_port;
    JTextField tf_local_data_port;
    JTextField tf_media_file;
    TargetListModel listModel;
    RTCPViewer rtcpViewer;
    JCheckBox cb_loop;
    Config config;
    AVReceiver avReceiver;

    public Rx() {
        setTitle( "JMF/RTP Receiver");

	config= new Config();
	
	GridBagLayout gridBagLayout= new GridBagLayout();

	GridBagConstraints gbc;

	JPanel p= new JPanel();
	p.setLayout( gridBagLayout);

	JPanel localPanel= createLocalPanel();
 
	gbc= new GridBagConstraints();
	gbc.gridx= 0;
	gbc.gridy= 0;
	gbc.gridwidth= 2;
	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill= GridBagConstraints.BOTH;
	gbc.insets= new Insets( 10, 5, 0, 0);
	((GridBagLayout)p.getLayout()).setConstraints( localPanel, gbc);
	p.add( localPanel);
	
	JPanel targetPanel= createTargetPanel();
	
	gbc= new GridBagConstraints();
	gbc.gridx= 1;
	gbc.gridy= 1;
	gbc.weightx= 1.0;
	gbc.weighty= 1.0;
	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill= GridBagConstraints.BOTH;
	gbc.insets= new Insets( 10, 5, 0, 0);
	((GridBagLayout)p.getLayout()).setConstraints( targetPanel, gbc);
        p.add( targetPanel);
	
        JPanel buttonPanel= new JPanel();
    	
        rtcp= new JButton( "RTCP Monitor");
        startRx= new JButton( "Start Receiver");

	rtcp.addActionListener( this);
	startRx.addActionListener( this);
	
	buttonPanel.add( rtcp);
	buttonPanel.add( startRx);

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 3;
        gbc.gridwidth= 2;
	gbc.weightx = 1.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets( 5,5,10,5);
	((GridBagLayout)p.getLayout()).setConstraints( buttonPanel, gbc);
	p.add( buttonPanel);

        
        getContentPane().add( p);

	list.addMouseListener( this);

	addWindowListener( this);

        pack();

        setVisible( true);
    }
	
    private JPanel createTargetPanel() {
        JPanel p= new JPanel();

     	GridBagLayout gridBagLayout= new GridBagLayout();

        GridBagConstraints gbc;

       	p.setLayout( gridBagLayout);

	targets= config.targets;
	
        listModel= new TargetListModel( targets);

        list= new JList( listModel);
	
	list.addKeyListener( this);

	list.setPrototypeCellValue( "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        JScrollPane scrollPane= new JScrollPane( list,
                                                 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

	gbc= new GridBagConstraints();
	gbc.gridx= 0;
	gbc.gridy= 0;
	gbc.weightx= 1.0;
	gbc.weighty= 1.0;
	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill= GridBagConstraints.BOTH;
	gbc.insets= new Insets( 10, 5, 0, 0);
	((GridBagLayout)p.getLayout()).setConstraints( scrollPane, gbc);
	p.add( scrollPane);


        JPanel p1= new JPanel();

       	p1.setLayout( gridBagLayout);
	
	JLabel label= new JLabel( "Sender IP:");

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.EAST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( label, gbc);
	p1.add( label);

	tf_remote_address= new JTextField( 15);

	gbc= new GridBagConstraints();
	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.WEST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( tf_remote_address, gbc);
	p1.add( tf_remote_address);

	label= new JLabel( "Sender Port:");

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 1;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.EAST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( label, gbc);
	p1.add( label);

	tf_remote_data_port= new JTextField( 15);

	gbc= new GridBagConstraints();
	gbc.gridx = 1;
	gbc.gridy = 1;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.WEST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( tf_remote_data_port, gbc);
	p1.add( tf_remote_data_port);	

	label= new JLabel( "Local Port:");

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 2;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.EAST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( label, gbc);
	p1.add( label);

	tf_local_data_port= new JTextField( 15);

	gbc= new GridBagConstraints();
	gbc.gridx = 1;
	gbc.gridy = 2;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.WEST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( tf_local_data_port, gbc);
	p1.add( tf_local_data_port);
	

        JPanel p2= new JPanel();

        addTarget= new JButton( "Add Target");	
        removeTarget= new JButton( "Remove Target");

	p2.add( addTarget);
	p2.add( removeTarget);

	addTarget.addActionListener( this);
	removeTarget.addActionListener( this);
	
	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 3;
	gbc.weightx = 1.0;
	gbc.weighty = 0.0;
	gbc.gridwidth= 2;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets( 20,5,0,5);
	((GridBagLayout)p1.getLayout()).setConstraints( p2, gbc);
	p1.add( p2);
	
	gbc= new GridBagConstraints();
	gbc.gridx= 1;
	gbc.gridy= 0;
	gbc.weightx= 1.0;
	gbc.weighty= 1.0;
	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill= GridBagConstraints.BOTH;
	gbc.insets= new Insets( 10, 5, 0, 0);
	((GridBagLayout)p.getLayout()).setConstraints( p1, gbc);
	p.add( p1);

	TitledBorder titledBorder= new TitledBorder( new EtchedBorder(), "Targets");

	p.setBorder( titledBorder);

	if( targets.size() > 0) {
	    removeTarget.setEnabled( true);
	} else {
	    removeTarget.setEnabled( false);
	}
	    
	return p;
    }
    
    private JPanel createLocalPanel() {
        JPanel p= new JPanel();

     	GridBagLayout gridBagLayout= new GridBagLayout();

        GridBagConstraints gbc;

       	p.setLayout( gridBagLayout);

	JLabel label= new JLabel( "IP Address:");

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.EAST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,0,5);
	((GridBagLayout)p.getLayout()).setConstraints( label, gbc);
	p.add( label);
	
	JTextField tf_local_host= new JTextField( 15);

	gbc= new GridBagConstraints();
	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = GridBagConstraints.WEST;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets = new Insets( 5,5,10,5);
	((GridBagLayout)p.getLayout()).setConstraints( tf_local_host, gbc);
	p.add( tf_local_host);

	try {
            String host= InetAddress.getLocalHost().getHostAddress();	
	    tf_local_host.setText( host);
	} catch( UnknownHostException e) {
	}
	
	TitledBorder titledBorder= new TitledBorder( new EtchedBorder(), "Local Host");

	p.setBorder( titledBorder);
	
	return p;
    }

    public void actionPerformed( ActionEvent event) {
        Object source= event.getSource();

	if( source == addTarget) {
	    String ip= tf_remote_address.getText().trim();
	    String port= tf_remote_data_port.getText().trim();
	    String localPort= tf_local_data_port.getText().trim();

	    if( avReceiver != null) {
		avReceiver.addTarget( ip, port, localPort);
	    }
	    
	    addTargetToList( localPort, ip, port);
	} else if( source == removeTarget) {
	    String ip= tf_remote_address.getText().trim();
	    String port= tf_remote_data_port.getText().trim();
	    
	    int index= list.getSelectedIndex();

	    if( index != -1) {
		Target target= (Target) targets.elementAt( index);

		if( avReceiver != null) {
		    avReceiver.removeTarget( ip, port);
		}

		targets.removeElement( target);
		listModel.setData( targets);

		if( targets.size() == 0) {
		    removeTarget.setEnabled( false);
		}

		if( targets.size() > 0) {
		    if( index > 0) {
			index--;
		    } else {
			index= 0;
		    }

		    list.setSelectedIndex( index);

		    setTargetFields();		    
		} else {
		    list.setSelectedIndex( -1);		    
		}		    
	    }
	} else if( source == rtcp) {
	    if( rtcpViewer == null) {
	        rtcpViewer= new RTCPViewer();
	    } else {
		rtcpViewer.setVisible( true);
		rtcpViewer.toFront();
	    }
	} else if( source == startRx) {
	    if( startRx.getLabel().equals( "Start Receiver")) {
	        avReceiver= new AVReceiver( this, targets);
		startRx.setLabel( "Stop Receiver");
	    } else {
		avReceiver.close();
		avReceiver= null;
		
		startRx.setLabel( "Start Receiver");		
	    }
	}	
    }

    synchronized public void addTargetToList( String localPort,
					      String ip, String port) {	
        ListUpdater listUpdater= new ListUpdater( localPort, ip,
						   port, listModel, targets,
						  removeTarget);
     
        SwingUtilities.invokeLater( listUpdater);  		
    }

    public void rtcpReport( String report) {
	if( rtcpViewer != null) {
	    rtcpViewer.report( report);
	}
    }
    
    public void windowClosing( WindowEvent event) {
	config.write();
	
        System.exit( 0);
    }

    public void windowClosed( WindowEvent event) {
    }

    public void windowDeiconified( WindowEvent event) {
    }

    public void windowIconified( WindowEvent event) {
    }

    public void windowActivated( WindowEvent event) {
    }

    public void windowDeactivated( WindowEvent event) {
    }

    public void windowOpened( WindowEvent event) {
    }

    public void keyPressed( KeyEvent event) {
    }
    
    public void keyReleased( KeyEvent event) {
        Object source= event.getSource();

	if( source == list) {
	    int index= list.getSelectedIndex();
	}
    }

    public void keyTyped( KeyEvent event) {
    }
	
    public void mousePressed( MouseEvent e) {
    }

    public void mouseReleased( MouseEvent e) {
    }

    public void mouseEntered( MouseEvent e) {
    }

    public void mouseExited( MouseEvent e) {
    }

    public void mouseClicked( MouseEvent e) {
        Object source= e.getSource();

	if( source == list) {
	    setTargetFields();
	}
    }

    public void setTargetFields() {
	int index= list.getSelectedIndex();

	if( index != -1) {
	    Target target= (Target) targets.elementAt( index);

	    tf_remote_address.setText( target.ip);
	    tf_remote_data_port.setText( target.port);
	    tf_local_data_port.setText( target.localPort);		
	}
    }
    
    public static void main( String[] args) {
        new Rx();
    }
}

class TargetListModel extends AbstractListModel {
    private Vector options;

    public TargetListModel( Vector options) {
	this.options= options;
    }

    public int getSize() {
	int size;

	if( options == null) {
	    size= 0;
	} else {
	    size= options.size();
	}

	return size;
    }

    public Object getElementAt( int index) {
        String name;

        if( index < getSize()) {
	    Target o= (Target)options.elementAt( index);

            name= o.localPort + " <--- " + o.ip + ":" + o.port;
	} else {
	    name= null;
	}

	return name;
    }

    public void setData( Vector data) {
	options= data;

	fireContentsChanged( this, 0, data.size());
    }
}


class ListUpdater implements Runnable {
    String localPort, ip, port;
    TargetListModel listModel;
    Vector targets;
    JButton removeTarget;
    
    public ListUpdater( String localPort, String ip, String port,
			TargetListModel listModel, Vector targets,
			JButton removeTarget) {
	this.localPort= localPort;
	this.ip= ip;
	this.port= port;
	this.listModel= listModel;
	this.targets= targets;
	this.removeTarget= removeTarget;
    }
	
     public void run() {
         Target target= new Target( localPort, ip, port);

	 if( !targetExists( localPort, ip, port)) {
     	     targets.addElement( target);
             listModel.setData( targets);
	     removeTarget.setEnabled( true);	     
	 }
    }

    public boolean targetExists( String localPort, String ip, String port) {
	boolean exists= false;
	
	for( int i= 0; i < targets.size(); i++) {
	    Target target= (Target) targets.elementAt( i);

	    if( target.localPort.equals( localPort)
	     && target.ip.equals( ip)
		&& target.port.equals( port)) {		
		exists= true;
	        break;
	    }
	}

	return exists;
    }
}

