package org.subaquatics.javarov;

import org.thingml.rtcharts.swing.GraphBuffer;
import org.thingml.rtcharts.swing.LineGraphPanel;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

import org.subaquatics.javarov.RovReader.LogListener;
import org.subaquatics.javarov.RovReader.VoltageListener;
import org.subaquatics.javarov.RovReader.TemperatureListener;

public class SwingUserInterface extends JFrame {

	private JTextArea log = new JTextArea(50, 80);
	private JTextField commandField = new JTextField(72);
	private JButton exectueButton = new JButton("Execute");
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel graphOne = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	private LineGraphPanel graphTwo = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);

	private ArrayList<QuitListener> quitters = new ArrayList<>();

	public SwingUserInterface() {
		JPanel panel = new JPanel(new MigLayout());

		JPanel cliPanel = new JPanel(new MigLayout());

		log.setEditable(false);
		DefaultCaret caret = (DefaultCaret)log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		exectueButton.addActionListener((e) -> {
			log.append(commandField.getText() + "\n");
		});

		cliPanel.add(new JScrollPane(log), "spanx 2, wrap");
		cliPanel.add(commandField, "grow");
		cliPanel.add(exectueButton);

		panel.add(cliPanel, "spany 2");
		panel.add(graphOne, "wrap");
		panel.add(graphTwo);

		this.add(panel);
		this.setSize(400, 300);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onQuit();
			}
		});

		graphOne.start();
		graphTwo.start();
	}

	public VoltageListener getVoltageListener() {
		return (voltage) -> {
			synchronized(voltageGraphBuffer) {
				voltageGraphBuffer.insertData(voltage);
			}
		};
	}

	public TemperatureListener getTemperatureListener() {
		return (temperature) -> {
			synchronized(temperatureGraphBuffer) {
				temperatureGraphBuffer.insertData(temperature);
			}
		};
	}

	public LogListener getLogListener() {
		return (message) -> {
			synchronized(log) {
				log.append(message+"\n");
			}
		};
	}

	public void addQuitListener(QuitListener quitter) {
		quitters.add(quitter);
	}

	private void onQuit() {
		for (QuitListener quitter: quitters) {
			quitter.quit();
		}
		System.exit(0);
	}

}