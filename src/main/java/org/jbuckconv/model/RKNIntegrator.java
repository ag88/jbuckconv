package org.jbuckconv.model;

import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;

public class RKNIntegrator {

	/*
	 * Runge-Kutta-Nyström integrator
	 */
	public RKNIntegrator() {
	}
	
	public class Result {
		
		public double y;
		public double ydot;
		
	}

	public Result singleStep(final SecondOrderDifferentialEquations equations, 
			final double t0, final double[] y0,	final double[] yDot0, double t) {

		double h = t - t0;

		final double[] y = y0.clone();
		final double[] yDot = yDot0.clone();
		/*
		final int stages = 4;
		final double[][] yDDotK = new double[stages][];
		for (int i = 0; i < stages; ++i) {
			yDDotK[i] = new double[y0.length];
		}
		*/
		final double[] yDDot = new double[1];
		//final double[] yTmp = y0.clone();

		equations.computeSecondDerivatives(t0, y, yDot, yDDot);		
		
		double k1 = yDDot[0];
		
		double ydot1 = yDot[0] + k1 * h / 2.0;
		yDot[0] = ydot1;
		
		double y1 = y0[0] + h / 2.0 * (yDot0[0] + ydot1) / 2.0;
		y[0] = y1;
		
		equations.computeSecondDerivatives(t0 + h/2, y, yDot, yDDot);
		
		double k2 = yDDot[0];
		
		double ydot2 = yDot[0] + k2 * h / 2.0;
		yDot[0] = ydot2;
		
		double y2 = y0[0] + h / 2.0 * (yDot0[0] + ydot2) / 2.0;
		y[0] = y2;
		
		equations.computeSecondDerivatives(t0 + h/2, y, yDot, yDDot);		
		
		double k3 = yDDot[0];
		
		double ydot3 = yDot[0] + k3 * h;
		yDot[0] = ydot3;
		
		double y3 = y0[0] + h * (yDot0[0] + ydot3) / 2.0;
		y[0] = y3;
		
		equations.computeSecondDerivatives(t0 + h, y, yDot, yDDot);		
		
		double k4 = yDDot[0];
			
		Result result = new Result();
		result.ydot = yDot[0] + h / 6.0 * (k1 + 2*k2 + 2*k3 + k4);
		result.y = y[0] + h / 6.0 * (ydot1 + 2*ydot2 + 2*ydot3 + result.ydot);

		return result;
	}
}
