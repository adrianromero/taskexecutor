//    Task Executor is a simple scripting task executor.
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

// TODO: Output of scheduled tasks thrown also to the output of the graphical interface...

// DONE: 0050.- Complete Save / Load / New logic
// DONE: 0060.- Fix and set properly current file in JFile Chooser
// DONE: 0100.- Extract task executor. Add Task text provider interface that can be the textbox or a file
// DONE: 0150.- Make opencsv a library
// DONE: 0160.- Locale as parameter
// DONE: 0170.- Separate tabs parameters y environment
// DONE: 0300.- Tools: Formatting
// DONE: 0350.- Write to CSV
// DONE: 0400.- Tools: Database, read, load, transactions.
// NOTNEEDED: 0450.- Tools constructors cannot throw exception in order to close properly without ifs
// DONE: 0603.- New thread for executing the task
// NOTNEEDED: 0605.- Implement exception Step
// DONE: 0606.- Implement records counter for a loop step. Statistics.
// DONE: 0607.- Execute subprojects. Substeps easy in js: executeStep(nextstep);
// DONE: 0608.- Refactor js infrastructure.
// DONE: 0609.- More statistics: average by record.
// DONE: 0610.- Parameters Save Load New, using plain text files concatenated with the parameters name.New window where all parameters are managed
// DONE: 0999.- Icons.
// DONE: 0999.- Executor buttons in top toolbar
// NOTNEEDED: En el GUI, en lugar de ejecutar el texto del editor, forzar grabar el archivo y ejecutar el archivo.
// DONE: 0999.- En el CSVRead, que la fila que devuelva sea un object con claves las cabeceras.
// DONE: 0610.- Application Icon.
// DONE: 0420.- Tools: Database, query complete, other convenience methods to execute easily statements.
// DONE: 0421.- Tools: Database, upsert.
// DONE: 0422.- Tools: Database, remove Database.java only .js needed.
// DONE: 0423.- Implementing architecture for supporting other scripting languages.

// DONE: 0450.- Export or at least copy statistics.
// DONE: 0500.- CLI interface: To show just an executor or to load a file, and a console only interface using previous enhancements. No need to localize CLI Interface
// DONE: 0500.- Copy/Paste popup menu for text boxes.
// DONE: 0700.- Internationalize messages and labels
// DONE: 0710.- When saving a task, if does not have extension, add .xml
// DONE: 0720.- Refactor main loop. Main block in steps must be executed only once. Te same for close function.
// DONE: 0730.- Add to the menu the list of recently loaded.
// DONE: 0730.- Open with new task. New task based on the template.
// DONE: 0750.- In generator lines tool, do not give a reference of the line, give a copy. Do not want the line to be modified by steps.
// DONE: 0760.- In database take into account timestamp, date... when setting parameters. Strings do not work very well.
// DONE: 0780.- Fixed issue of disabling stats and working folder when task is running.
// DONE: 0790.- Basic About dialog.
// DONE: 0791.- Close button in tabs. http://download.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TabComponentsDemo
// DONE: 0792.- Enhance about with system properties.

// DONE: 0793.- Add if's to Generator stats obj to improve performance if Statswriter is null. Done. If there are not stats collector it does not do anything.
// NOT NEEDED: 0800.- Quartz web server. Using Quartz RMI
// DONE: 0810.- Remote Quartz planning.
// DONE: 0820.- Copyright comments.
// DONE: 0840.- Improve DataSourceJNDI to add properties like jndi factory, principal, credentials... quartz style.
// DONE: 0841.- Added Rhino as javascript engine. Included also the DEBUGGER !!!!!!!!!!
// DONE: 8050.- Options (Local, remote and planned) saved to preferences.
// DONE: 8060.- Choose a product name and change all references to the new name.

// DONE: Review Encodigng and use system encoding, not UTF-8. Add system encoding info to about.
// DONE: 1300.- Protect from exceptions thrown trying to save when no document is open. (Better to disable menu icons)...
// PENDING: 1400.- Two returns in the process javascript object: result and error. Is it possible?
// PENDING: 1500.- Tools: Read XML, Save XML.
// PENDING: 1600.- Tools: Read JSON, Save JSON.
// PENDING: 1600.- Tools: Improve Database: Upsert. others... Use the new datasql jdbc wrapper.
// PENDING: 1600.- Tools: Lookups. Middle steps to searchdata.
// PENDING: 1600.- Tools: Clustering. Slice Entry data into different servers
// PENDING: 1605.- HTTP load y save. working with previous tools XML, JSON, even CSV
// PENDING: 16101.- Documentation Web or Wiki or..
// PENDING: 16102.- Documentation javadoc..
// PENDING: 16103.- Review libraries, inclusion and dependencies in all projects.
// PENDING: 16104.- Improve statistics object printing name of task executed total time for a task...
// PENDING: 16450.- Complete application menus. Similar to gedit.

// NOT NEEDED: SERVER: Create table TASK_RESOURCES if it does not exist. By default is used working dir persistence in the server.
// PENDING: SERVER: Create new servlet that exposes data results as a web service JSON or XML...
// DONE: SERVER: Ability to execute a task by name not using a text.

// Version 2
// PENDING: 2000.- Graphical designers for task and steps.
// PENDING: 2010.- Support for compilable script engines.
// PENDING: 2020.- Add GeneratorLoaderDataSource the ability to load "built" tasks ie: instantiated using constructor: Generator(LanguageProvider langprovider, String[] scripttools, String scripttask)
// PENDING: 2030.- Ant task

// To be discussed / Future
// PENDING: 2000.- More menus / Taskbar at the bottom ?
// PENDING: 2000.- In database fields names for query. ?

// En el GUI, detectar si el texto que se está editando se ha modificado con un editor externo. NO
// En el GUI que tenga los mismos parámetros que el interfaz CLI. NO
// PENDING: 2000.- Implementing other scripting languages: jython, groovy, jruby,  ... NO



/**
 *
 * @author adrian
 */
public class StartUI {

    private static final Logger logger = Logger.getLogger("com.adr.taskexecutor.ui.Start");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Set the look and feel.
                try {
                     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                     UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//                     UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//                     UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Cannot set look and feel", e); // NOI18N
                }

                Configuration.getInstance().init();
                new TaskFrame().start();
            }
        });
    }
}
