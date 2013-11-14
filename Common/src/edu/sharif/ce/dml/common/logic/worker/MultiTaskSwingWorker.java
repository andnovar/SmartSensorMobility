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

package edu.sharif.ce.dml.common.logic.worker;

import edu.sharif.ce.dml.common.ui.forms.ProgressForm;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 31, 2007
 * Time: 7:51:56 PM<br>
 * represents a background task. A progress form will be shown to show how much of work has been done.
 */
public abstract class MultiTaskSwingWorker extends SwingWorker<Long, ProcessInstance> {
    private final ProgressForm progressForm;

    /**
     * @param noOfTasks to manage progress bar steps
     */
    protected MultiTaskSwingWorker(int noOfTasks) {
        progressForm = new ProgressForm(noOfTasks);
    }

    /**
     * @return time of operation in milliSec
     * @throws Exception
     */
    protected final Long doInBackground() throws Exception {
        Long initTime = System.currentTimeMillis();
        doWork();
        return System.currentTimeMillis() - initTime;
    }

    /**
     * main function of the class will be written in this method
     *
     * @throws Exception
     */
    protected abstract void doWork() throws Exception;

    /**
     * @param chunks list of messages
     */
    protected void process(List<ProcessInstance> chunks) {
        for (ProcessInstance chunk : chunks) {
            progressForm.progress(chunk);
        }
    }

    protected void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            JOptionPane.showMessageDialog(progressForm, "Task is done! It takes " + get() / 1000.0 + " seconds.");
        } catch (Exception ignore) {
            ignore.printStackTrace();
            DevelopmentLogger.logger.debug("error in worker: " + ignore.getMessage());
        }
    }


}