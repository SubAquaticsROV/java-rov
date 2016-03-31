
package org.subaquatics.javarov;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import net.miginfocom.swing.MigLayout;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;

import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component;
import net.java.games.input.Controller;

public class Main {

	public void connect(String portName) throws Exception {
	    //Calling RXTX library to get the port the arduino is on - assumed to be
	    //"COM3" in this instance
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if(portIdentifier.isCurrentlyOwned()) //Makes sure com port is not being used by anything (such as the Arduino IDE)
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
		    //Connects to the port "COM3"
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
			if (commPort instanceof SerialPort) //Makes sure it is a serial port
			{
			    //Casting commPort from type CommPort to serialPort of type SerialPort
				SerialPort serialPort = (SerialPort) commPort;
				//Configure the port so it can communicate with the arduino (note the baud rate, 9600 in this case)
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                //Create an input stream (similar to System.in) that connects to serialPort
				InputStream in = serialPort.getInputStream();
				//Create an output stream (similar to System.out) that connects to serialPort
				OutputStream out = serialPort.getOutputStream();

                
                //Create new threads
                //This allows full-duplex (two-way) communication
                //The in and out streams are passed in
				CommandLine cli = new CommandLine(out);
				RovReader reader = new RovReader(in);
				SwingUserInterface sui = new SwingUserInterface();
				
				cli.setLogListener(sui.getLogListener());

				reader.setLogListener(sui.getLogListener());
				reader.setVoltageListener(sui.getVoltageListener());
				reader.setTemperatureListener(sui.getTemperatureListener());

				sui.addQuitListener(reader);
				sui.addQuitListener(cli);
				sui.setExecutor((command) -> {
					cli.executeCommand(command);
				});

				new Thread(reader).start();
				//new Thread(cli).start();
				sui.setVisible(true);
			}
			else //If it is not a serial port, say so
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public static void main(String[] args) {
		try {
			ArrayList<String> ports = new ArrayList<>();
			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements() ) {
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				ports.add(portIdentifier.getName());
			}

			JDialog dialog = new JDialog();
			JPanel panel = new JPanel(new MigLayout());

			String[] a = ports.toArray(new String[ports.size()]);

			JComboBox comportsBox = new JComboBox(a);
			JButton okButton = new JButton("Ok");
			okButton.addActionListener((e) -> {
				comportsBox.getSelectedItem();
				String selectedItem = (String)comportsBox.getSelectedItem();
				try {
					(new Main()).connect(selectedItem);	
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			});

			panel.add(comportsBox, "wrap");
			panel.add(okButton);
			dialog.add(panel);

			dialog.setVisible(true);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
