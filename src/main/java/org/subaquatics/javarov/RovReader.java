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
						System.out.println("[Error]"+logMessage(buffer));
						break;
					case 0x11:
						System.out.println("[Warning]"+logMessage(buffer));
						break;
					case 0x12:
						System.out.println("[Info]"+logMessage(buffer));
						break;
					case 0x13:
						System.out.println("[Debug]"+logMessage(buffer));
						break;
					case 0x20:
						System.out.println("[Sensor Voltage]"+readInt(buffer));
						break;
				}
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	private String logMessage(byte[] buffer) throws IOException {
		int length = this.in.read();
		int bytesRead = 0;
		StringBuilder builder = new StringBuilder();
		while (bytesRead < length) {
			builder.append((char)this.in.read());
			bytesRead++;
		}
		return builder.toString();
	}

	private int readInt(byte[] buffer) throws IOException {
		this.in.read(buffer, 0, 4);
		return (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | (buffer[3]);
	}
}