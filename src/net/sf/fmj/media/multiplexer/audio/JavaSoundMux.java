package net.sf.fmj.media.multiplexer.audio;

import java.io.*;
import java.util.logging.*;

import javax.media.*;
import javax.media.format.AudioFormat;
import javax.media.protocol.*;
import javax.sound.sampled.*;

import net.sf.fmj.media.multiplexer.*;
import net.sf.fmj.media.renderer.audio.*;
import net.sf.fmj.utility.*;

/**
 *
 * Audio multiplexer base class implemented using JavaSound. This mux has the
 * job of taking buffers passed in to process, and converting them to a stream
 * to be read by Javasound, and then converting the stream written by Javasound
 * into something that can be read as a track of a datasource. TODO: size is
 * written to file headers as Integer.MAXINT, because the seeking back to the
 * beginning and rewriting of the header is not yet implemented. JMF appears to
 * do this with a hack, casting the SourceTransferHandler to a Seekable, and
 * seeking the output, then rewriting.
 *
 * TODO: not so sure of all the open/close/connect/disconnect and which should
 * call what when.
 *
 * @author Ken Larson
 *
 */
public abstract class JavaSoundMux extends AbstractStreamCopyMux
{
    private class MyPushDataSource extends StreamCopyPushDataSource
    {
        final javax.sound.sampled.AudioFormat javaSoundFormats[];

        public MyPushDataSource(ContentDescriptor outputContentDescriptor,
                int numTracks, InputStream[] inputStreams, Format[] inputFormats)
        {
            super(outputContentDescriptor, numTracks, inputStreams,
                    inputFormats);
            javaSoundFormats = new javax.sound.sampled.AudioFormat[numTracks];
            for (int track = 0; track < numTracks; ++track)
            {
                javaSoundFormats[track] = JavaSoundUtils
                        .convertFormat((AudioFormat) inputFormats[track]);
            }
        }

        @Override
        protected void write(InputStream in, OutputStream out, int track)
                throws IOException
        {
            JavaSoundMux.this.write(in, out, javaSoundFormats[track]);
        }

    }

    private static final Logger logger = LoggerSingleton.logger;

    // TODO: deal with n tracks properly

    private final AudioFileFormat.Type audioFileFormatType;

    private static final int MAX_TRACKS = 1;

    public JavaSoundMux(final FileTypeDescriptor fileTypeDescriptor,
            AudioFileFormat.Type audioFileFormatType)
    {
        super(fileTypeDescriptor);
        this.audioFileFormatType = audioFileFormatType;
    }

    @Override
    protected StreamCopyPushDataSource createInputStreamPushDataSource(
            ContentDescriptor outputContentDescriptor, int numTracks,
            InputStream[] inputStreams, Format[] inputFormats)
    {
        return new MyPushDataSource(outputContentDescriptor, numTracks,
                inputStreams, inputFormats);
    }

    @Override
    public Format[] getSupportedInputFormats()
    {
        // TODO: query AudioSystem
        return new Format[] { new AudioFormat(AudioFormat.LINEAR) };
    }

    @Override
    public int setNumTracks(int numTracks)
    {
        return super.setNumTracks(numTracks > MAX_TRACKS ? MAX_TRACKS
                : numTracks);
    }

    protected void write(InputStream in, OutputStream out,
            javax.sound.sampled.AudioFormat javaSoundFormat) throws IOException
    {
        final long lengthInFrames = Integer.MAX_VALUE; // TODO: get
                                                       // java.io.IOException:
                                                       // stream length not
                                                       // specified for most
                                                       // formats (WAV, AIFF)
        final AudioInputStream ais = new AudioInputStream(in, javaSoundFormat,
                lengthInFrames);
        final AudioFileFormat.Type targetFileFormatType = audioFileFormatType;
        final int bytesWritten = AudioSystem.write(ais, targetFileFormatType,
                out);
        logger.fine("Audio OutputStream bytes written: " + bytesWritten);
    }

}
