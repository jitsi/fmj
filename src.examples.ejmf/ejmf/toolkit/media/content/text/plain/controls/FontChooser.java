package ejmf.toolkit.media.content.text.plain.controls;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class FontChooser extends JPanel implements ActionListener {
    private static final int GAP = 10;
    private static final Border
        emptyBorder  = new EmptyBorder(GAP,GAP,GAP,GAP),
        etchedBorder = new CompoundBorder(new EtchedBorder(), emptyBorder);

    private JLabel nameLabel  = new JLabel("Name:", JLabel.RIGHT);
    private JLabel styleLabel = new JLabel("Style:", JLabel.RIGHT);
    private JLabel sizeLabel  = new JLabel("Size:", JLabel.RIGHT);

    private JComboBox nameCombo;
    private JCheckBox boldCheckBox = new JCheckBox("Bold");
    private JCheckBox italicCheckBox = new JCheckBox("Italic");
    private JTextField sizeField = new JTextField(3);

    private JPanel labelPanel = new JPanel();
    private JPanel inputPanel = new JPanel();

    private Font font;
    private Component c;

    public FontChooser(Component c) {
        super();
        this.c = c;

        //  Set up Combo Box
        nameCombo = new JComboBox(
            Toolkit.getDefaultToolkit().getFontList() );
        nameCombo.setEditable(false);

        //  Load font
        font = c.getFont();
        loadFont();

        setUpListeners();

        labelPanel.setLayout( new GridLayout(4,1,GAP,GAP) );
        inputPanel.setLayout( new GridLayout(4,1,GAP,GAP) );

        labelPanel.add(nameLabel);
        labelPanel.add(styleLabel);
        labelPanel.add(new JLabel());
        labelPanel.add(sizeLabel);

        JPanel namePanel = new JPanel( new BorderLayout() );
        namePanel.add(nameCombo, BorderLayout.WEST);

        JPanel sizePanel = new JPanel( new BorderLayout() );
        sizePanel.add(sizeField, BorderLayout.WEST);

        inputPanel.add(namePanel);
        inputPanel.add(boldCheckBox);
        inputPanel.add(italicCheckBox);
        inputPanel.add(sizePanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout(GAP, GAP) );
        mainPanel.setBorder( new TitledBorder(etchedBorder, "Font Control") );
        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadFont() {
        nameCombo.setSelectedItem(font.getName());
        boldCheckBox.setSelected(font.isBold());
        italicCheckBox.setSelected(font.isItalic());
        sizeField.setText( Integer.toString(font.getSize()) );
    }

    private void setUpListeners() {
        nameCombo.addActionListener(this);
        boldCheckBox.addActionListener(this);
        italicCheckBox.addActionListener(this);
        sizeField.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String name = (String)nameCombo.getSelectedItem();
        int style = Font.PLAIN;

        if( boldCheckBox.isSelected() ) {
            style |= Font.BOLD;
        }

        if( italicCheckBox.isSelected() ) {
            style |= Font.ITALIC;
        }

        int size;

        try {
            size = Integer.valueOf((String)sizeField.getText()).intValue();
        } catch(NumberFormatException ex) {
            size = font.getSize();
            sizeField.setText( Integer.toString(size) );
        }

        if( ! font.getName().equals(name) ||
              font.getStyle() != style ||
              font.getSize() != size )
        {
            font = new Font(name, style, size);
            c.setFont(font);
        }
    }
}
