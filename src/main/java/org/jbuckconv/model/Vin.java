package org.jbuckconv.model;

import java.text.NumberFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Vin {

	Logger logger = LogManager.getLogger(Vin.class);
	
	double vinc;
	double freq;
	double duty_cycle;
	boolean single = false;
	boolean pulse = true;
	
	public Vin() {
		freq=10000;
		duty_cycle=0.5;
		vinc=10.0;
		single = false;
		pulse = true;
	}		
	

	public Vin(boolean single) {
		freq=10000;
		duty_cycle=0.5;
		vinc=10.0;
		this.single = single;
		pulse = true;
	}
	
	public Vin(double freq, double duty_cycle, double vinc, boolean single) {
		this.freq = freq;
		this.duty_cycle = duty_cycle;
		this.vinc = vinc;
		this.single = single;
		pulse = true;
	}


	public double getVin(double t) {
		Marker marker = MarkerManager.getMarker("vin");
		double cycle = t * freq;
		double fcycle = (cycle - (int) cycle);
		double val = 0.0;
		if(fcycle <= duty_cycle && pulse) 
			val = vinc;
		else
			val = 0;
		if(fcycle > duty_cycle && pulse && single)
			pulse = false;
		
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

	public double getVinc() {
		return vinc;
	}

	public void setVinc(double vinc) {
		this.vinc = vinc;
	}


	public boolean isSingle() {
		return single;
	}


	public void setSingle(boolean single) {
		this.single = single;
		pulse = true;
	}


	public boolean isPulse() {
		return pulse;
	}


	public void setPulse(boolean pulse) {
		this.pulse = pulse;
	}

	
}
