package rtp.toolsrx;
/*
 * @(#)Config.java	1.1 01/03/12
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Config {
    private String pathPrefix;

    public Vector targets;

    public Config() {
        pathPrefix= System.getProperty( "user.home") + File.separator;
	    
	read();
    }

    public void read() {
	targets= new Vector();
	
        try {
            String path= pathPrefix + "rx.dat";

	    FileInputStream fin= new FileInputStream( path);
	    BufferedInputStream bin= new BufferedInputStream( fin);
	    DataInputStream din= new DataInputStream( bin);

	    int n_targets= din.readInt();

	    for( int i= 0; i < n_targets; i++) {
		String localPort= readString( din);
		String ip= readString( din);
		String port= readString( din);
		
	        targets.addElement( new Target( localPort, ip, port));		
	    }

            fin.close();	    
	} catch( IOException e) {
	    System.out.println( "xmit.dat file missing!");
	}
     }

    public void write() {
        try {
            String path= pathPrefix + "rx.dat";

            FileOutputStream fout= new FileOutputStream( path);
	    BufferedOutputStream bout= new BufferedOutputStream( fout);
	    DataOutputStream dout= new DataOutputStream( bout);

	    dout.writeInt( targets.size());

	    for( int i= 0; i < targets.size(); i++) {
	        Target target= (Target) targets.elementAt( i);

		writeString( dout, target.localPort);
		writeString( dout, target.ip);
		writeString( dout, target.port);
	    }
	    
	    dout.flush();
            dout.close();
	    fout.close();	    			
	} catch( IOException e) {
	    System.out.println( "Error writing xmit.dat!");
	}
    }

    public String readString( DataInputStream din) {
        String s= null;

        try {
            short length= din.readShort();

            if( length > 0) {
                byte buf[]= new byte[ length];

                din.read( buf, 0, length);

                s= new String( buf);
            }
        } catch( IOException e) {
            System.err.println( e);
        }

        return s;
    }

    public void writeString( DataOutputStream dout, String str) {
	try {
	    if( str != null) {
		dout.writeShort( str.length());
		dout.writeBytes( str);
	    } else {
		dout.writeShort( 0);
	    }
	} catch( Exception e) {
	    e.printStackTrace();
	}
    }
}
  


