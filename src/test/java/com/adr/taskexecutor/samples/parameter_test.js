tools.DefineTask({
    title: "Parameter Test",   
    steps: {
        start: {
            run: function () {                
                logger.info("Starting task.");
                logger.info("parameter value for coco: " + this.params.coco);
            }
        }                  
    }
});
