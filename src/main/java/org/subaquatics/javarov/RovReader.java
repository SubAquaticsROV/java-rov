package org.subaquatics.javarov;

import java.io.InputStream;
import java.io.IOException;

import org.thingml.rtcharts.swing.GraphBuffer;
import org.thingml.rtcharts.swing.LineGraphPanel;
import javax.swing.*;
import java.awt.*;

public class RovReader extends JFrame implements Runnable {
	private InputStream in;
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel voltageGraphPanel = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel temperatureGraphPanel = new LineGraphPanel(voltageGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);
    
    //Constructor
	public RovReader(InputStream in) {
		this.in = in;
		this.getContentPane().add(voltageGraphPanel);
		this.getContentPane().add(temperatureGraphPanel);
		this.setSize(400, 300);

		this.setVisible(true);
		new Thread(this).start();
		voltageGraphPanel.start();
		temperatureGraphPanel.start();
	}
    
	public void run() {
		int index = 0;
		int max = 15000;
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while(this.isVisible()) {
				int responseType = this.in.read();
				switch(responseType) {
					case 0x10:
						System.out.println("[Error]"+logMessage());
						break;
					case 0x11:
						System.out.println("[Warning]"+logMessage());
						break;
					case 0x12:
						System.out.println("[Info]"+logMessage());
						break;
					case 0x13:
						System.out.println("[Debug]"+logMessage());
						break;
					case 0x20: // Voltage
						int intValue = readInt();
						//System.out.println("[Sensor Voltage]"+intValue);
						//int time = (int)(System.currentTimeMillis() % max);
						voltageGraphBuffer.insertData(intValue);
						break;
					case 0x21: // Temperature
						int temperatureValue = readInt();
						//System.out.println("[Sensor Voltage]"+intValue);
						//int temperatureTime = (int)(System.currentTimeMillis() % max);
						temperatureGraphBuffer.insertData(temperatureValue);
						break;
				}
			}
		} catch( IOException e ) {
			e.printStackTrace();
		} finally {
			voltageGraphPanel.stop();
		}
	}

	private String logMessage() throws IOException {
		int length = this.in.read();
		int bytesRead = 0;
		StringBuilder builder = new StringBuilder();
		while (bytesRead < length) {
			builder.append((char)this.in.read());
			bytesRead++;
		}
		return builder.toString();
	}

	private int readInt() throws IOException {
		int[] buffer = new int[4];
		for (int i=0; i<buffer.length; i++) buffer[i] = this.in.read();
		return (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | (buffer[3]);
	}
}