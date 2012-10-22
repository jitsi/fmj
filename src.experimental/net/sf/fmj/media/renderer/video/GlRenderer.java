/*
 *
 */
package net.sf.fmj.media.renderer.video;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.renderer.VideoRenderer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

/**
 * A VideoRenderer that uses the JOGL OpenGL library.
 * 
 * @author Warren Bloomer
 */
public class GlRenderer implements VideoRenderer {

	private static final Logger logger = Logger.getAnonymousLogger();

	/** The descriptive name of this renderer */
	private static final String name = "GL Renderer";

	/** the GL component to render video data onto */
	private GLCanvas canvas;
	//private GLJPanel canvas;

	/** an optional component that may contain the GL component */
	private Component component = null;

	/** an array of supported input video formats */
	private Format[] supportedFormats;

	/** the format of the video input */
	private RGBFormat inputFormat;

	/** a Buffer for a frame of video data. */
	private java.nio.Buffer frameData;

	/** the underlying video data array */
	private Object frameArray;

	private int inWidth = 0;

	private int inHeight = 0;
	
	private Rectangle reqBounds = null;

	private boolean flip = false;

	private float zoomX = 1f;
	
	private float zoomY = 1f;

	/** an object used for synchronizing access to the video data buffer */
	private Object syncObject = new Object();

