package org.subaquatics.javarov;

import java.io.OutputStream;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

//Inner class declaration
//Uses the runnable interface to allow for multi-threading
//Multi-threading = running multiple things simultaneously
public class CommandLine implements Runnable
{

	private HashMap<String, Command> commands;
	private OutputStream out;
	private IRobot bot;

    //Constructor
	public CommandLine(OutputStream out) {
		commands = new HashMap<>();
		this.out = out;
		this.bot = new Robot(this.out);
		initCommands();
	}

	public void addCommand(Command command) {
		this.commands.put(command.getName(), command);
	}

	public void run() {
		Scanner input = new Scanner(System.in);
		Pattern commandPattern = Pattern.compile("\\s+");
		while( true ) {
			String[] args = commandPattern.split(input.nextLine(), 2);
			String commandString = args[0];
			String arg = "";
			if (args.length >= 2) {
				arg = args[1];
			}
			
			if(commands.containsKey(commandString)) {
				Command command = commands.get(commandString);
				if(!command.execute(arg)) {
					System.out.println("Correct usage: "+command.getName()+"\t"+command.getParameters());
				}
			} else {
				System.out.println("Unknown command. Type 'help' for help.");
			}
		}
	}

	private void initCommands() {
		addCommand(new Command(
			"help",
			"[string]",
			"Get help.",
			(arg) -> {
				if (arg==null || arg.equals("")) {
					for (Command command: commands.values()) {
						System.out.println(command.getName()+"\t"+command.getDescription());
					}
				} else if(commands.containsKey(arg)) {
					Command command = commands.get(arg);
					System.out.println(command.getName()+"\t"+command.getParameters());
					System.out.println(command.getDescription());
				} else {
					// TODO: Do regex on name and description...
				}
				return true;
			}
		));

		addCommand(new Command(
			"echo-bytes",
			"<string>",
			"Have the rov repeat the string to the computer.",
			(arg) -> {
				for(int i=0; i < arg.length(); i++) {
					bot.echo((int)arg.charAt(i));
				}
				return true;
			}
		));

		Pattern boundsPattern = Pattern.compile("(\\d+)\\s+(\\d+)");
		addCommand(new Command(
			"configure-motorpwmbounds",
			"<lower> <upper>",
			"Configure the rov to have an upper and lower pwm bounds.",
			(arg) -> {
				Matcher m = boundsPattern.matcher(arg);
				if(m.matches()) {
					int lower = Integer.parseInt(m.group(1));
					int upper = Integer.parseInt(m.group(2));
					bot.configureMotorPWMBounds(lower, upper);
					return true;
				}
				return false;
			}
		));

		Pattern motorPinsPattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
		addCommand(new Command(
			"configure-motorpins",
			"<motor> <pwm-pin> <a-pin> <b-pin>",
			"Configure the motor to use the specified pins.",
			(arg) -> {
				Matcher m = motorPinsPattern.matcher(arg);
				if(m.matches()) {
					int id = Integer.parseInt(m.group(1));
					int pwm = Integer.parseInt(m.group(2));
					int a = Integer.parseInt(m.group(3));
					int b = Integer.parseInt(m.group(4));
					bot.configureMotorPins(id, pwm, a, b);
					return true;
				}
				return false;
			}
		));

		Pattern controlMotorPattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)");
		addCommand(new Command(
			"control-motor",
			"<motor> <flags> <pwm>",
			"Set the motor to spin at certain pwm. <flags> 0=stop, 1=left, 2=right",
			(arg) -> {
				Matcher m = controlMotorPattern.matcher(arg);
				if(m.matches()) {
					int id = Integer.parseInt(m.group(1));
					int flags = Integer.parseInt(m.group(2));
					int pwm = Integer.parseInt(m.group(3));
					bot.controlMotor(id, flags, pwm);
					return true;
				}
				return false;
			}
		));

		addCommand(new Command(
			"show-controllers",
			"",
			"Shows a list of controllers that can be used.",
			(arg) -> {
				Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
				for(int i=0; i<controllers.length; i++) {
					System.out.println(i + ": " + controllers[i].getName() + ", " + controllers[i].getType());
				}
				return true;
			}
		));

		Pattern startControllerPattern = Pattern.compile("(\\d+)\\s+(\\w+)");
		addCommand(new Command(
			"start-controller",
			"<controller> <mapping>",
			"Begin controlling the rov with a controller.",
			(arg) -> {
				Matcher m = startControllerPattern.matcher(arg);
				if(m.matches()) {
					int controller = Integer.parseInt(m.group(1));
					Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
					if(controller > controllers.length || controller < 0) {
						System.out.println("Invalid controller number \""+controller+"\".");
						System.out.println("List available controllers with \"show-controllers\".");
					} else {
						new Thread(new JoystickHandler(bot, controllers[controller], m.group(2))).start();
						System.out.println("Starting controller listening thread.");
					}
					return true;
				}
				return false;
			}
		));

		addCommand(new Command(
			"get-version",
			"",
			"Get the version of code running on the Arduino.",
			(arg) -> {
				if (arg.trim().equals("")) {
					bot.version();
					return true;
				}
				return false;
			}
		));
	}
}