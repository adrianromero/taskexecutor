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

/**
 *
 * @author adrian
 */
public class GeneratorLoaderUnsupported implements GeneratorLoader {

    private String languagename;
    private LanguageProvider language;
    
    
    public GeneratorLoaderUnsupported(String lang) {
        languagename = lang;
        language = LanguageProviderFactory.getInstance(lang);
    }

    @Override
    public final String getLanguageName() {
        return languagename;
    }

    @Override
    public final LanguageProvider getLanguage() {
        return language;
    }

    @Override
    public void start() {
    }
    @Override
    public void stop() {
    }
    @Override
    public Generator getGeneratorByName(String name) throws Exception {
        throw new UnsupportedOperationException("Execution of subtasks not supported: " + name);
    }
}
