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
public class StatsCollectorLog implements StatsCollector {

    private static final Logger logger = Logger.getLogger(StatsCollectorLog.class.getName());

    private File f;
    private Writer w = null;

    public StatsCollectorLog(File f) {
        this.f = f;
    }

    @Override
    public void start() {
        try {
            f.getAbsoluteFile().getParentFile().mkdirs();
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        if (w != null) {
            try {
                w.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void begin(String task, String name) {
        try {
            w.write(Long.toString(System.currentTimeMillis()));
            w.write(", begin, ");
            w.write(task);
            w.write(", ");
            w.write(name);
            w.write("\n");
            w.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void end(String task, String name) {
        try {
            w.write(Long.toString(System.currentTimeMillis()));
            w.write(", end, ");
            w.write(task);
            w.write(", ");
            w.write(name);
            w.write("\n");
            w.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void recordbegin(String task, String name) {
        try {
            w.write(Long.toString(System.currentTimeMillis()));
            w.write(", record begin, ");
            w.write(task);
            w.write(" , ");
            w.write(name);
            w.write("\n");
            w.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void recordend(String task, String name) {
        try {
            w.write(Long.toString(System.currentTimeMillis()));
            w.write(", record end, ");
            w.write(task);
            w.write(", ");
            w.write(name);
            w.write("\n");
            w.flush();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
