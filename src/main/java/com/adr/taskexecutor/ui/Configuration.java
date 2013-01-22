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
import com.adr.taskexecutor.common.LanguageProvider;
import com.adr.taskexecutor.engine.LanguageProviderFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author adrian
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    
    private static Configuration instance = null;

    private LanguageProvider language;
    private String parentstats = null;
    private String workingdir = null;

    private Preferences pref;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void init() {
        init(EnvironmentFactory.DEFAULT_LANGUAGE, "~/.taskexecutor", "taskexecutor");
    }

    public void init(String language, String workingdir, String preferencesnode) {
        this.language = LanguageProviderFactory.getInstance(language);
        this.workingdir = workingdir + "/tasks";
        this.parentstats = workingdir + "/logs/statistics";
        this.pref = Preferences.userRoot().node(preferencesnode);
    }

    public String getStatsCollector() {
        return parentstats;
    }

    public String getWorkingDir() {
        return workingdir;
    }

    public LanguageProvider getLanguageProvider() {
        return language;
    }

    public File getRecent(int i) {
        String s = pref.get("Recent" + Integer.toString(i), "<empty>");
        return "<empty>".equals(s) ? null : new File(s);
    }

    public void setRecent(int i, File f) {
        pref.put("Recent" + Integer.toString(i), f == null ? "<empty>" : f.toString());
    }

    public String getPreference(String key, String defaultvalue) {
        return pref.get(key, defaultvalue);
    }

    public void setPreference(String key, String value) {
        pref.put(key, value);
    }

    public void flushPreferences() {
        try {
            pref.flush();
        } catch (BackingStoreException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
