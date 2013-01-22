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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author adrian
 */
public class ConfigurationsMap {

    private Map<String, String> map = new HashMap<String, String>();
    private boolean dirty = false;

    public boolean isDirty() {
        return dirty;
    }
    
    public void resetDirty() {
        dirty = false;
    }
    
    public void put(String key, String value, boolean silent) {
        map.put(key, value);
        if (!silent) {
            dirty = true;
        }
    }

    public void put(String key, String value) {
        put(key, value, false);
    }

    public void remove(String key) {
        map.remove(key);
        dirty = true;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }
}
