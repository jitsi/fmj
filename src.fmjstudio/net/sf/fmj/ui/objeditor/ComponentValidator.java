package net.sf.fmj.ui.objeditor;

import javax.swing.*;

/**
 *
 * @author Ken Larson
 *
 */
public class ComponentValidator
{
    private static String buildMessage(String label, String msg)
    {
        if (label == null || label.equals(""))
            return msg;
        if (label.endsWith(":"))
            return label + " " + msg;
        else
            return label + ": " + msg;

    }

    public void validateInteger(JComboBox c, JLabel label)
            throws ComponentValidationException
    {
        final Object o = c.getSelectedItem();
        final String s = o == null ? null : o.toString();
        try
        {
            Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            throw new ComponentValidationException(c, buildMessage(
                    label.getText(), "not a valid number"));
        }

    }

    public void validateInteger(JTextField f, JLabel label)
            throws ComponentValidationException
    {
        final String s = f.getText();
        try
        {
            Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            throw new ComponentValidationException(f, buildMessage(
                    label.getText(), "not a valid number"));
        }

    }

    public void validateNotEmpty(JComboBox c, JLabel label)
            throws ComponentValidationException
    {
        final Object o = c.getSelectedItem();
        final String s = o == null ? null : o.toString();
        if (s == null || s.equals(""))
            throw new ComponentValidationException(c, buildMessage(
                    label.getText(), "may not be empty"));

    }

    public void validateNotEmpty(JTextField f, JLabel label)
            throws ComponentValidationException
    {
        final String s = f.getText();
        if (s == null || s.equals(""))
            throw new ComponentValidationException(f, buildMessage(
                    label.getText(), "may not be empty"));

    }
}
