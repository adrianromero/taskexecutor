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
import com.adr.datasql.ResultsString;
import com.adr.datasql.StatementSelect;
import com.adr.datasql.StatementUpdate;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * @author adrian
 */
public class StatementPutTask {

    private StatementSelect<String> stmtselect;
    private StatementUpdate stmtinsert;
    private StatementUpdate stmtupdate;

    public StatementPutTask(Connection c) {
        stmtinsert = new StatementUpdate(c, "INSERT INTO TASK_RESOURCES(ID, NAME, CONTENT) VALUES (?, ?, ?)", "ID", "NAME", "CONTENT");
        stmtupdate = new StatementUpdate(c, "UPDATE TASK_RESOURCES SET CONTENT = ? WHERE ID = ?", "CONTENT", "ID");
        stmtselect = new StatementSelect<String>(c, "SELECT ID FROM TASK_RESOURCES WHERE NAME = ?", "NAME");
    }

    public int saveTaskContent(final String name, final String content) throws SQLException {
        final String id = stmtselect.find(ResultsString.INSTANCE, name);
        if (id == null) {
            return stmtinsert.exec(new Parameters() {
                public void write(KindParameters dp) throws SQLException {
                    try {
                        dp.setString("ID", UUID.randomUUID().toString());
                        dp.setString("NAME", name);
                        dp.setBytes("CONTENT", content.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException ex) {
                    }
                }
            });
        } else {
            return stmtupdate.exec(new Parameters() {
                public void write(KindParameters dp) throws SQLException {
                    try {
                        dp.setString("ID", id);
                        dp.setBytes("CONTENT", content.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException ex) {
                    }
                }
            });
        }
    }
}