package org.jbuckconv.model;


import java.text.NumberFormat;

import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class BuckODE implements SecondOrderDifferentialEquations {
	
	Logger logger = LogManager.getLogger(BuckODE.class);
	
	double vin;
	double freq;
	double duty_cycle;
	double L;
	double C;
	double R;		
	
	public BuckODE() {
		freq = 10000;
		duty_cycle = 0.5;
		vin = 10.0;
		L = 100e-6;
		C = 100e-6;
		R = 1000;				
	}	

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
		double vin = Vin(t);
		
		yDDot[0] = 1.0 / (L * C) * (vin - y[0] - L/R * yDot[0] );
	}


	double Vin(double t) {
		Marker marker = MarkerManager.getMarker("vin");
		double cycle = t * freq;
		double fcycle = (cycle - (int) cycle);
		double val = 0.0;
		if(fcycle > duty_cycle) 
			val = 0;
		else
			val = vin;
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(5);		
		logger.debug(marker, "Vin: {}, t: {}, cycle: {}, fcycle: {}", val, f.format(t),
				f.format(cycle), f.format(fcycle));
		return val;
	}

	public double getFreq() {
		return freq;
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public double getDuty_cycle() {
		return duty_cycle;
	}

	public void setDuty_cycle(double duty_cycle) {
		this.duty_cycle = duty_cycle;
	}

	public double getVin() {
		return vin;
	}

	public void setVin(double vin) {
		this.vin = vin;
	}

	public double getL() {
		return L;
	}

	public void setL(double l) {
		L = l;
	}

	public double getC() {
		return C;
	}

	public void setC(double c) {
		C = c;
	}

	public double getR() {
		return R;
	}

	public void setR(double r) {
		R = r;
	}	
	
}
