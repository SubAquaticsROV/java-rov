package org.subaquatics.javarov;

import org.subaquatics.javarov.commands.*;
import org.subaquatics.javarov.robots.Robot;

public class TextCommandParser {

	private Robot robot;

	public TextCommandParser(Robot robot) {
		this.robot = robot;
	}

	/**
	 * @return Returns a string describing the error or null.
	 */
	public String parse(String input) {
		String[] lines = input.split("\n");
		for (String line: lines) {
			String trimmed = line.trim();
			if (trimmed.contains("#")) {
				trimmed = trimmed.substring(0, trimmed.indexOf('#'));
			}
			String[] tokens = trimmed.split(" ");
			if (tokens.length > 0) {
				switch(tokens[0].toLowerCase()) {
					case "set-motorpins":
						break;
					case "control-motor":
						if (tokens.length < 4 || tokens.length > 4) {
							return "Correct usage: control-motor <id> <direction> <speed>";
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
						robot.send(new ControlMotorCommand(motorId, direction, speed));
						break;
					case "set-pwmbounds":
						if (tokens.length < 3 || tokens.length > 3) {
							return "Correct usage: control-motor <min> <max>";
						}
						int min = Integer.parseInt(tokens[1]);
						int max = Integer.parseInt(tokens[2]);
						robot.send(new SetPWMBoundsCommand(min, max));
						break;
					case "echo-string":
						if (tokens.length < 2 || tokens.length > 2) {
							return "\"echo-string\" takes only one argument!";
						}
						for (int i=0; i<tokens[1].length(); i++) {
							byte c = (byte)(tokens[1].charAt(i));
							robot.send(new EchoByteCommand(c));
						}
						break;
					default:
						return "Error: \""+tokens[0]+"\" is not a command.";
				}
			}
		}
		return null;
	}

}