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

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jul 3, 2009
 * Time: 8:13:15 PM
 *<br/> $ns_ at <tt>time</tt> node_(<tt>id</tt>) setdest <tt>x</tt> <tt>y</tt> <tt>speed</tt><br/>
 * It is dependant to the position of first five columns

 */
public class NSTraceWriter extends TraceWriter {
    public void init(Collection<StringDataParameter> parameters, String[] dataLabels, String outputString) {
        String outputString2;
        if (outputString.contains(".")) {
            outputString2 = outputString.replaceAll("\\.[^\\.]*$", ".nam");
        } else {
            outputString2 = outputString + ".nam";
        }
        writer.setOutputString(outputString2);
        StringBuffer sb = new StringBuffer("# ");
        for (StringDataParameter parameter : parameters) {
            sb.append(parameter.toString()).append(BufferOutputWriter.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - BufferOutputWriter.separator.length(), sb.length());
        }
        writer.write(sb.append("\n").toString());
        sb = new StringBuffer("# ");
        for (String dataLabel : dataLabels) {
            sb.append(dataLabel).append(BufferOutputWriter.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - BufferOutputWriter.separator.length(), sb.length());
        }
        writer.write(sb.append("\n").toString());
    }

    public NSTraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels, BufferOutputWriter writer, String outputString) {
        super(parameters, dataLabels, writer, outputString);
        init(parameters, dataLabels, outputString);

    }

    public NSTraceWriter() {
        super();
    }

    public void writeTrace(Collection trace) {
        List<String> l = new ArrayList<String>();
        for (Object o : trace) {
            l.add(valueToString(o));
        }
        //$ns_ at <time> "$node_(<id>) setdest <x> <y> <speed>"
        String firstPart = new StringBuilder().append("$ns_ at ").append(l.get(0)).
                append(" \"$node").append(l.get(1)).append(" setdest ").
                append(l.get(2)).append(" ").append(l.get(3)).append(" ").append(l.get(4)).append("\"").toString();

        StringBuffer sb = new StringBuffer(firstPart+" ;# ");
        for (int i = 5; i < l.size(); i++) {
            sb.append(valueToString(l.get(i))).append(BufferOutputWriter.separator);
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

    @Override
    public String getOutputString() {
        return writer.getOutputString();
    }
}
