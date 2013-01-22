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

    tools.JSONRead = function (filename) {
        this.filename = filename;
        this.encoding = "UTF-8";
        this.functionloop = null; // function (json) { return json.items }
        this.functionfields = null; // function (item) { return {name: item.name, title: item.title}; }

        this.itemlist = null;
        this.nextline = 0;
    }

    tools.JSONRead.prototype.init = function () {
        if (this.itemlist == null) {
            var json = JSON.parse(String(Packages.com.adr.taskexecutor.engine.Utils.loadReader(new Packages.java.io.InputStreamReader(new Packages.java.io.FileInputStream(this.filename), this.encoding))));
            this.itemlist = this.functionloop(json);
            this.nextline = 0;
        }
    }

    tools.JSONRead.prototype.hasMore = function () {
        this.init();
        return this.itemlist != null && this.nextline < this.itemlist.length;
    }

    tools.JSONRead.prototype.next = function () {
        this.init();
        var result = this.functionfields(this.itemlist[this.nextline]);;
        this.nextline += 1;
        return result;
    }

    tools.JSONRead.prototype.close = function () {
        this.itemlist = null;
        this.nextline = 0;
    }

})();