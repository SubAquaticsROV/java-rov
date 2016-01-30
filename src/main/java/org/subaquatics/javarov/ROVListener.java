package org.subaquatics.javarov;

import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import java.io.InputStream;
import java.io.IOException;

public class ROVListener implements Runnable {

	InputStream input; // Where we listen to stuff from the ROV
	SendChannel<String> messageOutput; // The place where we log the ROV's messages to
	ReceiveChannel<Boolean> runningChannel; // Tells the thread when to close

	public ROVListener(InputStream input, SendChannel<String> messageOutput, ReceiveChannel<Boolean> runningChannel) {
		this.input = input;
		this.messageOutput = messageOutput;
		this.runningChannel = runningChannel;
	}

	public void run() {
		try {
			byte[] buffer = new byte[1024];
			int bytesRead = -1;
			boolean running = true;
			while ( (bytesRead = input.read(buffer)) > -1 && running) {
				messageOutput.send(new String(buffer, 0, bytesRead));

				Boolean runningMessage = runningChannel.tryReceive();
				if (runningMessage != null) {
					running = runningMessage;
				}
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