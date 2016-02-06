package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import net.miginfocom.swing.MigLayout;

public class RunningGUI extends JFrame implements Runnable {

	// Graphical stuff
	private JTextArea outputArea;
	
	public RunningGUI() {
		JPanel panel = new JPanel(new MigLayout("fill"));

		outputArea = new JTextArea(15, 20);

		// Add stuff to the layout
		panel.add(new JScrollPane(outputArea), "grow");
		
		add(panel, BorderLayout.CENTER);
	}

	private void initUI() {
		setTitle("GUI");
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void run() {
		initUI();

		this.setVisible(true);
	}
}
