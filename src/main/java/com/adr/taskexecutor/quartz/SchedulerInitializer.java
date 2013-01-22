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

package com.adr.taskexecutor.quartz;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author adrian
 */
public class SchedulerInitializer {

    private static final Logger logger = Logger.getLogger(SchedulerInitializer.class.getName());

    private Map<String, Scheduler> schedulers = new HashMap<String, Scheduler>();

    public static Scheduler startup(File f) throws SchedulerException, IOException {

        Properties props = new Properties();
        Reader e = null;
        try {
            e = new FileReader(f);
            props.load(e);
            return startup(props);
        } finally {
            if (e != null) {
                e.close();
            }
        }
    }

    public static Scheduler startup(String text) throws SchedulerException, IOException {

        Properties props = new Properties();
        Reader e = null;
        try {
            e = new StringReader(text);
            props.load(e);
            return startup(props);
        } finally {
            if (e != null) {
                e.close();
            }
        }
    }

    public static Scheduler startup(Properties props) throws SchedulerException {

        String schedname = props.getProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "QuartzScheduler");
        SchedulerRepository schedRep = SchedulerRepository.getInstance();
        Scheduler scheduler = schedRep.lookup(schedname);
        if (scheduler == null || scheduler.isShutdown()) {
            logger.info("Initializing Scheduler...");
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(props);
            scheduler = factory.getScheduler();
            logger.info("Scheduler has been initialized...");
            
            return scheduler;
        } else {
            throw new SchedulerException(MessageFormat.format("Scheduler with name \"{0}\" already exists. Choose a different name.", schedname));
        }
    }

    public static String[] getSchedulers() throws SchedulerException {

        ArrayList<String> l = new ArrayList<String>();
        Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
        for (Scheduler s : schedulers) {
            if (!s.isShutdown()) {
                l.add(s.getSchedulerName());
            }
        }

        return l.toArray(new String [l.size()]);
    }
}
