package org.jbuckconv.model;


import java.text.NumberFormat;

import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class BuckODE implements SecondOrderDifferentialEquations {
	
	Logger logger = LogManager.getLogger(BuckODE.class);
	
	double vinc;
	double freq;
	double duty_cycle;
	double L;
	double C;
	double R;
	double ydot;
	
	enum State {
		Low,
		High
	}
	
	State m_state;
		
	Diode diode;
	
	public BuckODE() {
		freq = 10000;
		duty_cycle = 0.5;
		vinc = 10.0;
		L = 100e-6;
		C = 100e-6;
		R = 1000;
		ydot = 0.0;
		diode = new Diode();
		m_state = State.Low;		
	}	

	@Override
	public int getDimension() {
		return 1;
	}
	
	@Override
	public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
		Marker marker = MarkerManager.getMarker("compute 2nd deriv");
		logger.debug(marker, "t : {}, y: {}, yDot: {}", t, y[0], yDot[0]);
		double vin = Vin(t);

		yDDot[0] = 1.0 / (L * C) * (vin - y[0] - L/R * yDot[0] );
		
		ydot = yDot[0];

		logger.debug(marker, "yDDot: {}", yDDot[0]);
	}


	public double Vin(double t) {
		Marker marker = MarkerManager.getMarker("vin");
		double cycle = t * freq;
		double fcycle = (cycle - (int) cycle);
		double val = 0.0;
		if(fcycle > duty_cycle) 
			val = 0;
		else
			val = vinc;
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(5);		
		logger.debug(marker, "Vin: {}, t: {}, cycle: {}, fcycle: {}", val, f.format(t),
				f.format(cycle), f.format(fcycle));
		return val;
	}

	class Diode {	
		
		// 1N5822
		
		double VTemp;
		final double Nn = 1.0281; // ideality factor
		final double Is = 2.5069e-6; // reverse bias sat current

		public Diode() {
			VTemp = VT(300); //VTemp at 300 K ~ 25mv
		}
		
		public Diode(double temp) {
			VTemp = VT(temp);
		}
		
		public double Id(double Vd) {
			
			Marker marker = MarkerManager.getMarker("Id");
			double I = Is * ( Math.exp(Vd / ( Nn * VTemp)) - 1.0);
			logger.debug(marker, "Id : {}",I);
			return I;
		}
		
		public double Vd(double Id) {
			double Vd;
			Marker marker = MarkerManager.getMarker("Vd");
			
			if (Id < 0.0)
				Vd = 0.0;
			else 
				Vd= Nn * VTemp * Math.log(Id / Is + 1.0);
			logger.debug(marker, "Vd : {}", Vd);
			return Vd;
		}
				
		/**
		 * @param temp Temperature in K
		 * @return
		 */
		public double VT(double temp) {
			Marker marker = MarkerManager.getMarker("Vtemp");
			// Boltzmann constant
			final double k = 1.380649e-23;
			// electron charge
			final double q = 1.602176634e-19;
			double vtemp = k*temp/q; 
			logger.debug(marker, "Vtemp: {}", vtemp);		
			return vtemp;		
		}
		
		public void setVTemp(double temp) {
			VTemp = VT(temp);
		}
		
		public double getVTemp() {
			return VTemp;
		}
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

	public double getVinc() {
		return vinc;
	}

	public void setVinc(double vinc) {
		this.vinc = vinc;
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

	public double getYdot() {
		return ydot;
	}

	public void setYdot(double ydot) {
		this.ydot = ydot;
	}
	
}
