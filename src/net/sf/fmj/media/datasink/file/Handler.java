package net.sf.fmj.media.datasink.file;

import java.io.*;

import javax.media.*;
import javax.media.protocol.*;

import net.sf.fmj.media.*;
import net.sf.fmj.media.datasink.*;

public class Handler extends BasicDataSink implements SourceTransferHandler,
        Seekable, Runnable, RandomAccess, Syncable
{
    final private static boolean DEBUG = false;

    final protected static int NOT_INITIALIZED = 0;
    final protected static int OPENED = 1;
    final protected static int STARTED = 2;
    // final protected static int STOPPED = 3;
    final protected static int CLOSED = 3;

    protected int state = NOT_INITIALIZED;

    protected DataSource source;
    protected SourceStream[] streams;
    protected SourceStream stream;
    protected boolean push;

    protected boolean errorEncountered = false;
    protected String errorReason = null;

    protected Control[] controls;

    protected File file;
    protected File tempFile = null;
    protected RandomAccessFile raFile = null;
    protected RandomAccessFile qtStrRaFile = null;
    protected boolean fileClosed = false;
    protected FileDescriptor fileDescriptor = null;
    protected MediaLocator locator = null;
    protected String contentType = null;
    protected int fileSize = 0; // Good for over 2 Gigabytes
    protected int filePointer = 0;
    protected int bytesWritten = 0;
    protected static final int BUFFER_LEN = 128 * 1024;
    protected boolean syncEnabled = false;

    protected byte[] buffer1 = new byte[BUFFER_LEN];
    protected byte[] buffer2 = new byte[BUFFER_LEN];
    protected boolean buffer1Pending = false;
    protected long buffer1PendingLocation = -1;
    protected int buffer1Length;
    protected boolean buffer2Pending = false;
    protected long buffer2PendingLocation = -1;
    protected int buffer2Length;
    protected long nextLocation = 0;
    protected Thread writeThread = null;
    private Integer bufferLock = Integer.valueOf(0);
    private boolean receivedEOS = false;

    public int WRITE_CHUNK_SIZE = 16384;

    private boolean streamingEnabled = false;
    private boolean errorCreatingStreamingFile = false;

    long lastSyncTime = -1;

    public void close()
    {
        close(null); // No Error;
    }

    protected final void close(String reason)
    {
        synchronized (this)
        {
            if (state == CLOSED)
                return;
            setState(CLOSED);
        }

        if (push)
        {
            for (int i = 0; i < streams.length; i++)
            {
                ((PushSourceStream) streams[i]).setTransferHandler(null);
            }
        }

        if (reason != null)
        {
            errorEncountered = true;
            sendDataSinkErrorEvent(reason);
            // Wake up the write thread
            synchronized (bufferLock)
            {
                bufferLock.notifyAll();
            }
        }

        // Wait for all buffers to be written
        /*
         * synchronized (bufferLock) { while (reason == null && (buffer2Pending
         * || buffer1Pending)) { try { bufferLock.wait(250); } catch
         * (InterruptedException ie) { } } }
         */

        try
        {
            source.stop();
        } catch (IOException e)
        {
            System.err.println("IOException when stopping source " + e);
        }

        try
        {
            if (raFile != null)
            {
                raFile.close();
            }

            if (streamingEnabled)
            {
                if (qtStrRaFile != null)
                {
                    qtStrRaFile.close();
                }
            }

            // Disconnect the data source
            if (source != null)
                source.disconnect();
            // //////////////////////////////////////////////////////////

            if (streamingEnabled && (tempFile != null))
            {
                // Delete the temp file if no errors creating streamable file.
                // If errors creating streamable file, delete streamable file.
                deleteFile(errorCreatingStreamingFile ? file : tempFile);
            }
            // fileClosed = true;
            // sendEndofStreamEvent();
        } catch (IOException e)
        {
            System.out.println("close: " + e);
        }

        raFile = null; // Should be done after setting the state to CLOSED
        qtStrRaFile = null;

        removeAllListeners();
    }

    private boolean deleteFile(File file)
    {
        boolean fileDeleted = false;
        try
        {
            fileDeleted = file.delete();
        } catch (Throwable e)
        {
        }
        return fileDeleted;
    }

    public long doSeek(long where)
    {
        if (raFile != null)
        {
            try
            {
                raFile.seek(where);
                filePointer = (int) where;
                return where;
            } catch (IOException ioe)
            {
                close("Error in seek: " + ioe);
            }
        }
        return -1;
    }

    public long doTell()
    {
        if (raFile != null)
        {
            try
            {
                return raFile.getFilePointer();
            } catch (IOException ioe)
            {
                close("Error in tell: " + ioe);
            }
        }
        return -1;
    }

    public String getContentType()
    {
        return contentType;
    }

    public Object getControl(String controlName)
    {
        return null;
    }

    public Object[] getControls()
    {
        if (controls == null)
        {
            controls = new Control[0];
        }
        return controls;
    }

    public MediaLocator getOutputLocator()
    {
        return locator;
    }

    public boolean isRandomAccess()
    {
        return true;
    }

    public void open() throws IOException, SecurityException
    {
        try
        {
            if (state == NOT_INITIALIZED)
            {
                if (locator != null)
                {
                    String pathName = locator.getRemainder(); // getFileName(locator);
                    // Strip off excess /'s
                    while (pathName.charAt(0) == '/'
                            && (pathName.charAt(1) == '/' || pathName.charAt(2) == ':'))
                    {
                        pathName = pathName.substring(1);
                    }
                    String fileSeparator = System.getProperty("file.separator");
                    if (fileSeparator.equals("\\"))
                    {
                        pathName = pathName.replace('/', '\\');
                    }

                    // In jdk1.2, RandomAccessFile has a useful method called
                    // setLength() which can be used to truncate an existing
                    // file. But this won't work on jdk1.1. So we have to
                    // delete a file if it exists

                    // On Windows, you cannot delete a file if some process
                    // is using it.

                    file = new File(pathName);
                    if (file.exists())
                    {
                        if (!deleteFile(file))
                        {
                            System.err.println("datasink open: Existing file "
                                    + pathName
                                    + " cannot be deleted. Check if "
                                    + "some other process is using "
                                    + " this file");
                            if (push)
                                ((PushSourceStream) stream)
                                        .setTransferHandler(null);
                            throw new IOException("Existing file " + pathName
                                    + " cannot be deleted");
                        }
                    }

                    String parent = file.getParent();
                    if (parent != null)
                    {
                        new File(parent).mkdirs();
                    }
                    try
                    {
                        if (!streamingEnabled)
                        {
                            raFile = new RandomAccessFile(file, "rw");
                            fileDescriptor = raFile.getFD();
                        } else
                        {
                            String fileqt;
                            int index;
                            if ((index = pathName.lastIndexOf(".")) > 0)
                            {
                                // TODO: maybe $$$ add random string
                                fileqt = pathName.substring(0, index)
                                        + ".nonstreamable"
                                        + pathName.substring(index,
                                                pathName.length());
                            } else
                            {
                                // TODO Maybe add random string?
                                // TODO Don't assume .mov if the file doesn't
                                // have an extension. Try to use content type if
                                // possible to guess the extension. However,
                                // extensions will be there as FMJ provides it
                                // even if user doesn't.
                                fileqt = file + ".nonstreamable.mov";
                            }
                            tempFile = new File(fileqt);

                            raFile = new RandomAccessFile(tempFile, "rw");
                            fileDescriptor = raFile.getFD();
                            qtStrRaFile = new RandomAccessFile(file, "rw");
                        }
                    } catch (IOException e)
                    {
                        // Catch the exception for debugging purpose and
                        // throw it again
                        System.err
                                .println("datasink open: IOException when creating RandomAccessFile "
                                        + pathName + " : " + e);
                        if (push)
                            ((PushSourceStream) stream)
                                    .setTransferHandler(null);
                        throw e;
                    }

                    setState(OPENED);
                }
            }
        } finally
        {
            if ((state == NOT_INITIALIZED) && (stream != null))
            {
                ((PushSourceStream) stream).setTransferHandler(null);
            }
        }
    }

    // Asynchronous write thread
    public void run()
    {
        while (!(state == CLOSED || errorEncountered))
        {
            synchronized (bufferLock)
            {
                // Wait for some data or error
                while (!buffer1Pending && !buffer2Pending && !errorEncountered
                        && state != CLOSED && !receivedEOS)
                {
                    if (DEBUG)
                        System.err.println("Waiting for filled buffer");
                    try
                    {
                        bufferLock.wait(500);
                    } catch (InterruptedException ie)
                    {
                    }
                    if (DEBUG)
                        System.err.println("Consumer notified");
                }
            }
            // Something's pending
            if (buffer2Pending)
            {
                if (DEBUG)
                    System.err.println("Writing Buffer2");
                // write that first
                write(buffer2, buffer2PendingLocation, buffer2Length);
                if (DEBUG)
                    System.err.println("Done writing Buffer2");
                buffer2Pending = false;
            }

            synchronized (bufferLock)
            {
                if (buffer1Pending)
                {
                    byte[] tempBuffer = buffer2;
                    buffer2 = buffer1;
                    buffer2Pending = true;
                    buffer2PendingLocation = buffer1PendingLocation;
                    buffer2Length = buffer1Length;
                    buffer1Pending = false;
                    buffer1 = tempBuffer;
                    if (DEBUG)
                        System.err.println("Notifying producer");
                    bufferLock.notifyAll();
                } else
                {
                    if (receivedEOS)
                        break;
                }
            }
        }
        if (receivedEOS)
        {
            if (DEBUG)
                System.err.println("Sending EOS: streamingEnabled is "
                        + streamingEnabled);
            // Close the file and flag it
            if (raFile != null)
            {
                if (!streamingEnabled)
                {
                    try
                    {
                        raFile.close();
                    } catch (IOException ioe)
                    {
                    }
                    raFile = null;
                }
                fileClosed = true;
            }
            if (!streamingEnabled)
            {
                sendEndofStreamEvent();
            }
        }
        if (errorEncountered && state != CLOSED)
        {
            close(errorReason);
        }
    }

    public synchronized long seek(long where)
    {
        nextLocation = where;
        return where;
    }

    public void setEnabled(boolean b)
    {
        streamingEnabled = b;
    }

    // TODO : Handle pull data source

    /**
     * Set the output <tt>MediaLocator</tt>. This method should only be called
     * once; an error is thrown if the locator has already been set.
     *
     * @param output
     *            <tt>MediaLocator</tt> that describes where the output goes.
     */
    public void setOutputLocator(MediaLocator output)
    {
        locator = output;
    }

    public void setSource(DataSource ds) throws IncompatibleSourceException
    {
        if (!(ds instanceof PushDataSource) && !(ds instanceof PullDataSource))
        {
            throw new IncompatibleSourceException("Incompatible datasource");
        }
        source = ds;

        if (source instanceof PushDataSource)
        {
            push = true;
            try
            {
                ((PushDataSource) source).connect();
            } catch (IOException ioe)
            {
            }
            streams = ((PushDataSource) source).getStreams();
        } else
        {
            push = false;
            try
            {
                ((PullDataSource) source).connect();
            } catch (IOException ioe)
            {
            }
            streams = ((PullDataSource) source).getStreams();
        }

        if (streams == null || streams.length != 1)
            throw new IncompatibleSourceException(
                    "DataSource should have 1 stream");
        stream = streams[0];

        contentType = source.getContentType();
        if (push)
            ((PushSourceStream) stream).setTransferHandler(this);
    }

    protected void setState(int state)
    {
        synchronized (this)
        {
            this.state = state;
        }
    }

    public void setSyncEnabled()
    {
        syncEnabled = true;
    }

    public void start() throws IOException
    {
        if (state == OPENED)
        {
            if (source != null)
                source.start();
            if (writeThread == null)
            {
                writeThread = new Thread(this);
                writeThread.start();
            }
            setState(STARTED);
        }
    }

    /**
     * Stop the data-transfer. If the source has not been connected and started,
     * <tt>stop</tt> does nothing.
     */
    public void stop() throws IOException
    {
        if (state == STARTED)
        {
            if (source != null)
                source.stop();
            setState(OPENED);
        }
    }

    public long tell()
    {
        return nextLocation;
    }

    public synchronized void transferData(PushSourceStream pss)
    {
        int totalRead = 0;
        int spaceAvailable = BUFFER_LEN;
        int bytesRead = 0;
        if (errorEncountered)
            return;

        if (buffer1Pending)
        {
            synchronized (bufferLock)
            {
                while (buffer1Pending)
                {
                    if (DEBUG)
                        System.err.println("Waiting for free buffer");
                    try
                    {
                        bufferLock.wait();
                    } catch (InterruptedException ie)
                    {
                    }
                }
            }
            if (DEBUG)
                System.err.println("Got free buffer");
        }

        // System.err.println("In transferData()");
        while (spaceAvailable > 0)
        {
            try
            {
                bytesRead = pss.read(buffer1, totalRead, spaceAvailable);
                // System.err.println("bytesRead = " + bytesRead);
                if (bytesRead > 16 * 1024 && WRITE_CHUNK_SIZE < 32 * 1024)
                {
                    if (bytesRead > 64 * 1024 && WRITE_CHUNK_SIZE < 128 * 1024)
                        WRITE_CHUNK_SIZE = 128 * 1024;
                    else if (bytesRead > 32 * 1024
                            && WRITE_CHUNK_SIZE < 64 * 1024)
                        WRITE_CHUNK_SIZE = 64 * 1024;
                    else if (WRITE_CHUNK_SIZE < 32 * 1024)
                        WRITE_CHUNK_SIZE = 32 * 1024;
                    // System.err.println("Increasing buffer to " +
                    // WRITE_CHUNK_SIZE);

                }
            } catch (IOException ioe)
            {
                // What to do here?
            }
            if (bytesRead <= 0)
            {
                break;
            }
            totalRead += bytesRead;
            spaceAvailable -= bytesRead;
        }

        if (totalRead > 0)
        {
            synchronized (bufferLock)
            {
                buffer1Pending = true;
                buffer1PendingLocation = nextLocation;
                buffer1Length = totalRead;
                nextLocation = -1; // assume next write is contiguous unless
                                   // seeked
                // Notify availability to write thread
                if (DEBUG)
                    System.err.println("Notifying consumer");
                bufferLock.notifyAll();
            }
        }
        // Send EOS if necessary
        if (bytesRead == -1)
        {
            if (DEBUG)
                System.err.println("Got EOS");
            receivedEOS = true;
            // Wait until file is closed. This makes the Processor's close
            // call to force the data sink to close the file, just in case
            // the user doesn't remember to close the datasink before exiting.
            while (!fileClosed && !errorEncountered && !(state == CLOSED))
            {
                try
                {
                    Thread.sleep(50);
                } catch (InterruptedException ie)
                {
                }
            }
        }
    }

    private void write(byte[] buffer, long location, int length)
    {
        int offset, toWrite;
        try
        {
            if (location != -1)
                doSeek(location);
            offset = 0;
            while (length > 0)
            {
                toWrite = WRITE_CHUNK_SIZE;
                if (length < toWrite)
                    toWrite = length;
                raFile.write(buffer, offset, toWrite);
                bytesWritten += toWrite;

                // Sync/Flush after a few write so that the
                // file writing is smooth. Improves capture smoothness

                /*
                 * if (fileDescriptor != null) { // Sync'ing the file system at
                 * every 1 sec interval. if (lastSyncTime < 0) lastSyncTime =
                 * System.currentTimeMillis(); else { long ts =
                 * System.currentTimeMillis(); if (ts - lastSyncTime > 1000L) {
                 * fileDescriptor.sync();
                 * //System.err.println("sync: byte written: " + bytesWritten);
                 * bytesWritten = 0; lastSyncTime = ts; } } }
                 */

                if (fileDescriptor != null && syncEnabled
                        && bytesWritten >= WRITE_CHUNK_SIZE)
                {
                    bytesWritten -= WRITE_CHUNK_SIZE;
                    fileDescriptor.sync();
                }

                filePointer += toWrite;
                length -= toWrite;
                offset += toWrite;
                if (filePointer > fileSize)
                    fileSize = filePointer;
                Thread.yield();
            }
        } catch (IOException ioe)
        {
            errorEncountered = true;
            errorReason = ioe.toString();
        }
    }

    // write chunk
    // Call with -1, -1 when done writing chunks to streamable file
    // Call with -1, (num > 0) to seek to num-1 and write 1 byte
    public boolean write(long inOffset, int numBytes)
    {
        try
        {
            if ((inOffset >= 0) && (numBytes > 0))
            {
                int remaining = numBytes;
                int bytesToRead;
                raFile.seek(inOffset);

                while (remaining > 0)
                {
                    bytesToRead = (remaining > BUFFER_LEN) ? BUFFER_LEN
                            : remaining;
                    raFile.read(buffer1, 0, bytesToRead); // $$ CAST
                    qtStrRaFile.write(buffer1, 0, bytesToRead); // $$ CAST
                    remaining -= bytesToRead;
                }
            } else if ((inOffset < 0) && (numBytes > 0))
            {
                qtStrRaFile.seek(0);
                qtStrRaFile.seek(numBytes - 1);
                qtStrRaFile.writeByte(0);
                qtStrRaFile.seek(0);
            } else
            {
                sendEndofStreamEvent();
            }
        } catch (Exception e)
        {
            errorCreatingStreamingFile = true;
            System.err
                    .println("Exception when creating streamable version of media file: "
                            + e.getMessage());
            return false;
        }
        return true;
    }

}
