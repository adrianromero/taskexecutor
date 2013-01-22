//    Task Executor is a simple script tasks executor.
//    Copyright (C) 2011 Adrián Romero Corchado.
//
//    This file is part of Task Executor
//
//    Task Executor is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Task Executor is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Task Executor. If not, see <http://www.gnu.org/licenses/>.

package com.adr.taskexecutor.ui;

import com.adr.taskexecutor.engine.EnvironmentFactory;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author adrian
 */
public class About extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(About.class.getName());
    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N

    public static final String APP_NAME = "Task Executor";
    public static final String APP_VERSION = "0.90.0";
    public static final String APP_COPYRIGHT = "Copyright (c) 2011 Adrián Romero Corchado.";

    /** Creates new form About */
    public About(java.awt.Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();

        Rectangle screenSize = parent.getBounds();
        this.setLocation(screenSize.x + (screenSize.width - this.getWidth()) / 2, screenSize.y + (screenSize.height - this.getHeight())/2);

        this.getRootPane().setDefaultButton(jOK);

        addAboutText(align22(bundle.getString("label.product")) + " ", Color.BLACK, true);
        addAboutText(APP_NAME + " " + APP_VERSION, Color.BLACK, false);
        addAboutText("\n", Color.BLACK, false);
        addAboutText(align22(bundle.getString("label.java")) + " ", Color.BLACK, true);
        addAboutText(System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version") + "; ", Color.BLACK, false);
        addAboutText("\n", Color.BLACK, false);
        addAboutText("                       ", Color.BLACK, true);
        addAboutText(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"), Color.BLACK, false);
        addAboutText("\n", Color.BLACK, false);
        addAboutText(align22(bundle.getString("label.system")) + " ", Color.BLACK, true);
        addAboutText(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "; " +  System.getProperty("file.encoding") + "; " +  EnvironmentFactory.parseLocale(System.getProperty("user.language"), System.getProperty("user.country"), System.getProperty("user.variant")).toString(), Color.BLACK, false);
        addAboutText("\n", Color.BLACK, false);
        addAboutText(align22(bundle.getString("label.workdir")) + " ", Color.BLACK, true);
        addAboutText(Configuration.getInstance().getWorkingDir(), Color.BLACK, false);
    }

    private static String align22(String value) {
        return (value + "                      ").substring(0, 22);
    }

    private void addAboutText(final String text, final Color color, final boolean bold) {
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                StyledDocument doc = textAbout.getStyledDocument();
                MutableAttributeSet attr = new SimpleAttributeSet();

                StyleConstants.setForeground(attr, color);
    //            StyleConstants.setBackground(attr, Color.yellow);
                StyleConstants.setBold(attr, bold);
                int offset = doc.getLength();
                doc.insertString(offset, text, attr);
            } catch (BadLocationException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }});
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jOK = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAbout = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        setTitle(bundle.getString("label.about")); // NOI18N
        setResizable(false);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jOK.setText(bundle.getString("label.ok")); // NOI18N
        jOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOKActionPerformed(evt);
            }
        });
        jPanel1.add(jOK);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()+12f));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/128x128/softwareD.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel2.add(jLabel1, java.awt.BorderLayout.LINE_START);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()+12f));
        jLabel2.setText(bundle.getString("title.application")); // NOI18N
        jPanel5.add(jLabel2);

        jLabel3.setText(APP_COPYRIGHT);
        jPanel5.add(jLabel3);

        jPanel4.add(jPanel5, new java.awt.GridBagConstraints());

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        textAbout.setEditable(false);
        textAbout.setFont(new java.awt.Font("Monospaced", 0, 12));
        jScrollPane2.setViewportView(textAbout);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(581, 368));
    }// </editor-fold>//GEN-END:initComponents

    private void jOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOKActionPerformed

        dispose();

    }//GEN-LAST:event_jOKActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton jOK;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane textAbout;
    // End of variables declaration//GEN-END:variables

}
