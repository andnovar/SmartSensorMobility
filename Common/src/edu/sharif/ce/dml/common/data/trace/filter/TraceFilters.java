package edu.sharif.ce.dml.common.data.trace.filter;

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2010
 * Time: 6:41:47 PM
 */
public class TraceFilters extends TraceFilter<SnapShotData> {
    private final List<TraceFilter<SnapShotData>> filters;

    public TraceFilters() {
        filters= Arrays.asList(FileFilters.getPlainTraceFilter(), FileFilters.getXmlTraceFilter(),FileFilters.getNSTraceFilter());
    }

    public boolean accept(File f) {
        boolean returnValue = false;
        for (TraceFilter filter : filters) {
            returnValue = returnValue || filter.accept(f);
        }
        return returnValue;
    }

    public String getDescription() {
        return "Trace Files";
    }

    public Class<? extends LoadingHandler<SnapShotData>> getLoadingHandlerClass(File file) {
        return getLoadingHandler(filters, file);
    }

    public Class<? extends TraceWriter> getTraceWriterClass(File file) {
        return getTraceWriter(filters, file);
    }
}
