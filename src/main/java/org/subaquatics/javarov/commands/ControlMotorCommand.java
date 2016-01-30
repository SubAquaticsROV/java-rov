package org.subaquatics.javarov.commands;

public class ControlMotorCommand implements Command {

	private int motorId;
	private MotorDirection direction;
	private int speed;

	public ControlMotorCommand(int motorId, MotorDirection direction, int speed) {
		this.motorId = motorId;
		this.direction = direction;
		this.speed = speed;
	}

	/**
	 * Returns the id of the command
	 * @return The id of the command
	 */
	public CommandId getId() {
		return CommandId.CONTROL_MOTOR;
	}

	/**
	 * Returns the payload of the command in an array of bytes.
	 * @return The payload
	 */
	public byte[] getData() {
		return new byte[] {
			(byte)(((motorId & 0xF) << 4) | (direction.getByte() & 0xF)),
			(byte)(speed & 0xFF)
		};
	}

	public static enum MotorDirection {
		STOP(0), LEFT(1), RIGHT(2), BRAKE(3); // BRAKE should  almost never be used

		private byte flagByte;

		private MotorDirection(int flagByte) {
			this.flagByte = (byte) flagByte;
		}

		public byte getByte() {
			return flagByte;
		}
	}
	
}