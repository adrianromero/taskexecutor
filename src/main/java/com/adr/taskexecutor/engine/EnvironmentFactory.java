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
import com.adr.datasql.DataSourceBasic;
import com.adr.datasql.DataSourceJNDI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author adrian
 */
public class EnvironmentFactory {

    public static final String DEFAULT_LANGUAGE = "javascript";

    private final static String PREFIX = "com.adr.taskexecutor.";
    public final static String LOADER_WORKING_FOLDER = PREFIX + "loader.source.workingfolder";
    private final static String DATASOURCE = "loader.source.datasource.";
    public final static String LOADER_DATASOURCE_JNDIURL = PREFIX + DATASOURCE + "jndiurl"; // JNDI
    public final static String LOADER_DATASOURCE_URL = PREFIX + DATASOURCE + "url"; // Basic
    public final static String LOADER_DATASOURCE_DRIVER = PREFIX + DATASOURCE + "driver";
    public final static String LOADER_DATASOURCE_USER = PREFIX + DATASOURCE + "user";
    public final static String LOADER_DATASOURCE_PASSWORD = PREFIX + DATASOURCE + "password";

    public final static String LOADER_DECORATOR_MEMCACHE = PREFIX + "loader.decorator.memcache";
    public final static String LOADER_DECORATOR_TEXTTASK = PREFIX + "loader.decorator.texttask";

    public final static String LOADER_LANGUAGE = PREFIX + "loader.language";

    public final static String ENVIRONMENT_TRACE = PREFIX + "environment.trace";
    public final static String ENVIRONMENT_STATS_FILE = PREFIX + "environment.stats.file";
    public final static String ENVIRONMENT_STATS_LOG_FILE = PREFIX + "environment.statslog.file";
    public final static String ENVIRONMENT_LOG_FILE = PREFIX + "environment.log.file";
    public final static String ENVIRONMENT_LOG_LEVEL = PREFIX + "environment.log.level";
    public final static String ENVIRONMENT_LOCALE_LANGUAGE = PREFIX + "environment.locale.language";
    public final static String ENVIRONMENT_LOCALE_COUNTRY = PREFIX + "environment.locale.country";
    public final static String ENVIRONMENT_LOCALE_VARIANT = PREFIX + "environment.locale.variant";

    private Properties props;

    public EnvironmentFactory() throws IOException {
        init(new Properties());
    }

    public EnvironmentFactory(String f) throws IOException {
        this(new FileInputStream(new File(f)));
    }

    public EnvironmentFactory(File f) throws IOException {
        this(new FileInputStream(f));
    }

    public EnvironmentFactory(InputStream in) throws IOException {

        try {
            Properties p = new Properties();
            p.load(in);
            init(p);
        } finally {
            in.close();
        }
    }

    public EnvironmentFactory(Map map) {

        Properties p = new Properties();
        for (Object key : map.keySet()) {
            String name = (String) key;
            if (name.startsWith(EnvironmentFactory.PREFIX)) {
                p.setProperty(name, (String) map.get(key));
            }
        }
        init(p);
    }

    public EnvironmentFactory(Properties props) {
        init(props);
    }

    private void init(Properties props) {
        this.props = props;
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return props.getProperty(key, defaultvalue);
    }

    public GeneratorLoader createGeneratorLoader() {

        String lang = props.getProperty(LOADER_LANGUAGE, DEFAULT_LANGUAGE);

        if (props.getProperty(LOADER_WORKING_FOLDER) != null) {
            return new GeneratorLoaderWorkingFolder(lang, new File(Utils.getSystemFile(props.getProperty(LOADER_WORKING_FOLDER))));
        } else if (props.getProperty(LOADER_DATASOURCE_JNDIURL) != null) {
            Hashtable env = new Hashtable();
            for (Object key : props.keySet()) {
                String skey = (String) key;
                if (skey.startsWith(PREFIX + DATASOURCE)) {
                    env.put(skey, props.getProperty(skey));
                }
            }
            return new GeneratorLoaderDataSource(lang, DataSourceJNDI.setupDataSource(props.getProperty(LOADER_DATASOURCE_JNDIURL), env));
        } else if (props.getProperty(LOADER_DATASOURCE_URL) != null) {
            return new GeneratorLoaderDataSource(lang, DataSourceBasic.setupDataSource(
                    props.getProperty(LOADER_DATASOURCE_DRIVER),
                    props.getProperty(LOADER_DATASOURCE_URL),
                    props.getProperty(LOADER_DATASOURCE_USER),
                    props.getProperty(LOADER_DATASOURCE_PASSWORD)));
        } else {
            return new GeneratorLoaderUnsupported(lang); // not working loader
        }
    }

    public GeneratorEnvironment create() throws Exception {

        // Build loader;
        GeneratorLoader loader = createGeneratorLoader();

        if (props.getProperty(LOADER_DECORATOR_TEXTTASK) != null) {
            loader = new GeneratorLoaderPlain(loader, props.getProperty(LOADER_DECORATOR_TEXTTASK));
        }

        if (Boolean.parseBoolean(props.getProperty(LOADER_DECORATOR_MEMCACHE, Boolean.TRUE.toString()))) {
            loader = new GeneratorLoaderCache(loader);
        }



        GeneratorEnvironment environment = new GeneratorEnvironment();
        environment.setLoader(loader);

        environment.setTrace(Boolean.parseBoolean(props.getProperty(ENVIRONMENT_TRACE, Boolean.FALSE.toString())));

        if (props.getProperty(ENVIRONMENT_STATS_FILE) != null) {
            environment.getStats().addStatsCollector(new StatsCollectorFile(Utils.getLogFile(props.getProperty(ENVIRONMENT_STATS_FILE))));
        }

        if (props.getProperty(ENVIRONMENT_STATS_LOG_FILE) != null) {
            environment.getStats().addStatsCollector(new StatsCollectorLog(Utils.getLogFile(props.getProperty(ENVIRONMENT_STATS_LOG_FILE))));
        }

        if (props.getProperty(ENVIRONMENT_LOG_FILE) != null) {
            environment.addHandler(new LogHandler(new FileOutputStream(Utils.getLogFile(props.getProperty(ENVIRONMENT_LOG_FILE))), parseLevel(props.getProperty(ENVIRONMENT_LOG_LEVEL))));
        }

        environment.setLocale(parseLocale(
                props.getProperty(ENVIRONMENT_LOCALE_LANGUAGE),
                props.getProperty(ENVIRONMENT_LOCALE_COUNTRY),
                props.getProperty(ENVIRONMENT_LOCALE_VARIANT)));

//        environment.addStats...
//        environment.addHandler(new LogHandler(os, (Level) jLoggingLevel.getSelectedItem()));
//        environment.addOut(new JTextWriter());
//        environment.addErr(new JTextWriter());
        return environment;
    }

    public static Locale parseLocale(String language, String country, String variant) {
        
        if (language != null && !language.equals("")) {
            if (country != null && !country.equals("")) {
                if (variant != null && !variant.equals("")) {
                    return new Locale(language, country, variant);
                } else {
                    return new Locale(language, country);
                }
            } else {
                return new Locale(language);
            }
        } else {
            return Locale.getDefault();
        }
    }

    public static Level parseLevel(String level) {
        try {
            return Level.parse(level);
        } catch (Exception ex) {
            return Level.INFO;
        }
    }
}
