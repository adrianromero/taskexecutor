tools.DefineTask({
    title: "Calling steps",   
    steps: {
        start: {
            run: function () {                
                logger.info("Calling step");
                this.executeStep("step");
                logger.info("Returning step");
            }
        },
        step: {
            run: function () {
                logger.info("Executing step");
            }
        }
    }
});

