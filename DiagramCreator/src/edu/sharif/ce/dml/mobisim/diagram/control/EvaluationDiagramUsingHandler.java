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

package edu.sharif.ce.dml.mobisim.diagram.control;

import edu.sharif.ce.dml.common.data.EvaluationLoadingHandler;
import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;
import edu.sharif.ce.dml.common.logic.entity.evaluation.EvaluationRecord;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUser;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUsingHandler;
import edu.sharif.ce.dml.mobisim.diagram.model.EvaluationTable;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 6, 2007
 * Time: 6:54:43 PM<br>
 * The using Handler class that is responsible to use evaluation datas and create a diagram
 */
public class EvaluationDiagramUsingHandler implements BulkUsingHandler<EvaluationRecord> {
    /**
     * list of evaluation labels. The position of labels in the list is important
     */
    private List<String> labels;
    /**
     * map which keeps track of a label and its position in {@link #labels} list.
     */
    private Map<String, Integer> labelNumber;

    /**
     * the key for loading template file.
     */
    public static final String TEMPLATE_FILE_KEY = "digaramtemplate";

    /**
     * excel template file
     */
    private final File templateFile;

    /**
     * excel output file
     */
    private final File outputFile;

    /**
     * the value of column that should used to draw diagram
     */
    private final String evaluationColumn;
    /**
     * series name column = first column.
     */
    private final String rowNameColumn;

    /**
     * list of labels that will be used to create columns
     */
    private final List<String> variableColumns;

    /**
     * variables to create sheets
     */
    private final List<String> importantColumns;

    /**
     * These parameters will be validated in {@link #validate()} method.
     * @param templateFile
     * @param outputFile it should be writable.
     * @param evaluationColumn should be not null, should be among labels
     * @param rowNameColumn should be not null, should be among labels.
     * @param variableColumns should be not null, its size should be greater than 0, values should be among labels
     * @param importantColumns should be not null, values should be among labels
     */
    public EvaluationDiagramUsingHandler(File templateFile, File outputFile, String evaluationColumn,
                                         String rowNameColumn, List<String> variableColumns,
                                         List<String> importantColumns) {
        this.templateFile = templateFile;
        this.outputFile = outputFile;
        this.evaluationColumn = evaluationColumn;
        this.rowNameColumn = rowNameColumn;
        this.variableColumns = variableColumns;
        this.importantColumns = importantColumns;

    }

    /**
     * validates input configurations.
     * @throws DiagramCreationException
     */
    public void validate() throws DiagramCreationException {
        //validate datas
        {
            //variableColumns Size >0
            if (rowNameColumn == null || evaluationColumn == null || importantColumns == null || variableColumns == null) {
                throw new DiagramCreationException("Some data is null");
            }
            if (variableColumns.size() == 0) {
                throw new DiagramCreationException("No variable column has been selected");
            }

            //labels should contains evaluationColumn & rowNameColumn & variableColumns & importantColumns
            if (!(labels.contains(rowNameColumn) && labels.contains(evaluationColumn) && labels.containsAll(importantColumns) && labels.containsAll(variableColumns))) {
                throw new DiagramCreationException("One of selected labels not exist in the file labels");
            }
            //evaluation column data should be double data

            try {
                new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                throw new DiagramCreationException("Can not open the file: "+outputFile.getPath()+". Please close it in other programs.");
            }
        }
    }

