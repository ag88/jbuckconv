package org.jbuckconv.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Compute {

	Logger logger = LogManager.getLogger(Compute.class);
	
	final int N = 5000;	
	public double T[];
	public double V[];
	int count = 0;
	double step = 1e-5;
	double stop;
	double t, t0;
		
	BuckODE ode;
	FirstOrderConverter conv;
	ClassicalRungeKuttaIntegrator integ;

	public Compute() {
		init();
		
		ode = new BuckODE();
		conv = new FirstOrderConverter(ode);
		integ = new ClassicalRungeKuttaIntegrator(step);
	}
	
	public Compute(BuckODE ode) {
		init();
		
		this.ode = ode;
		conv = new FirstOrderConverter(this.ode);
		integ = new ClassicalRungeKuttaIntegrator(step);
	}
	
	private void init() {
		T = new double[N];
		V = new double[N];
		t = 0.0;
		t0 = 0.0;		
	}

	public BuckODE getODE() {
		return ode;
	}
	
	public void docompute() {
		Marker marker = MarkerManager.getMarker("docompute");
		double t0=t;
		double[] y0 = new double[10000];
		if (count > 0)
			y0[0] = V[count-1];
		else
			y0[0] = 0.0;
		stop = t + step * 1000;
		count = 0;
		while(t<stop) {
			t = t + step;
			double[] y1 = integ.singleStep(conv, t0, y0, t);			
			T[count] = t;
			V[count] = y1[0];
			NumberFormat f = NumberFormat.getInstance();
			f.setMaximumFractionDigits(5);
			logger.debug(marker, "t: {}, v: {}", f.format(t0), f.format(y1[0]));
			t0 = t;
			y0[0] = y1[0];
			count++;			
		}				
	}
	
	public XYSeriesCollection getDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Vout");                 

		for(int i=0; i<count; i++) {        
        	series1.add(T[i] , V[i]);
        }
        dataset.addSeries(series1);

		return dataset;
	}

	
	public JFreeChart getChart() {

		String title = "buck converter";
		String xlabel = "t";
		String ylabel = "v";
		
		XYSeriesCollection dataset = getDataset();
		
        JFreeChart chart = ChartFactory.createXYLineChart(
        		title, 
        		xlabel,	//xAxisLabel 
        		ylabel,	//yAxisLabel 
        		dataset, 		// data
        		PlotOrientation.VERTICAL, //orientation 
        		true,			// legend 
        		true,   		// tooltips
        		false); 		// urls
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);

        //Shape square = new Rectangle2D.Double(-5.0, -5.0, 7.0, 7.0);
        //Shape circle = new Ellipse2D.Double(-5,-5, 10, 10);
        //renderer.setSeriesShape(1, circle);
        
		return chart;
	}


	public double[] getT() {
		return T;
	}

	public void setT(double[] t) {
		this.T = t;
	}

	public double[] getV() {
		return V;
	}

	public void setV(double[] v) {
		this.V = v;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
		integ = new ClassicalRungeKuttaIntegrator(step);
	}
	
	
}
