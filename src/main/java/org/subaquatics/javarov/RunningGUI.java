package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import net.miginfocom.swing.MigLayout;
import org.subaquatics.javarov.info.Info;
import org.subaquatics.javarov.views.InfoView;

public class RunningGUI extends JFrame implements Runnable, InfoView {

	// Graphical stuff
	private JTextArea outputArea;

	private void initUI() {
		JPanel panel = new JPanel(new MigLayout("fill"));

		outputArea = new JTextArea(15, 20);
		outputArea.setEditable(true);

		// Add stuff to the layout
		panel.add(new JScrollPane(outputArea), "grow");
		
		add(panel, BorderLayout.CENTER);

		setTitle("GUI");
		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void run() {
		initUI();

		this.setVisible(true);
	}

	public void append(Info info) {
		outputArea.append(info.getHumanReadableString());
		outputArea.append("\n");
	}

}
