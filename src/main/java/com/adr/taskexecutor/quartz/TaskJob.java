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

import com.adr.taskexecutor.engine.EnvironmentFactory;
import com.adr.taskexecutor.engine.GeneratorEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 *
 * @author adrian
 */
public class TaskJob  implements Job {

    private final static Logger logger = Logger.getLogger(TaskJob.class.getName());

    private final static String PREFIX = "com.adr.taskexecutor.";
    public final static String TASK_NAME_KEY = PREFIX + "task.name";
    public final static String TASK_PARAMETER_KEY = PREFIX + "task.parameter";

    private void printall(String[] values) {
        for (String val : values) {
            System.out.println(val);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {


        Map<String, String> datamap = new HashMap<String, String>();
        try {
            datamap.putAll(context.getScheduler().getContext());
            datamap.putAll(context.getMergedJobDataMap());
        } catch (SchedulerException ex) {
            throw new JobExecutionException(ex);
        }

        String name = datamap.get(TASK_NAME_KEY).toString();
        String parameter = datamap.get(TASK_PARAMETER_KEY).toString();

        EnvironmentFactory factory = new EnvironmentFactory(datamap);

        try {
            GeneratorEnvironment environment = factory.create();

            logger.log(Level.INFO, "Executing job: {0}", context.getJobDetail().getName());
            Object result = environment.runProcess(name, parameter == null ? "" : parameter);
            if (result == null) {
                logger.log(Level.INFO, "Execution success: {0}", context.getJobDetail().getName());
                context.setResult(0);
            } else {
                logger.log(Level.SEVERE, "Execution failed: {0}, Result: {1}", new Object[]{context.getJobDetail().getName(), result});
                context.setResult(1);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Execution failed. " + context.getJobDetail().getName(), ex);
            context.setResult(1);
        }
    }
}
