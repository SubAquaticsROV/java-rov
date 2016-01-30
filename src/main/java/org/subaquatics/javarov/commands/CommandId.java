package org.subaquatics.javarov.commands;

public enum CommandId {

	SET_MOTOR_PINS(0x10, 3),
	CONTROL_MOTOR(0x11, 2),
	SET_PWM_BOUNDS(0x12, 2),
	SET_SAFETY_TIMEOUT(0x13, 2),
	ECHO_BYTE(0xF0, 1);

	// The command is actually limited to a byte in size, but I don't want to 
 	// add a bunch of casts in. Casting it at the getByte() function is easier.
	private int id;
	private int size;

	private CommandId(int id, int payloadSize) {
		this.id = id;
		this.size = payloadSize;
	}

	/**
	 * Returns the byte that represents the command.
	 * @return The byte id of the command.
	 */
	public byte getByte() {
		return (byte) id;
	}

	/**
	 * Returns the size of the command in bytes, excluding the byte for the id.
	 * @return The size of the payload
	 */
	public int getSize() {
		return size;
	}
	
}