	/**
	 * Constructor
	 */
	public GlRenderer() {
		//logger.info("GlRenderer constructing...");
		GLCapabilities glCaps = new GLCapabilities();
		glCaps.setDoubleBuffered(true);
		glCaps.setHardwareAccelerated(true);

		canvas = new GLCanvas(glCaps);
		//canvas = new GLJPanel(glCaps);
		
		//canvas.setAutoSwapBufferMode(true);

		canvas.addGLEventListener(new GLEventListener() {
			public void display(GLAutoDrawable drawable) {
				synchronized (syncObject) {
					draw(drawable.getGL());
				}
			}

			public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
			}

			public void init(GLAutoDrawable drawable) {
				drawable.getGL().setSwapInterval(1);
			}

			public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
				synchronized (syncObject) {
					updateScale();
					draw(drawable.getGL());
				}
			}
		});

		// Prepare supported input formats and preferred format

		int rMask = 0x00FF0000;
		int gMask = 0x0000FF00;
		int bMask = 0x000000FF;

		supportedFormats = new VideoFormat[] {
				// 24bit BGR format
				new RGBFormat(null, 
						Format.NOT_SPECIFIED, 
						Format.byteArray, 
						Format.NOT_SPECIFIED, 
						24, 
						3, 2, 1, 
						3, 
						Format.NOT_SPECIFIED, 
						Format.TRUE, 
						Format.NOT_SPECIFIED
				),

				// 32bit RGB format
				new RGBFormat(null, // size
						Format.NOT_SPECIFIED, // maxDataLength
						Format.intArray, // buffer type
						Format.NOT_SPECIFIED, // frame rate
						32, // bitsPerPixel
						rMask, gMask, bMask, // component masks
						1, // pixel stride
						Format.NOT_SPECIFIED, // line stride
						Format.FALSE, // flipped
						Format.NOT_SPECIFIED // endian
				),

				// 32 bit BGR format
				new RGBFormat(null, // size
						Format.NOT_SPECIFIED, // maxDataLength
						Format.intArray, // buffer type
						Format.NOT_SPECIFIED, // frame rate
						32, // bitsPerPixel
						bMask, gMask, rMask, // component masks
						1, // pixel stride
						Format.NOT_SPECIFIED, // line stride
						Format.FALSE, // flipped
						Format.NOT_SPECIFIED // endian
				) 
		};

		// determine whether to flip the image
		String os = System.getProperty("os.name");
		if (os.startsWith("Win") || os.startsWith("win")) {
			flip = true;
		}
		else {
			flip = false;
		}

	}

	/* ---------------------- Controls interface --------------------------- */

	/**
	 * Returns an array of supported controls
	 */
	public Object[] getControls() {
		// No controls
		return (Object[]) new Control[0];
	}

	/**
	 * Return the control based on a control type for the PlugIn.
	 */
	public Object getControl(String controlType) {
		try {
			Class cls = Class.forName(controlType);
			Object controls[] = getControls();
			for (int i = 0; i < controls.length; i++) {
				if (cls.isInstance(controls[i])) {
					return controls[i];
				}
			}
			return null;
		}
		catch (Exception e) { // no such controlType or such control
			return null;
		}
	}

	/* ---------------------- PlugIn implementation ------------------------- */

	public String getName() {
		return name;
	}

	/**
	 * Opens the plugin
	 */
	public void open() throws ResourceUnavailableException {
		//logger.info("Opening...");
	}

	/**
	 * Resets the state of the plug-in. Typically at end of media or when media
	 * is repositioned.
	 */
	public void reset() {
		// Nothing to do
		//logger.info("Resetting...");
	}

	public synchronized void close() {
		//logger.info("Closing...");
	}

	/* ---------------------- Renderer implementation ------------------------------ */

	public void start() {
		//logger.info("Starting...");
		//started = true;
	}

	public void stop() {
		//logger.info("Stopping...");
		//started = false;
	}

	/**
	 * Lists the possible input formats supported by this plug-in.
	 */
	public Format[] getSupportedInputFormats() {
		return supportedFormats;
	}

	/**
	 * Set the data input format.
	 */
	public Format setInputFormat(Format format) {
		//logger.info("Setting input format: " + format);

		if (format != null && format instanceof RGBFormat) {
			for (int i = 0; i < supportedFormats.length; i++) {
				if (format.matches(supportedFormats[i])) {

					this.inputFormat = (RGBFormat) format;
					Dimension size = inputFormat.getSize();

					if (size != null) {
						inWidth = size.width;
						inHeight = size.height;
						updateScale();
					}
					return format;
				}
			}
		}
		else {
			// TODO handle YUV and use 3D texture map for conversion
		}

		//logger.info("Unsupported format: " + format);
		return null;
	}

	/**
	 * Processes the data and renders it to a component
	 */
	public int process(Buffer buffer) {
		//logger.info("process...");

		if (buffer == null || buffer.getLength() <= 0) {
			//logger.info("zero length buffer");
			return BUFFER_PROCESSED_OK;
		}

		Format format = buffer.getFormat();
		if (format instanceof VideoFormat) {
			Dimension size = ((VideoFormat) format).getSize();
			if ((size != null) && (size.width != inWidth || size.height != inHeight)) {
				inWidth = size.width;
				inHeight = size.height;
				updateScale();
			}
		}

		synchronized (syncObject) {
			Object data = buffer.getData();
			if (data == null) {
				return BUFFER_PROCESSED_FAILED;
			}

			frameArray = buffer.getData();
			if (format.getDataType() == Format.byteArray) {
				frameData = ByteBuffer.wrap((byte[]) frameArray);
			}
			else if (format.getDataType() == Format.intArray) {
				frameData = IntBuffer.wrap((int[]) frameArray);
			}
			else {
				return BUFFER_PROCESSED_FAILED;
			}

			/*
			if (format.getDataType() == Format.byteArray) {
				if (frameArray == null || ((byte[])frameArray).length < buffer.getLength() ) {
					frameArray = new byte[buffer.getLength()];
					frameData = ByteBuffer.wrap((byte[]) frameArray);
				}
			}
			else if (format.getDataType() == Format.intArray) {
				if (frameArray == null || ((int[])frameArray).length < buffer.getLength() ) {
					frameArray = new int[buffer.getLength()];
					frameData = IntBuffer.wrap((int[]) frameArray);
				}
			}
			else {
				return BUFFER_PROCESSED_FAILED;
			}

			// copy buffer data to the array that is wrapped in a nio.Buffer
			System.arraycopy(data, 0, frameArray, 0, buffer.getLength());
			*/
		}

		// repaint the canvas
		canvas.repaint();

		return BUFFER_PROCESSED_OK;
	}

	/* ---------------------- VideoRenderer implementation ----------------------------- */

	/**
	 * Returns an AWT component that it will render to. Returns null
	 * if it is not rendering to an AWT component.
	 */
	public java.awt.Component getComponent() {
		//logger.info("Getting component.  component=" + component + ". canvas=" + canvas);
		if (component != null) {
			return component;
		}
		else {
			return canvas;
		}
	}

	/**
	 * Requests the renderer to draw into a specified AWT component.
	 * Returns false if the renderer cannot draw into the specified
	 * component.
	 */
	public boolean setComponent(java.awt.Component comp) {
		//logger.info("Setting component: " + comp);

		if (comp instanceof Container) {
			((Container) comp).setLayout(new BorderLayout());
			((Container) comp).add(canvas, BorderLayout.CENTER);
			this.component = comp;
			return true;
		}
		else {
			this.component = null;
			return false;
		}
	}

	/**
	 * Sets the region in the component where the video is to be
	 * rendered to. Video is to be scaled if necessary. If <tt>rect</tt>
	 * is null, then the video occupies the entire component.
	 */
	public void setBounds(java.awt.Rectangle rect) {
		reqBounds = rect;
	}

	/**
	 * Returns the region in the component where the video will be
	 * rendered to. Returns null if the entire component is being used.
	 */
	public java.awt.Rectangle getBounds() {
		return reqBounds;
	}

	/* --------------------- private methods ---------------------- */

	/**
	 * Draw the scene.
	 */
	private void draw(GL gl) {
		if (inputFormat != null) {
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();

			// draw the image - may use display list
			drawImage(gl);

			// pop perspective
			gl.glPopMatrix();
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPopMatrix();

		}
	}

	private void drawImage(GL gl) {
		//logger.info("drawing video image");

		if (frameData == null) {
			return;
		}

		int rgbFormat = GL.GL_RGB;
		int dataType = GL.GL_UNSIGNED_BYTE;
		
		if (inputFormat.getDataType() == Format.byteArray) {
			dataType = GL.GL_UNSIGNED_BYTE;
		}
		else if (inputFormat.getDataType() == Format.intArray) {
			dataType = GL.GL_UNSIGNED_INT_8_8_8_8_REV;
		}
		
		switch (inputFormat.getBitsPerPixel()) {
			case 24:
				if (inputFormat.getRedMask() > 1) {
					rgbFormat = GL.GL_BGR;
				}
				else {
					rgbFormat = GL.GL_RGB;
				}
				break;
				
			case 32:
				if (inputFormat.getRedMask() > 0xFF) {
					rgbFormat = GL.GL_BGRA;
				}
				else {
					rgbFormat = GL.GL_RGBA;
				}
				break;
		}

		gl.glPixelZoom(zoomX, zoomY);
		gl.glRasterPos2i(-1, 1);
		gl.glDrawPixels(inWidth, inHeight, rgbFormat, dataType, frameData);
	}
	
	private void updateScale() {
		if (inputFormat != null) {
			//logger.info("Updating scale...");

			Dimension preferredSize = new Dimension(inWidth, inHeight);
			canvas.setPreferredSize(preferredSize);

			zoomX = (float)canvas.getWidth()/(float)inWidth;
			zoomY = -(float)canvas.getHeight()/(float)inHeight;
		}
	}
}
