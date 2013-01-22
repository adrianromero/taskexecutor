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

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author adrian
 */
public class ExtensionsFilter extends FileFilter {

    private String message;
    private String[] extensions;

    public ExtensionsFilter(String message, String... extensions) {
        this.message = message;
        this.extensions = extensions;
    }

    @Override
    public boolean accept(java.io.File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            String sFileName = f.getName();
            for(String s : extensions) {
                if (sFileName.endsWith(s)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String getDescription() {
        return message;
    }
}
