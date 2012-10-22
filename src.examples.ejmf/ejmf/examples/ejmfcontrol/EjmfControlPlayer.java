package ejmf.examples.ejmfcontrol;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.media.Player;
import javax.swing.SwingUtilities;

import ejmf.toolkit.gui.controlpanel.EjmfControlPanel;
import ejmf.toolkit.install.PackageUtility;
import ejmf.toolkit.util.PlayerDriver;
import ejmf.toolkit.util.PlayerPanel;
import ejmf.toolkit.util.StateWaiter;

/**
 * EjmfControlPlayer uses EJMF Control panel 
 * for control panel.
 *
 * @see ejmf.toolkit.gui.controlpanel.EjmfControlPanel
 * @author         Rob Gordon & Steve Talley
 */
public class EjmfControlPlayer extends PlayerDriver {

    private Component			visualComponent;

    private Player			player;
    private PlayerPanel			playerpanel;

    public static void main(String[] args) {
        PackageUtility.addContentPrefix("ejmf.toolkit", false);
        main(new EjmfControlPlayer(), args);
    }
    
    public void begin() {
	playerpanel = getPlayerPanel();
        player = playerpanel.getPlayer();

	StateWaiter waiter = new StateWaiter(player);

        waiter.blockingRealize();

        Runnable r = new Runnable() {
            public void run() {
                playerpanel.addControlComponent(
			new EjmfControlPanel(player));

                playerpanel.addVisualComponent();
                redraw();
            }
        };
	try {
	    // invokeLater is insufficient here. The construction
	    // of EjmfControlPanel takes long enough that Player
	    // can start before construction completes. The effect
	    // of this is that application behaves as if blockingPrefetch
	    // was never called. Additionally, it hoses working of 
	    // EjmfControlPanel is inexplicable ways.

            SwingUtilities.invokeAndWait(r);
	} catch (InterruptedException ie) {
	    ie.printStackTrace();
	} catch (InvocationTargetException te) {
	    te.printStackTrace();
	}
	waiter.blockingPrefetch();

        // Start Player
	player.start();
    }
}
