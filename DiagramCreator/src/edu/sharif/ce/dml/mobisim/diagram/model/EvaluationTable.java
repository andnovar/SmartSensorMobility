/*
 * Copyright (c) 2005-2008 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
 * The license.txt file describes the conditions under which this software may be distributed.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package edu.sharif.ce.dml.mobisim.diagram.model;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 4, 2007
 * Time: 10:01:00 PM<br>
 * entity object for an evaluation file specially created to use in this package (to create diagram file).
 */
public class EvaluationTable {
    /**
     * String contains ordered list of variables
     */
    private String VariableNames;
    /**
     * values that their records are important but not used as main variables.
     */
    private List<String> otherVariablesValue;
    /**
     * ordered rows of the table
     */
    private Set<EvaluationRow> rows = new TreeSet<EvaluationRow>();
    /**
     * list of values of variables used as head row (columns header) of table. each column header is a list of
     * some variables' value.
     */
    private List<List<String>> columns;
    /**
     * column which its value used as table body data
     */
    private String evaluationColumn;
    /**
     * Ordered set of {@link #otherVariablesValue} columns name.
     */
    private static TreeSet<String> importantVariables;
    /**
     * column which its value used to create rows
     */
    private static String rowNameColumn;

    /**
     * a map which associates each row value with its corresponidng {@link edu.sharif.ce.dml.mobisim.diagram.model.EvaluationRow}
     */
    private Map<String, EvaluationRow> rowNameRow = new HashMap<String, EvaluationRow>();

    /**
     * separator used to seperate variables values.
     */
    public static final String SEPARATOR = "-";

    public static void setRowNameColumn(String rowNameColumn) {
        EvaluationTable.rowNameColumn = rowNameColumn;
    }

    public String getRowNameColumn() {
        return rowNameColumn;
    }

    public String getEvaluationColumn() {
        return evaluationColumn;
    }

    public static void setImportantVariables(TreeSet<String> importantVariables) {
        EvaluationTable.importantVariables = importantVariables;
    }

    /**
     * @return list of {@link #importantVariables}+'='+corresponding value.
     */
    public List<String> getOtherVariablesString() {
        List<String> list = new LinkedList<String>();
        Iterator<String> it2 = otherVariablesValue.iterator();
        for (String importantVariable : importantVariables) {
            list.add(importantVariable + "=" + it2.next());
        }
        return list;
    }

    public void setEvaluationColumn(String evaluationColumn) {
        this.evaluationColumn = evaluationColumn;
    }

    public String getVariableNames() {
        return VariableNames;
    }

    public void setVariableNames(String variableNames) {
        VariableNames = variableNames;
    }

    public void setVariableNames(List<String> variableNames) {
        StringBuffer sb = new StringBuffer();
        for (String variableName : variableNames) {
            sb.append(variableName).append(SEPARATOR);
        }
        if (variableNames.size() > 0) {
            sb.delete(sb.lastIndexOf(SEPARATOR), sb.length());
        }
        VariableNames = sb.toString();
    }

    public List<String> getOtherVariablesValue() {
        return otherVariablesValue;
    }

    public void setOtherVariablesValue(List<String> otherVariablesValue) {
        this.otherVariablesValue = otherVariablesValue;
    }

    public boolean equals(Object obj) {
        return this.otherVariablesValue.equals(((EvaluationTable) obj).otherVariablesValue);
    }

    /**
     * sets data in row <tt>rowName</tt> and column <tt>column</tt> to <tt>evaluationData</tt>
     * @param rowName
     * @param column
     * @param evalueationData
     */
    public void setData(String rowName, int column, double evalueationData) {
        EvaluationRow currentRow = rowNameRow.get(rowName);
        if (currentRow == null) {
            currentRow = new EvaluationRow(rowName, columns.size());
            rows.add(currentRow);
            rowNameRow.put(rowName, currentRow);
        }
        currentRow.setValue(column, evalueationData);
    }

    public Set<EvaluationRow> getRows() {
        return rows;
    }

    public void setRows(Set<EvaluationRow> rows) {
        this.rows = rows;
    }

    /**
     * @return table columns' value.
     */
    public List<String> getColumns() {
        List<String> columnsStrings = new ArrayList<String>(columns.size());
        for (List<String> column : columns) {
            StringBuffer sb = new StringBuffer();
            for (String c : column) {
                sb.append(c).append(SEPARATOR);
            }
            if (column.size() > 0) {
                sb.delete(sb.lastIndexOf(SEPARATOR), sb.length());
            }
            columnsStrings.add(sb.toString());
        }
        return columnsStrings;
    }

    public void setColumns(List<List<String>> columns) {
        this.columns = columns;
    }

}
