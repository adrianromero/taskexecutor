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

import com.adr.datasql.KindParameters;
import com.adr.datasql.Parameters;
import com.adr.datasql.StatementUpdate;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author adrian
 */
public class StatementDeleteTask {

    private StatementUpdate stmt;
    
    public StatementDeleteTask(Connection c) {
        stmt = new StatementUpdate(c, "DELETE FROM TASK_RESOURCES WHERE NAME = ?", "NAME");
    }
    public int deleteTaskContent(final String name) throws SQLException {
        return stmt.exec(new Parameters() {
            public void write(KindParameters dp) throws SQLException {
                dp.setString("NAME", name);
            }
        });
    }
}
