package edu.sharif.ce.dml.common.parameters.logic.parameterable.random.levy;

import jsc.distributions.AbstractDistribution;

import java.util.Random;
import java.util.RandomAccess;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 29, 2010
 * Time: 11:01:01 AM
 */
public class LevyDistribution extends AbstractDistribution {
    private StabRnd randomGen;
    double alpha,  beta, c, delta;

    public LevyDistribution(double alpha, double beta, double c, double delta) {
        this.alpha = alpha;
        this.beta = beta;
        this.c = c;
        this.delta = delta;
    }

    public void setSeed(long seed){
        Random rand = new Random(seed);
        randomGen = new StabRnd(alpha,beta,c,delta,rand.nextLong(),rand.nextLong());
    }

    @Override
    public double cdf(double v) {
        return 0;
    }

    @Override
    public double inverseCdf(double v) {
        return 0;
    }

    @Override
    public double mean() {
        return 0;
    }

    @Override
    public double pdf(double v) {
        return 0;
    }

    @Override
    public double variance() {
        return 0;
    }

    @Override
    public double random() {
        return randomGen.getNext();
    }
}
