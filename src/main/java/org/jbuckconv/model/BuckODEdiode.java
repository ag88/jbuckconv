package org.jbuckconv.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jbuckconv.model.BuckODE.State;

public class BuckODEdiode extends BuckODE {

	Diode diode;
	
	public BuckODEdiode() {
		super();
		logger = LogManager.getLogger(BuckODEdiode.class);
		diode = new Diode();
	}

	@Override
	public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
		Marker marker = MarkerManager.getMarker("compute 2nd deriv");
		logger.debug(marker, "t : {}, y: {}, yDot: {}", t, y[0], yDot[0]);
		double vin = oVin.getVin(t);
				
		if(Math.abs(vin) < 1e-3 ) { // off state
			if (m_state == State.High) {
				//yDot[0] = -1.0 * yDot[0]; 
				//yDot[0] = 0.0;
				m_state = State.Low;
				logger.debug(marker, "state: {}", m_state.name());
			}
			// without diode
			//yDDot[0] = 1.0 / (L * C) * (- y[0] + L/R * yDot[0] );
			// with diode
			double id = C * yDot[0] + y[0] / R;
			logger.debug(marker, "Id: {}", id);
			if (id <= 0.0) {
				yDDot[0] = 0.0;
			}  else
				yDDot[0] = 1.0 / (L * C) * (- y[0] + L/R * yDot[0] + diode.Vd(id));
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
			yDDot[0] = 1.0 / (L * C) * (  vin - y[0] - L/R * yDot[0] );
			// using V_L = - L dI/dt
			//yDDot[0] = 1.0 / (L * C) * (  y[0] - vin - L/R * yDot[0] );
		}
		ydot = yDot[0];

		logger.debug(marker, "yDDot: {}", yDDot[0]);

	}
		
	@Override
	public double calcIL(double dotVout, double Vout) {
		double il =  super.calcIL(dotVout, Vout);
		return il < 0.0 ? 0.0 : il;
	}

}
