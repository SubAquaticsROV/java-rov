package org.subaquatics.javarov;

import org.subaquatics.javarov.commands.*;

import java.util.ArrayList;

public class TextCommandParser {

	/**
	 * @return Returns a string describing the error or null.
	 */
	public ArrayList<Command> parse(String input) {
		ArrayList<Command> commands = new ArrayList<>();
		String[] lines = input.split("\n");
		for (String line: lines) {
			Command c = parseLine(line);
			if (c != null) {
				commands.add(c);
			}
		}
		return commands;
	}

	/**
	 * Takes a command represented by a text and transforms it into a command
	 * Note that when it says "line" it really just means a string that contains
	 * just one command. Newlines can be in the text.
	 * @return Returns the command represented in the line, or null
	 */
	public Command parseLine(String line) {
		// Remove any comments
		String trimmed = line.trim();
		if (trimmed.contains("#")) {
			trimmed = trimmed.substring(0, trimmed.indexOf('#'));
		}

		// Split the string into command and parameters
		String[] tokens = trimmed.split(" ");
		if (tokens.length > 0) {
			switch(tokens[0].toLowerCase()) {
				case "set-motorpins":
					if (tokens.length < 5 || tokens.length > 5) {
						System.out.println("Correct usage: control-motor <id> <pwm> <left> <right>");
						return null;
					}
					int id = Integer.parseInt(tokens[1]);
					int pwm = Integer.parseInt(tokens[2]);
					int left = Integer.parseInt(tokens[3]);
					int right = Integer.parseInt(tokens[4]);
					return new SetMotorPinsCommand(id, pwm, left, right);
				case "control-motor":
					if (tokens.length < 4 || tokens.length > 4) {
						System.out.println("Correct usage: control-motor <id> <direction> <speed>");
						return null;
					}
					int motorId = Integer.parseInt(tokens[1]);
					ControlMotorCommand.MotorDirection direction = ControlMotorCommand.MotorDirection.STOP;
					switch (tokens[2].toLowerCase()) {
					case "left":
						direction = ControlMotorCommand.MotorDirection.LEFT;
						break;
					case "right":
						direction = ControlMotorCommand.MotorDirection.RIGHT;
						break;
					case "stop":
						direction = ControlMotorCommand.MotorDirection.STOP;
						break;
					}
					int speed = Integer.parseInt(tokens[3]);
					return new ControlMotorCommand(motorId, direction, speed);
				case "set-pwmbounds":
					if (tokens.length < 3 || tokens.length > 3) {
						System.out.println("Correct usage: set-pwmbounds <min> <max>");
						return null;
					}
					int min = Integer.parseInt(tokens[1]);
					int max = Integer.parseInt(tokens[2]);
					return new SetPWMBoundsCommand(min, max);
				case "echo-byte":
					if (tokens.length < 2 || tokens.length > 2 || tokens[1].length()>0) {
						System.out.println("Correct usage: echo-byte <char>");
						return null;
					}
					byte c = (byte)(tokens[1].charAt(0));
					return new EchoByteCommand(c);
				default:
					System.out.println("Error: \""+tokens[0]+"\" is not a command.");
			}
		}
		return null;
	}

}