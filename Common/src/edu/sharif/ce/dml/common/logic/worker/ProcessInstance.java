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

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 31, 2007
 * Time: 8:15:49 PM<br>
 * An entity object that represent an independent unit of work
 */
public class ProcessInstance {
    private String message;
    private ProcessResult result;
    private long durationMilSec;

    public String getMessage() {
        return message;
    }

    public ProcessResult getResult() {
        return result;
    }

    public long getDurationMilSec() {
        return durationMilSec;
    }

    public ProcessInstance(String message, ProcessResult result, long durationMilSec) {
        this.message = message;
        this.result = result;
        this.durationMilSec = durationMilSec;
    }

    /**
     * result of the work has been done.
     */
    public enum ProcessResult {
        success, failure
    }



}
