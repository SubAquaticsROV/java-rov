package org.subaquatics.javarov.robots;

import java.util.ArrayList;

import org.subaquatics.javarov.info.Info;
import org.subaquatics.javarov.commands.Command;

public interface Robot {

	// Be prepared to send this command
	public void send(Command command);
	
	// Send all the commands that need to be sent.
	public void flush();

	// Is there anything that we need to know about from the robot?
	public ArrayList<Info> read();

}