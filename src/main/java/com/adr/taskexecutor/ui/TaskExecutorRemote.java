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

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author adrian
 */
public class TaskExecutorRemote extends javax.swing.JPanel implements TaskExecutorProperties, TaskExecutorUI {

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
    private static final Logger logger = Logger.getLogger(TaskExecutorRemote.class.getName());

    private String task;
    private String language;
    private String parameter;

    /** Creates new form TaskExecutorRemote */
    public TaskExecutorRemote() {
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

        jURL.setText(Configuration.getInstance().getPreference("remote.serverurl", "http://localhost/taskexecutoree/executetask"));
        jLoggingLevel.setSelectedItem(Level.parse(Configuration.getInstance().getPreference("remote.logginglevel", Level.INFO.toString())));
        jTrace.setSelected(Boolean.parseBoolean(Configuration.getInstance().getPreference("remote.trace", Boolean.FALSE.toString())));
        jStats.setSelected(Boolean.parseBoolean(Configuration.getInstance().getPreference("remote.stats", Boolean.TRUE.toString())));
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return bundle.getString("title.executorremote");
    }

    @Override
    public void run(TaskExecutor.RunProcess ui) throws Exception {

        URL url;
        BufferedReader readerin = null;
        Writer writerout = null;
        try {
            // http://localhost:8084/taskexecutor/executetask?parameter=&loglevel=INFO&trace=false&stats=true&task=
            url = new URL(jURL.getText());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setAllowUserInteraction(false);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            String query = "&parameter=" + URLEncoder.encode(parameter, "UTF-8");
            query += "&task=" + URLEncoder.encode(task, "UTF-8");
            query += "&language=" + URLEncoder.encode(language, "UTF-8");
            query += "&loglevel=" + URLEncoder.encode(jLoggingLevel.getSelectedItem().toString(), "UTF-8");
            query += "&trace=" + URLEncoder.encode(Boolean.toString(jTrace.isSelected()), "UTF-8");
            query += "&stats=" + URLEncoder.encode(Boolean.toString(jStats.isSelected()), "UTF-8");

            writerout = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writerout.write("Content-Type: application/x-www- form-urlencoded,encoding=UTF-8\n");
            writerout.write("Content-length: " + String.valueOf(query.length()) + "\n");
            writerout.write("\n");
            writerout.write(query);
            writerout.flush();

            writerout.close();
            writerout = null;

            int responsecode = connection.getResponseCode();
            if (responsecode == HttpURLConnection.HTTP_OK) {
                StringBuilder text = new StringBuilder();

                readerin = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = readerin.readLine()) != null) {
                    text.append(line);
                    text.append(System.getProperty("line.separator"));
                }

                JSONParser jsonp = new JSONParser();
                JSONObject json = (JSONObject) jsonp.parse(text.toString());

                // Print lines
                JSONArray jsonlines = (JSONArray) json.get("lines");
                for(Object l : jsonlines){
                    JSONObject jsonline = (JSONObject) l;
                    Object type = jsonline.get("type");
                    if ("build".equals(type)) {
                        ui.buildMessage((String)jsonline.get("text"));
                    } else if ("run".equals(type)) {
                        ui.runMessage((String)jsonline.get("text"));
                    } else if ("out".equals(type)) {
                        ui.printOut((String)jsonline.get("text"));
                    } else if ("log".equals(type)) {
                        ui.printLog((String)jsonline.get("text"));
                    } else if ("success".equals(type)) {
                        ui.successMessage(((Number) jsonline.get("time")).longValue());
                    } else if ("fail".equals(type)) {
                        ui.failMessage((String) jsonline.get("text"), ((Number) jsonline.get("time")).longValue());
                    } else if ("exception".equals(type)) {
                        ui.exceptionMessage((String) jsonline.get("text"), ((Number) jsonline.get("time")).longValue());
                    }
                }

                // Print stats
                ui.getStatsModel().reset();
                JSONArray jsonstats = (JSONArray) json.get("stats");
                if (jsonstats != null) {
                    for (Object l : jsonstats) {
                        JSONObject jsonstat = (JSONObject) l;
                        ui.getStatsModel().addRow(
                                (String) jsonstat.get("task"),
                                (String) jsonstat.get("step"),
                                ((Number) jsonstat.get("executions")).intValue(),
                                ((Number) jsonstat.get("records")).intValue(),
                                ((Number) jsonstat.get("rejected")).intValue(),
                                ((Number) jsonstat.get("totaltime")).doubleValue(),
                                ((Number) jsonstat.get("avgtime")).doubleValue());
                    }
                }

                // save preferences if execution went well
                Configuration.getInstance().setPreference("remote.serverurl", jURL.getText());
                Configuration.getInstance().setPreference("remote.logginglevel", jLoggingLevel.getSelectedItem().toString());
                Configuration.getInstance().setPreference("remote.trace", Boolean.toString(jTrace.isSelected()));
                Configuration.getInstance().setPreference("remote.stats", Boolean.toString(jStats.isSelected()));
                Configuration.getInstance().flushPreferences();

//                 } catch (NullPointerException ex) {
//                 } catch (ClassCastException ex) {
//                 } catch (ParseException ex) {
            } else {
                throw new IOException(MessageFormat.format(bundle.getString("message.httperror"), Integer.toString(responsecode), connection.getResponseMessage()));
            }
//        } catch (MalformedURLException ex) {
//        } catch (IOException ex) {
        } finally {
            if (writerout != null) {
                try {
                    writerout.close();
                } catch (IOException ex) {
                }
                writerout = null;
            }
            if (readerin != null) {
                try {
                    readerin.close();
                } catch (IOException ex) {
                }
                readerin = null;
            }
        }
    }

    @Override
    public void setEnabledUI(boolean value) {
        // No need to do anything. This panel is a dialog;
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

        jLabel1 = new javax.swing.JLabel();
        jURL = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLoggingLevel = new javax.swing.JComboBox();
        jTrace = new javax.swing.JCheckBox();
        jStats = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.serverurl")); // NOI18N

        jLabel2.setText(bundle.getString("label.logginglevel")); // NOI18N

        jTrace.setText(bundle.getString("label.trace")); // NOI18N

        jStats.setSelected(true);
        jStats.setText(bundle.getString("label.statistics")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jURL, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTrace, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLoggingLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jStats, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLoggingLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTrace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStats))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox jLoggingLevel;
    private javax.swing.JCheckBox jStats;
    private javax.swing.JCheckBox jTrace;
    private javax.swing.JTextField jURL;
    // End of variables declaration//GEN-END:variables

}
