package org.jbuckconv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jbuckconv.model.BuckODE;
import org.jbuckconv.model.BuckODEdiode;
import org.jbuckconv.model.Compute;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;


public class MainFrame extends JFrame implements ActionListener, ItemListener {
	
	Logger logger = LogManager.getLogger(MainFrame.class);
	
	JLabel mlMsg;
	ChartPanel chartpanel;
	JFreeChart chart;
	PropPanel proppanel;
	BuckODE ode;
	Compute compute;
	ButtonGroup gPlot;
	ButtonGroup gModel;

	public MainFrame() {
		super();
		setTitle("buck converter simulator");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		compute = new Compute();
		ode = compute.getODE();
		createGui();
	}
	
	private void createGui() {
		setPreferredSize(new Dimension(1280, 800));		
		
		JMenuBar menubar = new JMenuBar();
		JMenu mFile = new JMenu("File");
		mFile.add(addmenuitem("Snapshot", "SNAP", KeyEvent.VK_N));
		mFile.add(addmenuitem("About", "ABOUT", KeyEvent.VK_A));
		mFile.add(addmenuitem("Close", "CLOSE", KeyEvent.VK_C));
		menubar.add(mFile);
		
		JMenu mCompute = new JMenu("Compute");
		mCompute.add(addmenuitem("Compute", "CALC", KeyEvent.VK_C));
		mCompute.add(addmenuitem("Next", "NCAL", KeyEvent.VK_N));
		mCompute.addSeparator();
		gModel = new ButtonGroup();
		JRadioButtonMenuItem mrbBasic = new JRadioButtonMenuItem("BuckODE - no diode", true);
		mrbBasic.setMnemonic(KeyEvent.VK_N);
		mrbBasic.setActionCommand("MBAS");
		mrbBasic.addActionListener(this);
		gModel.add(mrbBasic);
		mCompute.add(mrbBasic);
		JRadioButtonMenuItem mrbDiode = new JRadioButtonMenuItem("BuckODE - with diode", false);
		mrbDiode.setMnemonic(KeyEvent.VK_D);
		mrbDiode.setActionCommand("MDIOD");
		mrbDiode.addActionListener(this);
		gModel.add(mrbDiode);
		mCompute.add(mrbDiode);		
		menubar.add(mCompute);
		
		JMenu mPlot = new JMenu("Plot");
		gPlot = new ButtonGroup();
		JRadioButtonMenuItem mrbVout = new JRadioButtonMenuItem("Vout", true);
		mrbVout.setMnemonic(KeyEvent.VK_V);
		mrbVout.setActionCommand("VOUT");
		gPlot.add(mrbVout);
		mPlot.add(mrbVout);
		JRadioButtonMenuItem mrbComb = new JRadioButtonMenuItem("Components");
		mrbComb.setMnemonic(KeyEvent.VK_C);
		mrbComb.setActionCommand("COMB");
		gPlot.add(mrbComb);
		mPlot.add(mrbComb);
		menubar.add(mPlot);		

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
		JMenu mDebug = new JMenu("Debug");
		boolean b = config.getLoggerConfig("org.jbuckconv.model.Compute").getLevel() == Level.DEBUG ? true : false;
		JCheckBoxMenuItem mdcompute = new JCheckBoxMenuItem("Compute", b);
		mdcompute.addItemListener(this);
		mDebug.add(mdcompute);
		
		b = config.getLoggerConfig("org.jbuckconv.model.BuckODE").getLevel() == Level.DEBUG ? true : false;
		JCheckBoxMenuItem mdode = new JCheckBoxMenuItem("BuckODE", b);
		mdode.addItemListener(this);
		mDebug.add(mdode);
		
		menubar.add(mDebug);
		
		setJMenuBar(menubar);
		
		JToolBar toolbar = new JToolBar();
		
		toolbar.add(makeNavigationButton("Play24.gif", "CALC", "compute", "compute"));
		toolbar.add(makeNavigationButton("StepForward24.gif", "NCAL", "next", "next"));
		toolbar.add(makeNavigationButton("camera.png", "SNAP", "Snapshot", "Snapshot"));
						
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(toolbar,BorderLayout.NORTH);
		
		chart = ChartFactory.createXYLineChart(
        		"buck converter", 
        		"t",	//xAxisLabel 
        		"v",	//yAxisLabel 
        		null, 		// data
        		PlotOrientation.VERTICAL, //orientation 
        		true,			// legend 
        		true,   		// tooltips
        		false); 		// urls
		
		
		chartpanel = new ChartPanel(chart);
		
		getContentPane().add(chartpanel,BorderLayout.CENTER);
		chartpanel.setDefaultDirectoryForSaveAs(new File(App.getInstance().getPrevDir()));
		
		proppanel = new PropPanel(ode, compute);
		getContentPane().add(proppanel,BorderLayout.EAST);
		
		mlMsg = new JLabel();
		getContentPane().add(mlMsg, BorderLayout.SOUTH);
		
	}

	
	protected JButton makeNavigationButton(String imageName, String actionCommand,
			String toolTipText, String altText) {

		//Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		setIcon(button, imageName, altText);

		return button;
	}
	
