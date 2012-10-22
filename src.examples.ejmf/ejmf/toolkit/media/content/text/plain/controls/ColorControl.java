package ejmf.toolkit.media.content.text.plain.controls;

import java.awt.Component;

import javax.media.Control;

import ejmf.toolkit.gui.tickertape.TickerTape;

/**
 * The ColorControl class provides a color-choosing Control for
 * the Multi-Image Player.
 *
 * @author     Steve Talley & Rob Gordon
 */
public class ColorControl implements Control {
    private ColorChooser chooser;

    /**
     * Constructs a ColorControl for the given TickerTape.
     */
    public ColorControl(TickerTape tape) {
        chooser = new ColorChooser(tape);
    }

    /**
     * Returns the Control Component of this Control.
     */
    public Component getControlComponent() {
        return chooser;
    }
}
