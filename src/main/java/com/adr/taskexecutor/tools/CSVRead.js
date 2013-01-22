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

    function toObject(headers, line) {

        if (line == null) {
            return null;
        } else {
            var result = {};
            for (var i = 0; i < headers.length; i++) {
                result[headers[i]] = line[i];
            }
            return result;
        }
    }


    tools.CSVRead = function (filename) {
        this.filename = filename;
        this.encoding = "UTF-8";
        this.separator = ",";
        this.quote = "\"";
        this.escape = "\\";
        this.hasheader = true;
        this.headers = [];

        this.csv = null;
        this.nextline = null;
    }

    tools.CSVRead.prototype.init = function () {
        if (this.csv == null) {
            this.csv = Packages.au.com.bytecode.opencsv.CSVReader(
                    new Packages.java.io.InputStreamReader(new Packages.java.io.FileInputStream(this.filename), this.encoding),
                    this.separator, this.quote, this.escape, 0, true, true);
            if (this.hasheader) {
                this.headers = this.csv.readNext();
            }
            this.nextline = toObject(this.headers, this.csv.readNext());
        }
    }

    tools.CSVRead.prototype.hasMore = function () {
        this.init();
        return this.nextline != null;
    }

    tools.CSVRead.prototype.next = function () {
        this.init();
        var result = this.nextline;
        this.nextline = toObject(this.headers, this.csv.readNext());
        return result;
    }

    tools.CSVRead.prototype.close = function () {
        if (this.csv != null) {
            this.csv.close();
            this.csv = null;
            this.nextline = null;
        }
    }

})();

