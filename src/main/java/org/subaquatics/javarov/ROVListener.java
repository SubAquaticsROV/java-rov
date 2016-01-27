package org.subaquatics.javarov;

import com.flipkart.lois.channel.api.SendChannel;

public class ROVListener {

	InputStream input; // Where we listen to stuff from the ROV
	SendChannel<String> messageOutput; // The place where we log the ROV's messages to

	public ROVListener(IntputStream input, SendChannel<String> messageOutput) {
		this.input = input;
		this.messageOutput = messageOutput;
	}

	public void run() {
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		while ( (bytesRead = this.input.read(buffer)) ) {
			messageOutput.send(new String(buffer, 0, bytesRead));
		}
	}

}