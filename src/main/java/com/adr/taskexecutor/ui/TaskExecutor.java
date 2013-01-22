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

import com.adr.taskexecutor.engine.StopWatch;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author adrian
 */
public class TaskExecutor extends javax.swing.JPanel implements DocumentListener {

    private static final Logger logger = Logger.getLogger(TaskExecutor.class.getName());
    private static String DEFAULT = "default";

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N

    private TaskProvider tp = null;
    private boolean silent = false;

    private StatsModel stmodel = new StatsModel();
    private TaskExecutorLocal execlocal;


    private Thread process = null;
    private TaskExecutorUI processexec = null;

    /** Creates new form TaskExecutor */
    public TaskExecutor() {

        initComponents();

        textParameters.setFont(new Font(Font.MONOSPACED, textParameters.getFont().getStyle(), textParameters.getFont().getSize()));
        textParameters.getDocument().addDocumentListener(this);

        textOutput.setFont(new Font(Font.MONOSPACED, textOutput.getFont().getStyle(), textOutput.getFont().getSize()));

        jTable1.setModel(stmodel);

        // SwingConstants: LEFT, CENTER, RIGHT, LEADING, or TRAILING
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setDefaultRenderer(Object.class, new StatsCellRenderer(new int[]{
            SwingConstants.LEADING,
            SwingConstants.LEADING,
            SwingConstants.TRAILING,
            SwingConstants.TRAILING, 
            SwingConstants.TRAILING, 
            SwingConstants.TRAILING,
            SwingConstants.TRAILING
        }));
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        PopUp.addPopup(textParameters, true);

        PopUp.addPopup(textOutput, false);

        // Local parameter.
        execlocal = new TaskExecutorLocal();
        jPanel3.add(execlocal, BorderLayout.CENTER);
    }

    public void init(TaskProvider tp) {
        this.tp = tp;

        jConfig.removeAllItems();

        if (tp == null) {
            jConfig.setEnabled(false);
            jNew.setEnabled(false);
            jDelete.setEnabled(false);
            jRun.setEnabled(false);
            jexeclocal.setEnabled(false);
            jexecremote.setEnabled(false);
            jexecplan.setEnabled(false);
        } else {               
            jConfig.setEnabled(process == null);
            jNew.setEnabled(process == null);
            jDelete.setEnabled(process == null && !DEFAULT.equals(jConfig.getSelectedItem()));
            jRun.setEnabled(process == null);
            jexeclocal.setEnabled(process == null);
            jexecremote.setEnabled(process == null);
            jexecplan.setEnabled(process == null);
            
            ConfigurationsMap paramsconfig = tp.getConfigurations();
            jConfig.addItem(DEFAULT);
            if (!paramsconfig.containsKey(DEFAULT)) {
                paramsconfig.put(DEFAULT, "");
            }
            for (String key: paramsconfig.keySet()) {
                if (!DEFAULT.equals(key)) {
                    jConfig.addItem(key);
                }
            }
            jConfig.setSelectedItem(DEFAULT);
        }
    }

    public Component getToolbar() {
        return jToolbar;
    }

    public Component[] getMenu() {
        return new Component[] {
            jexecseparator2,
            jexeclocal,
            jexecremote,
            jexecplan,
            jexecseparator,
            jexecstop,
            jexecclear
        };
    }

    private void start(TaskExecutorUI proc) {

        if (process == null) {

            jTabbedPane1.setSelectedComponent(jOutput); // Select output tab
            clearOutputText();
            stmodel.reset();

            processexec = proc;
            process = new Thread(new RunProcess(proc));
            
            jRun.setEnabled(false);
            jexeclocal.setEnabled(false);
            jexecremote.setEnabled(false);
            jexecplan.setEnabled(false);
            jConfig.setEnabled(false);
            jNew.setEnabled(false);
            jDelete.setEnabled(false);
            textParameters.setEnabled(false);
            processexec.setEnabledUI(false);
            execlocal.setEnabledUI(false);
            jStop.setEnabled(true);
            jexecstop.setEnabled(true);

            process.start();
        }

    }

