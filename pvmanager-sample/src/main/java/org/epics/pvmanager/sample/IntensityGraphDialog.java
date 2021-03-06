/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sample;

import java.util.ArrayList;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import org.epics.graphene.NumberColorMap;
import org.epics.graphene.NumberColorMaps;

/**
 *
 * @author carcassi
 */
public class IntensityGraphDialog extends javax.swing.JDialog {

    private final IntensityGraphApp graph;
    
    /**
     * Creates new form ScatterGraphDialog
     */
    public IntensityGraphDialog(java.awt.Frame parent, boolean modal, IntensityGraphApp graph) {
        super(parent, modal);
        this.graph = graph;
        initComponents();
        colorSchemeField.setModel(new DefaultComboBoxModel<String>(new ArrayList<String>(NumberColorMaps.getRegisteredColorSchemes().keySet()).toArray(new String[0])));
        if (graph != null) {
            String currentName = null;
            for (Map.Entry<String, NumberColorMap> entry : NumberColorMaps.getRegisteredColorSchemes().entrySet()) {
                String string = entry.getKey();
                NumberColorMap numberColorMap = entry.getValue();
                if (numberColorMap == graph.getColorMap()) {
                    currentName = string;
                }
            }
            colorSchemeField.setSelectedItem(currentName);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        colorSchemeField = new javax.swing.JComboBox<String>();
        drawLegendField = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Color Scheme:");

        colorSchemeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSchemeFieldActionPerformed(evt);
            }
        });

        drawLegendField.setText("Draw Legend");
        drawLegendField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawLegendFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(colorSchemeField, 0, 279, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(drawLegendField)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(colorSchemeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drawLegendField)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void colorSchemeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSchemeFieldActionPerformed
        graph.setColorMap(NumberColorMaps.getRegisteredColorSchemes().get(colorSchemeField.getSelectedItem()));
    }//GEN-LAST:event_colorSchemeFieldActionPerformed

    private void drawLegendFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawLegendFieldActionPerformed
        graph.setDrawLegend(drawLegendField.isSelected());
    }//GEN-LAST:event_drawLegendFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IntensityGraphDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IntensityGraphDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IntensityGraphDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IntensityGraphDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IntensityGraphDialog dialog = new IntensityGraphDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> colorSchemeField;
    private javax.swing.JCheckBox drawLegendField;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
