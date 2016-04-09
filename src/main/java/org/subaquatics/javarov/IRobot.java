
package org.subaquatics.javarov;

public interface IRobot
{
	// Motors
	public void configureMotorPWMBounds(int min, int max);
	public void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin);
	public void controlMotor(int motorId, int flags, int pwm);

	// Stepper
	public void configureStepperPins(int directionPin, int stepPin, int enablePin);
	public void controlStepper(boolean direction, int amount);
	public void setStepperState(boolean enabled);

	// Sensors
	public void setSensorState(int sensor, int state);
	public void setVoltageSensorPin(int pin);
	public void setTemperatureSensorPin(int pin);
	public void setDepthSensorDensity(int density);

	// Cameras
	public void setCameraPins(int pa, int re, int ci, int vo, int mu, int xa, int ze, int bi);
	public void switchCamera(boolean multiplexer, int camera);

	// Misc
	public void echo(int byteInt);
	public void version();
	
}