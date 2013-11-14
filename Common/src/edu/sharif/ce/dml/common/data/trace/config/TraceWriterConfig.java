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

package edu.sharif.ce.dml.common.data.trace.config;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.filenamegenerator.FileNameGenerator;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 11, 2008
 * Time: 2:12:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class TraceWriterConfig extends AbstractTraceWriterConfig {
    protected SelectOneParameterable fileNameGenerator;
    protected ParameterableParameter outputFolder;
    protected SelectOneParameterable traceWriter;

    public TraceWriterConfig() {
        this.fileNameGenerator = new SelectOneParameterable(true);
        this.traceWriter = new SelectOneParameterable(false);
        outputFolder = new ParameterableParameter();
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        fileNameGenerator = (SelectOneParameterable) parameters.get("filenamegenerator");
        outputFolder = (ParameterableParameter) parameters.get("outputfolder");
        traceWriter = (SelectOneParameterable) parameters.get("tracewriter");
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("filenamegenerator", fileNameGenerator);
        parameters.put("outputfolder", outputFolder);
        parameters.put("tracewriter", traceWriter);
        return parameters;
    }

    public void setPrefix(String s) {
        getFileGenerator().setPrefix(s);
    }

    public void setPostfix(String s) {
        getFileGenerator().setPostfix(s);
    }

    public String getPrefix() {
        return getFileGenerator().getPrefix();
    }

    public String getPostfix() {
        return getFileGenerator().getPostfix();
    }


    private File getNextFile() {
        File file = new File(((FileParameter) outputFolder.getValue()).getValue().getPath(),
                getFileGenerator().getNextFileName());
        if (!file.isDirectory()) {
            file.delete();
        }

        return file;
    }

    public TraceWriter getNextTraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels) {
        TraceWriter traceWriter1 = (TraceWriter) traceWriter.getValue();
        traceWriter1.init(parameters, dataLabels, getNextFile().getPath());
        return traceWriter1;
    }

    private FileNameGenerator getFileGenerator() {
        assert fileNameGenerator.getValue() != null;
        return (FileNameGenerator) fileNameGenerator.getValue();
    }


}
