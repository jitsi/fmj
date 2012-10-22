package ejmf.toolkit.media.event;

import javax.media.ControllerErrorEvent;
import javax.media.Player;

/**
 * A <tt>ManagedControllerErrorEvent</tt> indicates that a
 * <tt>Controller</tt> managed by a Player posted a
 * ControllerErrorEvent and subsequently caused the managing
 * Player to close.
 */
public class ManagedControllerErrorEvent extends ControllerErrorEvent {
    private ControllerErrorEvent event;

    /**
     * Create a ManagedControllerErrorEvent for the given managing
     * Player and ControllerErrorEvent.
     *
     * @param      manager
     *             The managing Player.
     *
     * @param      event
     *             The original ControllerErrorEvent posted by a
     *             managed Controller.
     */
    public ManagedControllerErrorEvent(
        Player manager,
        ControllerErrorEvent event)
    {
        super(manager);
        this.event = event;
    }

    /**
     * Create a ManagedControllerErrorEvent for the given managing
     * Player, ControllerErrorEvent, and description.
     *
     * @param      manager
     *             The managing Player.
     *
     * @param      event
     *             The original ControllerErrorEvent posted by a
     *             managed Controller.
     *
     * @param      message
     *             A message describing the error.
     */
    public ManagedControllerErrorEvent(
        Player manager,
        ControllerErrorEvent event,
        String message)
    {
        super(manager, message);
        this.event = event;
    }

    /**
     * Get the original ControllerErrorEvent posted by the managed
     * Controller.
     */
    public ControllerErrorEvent getControllerErrorEvent() {
        return event;
    }
}
