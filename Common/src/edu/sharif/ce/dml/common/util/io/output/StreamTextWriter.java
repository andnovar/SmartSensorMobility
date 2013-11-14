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

import java.io.File;

// Referenced classes of package edu.sharif.ce.dml.common.util.io.output:
//            BufferWriter, WritingHandler, OutputLogger

public class StreamTextWriter implements WritingHandler {

    private BufferWriter bufferWriter;
    private String outputFileName;

    public StreamTextWriter() {
    }

    public void write(String s) {
        bufferWriter.getBuffer()[bufferWriter.full.getIndex()] = s;
        try {
            synchronized (bufferWriter.empty) {
                if ((bufferWriter.full.getIndex() + 1) % bufferWriter.getBufferSize() == bufferWriter.empty.getIndex()) {
                    bufferWriter.empty.wait();
                }
            }
            synchronized (bufferWriter.mutex) {
                bufferWriter.full.setIndex((bufferWriter.full.getIndex() + 1) % bufferWriter.getBufferSize());
            }
            synchronized (bufferWriter.full) {
                if (bufferWriter.full.getIndex() - bufferWriter.empty.getIndex() > bufferWriter.getBufferSize() / 3 ||
                        bufferWriter.empty.getIndex() - bufferWriter.full.getIndex() < (2 * bufferWriter.getBufferSize()) / 3 && bufferWriter.empty.getIndex() > bufferWriter.full.getIndex()) {
                    bufferWriter.full.notify();
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        synchronized (bufferWriter.full) {
            if (bufferWriter.full.getIndex() != bufferWriter.empty.getIndex()) {
                bufferWriter.full.notify();
                bufferWriter.flush = true;
            }
        }
    }

    public void flushAndClose() {
        if (bufferWriter != null) {
            bufferWriter.stop = true;
            synchronized (bufferWriter.full) {
                bufferWriter.full.notify();
            }
            synchronized (bufferWriter.end) {
                if (bufferWriter.end.getIndex() == 0) {
                    bufferWriter.end.setIndex(1);
                    try {
                        bufferWriter.end.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.err.println("No file Selected to write");
        }
    }

    public void setOutputString(String outputFileName) {
        if (outputFileName == null) {
            this.outputFileName = OutputLogger.generateOutputFileName(this.outputFileName);
        } else {
            this.outputFileName = outputFileName;
        }
        File f = new File(this.outputFileName);
        if (f.exists()) {
            f.delete();
        }
        //create necessary directories
        {
            File dir = new File(f.getPath().substring(0,f.getPath().indexOf(f.getName())));
            dir.mkdirs();
        }
        if (bufferWriter != null) {
            flushAndClose();
        }
        bufferWriter = new BufferWriter(f);
        bufferWriter.start();
    }

    public String getOutputString() {
        return outputFileName;
    }

}
