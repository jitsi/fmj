package rtp.rtpconnector;
/*
 * @(#)RTPSocketAdapter.java	1.2 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
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


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;


/**
 * An implementation of RTPConnector based on UDP sockets.
 */
public class RTPSocketAdapter implements RTPConnector {

    DatagramSocket dataSock;
    DatagramSocket ctrlSock;

    InetAddress addr;
    int port;

    SockInputStream dataInStrm = null, ctrlInStrm = null;
    SockOutputStream dataOutStrm = null, ctrlOutStrm = null;


    public RTPSocketAdapter(InetAddress addr, int port) throws IOException {
	this(addr, port, 1);
    }

    public RTPSocketAdapter(InetAddress addr, int port, int ttl) throws IOException {

	try {

	    if (addr.isMulticastAddress()) {
		dataSock = new MulticastSocket(port);
		ctrlSock = new MulticastSocket(port+1);
		((MulticastSocket)dataSock).joinGroup(addr);
		((MulticastSocket)dataSock).setTimeToLive(ttl);
		((MulticastSocket)ctrlSock).joinGroup(addr);
		((MulticastSocket)ctrlSock).setTimeToLive(ttl);
	    } else {
		dataSock = new DatagramSocket(port, InetAddress.getLocalHost());
		ctrlSock = new DatagramSocket(port+1, InetAddress.getLocalHost());
	    }


	} catch (SocketException e) {
	    throw new IOException(e.getMessage());
	}

	this.addr = addr;
	this.port = port;
    }

    /**
     * Returns an input stream to receive the RTP data. 
     */
    public PushSourceStream getDataInputStream() throws IOException {
	if (dataInStrm == null) {
	    dataInStrm = new SockInputStream(dataSock, addr, port);
	    dataInStrm.start();
	}
	return dataInStrm;
    }

    /**
     * Returns an output stream to send the RTP data.
     */
    public OutputDataStream getDataOutputStream() throws IOException {
	if (dataOutStrm == null)
	    dataOutStrm = new SockOutputStream(dataSock, addr, port);
	return dataOutStrm;
    }

    /**
     * Returns an input stream to receive the RTCP data.
     */
    public PushSourceStream getControlInputStream() throws IOException {
	if (ctrlInStrm == null) {
	    ctrlInStrm = new SockInputStream(ctrlSock, addr, port+1);
	    ctrlInStrm.start();
	}
	return ctrlInStrm;
    }

    /**
     * Returns an output stream to send the RTCP data.
     */
    public OutputDataStream getControlOutputStream() throws IOException {
	if (ctrlOutStrm == null)
	    ctrlOutStrm = new SockOutputStream(ctrlSock, addr, port+1);
	return ctrlOutStrm;
    }

    /**
     * Close all the RTP, RTCP streams.
     */
    public void close() {
	if (dataInStrm != null)
	    dataInStrm.kill();
	if (ctrlInStrm != null)
	    ctrlInStrm.kill();
	dataSock.close();
	ctrlSock.close();
    }

    /**
     * Set the receive buffer size of the RTP data channel.
     * This is only a hint to the implementation.  The actual implementation
     * may not be able to do anything to this.
     */
    public void setReceiveBufferSize( int size) throws IOException {
	dataSock.setReceiveBufferSize(size);
    }

    /**
     * Get the receive buffer size set on the RTP data channel.
     * Return -1 if the receive buffer size is not applicable for 
     * the implementation.
     */
    public int getReceiveBufferSize() {
	try {
	    return dataSock.getReceiveBufferSize();
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Set the send buffer size of the RTP data channel.
     * This is only a hint to the implementation.  The actual implementation
     * may not be able to do anything to this.
     */
    public void setSendBufferSize( int size) throws IOException {
	dataSock.setSendBufferSize(size);
    }

    /**
     * Get the send buffer size set on the RTP data channel.
     * Return -1 if the send buffer size is not applicable for 
     * the implementation.
     */
    public int getSendBufferSize() {
	try {
	    return dataSock.getSendBufferSize();
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Return the RTCP bandwidth fraction.  This value is used to
     * initialize the RTPManager.  Check RTPManager for more detauls.
     * Return -1 to use the default values.
     */
    public double getRTCPBandwidthFraction() {
	return -1;
    }

    /**
     * Return the RTCP sender bandwidth fraction.  This value is used to
     * initialize the RTPManager.  Check RTPManager for more detauls.
     * Return -1 to use the default values.
     */
    public double getRTCPSenderBandwidthFraction() {
	return -1;
    }


    /**
     * An inner class to implement an OutputDataStream based on UDP sockets.
     */
    class SockOutputStream implements OutputDataStream {

	DatagramSocket sock;
	InetAddress addr;
	int port;

	public SockOutputStream(DatagramSocket sock, InetAddress addr, int port) {
	    this.sock = sock;
	    this.addr = addr;
	    this.port = port;
	}

	public int write(byte data[], int offset, int len) {
	    try {
		sock.send(new DatagramPacket(data, offset, len, addr, port));
	    } catch (Exception e) {
		return -1;
	    }
	    return len;
	}
    }


    /**
     * An inner class to implement an PushSourceStream based on UDP sockets.
     */
    class SockInputStream extends Thread implements PushSourceStream {

	DatagramSocket sock;
	InetAddress addr;
	int port;
	boolean done = false;
	boolean dataRead = false;

	SourceTransferHandler sth = null;

	public SockInputStream(DatagramSocket sock, InetAddress addr, int port) {
	    this.sock = sock;
	    this.addr = addr;
	    this.port = port;
	}

	public int read(byte buffer[], int offset, int length) {
	    DatagramPacket p = new DatagramPacket(buffer, offset, length, addr, port);
	    try {
		sock.receive(p);
	    } catch (IOException e) {
		return -1;
	    }
	    synchronized (this) {
		dataRead = true;
		notify();
	    }
	    return p.getLength();
	}

	public synchronized void start() {
	    super.start();
	    if (sth != null) {
		dataRead = true;
		notify();
	    }
	}

	public synchronized void kill() {
	    done = true;
	    notify();
	}

	public int getMinimumTransferSize() {
	    return 2 * 1024;	// twice the MTU size, just to be safe.
	}

	public synchronized void setTransferHandler(SourceTransferHandler sth) {
	    this.sth = sth;
	    dataRead = true;
	    notify();
	}

	// Not applicable.
	public ContentDescriptor getContentDescriptor() {
	    return null;
	}

	// Not applicable.
	public long getContentLength() {
	    return LENGTH_UNKNOWN;
	}

	// Not applicable.
	public boolean endOfStream() {
	    return false;
	}

	// Not applicable.
	public Object[] getControls() {
	    return new Object[0];
	}

	// Not applicable.
	public Object getControl(String type) {
	    return null;
	}

	/**
	 * Loop and notify the transfer handler of new data.
	 */
	public void run() {
	    while (!done) {

		synchronized (this) {
		    while (!dataRead && !done) {
			try {
			    wait();
			} catch (InterruptedException e) { }
		    }
		    dataRead = false;
		}

		if (sth != null && !done) {
		    sth.transferData(this);
		}
	    }
	}
    }
}

