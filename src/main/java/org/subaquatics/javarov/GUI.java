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

		// COM ports

		outputarea = new JTextArea(17, 1);
		outputarea.setEditable(false);

		command = new JTextField(20);

		commandbutton = new JButton("Do Command");
		commandbutton.addActionListener((e) -> {
			String text = command.getText();
			outputarea.append(text + "\n");
			command.setText("");
		});

		// Add stuff to the layout
		panel.add(new JLabel("Robot"));
		panel.add(new JLabel("Controller"), "wrap");
		panel.add(new JScrollPane(outputarea), "grow, span 2, wrap");
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
