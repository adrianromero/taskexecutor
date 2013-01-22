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
import com.adr.taskexecutor.engine.LanguageProviderFactory;
import com.adr.taskexecutor.common.Utils;
import com.adr.taskexecutor.quartz.SchedulerInitializer;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.Properties;
import javax.swing.JCheckBoxMenuItem;
import org.quartz.SchedulerException;

/**
 *
 * @author adrian
 */
public class TaskFrame extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(TaskFrame.class.getName());
    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N

//    private final LanguageProvider language;

    private File currentdir = null;

    private Action newaction;
    private Action openaction;
    private Action saveaction;
    private Action saveasaction;
    private Action closeaction;

    private File[] recents = new File[4];

    private SchedulerInitializer schedinit = new SchedulerInitializer();

    /** Creates new form Launcher */
    public TaskFrame() {

        initComponents();

        try {
            List<Image> images = new ArrayList<Image>();
            images.add(ImageIO.read(TaskFrame.class.getResourceAsStream("/com/adr/taskexecutor/ui/images/32x32/softwareD.png")));
            images.add(ImageIO.read(TaskFrame.class.getResourceAsStream("/com/adr/taskexecutor/ui/images/64x64/softwareD.png")));
            images.add(ImageIO.read(TaskFrame.class.getResourceAsStream("/com/adr/taskexecutor/ui/images/128x128/softwareD.png")));
            setIconImages(images);
        } catch (IOException e) {
        }

        newaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                newDocument();
            }
        };
        newaction.putValue(Action.SMALL_ICON, new ImageIcon(TaskFrame.class.getResource("/com/adr/taskexecutor/ui/images/filenew.png")));
        newaction.putValue(Action.NAME, bundle.getString("label.new"));
        jNew.setAction(newaction);
        jMenuNew.setAction(newaction);

        openaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                openDocument();
            }
        };
        openaction.putValue(Action.SMALL_ICON, new ImageIcon(TaskFrame.class.getResource("/com/adr/taskexecutor/ui/images/fileopen.png")));
        openaction.putValue(Action.NAME, bundle.getString("label.open"));
        jOpen.setAction(openaction);
        jMenuOpen.setAction(openaction);

        saveaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                saveDocument();
            }
        };
        saveaction.putValue(Action.SMALL_ICON, new ImageIcon(TaskFrame.class.getResource("/com/adr/taskexecutor/ui/images/filesave.png")));
        saveaction.putValue(Action.NAME, bundle.getString("label.save"));
        saveaction.setEnabled(false);
        jSave.setAction(saveaction);
        jMenuSave.setAction(saveaction);

        saveasaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                saveDocumentAs();
            }
        };
        saveasaction.putValue(Action.SMALL_ICON, new ImageIcon(TaskFrame.class.getResource("/com/adr/taskexecutor/ui/images/filesaveas.png")));
        saveasaction.putValue(Action.NAME, bundle.getString("label.saveas"));
        saveasaction.setEnabled(false);
        jMenuSaveAs.setAction(saveasaction);
        
        closeaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                closeDocument();
            }
        };
        closeaction.putValue(Action.SMALL_ICON, new ImageIcon(TaskFrame.class.getResource("/com/adr/taskexecutor/ui/images/fileclose.png")));
        closeaction.putValue(Action.NAME, bundle.getString("label.close"));
        closeaction.setEnabled(false);
        jMenuClose.setAction(closeaction);

        recents[0] = Configuration.getInstance().getRecent(1);
        recents[1] = Configuration.getInstance().getRecent(2);
        recents[2] = Configuration.getInstance().getRecent(3);
        recents[3] = Configuration.getInstance().getRecent(4);

        paintRecents();

        for (LanguageProvider lp :LanguageProviderFactory.getAllInstances()) {
            addLanguageDebugger(lp);
        }
    }

    private void includeRecent(File f) {

        for (int i = 0; i < recents.length; i++) {
            if (f.equals(recents[0])) {
                break;
            }
            if (i + 1 == recents.length) {
                recents[0] = f;
            } else {
                File swap = recents[0];
                recents[0] = recents[i + 1];
                recents[i + 1] = swap;
            }
        }

        paintRecents();
    }

    private void paintRecents()  {

        jMenuItem1.setVisible(recents[0] != null);
        jMenuItem1.setText(recents[0] == null ? "" : "1. " + recents[0].getName());
        jMenuItem2.setVisible(recents[1] != null);
        jMenuItem2.setText(recents[1] == null ? "" : "2. " + recents[1].getName());
        jMenuItem3.setVisible(recents[2] != null);
        jMenuItem3.setText(recents[2] == null ? "" : "3. " + recents[2].getName());
        jMenuItem4.setVisible(recents[3] != null);
        jMenuItem4.setText(recents[3] == null ? "" : "4. " + recents[3].getName());

        jSeparatorRecents.setVisible(recents[0] != null);
    }

    public void start() {

        // pack();
        setLocationRelativeTo(null);

        setVisible(true);

        newDocument();
//        openDocument(new File("/home/adrian/personal/taskexecutor/test/com/adr/taskexecutor/samples/products.xml"));
//        openDocument(new File("/home/adrian/personal/taskexecutor/test/com/adr/taskexecutor/samples/subtask_test.xml"));
//        openDocument(new File("/home/adrian/personal/taskexecutor/test/com/adr/taskexecutor/samples/formats_test.xml"));
//        openDocument(new File("/home/adrian/personal/taskexecutor/test/com/adr/taskexecutor/samples/linecreator_test.xml"));

    }

    private void addTaskToTab(TaskWindow taskwindow) {
        jtabbed.addTab(taskwindow.getTaskName(), taskwindow);
        jtabbed.setTabComponentAt(jtabbed.getTabCount() - 1, new TaskTab(this, taskwindow));
        jPanel2.add(taskwindow.getToolbar(), taskwindow.getUUID());
        
        setSelectedTab(taskwindow);

        closeaction.setEnabled(true);
    }

    private void refreshTabTitle(int i) {
        TaskTab t = (TaskTab) jtabbed.getTabComponentAt(i);
        t.refreshLabel();
    }

    private void openDocument() {

        JFileChooser fc = fc();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentdir = fc.getCurrentDirectory();
            openDocument(fc.getSelectedFile());
        }
    }

    private void openDocument(File f) {

        for (int i = 0; i < jtabbed.getTabCount(); i++) {
            TaskWindow task = (TaskWindow) jtabbed.getComponentAt(i);
            if (f.equals(task.getFile())) {
                setSelectedTab(task);
                return;
            }
        }

        // Open it
        try {
            TaskWindow task = null;
            if (jtabbed.getTabCount() > 0) {
                task = (TaskWindow) jtabbed.getComponentAt(jtabbed.getTabCount() -1);
            }
            if (task != null && task.canReplaceTaskEditor()) {
                ((TaskEditor) task).replaceDocument(Configuration.getInstance().getLanguageProvider(), f);
                refreshTabTitle(jtabbed.getTabCount() - 1);
                setSelectedTab(task);
            } else {
                task = new TaskEditor(Configuration.getInstance().getLanguageProvider(), f);
                addTaskToTab(task);
            }

            saveaction.setEnabled(true);
            saveasaction.setEnabled(true);

            includeRecent(f);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("message.cannotopentask"), f.toString()), bundle.getString("message.opentask"), JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void newDocument() {

        String title = getNewTitle();
        try {      
           
            String newdoc = Utils.loadResourceText(Configuration.getInstance().getLanguageProvider().getNewScriptTemplate());

            TaskWindow task = new TaskEditor(Configuration.getInstance().getLanguageProvider(), title, newdoc.replace("$$title$$", title));
            addTaskToTab(task);

            saveaction.setEnabled(true);
            saveasaction.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("message.cannotopentask"), title), bundle.getString("message.opentask"), JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private boolean saveDocument() {

        JFileChooser fc = fc();
        TaskWindow taskwindow = (TaskWindow) jtabbed.getSelectedComponent();

        boolean result = taskwindow.saveDocument(fc);
        
        if (result) {
            currentdir = fc.getCurrentDirectory();
            includeRecent(taskwindow.getFile());
            includeRecent(taskwindow.getFile());
            refreshTabTitle(jtabbed.getSelectedIndex());
            setSelectedTab(taskwindow);
        }

        return result;
    }

    private boolean saveDocumentAs() {
        JFileChooser fc = fc();
        TaskWindow taskwindow = (TaskWindow) jtabbed.getSelectedComponent();

        boolean result = taskwindow.saveDocumentAs(fc);

        if (result) {
            currentdir = fc.getCurrentDirectory();
            includeRecent(taskwindow.getFile());
            refreshTabTitle(jtabbed.getSelectedIndex());
            setSelectedTab(taskwindow);
        }

        return result;
    }

    private boolean closeDocument() {
        return closeDocument((TaskWindow) jtabbed.getSelectedComponent());
    }

    public boolean closeDocument(TaskWindow taskwindow) {
       
        if (taskwindow == null) {
            return true;
        } else {

            switch (taskwindow.askClose()) {
            case TaskWindow.CLOSE_DO_SAVE:
                if (!saveDocument()) {
                    return false;
                }
                break;
            case TaskWindow.CLOSE_FALSE:
                return false;
            }

            jPanel2.remove(taskwindow.getToolbar());

            jmenutask.removeAll();
            jmenutask.add(jmenudebugger);
            // jmenutask.setEnabled(false);
            jtabbed.remove(taskwindow);  // at the end after everything has been removed before.

            if (jtabbed.getTabCount() == 0) {
                saveaction.setEnabled(false);
                saveasaction.setEnabled(false);
                closeaction.setEnabled(false);
            }
            return true;
        }
    }

    private void setSelectedTab(TaskWindow taskwindow) {
        jtabbed.setSelectedComponent(taskwindow);
        setFrameTitle(taskwindow);
    }

    private void setFrameTitle(TaskWindow taskwindow) {
        if (taskwindow == null) {
            setTitle(bundle.getString("title.application"));
        } else {
            setTitle(taskwindow.getFileName() + " - " + bundle.getString("title.application"));
            CardLayout cl = (CardLayout)(jPanel2.getLayout());
            cl.show(jPanel2, taskwindow.getUUID());
            jmenutask.removeAll();
            jmenutask.add(jmenudebugger);
            // jmenutask.setEnabled(true);
            for (Component menu : taskwindow.getMenu()) {
                jmenutask.add(menu);
            }
        }
    }

    private String getNewTitle() {

        int i = 1;

        String title_number = MessageFormat.format(bundle.getString("title.filenew"), Integer.toString(i));
        while (isNotValidTitle(title_number)) {
            i++;
            title_number = MessageFormat.format(bundle.getString("title.filenew"), Integer.toString(i));
        }
        return title_number;
    }

    private boolean isNotValidTitle(String title) {

        for (int i = 0; i < jtabbed.getTabCount(); i++) {
            TaskWindow task = (TaskWindow) jtabbed.getComponentAt(i);
            if (task.getTaskName().equals(title)) {
                return true;
            }
        }
        return false;
    }

    private JFileChooser fc() {

        JFileChooser fc = new JFileChooser(currentdir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // fc.setCurrentDirectory(null);
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(new ExtensionsFilter(Configuration.getInstance().getLanguageProvider().getExtensionName(), Configuration.getInstance().getLanguageProvider().getExtension()));
        fc.setMultiSelectionEnabled(false);
        return fc;
    }

    private void closeForm() {

        // Save documents
        while (jtabbed.getTabCount() > 0 ) {
            if (!closeDocument((TaskWindow) jtabbed.getComponentAt(jtabbed.getTabCount() - 1))) {
                return;
            }
        }
        
        // Save Preferences
        Configuration.getInstance().setRecent(1, recents[0]);
        Configuration.getInstance().setRecent(2, recents[1]);
        Configuration.getInstance().setRecent(3, recents[2]);
        Configuration.getInstance().setRecent(4, recents[3]);
        Configuration.getInstance().flushPreferences();

        dispose();
    }

    private void addLanguageDebugger(final LanguageProvider langprovider) {
        if (langprovider.isDebuggable()) {
            jmenudebugger.add(new JLanguageProviderMenuitem(langprovider));
        }
    }

    private static class JLanguageProviderMenuitem extends JCheckBoxMenuItem {
        
        private LanguageProvider lp;
        private boolean sealed;
        
        public JLanguageProviderMenuitem(LanguageProvider lp) {
            this.lp = lp;
            setText(lp.getLanguageName());
            
            sealed = false;
            setSelected(false);
            sealed = true;

            addItemListener(new java.awt.event.ItemListener() {
                @Override
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    if (sealed) {
                        if (JLanguageProviderMenuitem.this.lp.isDebuggerOpen()) {
                            JLanguageProviderMenuitem.this.lp.closeDebugger();
                        } else {
                            JLanguageProviderMenuitem.this.lp.openDebugger();
                        }
                    }
                }
            });            
        }

        public void refreshState() {
            sealed = false;
            setSelected(lp.isDebuggerOpen());
            sealed = true;
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

        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jNew = new javax.swing.JButton();
        jOpen = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jSave = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jPanel2 = new javax.swing.JPanel();
        jtabbed = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuNew = new javax.swing.JMenuItem();
        jMenuOpen = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuSaveAs = new javax.swing.JMenuItem();
        jSeparatorRecents = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuClose = new javax.swing.JMenuItem();
        jMenuExit = new javax.swing.JMenuItem();
        jmenutask = new javax.swing.JMenu();
        jmenudebugger = new javax.swing.JMenu();
        jMenuWindow = new javax.swing.JMenu();
        jMenuServer = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jNew.setText("*New");
        jNew.setFocusable(false);
        jNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jNew);

        jOpen.setText("*Open");
        jOpen.setFocusable(false);
        jOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jOpen);
        jToolBar1.add(jSeparator1);

        jSave.setText("*Save");
        jSave.setEnabled(false);
        jSave.setFocusable(false);
        jSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jSave);
        jToolBar1.add(jSeparator3);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new java.awt.CardLayout());
        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jtabbed.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jtabbed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtabbedStateChanged(evt);
            }
        });
        getContentPane().add(jtabbed, java.awt.BorderLayout.CENTER);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N
        jMenuFile.setText(bundle.getString("label.file")); // NOI18N

        jMenuNew.setText("*New");
        jMenuFile.add(jMenuNew);

        jMenuOpen.setText("*Open");
        jMenuFile.add(jMenuOpen);
        jMenuFile.add(jSeparator4);

        jMenuSave.setText("*Save");
        jMenuFile.add(jMenuSave);

        jMenuSaveAs.setText("*Save as...");
        jMenuFile.add(jMenuSaveAs);
        jMenuFile.add(jSeparatorRecents);

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem1);

        jMenuItem2.setText("jMenuItem2");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem2);

        jMenuItem3.setText("jMenuItem3");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem3);

        jMenuItem4.setText("jMenuItem4");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem4);
        jMenuFile.add(jSeparator5);

        jMenuClose.setText("*Close");
        jMenuFile.add(jMenuClose);

        jMenuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/adr/taskexecutor/ui/images/exit.png"))); // NOI18N
        jMenuExit.setText(bundle.getString("label.exit")); // NOI18N
        jMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExit);

        jMenuBar1.add(jMenuFile);

        jmenutask.setText(bundle.getString("menu.task")); // NOI18N

        jmenudebugger.setText(bundle.getString("menu.debugger")); // NOI18N
        jmenudebugger.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jmenudebuggerMenuSelected(evt);
            }
        });
        jmenutask.add(jmenudebugger);

        jMenuBar1.add(jmenutask);

        jMenuWindow.setText(bundle.getString("menu.window")); // NOI18N

        jMenuServer.setText(bundle.getString("menu.server")); // NOI18N
        jMenuServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuServerActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuServer);

        jMenuBar1.add(jMenuWindow);

        jMenu1.setText(bundle.getString("label.help")); // NOI18N

        jMenuItem5.setText(bundle.getString("label.about")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-621)/2, (screenSize.height-700)/2, 621, 700);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        closeForm();

    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        System.exit(0);

    }//GEN-LAST:event_formWindowClosed

    private void jtabbedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtabbedStateChanged

        setFrameTitle((TaskWindow) jtabbed.getSelectedComponent());

    }//GEN-LAST:event_jtabbedStateChanged

    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExitActionPerformed

        closeForm();
        
    }//GEN-LAST:event_jMenuExitActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        openDocument(recents[0]);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        openDocument(recents[1]);

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        openDocument(recents[2]);

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        openDocument(recents[3]);

    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed

        new About(this).setVisible(true);

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jmenudebuggerMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jmenudebuggerMenuSelected
      
        for(Component c : jmenudebugger.getMenuComponents()) {
            JLanguageProviderMenuitem menu = (JLanguageProviderMenuitem) c;
            menu.refreshState();
        }

    }//GEN-LAST:event_jmenudebuggerMenuSelected

    private void jMenuServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuServerActionPerformed
        
        ServerPropertiesDialog dialog = new ServerPropertiesDialog(this);
        dialog.setVisible(true);

        if (dialog.getOK()) {

            try {
                Properties prop = dialog.getSchedulerProperties();

                addTaskToTab(new TaskServer(schedinit, prop));

                saveaction.setEnabled(true);
                saveasaction.setEnabled(true);

            } catch (SchedulerException ex) {
                JOptionPane.showMessageDialog(this, new MessageComponent(bundle.getString("message.cannotstarttaskserver"), ex), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, new MessageComponent(bundle.getString("message.cannotstarttaskserver"), ex), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, ex.getMessage(), ex);
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, bundle.getString("message.taskserverexists"), bundle.getString("title.taskserver"), JOptionPane.ERROR_MESSAGE);
//                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }//GEN-LAST:event_jMenuServerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuClose;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuNew;
    private javax.swing.JMenuItem jMenuOpen;
    private javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenuItem jMenuSaveAs;
    private javax.swing.JMenuItem jMenuServer;
    private javax.swing.JMenu jMenuWindow;
    private javax.swing.JButton jNew;
    private javax.swing.JButton jOpen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jSave;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparatorRecents;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenu jmenudebugger;
    private javax.swing.JMenu jmenutask;
    private javax.swing.JTabbedPane jtabbed;
    // End of variables declaration//GEN-END:variables

}
