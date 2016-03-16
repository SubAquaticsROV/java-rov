package org.subaquatics.javarov;

import java.io.InputStream;
import java.io.IOException;

public class RovReader implements Runnable {
	InputStream in;
    
    //Constructor
	public RovReader(InputStream in)
	{
		this.in = in;
	}
    
	public void run()
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		try
		{
			while(true)
			{
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
					case 0x20:
						System.out.println("[Sensor Voltage]"+readInt());
						break;
				}
			}
		}
		catch( IOException e )
		{
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
		return (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | (buffer[3]);
	}
}