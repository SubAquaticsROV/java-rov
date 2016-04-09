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

import org.subaquatics.javarov.RovReader.VoltageListener;
import org.subaquatics.javarov.RovReader.TemperatureListener;

public class SwingUserInterface extends JFrame {

	private JButton cameraUpButton = new JButton("Up");
	private JButton cameraDownButton = new JButton("Down");
	private JButton cameraLeftButton = new JButton("Left");
	private JButton cameraRightButton = new JButton("Right");
	private JTextArea log = new JTextArea(50, 80);
	private JTextField commandField = new JTextField(72);
	private JButton executeButton = new JButton("Execute");
	private JTextArea script = new JTextArea(30, 40);
	private JButton scriptButton = new JButton("Script");
	private JButton executeScriptButton = new JButton("Execute Script");
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel graphOne = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	private LineGraphPanel graphTwo = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);

	private ArrayList<QuitListener> quitters = new ArrayList<>();
	private ExecuteComand executor;

	public SwingUserInterface() {
		JPanel panel = new JPanel(new MigLayout());

		JPanel cliPanel = new JPanel(new MigLayout());

		JPanel buttonPanel = new JPanel(new MigLayout());

		log.setEditable(false);
		DefaultCaret caret = (DefaultCaret)log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		executor = (text) -> {
			log.append(text + "\n");
		};

		executeButton.addActionListener((e) -> {
			executor.execute(commandField.getText());
			commandField.setText("");
		});

		commandField.addActionListener((e) -> {
			executor.execute(commandField.getText());
			commandField.setText("");
		});

		JDialog dialog = new JDialog();
		JPanel dialogPanel = new JPanel(new MigLayout());
		scriptButton.addActionListener((e) -> {
			dialog.setVisible(true);
		});
		dialog.setModal(true);

		executeScriptButton.addActionListener((e) -> {
			String[] scriptLines = script.getText().split("\n");
			for (String line: scriptLines) {
				executor.execute(line);
			}
			script.setText("");
			dialog.setVisible(false);
		});

		dialogPanel.add(new JScrollPane(script), "wrap");
		dialogPanel.add(executeScriptButton);
		dialog.add(dialogPanel);
		dialog.pack();

		cliPanel.add(new JScrollPane(log), "spanx 3, wrap");
		cliPanel.add(commandField, "grow");
		cliPanel.add(executeButton);
		cliPanel.add(scriptButton);

		buttonPanel.add(cameraUpButton);
		buttonPanel.add(cameraDownButton, "wrap");
		buttonPanel.add(cameraLeftButton);
		buttonPanel.add(cameraRightButton);


		cameraUpButton.addActionListener((e) -> {
			executor.execute("switch-camera a 0");
		});

		cameraDownButton.addActionListener((e) -> {
			executor.execute("switch-camera a 1");
		});

		cameraLeftButton.addActionListener((e) -> {
			executor.execute("switch-camera a 2");
		});

		cameraRightButton.addActionListener((e) -> {
			executor.execute("switch-camera a 3");
		});

		panel.add(buttonPanel);
		panel.add(graphOne, "wrap");
		panel.add(cliPanel);
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

	public void setExecutor(ExecuteComand e) {
		executor = e;
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

	public static interface ExecuteComand {
		public void execute(String text);
	}

}