package net.sf.fmj.ui.application;

/**
 * Used to allow PlayerPanel to update its status bar in response to state
 * changes of ContainerPlayer.
 *
 * @author Ken Larson
 *
 */
public interface ContainerPlayerStatusListener
{
    public static final String LOADING = "Loading...";

    public static final String CREATE_PLAYER_FAILED = "Error loading media.";
    public static final String REALIZE_COMPLETE = "Ready.";
    public static final String STOPPED = "Stopped.";
    public static final String STARTED = "Playing...";
    public static final String RESOURCE_UNAVAILABLE = "Error loading to media.";
    public static final String ERROR_SHOWING_PLAYER = "Error displaying player.";
    public static final String ERROR_PREFIX = "Error: ";
    public static final String END_OF_MEDIA = "End of media.";
    public static final String PROCESSING = "Processing...";

    public void onStatusChange(String newStatus);
}
