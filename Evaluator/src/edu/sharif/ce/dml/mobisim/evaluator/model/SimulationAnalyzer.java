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

package edu.sharif.ce.dml.mobisim.evaluator.model;

import edu.sharif.ce.dml.common.data.trace.Tracable;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 13, 2007
 * Time: 4:00:18 PM
 */
public class SimulationAnalyzer<E> implements Tracable {
    private List<Evaluator<E>> evaluators;

    public SimulationAnalyzer(List<Evaluator<E>> evaluators) {
        this.evaluators = evaluators;
    }

    public void evaluate(E snapShot) {
        for (Evaluator<E> evaluator : evaluators) {
            evaluator.runEvaluation(snapShot);
        }
    }

    public void reset() {
        for (Evaluator<E> evaluator : evaluators) {
            evaluator.reset();
        }
    }

    /*
       data for this specific object not internal objects
    */
    public List print() {
        List outputs = new LinkedList();
        for (Evaluator<E> evaluator : evaluators) {
            outputs.addAll(evaluator.print());
        }
        return outputs;
    }

    public List<String> getLabels() {
        List<String> labels = new LinkedList<String>();
        for (Evaluator<E> evaluator : evaluators) {
            labels.addAll(evaluator.getLabels());
        }
        return labels;
    }

    public void sort(int initialColumn, List<List> data) {
        Collections.sort(data, new EvaluationComparator(evaluators, initialColumn, 0.001));
    }

    public boolean isSorting() {
        boolean shouldSort = false;
        for (Evaluator<E> evaluator : evaluators) {
            if (evaluator.isSorting()) {
                shouldSort = true;
                break;
            }
        }
        return shouldSort;
    }

    public class EvaluationComparator implements Comparator<List> {
        private Map<Integer, List<Integer>> priorityColumnsMap;
        private List<Boolean> isDoubleSortableColumn;
        private double precision = 1;

        public EvaluationComparator(List<Evaluator<E>> evaluators, int initialColumn, double precision) {
            this.precision = precision;
            priorityColumnsMap = new TreeMap<Integer, List<Integer>>(new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return Math.abs(o1)-Math.abs(o2);
                }
            });
            isDoubleSortableColumn = new ArrayList<Boolean>();
            for (int i =0; i<initialColumn; i++){
                isDoubleSortableColumn.add(false);
            }
            int currentColumn = initialColumn;
            for (Evaluator<E> evaluator : evaluators) {

                List<Boolean> isDoubleSortable = evaluator.isDoubleSortable();
                if (evaluator.isSorting()) {
                    int priority = evaluator.getSortPriority();
                    List<Integer> columns = priorityColumnsMap.get(priority);
                    if (columns == null) {
                        columns = new LinkedList<Integer>();
                        priorityColumnsMap.put(priority, columns);
                    }
                    for (Boolean aBoolean : isDoubleSortable) {
                        columns.add(currentColumn);
                        isDoubleSortableColumn.add(aBoolean);
                        currentColumn++;
                    }
                } else {
                    for (Boolean anIsDoubleSortable : isDoubleSortable) {
                        isDoubleSortableColumn.add(false);
                    }
                    currentColumn += isDoubleSortable.size();
                }
            }
        }

        public int compare(List o1, List o2) {
            for (Integer priority : priorityColumnsMap.keySet()) {
                List<Integer> columns = priorityColumnsMap.get(priority);
                for (Integer column : columns) {
                    if (isDoubleSortableColumn.get(column)) {

                        int diff = (int) ((Double.parseDouble(o1.get(column).toString()) -
                                Double.parseDouble(o2.get(column).toString())) / precision);
                        if (diff != 0) {
                            if (priority<0) diff=-diff;
                            return diff;
                        }
                    } else {
                        int c = o1.get(column).toString().compareTo(o2.get(column).toString());
                        if (c != 0) {
                            if (priority<0) c=-c;
                            return c;
                        }
                    }
                }
            }
            return 0;
        }
    }
}
