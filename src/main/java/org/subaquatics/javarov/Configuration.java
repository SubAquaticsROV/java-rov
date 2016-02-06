package org.subaquatics.javarov;

import java.util.ArrayList;
import org.subaquatics.javarov.commands.*;

public class Configuration {

	public ArrayList<SetMotorPinsCommand> motors = new ArrayList<>();
	public SetPWMBoundsCommand pwmBounds = new SetPWMBoundsCommand(0, 255);

	public static Configuration fromString(String input) {
		Configuration configuration = new Configuration();
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
						if (tokens.length < 5 || tokens.length > 5) {
							System.out.println("Correct usage: control-motor <id> <pwm> <left> <right>");
							continue;
						}
						int id = Integer.parseInt(tokens[1]);
						int pwm = Integer.parseInt(tokens[2]);
						int left = Integer.parseInt(tokens[3]);
						int right = Integer.parseInt(tokens[4]);
						configuration.motors.add(new SetMotorPinsCommand(id, pwm, left, right));
						break;
					case "set-pwmbounds":
						if (tokens.length < 3 || tokens.length > 3) {
							System.out.println("Correct usage: set-pwmbounds <min> <max>");
							continue;
						}
						int min = Integer.parseInt(tokens[1]);
						int max = Integer.parseInt(tokens[2]);
						configuration.pwmBounds = new SetPWMBoundsCommand(min, max);
						break;
					default:
						System.out.println("Error: \""+tokens[0]+"\" is not a configuration command.");
				}
			}
		}
		return configuration;
	}

	public static final Configuration DEFUALT = fromString(
		"set-pwmbounds 64 200\n"+
		"set-motorpins 1 11 25 27\n"+
		"set-motorpins 2 6 28 30\n"+
		"set-motorpins 3 3 38 32\n"+
		"set-motorpins 4 9 37 39\n"+
		"set-motorpins 5 10 29 31\n"+
		"set-motorpins 6 5 26 24\n"+
		"set-motorpins 7 2 36 34\n"+
		"set-motorpins 8 8 35 33\n"
	);

}