    public void use(EvaluationRecord[] data) {
        double[] evalDatas = new double[data.length];
        try {
            Integer evaluationIndex = labelNumber.get(evaluationColumn);
            for (int i = 0; i < data.length; i++) {
                evalDatas[i] = Double.parseDouble(data[i].getValueAt(evaluationIndex));
            }
        } catch (NumberFormatException e) {
            DevelopmentLogger.logger.debug("Evaluation data parse error. Evaluation column values should be \"Double Parsable\"");
        }


        List<ExtractedEvaluationData> extractedEvaluationDataList = new ArrayList<ExtractedEvaluationData>(data.length);
        //average evaluationColumns Data datas that important+variable+rowName are equals
        {
            int rowNameIndex = labelNumber.get(rowNameColumn);
            List<Integer> variablesIndex = new ArrayList<Integer>(variableColumns.size());
            for (String variableColumn : variableColumns) {
                variablesIndex.add(labelNumber.get(variableColumn));
            }
            List<Integer> importantIndexes = new ArrayList<Integer>(importantColumns.size());
            for (String importantColumn : importantColumns) {
                importantIndexes.add(labelNumber.get(importantColumn));
            }
            for (int i = 0; i < data.length; i++) {
                EvaluationRecord evaluationRecord = data[i];
                List<String> variablesData = new ArrayList<String>(variablesIndex.size());
                for (Integer index : variablesIndex) {
                    variablesData.add(evaluationRecord.getValueAt(index));
                }
                List<String> importantData = new ArrayList<String>(importantIndexes.size());
                for (Integer index : importantIndexes) {
                    importantData.add(evaluationRecord.getValueAt(index));
                }
                ExtractedEvaluationData eData = new ExtractedEvaluationData(evaluationRecord.getValueAt(rowNameIndex), evalDatas[i],
                        variablesData, importantData);
                int eIndex = extractedEvaluationDataList.indexOf(eData);
                if (eIndex < 0) {
                    extractedEvaluationDataList.add(eData);
                } else {
                    extractedEvaluationDataList.get(eIndex).addValue(eData);
                }
            }
        }

        //create model objects
        TreeSet<List<String>> importantVariablesData = new TreeSet<List<String>>(new VariableDataComparator());
        List<EvaluationTable> tables = new ArrayList<EvaluationTable>(importantVariablesData.size());
        {
            //create table columns Strings, so should find existed variable columns combinations
            EvaluationTable.setRowNameColumn(rowNameColumn);
            EvaluationTable.setImportantVariables(new TreeSet<String>(importantColumns));
            TreeSet<List<String>> columns = new TreeSet<List<String>>(new VariableDataComparator());
            for (ExtractedEvaluationData evaluationData : extractedEvaluationDataList) {
                columns.add(evaluationData.variablesData);
                importantVariablesData.add(evaluationData.importantDatas);
            }
            List<List<String>> sortedColumns = new ArrayList<List<String>>(columns.size());
            for (List<String> column : columns) {
                sortedColumns.add(column);
            }

            StringBuffer sb = new StringBuffer();
            for (String variableName : variableColumns) {
                sb.append(variableName).append(EvaluationTable.SEPARATOR);
            }
            if (variableColumns.size() > 0) {
                sb.delete(sb.lastIndexOf(EvaluationTable.SEPARATOR), sb.length());
            }
            String variableNames = sb.toString();

            for (List<String> importantStrings : importantVariablesData) {
                //each should be a Evaluation Table
                EvaluationTable evaluationTable = new EvaluationTable();
                evaluationTable.setColumns(sortedColumns);
                evaluationTable.setOtherVariablesValue(importantStrings);
                evaluationTable.setVariableNames(variableNames);
                evaluationTable.setEvaluationColumn(evaluationColumn);
                tables.add(evaluationTable);
            }
            //fill tables rows
            for (ExtractedEvaluationData extractedEvaluationData : extractedEvaluationDataList) {
                EvaluationTable tempTable = new EvaluationTable();
                tempTable.setOtherVariablesValue(extractedEvaluationData.importantDatas);
                EvaluationTable table = tables.get(tables.indexOf(tempTable));
                table.setData(extractedEvaluationData.rowName, sortedColumns.indexOf(extractedEvaluationData.variablesData),
                        extractedEvaluationData.evaluationData);
            }
        }

        //write to excel files
        {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(templateFile));
                XLSTransformer transformer = new XLSTransformer();
                List sheetNames = new ArrayList();
                int i=1;
                for (EvaluationTable table : tables) {
                    /*StringBuffer sb = new StringBuffer();
                    List<String> otherValues = table.getOtherVariablesValue();
                    for (int i1 = 0; i1 < importantColumns.size(); i1++) {
                        sb.append(importantColumns.get(i1)).append("=").append(otherValues.get(i1)).append(EvaluationTable.SEPARATOR);
                    }
                    if (importantColumns.size() > 0) {
                        sb.delete(sb.lastIndexOf(EvaluationTable.SEPARATOR), sb.length());
                    } else {
                        sb.append("untitled");
                    }
                    sheetNames.add(sb.toString());*/
                    sheetNames.add("sheet"+i++);
                }
                HSSFWorkbook resultWorkbook = transformer.transformMultipleSheetsList(is, tables, sheetNames, "table", new HashMap(), 0);
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                resultWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * manages to compare two list of doubles item by item.
     * If a value was not double parsable it will be compared as a String value.
     */
    private class VariableDataComparator implements Comparator<List<String>> {

        public int compare(List<String> o1, List<String> o2) {
            assert o1.size() == o2.size();
            Iterator<String> iterator2 = o2.iterator();
            for (String s1 : o1) {
                String s2 = iterator2.next();
                try {
                    double d1 = Double.parseDouble(s1);
                    double d2 = Double.parseDouble(s2);
                    if (d1 > d2) {
                        return (int) (Math.ceil(d1 - d2));
                    }
                    if (d1 < d2) {
                        return (int) (Math.floor(d1 - d2));
                    }
                } catch (NumberFormatException e) {
                    int sc = s1.compareToIgnoreCase(s2);
                    if (sc != 0) {
                        return sc;
                    }
                }
            }
            return 0;
        }
    }

    /**
     * entity object for extracted data from file, used for averaging evaluationDatas that <tt>rowname</tt>,
     * <tt>variableData</tt> and <tt>importantDatas</tt> are equals.
     */
    public class ExtractedEvaluationData {
        String rowName;
        double evaluationData;
        List<String> variablesData;
        List<String> importantDatas;
        int weight;

        private ExtractedEvaluationData(String rowName, double evaluationData, List<String> variablesData, List<String> importantDatas) {
            this.rowName = rowName;
            this.evaluationData = evaluationData;
            this.variablesData = variablesData;
            this.importantDatas = importantDatas;
            weight = 1;
        }

        @Override
        public boolean equals(Object obj) {
            assert obj instanceof ExtractedEvaluationData;
            ExtractedEvaluationData e = (ExtractedEvaluationData) obj;
            assert e.variablesData.size() == variablesData.size() : "variables data sizes varies";
            assert e.importantDatas.size() == importantDatas.size() : "important data sizes varies";
            if (!e.rowName.equals(rowName)) {
                return false;
            }
            for (int i = 0; i < variablesData.size(); i++) {
                if (!variablesData.get(i).equals(e.variablesData.get(i))) {
                    return false;
                }
            }
            for (int i = 0; i < importantDatas.size(); i++) {
                if (!importantDatas.get(i).equals(e.importantDatas.get(i))) {
                    return false;
                }
            }
            return true;
        }

        public void addValue(ExtractedEvaluationData eData) {
            evaluationData = (evaluationData * weight + eData.evaluationData * eData.weight) / (weight + eData.weight);
        }
    }

    public void setConfiguration(Map<String, String> conf) {
        String[] labels = new String[conf.size()];
        labelNumber = new HashMap<String, Integer>();
        for (String s : conf.keySet()) {
            String label = conf.get(s);
            Integer number = new Integer(s);
            labels[number] = label;
            labelNumber.put(label, number);
        }
        this.labels = Arrays.asList(labels);
    }

    public void stopLoading() {

    }

    public void endLoading() {

    }

    public void startLoading() {

    }

    public static void main(String[] args) throws InvalidRequiredInputFileException, DiagramCreationException {
        EvaluationDiagramUsingHandler diagramUsingHandler = new EvaluationDiagramUsingHandler(FileManager.getInstance()
                .getFile(TEMPLATE_FILE_KEY + ".xls", TEMPLATE_FILE_KEY,true, new javax.swing.filechooser.FileFilter[]{ConfigFileFilter.getXMLInstance()}, true),
                new File("outputTest.xls"), "RelativeSpeed", "Model", Arrays.asList("Spatial Dependency", "maxspeed"), Arrays.asList("Temporal Dependency"));
        User<EvaluationRecord> user = new BulkUser<EvaluationRecord>(diagramUsingHandler, new File("test.txt"), new EvaluationLoadingHandler());
        user.run();
    }

}
