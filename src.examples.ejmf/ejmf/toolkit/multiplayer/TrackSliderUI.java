
package ejmf.toolkit.multiplayer;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
* L&F for TrackSlider
*/
public class TrackSliderUI extends BasicSliderUI 
{
	
	/**
	 * Added so at least it will compile.  KAL.
	 * @deprecated
	 * 
	 */
	private Rectangle getScrollTrackRect()
	{	throw new UnsupportedOperationException();	// TODO: no longer in Java - KAL
	}
	/**
	 * Added so at least it will compile.  KAL.
	 * @deprecated
	 * 
	 */
	private Rectangle getThumbRect()
	{	throw new UnsupportedOperationException();	// TODO: no longer in Java - KAL
	}
	/**
	 * Added so at least it will compile.  KAL.
	 * @deprecated
	 * 
	 */	
	private void setThumbBounds(int a, int b, int c, int d)
	{
		throw new UnsupportedOperationException();	// TODO: no longer in Java - KAL
	}
	private TrackListener	trackListener;
    private boolean		isDragging;

	/**
	* Create UI for UIManager
	* @return A ComponentUI
	* @see javax.swing.plaf.ComponentUI
	*/
    public static ComponentUI createUI(JComponent c)    {
        return new TrackSliderUI((JSlider) c);
    } 

	/** Create TrackSliderUI
	*/
    public TrackSliderUI(JSlider s)   {
	super(s);
    }

	/**
	* Calculate the bounds of the slider bounds
	*/
    public void calculateThumbBounds()	{
	setThumbBounds(xPositionForValue(slider.getValue()),
		 	getScrollTrackRect().y, 
			xPositionForValue(slider.getExtent()), 
			getScrollTrackRect().height);
    }

	/**
	* Install UI
	* @param c JComponent depicting UI
	* 
	* @see javax.swing.plaf.ComponentUI
	*/
    public void installUI(JComponent c) {
	super.installUI(c);
 	slider = (JSlider) c;	
	
	// Install my own mouse listeners
 	trackListener = new TrackListener();
	slider.addMouseListener(trackListener);
	slider.addMouseMotionListener(trackListener);
    }

	/**
	* Uninstall UI
	* @param c JComponent depicting UI
	* 
	* @see javax.swing.plaf.ComponentUI
	*/
    public void uninstallUI(JComponent c) {
	super.uninstallUI(c);
	slider.removeMouseListener(trackListener);
	slider.removeMouseMotionListener(trackListener);
    }

	/** 
	* @return the JSlider used to display this TrackSliderUI
	*/
    protected JSlider getSlider() {
	return slider;
    }

    protected Timer getScrollTimer() {
	return scrollTimer;
    }

    protected int getTrackBuffer() {
	return trackBuffer;
    }
	
    protected class TrackListener extends MouseAdapter 
			implements MouseMotionListener, Serializable {

	protected int 	offset;	
	protected int	currentMouseX, currentMouseY;
     
  	public void mouseMoved(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
	    if(!getSlider().isEnabled())
     		return;

	    currentMouseX = e.getX();
	    currentMouseY = e.getY();

	    getSlider().requestFocus();

	    if(getThumbRect().contains(currentMouseX, currentMouseY)) {
         	offset = currentMouseX - getThumbRect().x;
     	    }
     	    isDragging = true;
            getSlider().setValueIsAdjusting(true);
     	    return;
	}

      	public void mouseDragged(MouseEvent e) {
 	    Rectangle scrollTrackRect = getScrollTrackRect();
    
 	    if(!getSlider().isEnabled())
     	        return;

 	    currentMouseX = e.getX();
 	    currentMouseY = e.getY();

 	    if(!isDragging)
     	        return;

	    int thumbLeft = e.getX() - offset;
	    int trackLeft = scrollTrackRect.x + getTrackBuffer();
	    int trackRight = (scrollTrackRect.x + 
				(scrollTrackRect.width - 1)) - getTrackBuffer();

	    thumbLeft = Math.max( thumbLeft, trackLeft);
	    thumbLeft = Math.min( thumbLeft, trackRight);

	    setThumbLocation( thumbLeft, getThumbRect().y);

	    getSlider().setValue( valueForXPosition( thumbLeft ) );
	} 
    
        public void mouseReleased(MouseEvent e) {
	    if(!getSlider().isEnabled())
		return;

  	    offset = 0;
  	    getScrollTimer().stop();
	}
    }

    public int valueForXPosition( int xPos ) {
	    JSlider slider = getSlider();
            int value;
            int minValue = slider.getMinimum();
            int maxValue = slider.getMaximum();
            Rectangle trackRect = getScrollTrackRect();
            int trackLength = trackRect.width - (getTrackBuffer() * 2);
            int trackLeft = trackRect.x + getTrackBuffer();
            int trackRight = (trackRect.x + (trackRect.width - 1)) - 
				getTrackBuffer();

            if ( xPos <= trackLeft ) {
                value = slider.getInverted() ? maxValue : minValue;
            }
            else if ( xPos >= trackRight ) {
                value = slider.getInverted() ? minValue : maxValue;
            }
            else {
                int distanceFromTrackLeft = xPos - trackLeft;
                int valueRange = maxValue - minValue;
                double valuePerPixel = (double)valueRange / (double)trackLength;
                int valueFromTrackLeft = (int)Math.round( distanceFromTrackLeft
* valuePerPixel );

               value = slider.getInverted() ? maxValue - valueFromTrackLeft :
                                              minValue + valueFromTrackLeft;
           }

           return value;
       }
}
