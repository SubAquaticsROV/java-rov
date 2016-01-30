package org.subaquatics.javarov.commands;

public interface Command {

	/**
	 * Returns the id of the command
	 * @return The id of the command
	 */
	public CommandId getId();

	/**
	 * Returns the payload of the command in an array of bytes.
	 * @return The payload
	 */
	public byte[] getData();
	
}