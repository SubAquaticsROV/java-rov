
package org.subaquatics.javarov;

public interface IRobot
{
	
	public void configureMotorPWMBounds(int min, int max);
	public void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin);
	public void controlMotor(int motorId, int flags, int pwm);

	public void configureStepperPins(int directionPin, int stepPin, int enablePin);
	public void controlStepper(boolean direction);
	public void setStepperState(boolean enabled);

	public void echo(int byteInt);
	public void version();
	
}