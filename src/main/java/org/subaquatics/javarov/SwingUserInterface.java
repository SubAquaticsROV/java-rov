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

	private JButton temperatureOnButton = new JButton("On");
	private JButton temperatureOffButton = new JButton("Off");
	private JButton voltageOnButton = new JButton("On");
	private JButton voltageOffButton = new JButton("Off");
	private JButton testButton = new JButton("Test");
	private JButton cameraUpButton = new JButton("Front");
	private JButton cameraDownButton = new JButton("Back");
	private JButton cameraLeftButton = new JButton("Claw");
	private JButton cameraRightButton = new JButton("Bottom");
	private JTextArea log = new JTextArea(50, 80);
	private JTextField commandField = new JTextField(72);
	private JButton executeButton = new JButton("Execute");
	private JTextArea script = new JTextArea(30, 40);
	private JButton scriptButton = new JButton("Script");
	private JButton executeScriptButton = new JButton("Execute Script");
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel graphOne = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	//private LineGraphPanel graphTwo = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);
	private LineGraphPanel graphTwo = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 30000, 1000, 10000, Color.RED, 1, "", "AVG ", "LAST ");

	private ArrayList<QuitListener> quitters = new ArrayList<>();
	private ExecuteComand executor;

	public SwingUserInterface() {
		JPanel panel = new JPanel(new MigLayout());

		JPanel cliPanel = new JPanel(new MigLayout());

		JPanel buttonPanel = new JPanel(new MigLayout());

		JLabel depth = new JLabel("Depth");
		
		JLabel temperature = new JLabel("Temperature");

		JLabel voltage = new JLabel("Voltage");

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

		buttonPanel.add(cameraUpButton, "align right");
		buttonPanel.add(cameraDownButton, "wrap");
		buttonPanel.add(cameraLeftButton, "align right");
		buttonPanel.add(cameraRightButton, "wrap");
		buttonPanel.add(depth);
		buttonPanel.add(testButton, "wrap");
		buttonPanel.add(voltage);
		buttonPanel.add(voltageOnButton, "split 2");
		buttonPanel.add(voltageOffButton, "wrap");
		buttonPanel.add(temperature);
		buttonPanel.add(temperatureOnButton, "split 2");
		buttonPanel.add(temperatureOffButton);

		cameraUpButton.addActionListener((e) -> {
			executor.execute("switch-camera a 1");
		});

		cameraDownButton.addActionListener((e) -> {
			executor.execute("switch-camera a 3");
		});

		cameraLeftButton.addActionListener((e) -> {
			executor.execute("switch-camera a 0");
		});

		cameraRightButton.addActionListener((e) -> {
			executor.execute("switch-camera a 2");
		});
		
		testButton.addActionListener((e) -> {
			executor.execute("configure-sensorstate depth on");
		});

		temperatureOnButton.addActionListener((e) -> {
			executor.execute("configure-sensorstate temperature on");
		});

		temperatureOffButton.addActionListener((e) -> {
			executor.execute("configure-sensorstate temperature off");
		});


		voltageOnButton.addActionListener((e) -> {
			executor.execute("configure-sensorstate voltage on");
		});


		voltageOffButton.addActionListener((e) -> {
			executor.execute("configure-sensorstate voltage off");
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
		return (data) -> {
			synchronized(temperatureGraphBuffer) {
				voltageGraphBuffer.insertData(data);
				/*double A = 1.272556562e-3;
				double B = 2.057340045e-4;
				double C = 2.279277463e-7;
				double temperature = 1.0/(A+B * Math.log(data) + C * (Math.log(data) * Math.log(data) * Math.log(data)));*/
				double resistance = 10000.0;
				double percent = data / 1024.0;
				double resistance2 = (percent * resistance) / (1.0 - percent);
				temperatureGraphBuffer.insertData((int)(resistance2));
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