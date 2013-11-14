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

package edu.sharif.ce.dml.common.data.trace;

import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 24, 2007
 * Time: 11:08:43 AM <br/>
 * The class is responsible to write traces and entities
 */
public abstract class TraceWriter extends ParameterableImplement {
    /**
     * writer object.
     */
    protected BufferOutputWriter writer;

    /**
     * note that this constructor must be implemented in all children.
     *
     * @param parameters
     * @param dataLabels
     * @param writer
     * @param outputString
     */
    public TraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels, BufferOutputWriter writer,
                       String outputString) {
        this();
        this.writer = writer;
    }

    protected TraceWriter() {
        writer = BufferOutputWriter.createRandomWriter();
    }

    public void setWriter(BufferOutputWriter writer) {
        this.writer = writer;
    }

    /**
     * set initial parameters if has not been setted
     *
     * @param parameters
     * @param dataLabels
     * @param outputString will be passed to the writer
     */
    public abstract void init(Collection<StringDataParameter> parameters, String[] dataLabels, String outputString);

    public BufferOutputWriter getWriter() {
        return writer;
    }

    public abstract void writeTrace(Collection trace);

    /**
     * write buffer to the output
     */
    public abstract void flush();

    /**
     * write buffer to the output and close writer
     */
    public abstract void flushAndClose();

    /**
     * @param value
     * @return equivalent value for the <tt>value</tt> parameter
     */
    public static String valueToString(Object value) {
        if (value instanceof Number) {
            return BufferOutputWriter.DECIMAL_FORMATER.format(value)
                    .replaceAll("[^\\d\\.\\-]", "0");
        }
        return value.toString();
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {

    }

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }

    public static TraceWriter getNullTraceWriter() {
        return nullInstance;
    }

    public static boolean isNullInstance(TraceWriter tw) {
        return nullInstance.equals(tw);
    }

    private static TraceWriter nullInstance = new NullTraceWriter();

    public abstract String getOutputString();


    private static class NullTraceWriter extends TraceWriter {
        private String outString;
        public void init(Collection<StringDataParameter> parameters, String[] dataLabels, String outputString) {
            outString=outputString;
        }

        public void writeTrace(Collection trace) {

        }

        public void flush() {

        }

        public void flushAndClose() {

        }

        @Override
        public String getOutputString() {
            return outString;
        }


    }
}
