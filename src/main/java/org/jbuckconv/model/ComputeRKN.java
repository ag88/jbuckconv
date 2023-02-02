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

public class ComputeRKN extends Compute {

	Logger logger = LogManager.getLogger(ComputeRKN.class);
	
	RKNIntegrator rkn;

	public ComputeRKN() {
		super();
		rkn = new RKNIntegrator();
	}
	
	public ComputeRKN(BuckODE ode) {
		super(ode);
		rkn = new RKNIntegrator();
	}

	@Override
	public void docompute() {
		Marker marker = MarkerManager.getMarker("docompute");
		double t0=t;
		double[] y0 = new double[2];
		double[] yDot0 = new double[2];
		if (count > 0)
			y0[0] = Vout[count-1];
		else
			y0[0] = 0.0;
		
		if (count > 0)
			yDot0[0] = dotVout[count-1];
		else
			yDot0[0] = 0.0;

		logger.debug(marker, "N: {}", N);
		stop = t + step * N;
		count = 0;
		while(t<stop) {
			t = t + step;
			
			RKNIntegrator.Result result = rkn.singleStep(ode, t0, y0, yDot0, t);			
			T[count] = t;
			Vout[count] = result.y;
			dotVout[count] = result.ydot;
			if(ode instanceof BuckODEdiode) {
				Id[count] = ((BuckODEdiode) ode).getId();
				Vd[count] = ((BuckODEdiode) ode).getVd();
			} else if(ode instanceof BuckODEdiode2) {
				Id[count] = ((BuckODEdiode2) ode).getId();
				Vd[count] = ((BuckODEdiode2) ode).getVd();
			}
						
			NumberFormat f = NumberFormat.getInstance();
			f.setMaximumFractionDigits(5);
			logger.debug(marker, "t: {}, v: {}", f.format(t0), f.format(result.y));
			
			if(controller != null)
				controller.docontrol(ode.getoVin(), ode);

			t0 = t;
			y0[0] = result.y;
			yDot0[0] = result.ydot;
			count++;			
		}				
	}
	
	
}
