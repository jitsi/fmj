package ejmf.toolkit.media.protocol.nntp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

public class DataSource extends PullDataSource {

    public static final int    NNTP_PORT = 119;
    public static final String GROUP_CMD = "GROUP";
    public static final String BODY_CMD  = "BODY";
    public static final String QUIT_CMD  = "QUIT";

    private Socket socket;

    private String server;
    private String newsgroup;
    private String article;

    private PrintWriter output;
    private InputStream is;
    private DataInputStream input;
    private PullSourceStream[] streams;

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.protocol.DataSource methods
    //
    ////////////////////////////////////////////////////////////

    public void setLocator(MediaLocator locator) {
        String remainder = locator.getRemainder();
        StringTokenizer tokenizer =
            new StringTokenizer(remainder, "/", false);

        try {
            server    = tokenizer.nextToken();
            newsgroup = tokenizer.nextToken();
            article   = tokenizer.nextToken();
        }
        
        catch(NoSuchElementException e) {
            throw new Error(
                "Invalid MediaLocator set on DataSource");
        }

        if( tokenizer.hasMoreTokens() ||
            server    == null || server.length()    == 0 ||
            newsgroup == null || newsgroup.length() == 0 ||
            article   == null || article.length()   == 0 )
        {
            throw new Error(
                "Invalid MediaLocator set on DataSource");
        }

        super.setLocator(locator);
    }

    public String getContentType() {
        initCheck();
        return "text.plain";
    }
    
    public void connect()
        throws IOException
    {
        initCheck();

        if(socket != null) {
            disconnect();
        }

        socket = new Socket(server, NNTP_PORT);

        output =
            new PrintWriter(
                socket.getOutputStream(), true);

        is = socket.getInputStream();

        input =
            new DataInputStream(is);

        verifySuccessful();
        command(GROUP_CMD + " " + newsgroup);
    }

    public void disconnect() {
        initCheck();

        try {
            stop();
        } catch(IOException e) {}
    }

    public void start()
        throws IOException
    {
        initCheck();
        command(BODY_CMD + " " + article);
    }

    public void stop()
        throws IOException
    {
        initCheck();

        if( socket == null ) {
            return;
        }

        command(QUIT_CMD);
        input.close();
        output.close();
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.protocol.PullDataSource methods
    //
    ////////////////////////////////////////////////////////////

    public PullSourceStream[] getStreams() {
        MediaLocator locator = getLocator();

        if( locator == null || is == null ) {
            return null;
        }

        if( streams == null ) {
            streams = new PullSourceStream[1];
            streams[0] =
                new NNTPSourceStream(is);
        }

        return streams;
    }

    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Controls methods
    //
    ////////////////////////////////////////////////////////////

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String controlType) {
        return null;
    }
    
    ////////////////////////////////////////////////////////////
    //
    //  javax.media.Duration methods
    //
    ////////////////////////////////////////////////////////////

    public Time getDuration() {
        return DURATION_UNKNOWN;
    }

    ////////////////////////////////////////////////////////////
    //
    //  ejmf.toolkit.media.protocol.DataSource methods
    //
    ////////////////////////////////////////////////////////////

    private void command(String cmd)
        throws IOException
    {
        output.println(cmd);
        verifySuccessful();
    }

    private void verifySuccessful()
        throws IOException
    {
        String response = input.readLine();

        boolean successful =
            response.startsWith("1") ||
            response.startsWith("2") ||
            response.startsWith("3");

        if(! successful) {
            throw new IOException(response);
        }
    }
}
