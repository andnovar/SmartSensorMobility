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

package edu.sharif.ce.dml.common.util.io.loader.stream;

import edu.sharif.ce.dml.common.util.io.loader.FileLoader;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 12, 2007
 * Time: 9:55:19 PM
 * <br/> A stream loader which uses a buffer of <tt>S</tt> objects. it plays the role of
 * producer in synchronization problem with a {@link edu.sharif.ce.dml.common.util.io.loader.stream.StreamUser} object.
 * The factor of full signal is 1/3. which means when 1/3 of buffer is full
 * it will signal the consumer. but do not wait for it until all buffer space is full.
 */
public class StreamLoader<S> extends FileLoader<S> implements Runnable {
    protected S[] buffer;
    public static final int DEFAULT_BUFFER_SIZE = 100;
    ///////////////syndiction part
    final BufferIndicator full = new BufferIndicator();
    final BufferIndicator empty = new BufferIndicator();
    final BufferIndicator mutex = new BufferIndicator();
    boolean end = false;
    protected boolean stop = false;

    ////////////////////////
    public StreamLoader(File file, StreamLoadingHandler<S> handler, int bufferSize) {
        super(handler, file);
        buffer = handler.getEArray(bufferSize);
    }

    public StreamLoader(File file, StreamLoadingHandler<S> handler) {
        this(file, handler, DEFAULT_BUFFER_SIZE);
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            reader.readLine();
            while (reader.ready() && !stop) {
                buffer[full.getIndex()] = ((StreamLoadingHandler<S>) loadingHandler).sReadData(reader);
                synchronized (empty) {
                    if ((full.getIndex() + 1) % getBufferSize() == empty.getIndex()) {
//                        DevelopmentLogger.logger.debug("loader: buffer is full. i wait");
                        empty.wait();
//                        DevelopmentLogger.logger.debug("loader: buffer has empty space i will work");
                    }
                }
                synchronized (mutex) {
                    full.setIndex((full.getIndex() + 1) % getBufferSize());
                }
                synchronized (full) {
                    if (full.getIndex() - empty.getIndex() > getBufferSize() / 3 ||
                            (empty.getIndex() - full.getIndex() < 2 * getBufferSize() / 3 &&
                                    empty.getIndex() > full.getIndex())) {
                        full.notify();
                    }
                }
            }
            end = true;
            synchronized (full) {
                full.notify();
            }
//            DevelopmentLogger.logger.debug("loading is endded");
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * subclasses can override this method to change buffer size handling method.
     * for example make its size dynamic
     *
     * @return
     */
    protected int getBufferSize() {
        return buffer.length;
    }

    /**
     * stops edu.sharif.ce.dml.common.util.io from loading.
     */
    public void stopLoading() {
//        DevelopmentLogger.logger.debug("loader stopped manually");
        stop = true;
    }

    /**
     * @return if the loading process endded
     */
    protected boolean isEnd() {
        return end;
    }

    protected BufferIndicator getFull() {
        return full;
    }

    protected BufferIndicator getEmpty() {
        return empty;
    }

    protected BufferIndicator getMutex() {
        return mutex;
    }

    /**
     * @return if loading process stopped temporariliy
     */
    protected boolean isStop() {
        return stop;
    }

    /**
     * @return buffer reference.
     */
    public S[] getBuffer() {
        return buffer;
    }

}
