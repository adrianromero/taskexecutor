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

    tools.HttpReader = function (url) {
        this.url = url;
        this.encoding = "UTF-8";
        this.method ="GET"; // or POST

        this.data = null;


        this.reader = null;
    }

    tools.HttpReader.prototype.init = function () {
        if (this.reader == null) {
            var urljava = new Packages.java.net.URL(url);
            var connection = urljava.openConnection();

            connection.setRequestMethod(method);
            connection.setAllowUserInteraction(false);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);


            
        }
    }

    tools.HttpReader.prototype.close = function () {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
    }

})();

