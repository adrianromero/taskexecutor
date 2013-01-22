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

import com.adr.taskexecutor.common.ScopeProvider;
import com.adr.taskexecutor.common.Utils;
import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.io.StringReader;

/**
 *
 * @author adrian
 */
public class Generator {

    private static final Logger logger = Logger.getLogger(Generator.class.getName());

    private ScopeProvider scopeprovider = null;
    
    public Generator(ScopeProvider scope, String scripttask, String name) throws Exception {

        scopeprovider = scope;
        initScriptEngine();
        scopeprovider.eval(new StringReader(scripttask), name);
    }

    private void initScriptEngine() throws Exception {

        // global javascript objects
        for (String globalscript : scopeprovider.getLanguageProvider().getGlobalScriptResources()) {
             scopeprovider.eval(Utils.getResourceReader(globalscript), globalscript);
        }
    }

    public void setContext(GeneratorEnvironment environment) {

        Logger scriptlogger =  Logger.getAnonymousLogger();

        for(Handler h: environment.getAllHandlers()) {
            scriptlogger.addHandler(h);
        }

        scopeprovider.put("environment", environment);
        scopeprovider.put("logger", scriptlogger);
        scopeprovider.put("locale", new LocaleFormats(environment.getLocale()));

        scopeprovider.setOut(new PrintWriter(environment.getOut()));
        scopeprovider.setErr(new PrintWriter(environment.getErr()));
    }

    public Object run(String jsonparameter) throws Exception {
        return run(scopeprovider.eval(new StringReader(jsonparameter), "<parameter>"));
    }

    public Object run(Object parameter) throws Exception {

        // run javascript objects
        for (String runscript : scopeprovider.getLanguageProvider().getRunScriptResources()) {
             scopeprovider.eval(Utils.getResourceReader(runscript), runscript);
        }
        return scopeprovider.invoke("taskfunction", parameter);
    }
}
