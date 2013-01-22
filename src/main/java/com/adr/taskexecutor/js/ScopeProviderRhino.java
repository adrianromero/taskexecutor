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

import com.adr.taskexecutor.common.ScopeProvider;
import java.io.PrintWriter;
import java.io.Reader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author adrian
 */
public class ScopeProviderRhino extends ScopeProvider {

    private LanguageProviderRhino rhinoparent;
    private Scriptable scope;

    ScopeProviderRhino(LanguageProviderRhino langprovider) throws Exception {

        langparent = langprovider;
        rhinoparent = langprovider;
       
        Context cx = rhinoparent.enterContext();
        try {
            scope = cx.initStandardObjects();
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object eval(Reader code, String name) throws Exception {
        Context cx = rhinoparent.enterContext();
        try {
            return cx.evaluateReader(scope, code, name == null ? "<cmd>" : name, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object invoke(String function, Object... parameter) throws Exception {
        Context cx = rhinoparent.enterContext();
        try {
            Object f = scope.get(function, scope);
            if (!(f instanceof Function)) {
                throw new NoSuchMethodException("\"" +function + "\" is undefined or not a function.");
            } else {
                rhinoparent.setScope(scope);
                return ((Function) f).call(cx, scope, scope, parameter);
            }
         } finally {
            Context.exit();
        }
    }

    @Override
    public void put(String name, Object value) {
        Context cx = rhinoparent.enterContext();
        try {
            ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope));
         } finally {
            Context.exit();
        }
    }

    @Override
    public void setErr(PrintWriter err) {
        Context cx = rhinoparent.enterContext();
        try {
            ScriptableObject.putProperty(scope, "err", Context.javaToJS(err, scope));
         } finally {
            Context.exit();
        }
    }

    @Override
    public void setOut(PrintWriter out) {
        Context cx = rhinoparent.enterContext();
        try {
            ScriptableObject.putProperty(scope, "out", Context.javaToJS(out, scope));
         } finally {
            Context.exit();
        }
    }
}
