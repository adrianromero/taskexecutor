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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author adrian
 */
public abstract class StatsCollectorTime implements StatsCollector {

    private Map<String, StatsItem> stats;
    private List<StatsItem> statsorder;

    private long currentrunningtime = 0L;
    private Deque<Long> unassignedtime = new ArrayDeque<Long>();

    public StatsCollectorTime() {
        reset();
    }

    @Override
    public abstract void start();

    @Override
    public abstract void stop();

    protected StatsItem[] getStats() {
        return statsorder.toArray(new StatsItem[statsorder.size()]);
    }

    public final void reset() {
        stats = new HashMap<String, StatsItem>();
        statsorder = new ArrayList<StatsItem>();
    }

    @Override
    public void begin(String task, String name) {
        StatsItem item = getItem(task, name);
        item.functionin ++;

        long thistime = System.currentTimeMillis();
        unassignedtime.add(thistime - currentrunningtime);
        currentrunningtime = thistime;
    }

    @Override
    public void end(String task, String name) {
        StatsItem item = getItem(task, name);
        item.functionout ++;

        long thistime = System.currentTimeMillis();
        item.totaltime += thistime - currentrunningtime;
        currentrunningtime = thistime - unassignedtime.pollLast();
    }

    @Override
    public void recordbegin(String task, String name) {
        StatsItem item = getItem(task, name);
        item.recordin ++;
    }

    @Override
    public void recordend(String task, String name) {
        StatsItem item = getItem(task, name);
        item.recordout ++;
    }

    private StatsItem getItem(String task, String name) {
        StatsItem item = stats.get(task + "." + name);
        if (item == null) {
            item = new StatsItem(task, name);
            stats.put(task + "." + name, item);
            statsorder.add(item);
        }
        return item;
    }
}
