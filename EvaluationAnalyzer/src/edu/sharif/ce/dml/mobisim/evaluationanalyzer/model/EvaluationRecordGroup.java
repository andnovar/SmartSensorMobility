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

package edu.sharif.ce.dml.mobisim.evaluationanalyzer.model;

import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 5:00:59 PM<br>
 */
public class EvaluationRecordGroup {
    private String name;
    protected Collection <EvaluationRecord> records = new LinkedList<EvaluationRecord>();
    public static int realGroupIndex=-1;

    public EvaluationRecordGroup(EvaluationRecordGroup evaluationRecordGroup) {
        this.name =evaluationRecordGroup.getName();
        records.addAll(evaluationRecordGroup.getRecords());
    }

    public EvaluationRecordGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<EvaluationRecord> getRecords() {
        return records;
    }



    public void setRecords(Collection<EvaluationRecord> records) {
        this.records = records;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationRecordGroup)) return false;

        EvaluationRecordGroup that = (EvaluationRecordGroup) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
