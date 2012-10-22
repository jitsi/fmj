package editing.minime;

/*
 * @(#)SelectionPanel.java	1.1 99/08/03
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

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SelectionPanel2 extends JPanel implements ActionListener {

    ActionListener al;
    ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					      "selection");
    JTextField tSMin, tSSec, tSMilli;
    JTextField tEMin, tESec, tEMilli;
    
    public SelectionPanel2(ActionListener al) {
	setLayout( new FlowLayout() );
	Font smallFont = new Font("Dialog", Font.PLAIN, 10);
	JLabel label;
	this.al = al;

	tSMin = new JTextField(2);
	tSMin.setFont(smallFont);
	tSSec = new JTextField(2);
	tSSec.setFont(smallFont);
	tSMilli = new JTextField(3);
	tSMilli.setFont(smallFont);

	
	tEMin = new JTextField(2);
	tEMin.setFont(smallFont);
	tESec = new JTextField(2);
	tESec.setFont(smallFont);
	tEMilli = new JTextField(3);
	tEMilli.setFont(smallFont);

	add(tSMin);
	add(tSSec);
	add(tSMilli);
	JButton button = new JButton("to");
	button.setFont(smallFont);
	button.addActionListener( new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		setStartTimeMillis(5000);
		setStopTimeMillis(12000);
		sendActionEvent();
	    }
	});
	add(button);
	//label = new JLabel(" to ");
	//label.setFont(smallFont);
	//add(label);
	add(tEMin);
	add(tESec);
	add(tEMilli);

	tSMilli.addActionListener( this );
	tSSec.addActionListener( this );
	tSMin.addActionListener( this );
	tEMilli.addActionListener( this );
	tESec.addActionListener( this );
	tEMin.addActionListener( this );
	
    }
    /*
    public void caretPositionChanged(InputMethodEvent ime) {
	//sendActionEvent();
    }

    public void inputMethodTextChanged(InputMethodEvent ime) {
	sendActionEvent();
    }
    */
    public void actionPerformed(ActionEvent ae) {
	sendActionEvent();
    }
    
    public long getStartTimeMillis() {
	long val = 0;
	try {
	    val = Long.parseLong(tSMilli.getText());
	    val += Long.parseLong(tSSec.getText()) * 1000;
	    val += Long.parseLong(tSMin.getText()) * 60000;
	} catch (Throwable t) {
	}
	return val;
    }

    public long getStopTimeMillis() {
	long val = 0;
	try {
	    val = Long.parseLong(tEMilli.getText());
	    val += Long.parseLong(tESec.getText()) * 1000;
	    val += Long.parseLong(tEMin.getText()) * 60000;
	} catch (Throwable t) {
	}
	return val;
    }

    public void setStartTimeMillis(long millis) {
	tSMilli.setText(Long.toString(millis % 1000));
	millis = millis / 1000;
	tSSec.setText(Long.toString(millis % 60));
	millis = millis / 60;
	tSMin.setText(Long.toString(millis));
    }

    public void setStopTimeMillis(long millis) {
	tEMilli.setText(Long.toString(millis % 1000));
	millis = millis / 1000;
	tESec.setText(Long.toString(millis % 60));
	millis = millis / 60;
	tEMin.setText(Long.toString(millis));	
    }

    public void sendActionEvent() {
	if (al != null)
	    al.actionPerformed(actionEvent);
    }
}
