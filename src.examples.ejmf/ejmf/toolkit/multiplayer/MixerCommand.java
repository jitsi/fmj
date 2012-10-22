package ejmf.toolkit.multiplayer;

/**
  * A command pattern used with Mixer and MultiPlayerControl.
  */
public interface MixerCommand {
	/** Execute the command
	*/
    public void execute();
	/** String name of command
	* @return name of command
	*/
    public String toString();
}
