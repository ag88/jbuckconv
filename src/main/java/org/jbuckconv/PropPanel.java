package org.jbuckconv;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.jbuckconv.model.BuckODE;
import org.jbuckconv.model.Compute;

public class PropPanel extends JPanel implements ActionListener {

	Logger logger = LogManager.getLogger(PropPanel.class);

	JTextField tfstep;
	JFormattedTextField tfn;
	JFormattedTextField tffreq;
	JFormattedTextField tfdutycycle;
	JFormattedTextField tfvin;
	JFormattedTextField tfL;
	JFormattedTextField tfC;
	JFormattedTextField tfR;

	JButton btnUpdate;

	public BuckODE ode;
	public Compute compute;

	public PropPanel(BuckODE ode, Compute compute) {
		this.ode = ode;
		this.compute = compute;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		creategui();
		updatevals();
	}

	private void creategui() {

				
		JLabel l1 = new JLabel("time step");
		l1.setToolTipText("integration time step");
		add(l1);
		tfstep = new JTextField(20);
		add(tfstep);

		add(new JLabel("number of steps"));
		Format format = new DecimalFormat("####"); 
		tfn = new JFormattedTextField(format);
		add(tfn);

		
		add(new JLabel("pwm freq"));
		format = new DecimalFormat("########.#"); 		
		tffreq = new JFormattedTextField(format);
		add(tffreq);

		add(new JLabel("duty cycle"));
		format = new DecimalFormat("###.##");
		tfdutycycle = new JFormattedTextField(format);
		add(tfdutycycle);

		add(new JLabel("Vin"));
		format = new DecimalFormat("#####.##");
		tfvin = new JFormattedTextField(format);
		add(tfvin);

		add(new JLabel("L uH"));
		format = new DecimalFormat("#####.#");
		tfL = new JFormattedTextField(format);
		add(tfL);

		add(new JLabel("C uF"));
		format = new DecimalFormat("#####.#");
		tfC = new JFormattedTextField(format);
		add(tfC);

		JLabel l3 = new JLabel("R");
		l3.setToolTipText("this is the load");
		add(l3);
		format = new DecimalFormat("#####.#");
		tfR = new JFormattedTextField(format);
		add(tfR);
		
		add(Box.createVerticalStrut(10));

		btnUpdate = new JButton("update");
		btnUpdate.setActionCommand("UPD");
		btnUpdate.addActionListener(this);
		add(btnUpdate);
		
		add(Box.createVerticalGlue());
		
		//getRootPane().setDefaultButton(btnUpdate);
		
	}

	private void updatevals() {
		tfstep.setText(Double.toString(compute.getStep()));
		tfn.setValue(compute.getN());

		tffreq.setValue(ode.getFreq());
		tfdutycycle.setValue(ode.getDuty_cycle() * 100.0);
		tfvin.setValue(ode.getVinc());
		tfL.setValue(ode.getL() * 1e6);
		tfC.setValue(ode.getC() * 1e6);
		tfR.setValue(ode.getR());

	}

	public void doupdate() {
		String efield = "";
		try {
			tfn.commitEdit();
			tffreq.commitEdit();			
			tfdutycycle.commitEdit();
			tfvin.commitEdit();
			tfL.commitEdit();
			tfC.commitEdit();
			tfR.commitEdit();
			
			efield = "step";
			compute.setStep(Double.parseDouble(tfstep.getText()));
			efield = "N";
			compute.setN(((Number)tfn.getValue()).intValue());
			efield = "freq";
			ode.setFreq(((Number)tffreq.getValue()).doubleValue());
			efield = "duty cycle";
			ode.setDuty_cycle(((Number)tfdutycycle.getValue()).doubleValue() / 100.0);
			efield = "vin";
			ode.setVinc(((Number)tfvin.getValue()).doubleValue());
			efield = "L";
			ode.setL(((Number)tfL.getValue()).doubleValue() * 1e-6);
			efield = "C";
			ode.setC(((Number)tfC.getValue()).doubleValue() * 1e-6);
			efield = "R";
			ode.setR(((Number)tfR.getValue()).doubleValue());

		} catch (ParseException e1) {
			logger.error(efield, e1);
		} catch (Exception e2) {
			logger.error(efield, e2);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("UPD")) {
			doupdate();
		}
	}
	
	public double getStep() {
		return Double.parseDouble(tfstep.getText());
	}
	
	public BuckODE getOde() {
		return ode;
	}

	public void setOde(BuckODE ode) {
		this.ode = ode;
		updatevals();
	}

	public Compute getCompute() {
		return compute;
	}

	public void setCompute(Compute compute) {
		this.compute = compute;
		tfstep.setText(Double.toString(compute.getStep()));
	}


}
