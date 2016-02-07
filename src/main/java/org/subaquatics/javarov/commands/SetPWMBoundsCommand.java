package org.subaquatics.javarov.commands;

public class SetPWMBoundsCommand implements Command {

	private int min;
	private int max;

	public SetPWMBoundsCommand(int min, int max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Returns the id of the command
	 * @return The id of the command
	 */
	public CommandId getId() {
		return CommandId.SET_PWM_BOUNDS;
	}

	/**
	 * Returns the payload of the command in an array of bytes.
	 * @return The payload
	 */
	public byte[] getData() {
		return new byte[] {
			(byte)(min),
			(byte)(max)
		};
	}
	
}