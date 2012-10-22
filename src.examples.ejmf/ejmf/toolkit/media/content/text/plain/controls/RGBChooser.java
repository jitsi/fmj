package ejmf.toolkit.media.content.text.plain.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class RGBChooser extends JPanel {
    private static final int GAP = 10;
    private static final Border
        emptyBorder  = new EmptyBorder(GAP,GAP,GAP,GAP),
        etchedBorder = new CompoundBorder(new EtchedBorder(), emptyBorder);

    private Color color;

    private JLabel redLabel = new JLabel("Red:");
    private JLabel blueLabel = new JLabel("Blue:");
    private JLabel greenLabel = new JLabel("Green:");

    private JTextField redTextField = new JTextField(3);
    private JTextField blueTextField = new JTextField(3);
    private JTextField greenTextField = new JTextField(3);

    public RGBChooser(String title, Color color, ActionListener l) {
        super();

        this.color = color;
        loadColor();

        redLabel.setForeground( Color.red );
        greenLabel.setForeground( Color.green );
        blueLabel.setForeground( Color.blue );

        redTextField.addActionListener(l);
        blueTextField.addActionListener(l);
        greenTextField.addActionListener(l);

        JPanel gridPanel = new JPanel( new GridLayout(1,3,GAP,GAP) );
        JPanel redPanel = new JPanel( new BorderLayout(GAP,GAP) );
        JPanel greenPanel = new JPanel( new BorderLayout(GAP,GAP) );
        JPanel bluePanel = new JPanel( new BorderLayout(GAP,GAP) );

        redPanel.add( redLabel, BorderLayout.CENTER );
        redPanel.add( redTextField, BorderLayout.EAST );

        greenPanel.add( greenLabel, BorderLayout.CENTER );
        greenPanel.add( greenTextField, BorderLayout.EAST );

        bluePanel.add( blueLabel, BorderLayout.CENTER );
        bluePanel.add( blueTextField, BorderLayout.EAST );

        gridPanel.add(redPanel);
        gridPanel.add(greenPanel);
        gridPanel.add(bluePanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( new TitledBorder(etchedBorder, title) );
        mainPanel.add(gridPanel);

        add(mainPanel);
    }

    public void loadColor() {
        redTextField.setText( Integer.toString(color.getRed()) );
        greenTextField.setText( Integer.toString(color.getGreen()) );
        blueTextField.setText( Integer.toString(color.getBlue()) );
    }
    
    public Color getColor() {
        int red;
        try {
            red = Integer.valueOf(
                (String)redTextField.getText()).intValue();
            if( red < 0 || red > 255 ) {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException ex) {
            red = color.getRed();
            redTextField.setText( Integer.toString(red) );
        }

        int green;
        try {
            green = Integer.valueOf(
                (String)greenTextField.getText()).intValue();
            if( green < 0 || green > 255 ) {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException ex) {
            green = color.getGreen();
            greenTextField.setText( Integer.toString(green) );
        }

        int blue;
        try {
            blue = Integer.valueOf(
                (String)blueTextField.getText()).intValue();
            if( blue < 0 || blue > 255 ) {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException ex) {
            blue = color.getBlue();
            blueTextField.setText( Integer.toString(blue) );
        }

        if( color.getRed() != red ||
            color.getGreen() != green ||
            color.getBlue() != blue )
        {
            color = new Color(red, green, blue);
        }

        return color;
    }
}
