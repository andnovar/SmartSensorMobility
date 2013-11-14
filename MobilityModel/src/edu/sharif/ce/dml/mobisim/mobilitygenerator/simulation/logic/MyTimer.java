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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 7:08:30 PM
 * <br/> a class that stores current edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation time and has a timer to update
 * {@link Simulation} datas and view.
 */
public class MyTimer {
    private static long time;
    public static long calculationTimeStep = 1;
    public static long paintTimeStepRatio = 1;
    //todo
    public static int delay = 100;// PublicConfig.getInstance().getSpeed();
    public static final int NORMAL_DELAY = 1000;
    private static long turn = paintTimeStepRatio;
    private static Simulation simulation;
    /**
     * For graphic responsing it should be swing timer
     */
    private static Timer timer = new Timer(delay, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            MyTimer.step();
        }
    });

    static {
        timer.setInitialDelay(0);
        timer.setRepeats(true);
    }

    /**
     * @return current edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation time.
     */
    public static long getTime() {
        return time;
    }

    /**
     * increases the time by 1
     */
    public static void incTime() {
        time++;
    }

    /**
     * resets time to 0
     */
    public static void resetTime() {
        time = 0;
    }

    /**
     * if time &lt; {@link Simulation} maxSimulationTime, and configuration is stable
     * it runs updates the edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation structure and view.
     */
    private static void step() {
        if (simulation.getMaxSimulationTime() == 0 || time < simulation.getMaxSimulationTime()) {
            //this mutex and if implemented to stop simulation while simulation configuration is changing
            time++;
            if (--turn == 0) {
                simulation.updateNodes();
                turn = paintTimeStepRatio;
            }
            simulation.updateView(time);
        }
    }

    /**
     * stops the timer and resets the time to 0.
     */
    public static void reset() {
        timer.stop();
        resetTime();
    }

    public static void setSpeed(int speedRatio) {
        MyTimer.delay = NORMAL_DELAY / speedRatio;
    }

    /**
     * stops the timer
     */
    public static void pause() {
        timer.stop();
    }

    /**
     * sets simulation to newSimulation and runs timer
     *
     * @param newSimulation
     */
    public static void play(Simulation newSimulation) {
        play(newSimulation,MyTimer.delay);
    }

    public static void play(Simulation newSimulation, int speedRatio) {
        setSpeed(speedRatio);
        timer.setDelay(delay);
        simulation = newSimulation;
        timer.start();
    }


    /**
     * starts timer with last .simulation.
     */
    public static void play(int speedRatio) {
        play(simulation,speedRatio);
    }
}
