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

public class Compute {

	Logger logger = LogManager.getLogger(Compute.class);
	
	final int MAXN = 2000;	
	public double T[];
	public double Vout[];
	public double dotVout[];	
	
	int N=1000;
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
		//ode = new BuckODEdiode();
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
		T = new double[MAXN];
		Vout = new double[MAXN];
		dotVout = new double[MAXN];
		t = 0.0;
		t0 = 0.0;		
	}

	public BuckODE getODE() {
		return ode;
	}
	
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
			
			NumberFormat f = NumberFormat.getInstance();
			f.setMaximumFractionDigits(5);
			logger.debug(marker, "t: {}, v: {}", f.format(t0), f.format(y1[0]));
			t0 = t;
			y0[0] = y1[0];
			count++;			
		}				
	}
	
	public XYSeriesCollection getVoutDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Vout");                 

		for(int i=0; i<count; i++) {        
        	series1.add(T[i] , Vout[i]);
        }
        dataset.addSeries(series1);

		return dataset;
	}

	public XYSeriesCollection getILDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("I_L");                 

		for(int i=0; i<count; i++) {
			double il = ode.calcIL(dotVout[i], Vout[i]);
        	series1.add(T[i] , il);
        }
        dataset.addSeries(series1);
        

		return dataset;
	}
	
	private XYDataset getyDotDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("dVout/dt");                 

		for(int i=0; i<count; i++) { 
        	series1.add(T[i] , dotVout[i]);
        }
        dataset.addSeries(series1);
		return dataset;
	}

	
	public XYSeriesCollection getVinDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Vin");                 

		Vin ovin = ode.getoVin();
		
		for(int i=0; i<count; i++) {        
        	series1.add(T[i] , ovin.getVin(T[i]));
        }
        dataset.addSeries(series1);

		return dataset;
	}
	
	public JFreeChart getChart() {

		String title = "buck converter";
		String xlabel = "t";
		String ylabel = "v";
		
		XYSeriesCollection dataset = getVoutDataset();
		
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
		
	XYPlot createSubPlot(XYDataset dataset, String label, Color c) {        
        final XYItemRenderer renderer = new StandardXYItemRenderer();
        if (c != null)
        	renderer.setSeriesPaint(0, c);
        final NumberAxis rangeAxis = new NumberAxis(label);
        rangeAxis.setAutoRangeIncludesZero(false);
        final XYPlot subplot = new XYPlot(dataset, null, rangeAxis, renderer);
        subplot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        
        //final XYTextAnnotation annotation = new XYTextAnnotation("Hello!", 50.0, 10000.0);
        //annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
        //annotation.setRotationAngle(Math.PI / 4.0);
        //subplot1.addAnnotation(annotation);

        return subplot;
	}

    public JFreeChart getCombinedChart() {

    	final NumberAxis domainaxis = new NumberAxis("t");
    	domainaxis.setAutoRange(true);
    	domainaxis.setAutoRangeIncludesZero(false);
    	//domainaxis.setDefaultAutoRange(new Range(T[0],T[N-1]));

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainaxis);
        plot.setGap(10.0);
        
        // create subplot 1...
        final XYDataset data1 = getVoutDataset();
        final XYPlot subplot1 = createSubPlot(data1, "Vout", null);
        // add the subplots...
        plot.add(subplot1, 1);            	                
                
        // create subplot 2...
        final XYDataset data2 = getVinDataset();
        final XYPlot subplot2 = createSubPlot(data2, "Vin", null);
        plot.add(subplot2, 1);

        // create subplot 3...
        final XYDataset data3 = getILDataset();
        final XYPlot subplot3 = createSubPlot(data3, "I_L", Color.GRAY);
        plot.add(subplot3, 1);
        
        
        final XYDataset data4 = getyDotDataset();        
        final XYPlot subplot4 = createSubPlot(data4, "dVout/dt", Color.GRAY);
        plot.add(subplot4, 1);

        plot.setOrientation(PlotOrientation.VERTICAL);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("components",
                              JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    }

	public double[] getT() {
		return T;
	}

	public void setT(double[] t) {
		this.T = t;
	}

	public double[] getV() {
		return Vout;
	}

	public void setV(double[] v) {
		this.Vout = v;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
		integ = new ClassicalRungeKuttaIntegrator(step);
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		if (n > MAXN)
			n = MAXN;
		N = n;
	}
	
	
}
