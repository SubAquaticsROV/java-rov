package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class GUI extends JFrame implements Runnable {

	public GUI() {

		
	}

	private void initUI() {
		setTitle("GUI");
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void run() {
		initUI();

		this.setVisible(true);
	}
}
