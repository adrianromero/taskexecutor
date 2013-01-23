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

import com.adr.taskexecutor.common.LanguageProvider;
import com.adr.taskexecutor.common.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author adrian
 */
public class TaskEditor extends TaskWindow implements DocumentListener, TaskProvider {

    private static final Logger logger = Logger.getLogger(TaskEditor.class.getName());
    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N

    private RSyntaxTextArea textArea;
    private RTextScrollPane sp;
    private TaskExecutor executor;

    private boolean dirty = false;
    private File file = null;
    private ConfigurationsMap configurations = null;
    private String filename;
    private String tasklanguage;

    public TaskEditor(LanguageProvider language, File file) throws IOException {

        init();

        replaceDocument(language, file);
    }

    /** Creates new form TaskPanel with a new document */
    public TaskEditor(LanguageProvider language, String title, String text) throws IOException {

        init();

        this.file = null;
        this.configurations = new ConfigurationsMap();
        this.filename = title;
        this.tasklanguage = language.getLanguage();

        textArea.setSyntaxEditingStyle(language.getMime());
        textArea.setText(text);
        textArea.setCaretPosition(0);

        refresh();
    }

    public void replaceDocument(LanguageProvider language, final File file) throws IOException {
    // preconditions isNew() && !isDirty()

        this.file = file;
        this.configurations = new ConfigurationsMap();
        this.filename = file.getName();
        this.tasklanguage = language.getLanguage();
        
        textArea.setSyntaxEditingStyle(language.getMime());
        textArea.setText(Utils.loadText(file));
        textArea.setCaretPosition(0);

        // Configs
        File[] files = file.getParentFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(file.getName() + ".") && name.endsWith(".txt");
            }
        });

        int i = file.getAbsolutePath().length();
        for (File fparam : files) {
            String s = fparam.getAbsolutePath();
            configurations.put(s.substring(i + 1, s.length() - 4), Utils.loadText(fparam));
        }

        refresh();
    }
    
    private void refresh() {

        executor.init(this);

        configurations.resetDirty();
        dirty = false;
    }
    
    private void init() {

        initComponents();


        textArea = new RSyntaxTextArea();

        // desktophints = (Map) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        // desktophints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        // textArea.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
        textArea.setAntiAliasingEnabled(true);
        // textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.getDocument().addDocumentListener(this);
        textArea.setFont(new Font(Font.MONOSPACED, textAreaHidden.getFont().getStyle(), textAreaHidden.getFont().getSize()));

        sp = new RTextScrollPane(textArea);
        sp.getGutter().setLineNumberFont(new Font(Font.MONOSPACED, textAreaHidden.getFont().getStyle(), textAreaHidden.getFont().getSize()));

        panelText.add(sp, BorderLayout.CENTER);

        executor = new TaskExecutor();
        panelExecutor.add(executor);
    }

    private void save(File file) throws IOException {
        this.file = file;
        this.filename = file.getName();
        save();
    }

    private void save() throws IOException {
        if (file != null) {
            Utils.saveText(file, textArea.getText());
            
            // Configs
            File[] files = file.getParentFile().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(file.getName()  + ".") && name.endsWith(".txt"); //
                }
            });

            int i = file.getAbsolutePath().length();
            for (File fparam : files) {
                fparam.delete();
            }
            for (String key : configurations.keySet()) {
                Utils.saveText(new File(file.getParentFile(), file.getName() + "." + key + ".txt"), configurations.get(key));
            }

            configurations.resetDirty();
            dirty = false;
        }
    }

    private boolean isNew() {
        return file == null;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        dirty = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        dirty = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        dirty = true;
    }
    
    @Override
    public String getTaskName() {
        return filename;
    }

    @Override
    public String getTaskText() {
        return textArea.getText();
    }

    @Override
    public String getTaskLanguage() {
        return tasklanguage;
    }

    @Override
    public ConfigurationsMap getConfigurations() {
        return configurations;
    }


    @Override
    public String getFileName() {
        if (file == null) {
            return filename;
        } else {
            return file.getPath();
        }
    }

    @Override
    public Component[] getMenu() {
        return executor.getMenu();
    }

    @Override
    public Component getToolbar() {
        return executor.getToolbar();
    }

    @Override
    public int askClose() {
        if (dirty || configurations.isDirty()) {
            int result = JOptionPane.showConfirmDialog(this, MessageFormat.format(bundle.getString("message.wantsavetask"), getTaskName()), bundle.getString("message.closetask"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION ) {
                return TaskWindow.CLOSE_FALSE;
            }
            if (result == JOptionPane.YES_OPTION) {
                return TaskWindow.CLOSE_DO_SAVE;
            }
        }
        return TaskWindow.CLOSE_TRUE;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public boolean saveDocument(JFileChooser fc) {
        if (isNew()) {
            return saveDocumentAs(fc);
        } else {
            try {
                save();
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("message.cannotsavetask"), getFile().toString()), bundle.getString("message.savetask"), JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
        }
    }

    @Override
    public boolean saveDocumentAs(JFileChooser fc) {

        if (getFile() != null) {
            fc.setSelectedFile(getFile());
        }

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {           

            File selectedfile = fc.getSelectedFile();

            if (!selectedfile.getName().endsWith(Configuration.getInstance().getLanguageProvider().getExtension())) {
                // add .xml extension if selected file does not have extension
                selectedfile = new File(selectedfile.getParentFile(), selectedfile.getName() + Configuration.getInstance().getLanguageProvider().getExtension());
            }

            // Check if the file exists.
            if (selectedfile.exists()) {
                int result = JOptionPane.showConfirmDialog(this, MessageFormat.format(bundle.getString("message.taskexists"), selectedfile.toString()) , bundle.getString("message.savetask"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
                    return false;
                }
            }

            try {
                save(selectedfile);
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("message.cannotsavetask"), getFile().toString()), bundle.getString("message.savetask"), JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canReplaceTaskEditor() {
         return isNew() && !dirty && !configurations.isDirty();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textAreaHidden = new javax.swing.JTextArea();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelText = new javax.swing.JPanel();
        panelExecutor = new javax.swing.JPanel();

        textAreaHidden.setColumns(20);
        textAreaHidden.setRows(5);

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);

        panelText.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setTopComponent(panelText);

        panelExecutor.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(panelExecutor);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel panelExecutor;
    private javax.swing.JPanel panelText;
    private javax.swing.JTextArea textAreaHidden;
    // End of variables declaration//GEN-END:variables

}
