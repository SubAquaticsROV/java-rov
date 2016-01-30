package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;

public class GUI extends JFrame implements Runnable {
	private JTextField command;
	private JTextArea outputarea;
	private JButton commandbutton;
	
	public GUI() {
		JPanel panel = new JPanel(new MigLayout());

		outputarea = new JTextArea(20, 1);
		command = new JTextField(20);
		commandbutton = new JButton("Do Command");
		commandbutton.addActionListener((e) -> {
			String text = command.getText();
			outputarea.append(text + "\n");
			command.setText("");
		});

		panel.add(new JScrollPane(outputarea), "w 100%, span 2, wrap");
		panel.add(command, "w 80%");
		panel.add(commandbutton);
		
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
