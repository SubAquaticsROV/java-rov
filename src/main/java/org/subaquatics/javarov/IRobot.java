
package org.subaquatics.javarov;

public interface IRobot
{
	
	public void configureMotorPWMBounds(int min, int max);
	public void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin);
	public void controlMotor(int motorId, int flags, int pwm);
	public void echo(int byteInt);
	public void version();
	
}