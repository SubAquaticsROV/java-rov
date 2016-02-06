package org.subaquatics.javarov;

// A note: most of this is pseudocode at this point.
public class MainLoop {

	private Controller controller;
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

	public void update() {
		controller.update();

		// Does the configuration need to be sent to the robot
		if (controller.shouldUpdateConfig()) {
			for (MotorPins motor: configuration.motors) {
				robot.send(new SetMotorPinsCommand(motor.id, motor.pwm, motor.left, motor.right));
			}
			robot.send(new SetPWMBounds(configuration.pwmBounds.min, configuration.pwmBounds.max));
		}

		{ // Code to move the robot forward
			int forwardSpeed = (int) controller.throttleValue() * 255;
			boolean forward = forwardSpeed > 0;
			forwardSpeed = forward ? forwardSpeed : -forwardSpeed;
			robot.send(new ControlMotorCommand(bottomBackLeftMotor, forward ? LEFT : RIGHT, forwardSpeed));
			robot.send(new ControlMotorCommand(bottomBackRightMotor, forward ? RIGHT : LEFT, forwardSpeed));
		}

		{ // Code to turn the robot left and right
			int turnSpeed = (int) controller.horizontalValue() * 255;
			boolean right = turnSpeed > 0;
			turnSpeed = right ? turnSpeed : -turnSpeed;
			robot.send(new ControlMotorCommand(bottomFrontLeftMotor, right ? LEFT : RIGHT, turnSpeed));
			robot.send(new ControlMotorCommand(bottomFrontRightMotor, right ? RIGHT : LEFT, turnSpeed));
		}

		{ // Code to move the robot up and down
			int upSpeed = (int) controller.verticalValue() * 255;
			boolean up = upSpeed > 0;
			upSpeed = up > upSpeed : -upSpeed;
			robot.send(new ControlMotorCommand(topFrontLeftMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topFrontRightMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topBackLeftMotor, up ? LEFT : RIGHT, upSpeed));
			robot.send(new ControlMotorCommand(topBackRightMotor, up ? LEFT : RIGHT, upSpeed));
		}

		robot.flush(); // Send all the batched commands

		// Read messages from the robot and dispatch them to the view
		for (Info info: robot.read()) {
			infoView.give(info);
		}
	}

	public void setController(Controller controller) {this.controller = controller;}
	public void setConfiguration(Configuration configuration) {this.configuration = configuration;}
	public void setRobot(Robot robot) {this.robot = robot;}
	public void setInfoView(InfoView infoView) {this.infoView = infoView;}
	
}