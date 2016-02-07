package org.subaquatics.javarov.commands;

public class SetMotorPinsCommand implements Command {

	private int motorId;
	private int pwm;
	private int left;
	private int right;

	public SetMotorPinsCommand(int motorId, int pwm, int left, int right) {
		this.motorId = motorId;
		this.pwm = pwm;
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns the id of the command
	 * @return The id of the command
	 */
	public CommandId getId() {
		return CommandId.SET_MOTOR_PINS;
	}

	/**
	 * Returns the payload of the command in an array of bytes.
	 * @return The payload
	 */
	public byte[] getData() {
		return new byte[] {
			(byte)(((motorId & 0xF) << 4) | (pwm & 0xF)),
			(byte)(left),
			(byte)(right)
		};
	}
	
}