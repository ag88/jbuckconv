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
	
	public Vin() {
		freq=10000;
		duty_cycle=0.5;
		vinc=10.0;
	}		
	
	public Vin(double freq, double duty_cycle, double vinc) {
		this.freq = freq;
		this.duty_cycle = duty_cycle;
		this.vinc = vinc;
	}


	public double getVin(double t) {
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
		
}
