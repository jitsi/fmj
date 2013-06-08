package net.sf.fmj.ui.objeditor;

import java.awt.*;

import javax.swing.*;

/**
 * Generic OK cancel dialog with an ObjEditor inside.
 *
 * @author Ken Larson
 *
 */
public class ObjEditorOKCancelDialog
{
    public static Object run(Frame parent, final ObjEditor objEditor, Object o,
            String title)
    {
        objEditor.setObjectAndUpdateControl(o);

        // TODO: center on parent
        final JDialog d = new JDialog(parent);
        d.setTitle(title);
        final Component p = objEditor.getComponent();

        d.getContentPane().add(p, java.awt.BorderLayout.CENTER);

        JPanel panelButtons = new javax.swing.JPanel();
        JButton buttonOK = new javax.swing.JButton();
        JButton buttonCancel = new javax.swing.JButton();

        panelButtons.setLayout(new java.awt.GridBagLayout());

        final Object[] result = new Object[1]; // primitive object holder

        buttonOK.setText("OK");
        buttonOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                // buttonOKActionPerformed(evt);
                if (!objEditor.validateAndUpdateObj())
                    return;
                result[0] = objEditor.getObject();
                d.dispose();
            }
        });

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelButtons.add(buttonOK, gridBagConstraints);

        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                // buttonCancelActionPerformed(evt);
                d.dispose();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelButtons.add(buttonCancel, gridBagConstraints);

        d.getContentPane().add(panelButtons, java.awt.BorderLayout.SOUTH);

        d.setModal(true);
        d.pack();
        d.setLocationRelativeTo(parent);

        d.setVisible(true);

        return result[0];

    }
}
