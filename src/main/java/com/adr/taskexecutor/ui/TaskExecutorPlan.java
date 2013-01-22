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
import com.adr.taskexecutor.quartz.SchedulerInitializer;
import com.adr.taskexecutor.quartz.TaskJob;
import java.awt.Component;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author adrian
 */
public class TaskExecutorPlan extends javax.swing.JPanel implements TaskExecutorProperties, TaskExecutorUI {

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
    private static final Logger logger = Logger.getLogger(TaskExecutorPlan.class.getName());

    private String task;
    private String language;
    private String parameter;

    /** Creates new form TaskExecutorPlan */
    public TaskExecutorPlan() {
        initComponents();

        try {
            jSchedulersList.setModel(new DefaultComboBoxModel(SchedulerInitializer.getSchedulers()));
        } catch (SchedulerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        jHost.setText(Configuration.getInstance().getPreference("plan.registryhost", "localhost"));
        jPort.setText(Configuration.getInstance().getPreference("plan.registryport", "1099"));
        jInstance.setText(Configuration.getInstance().getPreference("plan.instancename", "QuartzScheduler"));

        if (jSchedulersList.getModel().getSize() == 0) {
            jRadioButton1.setEnabled(false);
            jRadioButton2.setSelected(true);
            jRadioButton2.doClick();
        } else {
            jRadioButton1.setSelected(true);
            jSchedulersList.setSelectedIndex(0);
            jRadioButton1.doClick();
        }

        jCount.setText(Formats.INTEGER.format(0));
        jInterval.setText(Formats.LONG.format(1000L));

        jOneTimeDate.setText(Formats.TIMESTAMP.format(new Date(System.currentTimeMillis() + 3600000L)));

        jStartDate.setText(Formats.TIMESTAMP.format(new Date()));

        jOptionImmediate.setSelected(true);
        jOptionImmediateActionPerformed(null);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return bundle.getString("title.executionplanned");
    }

    private void setEnabledImmediate(boolean value) {
        jLabel1.setEnabled(value);
        jCount.setEnabled(value);
        jLabel2.setEnabled(value);
        jInterval.setEnabled(value);
    }

    private void setEnabledOneTime(boolean value) {
        jLabel13.setEnabled(value);
        jOneTimeDate.setEnabled(value);
    }

    private void setEnabledCron(boolean value) {
        jLabel3.setEnabled(value);
        jStartDate.setEnabled(value);
        jLabel4.setEnabled(value);
        jEndDate.setEnabled(value);
        jLabel5.setEnabled(value);
        jCronExpression.setEnabled(value);
    }

    @Override
    public void run(TaskExecutor.RunProcess ui) throws Exception {

        Properties quartzprops = new Properties();
        quartzprops.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, jInstance.getText());
        quartzprops.setProperty(StdSchedulerFactory.PROP_SCHED_RMI_HOST, jHost.getText());
        quartzprops.setProperty(StdSchedulerFactory.PROP_SCHED_RMI_PORT, jPort.getText());
        quartzprops.setProperty(StdSchedulerFactory.PROP_SCHED_RMI_PROXY, "true");
        quartzprops.setProperty(StdSchedulerFactory.PROP_SCHED_SKIP_UPDATE_CHECK, "true");

        SchedulerFactory sf = new StdSchedulerFactory(quartzprops);
        Scheduler sched = sf.getScheduler();

        // define the job and ask it to run
        JobDetail job = new JobDetail(jJobName.getText(), jJobGroup.getText(), TaskJob.class);
        JobDataMap map = new JobDataMap();
        map.put(EnvironmentFactory.LOADER_DECORATOR_TEXTTASK, task);
        map.put(EnvironmentFactory.LOADER_LANGUAGE, language);
        // map.put(TaskJob.TASK_NAME_KEY, null);
        map.put(TaskJob.TASK_PARAMETER_KEY, parameter);
        job.setJobDataMap(map);

        Trigger trigger;
        if (jOptionImmediate.isSelected()) {
            trigger = TriggerUtils.makeImmediateTrigger(Formats.INTEGER.parse(jCount.getText()), Formats.LONG.parse(jInterval.getText()));
        } else if (jOptionOneTime.isSelected()) {
            trigger = new SimpleTrigger(jTriggerName.getText(), jTriggerGroup.getText(), Formats.TIMESTAMP.parse(jOneTimeDate.getText()));
        } else { // scheduled
            trigger = new CronTrigger(
                    jTriggerName.getText(), jTriggerGroup.getText(),
                    jJobName.getText(), jJobGroup.getText(),
                    Formats.TIMESTAMP.parse(jStartDate.getText()),
                    Formats.TIMESTAMP.parse(jEndDate.getText()),
                    jCronExpression.getText());
        }
        trigger.setName(jTriggerName.getText());
        trigger.setGroup(jTriggerGroup.getText());
        trigger.setJobName(jJobName.getText());
        trigger.setJobGroup(jJobGroup.getText());

        // schedule the job !!!
        sched.scheduleJob(job, trigger);

        ui.printSuccess(bundle.getString("message.plansuccess"));

        // save preferences
        Configuration.getInstance().setPreference("plan.registryhost", jHost.getText());
        Configuration.getInstance().setPreference("plan.registryport", jPort.getText());
        Configuration.getInstance().setPreference("plan.instancename", jInstance.getText());
        Configuration.getInstance().flushPreferences();

//        } catch (ParseException ex) {
//        } catch (SchedulerException ex) {
    }

