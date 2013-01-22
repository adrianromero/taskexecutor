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

/**
 *
 * @author adrian
 */
public class StatsItem {

    private String task;
    private String name;

    int functionin = 0;
    int functionout = 0;
    int recordin = 0;
    int recordout = 0;
    long totaltime = 0L;

    StatsItem(String task, String name) {
        this.task = task;
        this.name = name;
    }

    public String getTask() {
        return task;
    }
    public String getName() {
        return name;
    }

    public int getFunctionIn() {
        return functionin;
    }
    public int getRecordIn() {
        return recordin;
    }
    public int getRecordRejected() {
        return recordin - recordout;
    }
    public double getTotalTime() {
        return totaltime / 1000.0;
    }
    public double getAverageTime() {
        return totaltime / functionin / 1000.0;
    }
}
