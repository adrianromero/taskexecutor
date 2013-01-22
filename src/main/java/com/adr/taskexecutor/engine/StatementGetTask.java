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

import com.adr.datasql.ResultsByteA;
import com.adr.datasql.StatementSelect;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author adrian
 */
public class StatementGetTask {
    private StatementSelect<byte[]> rs;
    public StatementGetTask(Connection conn) {
        rs = new StatementSelect<byte[]>(conn, "SELECT CONTENT FROM TASK_RESOURCES WHERE NAME = ?", "NAME");
    }
    public String getTaskContent(String name) throws SQLException {
        try {
            byte[] result = rs.find(ResultsByteA.INSTANCE, name);
            if (result == null) {
                throw new SQLException("Task not found: " + name);
            } else {
                return new String(result, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            return null; // never reached.
        }
    }
}
