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

import com.adr.taskexecutor.engine.TextHandler;
import com.adr.taskexecutor.engine.EnvironmentFactory;
import com.adr.taskexecutor.engine.GeneratorEnvironment;
import com.adr.taskexecutor.engine.StopWatch;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author adrian
 */
public class TaskExecutorLocal extends javax.swing.JPanel implements TaskExecutorUI {

    private String task;
    private String language;
    private String parameter;

    /** Creates new form TaskExecutorLocal */
    public TaskExecutorLocal() {
        initComponents();

        jLoggingLevel.addItem(Level.SEVERE);
        jLoggingLevel.addItem(Level.WARNING);
        jLoggingLevel.addItem(Level.INFO);
        jLoggingLevel.addItem(Level.CONFIG);
        jLoggingLevel.addItem(Level.FINE);
        jLoggingLevel.addItem(Level.FINER);
        jLoggingLevel.addItem(Level.FINEST);
        jLoggingLevel.addItem(Level.OFF);
        jLoggingLevel.addItem(Level.ALL);

        List<Locale> availablelocales = new ArrayList<Locale>();
        availablelocales.addAll(Arrays.asList(Locale.getAvailableLocales()));

        Collections.sort(availablelocales, new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        for (Locale l : availablelocales) {
            jLocales.addItem(new LocaleWrapper(l));
        }

        jWorkingFolder.setText(Configuration.getInstance().getPreference("local.workingdir", Configuration.getInstance().getWorkingDir()));
        jLoggingLevel.setSelectedItem(Level.parse(Configuration.getInstance().getPreference("remote.logginglevel", Level.INFO.toString())));
        jLocales.setSelectedItem(new LocaleWrapper(EnvironmentFactory.parseLocale(
                Configuration.getInstance().getPreference("local.language", ""),
                Configuration.getInstance().getPreference("local.country", ""),
                Configuration.getInstance().getPreference("local.variant", ""))));
        jTrace.setSelected(Boolean.parseBoolean(Configuration.getInstance().getPreference("local.trace", Boolean.FALSE.toString())));
        jStats.setSelected(Boolean.parseBoolean(Configuration.getInstance().getPreference("local.stats", Boolean.TRUE.toString())));
    }

    @Override
    public void setEnabledUI(boolean value) {

        jLoggingLevel.setEnabled(value);
        jLocales.setEnabled(value);
        jTrace.setEnabled(value);
        jStats.setEnabled(value);
        jWorkingFolder.setEnabled(value);
    }

    @Override
    public void run(TaskExecutor.RunProcess ui) throws Exception {

        StopWatch watch = new StopWatch();
        GeneratorEnvironment environment = null;
        try {
            ui.buildMessage("");

            Properties props = new Properties();
            props.setProperty(EnvironmentFactory.LOADER_WORKING_FOLDER, jWorkingFolder.getText());
            props.setProperty(EnvironmentFactory.LOADER_LANGUAGE, language);
            props.setProperty(EnvironmentFactory.LOADER_DECORATOR_MEMCACHE, Boolean.TRUE.toString());
            props.setProperty(EnvironmentFactory.LOADER_DECORATOR_TEXTTASK, getTask());
            props.setProperty(EnvironmentFactory.ENVIRONMENT_TRACE, Boolean.toString(jTrace.isSelected()));
            if (Configuration.getInstance().getStatsCollector() != null) {
                props.setProperty(EnvironmentFactory.ENVIRONMENT_STATS_LOG_FILE, Configuration.getInstance().getStatsCollector());
            }

            Locale locale = ((LocaleWrapper) jLocales.getSelectedItem()).locale;
            props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_LANGUAGE, locale.getLanguage());
            props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_COUNTRY, locale.getCountry());
            props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_VARIANT, locale.getVariant());
            EnvironmentFactory factory = new EnvironmentFactory(props);

            // Create environment
            environment = factory.create();
            if (jStats.isSelected()) {
                environment.getStats().addStatsCollector(new StatsCollectorTable(ui.getStatsModel()));
            }
            ExecutorLogOutputStream execlog = new ExecutorLogOutputStream(ui);
            environment.addHandler(new TextHandler(execlog, execlog.getEncoding(), (Level) jLoggingLevel.getSelectedItem()));
            environment.addOut(new ExecutorOutWriter(ui));
            environment.addErr(new ExecutorOutWriter(ui));

            ui.runMessage("");

            environment.start();
            Object result = environment.runProcess(null, getParameter());

            if (result == null) {
                ui.successMessage(watch.getMillisElapsed());
            } else {
                ui.failMessage(result.toString(), watch.getMillisElapsed());
            }

            // save preferences
            Configuration.getInstance().setPreference("local.workingdir", jWorkingFolder.getText());
            Configuration.getInstance().setPreference("local.logginglevel", jLoggingLevel.getSelectedItem().toString());
            Configuration.getInstance().setPreference("local.language", ((LocaleWrapper) jLocales.getSelectedItem()).getLocale().getLanguage());
            Configuration.getInstance().setPreference("local.country", ((LocaleWrapper) jLocales.getSelectedItem()).getLocale().getCountry());
            Configuration.getInstance().setPreference("local.variant", ((LocaleWrapper) jLocales.getSelectedItem()).getLocale().getVariant());
            Configuration.getInstance().setPreference("local.trace", Boolean.toString(jTrace.isSelected()));
            Configuration.getInstance().setPreference("local.stats", Boolean.toString(jStats.isSelected()));
            Configuration.getInstance().flushPreferences();

        // Much more exceptions to catch
        } catch (Exception ex) {
            StringWriter trace = new StringWriter();
            ex.printStackTrace(new PrintWriter(trace));
            ui.exceptionMessage(trace.toString(), watch.getMillisElapsed());
        } finally {
            if (environment != null) {
                environment.stop();
            }
        }
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

    private class ExecutorOutWriter extends Writer {

        private TaskExecutor.RunProcess ui;

        public ExecutorOutWriter(TaskExecutor.RunProcess ui) {
            this.ui = ui;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            ui.printOut(new String(cbuf, off, len));
        }
        @Override
        public void flush() throws IOException {
        }
        @Override
        public void close() throws IOException {
        }
    }

    private static class LocaleWrapper {
        private Locale locale;

        public LocaleWrapper(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return locale.getDisplayName();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + (this.locale != null ? this.locale.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LocaleWrapper other = (LocaleWrapper) obj;
            if (this.locale != other.locale && (this.locale == null || !this.locale.equals(other.locale))) {
                return false;
            }
            return true;
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

        jLabel1 = new javax.swing.JLabel();
        jWorkingFolder = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLoggingLevel = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLocales = new javax.swing.JComboBox();
        jTrace = new javax.swing.JCheckBox();
        jStats = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.workingfolder")); // NOI18N

        jLabel2.setText(bundle.getString("label.logginglevel")); // NOI18N

        jLabel4.setText(bundle.getString("label.locale")); // NOI18N

        jTrace.setText(bundle.getString("label.trace")); // NOI18N

        jStats.setText(bundle.getString("label.statistics")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jWorkingFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLoggingLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTrace, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLocales, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStats, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jWorkingFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLoggingLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLocales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTrace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStats)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jLocales;
    private javax.swing.JComboBox jLoggingLevel;
    private javax.swing.JCheckBox jStats;
    private javax.swing.JCheckBox jTrace;
    private javax.swing.JTextField jWorkingFolder;
    // End of variables declaration//GEN-END:variables

}
