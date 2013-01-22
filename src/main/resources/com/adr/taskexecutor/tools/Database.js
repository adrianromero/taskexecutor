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

(function () {

    tools.Database = function (driver, url, user, password) {
//        return (user == undefined)
//            ? new com.adr.taskexecutor.tools.Database(driver, url)
//            : new com.adr.taskexecutor.tools.Database(driver, url, user, password);

        java.lang.Class.forName(driver);
        if (user == undefined) {
            this.c = java.sql.DriverManager.getConnection(url);
        } else {
            this.c = java.sql.DriverManager.getConnection(url, user, password);
        }
        this.commitperiod = 1;
        this.commitcounter = 0;
    }


    tools.Database.prototype.setAutoCommit = function(value) {
        this.c.setAutoCommit(value);
    }

    tools.Database.prototype.getAutoCommit = function() {
        return this.c.getAutoCommit();
    }

    tools.Database.prototype.commit = function() {
        this.commitcounter ++;
        if (this.commitcounter >= this.commitperiod) {
            this.forceCommit();
        }
    }

    tools.Database.prototype.forceCommit = function() {
        this.commitcounter = 0;
        this.c.commit();
    }

    tools.Database.prototype.close = function() {
        if (this.c != null) {
            this.c.close();
            this.c = null;
            this.commitperiod = 1;
            this.commitcounter = 0;
        }
    }

    tools.Database.prototype.execute = function (sql, fields, line) {
        var sentence = this.Update(sql, fields);
        sentence.execute(line);
        sentence.close();
    }

    tools.Database.prototype.Update = function(sql, fields) {
        return new tools.Update(this, sql, fields);
    }

    tools.Update = function(db, sql, fields) {
        this.db = db;
        this.sql = sql;
        this.fields = fields;
        this.stmt = db.c.prepareStatement(sql);
    }

    tools.Update.prototype.execute = function(line) {

        stmtSetParameters(this.stmt, this.fields, line);
        return this.stmt.executeUpdate();
    }

    function stmtSetParameters(stmt, fields, line) {

        if (fields != undefined && fields != null) {
            for (var i = 0; i < fields.length; i++) {
                var value = line[fields[i]];
                if (value === undefined || value === null) {
                    stmt.setObject(i + 1, null);
                } else if (value.constructor === Boolean) {
                    stmt.setBoolean(i + 1, value);
                } else if (value.constructor === String) {
                    stmt.setString(i + 1, value);
                } else if (value.constructor === Number) {
                    if (value == Math.ceil(value)) {
                        stmt.setInt(i + 1, value);
                    } else {
                        stmt.setDouble(i + 1, value);
                    }
                } else if (value.constructor === Date) {
                    if (value.getHours() == 0 && value.getMinutes() == 0 && value.getSeconds() == 0 && value.getMilliseconds() == 0) {
                        stmt.setDate(i + 1, new java.sql.Date(value.getTime()));
                    } else if (value.getDate() == 1 && value.getMonth() == 0 && value.getYear() == 1970) {
                        stmt.setTime(i + 1, new java.sql.Time(value.getTime()));
                    } else {
                        stmt.setTimestamp(i + 1, new java.sql.Timestamp(value.getTime()));
                    }
                } else {
                    stmt.setObject(i + 1, value);
                }
            }
        }
    }

    tools.Update.prototype.close = function() {
        if (this.stmt != null) {
            this.stmt.close();
            this.stmt = null;
        }
    }

    tools.Database.prototype.Query = function(sql, fields) {
        return new tools.Query(this, sql, fields);
    }

    tools.Query = function(db, sql, fields) {
        this.db = db;
        this.sql = sql;
        this.fields = fields;
        this.stmt = db.c.prepareStatement(sql);
        this.rs = null;
        this.columns = null;
    }

    tools.Query.prototype.execute = function(line) {
        stmtSetParameters(this.stmt, this.fields, line);
    }

    tools.Query.prototype.init = function() {
        this.rs = this.stmt.executeQuery();
        var metadata = this.rs.getMetaData();
        var columncount = metadata.getColumnCount();
        this.columns = [];
        for (var i = 0; i < columncount; i++) {
            this.columns[i] = {};
            this.columns[i].name = metadata.getColumnName(i + 1);
            this.columns[i].type = metadata.getColumnType(i + 1);
        }
    }

    tools.Query.prototype.close = function() {
        
        if (this.rs != null) {
            this.rs.close();
            this.rs = null;
            this.columns = [];
        }
        if (this.stmt != null) {
            this.stmt.close();
            this.stmt = null;
        }
    }


    tools.Query.prototype.hasMore = function() {
        return this.rs.next();
    }

    tools.Query.prototype.next = function() {
        var record = {};
        for (var i = 0; i < this.columns.length; i++) {
            var type = this.columns[i].type;
            var value;
            if (type == java.sql.Types.BOOLEAN || type == java.sql.Types.BIT) {
                value = this.rs.getBoolean(i + 1);
                record[this.columns[i].name] = this.rs.wasNull() ? null : value
            } else if (type == java.sql.CHAR || type == java.sql.NCHAR || type == java.sql.NVARCHAR|| type == java.sql.VARCHAR || type == java.sql.LONGNVARCHAR || type == java.sql.LONGVARCHAR  || type == java.sql.ROWID) {
                record[this.columns[i].name] = this.rs.getString(i + 1);
            } else if (type == java.sql.BIGINT || type == java.sql.INTEGER || type == java.sql.SMALLINT || type == java.sql.TINYINT) {
                value = this.rs.getInt(i + 1);
                record[this.columns[i].name] = this.rs.wasNull() ? null : value
            } else if (type == java.sql.DECIMAL || type == java.sql.DOUBLE || type == java.sql.FLOAT || type == java.sql.NUMERIC || type == java.sql.REAL) {
                value = this.rs.getDouble(i + 1);
                record[this.columns[i].name] = this.rs.wasNull() ? null : value
            } else if (type == java.sql.DATE) {
                value = this.rs.getDate(i + 1)
                record[this.columns[i].name] = value == null ? null : new Date(value.getTime());
            } else if (type == java.sql.TIME) {
                value = this.rs.getTime(i + 1)
                record[this.columns[i].name] = value == null ? null : new Date(value.getTime());
            } else if (type == java.sql.TIMESTAMP) {
                value = this.rs.getTimestamp(i + 1)
                record[this.columns[i].name] = value == null ? null : new Date(value.getTime());
            } else {
                record[this.columns[i].name] = this.rs.getObject(i + 1);
            }
        }
        return record;
    }

    tools.Database.prototype.Upsert = function(table, keyfields, fields) {
        return new tools.Upsert(this, table, keyfields, fields);
    }

    tools.Upsert = function(db, table, fields, keyfields) {
        this.db = db;
        this.table = table;
        this.fields = fields;
        this.keyfields = keyfields;

        var update = "UPDATE " + table + " SET ";
        for (var i = 0; i < fields.length; i++) {
            if (i > 0) {
                update += ", ";
            }
            update += fields[i] + " = ? "
        }
        update += " WHERE ";
        for (var j = 0; j < keyfields.length; j++) {
            if (j > 0) {
                update += "AND ";
            }
            update += keyfields[j] + " = ? ";
        }
        this.stmtupdate = db.c.prepareStatement(update);

        var insert = "INSERT INTO " + table + " (";
        for (var k = 0; k < fields.length; k++) {
            if (k > 0) {
                insert += ", ";
            }
            insert += fields[k];
        }
        for (var l = 0; l < keyfields.length; l++) {
            if (k + l > 0) {
                insert += ", ";
            }
            insert += keyfields[l];
        }
        insert += ") VALUES (";
        for (var m = 0; m < keyfields.length + fields.length; m++) {
            if (m > 0) {
                insert += ", ";
            }
            insert += "?";
        }
        insert+= ")" ;
        this.stmtinsert = db.c.prepareStatement(insert);
    }

    tools.Upsert.prototype.execute = function(line) {

        function setParameters(stmt, fields, keyfields) {
            for (var i = 0; i < fields.length; i++) {
                stmt.setString(i + 1, line[fields[i]]);
            }
            for (var j = 0; j < keyfields.length; j++) {
                stmt.setString(i + j + 1, line[keyfields[j]]);
            }
        }

        setParameters(this.stmtupdate, this.fields, this.keyfields);
        if (this.stmtupdate.executeUpdate() == 0) {
            setParameters(this.stmtinsert, this.fields, this.keyfields);
            return this.stmtinsert.executeUpdate();
        } else {
            return 1;
        }
    }

    tools.Upsert.prototype.close = function() {
        if (this.stmtupdate != null) {
            this.stmtupdate.close();
            this.stmtupdate = null;
        }
        if (this.stmtinsert != null) {
            this.stmtinsert.close();
            this.stmtinsert = null;
        }
    }

})();