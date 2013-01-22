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

    tools.FileReader = function (filename) {
        this.filename = filename;
        this.encoding = "UTF-8";

        this.reader = null;
    }

    tools.FileReader.prototype.init = function () {
        if (this.reader == null) {
            this.reader = new Packages.java.io.InputStreamReader(new Packages.java.io.FileInputStream(this.filename), this.encoding);
        }
    }

    tools.FileReader.prototype.close = function () {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
    }

})();

