package org.jbuckconv.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.math3.ode.FirstOrderConverter;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ComputeCommMath extends Compute {

	Logger logger = LogManager.getLogger(ComputeCommMath.class);
	
	FirstOrderConverter conv;
	ClassicalRungeKuttaIntegrator integ;

	public ComputeCommMath() {
		super();

		conv = new FirstOrderConverter(ode);
		integ = new ClassicalRungeKuttaIntegrator(step);
	}
	
	public ComputeCommMath(BuckODE ode) {
		super(ode);
		
		conv = new FirstOrderConverter(this.ode);
		integ = new ClassicalRungeKuttaIntegrator(step);
	}
	
	@Override
	public void docompute() {
		Marker marker = MarkerManager.getMarker("docompute");
		double t0=t;
		double[] y0 = new double[2];
		if (count > 0)
			y0[0] = Vout[count-1];
		else
			y0[0] = 0.0;
		logger.debug(marker, "N: {}", N);
		stop = t + step * N;
		count = 0;
		while(t<stop) {
			t = t + step;			
			double[] y1 = integ.singleStep(conv, t0, y0, t);			
			T[count] = t;
			Vout[count] = y1[0];
			dotVout[count] = ode.getYdot();
			if(ode instanceof BuckODEdiode) {
				Id[count] = ((BuckODEdiode) ode).getId();
				Vd[count] = ((BuckODEdiode) ode).getVd();
			} else if(ode instanceof BuckODEdiode2) {
				Id[count] = ((BuckODEdiode2) ode).getId();
				Vd[count] = ((BuckODEdiode2) ode).getVd();
			}
			
			NumberFormat f = NumberFormat.getInstance();
			f.setMaximumFractionDigits(5);
			logger.debug(marker, "t: {}, v: {}", f.format(t0), f.format(y1[0]));
			
			if(controller != null)
				controller.docontrol(ode.getoVin(), ode);
			
			t0 = t;
			y0[0] = y1[0];
			count++;			
		}				
	}
	
	@Override
	public void setStep(double step) {
		this.step = step;
		integ = new ClassicalRungeKuttaIntegrator(step);
	}

	
}
