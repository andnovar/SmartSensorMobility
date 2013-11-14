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

package edu.sharif.ce.dml.common.data;

import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoadingHandler;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 4:06:55 PM<br>
 * a loadinghandler class that can read evaluation files and return {@link edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord}s.
 * evaluation format:<br/><ul>
 * <li>first line: configs : key=value list seperated by application separator ({@link edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter#separator}).</li>
 * <li>second line: labels: label list seperated by application separator.</li>
 * <li>next lines: value of table: value list seperated by application separator
 * </ul>
 */
public class EvaluationLoadingHandler implements BulkLoadingHandler<EvaluationRecord> {
    //loadded labels
    private String[] labels;

    public List<? extends EvaluationRecord> bReadData(BufferedReader reader) throws IOException {
        reader.readLine();
        reader.readLine();
        String sData = null;
        List<EvaluationRecord> records = new LinkedList<EvaluationRecord>();
        while ((sData = reader.readLine()) != null) {
            records.add(new EvaluationRecord(new ArrayList<String>(Arrays.asList(sData.split(BufferOutputWriter.separator)))));
        }
        return records;
    }

    public Map<String, String> loadConfiguration(BufferedReader reader) throws IOException {
        reader.readLine();
        String firstLine = reader.readLine();
        labels = firstLine.split(BufferOutputWriter.separator);
        HashMap<String, String> labelsMap = new HashMap<String, String>();
        //columns should be unique
        for (int i = 0; i < labels.length; i++) {
            labelsMap.put(i + "", labels[i]);
        }
        return labelsMap;
    }

    public EvaluationRecord[] getEArray(int capacity) {
        return new EvaluationRecord[capacity];
    }

    public String[] getDataLabels() {
        return labels;
    }

    /**
     * converts a map of configs to a list!
     * @param conf
     * @return
     */
    public static List<String> convertConfigurations(Map<String, String> conf) {
        String[] labels = new String[conf.size()];
        for (String s : conf.keySet()) {
            labels[new Integer(s)] = conf.get(s);
        }
        return Arrays.asList(labels);
    }
}
