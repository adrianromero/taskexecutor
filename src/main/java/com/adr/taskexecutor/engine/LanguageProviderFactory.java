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

import com.adr.taskexecutor.common.LanguageProvider;
import com.adr.taskexecutor.common.ScopeProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 * @author adrian
 */
public class LanguageProviderFactory {

    private static Map<String, LanguageProvider> langsprovider = null;

    private LanguageProviderFactory() {
    }

    private static void init() {
        if (langsprovider == null) {
             ServiceLoader<LanguageProvider> service = ServiceLoader.load(LanguageProvider.class);
             langsprovider = new HashMap<String, LanguageProvider>();
             for (LanguageProvider lp : service) {
                 langsprovider.put(lp.getLanguage(), lp);
             }
        }
    }

    public static LanguageProvider[] getAllInstances() {
        init();
        return langsprovider.values().toArray(new LanguageProvider[langsprovider.size()]);
    }
    
    public static LanguageProvider getInstance(String name) {
        init();
        return langsprovider.get(name);
    }

    public static ScopeProvider createScope(String name) throws Exception {
        LanguageProvider lp = getInstance(name);
        return lp == null
                ? null
                : lp.createScope();
    }
}
