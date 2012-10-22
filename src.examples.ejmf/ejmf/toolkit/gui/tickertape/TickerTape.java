package ejmf.toolkit.gui.tickertape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

/**
 * The Tickertape class is a generic tickertape that slowly
 * scrolls text across the viewable window.  The TickerTape class
 * provides a the basis of the Multi-Image Player.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class TickerTape extends Panel implements Runnable
{
    //  Default values
    private static final int   MAXLENGTH             = 500;
    private static final int   OVERLAP               = 2;
    private static final int   _defaultRate          = 1;
    private static final int   _defaultShadowXOffset = 4;
    private static final int   _defaultShadowYOffset = 2;
    private static final Color _defaultBackground    = Color.white;
    private static final Color _defaultForeground    = new Color(128,128,200);
    private static final Font  _defaultFont          =
        new Font("Dialog", Font.BOLD | Font.ITALIC, 78);

    //  Properties
    private String message = "";
    private Color shadow;
    private int rate;
    private int shadowX;
    private int shadowY;
    private boolean shadowEnabled;
    private boolean loop;
    private Thread playerThread;

    private boolean invalidXOffset = true;
    private boolean invalidYOffset = true;
    private boolean newImageNeeded;
    private boolean newMetricsNeeded;
    private Image stringImage;
    private int xoffset;
    private int yoffset;
    private int stringHeight;
    private int stringWidth;
    private int stringAscent;
    private int stringDescent;

    /**
     * Constructs a TickerTape to display the given message.
     */
    public TickerTape(String message) {
        super();

        setMessage(message);
        setRate(_defaultRate);
        setBackground(_defaultBackground);
        setForeground(_defaultForeground);
        setShadow(_defaultForeground.darker().darker().darker());
        setShadowXOffset(_defaultShadowXOffset);
        setShadowYOffset(_defaultShadowYOffset);
        setShadowEnabled(true);
        setFont(_defaultFont);
        setLoop(true);
    }

    /**
     * Set the message to be displayed by this TickerTape.
     */
    public void setMessage(String newMessage) {
        synchronized(message) {
            message = printable(newMessage);
            if( message.length() > MAXLENGTH ) {
                message = message.substring(0,MAXLENGTH);
            }
        }
        invalidateMetrics();
    }

    /**
     * Append the given message to the current message being
     * displayed in this TickerTape.
     */
    public void appendMessage(String message) {
        setMessage(this.message + message);
    }

    /**
     * Get the message being displayed in this TickerTape.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the number of pixels ofsetting the message from it's
     * shadow on the X-plain.
     */
    public void setShadowXOffset(int shadowX) {
        this.shadowX = shadowX;
        invalidateMetrics();
    }

    /**
     * Gets the number of pixels ofsetting the message from it's
     * shadow on the X-plain.
     */
    public int getShadowXOffset() {
        return shadowX;
    }

    /**
     * Set the number of pixels ofsetting the message from it's
     * shadow on the Y-plain.
     */
    public void setShadowYOffset(int shadowY) {
        this.shadowY = shadowY;
        invalidateMetrics();
    }

    /**
     * Gets the number of pixels ofsetting the message from it's
     * shadow on the X-plain.
     */
    public int getShadowYOffset() {
        return shadowY;
    }

    /**
     * Set the color of the shadow.
     */
    public void setShadow(Color shadow) {
        this.shadow = shadow;
        invalidateImage();
    }

    /**
     * Get the color of the shadow.
     */
    public Color getShadow() {
        return shadow;
    }

    /**
     * Enable/disable the display of the TickerTape's shadow.
     */
    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
        invalidateImage();
    }

    /**
     * Gets the status of the TickerTape's shadow.
     */
    public boolean getShadowEnabled() {
        return shadowEnabled;
    }

    /**
     * Enable/disable the looping of the TickerTape's text.  If
     * true, the message will be repeatedly replayed once it has
     * been shown.
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Returns a boolean indicating whether the TickerTape's
     * message will loop.
     */
    public boolean getLoop() {
        return loop;
    }

    /**
     * Sets the rate for this TickerTape.
     */
    public void setRate(int rate) {
        this.rate = rate;
    }
    
    /**
     * Gets the rate for this TickerTape.
     */
    public int getRate() {
        return rate;
    }

    /**
     * Sets the font of this TickerTape.
     */
    public void setFont(Font f) {
        super.setFont(f);
        invalidateMetrics();
    }

    /**
     * Gets the font of this TickerTape.
     */
    public void setBackground(Color c) {
        super.setBackground(c);
        invalidateImage();
    }

    /**
     * Sets the text color of this TickerTape.
     */
    public void setForeground(Color c) {
        super.setForeground(c);
        invalidateImage();
    }

    /**
     * Forces the message image to be reconstructed within the
     * TickerTape.
     */
    public void invalidateImage() {
        newImageNeeded = true;
    }

    /**
     * Forces the message image to be reconstructed within the
     * TickerTape.
     */
    public void invalidateMetrics() {
        newMetricsNeeded = true;
        invalidateImage();
    }

    private void calculateInitialXOffset() {
        if( rate > 0 ) {
            xoffset = getSize().width;
        } else {
            xoffset = -stringWidth;
        }
    }

    private void calculateInitialYOffset() {
        yoffset = ( getSize().height - stringHeight ) / 2;
    }

    private void calculateSize() {
        newMetricsNeeded = false;

        FontMetrics fontMetrics = getFontMetrics(getFont());
        stringAscent = fontMetrics.getAscent();
        stringDescent = fontMetrics.getDescent();
        stringWidth = fontMetrics.stringWidth(message) + shadowX;
        stringHeight = stringAscent + stringDescent + shadowY;

        //  Set initial yoffset
        calculateInitialYOffset();
    }

    private void createStringImage() {
        newImageNeeded = false;

        //  Recalculate size of image?
        if( newMetricsNeeded ) {
            calculateSize();
        }
        
        stringImage = createImage(stringWidth, stringHeight);
        Graphics graphics = stringImage.getGraphics();

        //  Draw shadow
        if( shadowEnabled ) {
            graphics.setColor(getShadow());
            graphics.drawString(message, shadowX, stringAscent + shadowY);
        }

        //  Draw foreground
        graphics.setColor(getForeground());
        graphics.drawString(message, 0, stringAscent);
    }

    /**
     * Start the TickerTape.
     */
    public synchronized void start() {
        if( playerThread == null ||
           !playerThread.isAlive() )
        {
            playerThread = new Thread(this);
            playerThread.start();
        }
    }

    /**
     * Stop the TickerTape.
     */
    public void stop() {
        if( playerThread != null &&
            playerThread.isAlive() )
        {
            playerThread.stop();
        }
    }

    //  The run() method and the paint() method are both
    //  synchronized so that run() will wait for the paint() to
    //  occur when it calls repaint().  Otherwise, xoffset will
    //  change dramatically between calls to paint().

    /**
     * Called when the TickerTape is started with start().  This
     * method should not be called directly.
     */
    public synchronized void run() {
        while (true) {
            //  Check for xoffset out of bounds
            if( xoffset < -stringWidth || xoffset > getSize().width ) {
                if(loop) {
                    calculateInitialXOffset();
                } else {
                    break;
                }
            }

            xoffset -= (rate * 2);

            //  Schedule a repaint
            repaint();

            //  Depending on how fast paint() is queued and
        	//  called, the animation could be quite
        	//  choppy.  Sleep here for a short time so that
        	//  paint() is always called immediately
        	//  following, resulting in smooth animation.

            try { Thread.sleep(30); }
            catch (InterruptedException e) {}

            //  Block until paint has completed
            try { wait(); }
            catch(InterruptedException e) {}
        }
    }

    /**
     * Paint the TickerTape.
     */
    public synchronized void paint(Graphics g) {
        if( newImageNeeded ) {
            if( stringImage == null ) {
                createStringImage();
            } else {
                int width = stringImage.getWidth(this);
                int height = stringImage.getHeight(this);
                int x = xoffset;
                int y = yoffset;
                int overlap = Math.abs(rate) + OVERLAP;

                createStringImage();

                //  Clear the old image
                g.clearRect(x - overlap, y, width + 2 * overlap, height);
            }
        } else {
            //  Clear only the trailing pixels from the last image
            if( rate > 0 ) {
                g.clearRect(xoffset + stringWidth, yoffset, 2*rate + OVERLAP, stringHeight);
            } else {
                g.clearRect(xoffset + 2*rate - OVERLAP, yoffset, OVERLAP - 2*rate, stringHeight);
            }
        }

        if( invalidXOffset ) {
            invalidXOffset = false;
            calculateInitialXOffset();
        }

        if( invalidYOffset ) {
            invalidYOffset = false;
            calculateInitialYOffset();
        }

        //  Don't draw string if the tickertape has never
        //  been started.  The best way to check this is
        //  with invalidXOffset

        if( ! invalidXOffset ) {
            g.drawImage(stringImage, xoffset, yoffset, this);
        }

        //  Wake up run()
        notifyAll();
    }

    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Returns the preferred size of the TickerTape.  By default,
     * it is 4h x h, where h is the height of the TickerTape's
     * font.
     */
    public Dimension getPreferredSize() {
        //  Recalculate preferred size?
        if( newMetricsNeeded ) {
            calculateSize();
        }
        return new Dimension(stringHeight*4, stringHeight);
    }

    public void invalidate() {
        super.invalidate();
        invalidYOffset = true;
    }

    /**
     * Converts all control characters of a String to spaces.
     *
     * @param      convert
     *             The String to convert.
     *
     * @return     The converted String.
     */
    public static String printable(String convert) {
        char[] c = convert.toCharArray();
        for(int i = 0; i < c.length; i++ ) {
            if( Character.isISOControl(c[i]) ) c[i] = ' ';
        }
        return new String(c);
    }
}
