package ejmf.toolkit.multiplayer;

import ejmf.toolkit.util.Sortable;
/**
 * A MixerEvent encapsulates a time-based operation
 * on a Player. 
 * <p>
 * A MixerEvent maintains a pair: time and command.  
 * The command is executed at the specified time offset. 
 * The time represents the offset from some arbitrary start
 * time. The command is an instance of MixerCommand.
 * <p>
 * MixerEvent implements the Sortable interface so that
 * a collection of them can be easily sort according to their
 * time offset.
 *
 * @see ejmf.toolkit.multiplayer.MixerCommand
 * @see ejmf.toolkit.multiplayer.Track
 */

public class MixerEvent implements Sortable { 

    private MixerCommand	cmd;
    private long		time;	// milliseconds
					// This is always offset from
					// previous event in queue
			
	/**
	* Create a MixerEvent specifying its command/time pair
	* completely. Time is offset from arbitrary start time.
	*
	* @param cmd MixerCommand command to be executed at given time
	* @param time Time, in milliseconds at which command is executed.
	*/
    public MixerEvent(MixerCommand cmd, long time) {
	this.time = time;
	this.cmd = cmd;
    }

	/**
	* Set the time offset at which this MixerEvent's
	* command is to be executed.
	*
	* @param time Time, in milliseconds at which command is executed.
	*/
    public void setTime(long time) {
	this.time = time;
    }

	/**
	* Get the time offset at which this MixerEvent's
	* command is to be executed.
	*
	* @return Command execution offset.
	*/
    public long getTime() {
	return time;
    }

	/**
	* Determine whether this MixerEvent's command is executed
	* before another MixerEvent's command. This method implements
	* Sortable interface required by quick sort. It tests if
        * if this event's time offset is smaller than one passed as
	* argument.
	*
	* @param s a Sortable
	* @see ejmf.toolkit.util.Sortable
	* @see ejmf.tooklit.util.QuickSort
	*/
    public boolean lessThan(Sortable s) {
	MixerEvent e = (MixerEvent) s;
	return time < e.getTime();
    }

	/**
	*  Get the MixerCommand associated with this	
	* MixerEvent.
        * 
	* @return A MixerCommand associated with this event.
	*/
    public MixerCommand getCommand() {
	return cmd;
    }

	/**
	* Execute the command associated with MixerEvent
	*/
    public void execute() {
	cmd.execute();
    }
}
