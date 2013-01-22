/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


tools.Task.extend({

    name: "init",
    firstStep: "start", // or function()
    
    includes: [
        "include1",
        "include2",
    ],
    
    init: function () {
    },
    
    close: function () {
    }, 
    
    steps: {
        step1: {
            run: function () {
            },
            nextStep: function () {
            }
        },
        step2: {
            run: function () {
            },
            nextStep: "xxxx"
        }                   
    }
});


tools.Task.extend({
    name: "init",
    firstStep: "start", // or function()
    
    steps: {
        start: {
            run: function () {                
                logger.info("Starting task.");
            }
        }                  
    }
});