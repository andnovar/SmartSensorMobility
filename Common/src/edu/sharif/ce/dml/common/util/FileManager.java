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

package edu.sharif.ce.dml.common.util;

import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.ui.forms.FrameTemplate;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 26, 2007
 * Time: 11:04:40 PM<br/>
 * A singleton utility class to manage files. it uses a {@link edu.sharif.ce.dml.common.util.PublicConfig#CONFIG_FILE_NAME} to rememeber
 * last used files, and a map to find files (especially config files) easier.<br/> Note that it saves relative paths to the configfile if it can.
 * So config file should be in the Masoud of project.
 */
public class FileManager {
    /**
     * singleton instance
     */
    private static FileManager instance;
    /**
     * FileChooser component
     */
    private JFileChooser fileChooser;
    /**
     * project Dir that is configfile path.
     */
    private File projectDir = new File(new File(PublicConfig.CONFIG_FILE_NAME).getAbsolutePath().replaceAll(PublicConfig.CONFIG_FILE_NAME + "$", ""));

    /**
     * @return Singleton object
     */
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /**
     * @param path
     * @param title   used to find from properities file.
     * @param save    if the found file path save in config file
     * @param isInput
     * @return selected file.
     * @throws InvalidRequiredInputFileException if user not select the file. instead of returning null.
     */
    public File getFile(String path, String title, boolean save, FileFilter[] filters, boolean isInput) throws InvalidRequiredInputFileException {

        if (!isInput) {
            File f = new File(path);
            if (f.exists()) {
                if (f.isDirectory()) {
                    f.mkdirs();
                }else{
                    f.delete();
                }
            }
            return f;
        }
        File f = new File(path);
        if (f.exists()) {
            return f;
        }
        if (!title.equals(PublicConfig.CONFIG_FILE_KEY)) {
            String p = PublicConfig.getInstance().getConfigFilePropertyManager().readProperty(title);
            if (p != null) {
                f = new File(p);

                if (f.exists()) {
                    return f;
                }
            }
        }
        //show select message until user select the file or cancel this procedure
        while (true) {
            if (JOptionPane.showConfirmDialog(null, title + " file not found. Do you want to find it yourself?",
                    "File not found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                File[] files = showFileDialog(title, false, filters);
                if (files.length > 0) {
                    if (save) {
                        PublicConfig.getInstance().getConfigFilePropertyManager().addProperty(title, getRelativePath(projectDir, files[0].getAbsoluteFile()));
                        PublicConfig.getInstance().saveConfigFile();
                    }
                    return files[0];
                }
            } else {
                throw new InvalidRequiredInputFileException("No file has been selected!");
            }
        }
    }

    /**
     * @return user last selected file type
     */
    public FileFilter getlastFileFilter() {
        assert fileChooser != null;
        return fileChooser.getFileFilter();
    }

    /**
     * shows a file dialog and remembers/manages last used directory
     *
     * @param title
     * @param multiple
     * @param filter
     * @return
     */
    public File[] showFileDialog(String title, boolean multiple, FileFilter[] filter) {
        assert filter != null;
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose " + title +
                (filter.length > 0 && filter[0].equals(FileFilters.getDirectoryFilter()) ? " Directory" : " File"));
        for (FileFilter fileFilter : filter) {
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        if (filter.length == 1 && filter[0].equals(FileFilters.getDirectoryFilter())) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        try {
            String lastFolder = PublicConfig.getInstance().getLastFolderProperty();
            fileChooser.setCurrentDirectory(new File(lastFolder));
        } catch (Exception e) {

        }
        fileChooser.setMultiSelectionEnabled(multiple);
        if (fileChooser.showOpenDialog(FrameTemplate.getParentFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                PublicConfig.getInstance().setLastFolderProperty(fileChooser.getSelectedFile());
                PublicConfig.getInstance().saveConfigFile();
            } catch (Exception e) {
            }
            if (multiple) {
                return fileChooser.getSelectedFiles();
            } else {
                return new File[]{fileChooser.getSelectedFile()};
            }
        } else {
            return new File[0];
        }
    }

    /**
     * break a path down into individual elements and add to a list.
     * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
     *
     * @param f input file
     * @return a List collection with the individual elements of the path in
     *         reverse order
     */
    private static List getPathList(File f) {
        List l = new ArrayList();
        File r;
        try {
            r = f.getCanonicalFile();
            while (r != null) {
                l.add(r.getName());
                r = r.getParentFile();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            l = null;
        }
        return l;
    }

    /**
     * figure out a string representing the relative path of
     * 'f' with respect to 'r'
     *
     * @param r home path
     * @param f path of file
     */
    private static String matchPathLists(List r, List f) {
        int i;
        int j;
        String s;
        // start at the beginning of the lists
        // iterate while both lists are equal
        s = "";
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common Masoud
        while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
            i--;
            j--;
        }

        // for each remaining level in the home path, add a ..
        for (; i >= 0; i--) {
            s += ".." + File.separator;
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            s += f.get(j) + File.separator;
        }

        // file name
        s += f.get(j);
        return s;
    }

    /**
     * get relative path of File 'f' with respect to 'home' directory
     * example : home = /a/b/c
     * f    = /a/d/e/x.txt
     * s = getRelativePath(home,f) = ../../d/e/x.txt
     *
     * @param home base path, should be a directory, not a file, or it doesn't
     *             make sense
     * @param f    file to generate path for
     * @return path from home to f as a string
     */
    private static String getRelativePath(File home, File f) {
        File r;
        List homelist;
        List filelist;
        String s;

        homelist = getPathList(home);
        filelist = getPathList(f);
        s = matchPathLists(homelist, filelist);

        return s;
    }
}
