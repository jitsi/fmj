package ejmf.toolkit.media.content.text.plain.controls;

import java.awt.Component;

import javax.media.Control;

public class FontControl implements Control {
    private FontChooser chooser;

    public FontControl(Component c) {
        chooser = new FontChooser(c);
    }

    public Component getControlComponent() {
        return chooser;
    }
}
