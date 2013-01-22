tools.DefineTask({
    title: "$$title$$",
    firstStep: "start",
    
    steps: {
        start: {
            run: function () {                
                logger.info("Starting task.");
            }
        }                  
    }
});