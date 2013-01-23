tools.DefineTask({
    title: "Lines Creator",   
    includes: [
        "/com/adr/taskexecutor/tools/LinesCreator.js"
    ],    
    steps: {
        start: {
            run: function () {   
            	var creator = new LinesCreator();
            	creator.size = this.params.linesnumber;
            	creator.lines = [
	              {reference: "1", name: "pear", price: 5.50},
	              {reference: "2", name: "peach", price: 2.00},
	              {reference: "3", name: "apple", price: 3.50},
	              {reference: "4", name: "banana", price: 5.20},
	              {reference: "5", name: "carrot", price: 2.10},
	              {reference: "6", name: "corn", price: 1.20},
	              {reference: "7", name: "strawberry", price: 3.00},
	              {reference: "8", name: "cherry", price: 3.80},
	              {reference: "9", name: "orange", price: 5.40},
	              {reference: "10", name: "lemon", price: 4.20}
	          ];
	          return creator;
            },
            nextStep: "logrow"
        },
        logrow: {
        	  run: function () {
        	      tools.log("list", "mylog", this.row);                      
        	  }
        }
    }
});
