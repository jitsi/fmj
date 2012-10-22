package net.sf.fmj.media.control;

public interface ProgressControl extends GroupControl
{
    StringControl getAudioCodec();

    /**
     * Displays the audio properties such as sampling rate, resolution,
     * compression type, etc. specific to the incoming audio stream.
     */
    StringControl getAudioProperties();

    /**
     * A StringControl that displays the instantaneous bandwidth of the input
     * stream.
     */
    StringControl getBitRate();

    /**
     * A StringControl that displays the instantaneous frame rate, if video is
     * present.
     */
    StringControl getFrameRate();

    StringControl getVideoCodec();

    /**
     * Displays the video properties such as size, compression type, etc. which
     * are specific to the incoming video stream.
     */
    StringControl getVideoProperties();
}
