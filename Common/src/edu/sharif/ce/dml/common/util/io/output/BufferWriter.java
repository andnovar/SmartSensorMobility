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


import java.io.*;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 18, 2006
 * Time: 1:12:35 PM
 * <br/> an writer as a consumer in producer consumer pattern using a buffer
 */
class BufferWriter extends Thread {
    private String[] buffer;
    private int bufferSize = 1000;
    //syndiction part
    final BufferIndicator full = new BufferIndicator();
    final BufferIndicator empty = new BufferIndicator();
    final BufferIndicator mutex = new BufferIndicator();
    final BufferIndicator end = new BufferIndicator();
    boolean flush = false;
    boolean stop = false;
    File file;
    static int idd = 1;
    int id;


    /**
     * it crates a new object, and initiates buffer
     *
     * @param file
     */
    BufferWriter(File file) {
        this.file = file;
        buffer = new String[bufferSize];
        this.id = idd++;
    }

    /**
     * @return size of buffer of writer
     */
    int getBufferSize() {
        return bufferSize;
    }

    String[] getBuffer() {
        return buffer;
    }

    /**
     * writes buffer array to the file in consumer
     */
    public void run() {
        String data;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            try {
                while (!stop || full.getIndex() != empty.getIndex()) {
                    synchronized (full) {
                        if (full.getIndex() == empty.getIndex()) {
                            if (flush) {
                                writer.flush();
                                flush = false;
                            }
                            full.wait();
                        }
                    }
                    if (stop) {
                        break;
                    }
                    synchronized (mutex) {
                        data = buffer[empty.getIndex()];
                        empty.setIndex((empty.getIndex() + 1) % getBufferSize());
                    }
                    synchronized (empty) {
                        if ((full.getIndex() - empty.getIndex() < getBufferSize() / 3 &&
                                full.getIndex() > empty.getIndex()) ||
                                empty.getIndex() - full.getIndex() > 2 * getBufferSize() / 3) {
                            empty.notify();
                        }

                    }
                    writer.write(data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //it is on exit
            while (full.getIndex() != empty.getIndex()) {
                writer.write(buffer[empty.getIndex()]);
                empty.setIndex((empty.getIndex() + 1) % getBufferSize());
            }
            writer.close();
            synchronized (end) {
                end.setIndex(1);
                end.notify();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * a wrapper class which wrapes an integer value that can be changed without changes in reference
     */
    class BufferIndicator {
        private int index = 0;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }


        public String toString() {
            return Integer.toString(index);
        }
    }

}
