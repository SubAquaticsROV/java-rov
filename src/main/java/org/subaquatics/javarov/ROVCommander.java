package org.subaquatics.javarov;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import java.io.OutputStream;
import java.io.IOException;
import org.subaquatics.javarov.commands.Command;

public class ROVCommander implements Runnable {

	OutputStream serialPort; 
	ReceiveChannel<Command> commandChannel; 
	ReceiveChannel<Boolean> runningChannel; // Tells the thread when to close

	public ROVCommander(OutputStream output, ReceiveChannel<Command> commandChannel, ReceiveChannel<Booleab> runningChannel) {
		this.input = input;
		this.commandChannel = commandChannel;
	}

	public void run() {
		try {
			boolean running = true;
			while (running) {
				Command command = commandChannel.tryReceive();
				if (command != null) {
					out.write(command.getId().getByte());
					byte[] payload = command.getData();
					int length = command.getId().getSize();
					for (int i=0; i<length; i++) {
						out.write(payload[i]);
					}
				}
				Boolean runningMessage = runningChannel.tryReceive();
				if (runningMessage != null) {
					running = runningMessage;
				}
				Thread.sleep(10);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ChannelClosedException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}