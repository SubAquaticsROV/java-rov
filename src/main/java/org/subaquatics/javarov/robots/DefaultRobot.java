package org.subaquatics.javarov.robots;

import org.subaquatics.javarov.info.Info;
import org.subaquatics.javarov.info.MessageInfo;
import org.subaquatics.javarov.commands.Command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DefaultRobot implements Robot {

	private ArrayList<Command> commands = new ArrayList<>();
	private InputStream in;
	private OutputStream out;

	public DefaultRobot(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void send(Command command) {
		commands.add(command);
	}
	
	public void flush() {
		for (Command command: commands) {
			out.write(command.getId().getByte());
			byte[] payload = command.getData();

			// Get how long the command is according to the specification
			int length = command.getId().getSize();
			for (int i=0; i<length; i++) {
				out.write(payload[i]);
			}
		}
	}

	public ArrayList<Info> read() {
		ArrayList<Info> info = new ArrayList<>();
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		while ( (bytesRead = in.read(buffer)) > -1) {
			info.add(new MessageInfo(new String(buffer, 0, bytesRead)));
		}
		return info;
	}

}