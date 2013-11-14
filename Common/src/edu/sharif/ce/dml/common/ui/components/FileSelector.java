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

package edu.sharif.ce.dml.common.ui.components;

import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.util.FileManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 26, 2007
 * Time: 10:59:20 PM<br/>
 * UI component for selecting a file
 */
public class FileSelector extends Box {
    /**
     * descriptive name label
     */
    private JLabel nameLbl;
    /**
     * shows user selected file name as the state of this object
     */
    private JLabel stateLbl;
    private JButton browseBtn;
    private boolean multipleFile = false;
    private String name2;
    /**
     * selected file(s)
     */
    private File[] selectedFiles = new File[0];
    /**
     * selected file filter
     */
    private FileFilter fileFilter;

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        browseBtn.setEnabled(enabled);
    }

    private boolean isDirectory = false;

    private FileFilter[] filters = new FileFilter[0];

    /**
     * updates when user selects the file. can be Used to update window's size.
     */
    private FileObservable fileObservable = new FileObservable();

    /**
     * state text when no file has been selected.
     */
    public static final String NOT_SELECTED = "No file selected";
    /**
     * string used with selected files as component state.
     */
    public static final String ONE_SELECTED = " Selected";

    /**
     * state text when multiple file has been selected.
     */
    public static final String MULTI_SELECTED = "MultiFile selected";

    public FileSelector(boolean multipleFliesParam, final String name, FileFilter[] filters2) {
        super(BoxLayout.X_AXIS);
        this.multipleFile = multipleFliesParam;
        this.name2 = name;
        this.filters = filters2;
        nameLbl = new JLabel();
        updateLabel();
        add(nameLbl);
        add(Box.createHorizontalStrut(10));
        stateLbl = new JLabel(NOT_SELECTED);
        add(stateLbl);
        browseBtn = new JButton("Browse");
        add(Box.createHorizontalStrut(10));
        add(Box.createHorizontalGlue());
        add(browseBtn);
        browseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedFiles = FileManager.getInstance().showFileDialog(name, multipleFile, filters);
                if (selectedFiles.length > 0) {
                    fileFilter = FileManager.getInstance().getlastFileFilter();
                    fileObservable.setChanged();
                    fileObservable.notifyObservers(selectedFiles);
                    if (selectedFiles.length == 1) {
                        stateLbl.setText(selectedFiles[0].getName() + ONE_SELECTED);
                    } else {
                        stateLbl.setText(MULTI_SELECTED);
                    }
                    ((Window) getTopLevelAncestor()).pack();
                }
            }
        });
    }

    public void setMultipleFile(boolean multipleFile) {
        this.multipleFile = multipleFile;
        updateLabel();
    }

    private void updateLabel() {
        nameLbl.setText(name2 + (isDirectory? " directory": "")+ (multipleFile ? "(s)" : "") + ":");
    }

    /**
     * updates the <tt>o</tt> Observer when user selects some files.
     *
     * @param o
     */
    public void addObserver(Observer o) {
        fileObservable.addObserver(o);
    }

    public void setFilters(FileFilter[] filters) {
        if (filters.length > 0) {
            this.filters = filters;

        }
    }

    private void checkDirectory() {
        isDirectory = filters[0] == FileFilters.getDirectoryFilter();
    }

    /**
     * sets default selected files.
     *
     * @param files  selected files
     * @param filter files filter
     */
    public void setSelectedFiles(File[] files, FileFilter filter) {
        selectedFiles = files;
        if (selectedFiles.length > 0) {
            fileFilter = filter;
            if (selectedFiles.length == 1) {
                stateLbl.setText(selectedFiles[0].getName() + ONE_SELECTED);
            } else {
                stateLbl.setText(MULTI_SELECTED);
            }
            if (getTopLevelAncestor() != null) {
                ((Window) getTopLevelAncestor()).pack();
            }
        }else {
            stateLbl.setText(NOT_SELECTED);
        }
    }

    public void reset() {
        setSelectedFiles(new File[0], fileFilter);
    }

    /**
     *
     * @return its size would be zero if the user has not selected any file
     */
    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    public FileFilter getSelectedFileFilter() {
        return fileFilter;
    }

    public boolean isFileSelected() {
        return getSelectedFiles().length > 0;
    }

    private class FileObservable extends Observable {
        protected synchronized void setChanged() {
            super.setChanged();
        }
    }
}
