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

/* locale, parseInt */

(function () {
    
    var Formatter = tools.Object.extend({
        format: function(value) {
            return value ? this._format(value) : "";
        },
        parse: function(str) {
            return str ? this._parse(str) : null;
        },
        formatISO: function(value) {
            return value ? this._formatISO(value) : "";
        },
        parseISO: function(str) {
            return str ? this._parseISO(str) : null;
        }
    });
    

    var locale_format = function(value, decimals) {
 	var x = value.toFixed(decimals).split(".");
	var x1 = x[0];
	var x2 = x.length > 1 ? locale.getSeparator() + x[1] : "";
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
            x1 = x1.replace(rgx, '$1' + locale.getGrouping() + '$2');
	}
	return x1 + x2;
    };

    var locale_parse = function(str) {
        var str1 = str.replace(locale.getGrouping(), "");
        return parseFloat(str1.replace(locale.getSeparator(), "."));
    }; 
    
    // None
    var NONE = new (Formatter.extend({
        _format : function(value) {
            return value;
        },
        _parse : function(str) {
            return str;
        },
        _formatISO : function(value) {
            return value;
        },
        _parseISO : function(str) {
            return str;
        }
    }))();

    // String
    var STRING = new (Formatter.extend({
        _format : function(value) {
            return value;
        },
        _parse : function(str) {
            return str;
        },
        _formatISO : function(value) {
            return value;
        },
        _parseISO : function(str) {
            return str;
        }
    }))();
    
    var BOOLEAN = new (Formatter.extend({
        _format : function(value) {
            return value ? locale.getTrue() : locale.getFalse(); 
        },
        _parse : function(str) {
            return locale.getTrue().toLowerCase() == str.toLowerCase(); // locale true
        },
        _formatISO : function(value) {
            return value ? "true" : "false";
        },
        _parseISO : function(str) {
            return "true" == str.toLowerCase();
        }
    }))();
    
    var INTEGER = new (Formatter.extend({
        _format : function(value) {
            return String(value);
        },
        _parse : function(str) {
            return parseInt(str);
        },
        _formatISO : function(value) {
            return String(value);
        },
        _parseISO : function(str) {
            return parseInt(str);
        }
    }))();

    var NUMBER = new (Formatter.extend({
        _format : function(value) {
            return locale_format(value, locale.getDecimals());
        },
        _parse : function(str) {
            return locale_parse(str);
        },
        _formatISO : function(value) {
            return value.toString();
        },
        _parseISO : function(str) {
            return parseFloat(str);
        }
    }))();
    
    var CURRENCY = new (Formatter.extend({
        _format : function(value) {
            var s = locale_format(value, locale.getCurrencyDecimals());
            if (locale.getCurrencyAfter()) {
                return s + " " + locale.getCurrencySymbol();
            } else {
                return locale.getCurrencySymbol() + " " + s;
            }
        },
        _parse : function(str) {
            return locale_parse(str.replace(locale.getCurrencySymbol(), ""));
        },
        _formatISO : function(value) {
            return value.toString();
        },
        _parseISO : function(str) {
            return parseFloat(str);
        }
    }))();    
    
    var PERCENTAGE = new (Formatter.extend({
        _format : function(value) {
            return locale_format(value * 100, 2) + " " + locale.getPercentageSymbol();
        },
        _parse : function(str) {
            return locale_parse(str.replace(locale.getPercentageSymbol(), "")) / 100;
        },
        _formatISO : function(value) {
            return (value * 100).toString();
        },
        _parseISO : function(str) {
            return parseFloat(str) / 100;
        }
    }))();
    
    var DATE = new (Formatter.extend({
        _format : function(value) {
            return locale.formatDate(new Date(value), locale.getDatePattern());
        },
        _parse : function(str) {
            return new Date(locale.parseDate(str, locale.getDatePattern()).getTime());
        },
        _formatISO : function(value) {
            return locale.formatDate(new Date(value), "yyyy-MM-dd");
        },
        _parseISO : function(str) {
            return new Date(locale.parseDate(str, "yyyy-MM-dd").getTime());
        }
    }))();
    
    var DATETIME = new (Formatter.extend({
        _format : function(value) {
            return locale.formatDate(new Date(value), locale.getDateTimePattern());
        },
        _parse : function(str) {
            try {
                return new Date(locale.parseDate(str, locale.getDateTimePattern()).getTime());
            } catch (ex) {
                return tools.type.DATE._parse(str);
            }
        },
        _formatISO : function(value) {
            return locale.formatDate(new Date(value), "yyyy-MM-dd HH:mm:ss.SSS");
        },
        _parseISO : function(str) {
            try {
                return new Date(locale.parseDate(str, "yyyy-MM-dd HH:mm:ss.SSS").getTime());
            } catch (ex) {
                try {
                    return new Date(locale.parseDate(str, "yyyy-MM-dd HH:mm:ss").getTime());
                } catch (ex2) {
                    return tools.type.DATE._parseISO(str);
                }
            }
        }
    }))();

    var TIME = new (Formatter.extend({
        _format : function(value) {
            return locale.formatDate(new Date(value), locale.getTimePattern());
        },
        _parse : function(str) {
            return new Date(locale.parseDate(str, locale.getTimePattern()).getTime());
        },
        _formatISO : function(value) {
            return locale.formatDate(new Date(value), "HH:mm:ss.SSS");
        },
        _parseISO : function(str) {
            return new Date(locale.parseDate(str, "HH:mm:ss.SSS").getTime());
        }
    }))();
    
    tools.type = {
        NONE: NONE,
        STRING: STRING,
        BOOLEAN: BOOLEAN,
        INTEGER: INTEGER,
        NUMBER: NUMBER,
        CURRENCY: CURRENCY,
        PERCENTAGE: PERCENTAGE,
        DATE: DATE,
        DATETIME: DATETIME,
        TIME: TIME
    };

})();