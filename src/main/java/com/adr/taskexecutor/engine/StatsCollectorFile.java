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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
public class StatsCollectorFile extends StatsCollectorTime {

    private static final Logger logger = Logger.getLogger(StatsCollectorFile.class.getName());

    private File f;

    public StatsCollectorFile(File f) {
        this.f = f;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

        StatsItem[] stats = getStats();

        Writer w = null;
        try {
            f.getAbsoluteFile().getParentFile().mkdirs();
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
            for(StatsItem item : stats) {
                w.write(item.getTask());
                w.write(", ");
                w.write(item.getName());
                w.write(", ");
                w.write(Integer.toString(item.getFunctionIn()));
                w.write(", ");
                w.write(Integer.toString(item.getRecordIn()));
                w.write(", ");
                w.write(Integer.toString(item.getRecordRejected()));
                w.write(", ");
                w.write(Double.toString(item.getTotalTime()));
                w.write(", ");
                w.write(Double.toString(item.getAverageTime()));
                w.write("\n");
            }
            w.flush();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }
}
