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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author adrian
 */
public class ExecutorLogOutputStream extends OutputStream {

    private TaskExecutor.RunProcess ui;
    private ByteArrayOutputStream buf;



    public ExecutorLogOutputStream(TaskExecutor.RunProcess ui) {
        this.ui = ui;
        buf = new ByteArrayOutputStream();
    }

    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public void write(int b) throws IOException {
        if (buf == null) {
            return;
        }
        buf.write(b);
    }
    @Override
    public void write(byte b[], int off, int len) throws IOException {
        if (buf == null) {
            return;
        }
        buf.write(b, off, len);
    }
    @Override
    public void flush() {
        if (buf == null) {
            return;
        }
        try {
            if (buf.size() > 0) {
                ui.printLog(buf.toString(getEncoding()));
                buf.reset();
            }
        } catch (UnsupportedEncodingException ex) {
        }
    }
    @Override
    public void close() {
        flush();
        buf = null;
        ui = null;
    }
}
