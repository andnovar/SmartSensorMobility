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

package edu.sharif.ce.dml.common.logic.entity.evaluation;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 4:08:27 PM<br>
 * represent each row of a table
 */
public class EvaluationRecord {
    private List<String> values;
    private List<Double> consideringValues;

    public EvaluationRecord(List<String> values) {
        this.values = values;
    }

    public EvaluationRecord() {
    }

    protected List<String> getValues() {
        return values;
    }

    public String getValueAt(int index) {
        return values.get(index);
    }

    public void addValues(List values) {
        for (Object value : values) {
            this.values.add(TraceWriter.valueToString(value));
        }
    }

    public void addValue(Object value) {
        this.values.add(TraceWriter.valueToString(value));
    }

    public void setConsideringIndexes(List<Integer> consideringIndexes) {
        consideringValues = new ArrayList<Double>(consideringIndexes.size());
        for (Integer index : consideringIndexes) {
            consideringValues.add(Double.parseDouble(values.get(index)));
        }
    }

    public List<Double> getConsideringValues() {
        return consideringValues;
    }

    public List print(){
        return new LinkedList(values);
    }
}
