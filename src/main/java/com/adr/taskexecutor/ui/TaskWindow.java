/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adr.taskexecutor.ui;

import java.awt.Component;
import java.io.File;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 *
 * @author adrian
 */
public abstract class TaskWindow extends JComponent {


    public static final int CLOSE_FALSE = 0;
    public static final int CLOSE_TRUE = 1;
    public static final int CLOSE_DO_SAVE = 2;

    private String uuid = UUID.randomUUID().toString();;

    public final String getUUID() {
        return uuid;
    }

    public abstract String getTaskName();
    public abstract String getFileName();

    public abstract Component[] getMenu();
    public abstract Component getToolbar();

    public abstract int askClose();

    public abstract File getFile();
    public abstract boolean saveDocument(JFileChooser fc);
    public abstract boolean saveDocumentAs(JFileChooser fc);

    public abstract boolean canReplaceTaskEditor();
}
