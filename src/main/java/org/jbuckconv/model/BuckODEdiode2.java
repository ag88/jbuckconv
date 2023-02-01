package org.jbuckconv.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jbuckconv.model.BuckODE.State;

public class BuckODEdiode2 extends BuckODE {

	Diode diode;
	
	double Il;
	
	double Id;
	double Vd;
	
	public BuckODEdiode2() {
		super();
		logger = LogManager.getLogger(BuckODEdiode2.class);
		
		diode = new Diode();
		Il = 0;
		Id = 0;
		Vd = 0;
	}

	@Override
	public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
		Marker marker = MarkerManager.getMarker("2nd deriv");
		logger.debug(marker, "t : {}, y: {}, yDot: {}", t, y[0], yDot[0]);
		double vin = oVin.getVin(t);
				
		if(Math.abs(vin) < 1e-3 ) { // off state
			if (m_state == State.High) {
				//yDot[0] = -1.0 * yDot[0]; 
				//yDot[0] = 0.0;
				//if (Math.abs(Il) > 1e-4) {
					//yDot[0] = (  Il - y[0] / R ) / C;					
				//}
				//y[0] = -y[0];
				logger.debug(marker, "yDot: {}", yDot[0]);
				m_state = State.Low;				
				logger.debug(marker, "state: {}", m_state.name());
			}
			// without diode
			//yDDot[0] = 1.0 / (L * C) * (- y[0] + L/R * yDot[0] );
			// with diode
			Id =  - C * yDot[0] - y[0] / R;
			Vd = 0.0;
			try {
				Vd = diode.Vd(Id);
			} catch (DiodeCurrException e) {
				Vd =  - y[0] - L/R * yDot[0];
			}
			logger.debug(marker, "Id: {}, Vd: {}", Id, Vd);
			//if (id <= 0.0) {
			//	yDDot[0] = 0.0;
			//}  else
				yDDot[0] = 1.0 / (L * C) * (- y[0] + Vd - L/R * yDot[0]);
			// inverted v for L
			//yDDot[0] = 1.0 / (L * C) * (y[0] - L/R * yDot[0] + diode.Vd( C * yDot[0] - y[0] / R));
			// inverted without diode
			//yDDot[0] = 1.0 / (L * C) * (y[0] - L/R * yDot[0] );
		} else { // on state
			if (m_state == State.Low) {	
				//yDot[0] = -1.0 * yDot[0];
				//yDot[0] = 0.0;
				m_state = State.High;
				logger.debug(marker, "state: {}", m_state.name());
			}
			Id = 0;
			Vd = 0;
			yDDot[0] = 1.0 / (L * C) * (  vin - y[0] - L/R * yDot[0] );
			// using V_L = - L dI/dt
			//yDDot[0] = 1.0 / (L * C) * (  y[0] - vin - L/R * yDot[0] );
		}
		ydot = yDot[0];
		Il = C * yDot[0] + y[0] / R;
		
		logger.debug(marker, "yDDot: {}, Il {}", yDDot[0], Il);

	}
		
	@Override
	public double calcIL(double dotVout, double Vout) {
		double il =  super.calcIL(dotVout, Vout);
		return il < 0.0 ? 0.0 : il;
	}

	public double getIl() {
		return Il;
	}

	public void setIl(double il) {
		Il = il;
	}

	public double getId() {
		return Id;
	}

	public void setId(double id) {
		Id = id;
	}

	public double getVd() {
		return Vd;
	}

	public void setVd(double vd) {
		Vd = vd;
	}
	
	

}
