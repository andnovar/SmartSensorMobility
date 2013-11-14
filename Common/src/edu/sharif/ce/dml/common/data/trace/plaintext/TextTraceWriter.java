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

package edu.sharif.ce.dml.common.data.trace.plaintext;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 24, 2007
 * Time: 11:22:20 AM<br/>
 * write traces as a plain text to a writer.
 */
public class TextTraceWriter extends TraceWriter {

    public TextTraceWriter() {
        super();
    }

    public void init(Collection<StringDataParameter> parameters, String[] dataLabels, String outputString) {
        String outputString2;
        if (outputString.contains(".")) {
            outputString2 = outputString.replaceAll("\\.[^\\.]*$", ".txt");
        } else {
            outputString2 = outputString + ".txt";
        }
        writer.setOutputString(outputString2);
        StringBuffer sb = new StringBuffer();
        for (StringDataParameter parameter : parameters) {
            sb.append(parameter.toString()).append(BufferOutputWriter.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - BufferOutputWriter.separator.length(), sb.length());
        }
        writer.write(sb.append("\n").toString());
        sb = new StringBuffer();
        for (String dataLabel : dataLabels) {
            sb.append(dataLabel).append(BufferOutputWriter.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - BufferOutputWriter.separator.length(), sb.length());
        }
        writer.write(sb.append("\n").toString());
    }

    public TextTraceWriter(Collection<StringDataParameter> parameters, String dataLabels[],
                           BufferOutputWriter writer, String outputString) {
        super(parameters, dataLabels, writer, outputString);
        init(parameters, dataLabels, outputString);

    }

    @Override
    public String getOutputString() {
        return writer.getOutputString();
    }

    public void writeTrace(Collection trace) {
        StringBuffer sb = new StringBuffer();
        for (Object data : trace) {
            sb.append(valueToString(data)).append(BufferOutputWriter.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - BufferOutputWriter.separator.length(), sb.length());
        }
        writer.write(sb.append("\n").toString());
    }

    public void flush() {
        writer.flush();
    }

    public void flushAndClose() {
        writer.flushAndClose();
    }
}
