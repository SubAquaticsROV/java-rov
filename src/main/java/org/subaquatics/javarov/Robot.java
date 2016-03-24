
package org.subaquatics.javarov;

import java.io.OutputStream;
import java.io.IOException;

public class Robot implements IRobot
{

	OutputStream out;

	public Robot(OutputStream out)
	{
		this.out = out;
	}
	
	@Override
	public void configureMotorPWMBounds(int min, int max)
	{
		try
		{
			out.write(0x12); // The PWM bounds command id
			out.write(min);
			out.write(max);
		}
		catch(IOException e)
		{
			System.out.println("Error writing to robot");
		}
	}

	@Override
	public void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin)
	{
		try
		{
			out.write(0x10);
			out.write((motorId & 0xF)<<4 | (pwmPin & 0xF));
			out.write(aPin & 0xFF);
			out.write(bPin & 0xFF);
		}
		catch(IOException e)
		{
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public void controlMotor(int motorId, int flags, int pwm)
	{
		try
		{
			out.write(0x11);
			out.write((motorId & 0xF)<<4 | (flags & 0xF));
			out.write(pwm & 0xFF);
		}
		catch(IOException e)
		{
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public void echo(int byteInt)
	{
		try
		{
			out.write(0xF0);
			out.write(byteInt & 0xFF);
		}
		catch(IOException e)
		{
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public void version() {
		try {
			out.write(0xF1);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

}