/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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

package edu.sharif.ce.dml.common.data.trace.ns;

import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoadingHandler;
import edu.sharif.ce.dml.common.util.io.loader.stream.StreamLoadingHandler;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jul 3, 2009
 * Time: 8:13:04 PM
 */
public class NSSimulationTrackLoadingHandler implements BulkLoadingHandler<SnapShotData>,
        StreamLoadingHandler<SnapShotData> {
    ////////////////////////////////////
    protected Map<String, String> configurations;
    protected Map<String, Integer> labelNumber = null;

    long lastTime=0;
    /**
     * {@inheritDoc}<br/>
     * It uses loadded labels to load values. If it was null, the default value from {@link edu.sharif.ce.dml.common.data.entity.SnapShotData}
     * will be used.<br/>
     * loads range values if it exists.
     *
     * @param reader
     * @return
     * @throws java.io.IOException
     */
    public SnapShotData sReadData(BufferedReader reader) throws IOException {
        int nodeNumber = Integer.parseInt(configurations.get(NODE_NUMBER_KEY));
        SnapShotData snapShotData = new SnapShotData(nodeNumber);
        if (labelNumber == null || labelNumber.size() == 0) {
            labelNumber = new HashMap<String, Integer>();
            int i = 0;
            for (String label : snapShotData.getTraceLabels()) {
                labelNumber.put(label, i++);
            }
        }
        snapShotData.setTime(lastTime);
        //$ns_ at <time> "$node_(<id>) setdest <x> <y> <speed>"
        for (int i = 0; i < nodeNumber; i++) {
            String s = reader.readLine();
            if (s == null) {
                DevelopmentLogger.logger.fatal("input file has no data value ");
                throw new IOException("input file has no data value ");
            }
            Long time = Long.parseLong(s.substring(s.indexOf("at "), s.indexOf("\"")).trim().substring(3));
            String name = s.substring(s.indexOf("\""), s.indexOf("setdest")).trim();
            name = name.replaceAll("[^0-9]", "");
            String[] xys = s.substring(s.indexOf("setdest"), s.lastIndexOf("\"")).substring("setdest".length()).trim().split("\\s+");

            if (i==0){
                snapShotData.setTime(time);
                lastTime=time;
            }
            snapShotData.addNodeShadows(new NodeShadow(name,
                    new DataLocation(Integer.parseInt(xys[0]),
                            Integer.parseInt(xys[1])), Double.parseDouble(xys[2]), 0, 0));
        }
        return snapShotData;
    }

    public List<SnapShotData> bReadData(BufferedReader reader) throws IOException {
        List<SnapShotData> datas = new LinkedList<SnapShotData>();
        reader.readLine();
        reader.readLine();
        while (reader.ready()) {
            datas.add(sReadData(reader));
        }
        return datas;
    }

    public Map<String, String> loadConfiguration(BufferedReader reader) throws IOException {
        String configurationLine = reader.readLine();
        //skip # 
        configurationLine = configurationLine.substring(2, configurationLine.length());
        configurations = new TreeMap<String, String>();
        String[] rawConfigurations = configurationLine.split(BufferOutputWriter.separator);
        for (String rconfiguration : rawConfigurations) {
            if (rconfiguration.contains("=")) {
                String[] tempmString = rconfiguration.split("=");
                configurations.put(tempmString[0], tempmString[1]);
            }
        }
        labelNumber = new HashMap<String, Integer>();
        String labelsLine = reader.readLine();
        labelsLine = labelsLine.substring(2, labelsLine.length());
        String[] labelsString = labelsLine.split(BufferOutputWriter.separator);
        if (!(labelsString.length == 0 || (labelsString.length == 1 && labelsString[0].length() == 0))) {
            for (int i = 0; i < labelsString.length; i++) {
                labelNumber.put(labelsString[i], i);
            }
        }
        return configurations;
    }

    public SnapShotData[] getEArray(int capacity) {
        return new SnapShotData[capacity];
    }

    public String[] getDataLabels() {
        Set<String> attSet = labelNumber.keySet();
        String[] labels = new String[attSet.size()];
        for (String att : attSet) {
            labels[labelNumber.get(att)] = att;
        }
        return labels;
    }
}
