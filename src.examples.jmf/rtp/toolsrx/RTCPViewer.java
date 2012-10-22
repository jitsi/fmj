package rtp.toolsrx;

/*
 * @(#)RTCPViewer.java	1.1 01/03/12
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
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class RTCPViewer extends JFrame implements ActionListener, KeyListener,
                                            MouseListener, WindowListener {

    private JList list;
    private Vector reports;
    private RtcpListModel listModel;
    private JButton clear;
    private JButton start;
    private boolean recording;
    
    public RTCPViewer() {
        setTitle( "JMF/RTCP Monitor");

	recording= true;
	
	reports= new Vector();
	
        GridBagLayout gridBagLayout= new GridBagLayout();

	GridBagConstraints gbc;

	JPanel p= new JPanel();
	p.setLayout( gridBagLayout);

	JPanel localPanel= createLocalPanel();
 
	gbc= new GridBagConstraints();
	gbc.gridx= 0;
	gbc.gridy= 0;
	gbc.weightx= 1.0;
	gbc.weighty= 1.0;
	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill= GridBagConstraints.BOTH;
	gbc.insets= new Insets( 10, 5, 0, 0);
	((GridBagLayout)p.getLayout()).setConstraints( localPanel, gbc);
	p.add( localPanel);

        JPanel buttonPanel= new JPanel();
    	
        clear= new JButton( "Clear");
        start= new JButton( "Stop Recording");

	clear.addActionListener( this);
	start.addActionListener( this);
	
	buttonPanel.add( clear);
	buttonPanel.add( start);

	gbc= new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 1;
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

    private JPanel createLocalPanel() {
        JPanel p= new JPanel();

     	GridBagLayout gridBagLayout= new GridBagLayout();

        GridBagConstraints gbc;

       	p.setLayout( gridBagLayout);

	listModel= new RtcpListModel( reports);
	
        list= new JList( listModel);
	
	list.addKeyListener( this);

	list.setPrototypeCellValue( "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

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
	gbc.insets= new Insets( 10, 10, 10, 10);
	((GridBagLayout)p.getLayout()).setConstraints( scrollPane, gbc);
	p.add( scrollPane);

	TitledBorder titledBorder= new TitledBorder( new EtchedBorder(), "RTCP Reports");

	p.setBorder( titledBorder);
	
        return p;
    }

    public void windowClosing( WindowEvent event) {
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
	// int index= list.locationToIndex( e.getPoint());
    }

    public void actionPerformed( ActionEvent event) {
        Object source= event.getSource();

	if( source == clear) {
	    reports= new Vector();
	    listModel.setData( reports);	    
	} else if( source == start) {
	    if( start.getLabel().equals( "Stop Recording")) {
		recording= false;
		start.setLabel( "Start Recording");
	    } else {
		recording= true;
		start.setLabel( "Stop Recording");		
	    }
	}
    }

    public void report( String text) {
	if( recording) {
	    reports.addElement( text);
	    
	    listModel.setData( reports);

	    list.ensureIndexIsVisible( reports.size() - 1);
	}
    }
}

class RtcpListModel extends AbstractListModel {
    private Vector options;

    public RtcpListModel( Vector options) {
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
	    name= (String)options.elementAt( index);
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

