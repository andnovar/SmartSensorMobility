package edu.sharif.ce.dml.common.data.trace.config;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 10, 2010
 * Time: 1:02:06 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTraceWriterConfig extends ParameterableImplement{
    public abstract TraceWriter getNextTraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels) ;
}
