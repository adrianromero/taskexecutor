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

package com.adr.taskexecutor.cli;

import com.adr.taskexecutor.engine.EnvironmentFactory;
import com.adr.taskexecutor.engine.GeneratorEnvironment;
import com.adr.taskexecutor.engine.StopWatch;
import com.adr.taskexecutor.common.Utils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 *
 * @author adrian
 */
public class StartCLI {

    public static void main(String[] args) {
        
        OptionParser parser = new OptionParser();
        OptionSpec<File> parameteroption = parser.acceptsAll(Arrays.asList("p", "parameter"), "Task parameters file.").withRequiredArg().ofType(File.class);
        OptionSpec<Boolean> traceoption = parser.acceptsAll(Arrays.asList("t", "trace"), "Shows trace information.").withRequiredArg().ofType(Boolean.class).defaultsTo(Boolean.FALSE);
        OptionSpec<String> statsfileoption = parser.acceptsAll(Arrays.asList("s", "statsfile"), "Statistics file.").withRequiredArg().ofType(String.class);
        OptionSpec<String> statslogfileoption = parser.acceptsAll(Arrays.asList("statslogfile"), "Statistics log file.").withRequiredArg().ofType(String.class);
        OptionSpec<String> logfileoption = parser.acceptsAll(Arrays.asList("logfile"), "Log file.").withRequiredArg().ofType(String.class);
        OptionSpec<String> logleveloption = parser.acceptsAll(Arrays.asList("loglevel"), "Log level.").withRequiredArg().ofType(String.class).defaultsTo(Level.INFO.toString());
        OptionSpec<String> languageoption = parser.acceptsAll(Arrays.asList("l", "language"), "Language of the localization.").withRequiredArg().ofType(String.class);
        OptionSpec<String> countryoption = parser.acceptsAll(Arrays.asList("c","country"), "Country of the localization.").withRequiredArg().ofType(String.class);
        OptionSpec<String> variantoption = parser.acceptsAll(Arrays.asList("v", "variant"), "Variant of the localization.").withRequiredArg().ofType(String.class);
        parser.acceptsAll(Arrays.asList("h", "?"), "Shows help.");

        OptionSet options = parser.parse(args);

        int retvalue = 0;

        if (options.has("?")) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ex) {
            }
        } else {

            List<String> tasks = options.nonOptionArguments();
            if (tasks.isEmpty()) {
                System.out.println("No tasks to execute defined.");
                retvalue = 2;
            } else {
                StopWatch watch = new StopWatch();
                GeneratorEnvironment environment = null;
                try {
                    Properties props = new Properties();
                    props.setProperty(EnvironmentFactory.LOADER_WORKING_FOLDER, System.getProperty("user.dir"));
                    props.setProperty(EnvironmentFactory.LOADER_DECORATOR_MEMCACHE, Boolean.TRUE.toString());
                    props.setProperty(EnvironmentFactory.ENVIRONMENT_TRACE, options.valueOf(traceoption).toString());
                    if (options.has(statsfileoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_STATS_FILE, options.valueOf(statsfileoption));
                    }
                    if (options.has(statslogfileoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_STATS_LOG_FILE, options.valueOf(statslogfileoption));
                    }
                    if (options.has(logfileoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_LOG_FILE, options.valueOf(logfileoption));
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_LOG_LEVEL, options.valueOf(logleveloption));
                    }
                    if (options.has(languageoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_LANGUAGE, options.valueOf(languageoption));
                    }
                    if (options.has(countryoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_COUNTRY, options.valueOf(countryoption));
                    }
                    if (options.has(variantoption)) {
                        props.setProperty(EnvironmentFactory.ENVIRONMENT_LOCALE_VARIANT, options.valueOf(variantoption));
                    }
                    EnvironmentFactory factory = new EnvironmentFactory(props);

                    // Create environment
                    environment = factory.create();
                    // environment.addOut
                    // environment.addErr

                    System.out.println("running all taks:");

                    environment.start();
                    for (String t : tasks) {
                    System.out.println("run: " + t);
                        Object result = environment.runProcess(t, options.has(parameteroption) ? Utils.loadText(options.valueOf(parameteroption)) : "");
                        if (result != null) {
                            System.out.println();
                            System.out.println("EXECUTION FAILED (total time: " + StopWatch.getTimeElapsed(watch.getMillisElapsed()) + ")");
                            System.out.println(result.toString());
                            retvalue = 1;
                            break;
                        }
                    }
                    if (retvalue == 0) {
                        System.out.println();
                        System.out.println("EXECUTION SUCCESSFUL (total time: " + StopWatch.getTimeElapsed(watch.getMillisElapsed()) + ")");
                    }

                // } catch (LanguageException ex) {
                // } catch (NoSuchMethodException ex) {
                // Much more exceptions to catch
                } catch (Exception ex) {
                    System.out.println();
                    System.out.println("EXECUTION ABORTED (total time: " + StopWatch.getTimeElapsed(watch.getMillisElapsed()) + ")");

                    StringWriter stacktrace = new StringWriter();
                    ex.printStackTrace(new PrintWriter(stacktrace));
                    System.out.println(stacktrace.toString());
                    retvalue = 1;
                } finally {
                    if (environment != null) {
                        environment.stop();
                    }
                }
            }
        }
        System.exit(retvalue);
    }
}
