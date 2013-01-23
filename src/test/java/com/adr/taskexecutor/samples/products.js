/* Sample file for importing products in Openbravo POS */


var task = function(parameter) {

    function executeStep(nextstep) {

        if (nextstep == null) {
            return;
        }
        var step = nextstep();
        if (step.loop == undefined) {
            executeStep(step.run());
        } else {
            while (step.loop()) {
                executeStep(step.run());
            }
        }
    }

    var process = {};
    var row = {};

    executeStep(step_start);

// Begin the definition of the process.

    function step_start() {
        return { run: function() {
//        process.conn = new DatabaseConnection(
//            parameter.db.driverlib,
//            parameter.db.driver,
//            parameter.db.URL,
//            parameter.db.user,
//            parameter.db.password);
            return step_readcsv;
        }};
    }

    function step_readcsv() {

        var csvfile = new tools.CSVFile(parameter.file.name);
        csvfile.separator = ",";
        csvfile.ignoreheader = true;
        csvfile.init();

        return { run: function() {
            row.line = csvfile.next();
            return step_populaterow;
        }, loop: function() {
            return csvfile.hasMore();
        }};
    }


    function step_populaterow() {
        return { run: function() {
            row.reference = row.line[0];
            row.name = row.line[1];
            row.barcode = row.line[2];
            row.category = row.line[3];
            row.pricebuy = row.line[4]; //Formats.toNumber(row.csvline[2]);
            delete(row.line);

            return step_log;
        }};
    }

    function step_log() {
        return { run: function() {
            var log = new tools.Log();
            log.print(row);

            return null;
        }};
    }

    function step_insert() {
        return { run: function() {

            row.categoryid = Utils.uuid();
            process.conn.save({table: "CATEGORIES", search: { NAME: row.category}, fields: { ID: row.categoryid }});
            process.conn.save({table: "PRODUCTS", fields: { ID: Utils.uuid()}});
            return null;
        }};
    }

// End definition

}