    @Override
    public void setEnabledUI(boolean value) {
        // No need to do anything. This panel is a dialog;
    }

    public void setTaskName(String taskname) {
        jJobName.setText(taskname);
        jJobGroup.setText(Scheduler.DEFAULT_GROUP);
        jTriggerName.setText(taskname + "-trg");
        jTriggerGroup.setText(Scheduler.DEFAULT_GROUP);
    }

    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * @return the task language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the task language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        ServerGroup = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jHost = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPort = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jInstance = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jSchedulersList = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jOptionImmediate = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jCount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jInterval = new javax.swing.JTextField();
        jOptionOneTime = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jStartDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jEndDate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jCronExpression = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jJobName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jJobGroup = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTriggerName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTriggerGroup = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jOneTimeDate = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("label.taskserver"))); // NOI18N

        ServerGroup.add(jRadioButton1);
        jRadioButton1.setText(bundle.getString("label.local")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        ServerGroup.add(jRadioButton2);
        jRadioButton2.setText(bundle.getString("label.remote")); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("label.registryhost")); // NOI18N

        jLabel7.setText(bundle.getString("label.registryport")); // NOI18N

        jPort.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        jLabel8.setText(bundle.getString("label.instancename")); // NOI18N

        jLabel14.setText(bundle.getString("label.instancename")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jHost, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPort, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSchedulersList, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jSchedulersList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jInstance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("label.jobdetails"))); // NOI18N

        buttonGroup1.add(jOptionImmediate);
        jOptionImmediate.setText(bundle.getString("label.immediate")); // NOI18N
        jOptionImmediate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionImmediateActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("label.times")); // NOI18N

        jCount.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        jLabel2.setText(bundle.getString("label.interval")); // NOI18N

        jInterval.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        buttonGroup1.add(jOptionOneTime);
        jOptionOneTime.setText(bundle.getString("label.onetime")); // NOI18N
        jOptionOneTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionOneTimeActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText(bundle.getString("label.cron")); // NOI18N
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("label.starttime")); // NOI18N

        jLabel4.setText(bundle.getString("label.endtime")); // NOI18N

        jLabel5.setText(bundle.getString("label.cronexpression")); // NOI18N

        jCronExpression.setText("0 0 12 * * ?");

        jLabel9.setText(bundle.getString("label.jobname")); // NOI18N

        jLabel10.setText(bundle.getString("label.group")); // NOI18N

        jLabel11.setText(bundle.getString("label.triggername")); // NOI18N

        jLabel12.setText(bundle.getString("label.group")); // NOI18N

        jLabel13.setText(bundle.getString("label.time")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jJobName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jJobGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jOptionOneTime, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTriggerName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jCount, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTriggerGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jOptionImmediate, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jOneTimeDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCronExpression, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jJobName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jJobGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTriggerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTriggerGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jOptionImmediate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jOptionOneTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jOneTimeDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jCronExpression, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jOptionImmediateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionImmediateActionPerformed

        setEnabledImmediate(true);
        setEnabledOneTime(false);
        setEnabledCron(false);

    }//GEN-LAST:event_jOptionImmediateActionPerformed

    private void jOptionOneTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionOneTimeActionPerformed

        setEnabledImmediate(false);
        setEnabledOneTime(true);
        setEnabledCron(false);

    }//GEN-LAST:event_jOptionOneTimeActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed

        setEnabledImmediate(false);
        setEnabledOneTime(false);
        setEnabledCron(true);

    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed

        jSchedulersList.setEnabled(true);
        jInstance.setEnabled(false);
        jHost.setEnabled(false);
        jPort.setEnabled(false);

    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed

        jSchedulersList.setEnabled(false);
        jInstance.setEnabled(true);
        jHost.setEnabled(true);
        jPort.setEnabled(true);

    }//GEN-LAST:event_jRadioButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup ServerGroup;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField jCount;
    private javax.swing.JTextField jCronExpression;
    private javax.swing.JTextField jEndDate;
    private javax.swing.JTextField jHost;
    private javax.swing.JTextField jInstance;
    private javax.swing.JTextField jInterval;
    private javax.swing.JTextField jJobGroup;
    private javax.swing.JTextField jJobName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jOneTimeDate;
    private javax.swing.JRadioButton jOptionImmediate;
    private javax.swing.JRadioButton jOptionOneTime;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jPort;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JComboBox jSchedulersList;
    private javax.swing.JTextField jStartDate;
    private javax.swing.JTextField jTriggerGroup;
    private javax.swing.JTextField jTriggerName;
    // End of variables declaration//GEN-END:variables

}