    private void stop() {

        if (process != null) {
            process.interrupt();

            jRun.setEnabled(tp != null);
            jexeclocal.setEnabled(tp != null);
            jexecremote.setEnabled(tp != null);
            jexecplan.setEnabled(tp != null);
            jNew.setEnabled(tp != null);
            jDelete.setEnabled(tp != null && !DEFAULT.equals(jConfig.getSelectedItem()));
            jConfig.setEnabled(tp != null);
            textParameters.setEnabled(true);
            processexec.setEnabledUI(true);
            execlocal.setEnabledUI(true);
            jStop.setEnabled(false);
            jexecstop.setEnabled(false);

            processexec = null;
            process = null;
        }
    }

    private void textParametersSetText(String text) {
        silent = true;
        textParameters.setText(text);
        silent = false;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        String p = (String) jConfig.getSelectedItem();
        if (p != null) {
            tp.getConfigurations().put(p, textParameters.getText(), silent);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        String p = (String) jConfig.getSelectedItem();
        if (p != null) {
            tp.getConfigurations().put(p, textParameters.getText(), silent);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        String p = (String) jConfig.getSelectedItem();
        if (p != null) {
            tp.getConfigurations().put(p, textParameters.getText(), silent);
        }
    }

    public class RunProcess implements Runnable {

        private TaskExecutorUI exec;

        public RunProcess(TaskExecutorUI exec) {
            this.exec = exec;
        }

        public StatsModel getStatsModel() {
            return stmodel;
        }

        public void buildMessage(String title) {
            addOutputText(bundle.getString("message.build") + " " + title + "\n", Color.GRAY);
        }

        public void runMessage(String title) {
            addOutputText(bundle.getString("message.run") + "\n", Color.GRAY);
        }

        public void printLog(String line) {
            addOutputText(line, Color.RED.darker());
        }
        
        public void printOut(String line) {
            addOutputText(line, Color.BLACK);
        }

        public void printSuccess(String line) {
            addOutputText(line, Color.GREEN.darker());
        }

        public void successMessage(long millis) {
            addOutputText("\n" + MessageFormat.format(bundle.getString("message.runsuccess"), StopWatch.getTimeElapsedLocalized(millis)) + "\n", Color.GREEN.darker());
        }

        public void failMessage(String result, long millis) {
            addOutputText("\n" + MessageFormat.format(bundle.getString("message.runfail"),  StopWatch.getTimeElapsedLocalized(millis)) + "\n", Color.RED.darker());
            addOutputText(result, Color.RED.darker());
        }

        public void exceptionMessage(String ex, long millis) {
            addOutputText("\n" + MessageFormat.format(bundle.getString("message.runexception"),  StopWatch.getTimeElapsedLocalized(millis)) + "\n", Color.RED.darker());
            addOutputText(ex, Color.RED.darker());
        }

        private void addOutputText(final String text, final Color color) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        StyledDocument doc = textOutput.getStyledDocument();
                        MutableAttributeSet attr = new SimpleAttributeSet();

                        StyleConstants.setForeground(attr, color);
            //            StyleConstants.setBackground(attr, Color.yellow);
            //            StyleConstants.setBold(attr,true);
                        int offset = doc.getLength();
                        doc.insertString(offset, text, attr);
                    } catch (BadLocationException ex) {
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            });
        }

