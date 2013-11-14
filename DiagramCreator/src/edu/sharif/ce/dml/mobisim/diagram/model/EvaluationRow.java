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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 6, 2007
 * Time: 6:49:58 PM<br>
 * Entity for each row of evaluation
 */
public class EvaluationRow implements Comparable {
    /**
     * unique name of this evaluation
     */
    String rowName;
    /**
     * values in this row.
     */
    List<Double> values;

    public EvaluationRow() {
    }

    public EvaluationRow(String rowName, int valuesSize) {
        this.rowName = rowName;
        Double[] fillDouble = new Double[valuesSize];
        Arrays.fill(fillDouble, 0d);
        values = new ArrayList<Double>(Arrays.asList(fillDouble));
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public int compareTo(Object o) {
        return rowName.compareToIgnoreCase(((EvaluationRow) o).rowName);
    }

    public void setValue(int column, double evalueationData) {
        values.set(column, evalueationData);
    }
}
