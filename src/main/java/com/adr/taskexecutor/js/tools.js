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

var tools = {
    
    log: function (title, funcname, row) {

        print("[" + title + "." + funcname + "] {");
        var i = 0;
        for (var prop in row) {
            if (i++ > 0) {
                print(", ");
            }
            print("\"" + prop + "\": \"" + row[prop] + "\"");
        }
        print("}\n");
    },
    
    copyobj: function(obj) {
        var newobj = {};
        for (var prop in obj) {
            newobj[prop] = obj[prop];
        }
        return newobj;
    },
    
    isFunction: function(f) {
        return f && f.prototype;
    },
    
    
    _included: {},
    include: function(resource) {
        if (!this._included[resource]) {
            eval(String(Packages.com.adr.taskexecutor.common.Utils.loadResourceText(resource)));
            this._included[resource] = true;
        }
    }
};
