package net.sf.fmj.media.protocol.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.media.Duration;
import javax.media.Time;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

/**
 * 
 * adapted from http://java.sun.com/products/java-media/jmf/2.1.1/guide/JMFApp-DataSource.html#82808
 *
 */
public class DataSource extends PullDataSource
 {
    public static final int FTP_PORT = 21;
    public static final int FTP_SUCCESS = 1;
    public static final int FTP_TRY_AGAIN = 2;
    public static final int FTP_ERROR = 3;
 
    // used to send commands to server
    protected Socket controlSocket;   

           
    // used to receive file
    protected Socket dataSocket;     
    // wraps controlSocket's output stream
    protected PrintStream controlOut;     
        
    // wraps controlSocket's input stream
    protected InputStream controlIn;   
        
    // hold (possibly multi-line) server response
    protected Vector response = new Vector(1);  
     
    // reply code from previous command
    protected int previousReplyCode;       
    
    // are we waiting for command reply?
    protected boolean replyPending;   
            
    // user login name
     protected String user = "anonymous"; 
         
    // user login password
    protected String password = "anonymous";  
    
    // FTP server name
    protected String hostString;         
      
    // file to retrieve
    protected String fileString;           
 
    public void connect() throws IOException
    {
       initCheck();  // make sure the locator is set
       if (controlSocket != null)
       {
          disconnect();
       }
       // extract FTP server name and target filename from locator
       parseLocator();   
       controlSocket = new Socket(hostString, FTP_PORT);
       controlOut = new PrintStream(new BufferedOutputStream(
          controlSocket.getOutputStream()), true);
       controlIn = new
          BufferedInputStream(controlSocket.getInputStream());
     
      if (readReply() == FTP_ERROR)
       {
           throw new IOException("connection failed");
       }
       
       if (issueCommand("USER " + user) == FTP_ERROR)
       {
          controlSocket.close();

          
          throw new IOException("USER command failed");
       }
 
       if (issueCommand("PASS " + password) == FTP_ERROR)
       {
          controlSocket.close();
          throw new IOException("PASS command failed");
       }
    }
 
    public void disconnect()
    {
       if (controlSocket == null)
       {
          return;
       }
 
       try
       {
          issueCommand("QUIT");
          controlSocket.close();
       }
 
       catch (IOException e) 
       {
          // do nothing, we just want to shutdown
       }
 
       controlSocket = null;
       controlIn = null;
       controlOut = null;
    }
 
     public void start() throws IOException
     {
          ServerSocket serverSocket;
          InetAddress myAddress = InetAddress.getLocalHost();
          byte[] address = myAddress.getAddress();
 
          String portCommand = "PORT ";
          serverSocket = new ServerSocket(0, 1);
 
          // append each byte of our address (comma-separated)
 
          for (int i = 0; i < address.length; i++)
          {
             portCommand = portCommand + (address[i] & 0xFF) + ",";
          }
 
       // append our server socket's port as two comma-separated
       // hex bytes

          portCommand = portCommand +
             ((serverSocket.getLocalPort() >>> 8) 
             & 0xFF) + "," + (serverSocket.getLocalPort() & 0xFF);
 
          // issue PORT command
          if (issueCommand(portCommand) == FTP_ERROR)
          {
             serverSocket.close();
             throw new IOException("PORT");
          }
 
          // issue RETRieve command
          if (issueCommand("RETR " + fileString) == FTP_ERROR)
          {
             serverSocket.close();
             throw new IOException("RETR");
          }
 
          dataSocket = serverSocket.accept();
          serverSocket.close();
    }
    public void stop()
    {
       try
       {
       // issue ABORt command
       issueCommand("ABOR");
       dataSocket.close();
       }
       catch(IOException e) {}
    }
 
    public String getContentType()
    {
     // We don't get MIME info from FTP server.  This
     // implementation makes an attempt guess the type using
     // the File name and returns "unknown" in the default case.
     // A more robust mechanisms should
     // be supported for real-world applications.
 
       String locatorString = getLocator().toExternalForm();
       int dotPos = locatorString.lastIndexOf(".");
       String extension = locatorString.substring(dotPos + 1);
       String typeString = "unknown";
 
       if (extension.equals("avi"))
          typeString = "video.x-msvideo";
       else if (extension.equals("mpg") ||     
          extension.equals("mpeg"))
          typeString = "video.mpeg";
       else if (extension.equals("mov"))
          typeString = "video.quicktime";

       else if (extension.equals("wav"))
          typeString = "audio.x-wav";
       else if (extension.equals("au"))
          typeString = "audio.basic";
       return typeString;
    }
 
    public PullSourceStream[] getStreams()
    {
       PullSourceStream[] streams = new PullSourceStream[1];
       try
       {
          streams[0] = new FTPSourceStream(dataSocket.getInputStream());
       }
 
       catch(IOException e)
       {
          System.out.println("error getting streams");
       }
       return streams;
    }
 
 
 
    public Time getDuration()
    {
       return Duration.DURATION_UNKNOWN;
    }
 
    public void setUser(String user)
 
    {
       this.user = user;
    }
 
    public String getUser()
    {
       return user;
    }
 
    public void setPassword(String password)
    {
       this.password = password;
    }
 
    public String getPassword()
    {
       return password;
    }

    private int readReply() throws IOException
    {
       previousReplyCode = readResponse();
       System.out.println(previousReplyCode);
       switch (previousReplyCode / 100)
       {
          case 1:
             replyPending = true;
             // fall through
          case 2:
          case 3:
             return FTP_SUCCESS;
          case 5:
             if (previousReplyCode == 530)
             {
                if (user == null)
                {
                   throw new IOException("Not logged in");
                }
                return FTP_ERROR;
             }
             if (previousReplyCode == 550)
             {
                throw new FileNotFoundException();
             }
       }
       return FTP_ERROR;
     }
 
    /**
     * Pulls the response from the server and returns the code as a
     * number. Returns -1 on failure.
     */
 
    private int readResponse() throws IOException
    {
       StringBuffer buff = new StringBuffer(32);
       String responseStr;
       int   c;
       int   continuingCode = -1;
       int   code = 0;
 
       response.setSize(0);
       
       while (true)
       {
          while ((c = controlIn.read()) != -1)
          {
             if (c == '\r')
             {
                if ((c = controlIn.read()) != '\n')
                {
                   buff.append('\r');

                }
             }
             buff.append((char)c);
 
 
             if (c == '\n')
             {
                 break;
             }
          }
          responseStr = buff.toString();
          buff.setLength(0);
          try
          {
             code = Integer.parseInt(responseStr.substring(0, 3));
          }
          catch (NumberFormatException e)
          {
             code = -1;
          }
          catch (StringIndexOutOfBoundsException e)
          {
             /* this line doesn't contain a response code, so
              * we just completely ignore it 
              */
              continue;
          }
          response.addElement(responseStr);
          if (continuingCode != -1)
          {
             /* we've seen a XXX- sequence */
             if (code != continuingCode ||
                (responseStr.length() >= 4 && 
                responseStr.charAt(3) == '-'))
             {
                 continue;
             }
             else
             {
                /* seen the end of code sequence */
                continuingCode = -1;
                break;
             }
          }
          else if (responseStr.length() >= 4 &&
             responseStr.charAt(3) == '-')
          {
             continuingCode = code;
             continue;
          }
          else
          {
             break;
          }
       }
 
       previousReplyCode = code;
       return code;
    }
 
    private int issueCommand(String cmd) throws IOException
    {
       int reply;
       if (replyPending)
       {
          if (readReply() == FTP_ERROR)
          {
             System.out.print("Error reading pending reply\n");
          }
       }
       replyPending = false;
       do
       {
          System.out.println(cmd);
          controlOut.print(cmd + "\r\n");
          reply = readReply();
       } while (reply == FTP_TRY_AGAIN);
       return reply;
    }
    /**
     * Parses the mediaLocator field into host and file strings
     */
 
    protected void parseLocator()
    {
       initCheck();
       String rest = getLocator().getRemainder();
       System.out.println("Begin parsing of: " + rest);
       int p1, p2 = 0;
       p1 = rest.indexOf("//");
       p2 = rest.indexOf("/", p1+2);
       hostString = rest.substring(p1 + 2, p2);
       fileString = rest.substring(p2);
       System.out.println("host: " + hostString + "   file: " 
          + fileString);
    }
    
    public Object getControl(String controlType)
	{
		return null;
	}

	public Object[] getControls()
	{
		return new Object[0];
	}
 }

