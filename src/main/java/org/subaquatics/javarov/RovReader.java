package org.subaquatics.javarov;

import java.io.InputStream;
import java.io.IOException;

import org.thingml.rtcharts.swing.GraphBuffer;
import org.thingml.rtcharts.swing.LineGraphPanel;
import javax.swing.*;
import java.awt.*;

public class RovReader extends JFrame implements Runnable {
	private InputStream in;
	private JTextArea log = new JTextArea(50, 80);
	private GraphBuffer voltageGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel voltageGraphPanel = new LineGraphPanel(voltageGraphBuffer, "Voltage", 0, 1024, 32, 1000, Color.GREEN);
	private GraphBuffer temperatureGraphBuffer = new GraphBuffer(1500);
	private LineGraphPanel temperatureGraphPanel = new LineGraphPanel(temperatureGraphBuffer, "Temperature", 0, 1024, 32, 1000, Color.RED);
    
    //Constructor
	public RovReader(InputStream in) {
		this.in = in;
		log.setEditable(false);
		JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, voltageGraphPanel, temperatureGraphPanel);
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(log), right);
		this.add(pane);
		this.setSize(400, 300);

		this.setVisible(true);
		new Thread(this).start();
		voltageGraphPanel.start();
		temperatureGraphPanel.start();
	}
    
	public void run() {
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while(this.isVisible()) {
				int responseType = this.in.read();
				switch(responseType) {
					case 0x10:
						log.append("[Error]"+logMessage()+"\n");
						break;
					case 0x11:
						log.append("[Warning]"+logMessage()+"\n");
						break;
					case 0x12:
						log.append("[Info]"+logMessage()+"\n");
						break;
					case 0x13:
						log.append("[Debug]"+logMessage()+"\n");
						break;
					case 0x20: // Voltage
						int intValue = readInt();
						voltageGraphBuffer.insertData(intValue);
						break;
					case 0x21: // Temperature
						int temperatureValue = readInt();
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
		return ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
	}
}