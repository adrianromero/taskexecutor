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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author adrian
 */
public class GeneratorStats {
    
    private List<StatsCollector> stats = new ArrayList<StatsCollector>();
    
    public void addStatsCollector(StatsCollector sc) {
        stats.add(sc);
    }

    public void start() {
        for(StatsCollector sc : stats) {
            sc.start();
        }
    }

    public void stop() {
        for(StatsCollector sc : stats) {
            sc.stop();
        }
    }
    
    public void begin(String task, String name) {
        for(StatsCollector sc : stats) {
            sc.begin(task, name);
        }
    }

    public void end(String task, String name) {
        for(StatsCollector sc : stats) {
            sc.end(task, name);
        }
    }

    public void recordbegin(String task, String name) {
        for(StatsCollector sc : stats) {
            sc.recordbegin(task, name);
        }
    }

    public void recordend(String task, String name) {
        for(StatsCollector sc : stats) {
            sc.recordend(task, name);
        }
    }    
}

