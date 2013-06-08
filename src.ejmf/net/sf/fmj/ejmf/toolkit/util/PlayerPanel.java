package net.sf.fmj.ejmf.toolkit.util;

import java.awt.*;
import java.io.*;

import javax.media.*;
import javax.swing.*;

/**
 * This class is a panel that is intended to display a particular medium. It is
 * intended that all GUI layout take place in the constructor, and that the
 * media should begin playing when begin() is called. This will allow its parent
 * container to complete its own layout before the media plays.
 * <p>
 * Example:
 * <P>
 * <blockquote>
 *
 * <pre>
 * Frame f = new Frame();
 * Player player = Utility.getPlayer("/ejmf/media/xmas.avi");
 * PlayerPanel p = new PlayerPanel(player);
 * Label l = new Label("Media example");<p>
 * //  Configure display
 * p.addControlComponent();
 * p.addVisualComponent();
 * add(p);
 * add(l);<p>
 * //  Display components
 * f.pack();
 * f.setvisible(true);<p>
 * //  Start media play
 * p.begin();
 * </pre>
 *
 * </blockquote> Note that it may be worthwhile to realize() the player in the
 * constructor of the PlayerPanel, so that when begin() is called the components
 * do not have to resize.
 *
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 *
 * Modified by Ken Larson.
 *
 * @author Steve Talley & Rob Gordon
 */

public class PlayerPanel extends JPanel
{
    private static final Object _VISLOC = BorderLayout.CENTER;
    private static final Object _CPLOC = BorderLayout.SOUTH;
    private static final String LOADLABEL = "Loading Media...";

    // public TitledBorder
    // mediaBorder = new TitledBorder(
    // BorderConstants.etchedBorder, "Media" );

    private JPanel mediaPanel;
    private Player player;

    // private JLabel loadingLabel;

    /**
     * Constructs a PlayerPanel for the given MediaLocator.
     *
     * @exception IOException
     *                If an I/O error occurs while accessing the media.
     *
     * @exception NoPlayerException
     *                If a Player cannot be created from the given MediaLocator.
     */
    public PlayerPanel(MediaLocator locator) throws IOException,
            NoPlayerException
    {
        player = Manager.createPlayer(locator);

        this.setLayout(new BorderLayout());
        // this.setLayout(new BorderLayout());
        mediaPanel = new JPanel();
        mediaPanel.setLayout(new BorderLayout());
        // mediaPanel.setBorder(mediaBorder);
        // setBorder( BorderConstants.emptyBorder );

        // loadingLabel =
        // new JLabel(LOADLABEL);
        //
        // loadingLabel.setFont(
        // new Font("Dialog", Font.BOLD, 12) );
        //
        // add(loadingLabel);
    }

    public PlayerPanel(Player p) throws IOException, NoPlayerException
    {
        this.player = p;

        this.setLayout(new BorderLayout());
        // this.setLayout(new BorderLayout());
        mediaPanel = new JPanel();
        mediaPanel.setLayout(new BorderLayout());

    }

    private Component addComponent(Component c, Object constraints)
    {
        if (c == null)
        {
            return c;
        }

        mediaPanel.add(c, constraints);
        return c;
    }

    /**
     * Adds the Player's control component to this PlayerPanel.
     *
     * @return <addtext>
     */
    public Component addControlComponent()
    {
        return addControlComponent(player.getControlPanelComponent());
    }

    /**
     * Adds a given component to the part of this PlayerPanel reserved for its
     * Player's control component.
     *
     * @param cc
     *            <addtext>
     *
     * @return <addtext>
     */
    public Component addControlComponent(Component cc)
    {
        // removeLoadingLabel();
        addMediaPanel();

        return addComponent(cc, _CPLOC);
    }

    /**
     * Removes the initial "loading" label that appears while the media is
     * loading.
     */
    // public void removeLoadingLabel() {
    // if( isAncestorOf(loadingLabel) ) {
    // remove(loadingLabel);
    // }
    // }

    public void addMediaPanel()
    {
        if (!isAncestorOf(mediaPanel))
        {
            add(mediaPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Adds the Player's visual component to this PlayerPanel.
     *
     * @return The Component added.
     */
    public Component addVisualComponent()
    {
        return addVisualComponent(player.getVisualComponent());
    }

    /**
     * Adds a given component to the part of this PlayerPanel reserved for its
     * Player's visual component.
     *
     * @param cc
     *            The Component to add.
     *
     * @return The Component added.
     */
    public Component addVisualComponent(Component cc)
    {
        // removeLoadingLabel();
        addMediaPanel();

        return addComponent(cc, _VISLOC);
    }

    /**
     * Get the media panel component
     */
    public JPanel getMediaPanel()
    {
        return mediaPanel;
    }

    /**
     * Gets the Player for this PlayerPanel.
     */
    public Player getPlayer()
    {
        return player;
    }
}
