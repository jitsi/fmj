package ejmf.toolkit.util;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class BorderConstants {
    public final static int GAP = 10;

    public final static EmptyBorder
        emptyBorder = new EmptyBorder(GAP,GAP,GAP,GAP);

    public final static CompoundBorder
        etchedBorder = new CompoundBorder(new EtchedBorder(), emptyBorder);
}
