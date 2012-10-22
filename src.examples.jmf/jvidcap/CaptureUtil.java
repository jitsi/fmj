/*
 * Copyright (c) 1996-2001 Sun Microsystems, Inc. All Rights Reserved.
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

package jvidcap;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;
import javax.media.control.*;
import java.util.Vector;



public class CaptureUtil  {

    public static DataSource getCaptureDS(VideoFormat vf, AudioFormat af) {
	DataSource dsVideo = null;
	DataSource dsAudio = null;
	DataSource ds = null;

	// Create a capture DataSource for the video
	// If there is no video capture device, then exit with null
	if (vf != null) {
	    dsVideo = createDataSource(vf);
	    if (dsVideo == null)
		return null;
	}
	if (af != null) {
	    dsAudio = createDataSource(af);
	}

	// Create the monitoring datasource wrapper
	if (dsVideo != null) {
	    dsVideo = new MonitorCDS(dsVideo);
	    if (dsAudio == null)
		return dsVideo;
	    ds = dsVideo;
	} else if (dsAudio != null) {
	    return dsAudio;
	} else
	    return null;

	// Merge the data sources, if both audio and video are available
	try {
	    ds = Manager.createMergingDataSource(new DataSource [] {
		dsAudio, dsVideo
	    });
	} catch (IncompatibleSourceException ise) {
	    return null;
	}

	return ds;
    }

    static DataSource createDataSource(Format format) {
	DataSource ds;
	Vector devices;
	CaptureDeviceInfo cdi;
	MediaLocator ml;

	// Find devices for format
	devices = CaptureDeviceManager.getDeviceList(format);
	if (devices.size() < 1) {
	    System.err.println("! No Devices for " + format);
	    return null;
	}
	// Pick the first device
	cdi = (CaptureDeviceInfo) devices.elementAt(0);

	ml = cdi.getLocator();

	try {
	    ds = Manager.createDataSource(ml);
	    ds.connect();
	    if (ds instanceof CaptureDevice) {
		setCaptureFormat((CaptureDevice) ds, format);
	    }
	} catch (Exception e) {
	    System.err.println(e);
	    return null;
	}
	return ds;
    }

    static void setCaptureFormat(CaptureDevice cdev, Format format) {
	FormatControl [] fcs = cdev.getFormatControls();
	if (fcs.length < 1)
	    return;
	FormatControl fc = fcs[0];
	Format [] formats = fc.getSupportedFormats();

	for (int i = 0; i < formats.length; i++) {
	    if (formats[i].matches(format)) {
		format = formats[i].intersects(format);
		System.out.println("Setting format " + format);
		fc.setFormat(format);
		break;
	    }
	}
    }
}
