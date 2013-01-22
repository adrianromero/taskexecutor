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

XMLRead = tools.Object.extend({
    filename: "",
    encoding: "UTF-8",
    xpathloop: "",
    xpathfields: {},
    
    _nodelist: null,
    _xpath: null,
    _nextline: 0,
    
    nextRow: function () {
        if (!this._nodelist) {
            var dbf = Packages.javax.xml.parsers.DocumentBuilderFactory.newInstance();
            var db = dbf.newDocumentBuilder();       
            var doc = db.parse(new Packages.org.xml.sax.InputSource(new Packages.java.io.InputStreamReader(new Packages.java.io.FileInputStream(this.filename), this.encoding)));
            
            this._xpath = Packages.javax.xml.xpath.XPathFactory.newInstance().newXPath();
            this._nodelist = this._xpath.evaluate(this.xpathloop, doc.getDocumentElement(), Packages.javax.xml.xpath.XPathConstants.NODESET);
            this._nextline = 0;            
        }
        
        if (this._nextline < this._nodelist.getLength()) {
            var result = {};
            for (var prop in this.xpathfields) {
                result[prop] = this._xpath.evaluate(this.xpathfields[prop], this._nodelist.item(this._nextline), Packages.javax.xml.xpath.XPathConstants.STRING)
            }

            this._nextline += 1;
            return result;           
        } else { 
            return null;
        }
    }
});

