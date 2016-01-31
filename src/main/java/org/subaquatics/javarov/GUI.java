package org.subaquatics.javarov;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import net.miginfocom.swing.MigLayout;
import org.subaquatics.javarov.commands.Command;
import com.flipkart.lois.channel.impl.BufferedChannel;
import java.util.Enumeration;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;

public class GUI extends JFrame implements Runnable {

	// Graphical stuff
	private JLabel robotLabel;
	private JComboBox robotComboBox;
	private JButton robotButton;
	private JButton robotRefreshButton;

	private JLabel controllerLabel;
	private JComboBox controllerComboBox;
	private JButton controllerButton;
	private JButton controllerRefreshButton;

	private JTextField command;
	private JTextArea outputarea;
	private JButton commandbutton;

	// Business logic stuff
	private BufferedChannel<Command> commandChannel;
	private BufferedChannel<String> messageChannel;
	private BufferedChannel<Boolean> robotRunningChannel = new BufferedChannel<Boolean>(1);
	private BufferedChannel<Boolean> controllerRunningChannel = new BufferedChannel<Boolean>(1);
	private TextCommandParser parser;
	
	public GUI() {
		JPanel panel = new JPanel(new MigLayout("wrap 2"));

		// COM ports
		robotLabel = new JLabel("Robot: ");
		robotComboBox = new JComboBox(new String[]{""});
		robotButton = new JButton("Connect");
		robotButton.addActionListener((e) -> {
			if (commandChannel==null) {
				try {
					String identifier = (String) robotComboBox.getSelectedItem();
					CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(identifier);
					if (portIdentifier != null && !portIdentifier.isCurrentlyOwned()) {
						CommPort commPort = portIdentifier.open(this.getClass().getName(), 1000);
						if (commPort instanceof SerialPort) {
							SerialPort serialPort = (SerialPort) commPort;
								serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
								InputStream in = serialPort.getInputStream();
								OutputStream out = serialPort.getOutputStream();

								commandChannel = new BufferedChannel<Command>(16);
								messageChannel = new BufferedChannel<String>(16);

								parser = new TextCommandParser(commandChannel);

								(new Thread(new ROVCommander(out, commandChannel))).start();
								(new Thread(new ROVListener(in, messageChannel))).start();

								robotComboBox.setEnabled(false);
								robotButton.setText("Disconnect");
						} else {
							outputarea.append("Error: port not a serial port\n");
						}
					} else {
						outputarea.append("Error: Could not open port.\n");
					}
				} catch(Exception err) {
					err.printStackTrace();
					return;
				}
			} else {
				commandChannel.close();
				commandChannel = null;
				parser = null;
				robotComboBox.setEnabled(true);
				robotButton.setText("Connect");
			}
		});
		robotRefreshButton = new JButton("Refresh");
		robotRefreshButton.addActionListener((e) -> {
			String[] ports = getSerialPorts();
			robotComboBox.setModel(new DefaultComboBoxModel(ports));
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
			command.setText("");
			if (parser == null) {
				outputarea.append("!!! No ROV is connected !!!\n");
			} else {
				parser.parse(text);
			}
		});

		// Add stuff to the layout
		panel.add(robotLabel, "split 4");
		panel.add(robotComboBox);
		panel.add(robotButton);
		panel.add(robotRefreshButton);

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

	private String[] getSerialPorts() {
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> ports = new ArrayList<String>();
		while(portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			ports.add(portIdentifier.getName());
		}
		String[] portArray = new String[ports.size()];
		for (int i=0; i<ports.size(); i++) {
			portArray[i] = ports.get(i);
		}
		return portArray;
	}
}
