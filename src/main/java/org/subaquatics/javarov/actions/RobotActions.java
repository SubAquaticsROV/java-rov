package org.subaquatics.javarov.actions;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.ArrayList;

public class RobotActions {

	public static interface ConnectedCallback {
		/**
		 * If there was an exception while connecting to the robot, it is passed
		 * in as error. Otherwise, error is null.
		 */
		public void connected(InputStream in, OutputStream out, Exception error);
	}

	public static void connect(String port, ConnectedCallback callback) {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
			if (portIdentifier != null && !portIdentifier.isCurrentlyOwned()) {
				CommPort commPort = portIdentifier.open("java-rov", 1000);
				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
						serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
						InputStream in = serialPort.getInputStream();
						OutputStream out = serialPort.getOutputStream();
						callback.connected(in, out, null);
				} else {
					callback.connected(null, null, new Exception("Error: port not a serial port\n"));
				}
			} else {
				callback.connected(null, null, new Exception("Error: Could not open port.\n"));
			}
		} catch(Exception err) {
			callback.connected(null, null, err);
		}
	}

	public static interface SerialListCallback {
		public void connected(String[] commPorts);
	}

	public static void getSerialPorts(SerialListCallback callback) {
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
		callback.connected(portArray);
	}

}