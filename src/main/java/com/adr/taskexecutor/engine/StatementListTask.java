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

import com.adr.datasql.ResultsString;
import com.adr.datasql.StatementSelect;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author adrian
 */
public class StatementListTask {

    private StatementSelect<String> stmt;

    public StatementListTask(Connection c) {
        stmt = new StatementSelect<String>(c, "SELECT NAME FROM TASK_RESOURCES");
    }
    
    public List<String> getTaskList() throws SQLException {
        return stmt.list(ResultsString.INSTANCE);
    }
}
