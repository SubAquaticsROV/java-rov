package org.subaquatics.javarov;

import org.subaquatics.javarov.info.Info;
import org.subaquatics.javarov.views.InfoView;
import org.subaquatics.javarov.devices.InputDevice;
import org.subaquatics.javarov.robots.Robot;
import org.subaquatics.javarov.commands.ControlMotorCommand;
import org.subaquatics.javarov.commands.SetMotorPinsCommand;
import org.subaquatics.javarov.commands.SetPWMBoundsCommand;

// A note: most of this is pseudocode at this point.
public class MainLoop {

	private InputDevice device;
	private Configuration configuration;
	private Robot robot;
	private InfoView infoView;

	private int bottomFrontLeftMotor = 1;
	private int bottomFrontRightMotor = 2;
	private int bottomBackLeftMotor = 3;
	private int bottomBackRightMotor = 4;
	private int topFrontLeftMotor = 5;
	private int topFrontRightMotor = 6;
	private int topBackLeftMotor = 7;
	private int topBackRightMotor = 8;

	private static ControlMotorCommand.MotorDirection LEFT = ControlMotorCommand.MotorDirection.LEFT;
	private static ControlMotorCommand.MotorDirection RIGHT = ControlMotorCommand.MotorDirection.RIGHT;

	public void update() {
		device.update();

		// Does the configuration need to be sent to the robot
		if (device.shouldUpdateConfig()) {
			for (Configuration.MotorPins motor: configuration.motors) {
				robot.send(new SetMotorPinsCommand(motor.id, motor.pwm, motor.left, motor.right));
			}
			robot.send(new SetPWMBoundsCommand(configuration.pwmBounds.min, configuration.pwmBounds.max));
		}

		{ // Code to move the robot forward
			int forwardSpeed = (int) device.throttleValue() * 255;
			boolean forward = forwardSpeed > 0;
			forwardSpeed = forward ? forwardSpeed : -forwardSpeed;
			robot.send(new ControlMotorCommand(bottomBackLeftMotor, forward ? LEFT : RIGHT, forwardSpeed));
			robot.send(new ControlMotorCommand(bottomBackRightMotor, forward ? RIGHT : LEFT, forwardSpeed));
		}

		{ // Code to turn the robot left and right
			int turnSpeed = (int) device.horizontalValue() * 255;
			boolean right = turnSpeed > 0;
			turnSpeed = right ? turnSpeed : -turnSpeed;
			robot.send(new ControlMotorCommand(bottomFrontLeftMotor, right ? LEFT : RIGHT, turnSpeed));
			robot.send(new ControlMotorCommand(bottomFrontRightMotor, right ? RIGHT : LEFT, turnSpeed));
		}

		{ // Code to move the robot up and down
			int upSpeed = (int) device.verticalValue() * 255;
			boolean up = upSpeed > 0;
			upSpeed = up ? upSpeed : -upSpeed;
			robot.send(new ControlMotorCommand(topFrontLeftMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topFrontRightMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topBackLeftMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topBackRightMotor, up ? LEFT : RIGHT, upSpeed));
		}

		robot.flush(); // Send all the batched commands

		// Read messages from the robot and dispatch them to the view
		for (Info info: robot.read()) {
			infoView.append(info);
		}
	}

	public void setInputDevice(InputDevice device) {this.device = device;}
	public void setConfiguration(Configuration configuration) {this.configuration = configuration;}
	public void setRobot(Robot robot) {this.robot = robot;}
	public void setInfoView(InfoView infoView) {this.infoView = infoView;}

}