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

import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.ns.NSSimulationTrackLoadingHandler;
import edu.sharif.ce.dml.common.data.trace.ns.NSTraceWriter;
import edu.sharif.ce.dml.common.data.trace.plaintext.SimulationTrackLoadingHandler;
import edu.sharif.ce.dml.common.data.trace.plaintext.TextTraceWriter;
import edu.sharif.ce.dml.common.data.trace.xml.XMLSimulationTrackLoadingHandler;
import edu.sharif.ce.dml.common.data.trace.xml.XMLTraceWriter;
import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 28, 2007
 * Time: 10:38:00 PM
 */
public class FileFilters {
    private static final  PlainTraceFilter plainTraceFilter = new PlainTraceFilter();
    private static final XMLTraceFilter xmlTraceFilter = new XMLTraceFilter();
    private static final  NSTraceFilter nsTraceFilter = new NSTraceFilter();
    private static final TraceFilters traceFilters = new TraceFilters();
    private static final DirectoryFilter directoryFilter = new DirectoryFilter();
    private static final EvaluationFilter evaluationFilter = new EvaluationFilter();
    private static final DiagramFilter diagramFilter = new DiagramFilter();


    private static class PlainTraceFilter extends TraceFilter<SnapShotData> {

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().matches(".*\\.txt$");
        }

        public String getDescription() {
            return "Plain Trace File";
        }

        public Class<? extends LoadingHandler<SnapShotData>> getLoadingHandlerClass(File file) {
            if (accept(file)) {
                return (SimulationTrackLoadingHandler.class);
            } else
                return null;
        }

        public Class<? extends TraceWriter> getTraceWriterClass(File file) {
            if (accept(file)) {
                return (TextTraceWriter.class);
            } else
                return null;
        }
    }

    private static class NSTraceFilter extends TraceFilter<SnapShotData> {

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().matches(".*\\.nam$");
        }

        public String getDescription() {
            return "NS Trace File";
        }

        public Class<? extends LoadingHandler<SnapShotData>> getLoadingHandlerClass(File file) {
            if (accept(file)) {
                return (NSSimulationTrackLoadingHandler.class);
            } else
                return null;
        }

        public Class<? extends TraceWriter> getTraceWriterClass(File file) {
            if (accept(file)) {
                return (NSTraceWriter.class);
            } else
                return null;
        }
    }

    private static class XMLTraceFilter extends TraceFilter<SnapShotData> {

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().matches(".*\\.xml$");
        }

        public String getDescription() {
            return "XML Trace File";
        }

        public Class<? extends LoadingHandler<SnapShotData>> getLoadingHandlerClass(File file) {
            if (accept(file)) {
                return (XMLSimulationTrackLoadingHandler.class);
            } else
                return null;
        }

        public Class<? extends TraceWriter> getTraceWriterClass(File file) {
            if (accept(file)) {
                return (XMLTraceWriter.class);
            } else
                return null;
        }
    }


    private static class DiagramFilter extends FileFilter {
        public String getDescription() {
            return "Diagram File";
        }

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().matches(".*\\.xls$") ;
        }
    }

    public static TraceFilter<SnapShotData> getPlainTraceFilter() {
        return plainTraceFilter;
    }

    public static TraceFilter<SnapShotData> getTraceFilters() {
        return traceFilters;
    }

    public static FileFilter[] getAllTraceFilters() {
        return new FileFilter[]{plainTraceFilter, xmlTraceFilter,nsTraceFilter, traceFilters};
    }

    public static TraceFilter<SnapShotData> getXmlTraceFilter() {
        return xmlTraceFilter;
    }

    public static TraceFilter<SnapShotData> getNSTraceFilter(){
        return nsTraceFilter;
    }

    public static FileFilter getDirectoryFilter() {
        return directoryFilter;
    }

    public static EvaluationFilter getEvaluationFilter() {
        return evaluationFilter;
    }

    public static DiagramFilter getDiagramFilter() {
        return diagramFilter;
    }
}

