package net.sf.fmj.ui.utils;

import java.awt.*;

import javax.swing.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ErrorDialog
{
    public static void showError(Component c, String e)
    {
        JOptionPane.showMessageDialog(c, e, "Error",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component c, Throwable e)
    {
        showError(c, "" + e);
    }
}
