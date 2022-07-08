package org.jbuckconv.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Diode {

	Logger logger = LogManager.getLogger(Diode.class);
	
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
