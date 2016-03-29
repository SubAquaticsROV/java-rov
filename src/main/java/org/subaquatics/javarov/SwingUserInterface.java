package org.subaquatics.javarov;

import org.thingml.rtcharts.swing.GraphBuffer;
import org.thingml.rtcharts.swing.LineGraphPanel;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

import net.miginfocom.swing.MigLayout;

import org.subaquatics.javarov.RovReader.LogListener;
import org.subaquatics.javarov.RovReader.VoltageListener;
import org.subaquatics.javarov.RovReader.TemperatureListener;

public class SwingUserInterface extends JFrame {

	private JTextArea log = new JTextArea(50, 80);
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel graphOne = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	private LineGraphPanel graphTwo = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);

	public SwingUserInterface() {
		JPanel panel = new JPanel(new MigLayout());

		log.setEditable(false);
		DefaultCaret caret = (DefaultCaret)log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		panel.add(new JScrollPane(log), "spany 2");
		panel.add(graphOne, "wrap");
		panel.add(graphTwo);
		
		this.add(panel);
		this.setSize(400, 300);

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

}