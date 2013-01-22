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

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author adrian
 */
public class ComposedWriter  extends Writer {

    private Writer out1;
    private Writer out2;

    public ComposedWriter(Writer out1, Writer out2) {
        this.out1 = out1;
        this.out2 = out2;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        out1.write(cbuf, off, len);
        out2.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        out1.flush();
        out2.flush();
    }

    @Override
    public void close() throws IOException {
        out1.close();
        out2.close();
    }
}
