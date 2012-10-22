package ejmf.toolkit.media.content.text.plain.controls;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ejmf.toolkit.gui.tickertape.TickerTape;

/**
 * ColorChooser is the GUI component for a Control for the
 * TickerTape class, used by the Multi-Image (.miv) Player.  It
 * provides the ability to change the colors of the foreground,
 * background, or shadow of the Multi-Image Player.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class ColorChooser extends JPanel {
    private static final int GAP = 10;
    private static final Border
        emptyBorder  = new EmptyBorder(GAP,GAP,GAP,GAP),
        etchedBorder = new CompoundBorder(new EtchedBorder(), emptyBorder);

    private TickerTape tickertape;
    private RGBChooser background;
    private RGBChooser foreground;
    private RGBChooser shadow;

    private ActionListener backgroundListener =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tickertape.setBackground(
                    background.getColor() );
            }
        };

    private ActionListener foregroundListener =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tickertape.setForeground(
                    foreground.getColor() );
            }
        };

    private ActionListener shadowListener =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tickertape.setShadow(
                    shadow.getColor() );
            }
        };

    /**
     * Constructs a ColorChooser for the given Tickertape.
     */
    public ColorChooser(TickerTape tickertape) {
        super();
        this.tickertape = tickertape;

        background = new RGBChooser(
            "Background", tickertape.getBackground(),
            backgroundListener);

        foreground = new RGBChooser(
            "Foreground", tickertape.getForeground(),
            foregroundListener);

        shadow = new RGBChooser(
            "Shadow", tickertape.getShadow(),
            shadowListener);

        JPanel gridPanel = new JPanel( new GridLayout(3,1,GAP,GAP) );
        gridPanel.add(background);
        gridPanel.add(foreground);
        gridPanel.add(shadow);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( new TitledBorder(etchedBorder, "Color Control") );
        mainPanel.add(gridPanel);

        add(mainPanel);
    }
}
