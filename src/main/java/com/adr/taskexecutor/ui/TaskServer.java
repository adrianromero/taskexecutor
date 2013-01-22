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

import com.adr.taskexecutor.quartz.SchedulerInitializer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author adrian
 */
public class TaskServer extends TaskWindow {

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
    private static final Logger logger = Logger.getLogger(TaskServer.class.getName());

    private static Icon icon = new ImageIcon(TaskServer.class.getResource("/com/adr/taskexecutor/ui/images/advanced.png"));
    private static Icon iconpaused = new ComposedIcon(
            new ImageIcon(TaskServer.class.getResource("/com/adr/taskexecutor/ui/images/advanced.png")),
            new ImageIcon(TaskServer.class.getResource("/com/adr/taskexecutor/ui/images/taskpaused.png")));


    private SchedulerInitializer schedinit;
    private Properties props;

    private Scheduler sched;
    private String schedname;

    private SchedulerListener schedlistener;
    private DefaultListModel listModel;

    private TriggerNames selectedtrigger = null;

    /** Creates new form TaskServer */
    public TaskServer(SchedulerInitializer schedinit, Properties props) throws SchedulerException {

        this.schedinit = schedinit;
        this.props = props;

        this.sched = SchedulerInitializer.startup(props);
        schedname = sched.getSchedulerName();

        initComponents();

        textOutput.setFont(new Font(Font.MONOSPACED, textOutput.getFont().getStyle(), textOutput.getFont().getSize()));
        listModel = new DefaultListModel();
        jList1.setCellRenderer(new TriggerRenderer());
        jList1.setModel(listModel);

        PopUp.addPopup(textOutput, false);
             
        schedlistener = new SchedulerListener() {

            @Override
            public void jobScheduled(Trigger trigger) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobscheduled"), new Date(), trigger.getGroup(), trigger.getName()) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void jobUnscheduled(String triggerName, String triggerGroup) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobunscheduled"), new Date(), triggerGroup, triggerName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void triggerFinalized(Trigger trigger) {
                addOutputText(MessageFormat.format(bundle.getString("message.triggerfinalized"), new Date(), trigger.getGroup(), trigger.getName()) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void triggersPaused(String triggerName, String triggerGroup) {
                addOutputText(MessageFormat.format(bundle.getString("message.triggerpaused"), new Date(), triggerGroup, triggerName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void triggersResumed(String triggerName, String triggerGroup) {
                addOutputText(MessageFormat.format(bundle.getString("message.triggerresumed"), new Date(), triggerGroup, triggerName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void jobAdded(JobDetail jobDetail) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobadded"), new Date(), jobDetail.getGroup(), jobDetail.getName()) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void jobDeleted(String jobName, String groupName) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobdeleted"), new Date(), groupName, jobName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void jobsPaused(String jobName, String jobGroup) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobpaused"), new Date(), jobGroup, jobName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void jobsResumed(String jobName, String jobGroup) {
                addOutputText(MessageFormat.format(bundle.getString("message.jobresumed"), new Date(), jobGroup, jobName) + "\n", Color.BLACK);
                paintStatusActionGUI();
            }

            @Override
            public void schedulerError(String msg, SchedulerException cause) {
                addOutputText(MessageFormat.format(bundle.getString("message.schedulererror"), new Date(), msg) + "\n", Color.RED);
                paintStatusActionGUI();
            }

            @Override
            public void schedulerInStandbyMode() {
                addOutputText(MessageFormat.format(bundle.getString("message.schedulerinstandbymode"), new Date()) + "\n", Color.DARK_GRAY);
                paintStatusActionGUI();
            }

            @Override
            public void schedulerStarted() {
                addOutputText(MessageFormat.format(bundle.getString("message.schedulerstarted"), new Date()) + "\n", Color.DARK_GRAY);
                paintStatusActionGUI();
            }

            @Override
            public void schedulerShutdown() {
                addOutputText(MessageFormat.format(bundle.getString("message.schedulershutdown"), new Date()) + "\n", Color.DARK_GRAY);
                paintStatusActionGUI();
            }

            @Override
            public void schedulerShuttingdown() {
                addOutputText(MessageFormat.format(bundle.getString("message.schedulershuttingdown"), new Date()) + "\n", Color.DARK_GRAY);
                paintStatusActionGUI();
            }
        };

        sched.addSchedulerListener(schedlistener);

//        sched.addGlobalTriggerListener(new TriggerListener() {
//
//            @Override
//            public String getName() {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public void triggerFired(Trigger trigger, JobExecutionContext context) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public void triggerMisfired(Trigger trigger) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        });

        if (!sched.isStarted() && !isRemote()) {
            sched.start();
        }

        // register scheduler in schedinit

        paintStatusAction();
    }

    private void disconnect() {

        if (sched != null) {
            try {
                sched.removeSchedulerListener(schedlistener);
            } catch (SchedulerException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }

            try {
                if (!sched.isShutdown() && !isRemote()) {
                    sched.shutdown();
                }
            } catch (SchedulerException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }

            // unregister scheduler in schedinit

            schedlistener = null;
            sched = null;
        }

        paintNullStatus();
    }

    private boolean isRemote() {
        return Boolean.parseBoolean(props.getProperty(StdSchedulerFactory.PROP_SCHED_RMI_PROXY));
    }

    private void paintNullStatus() {
        jButtonStart.setEnabled(false);
        jButtonPause.setEnabled(false);
        jButtonStop.setEnabled(false);

        jTextArea2.setText(bundle.getString("message.disconnected"));

        listModel.removeAllElements();
    }

    private void paintStatus() throws SchedulerException {
        if (sched == null) {
            paintNullStatus();
        } else {

            jButtonStart.setEnabled(!sched.isShutdown() && sched.isInStandbyMode());
            jButtonPause.setEnabled(!sched.isShutdown() && !sched.isInStandbyMode());
            jButtonStop.setEnabled(!sched.isShutdown());

            jTextArea2.setText(sched.getMetaData().getSummary());

            TriggerNames currenttrigger = selectedtrigger;

            listModel.removeAllElements();
            selectTrigger();

            if (!sched.isShutdown()) {

                String[] triggerGroups;
                String[] triggerNames;

                triggerGroups = sched.getTriggerGroupNames();
                for (int i = 0; i < triggerGroups.length; i++) {
                    triggerNames = sched.getTriggerNames(triggerGroups[i]);

                    for (int j = 0; j < triggerNames.length; j++) {
                        TriggerNames trg = new TriggerNames(triggerGroups[i], triggerNames[j], sched.getTriggerState(triggerNames[j], triggerGroups[i]));
                        listModel.addElement(trg);

                        if (trg.isTheSame(currenttrigger)) {
                            jList1.setSelectedValue(trg, true);
                        }
                        // Trigger trg = sched.getTrigger(triggerNames[j], triggerGroups[i]);
                        // listModel.add(0, trg.getFullName() + "-" + trg.getFullJobName() + "-" + trg.getDescription() + "-" +  sched.getTriggerState(triggerNames[j], triggerGroups[i]));
                    }
                }

                if (listModel.size() > 0 && jList1.getSelectedValue() == null) {
                    jList1.setSelectedIndex(0);
                }
            }

        }
    }

    private void paintStatusAction() {

        try {
            paintStatus();
        } catch (SchedulerException ex) {
            Logger.getLogger(TaskServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            disconnect();
            JOptionPane.showMessageDialog(this, bundle.getString("message.taskservererror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void paintStatusActionGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    paintStatus();
                } catch (SchedulerException ex) {
                    Logger.getLogger(TaskServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    disconnect();
                    JOptionPane.showMessageDialog(TaskServer.this, bundle.getString("message.taskservererror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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
    
    private void clearOutputText() {
        try {
            StyledDocument doc = textOutput.getStyledDocument();
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void selectTrigger() {
        selectedtrigger = (TriggerNames) jList1.getSelectedValue();
        if (selectedtrigger == null) {
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
        } else {
            jButton1.setEnabled(selectedtrigger.getStatus() == Trigger.STATE_PAUSED);
            jButton2.setEnabled(selectedtrigger.getStatus() != Trigger.STATE_PAUSED);
            jButton3.setEnabled(true);
        }

    }

    @Override
    public String getTaskName() {
        return MessageFormat.format(bundle.getString(isRemote() ? "name.taskserver.remote" : "name.taskserver.local"), schedname);
    }

    @Override
    public String getFileName() {
        return MessageFormat.format(bundle.getString(isRemote() ? "label.taskserver.remote" : "label.taskserver.local"), schedname);
    }

    @Override
    public Component[] getMenu() {
        return new Component[]{};
    }

    @Override
    public Component getToolbar() {
        return jToolBar1;
    }

    @Override
    public int askClose() {

        try {
            if (sched != null && !sched.isShutdown() && !isRemote()
                    && JOptionPane.showConfirmDialog(this, bundle.getString("message.areyousureexitserver"), bundle.getString("title.taskserver"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return TaskWindow.CLOSE_FALSE;
            }
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        disconnect();
        return TaskWindow.CLOSE_TRUE;
    }
    
    @Override
    public File getFile() {
        return null;
    }

    @Override
    public boolean saveDocument(JFileChooser fc) {
        return true;
    }

    @Override
    public boolean saveDocumentAs(JFileChooser fc) {
        return true;
    }

    @Override
    public boolean canReplaceTaskEditor() {
        return false;
    }
    
    private static class TriggerNames {
        
        private String name;
        private String group;
        private int status;
        
        public TriggerNames(String group, String name, int status) {
            this.name = name;
            this.group = group;
            this.status = status;
        }
        
        public String getGroup() {
            return group;
        }
        
        public String getName() {
            return name;
        }

        public int getStatus() {
            return status;
        }

        public boolean isTheSame(TriggerNames trg) {
            if (trg == null) {
                return false;
            } else {
                return name.equals(trg.name) && group.equals(trg.group);
            }
        }
    }

    private class TriggerRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);

            TriggerNames trg = (TriggerNames) value;
            if (trg != null) {
                setText("<html>" + trg.getGroup() + "." + trg.getName());
                // setText("<html>" + prod.getReference() + " - " + prod.getName() + "<br>&nbsp;&nbsp;&nbsp;&nbsp;" + Formats.CURRENCY.formatValue(new Double(prod.getPriceSell())));

                switch (trg.getStatus()) {
                case Trigger.STATE_PAUSED:
                    setIcon(iconpaused);
                    break;
                case Trigger.STATE_NORMAL:
                    setIcon(icon);
                    break;
                default:
                    setIcon(null);
                    break;
                }
            }
            return this;
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

        jToolBar1 = new javax.swing.JToolBar();
        jButtonStart = new javax.swing.JButton();
        jButtonPause = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonStop = new javax.swing.JButton();
        jMenu1 = new javax.swing.JMenu();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        textOutput = new javax.swing.JTextPane();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButtonStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_play_green.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jButtonStart.setText(bundle.getString("label.start")); // NOI18N
        jButtonStart.setFocusable(false);
        jButtonStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonStart);

        jButtonPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_pause_green.png"))); // NOI18N
        jButtonPause.setText(bundle.getString("label.pause")); // NOI18N
        jButtonPause.setFocusable(false);
        jButtonPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPauseActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPause);
        jToolBar1.add(jSeparator1);

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_stop_green.png"))); // NOI18N
        jButtonStop.setText(bundle.getString("label.stop")); // NOI18N
        jButtonStop.setFocusable(false);
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonStop);

        jMenu1.setText("jMenu1");

        jTextArea2.setEditable(false);
        jScrollPane2.setViewportView(jTextArea2);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_play_task.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_pause_task.png"))); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/player_stop_task.png"))); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton3);

        jPanel1.add(jToolBar2, java.awt.BorderLayout.NORTH);

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(bundle.getString("label.triggers"), jPanel1); // NOI18N

        textOutput.setEditable(false);
        jScrollPane3.setViewportView(textOutput);

        jTabbedPane1.addTab(bundle.getString("label.output"), jScrollPane3); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed

        if (JOptionPane.showConfirmDialog(this, bundle.getString("message.areyousurestopserver"),bundle.getString("title.taskserver"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            try {
                sched.shutdown();
                paintStatusAction();
            } catch (SchedulerException ex) {
                Logger.getLogger(TaskServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                disconnect();
                JOptionPane.showMessageDialog(this, bundle.getString("message.taskservererror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed

        try {
            sched.start();
            paintStatusAction();
        } catch (SchedulerException ex) {
            Logger.getLogger(TaskServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            disconnect();
            JOptionPane.showMessageDialog(this, bundle.getString("message.taskservererror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jButtonPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPauseActionPerformed

        try {
            sched.standby();
            paintStatusAction();
        } catch (SchedulerException ex) {
            Logger.getLogger(TaskServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            disconnect();
            JOptionPane.showMessageDialog(this, bundle.getString("message.taskservererror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonPauseActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        TriggerNames trg = (TriggerNames) jList1.getSelectedValue();
        try {
            sched.resumeTrigger(trg.getName(), trg.getGroup());
            // sched.pauseTrigger(trg.getName(), trg.getGroup());
            // sched.unscheduleJob(trg.getName(), trg.getGroup());
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, bundle.getString("message.taskerror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        TriggerNames trg = (TriggerNames) jList1.getSelectedValue();
        try {
            // sched.resumeTrigger(trg.getName(), trg.getGroup());
            sched.pauseTrigger(trg.getName(), trg.getGroup());
            // sched.unscheduleJob(trg.getName(), trg.getGroup());
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, bundle.getString("message.taskerror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged

        if (!evt.getValueIsAdjusting()) {
            selectTrigger();
        }

    }//GEN-LAST:event_jList1ValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        if (JOptionPane.showConfirmDialog(this, bundle.getString("message.areyousure"),bundle.getString("title.taskserver"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            TriggerNames trg = (TriggerNames) jList1.getSelectedValue();
            try {
                sched.unscheduleJob(trg.getName(), trg.getGroup());
            } catch (SchedulerException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, bundle.getString("message.taskerror"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonPause;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTextPane textOutput;
    // End of variables declaration//GEN-END:variables

}