	protected void setIcon(JButton button, String imageName, String altText) {
		//Look for the image.
		String imgLocation = "/icons/" + imageName;
		URL imageURL = App.class.getResource(imgLocation);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}		
	}

	
	protected JMenuItem addmenuitem(String label, String cmd, int keyevent) {
		JMenuItem item = new JMenuItem(label);
		item.setMnemonic(keyevent);
		item.setActionCommand(cmd);
		item.addActionListener(this);
		return item;		
	}
	
	public File savedialog() {
		String prevdir = App.getInstance().getPrevDir();
		JFileChooser chooser = new JFileChooser(prevdir);
		int ret = chooser.showSaveDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			File dir = file.getParentFile();
			if (dir != null && dir.isDirectory())
				App.getInstance().setPrevDir(dir.getAbsolutePath());
			return file;
		} else
			return null;
	}

	public void dosnapshot() {
		File file = savedialog();
		if (file == null)
			return;
        try {
			ChartUtils.saveChartAsPNG(file, this.chart,
			        chartpanel.getWidth(), chartpanel.getHeight());
		} catch (IOException e) {
			logger.error("dosnapshot",e);
		}
	}

	
	private void docompute() {
		int n = compute.getN();
		compute = new Compute(ode);
		compute.setN(n);
		if(proppanel.getStep() != 0.0)
			compute.setStep(proppanel.getStep());		
		compute.docompute();
		if (gPlot.getSelection().getActionCommand().equals("COMB"))			
			chart = compute.getCombinedChart();
		else
			chart = compute.getChart();			
		chartpanel.setChart(chart);
		proppanel.setCompute(compute);
		
	}
	
	private void docompnext() {	
		if(compute == null) return;
		compute.docompute();
		if (gPlot.getSelection().getActionCommand().equals("COMB"))			
			chart = compute.getCombinedChart();
		else
			chart = compute.getChart();			
		chartpanel.setChart(chart);			
	}

	private void changeode(BuckODE newode) {
		//BuckODE oldode = this.ode;
		this.ode = newode;
		proppanel.setOde(this.ode);
		proppanel.doupdate();		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand(); 
		if(cmd.equals("CALC")) {
			docompute();
		} else if (cmd.equals("NCAL")) {
			docompnext();
		} else if (cmd.equals("SNAP")) {
			dosnapshot();
		} else if (cmd.equals("MBAS")) {			
			if (!(ode instanceof BuckODEdiode)) return;
			logger.debug("ODE without diode selected");
			BuckODE newode = new BuckODE();
			changeode(newode);
		} else if (cmd.equals("MDIOD")) {			
			if (ode instanceof BuckODEdiode) return;
			logger.debug("ODE with diode selected");
			BuckODE newode = new BuckODEdiode();
			changeode(newode);
		} else if (cmd.equals("ABOUT")) {
			About a =  new About();
			a.setLocationRelativeTo(this);
			a.pack();
			a.setVisible(true);
		} else if (cmd.equals("CLOSE")) {
			dispose();
		}		
	}



	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBoxMenuItem m = (JCheckBoxMenuItem) e.getSource();
		Level level = m.isSelected() ? Level.DEBUG : Level.INFO;
		updatelogger("org.jbuckconv.model." + m.getText(), level);		
		
	}

	
	private void updatelogger(String loggername, Level level) {
		// org.jbuckconv.model.Compute
		// org.jbuckconv.model.BuckODE
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Map<String,LoggerConfig> loggers = config.getLoggers();		
		LoggerConfig l = loggers.get(loggername);
		if (l != null) l.setLevel(level);
		logger.info("{}: {}", loggername, l==null?null:l.getLevel());
		ctx.updateLoggers();
	}


	private static final long serialVersionUID = 3354572268511291816L;


}
