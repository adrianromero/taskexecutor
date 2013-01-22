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

package com.adr.taskexecutor.js;

import com.adr.taskexecutor.common.LanguageProvider;
import com.adr.taskexecutor.common.ScopeProvider;
import com.adr.taskexecutor.common.Utils;
import java.io.Reader;
import java.util.ResourceBundle;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.Main;

/**
 *
 * @author adrian
 */
public class LanguageProviderRhino implements LanguageProvider {

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/js/i18n/messages"); // NOI18N    

    private ContextFactory factory;
    private Main debugger;

    public LanguageProviderRhino() {
        factory = ContextFactory.getGlobal();
        debugger = null;
    }

    @Override
    public String getLanguage() {
        // This is the identifier of the LanguageProvider implementation.
        return "javascript";
    }

    @Override
    public String getLanguageName() {
        return "JavaScript";
    }

    @Override
    public String getExtension() {
        return ".js";
    }

    @Override
    public String getMime() {
        return "text/javascript";
    }

    @Override
    public String getExtensionName() {
        return bundle.getString("message.taskextensions");
    }
    
    @Override
    public String[] getGlobalScriptResources() {
        return new String[]{
            "/com/adr/taskexecutor/js/tools.js",
            "/com/adr/taskexecutor/js/rhinotools.js",
            "/com/adr/taskexecutor/js/object.js",
            "/com/adr/taskexecutor/js/datatype.js",
            "/com/adr/taskexecutor/js/engine.js"};
    }
    @Override
    public String[] getRunScriptResources() {
        return new String[]{
        };
    }
    @Override
    public String getNewScriptTemplate() {
        return "/com/adr/taskexecutor/js/tasknew.js";
    }

    @Override
    public ScopeProvider createScope() throws Exception {
        return new ScopeProviderRhino(this);
    }

    @Override
    public boolean isDebuggable() {
        return true;
    }

    @Override
    public void openDebugger() {

        if (debugger == null) {

            debugger = new Main("Task Executor JavaScript Debugger");

            debugger.setExitAction(new Runnable() {
                @Override
                public void run() {
                    closeDebugger();
                }
            });

            debugger.attachTo(factory);
    //        main.setScope(scope);
    //        main.doBreak();

            debugger.pack();
            debugger.setSize(600, 700);
            debugger.setVisible(true);
        }
    }

    @Override
    public void closeDebugger() {
        if (debugger != null) {
            debugger.dispose();
            debugger = null;
        }
    }

    @Override
    public boolean isDebuggerOpen() {
        return debugger != null;
    }

    Context enterContext() {
        return factory.enterContext();
    }

    void setScope(Scriptable scope) {
        if (debugger != null) {
            debugger.setScope(scope);
            debugger.doBreak();
        }
    }

    @Override
    public String getScriptTask(Reader task) throws Exception {
        
        return Utils.loadReader(task);
//        Writer w = new StringWriter();
//        TemplateEval e = new TemplateEval("javascript");
//        e.put("task", new Task(task));
//        e.eval(Utils.getResourceReader("/com/adr/taskexecutor/xmljs/body.jst"), w);
//        return w.toString();
    }
}
