package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;


public class GUI extends JFrame implements Runnable {
	private JTextField command;
	private JTextArea outputarea;
	
	public GUI() {
		command = new JTextField();
		outputarea = new JTextArea();
		add(command);
		add(outputarea);
		
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
