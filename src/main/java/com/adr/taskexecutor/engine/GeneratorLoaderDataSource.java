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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


/**
 *
 * @author adrian
 */
public class GeneratorLoaderDataSource extends GeneratorLoaderPersistent {

    private static final Logger logger = Logger.getLogger(GeneratorLoaderDataSource.class.getName());

    private DataSource ds;
    private Connection conn;

    private StatementGetTask sentget;
    private StatementDeleteTask sentdelete;
    private StatementPutTask sentput;
    private StatementListTask sentlist;

    public GeneratorLoaderDataSource(String lang, DataSource ds) {
        super(lang);
        
        this.ds = ds;
        this.conn = null;
        this.sentget = null;
    }

    public DataSource getDataSource() {
        return ds;
    }

    @Override
    public void stop() {
        super.stop();

        sentget = null;
        sentdelete = null;
        sentput = null;
        sentlist = null;

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            conn = null;
        }
    }

    @Override
    public Generator getGeneratorByName(String name) throws Exception {

        if (getLanguage() == null) {
            throw new Exception("Task language not supported: " + getLanguageName());
        }
        return new Generator(getLanguage().createScope(), getLanguage().getScriptTask(new StringReader(get(name))), name);
    }

    @Override
    public String get(String name) throws Exception {
        if (conn == null) {
            conn = ds.getConnection();
        }
        if (sentget == null) {
            sentget = new StatementGetTask(conn);
        }
        return sentget.getTaskContent(name);
    }

    @Override
    public int delete(String name) throws Exception {
        if (conn == null) {
            conn = ds.getConnection();
        }
        if (sentdelete == null) {
            sentdelete = new StatementDeleteTask(conn);
        }
        return sentdelete.deleteTaskContent(name);
    }

    @Override
    public void put(String name, String task) throws Exception {
        if (conn == null) {
            conn = ds.getConnection();
        }
        if (sentput == null) {
            sentput = new StatementPutTask(conn);
        }
        sentput.saveTaskContent(name, task);
    }

    @Override
    public List<String> list() throws Exception {
        if (conn == null) {
            conn = ds.getConnection();
        }
        if (sentlist == null) {
            sentlist =  new StatementListTask(conn);
        }
        return sentlist.getTaskList();
    }

    @Override
    public void commit() {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void rollback() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}

