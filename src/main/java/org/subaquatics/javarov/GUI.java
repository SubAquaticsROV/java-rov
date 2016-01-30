package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;

public class GUI extends JFrame implements Runnable {
	private JLabel robotLabel;
	private JComboBox robotComboBox;
	private JButton robotButton;

	private JLabel controllerLabel;
	private JComboBox controllerComboBox;
	private JButton controllerButton;

	private JTextField command;
	private JTextArea outputarea;
	private JButton commandbutton;
	
	public GUI() {
		JPanel panel = new JPanel(new MigLayout("wrap 2"));

		// COM ports
		robotLabel = new JLabel("Robot: ");
		robotComboBox = new JComboBox(new String[]{"Option one", "Option two"});
		robotButton = new JButton("Connect");
		robotButton.addActionListener((e) -> {
			robotComboBox.setEnabled(!robotComboBox.isEnabled());
		});

		// Controller ports
		controllerLabel = new JLabel("Controller: ");
		controllerComboBox = new JComboBox(new String[]{"Option one", "Option two"});
		controllerButton = new JButton("Start");
		controllerButton.addActionListener((e) -> {
			controllerComboBox.setEnabled(!controllerComboBox.isEnabled());
		});

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
		panel.add(robotLabel, "split 3");
		panel.add(robotComboBox);
		panel.add(robotButton);

		panel.add(controllerLabel, "split 3");
		panel.add(controllerComboBox);
		panel.add(controllerButton);

		panel.add(new JScrollPane(outputarea), "grow, span 2");
		panel.add(command, "growx");
		panel.add(commandbutton);
		
		add(panel, BorderLayout.SOUTH);
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
