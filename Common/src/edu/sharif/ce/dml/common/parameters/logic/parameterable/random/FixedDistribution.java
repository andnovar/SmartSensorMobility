package edu.sharif.ce.dml.common.parameters.logic.parameterable.random;

import jsc.distributions.AbstractDistribution;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 27, 2010
 * Time: 12:59:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixedDistribution extends AbstractDistribution{
    private double value;

    public FixedDistribution(double value) {
        this.value = value;
    }

    public FixedDistribution() {
        this(0);
    }

    @Override
    public double cdf(double v) {
        if (v<value) return 0;
        return 1;
    }

    @Override
    public double inverseCdf(double v) {
        return 0;
    }

    @Override
    public double mean() {
        return value;
    }

    @Override
    public double pdf(double v) {
        if (v==value){
            return 1;
        }
        return 0;
    }

    @Override
    public double random() {
        return value;
    }

    @Override
    public double variance() {
        return 0;
    }
}
