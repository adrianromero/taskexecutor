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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author adrian
 */
public class GeneratorLoaderCache implements GeneratorLoader {

    private Map<String, Generator> gencache = new HashMap<String, Generator>();
    private GeneratorLoader mygenloader;

    public GeneratorLoaderCache(GeneratorLoader mygenloader) {
        this.mygenloader = mygenloader;
    }

    public void resetCache() {
         gencache = new HashMap<String, Generator>();
    }

    @Override
    public final String getLanguageName() {
        return mygenloader.getLanguageName();
    }

    @Override
    public final LanguageProvider getLanguage() {
        return mygenloader.getLanguage();
    }

    @Override
    public void start() {
        mygenloader.start();
        resetCache();
    }

    @Override
    public void stop() {
        mygenloader.stop();
    }

    @Override
    public final Generator getGeneratorByName(String name) throws Exception {

        Generator gen = gencache.get(name);
        if (gen == null) {
            gen = mygenloader.getGeneratorByName(name);
            gencache.put(name, gen);
        }
        return gen;
    }
}
