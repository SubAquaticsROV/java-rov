package org.subaquatics.javarov.commands;

public class EchoByteCommand implements Command {

	private byte data;

	public EchoByteCommand(byte data) {
		this.data = data;
	}

	/**
	 * Returns the id of the command
	 * @return The id of the command
	 */
	public CommandId getId() {
		return CommandId.ECHO_BYTE;
	}

	/**
	 * Returns the payload of the command in an array of bytes.
	 * @return The payload
	 */
	public byte[] getData() {
		return new byte[] {data};
	}
	
}