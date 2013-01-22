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

package com.adr.taskexecutor.engine;

import com.adr.taskexecutor.common.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrian
 */
public class GeneratorLoaderWorkingFolder extends GeneratorLoaderPersistent {

    private File workingfolder;

    public GeneratorLoaderWorkingFolder(String lang, File workingfolder) {
        super(lang);
        this.workingfolder = workingfolder;
    }

    @Override
    public Generator getGeneratorByName(String name) throws Exception {

        if (getLanguage() == null) {
            throw new Exception("Task language not supported: " + getLanguageName());
        }
        return new Generator(getLanguage().createScope(), getLanguage().getScriptTask(getReader(name)), name);
    }

    private File getFile(String name) throws Exception {
        return new File(workingfolder, name + getLanguage().getExtension());
    }

    private Reader getReader(String name) throws Exception {
        return new InputStreamReader(new FileInputStream(getFile(name)));
    }

    @Override
    public String get(String name) throws Exception {
        return Utils.loadText(getFile(name));
    }

    @Override
    public int delete(String name) throws Exception {
        return getFile(name).delete() ? 1 : 0;
    }

    @Override
    public void put(String name, String task) throws Exception {
        workingfolder.mkdirs();
        Utils.saveText(getFile(name), task);
    }

    @Override
    public List<String> list() throws Exception {
        File[] l = workingfolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(getLanguage().getExtension());
            }
        });

        ArrayList<String> al = new ArrayList<String>();
        if (l != null) {
            for (File f : l) {
                String filename = f.getName();
                al.add(filename.substring(0, filename.length() - getLanguage().getExtension().length())); // without the extension
            }
        }
        return al;
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }
}
