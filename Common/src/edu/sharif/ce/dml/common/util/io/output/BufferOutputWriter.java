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

package edu.sharif.ce.dml.common.util.io.output;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 18, 2006
 * Time: 1:11:36 PM
 * <br/> a class which services like buffer writer using {@link BufferWriter},
 * with a buffer that can be adjust to load and write with a threshold of buffer fullness,
 * it gives two writer TraceWriter and Transition Writer
 */
public class BufferOutputWriter extends Writer {

    public static final String separator = "\t";
    private WritingHandler writingHandler;
    private static Map writers = new HashMap();
    public static final DecimalFormat DECIMAL_FORMATER = new DecimalFormat("###.##");

    protected BufferOutputWriter(WritingHandler writingHandler) {
        this.writingHandler = writingHandler;
    }


    public WritingHandler getWritingHandler() {
        return writingHandler;
    }

    public static BufferOutputWriter createNewWriter(String name)
            throws DuplicateBufferCreation {
        return createNewWriter(name, new StreamTextWriter());
    }


    public static BufferOutputWriter createNewWriter(String name, WritingHandler writingHandler)
            throws DuplicateBufferCreation {
        BufferOutputWriter bufferOutputWriter = new BufferOutputWriter(writingHandler);
        if (writers.containsKey(name)) {
            throw new DuplicateBufferCreation((new StringBuilder()).append("Duplicate Buffer with the name: ").append(name).toString());
        } else {
            writers.put(name, bufferOutputWriter);
            return bufferOutputWriter;
        }
    }

    public static void resetWriters() {
        writers = new HashMap();
    }

    public static BufferOutputWriter createNewWriter(String name, String outputFileName)
            throws DuplicateBufferCreation {
        return createNewWriter(name, outputFileName, new StreamTextWriter());
    }

    public static BufferOutputWriter createNewWriter(String name, String outputFileName, WritingHandler writingHandler)
            throws DuplicateBufferCreation {
        BufferOutputWriter bufferOutputWriter = new BufferOutputWriter(writingHandler);
        if (writers.containsKey(name)) {
            throw new DuplicateBufferCreation((new StringBuilder()).append("Duplicate Buffer with the name: ").append(name).toString());
        } else {
            writers.put(name, bufferOutputWriter);
            ((BufferOutputWriter) writers.get(name)).setOutputString(outputFileName);
            return bufferOutputWriter;
        }
    }

    public static BufferOutputWriter createRandomWriter(String outputFileName, WritingHandler writingHandler) {
        try {
            return createNewWriter(Double.toString(Math.random()), outputFileName,writingHandler);
        } catch (DuplicateBufferCreation duplicateBufferCreation) {
            return createRandomWriter(outputFileName);
        }
    }

    public static BufferOutputWriter createRandomWriter(String outputFileName) {
        return createRandomWriter(outputFileName,new StreamTextWriter());
    }

    public static BufferOutputWriter createRandomWriter() {
        try {
            return createNewWriter(Double.toString(Math.random()));
        } catch (DuplicateBufferCreation duplicateBufferCreation) {
            return createRandomWriter();
        }
    }

    public static BufferOutputWriter getWriter(String name) {
        return (BufferOutputWriter) writers.get(name);
    }

    public void setOutputString(String outputString) {
        writingHandler.setOutputString(outputString);
    }

    public void write(char cbuf[], int off, int len)  throws IOException {
        write(new String(cbuf, off, len));
    }

    public void write(String data) {
        writingHandler.write(data);
    }

    public void flushAndClose() {
        writingHandler.flushAndClose();
    }

    public void flush() {
        writingHandler.flush();
    }

    public void close()
            throws IOException {
        flushAndClose();
    }

    public String getOutputString() {
        return writingHandler.getOutputString();
    }

}