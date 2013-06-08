package javax.media.control;

import javax.media.*;

/**
 * Standard JMF class -- see <a href=
 * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/control/MpegAudioControl.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 *
 * @author Ken Larson
 *
 */
public interface MpegAudioControl extends Control
{
    public static final int LAYER_1 = 1;

    public static final int LAYER_2 = 2;

    public static final int LAYER_3 = 4;

    public static final int SAMPLING_RATE_16 = 1;

    public static final int SAMPLING_RATE_22_05 = 2;

    public static final int SAMPLING_RATE_24 = 4;

    public static final int SAMPLING_RATE_32 = 8;

    public static final int SAMPLING_RATE_44_1 = 16;

    public static final int SAMPLING_RATE_48 = 32;

    public static final int SINGLE_CHANNEL = 1;

    public static final int TWO_CHANNELS_STEREO = 2;

    public static final int TWO_CHANNELS_DUAL = 4;

    public static final int THREE_CHANNELS_2_1 = 4;

    public static final int THREE_CHANNELS_3_0 = 8;

    public static final int FOUR_CHANNELS_2_0_2_0 = 16;

    public static final int FOUR_CHANNELS_2_2 = 32;

    public static final int FOUR_CHANNELS_3_1 = 64;

    public static final int FIVE_CHANNELS_3_0_2_0 = 128;

    public static final int FIVE_CHANNELS_3_2 = 256;

    public int getAudioLayer();

    public int getChannelLayout();

    public boolean getLowFrequencyChannel();

    public boolean getMultilingualMode();

    public int getSupportedAudioLayers();

    public int getSupportedChannelLayouts();

    public int getSupportedSamplingRates();

    public boolean isLowFrequencyChannelSupported();

    public boolean isMultilingualModeSupported();

    public int setAudioLayer(int audioLayer);

    public int setChannelLayout(int channelLayout);

    public boolean setLowFrequencyChannel(boolean on);

    public boolean setMultilingualMode(boolean on);
}
