tools.DefineTask({
    title: "Sheldon Read",   
    includes: [
        "/com/adr/taskexecutor/tools/XMLRead.js"
    ],    
    steps: {
        start: {
            run: function () {   
            	var xml = new XMLRead();
                xml.filename = this.params.filename;
                xml.xpathloop = "/ItemSearchResponse/Items/Item";
                xml.xpathfields = {
                    asin: "ASIN/text()", 
                    author: "ItemAttributes/Author/text()",
                    title: "ItemAttributes/Title/text()", 
                    manufacturer: "ItemAttributes/Manufacturer/text()" 
                };
	        return xml;
            },
            nextStep: "logrow"
        },
        logrow: {
            run: function () {
                tools.log("sheldon", "mylog", this.row);                      
            }
        }
    }
});
