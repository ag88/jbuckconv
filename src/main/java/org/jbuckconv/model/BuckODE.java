package org.jbuckconv.model;


import java.text.NumberFormat;

import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jbuckconv.model.BuckODE.State;


public class BuckODE implements SecondOrderDifferentialEquations {
	
	protected Logger logger = LogManager.getLogger(BuckODE.class);
	
	public double L;
	public double C;
	public double R;
	public double ydot;
	
	public Vin oVin;
	protected VinConf vinconf = null;
	
	public enum State {
		Low,
		High
	}
	
	public State m_state;
			
	public BuckODE() {
		oVin = new VinDefault();
		vinconf = (VinConf) oVin;
		L = 100e-6;
		C = 100e-6;
		R = 1000;
		ydot = 0.0;		
		m_state = State.Low;		
	}	

	@Override
	public int getDimension() {
		return 1;
	}
	
	@Override
	public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
		Marker marker = MarkerManager.getMarker("2nd deriv");
		logger.debug(marker, "t : {}, y: {}, yDot: {}", t, y[0], yDot[0]);
		double vin = oVin.getVin(t);

		if(Math.abs(vin) < 1e-3 ) { // off state
			if (m_state == State.High) {
				m_state = State.Low;				
				logger.debug(marker, "state: {}", m_state.name());
			}
		} else {
			if (m_state == State.Low) {
				m_state = State.High;				
				logger.debug(marker, "state: {}", m_state.name());
			}
		}
		
		yDDot[0] = 1.0 / (L * C) * (vin - y[0] - L/R * yDot[0] );
		
		ydot = yDot[0];
		
		double Il = C * yDot[0] + y[0] / R;

		logger.debug(marker, "yDDot: {}, Il: {}", yDDot[0], Il);
	}
	
	public double calcIL(double dotVout, double Vout) {
		 return getC() * dotVout + Vout / getR();
	}
	
	public double getFreq() {
		if (oVin instanceof VinDefault)
			return ((VinDefault) oVin).getFreq();
		else
			return 0.0;
	}

	public void setFreq(double freq) {
		if(vinconf != null)
			vinconf.setFreq(freq);
	}

	public double getDuty_cycle() {
		if(vinconf != null)
			return vinconf.getDuty_cycle();
		else
			return 0.0;
	}

	public void setDuty_cycle(double duty_cycle) {
		if(vinconf != null)
			vinconf.setDuty_cycle(duty_cycle);
	}

	public double getVinLevel() {
		if(vinconf != null)
			return vinconf.getVinLevel();
		else
			return 0.0;
	}

	public void setVinLevel(double vinc) {
		if(vinconf != null)
			vinconf.setVinLevel(vinc);
	}

	public Vin getoVin() {
		return oVin;
	}

	public void setoVin(Vin oVin) {
		this.oVin = oVin;
		if (oVin instanceof VinConf)
			vinconf = (VinConf) oVin;
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
