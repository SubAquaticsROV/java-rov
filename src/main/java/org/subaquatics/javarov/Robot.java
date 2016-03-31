
package org.subaquatics.javarov;

import java.io.OutputStream;
import java.io.IOException;

public class Robot implements IRobot
{

	OutputStream out;

	public Robot(OutputStream out) {
		this.out = out;
	}
	
	// MOTORS
	@Override
	public synchronized void configureMotorPWMBounds(int min, int max) {
		try {
			out.write(0x12); // The PWM bounds command id
			out.write(min);
			out.write(max);
		} catch(IOException e) {
			System.out.println("Error writing to robot");
		}
	}

	@Override
	public synchronized void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin) {
		try {
			out.write(0x10);
			out.write((motorId & 0xF)<<4 | (pwmPin & 0xF));
			out.write(aPin & 0xFF);
			out.write(bPin & 0xFF);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void controlMotor(int motorId, int flags, int pwm) {
		try {
			out.write(0x11);
			out.write((motorId & 0xF)<<4 | (flags & 0xF));
			out.write(pwm & 0xFF);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}


	// STEPPER
	@Override
	public synchronized void configureStepperPins(int directionPin, int stepPin, int enablePin) {
		try {
			out.write(0x20);
			out.write(directionPin & 0xFF);
			out.write(stepPin & 0xFF);
			out.write(enablePin & 0xFF);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void controlStepper(boolean direction, int amount) {
		try {
			out.write(0x21);
			out.write((direction ? 0x80 : 0x00) | (amount & 0x7F));
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void setStepperState(boolean enabled) {
		try {
			out.write(0x22);
			out.write(enabled ? 0 : 1);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}


	// !!!!! SENSORS !!!!!
	@Override
	public synchronized void setSensorState(int sensor, int state) {
		try {
			out.write(0x30);
			out.write(sensor);
			out.write(state);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void setVoltageSensorPin(int pin) {
		try {
			out.write(0x31);
			out.write(pin);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void setTemperatureSensorPin(int pin) {
		try {
			out.write(0x32);
			out.write(pin);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}


	// !!!!! CAMERAS !!!!!
	@Override
	public synchronized void setCameraPins(int pa, int re, int ci, int vo, int mu, int xa, int ze, int bi) {
		try {
			out.write(0x40);
			out.write(pa);
			out.write(re);
			out.write(ci);
			out.write(vo);
			out.write(mu);
			out.write(xa);
			out.write(ze);
			out.write(bi);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void switchCamera(boolean multiplexer, int camera) {
		try {
			out.write(0x41);
			int flags = 0;
			flags |= multiplexer ? 1 : 0;
			out.write(((flags & 0xF)<<4) | (camera & 0xF));
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}


	// !!!!! MISC !!!!!

	@Override
	public synchronized void echo(int byteInt) {
		try {
			out.write(0xF0);
			out.write(byteInt & 0xFF);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

	@Override
	public synchronized void version() {
		try {
			out.write(0xF1);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}

}