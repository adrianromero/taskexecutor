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
    
    var _tasks= {};
    
    tools.Task = tools.Object.extend({
        name: "init",  
        title: "No title", 
        
        includes: [],   
        firstStep: "start", // Can be a function that returns the next step name.   
        
        params: null,
        result: null,
        error: null,
        row: {},
        
        init: function () {
        },
        close: function () {
        },
        
        constructor: function () {
            tools.Object.apply(this, arguments);
            
            var index;

            // include external scripts.
            for (index in this.includes) {
                tools.include(this.includes[index]);
            }   
        },

        executeStep: function (nextstepdef) {

            var nextstep = tools.isFunction(nextstepdef) ? nextstepdef.apply(this) : nextstepdef;
            
            if (nextstep) {
                if (environment.trace) {
                    tools.log(this.name, nextstep, this.row);
                }
                environment.stats.begin(this.name, nextstep);    
                
                var step = this.steps[nextstep];

                try {
                    environment.stats.recordbegin(this.name, nextstep);

                    var it = step.run.apply(this);
                    
                    if (it) {
                        while ((this.row = it.nextRow())) {
                            this.executeStep(step.nextStep);
                        }
                    } else {
                        this.executeStep(step.nextStep);
                    }
                                        
                    environment.stats.recordend(this.name, nextstep);
                } finally {
                    environment.stats.end(this.name, nextstep);
                }                               
            }
        },
        
        execute: function (params) {
            this.params = params;
            this.error = null;
            this.result = null;
            this.row = {};
            
            try {
                this.init();
                this.executeStep(this.firstStep);
            } finally {                  
                this.close();
                this.params = null;   
            }
            
            if (this.error != null) {
                throw this.error;
            }
            return this.result;            
        }       
    });    
    
    tools.executeTask= function(params, taskname) {
        var Task = _tasks[taskname || "init"];
        return Task ? new Task().execute(params) : null;
    };

    tools.DefineTask = function (taskdef) {
        var Task = tools.Task.extend(taskdef);
        _tasks[taskdef.name || "init"] = Task;
    };
        
}());


function taskfunction(params) {
    return tools.executeTask(params);
}
