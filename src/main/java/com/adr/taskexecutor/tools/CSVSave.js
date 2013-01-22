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

tools.CSVSave = function (filename) {
    this.filename = filename;
    this.encoding = "UTF-8";
    this.separator = ",";
    this.quote = "\"";
    this.escape = "\\";
    this.lineend = "\n";
    this.append = false;
    this.hasheader = true;
    this.headers = [];

    this.csv = null;
}

tools.CSVSave.prototype.init = function () {
    if (this.csv == null) {
        this.csv = new Packages.au.com.bytecode.opencsv.CSVWriter(
                new Packages.java.io.OutputStreamWriter(
                new Packages.java.io.FileOutputStream(this.filename, this.append),
                    this.encoding),
                    this.separator, this.quote, this.escape, this.lineend);
        if (this.hasheader) {
            this.csv.writeNext(this.headers);
        }
    }
}

tools.CSVSave.prototype.write = function(line) {
    this.init();
    var arrayline = [];
    for (var i = 0; i < this.headers.length; i++) {
        arrayline[i] = line[this.headers[i]];
    }
    this.csv.writeNext(arrayline);
}

tools.CSVSave.prototype.close = function() {
    if (this.csv != null) {
        this.csv.close();
        this.csv = null;
    }
}
