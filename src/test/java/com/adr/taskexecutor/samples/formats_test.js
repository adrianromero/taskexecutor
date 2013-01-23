tools.DefineTask({
    title: "Formats Test",   
    steps: {
        start: {
            run: function () {                
			logger.info("Performing format tests.");
			
			println(tools.type.BOOLEAN.format(true));
			println(tools.type.INTEGER.format(-11234));
			println(tools.type.NUMBER.format(-23123234232.33));
			println(tools.type.CURRENCY.format(234.3));
			println(tools.type.PERCENTAGE.format(0.25));
			println(tools.type.DATE.format(new Date()));
			println(tools.type.DATETIME.format(new Date()));
			println(tools.type.TIME.format(new Date()));
			
			println(tools.type.BOOLEAN.parse("true"));
			println(tools.type.INTEGER.parse("-11234"));
			println(tools.type.NUMBER.parse("-23.123.234.232,33"));
			println(tools.type.CURRENCY.parse("234,30 €"));
			println(tools.type.PERCENTAGE.parse("25,10 %"));
			println(tools.type.DATE.parse("6-oct-2010"));
			println(tools.type.DATETIME.parse("1-oct-2010 4:10:00"));
			println(tools.type.TIME.parse("10:10:21"));
            }
        }                  
    }
});
