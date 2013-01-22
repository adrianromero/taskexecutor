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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author adrian
 */
public class StopWatch {

    private static final ResourceBundle default_bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/i18n/messages"); // NOI18N
    private static final ResourceBundle root_bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/i18n/messages", Locale.ROOT); // NOI18N

    private long millis;

    public StopWatch() {
        millis = System.currentTimeMillis();
    }

    public long getMillisElapsed() {
        return System.currentTimeMillis() - millis;
    }

    public static String getTimeElapsed(long elapsed) {
        return getTimeElapsed(root_bundle, elapsed);
    }

    public static String getTimeElapsedLocalized(long elapsed) {
        return getTimeElapsed(default_bundle, elapsed);
    }

    private static String getTimeElapsed(ResourceBundle bundle, long elapsed) {
        
        long hours = elapsed / 3600000L;
        elapsed = elapsed % 3600000L;
        long minutes = elapsed / 60000L;
        elapsed = elapsed % 60000L;
        long seconds = elapsed / 1000L;

        if (hours > 0L) {
            return MessageFormat.format(bundle.getString("message.hoursminutesseconds"), hours, minutes, seconds);
        } else if (minutes > 0L) {
            return MessageFormat.format(bundle.getString("message.minutesseconds"), minutes, seconds);
        } else {
            return MessageFormat.format(bundle.getString("message.seconds"), seconds);
        }
    }
}
