package org.subaquatics.javarov;

import java.io.InputStream;
import java.io.IOException;

public class RovReader implements Runnable {
	private InputStream in;
	private LogListener logListener;
	private VoltageListener voltageListener;
	private TemperatureListener temperatureListener;
    
    //Constructor
	public RovReader(InputStream in) {
		this.in = in;
		this.logListener = (message) -> {
			System.out.println(message);
		};
		this.voltageListener = (voltage) -> {
			System.out.println("[Voltage]"+voltage);
		};
		this.temperatureListener = (voltage) -> {
			System.out.println("[Temperature]"+voltage);
		};
	}
    
	public void run() {
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while(true) {
				int responseType = this.in.read();
				switch(responseType) {
					case 0x10:
						logListener.update("[Error]"+logMessage());
						break;
					case 0x11:
						logListener.update("[Warning]"+logMessage());
						break;
					case 0x12:
						logListener.update("[Info]"+logMessage());
						break;
					case 0x13:
						logListener.update("[Debug]"+logMessage());
						break;
					case 0x20: // Voltage
						voltageListener.update(readInt());
						break;
					case 0x21: // Temperature
						temperatureListener.update(readInt());
						break;
				}
			}
		} catch( IOException e ) {
			e.printStackTrace();
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

	public void setLogListener(LogListener logListener) {
		this.logListener = logListener;
	}

	public void setVoltageListener(VoltageListener voltageListener) {
		this.voltageListener = voltageListener;
	}

	public void setTemperatureListener(TemperatureListener temperatureListener) {
		this.temperatureListener = temperatureListener;
	}

	public static interface VoltageListener {
		public void update(int voltage);
	}

	public static interface TemperatureListener {
		public void update(int temperature);
	}

	public static interface LogListener {
		public void update(String message);
	}
}