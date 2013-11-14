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

package edu.sharif.ce.dml.common.data.trace.filter;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 30, 2007
 * Time: 5:12:53 PM <br/>
 * accepts files that may contain any type of traces
 */
public abstract class TraceFilter<E> extends SingletonFileFilter {

    /**
     *
     * @param file
     * @return the loading handler that can read the file and extract its objects
     */

    public abstract Class<? extends LoadingHandler<E>> getLoadingHandlerClass(File file);

    /**
     * @param file
     * @return the trace writer which can write in the selected format
     */
    public abstract Class<? extends TraceWriter> getTraceWriterClass(File file);

    /**
     * uses responsibility chain pattern which iterates over filters list to get someone who can write on the selected file
     * @param filters
     * @param file
     * @return it may be null
     */
    public Class<? extends TraceWriter> getTraceWriter(List<TraceFilter<E>> filters, File file) {
        //responsibility chain
        Class<? extends TraceWriter> traceWriterClass = null;
        for (TraceFilter filter : filters) {
            traceWriterClass = filter.getTraceWriterClass(file);
            if (traceWriterClass!=null){
                return traceWriterClass;
            }
        }
        return traceWriterClass;
    }

    /**
     * uses responsibility chain pattern which iterates over filters list to get someone who can read the selected file
     * @param filters
     * @param file
     * @return
     */
    public Class<? extends LoadingHandler<E>> getLoadingHandler(List<TraceFilter<E>> filters, File file) {
        Class<? extends LoadingHandler<E>> loadingHandlerClass = null;
        for (TraceFilter filter : filters) {
            loadingHandlerClass = filter.getLoadingHandlerClass(file);
            if (loadingHandlerClass !=null){
                return loadingHandlerClass ;
            }
        }
        return loadingHandlerClass;
    }
}

