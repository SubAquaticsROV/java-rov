package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;

public class GUI extends JFrame implements Runnable {
	private JTextField command;
	private JTextArea outputarea;
	
	public GUI() {
		JPanel panel = new JPanel(new MigLayout());

		outputarea = new JTextArea(25,30);
		command = new JTextField(20);

		panel.add(outputarea, "w 100%, wrap");
		panel.add(command, "w 80%");
		
		add(panel, BorderLayout.SOUTH);
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
