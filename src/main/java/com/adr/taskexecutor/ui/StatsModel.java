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

package com.adr.taskexecutor.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author adrian
 */
public class StatsModel extends AbstractTableModel {

    private static NumberFormat fmtint = NumberFormat.getIntegerInstance();
    private static NumberFormat fmttime = new DecimalFormat("#,##0.000");

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adr/taskexecutor/ui/i18n/messages"); // NOI18N

    private String[] columnNames = new String[]{
        bundle.getString("label.task"),
        bundle.getString("label.step"),
        bundle.getString("label.executions"),
        bundle.getString("label.records"),
        bundle.getString("label.rejected"),
        bundle.getString("label.totaltime"),
        bundle.getString("label.averagetime")
    };
    private List<String[]> data = new ArrayList<String[]>();

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public void addRow(String task, String name, int functionin, int recordin, int recordrejected, double totaltime, double avgtime) {
        data.add(new String[]{
                task,
                name,
                fmtint.format(functionin),
                fmtint.format(recordin),
                fmtint.format(recordrejected),
                fmttime.format(totaltime),
                fmttime.format(avgtime)
            });
        fireTableRowsInserted(data.size() - 1, data.size() -1);
    }

    public void reset() {
        data = new ArrayList<String[]>();
        fireTableDataChanged();
    }
}