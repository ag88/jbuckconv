package org.jbuckconv;

import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

public class App {

	Preferences pref = Preferences.userNodeForPackage(App.class);
	String prev_dir;
	
	private static App m_instance;

	public App() {
	}

	public static App getInstance() {
		// Double lock for thread safety.
		if (m_instance == null) {
			synchronized (App.class) {
				if (m_instance == null) {
					m_instance = new App();
				}
			}
		}
		return m_instance;
	}

	public void run(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame mf = new MainFrame();
				mf.pack();
				mf.setLocationRelativeTo(null);
				mf.setVisible(true);
			}
		});
	}
	
	public String getPrevDir() {
		if (prev_dir == null)
			prev_dir = pref.get("prev_dir", System.getProperty("user.home"));
		return prev_dir;
	}

	public void setPrevDir(String prevdir) {
		if (prev_dir == null || ! prevdir.equals(prev_dir)) {
			pref.put("prev_dir", prevdir);
			prev_dir = prevdir;
		}
	}
	
	public static void main(String[] args) {
		new App().run(args);
	}

}
