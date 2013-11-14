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


import edu.sharif.ce.dml.common.util.io.loader.FileLoader;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 24, 2007
 * Time: 5:43:38 AM
 * <br/> a loader object which loads file edu.sharif.ce.dml.common.data as a whole object.
 * It loads edu.sharif.ce.dml.common.data by deligating {@link Reader} object to {@link edu.sharif.ce.dml.common.util.io.loader.LoadingHandler}
 */
public class BulkLoader<E> extends FileLoader<E> {
    protected final List<E> buffer = new LinkedList<E>();

    /**
     * @param file
     * @param loadingHandler type should be similar to this object type.
     */
    public BulkLoader(File file, BulkLoadingHandler<E> loadingHandler) {
        super(loadingHandler, file);
    }

    /**
     * loads edu.sharif.ce.dml.common.data from the file.
     */
    public void load() {
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            buffer.addAll(((BulkLoadingHandler<E>)loadingHandler).bReadData(reader));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return an array of datas that have been loaded
     */
    public E[] getData() {
        return buffer.toArray(loadingHandler.getEArray(0));
    }
}
