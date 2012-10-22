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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SelectionPanel extends JPanel {

    final int HGAP = 10;
    final int BARH = 9;
    final int HANDLEW = HGAP;
    final int HANDLEH = BARH;
    final Color colorBPURPLE = new Color(230, 230, 255);
    final Color colorPURPLE = new Color(196, 196, 255);
    final Color colorDPURPLE = new Color(128, 128, 255);
    final Color colorBORDER = new Color(64, 64, 64);
    final Color colorHandle = Color.red;
    JComponent compBeginHandle;
    JComponent compEndHandle;
    int minPos, maxPos;
    int sizew = 160;
    Dimension preferredSize = new Dimension(sizew, BARH + 2);
    int DRAGMASK = MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK;
    ActionListener al;
    ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
					      "selection");
    
    public SelectionPanel(ActionListener al) {
	setLayout(null);
	this.al = al;
	minPos = HGAP;
	maxPos = sizew - HGAP;
	setOpaque(false);
	setDoubleBuffered(true);
	compBeginHandle = new HandleComp(0);
	compBeginHandle.setSize(HANDLEW, HANDLEH);
	compBeginHandle.setLocation(HGAP - HANDLEW, 1);
	add(compBeginHandle);

	compEndHandle = new HandleComp(1);
	compEndHandle.setSize(HANDLEW, HANDLEH);
	compEndHandle.setLocation(maxPos, 1);
	add(compEndHandle);
    }

    public float getMinPos() {
	Point p = compBeginHandle.getLocation();
	float val = (float) (p.x) / (sizew - (HGAP * 2) - 1);

	return val;
    }

    public float getMaxPos() {
	Point p = compEndHandle.getLocation();
	float val = (float) (p.x - HGAP) / (sizew - (HGAP * 2) - 1);

	return val;
    }

    public void setMinPos(float pos) {
	minPos = (int) (pos * (sizew - (HGAP * 2) - 1)) + HGAP;
	compBeginHandle.setLocation(minPos - HGAP, 1);
    }

    public void setMaxPos(float pos) {
	maxPos = (int) (pos * (sizew - (HGAP * 2) - 1) + HGAP);
	compEndHandle.setLocation(maxPos, 1);
    }

    public void sendActionEvent() {
	if (al != null)
	    al.actionPerformed(actionEvent);
    }

    private void sizeChanged() {
	int nsizew = getSize().width;
	maxPos = ((maxPos - HGAP) * (nsizew - 2 * HGAP)) / (sizew - 2 * HGAP) + HGAP;
	sizew = nsizew;
	compEndHandle.setLocation(maxPos, 1);
    }
    
    public void setSize(int w, int h) {
	super.setSize(w, h);
	sizeChanged();
    }

    public Dimension getPreferredSize() {
	return preferredSize;
    }
    
    public void paint(Graphics g) {
	if (g == null)
	    return;
	g.setColor(colorBORDER);
	g.drawRect(HGAP - 1, 0, sizew - (2 * HGAP) + 1, BARH + 1);
	g.setColor(colorBPURPLE);
	g.drawLine(minPos, 1, minPos, BARH);
	g.drawLine(minPos, 1, maxPos, 1);
	g.setColor(colorDPURPLE);
	g.drawLine(minPos + 1, BARH, maxPos, BARH);
	g.drawLine(maxPos, BARH, maxPos, 2);
	g.setColor(colorPURPLE);
	if (maxPos - minPos - 1 > 0)
	    g.fillRect(minPos + 1, 2, maxPos - minPos - 1, BARH - 2);
	super.paint(g);
    }

    class HandleComp extends JComponent
          implements MouseListener, MouseMotionListener {

	int type;
	int startx, starty;
	boolean inside = false;
	boolean dragging = false;
	
	public HandleComp(int type) {
	    this.type = type;
	    addMouseListener(this);
	    addMouseMotionListener(this);
	}

	public void paint(Graphics g) {
	    if (inside || dragging) {
		g.setColor(colorHandle);
		if (type == 0) {
		    g.drawLine(0, BARH / 2, HGAP - 1, BARH / 2);
		    g.drawLine(0, BARH / 2, 2, BARH / 2 - 2);
		    g.drawLine(0, BARH / 2, 2, BARH / 2 + 2);
		} else {
		    g.drawLine(0, BARH / 2, HGAP - 1, BARH / 2);
		    g.drawLine(HGAP - 1, BARH / 2, HGAP - 1 - 2, BARH / 2 - 2);
		    g.drawLine(HGAP - 1, BARH / 2, HGAP - 1 - 2, BARH / 2 + 2);
		}
	    }
	}
	
	public void mousePressed(MouseEvent me) {
	    if ((me.getModifiers() & DRAGMASK) != 0)
		return;
	    dragging = true;
	    startx = me.getX();
	    starty = me.getY();
	}

	public void mouseClicked(MouseEvent me) {
	}
	
	public void mouseEntered(MouseEvent me) {
	    inside = true;
	    repaint();
	}
	
	public void mouseExited(MouseEvent me) {
	    inside = false;
	    repaint();
	}

	public void mouseReleased(MouseEvent me) {
	    dragging = false;
	    repaint();
	}

	public void mouseDragged(MouseEvent me) {
	    if ((me.getModifiers() & DRAGMASK) != 0)
		return;
	    Point loc = getLocation();
	    loc.x = me.getX() - (startx - loc.x);
	    if (type == 0) {
		if (loc.x < 0) loc.x = 0;
		if (loc.x > maxPos - HGAP - 1) loc.x = maxPos - HGAP - 1;
		minPos = loc.x + HGAP;
	    } else {
		if (loc.x < minPos + 1) loc.x = minPos + 1;
		if (loc.x > sizew - HGAP - 1) loc.x = sizew - HGAP - 1;
		maxPos = loc.x;
	    }
	    setLocation(loc.x, loc.y);
	    getParent().repaint();
	    sendActionEvent();
	}
	
	public void mouseMoved(MouseEvent me) {
	}
    }

    public static void main(String [] args) {
	SelectionPanel selPanel;
	JFrame frame = new JFrame("Test SelectionPanel");
	frame.setSize(320, 240);
	frame.getContentPane().setLayout(new FlowLayout());
	frame.getContentPane().add(selPanel = new SelectionPanel(null));
	frame.setVisible(true);
	selPanel.setMinPos(0f);
	selPanel.setMaxPos(0.5f);
	
    }
}
