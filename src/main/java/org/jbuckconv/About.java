package org.jbuckconv;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class About extends JFrame implements ActionListener {

	Logger logger = LogManager.getLogger(About.class);

	URI url, url2;

	public About() throws HeadlessException {
		super("About");

		try {
			url = new URI("https://github.com/ag88/jbuckconv");
		} catch (URISyntaxException e) {
			url = null;
		}
		try {
			url2 = new URI("https://donorbox.org/jbuckconv");
		} catch (URISyntaxException e) {
			url2 = null;
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(400, 200));
		creategui();

	}

	private void creategui() {

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		add(Box.createVerticalStrut(10));
		add(new JLabel("A little buck converter simulator"));
		add(new JLabel("brought to you by Andrew Goh"));
		add(Box.createVerticalStrut(10));
		add(new JLabel("github repository:"));
		
		JButton button = new JButton();
		button.setText(
				"<HTML><FONT color=\"#000099\"><U>" + url.toString()  +"</U></FONT></HTML>");
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setBackground(Color.WHITE);
		button.setToolTipText(url.toString());
		button.addActionListener(this);
		button.setActionCommand("U1");
		add(button);
		
		add(Box.createVerticalStrut(10));
		add(new JLabel("support my efforts:"));
		JButton link2 = new JButton();
		link2.setText(
				"<HTML><FONT color=\"#000099\"><U>" + url2.toString()  +"</U></FONT></HTML>");
		link2.setHorizontalAlignment(SwingConstants.LEFT);
		link2.setBorderPainted(false);
		link2.setOpaque(false);
		link2.setBackground(Color.WHITE);
		link2.setToolTipText(url2.toString());
		link2.addActionListener(this);
		link2.setActionCommand("U2");
		getContentPane().add(link2);

	}

	private void openurl(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("U1")) {
			if(url != null) openurl(url); 
		} else if (e.getActionCommand().equals("U2")) {
			if(url2 != null) openurl(url2);
		}
	}

}
