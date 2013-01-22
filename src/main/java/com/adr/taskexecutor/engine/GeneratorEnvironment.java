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

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

/**
 *
 * @author adrian
 */
public class GeneratorEnvironment {

    private GeneratorLoader loader;

    private Writer out;
    private Writer err;

    private boolean trace;
    private GeneratorStats stats;
    private List<Handler> handlers;
    private Locale locale;

    public GeneratorEnvironment() {
        
        loader = new GeneratorLoaderUnsupported(EnvironmentFactory.DEFAULT_LANGUAGE);

        out = new OutputStreamWriter(System.out);
        err = new OutputStreamWriter(System.err);

        trace = false;
        stats = new GeneratorStats();
        handlers = new ArrayList<Handler>();
        locale = Locale.getDefault();
    }

    public void start() {
        stats.start();
        loader.start();
    }

    public void stop() {
        stats.stop();
        loader.stop();
        for (Handler h : handlers) {
            h.close();
        }
    }

    public void setLoader(GeneratorLoader loader) {
        this.loader = loader;
    }

    public void setTrace(boolean value) {
        trace = value;
    }
    
    public boolean getTrace() {
        return trace;
    }

    public GeneratorStats getStats() {
        return stats;
    }
    
    public void setLocale(Locale value) {
        locale = value;
    }
    
    public Locale getLocale() {
        return locale;
    }

    public void addHandler(Handler value) {
        handlers.add(value);
    }

    Handler[] getAllHandlers() {
        return handlers.toArray(new Handler[handlers.size()]);
    }

    public void addOut(Writer value) {
        out = new ComposedWriter(out, value);
    }

    public Writer getOut() {
        return out;
    }

    public void addErr(Writer value) {
        err = new ComposedWriter(err, value);
    }

    public Writer getErr() {
        return err;
    }

    public final Object runProcess(String name) throws Exception {
        return runProcess(name, (Object) null);
    }

    public final Object runProcess(String name, String parameters) throws Exception {

        Generator gen = loader.getGeneratorByName(name);
        gen.setContext(this);
        return gen.run(parameters);
    }

    public final Object runProcess(String name, Object parameters) throws Exception {

        Generator gen = loader.getGeneratorByName(name);
        gen.setContext(this);
        return gen.run(parameters);
    }
}
