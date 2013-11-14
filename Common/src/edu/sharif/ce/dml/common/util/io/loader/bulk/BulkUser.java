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

package edu.sharif.ce.dml.common.util.io.loader.bulk;

import edu.sharif.ce.dml.common.util.io.loader.User;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 7:44:04 PM
 * <br/> a <tt>User</tt> Object which uses edu.sharif.ce.dml.common.data as a whole with the aid of a {@link edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUsingHandler}
 * which has the same type as this class. note that this class depends on {@link edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoader} instead of
 * {@link edu.sharif.ce.dml.common.util.io.loader.FileLoader} and all its subclasses.
 */
public class BulkUser<S> extends User<S> {
    private BulkLoader<S> bulkLoader;
    private BulkUsingHandler<S> bulkUsingHandler;

    public BulkUser(BulkUsingHandler<S> bulkUsingHandler, BulkLoader<S> bulkLoader) {
        super(bulkLoader,bulkUsingHandler);
        this.bulkLoader = bulkLoader;
        this.bulkUsingHandler = bulkUsingHandler;
    }

    public BulkUser(BulkUsingHandler<S> bulkUsingHandler, File file, BulkLoadingHandler<S> loadingHandler) {
        this(bulkUsingHandler, new BulkLoader<S>(file,loadingHandler));
    }

    public void run() {
        super.run();
        if (bulkLoader.getData().length==0){
            bulkLoader.load();
        }
        bulkUsingHandler.use(bulkLoader.getData());
        endLoading();
    }

    /**
     * delegates stop event to the <tt>bulkUsingHandler</tt>
     */
    public void stopLoading() {
        bulkUsingHandler.stopLoading();
    }

    /**
     * delegates end event to the <tt>bulkUsingHandler</tt>
     */
    public void endLoading() {
        bulkUsingHandler.endLoading();
    }

    /**
     * delegates start event to the <tt>bulkUsingHandler</tt>
     */
    public void startLoading() {
        bulkUsingHandler.startLoading();
    }

    @Override
    public synchronized void start() {
        startLoading();
        super.start();
    }
}
