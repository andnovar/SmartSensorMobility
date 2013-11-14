package edu.sharif.ce.dml.common.data.trace.config;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 10, 2010
 * Time: 12:59:19 AM
 */
public class NullTraceWriterConfig extends AbstractTraceWriterConfig {

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
    }

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }

    @Override
    public TraceWriter getNextTraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels) {
        TraceWriter nullWriter = TraceWriter.getNullTraceWriter();
        nullWriter.init(parameters,dataLabels,"");
        return nullWriter;
    }
}