        @Override
        public void run() {

            try {
                exec.run(this);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                printLog(bundle.getString("message.executionexception") + "\n");
                StringWriter trace = new StringWriter();
                ex.printStackTrace(new PrintWriter(trace));
                printLog(trace.toString());
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            });
        }
    }

    private void clearOutputText() {
        try {
            StyledDocument doc = textOutput.getStyledDocument();
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private static class StatsCellRenderer extends DefaultTableCellRenderer {

        private int[] columnalign;

        public StatsCellRenderer(int[] columnalign) {
            this.columnalign = columnalign;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){

            JLabel aux = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            aux.setHorizontalAlignment(columnalign[column]);
            return aux;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolbar = new javax.swing.JToolBar();
        jRun = new javax.swing.JButton();
        jStop = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jMenu1 = new javax.swing.JMenu();
        jexecseparator2 = new javax.swing.JPopupMenu.Separator();
        jexeclocal = new javax.swing.JMenuItem();
        jexecremote = new javax.swing.JMenuItem();
        jexecplan = new javax.swing.JMenuItem();
        jexecseparator = new javax.swing.JPopupMenu.Separator();
        jexecstop = new javax.swing.JMenuItem();
        jexecclear = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textParameters = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jConfig = new javax.swing.JComboBox();
        jNew = new javax.swing.JButton();
        jDelete = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jOutput = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jToolbar.setFloatable(false);
        jToolbar.setRollover(true);

        jRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_play.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jRun.setText(bundle.getString("label.run")); // NOI18N
        jRun.setEnabled(false);
        jRun.setFocusable(false);
        jRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRunActionPerformed(evt);
            }
        });
        jToolbar.add(jRun);

        jStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_stop.png"))); // NOI18N
        jStop.setText(bundle.getString("label.stop")); // NOI18N
        jStop.setEnabled(false);
        jStop.setFocusable(false);
        jStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopActionPerformed(evt);
            }
        });
        jToolbar.add(jStop);
        jToolbar.add(jSeparator1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/locationbar_erase.png"))); // NOI18N
        jButton2.setText(bundle.getString("label.clear")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolbar.add(jButton2);

        jMenu1.setText("jMenu1");
        jMenu1.add(jexecseparator2);

        jexeclocal.setText(bundle.getString("menu.execlocal")); // NOI18N
        jexeclocal.setEnabled(false);
        jexeclocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jexeclocalActionPerformed(evt);
            }
        });
        jMenu1.add(jexeclocal);

        jexecremote.setText(bundle.getString("menu.execremote")); // NOI18N
        jexecremote.setEnabled(false);
        jexecremote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jexecremoteActionPerformed(evt);
            }
        });
        jMenu1.add(jexecremote);

        jexecplan.setText(bundle.getString("menu.execplan")); // NOI18N
        jexecplan.setEnabled(false);
        jexecplan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jexecplanActionPerformed(evt);
            }
        });
        jMenu1.add(jexecplan);
        jMenu1.add(jexecseparator);

        jexecstop.setText(bundle.getString("label.stop")); // NOI18N
        jexecstop.setEnabled(false);
        jexecstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jexecstopActionPerformed(evt);
            }
        });
        jMenu1.add(jexecstop);

        jexecclear.setText(bundle.getString("label.clear")); // NOI18N
        jexecclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jexecclearActionPerformed(evt);
            }
        });
        jMenu1.add(jexecclear);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(textParameters);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel3.setText(bundle.getString("label.configuration")); // NOI18N

        jConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConfigActionPerformed(evt);
            }
        });

        jNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/edit.png"))); // NOI18N
        jNew.setToolTipText("New");
        jNew.setFocusable(false);
        jNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewActionPerformed(evt);
            }
        });

        jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/editdelete.png"))); // NOI18N
        jDelete.setToolTipText("Delete");
        jDelete.setFocusable(false);
        jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNew, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 12, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNew)
                            .addComponent(jDelete))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.addTab(bundle.getString("label.parameter"), jPanel2); // NOI18N

        jPanel3.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(bundle.getString("label.environment"), jPanel3); // NOI18N

        jOutput.setLayout(new java.awt.BorderLayout());

        textOutput.setEditable(false);
        jScrollPane2.setViewportView(textOutput);

        jOutput.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(bundle.getString("label.output"), jOutput); // NOI18N

        jPanel5.setLayout(new java.awt.BorderLayout());

        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(jTable1);

        jPanel5.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(bundle.getString("label.statistics"), jPanel5); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRunActionPerformed

        // Selecting local process...
        execlocal.setTask(tp.getTaskText());
        execlocal.setLanguage(tp.getTaskLanguage());
        execlocal.setParameter(textParameters.getText());
        start(execlocal);

    }//GEN-LAST:event_jRunActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        clearOutputText();
        stmodel.reset();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStopActionPerformed

        stop();
        
    }//GEN-LAST:event_jStopActionPerformed

    private void jexeclocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jexeclocalActionPerformed

        // Selecting local process...
        execlocal.setTask(tp.getTaskText());
        execlocal.setLanguage(tp.getTaskLanguage());
        execlocal.setParameter(textParameters.getText());
        start(execlocal);

    }//GEN-LAST:event_jexeclocalActionPerformed

    private void jexecstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jexecstopActionPerformed

        stop();

    }//GEN-LAST:event_jexecstopActionPerformed

    private void jexecclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jexecclearActionPerformed

        clearOutputText();
        stmodel.reset();
        
    }//GEN-LAST:event_jexecclearActionPerformed

    private void jexecremoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jexecremoteActionPerformed

        TaskExecutorRemote remote = new TaskExecutorRemote();
        TaskExecutorDialog dialog = new TaskExecutorDialog(SwingUtilities.getWindowAncestor(this), remote);
        
        dialog.setVisible(true);

        if (dialog.getOK()) {
            // Selecting remote process...
            remote.setTask(tp.getTaskText());
            remote.setLanguage(tp.getTaskLanguage());
            remote.setParameter(textParameters.getText());
            start(remote);
        }

    }//GEN-LAST:event_jexecremoteActionPerformed

    private void jexecplanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jexecplanActionPerformed

        TaskExecutorPlan plan = new TaskExecutorPlan();
        plan.setTaskName(tp.getTaskName());

        TaskExecutorDialog dialog = new TaskExecutorDialog(SwingUtilities.getWindowAncestor(this), plan);
        dialog.setVisible(true);

        if (dialog.getOK()) {
            plan.setTask(tp.getTaskText());
            plan.setLanguage(tp.getTaskLanguage());
            plan.setParameter(textParameters.getText());
            start(plan);
        }
    }//GEN-LAST:event_jexecplanActionPerformed

    private void jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteActionPerformed

        String p = (String) jConfig.getSelectedItem();
        if (p != null) {
            tp.getConfigurations().remove(p);
            jConfig.removeItem(p);
        }
    }//GEN-LAST:event_jDeleteActionPerformed

    private void jNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewActionPerformed

        String p = JOptionPane.showInputDialog(this, bundle.getString("message.configurationname"), bundle.getString("title.configurationname"), JOptionPane.QUESTION_MESSAGE);
        if (p!= null) {
            p = p.replace(' ', '_');
            p = p.replace('?', '_');
            p = p.replace('*', '_');
            p = p.replace('/', '_');
            p = p.replace('\\', '_');
            p = p.replace(':', '_');
            p = p.replace(';', '_');
            p = p.replace('\r', '_');
            p = p.replace('\n', '_');
            p = p.replace('\t', '_');
            tp.getConfigurations().put(p, "");
            jConfig.addItem(p);
            jConfig.setSelectedItem(p);
        }
    }//GEN-LAST:event_jNewActionPerformed

    private void jConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConfigActionPerformed

        String p = (String) jConfig.getSelectedItem();
        if (p != null) {
            textParametersSetText(tp.getConfigurations().get(p));
            textParameters.setCaretPosition(0);
            jDelete.setEnabled(!DEFAULT.equals(p));
        }
    }//GEN-LAST:event_jConfigActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jConfig;
    private javax.swing.JButton jDelete;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JButton jNew;
    private javax.swing.JPanel jOutput;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton jRun;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JButton jStop;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolbar;
    private javax.swing.JMenuItem jexecclear;
    private javax.swing.JMenuItem jexeclocal;
    private javax.swing.JMenuItem jexecplan;
    private javax.swing.JMenuItem jexecremote;
    private javax.swing.JPopupMenu.Separator jexecseparator;
    private javax.swing.JPopupMenu.Separator jexecseparator2;
    private javax.swing.JMenuItem jexecstop;
    private javax.swing.JTextPane textOutput;
    private javax.swing.JTextArea textParameters;
    // End of variables declaration//GEN-END:variables

}
