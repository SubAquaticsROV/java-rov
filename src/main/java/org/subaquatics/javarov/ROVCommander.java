package org.subaquatics.javarov;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import java.io.OutputStream;
import java.io.IOException;
import org.subaquatics.javarov.commands.Command;

public class ROVCommander implements Runnable {

	OutputStream serialPort; 
	ReceiveChannel<Command> commandChannel;

	public ROVCommander(OutputStream serialPort, ReceiveChannel<Command> commandChannel) {
		this.serialPort = serialPort;
		this.commandChannel = commandChannel;
	}

	public void run() {
		try {
			while (commandChannel.isOpen()) {
				Command command = commandChannel.tryReceive();
				if (command != null) {
					serialPort.write(command.getId().getByte());
					byte[] payload = command.getData();
					int length = command.getId().getSize();
					for (int i=0; i<length; i++) {
						serialPort.write(payload[i]);
					}
				}
				Thread.sleep(10);
			}
			serialPort.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ChannelClosedException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}