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

import edu.sharif.ce.dml.common.data.trace.filter.TraceFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;

import java.io.File;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 4, 2007
 * Time: 6:59:01 PM<br>
 * An analyzer that uses a learn AccuracyEvaluator file.
 */
public interface LearnableAnalyzer {
    /**
     * learns from loadded EvalueationRecordGroups. They are initial groups
     * @param inputData
     */
    public void learn(Collection<EvaluationRecordGroup> inputData);

    /**
     * @return learn file that has been setted usually through a parameter.
     */
    public File getLearnFile();

    /**
     * @return learn file filetype.
     */
    public TraceFilter<EvaluationRecord> getLearnFileFilter();

}
