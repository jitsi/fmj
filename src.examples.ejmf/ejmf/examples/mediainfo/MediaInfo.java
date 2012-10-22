package ejmf.examples.mediainfo;

import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

import ejmf.toolkit.util.StateWaiter;
import ejmf.toolkit.util.Utility;

/**
 * This class is provided as a utility to list attributes of a
 * particular meduim, and to check whether there is a DataSource
 * or Player installed that will support it.  Upon construction,
 * the MediaInfo object will attempt to create a MediaLocator from
 * the given URL, a DataSource from the resulting MediaLocator,
 * and a Player from the resulting DataSource.  References to
 * these attributes (URL, MediaLocator, DataSource, and Player)
 * are maintained within the class, along with protocol and
 * contentType attributes which are defined along the way.
 * <p>
 * If there is an error in any step in this process of creating a
 * Player, then the error string is set, and those attributes which
 * have not yet been set are left as null.  Use getError() to get any
 * error string.
 * <p>
 * Use of this class is twofold.  If run as an application, the
 * printMediaInfo() method will print all known attributes of the
 * given medium, as well as any errors that occurred while creating
 * a player for this medium.  Use this method to see if a
 * protocol is supported and by which DataSource, and if a media
 * format is supported and by which Player.  If there is not a
 * supported Player for the medium, use the contentType attribute
 * to form a package structure to hold your new custrom Player.
 * <p>
 * Use this class also to maintain references to objects created
 * while creating a Player.  Some of these objects may still be
 * necessary beyond the creation of the Player.  Specifically, the
 * DataSource object which is created en route to a Player is not
 * accessible via the JMF API once the Player is created.  Use of
 * this class will allow you to reference the DataSource and its
 * methods.
 *
 * @see            java.net.URL
 * @see            javax.media.MediaLocator
 * @see            javax.media.DataSource
 * @see            javax.media.Player
 * @author         Rob Gordon and Steve Talley
 */
public class MediaInfo {
    private URL url = null;
    private MediaLocator medialocator = null;
    private DataSource datasource = null;
    private Player player = null;
    private String contentType = null;
    private String protocol = null;
    private String error = null;

    /**
     * Construct a MediaInfo object for the given URL.
     *
     * @param          url
     *                 the media URL
     */
    public MediaInfo(URL url) {
        try {
            setURL(url);
        } catch(NoDataSourceException e) {
            error = "Protocol \"" + protocol + "\" not supported";
        } catch(NoPlayerException e) {
            error = "Content type \"" + contentType + "\" not supported";
        } catch(IOException e) {
            error = "Could not connect to media";
        }

        //  Clean up
        try { player.close(); }          catch(Exception e) {}
        try { datasource.disconnect(); } catch(Exception e) {}
    }

    public MediaInfo(MediaLocator locator) {
        try {
            setMediaLocator(locator);
        } catch(NoDataSourceException e) {
            error = "Protocol \"" + protocol + "\" not supported";
        } catch(NoPlayerException e) {
            error = "Content type \"" + contentType + "\" not supported";
        } catch(IOException e) {
            error = "Could not connect to media";
        }

        //  Clean up
        try { player.close(); }          catch(Exception e) {}
        try { datasource.disconnect(); } catch(Exception e) {}
    }

    /**
     * Get the URL attribute of this MediaInfo object
     */
    public URL getURL() { return url; }
    private void setURL(URL url)
        throws
        NoDataSourceException,
        NoPlayerException,
        IOException
    {
        this.url = url;
        setMediaLocator( new MediaLocator(url) );
    }

    /**
     * Get the MediaLocator attribute of this MediaInfo object
     */
    public MediaLocator getMediaLocator() { return medialocator; }
    private void setMediaLocator(MediaLocator medialocator)
        throws
        NoDataSourceException,
        NoPlayerException,
        IOException
    {
        this.medialocator = medialocator;
        protocol = medialocator.getProtocol();
        setDataSource( Manager.createDataSource(medialocator) );
    }
    
    /**
     * Get the DataSource attribute of this MediaInfo object
     */
    public DataSource getDataSource() { return datasource; }
    private void setDataSource(DataSource datasource)
        throws
        NoPlayerException,
        IOException
    {
        this.datasource = datasource;
        contentType = datasource.getContentType();
        setPlayer(Manager.createPlayer(datasource));
    }

    /**
     * Get the Player attribute of this MediaInfo object
     */
    public Player getPlayer() { return player; }
    private void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Get the protocol of this MediaInfo object
     *
     * @return    a String representing the media protocol
     */
    public String getProtocol() { return protocol; }

    /**
     * Get the content type of this MediaInfo object
     *
     * @return    a String representing the media content type
     */
    public String getContentType() { return contentType; }

    /**
     * Get the error attribute of this MediaInfo object
     *
     * @return    a String description of any error that has occurred
     */
    public String getError() { return error; }

    /**
     * Prints the formatted error String to System.out
     */
    protected void printError() {
        System.out.println( "(" + error + ")" );
    }
    
    /**
     * Prints formatted attributes of this MediaInfo object
     * to System.out
     */
    public void printMediaInfo() {
        //  Print out MediaLocator
        System.out.println();
        System.out.print("MediaLocator: ");
        if( medialocator == null ) {
            printError();
        } else {
            System.out.println(medialocator);
        }

        //  Print out protocol
        System.out.println("    Protocol: " + protocol);

        //  Print out DataSource
        System.out.print("  DataSource: ");
        if( datasource == null ) {
            printError();
        } else {
            System.out.println( datasource.getClass().getName() );
        }

        //  Print out content contentType
        System.out.println("Content type: " + contentType);

        //  Print out any stream information
        if( datasource instanceof PullDataSource ) {
            PullSourceStream[] streams = ((PullDataSource)datasource).getStreams();
            for(int i = 0; i < streams.length; i++ ) {
                try {
                    System.out.println("      Stream: " + streams[i].getClass().getName());
                    System.out.println("      Length: " + streams[i].getContentLength());
                } catch(NullPointerException e) {}
            }
        }

        //  Print out Player
        System.out.print("      Player: ");
        if( player == null ) {
            printError();
        } else {
            System.out.println( player.getClass().getName() );
        }

        if( player != null ) {
            new StateWaiter(player).blockingRealize();
            Frame f = new Frame();
            f.add( player.getVisualComponent() );
            f.pack();
            f.setVisible(true);
            player.start();
        }
    }

    /**
     * For each media URL string, constructs a MediaInfo
     * object and calls printMediaInfo().
     *
     * @param          args
     *                 A list of media URL strings
     */
    public static void main(String[] args) {
        for( int i = 0; i < args.length; i++ ) {
            MediaInfo m = new MediaInfo( Utility.appArgToMediaLocator(args[i]) );
            m.printMediaInfo();
        }
    }
}
