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

import edu.sharif.ce.dml.common.util.io.loader.User;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 12, 2007
 * Time: 8:13:07 PM
 * <br/>a <tt>User</tt> object which uses edu.sharif.ce.dml.common.data as a consumer and have producer/consumer relation with
 * {@link edu.sharif.ce.dml.common.util.io.loader.stream.StreamLoader} objects
 */
public class StreamUser<S> extends User<S> {
    private boolean stop = false;
    protected boolean end = false;
    /**
     * a shortcut for parent fileloader
     */
    protected StreamLoader<S> streamLoader;
    protected StreamUsingHandler<S> streamUsingHandler;


    public StreamUser(StreamUsingHandler<S> streamUsingHandler, StreamLoader<S> streamLoader) {
        super( streamLoader,streamUsingHandler);
        this.streamUsingHandler = streamUsingHandler;
        this.streamLoader = streamLoader;
    }

     public StreamUser(StreamUsingHandler<S> streamUsingHandler,File file, StreamLoadingHandler<S> loadingHandler) {
        this(streamUsingHandler, new StreamLoader<S>(file,loadingHandler));
    }

    public void run() {
        super.run();
        Thread loader = new Thread(streamLoader);
        loader.setDaemon(true);
        loader.start();
        try {
            while (!end && (!streamLoader.isEnd() || streamLoader.getFull().getIndex() != streamLoader.getEmpty().getIndex())) {
                if (stop) {
                    synchronized (this) {
//                        DevelopmentLogger.logger.debug("user is stopped");
                        wait();
//                        DevelopmentLogger.logger.debug("user awakend after stop");
                        if (end) {
                            break;
                        }
                    }
                }
                synchronized (streamLoader.getFull()) {
                    if (streamLoader.getFull().getIndex() == streamLoader.getEmpty().getIndex()) {
//                        DevelopmentLogger.logger.debug("user goes to wait, edu.sharif.ce.dml.common.data not exists");
                        streamLoader.getFull().wait();
//                        DevelopmentLogger.logger.debug("user oh! edu.sharif.ce.dml.common.data is comming");

                    }
                }
                synchronized (streamLoader.getMutex()) {
                    streamUsingHandler.loadData(streamLoader.getBuffer()[streamLoader.getEmpty().getIndex()]);
                    streamLoader.getEmpty().setIndex((streamLoader.getEmpty().getIndex() + 1) % streamLoader.getBufferSize());
                }
                synchronized (streamLoader.getEmpty()) {
                    if ((streamLoader.getFull().getIndex() - streamLoader.getEmpty().getIndex() < streamLoader.getBufferSize() / 3 &&
                            streamLoader.getFull().getIndex() > streamLoader.getEmpty().getIndex()) ||
                            streamLoader.getEmpty().getIndex() - streamLoader.getFull().getIndex() > 2 * streamLoader.getBufferSize() / 3) {
                        streamLoader.getEmpty().notify();
                    }

                }
                streamUsingHandler.useData();

            }
//            DevelopmentLogger.logger.debug("using is endded");
            endLoading();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        startLoading();
        super.start();
    }

    public void startLoading() {
        synchronized (this) {
            stop = false;
            notify();
            streamUsingHandler.startLoading();
        }
    }

    public void stopLoading() {
        stop = true;
        streamUsingHandler.stopLoading();
    }

    public void endLoading() {
        if (!end) {
            streamLoader.stopLoading();
            end = true;
            synchronized (this) {
                notify();
            }
            streamUsingHandler.endLoading();
        }
    }
}
