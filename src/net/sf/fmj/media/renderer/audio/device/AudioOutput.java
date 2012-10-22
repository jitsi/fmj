package net.sf.fmj.media.renderer.audio.device;

import javax.media.format.*;

public interface AudioOutput
{
    /**
     * Obtains the number of bytes of data that can be written to the device
     * without blocking.
     */
    public int bufferAvailable();

    /**
     * Close the device. Cannot call this close since it clashes
     * InputStream.close for SunAudioOutput which also implements InputStream.
     */
    public void dispose();

    /**
     * Drain the device.
     */
    public void drain();

    /**
     * Flush the device.
     */
    public void flush();

    /**
     * Return the audio gain of the device.
     */
    public double getGain();

    /**
     * Return the time as measured from the samples consumed since the device
     * has opened.
     */
    public long getMediaNanoseconds();

    /**
     * Return if the device is muted.
     */
    public boolean getMute();

    /**
     * get the playback rate.
     */
    public float getRate();

    /**
     * Initialize the audio output.
     */
    public boolean initialize(AudioFormat format, int bufferSize);

    /**
     * Pause the device.
     */
    public void pause();

    /**
     * Resume the device.
     */
    public void resume();

    /**
     * Set the audio gain of the device.
     */
    public void setGain(double g);

    /**
     * Mute the audio device.
     */
    public void setMute(boolean m);

    /**
     * set the playback rate.
     */
    public float setRate(float rate);

    /**
     * Write data to the device.
     */
    public int write(byte data[], int off, int len);

}
