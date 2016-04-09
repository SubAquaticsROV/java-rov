
package org.subaquatics.javarov;

import java.io.OutputStream;
import java.io.IOException;

public class Robot implements IRobot
{

	OutputStream out;

	public Robot(OutputStream out) {
		this.out = out;
	}

	private void wrap(int[] data) {
		try {
			int crc = 0;
			for (int i=0; i < data.length; i++) {
				out.write(data[i]);
				crc ^= data[i];
			}
			out.write(crc);
		} catch(IOException e) {
			System.out.println("Error writing to robot.");
		}
	}
	
	// MOTORS
	@Override
	public synchronized void configureMotorPWMBounds(int min, int max) {
		wrap(new int[] {
			0x12,
			min & 0xFF,
			max & 0xFF
		});
	}

	@Override
	public synchronized void configureMotorPins(int motorId, int pwmPin, int aPin, int bPin) {
		wrap(new int[] {
			0x10,
			(motorId & 0xF)<<4 | (pwmPin & 0xF),
			aPin & 0xFF,
			bPin & 0xFF
		});
	}

	@Override
	public synchronized void controlMotor(int motorId, int flags, int pwm) {
		wrap(new int[] {
			0x11,
			(motorId & 0xF)<<4 | (flags & 0xF),
			pwm & 0xFF
		});
	}


	// STEPPER
	@Override
	public synchronized void configureStepperPins(int directionPin, int stepPin, int enablePin) {
		wrap(new int[] {
			0x20,
			directionPin & 0xFF,
			stepPin & 0xFF,
			enablePin & 0xFF
		});
	}

	@Override
	public synchronized void controlStepper(boolean direction, boolean run) {
		wrap(new int[] {
			0x21,
			(direction ? 0x80 : 0x00) | (run ? 0x40 : 0x00)
		});
	}

	@Override
	public synchronized void setStepperState(boolean enabled) {
		wrap(new int[] {
			0x22,
			(enabled ? 0 : 1)
		});
	}


	// !!!!! SENSORS !!!!!
	@Override
	public synchronized void setSensorState(int sensor, int state) {
		wrap(new int[] {
			0x30,
			sensor & 0xFF,
			state & 0xFF
		});
	}

	@Override
	public synchronized void setVoltageSensorPin(int pin) {
		wrap(new int[] {
			0x31,
			pin & 0xFF
		});
	}

	@Override
	public synchronized void setTemperatureSensorPin(int pin) {
		wrap(new int[] {
			0x32,
			pin & 0xFF
		});
	}

	@Override
	public synchronized void setDepthSensorDensity(int density) {
		wrap(new int[] {
			0x33,
			(density >> 24) & 0xFF,
			(density >> 16) & 0xFF,
			(density >> 8) & 0xFF,
			(density) & 0xFF
		});
	}


	// !!!!! CAMERAS !!!!!
	@Override
	public synchronized void setCameraPins(int pa, int re, int ci, int vo, int mu, int xa, int ze, int bi) {
		wrap(new int[] {
			0x40,
			pa & 0xFF,
			re & 0xFF,
			ci & 0xFF,
			vo & 0xFF,
			mu & 0xFF,
			xa & 0xFF,
			ze & 0xFF,
			bi & 0xFF
		});
	}

	@Override
	public synchronized void switchCamera(boolean multiplexer, int camera) {
		int flags = 0;
		flags |= multiplexer ? 1 : 0;
		wrap(new int[] {
			0x41,
			((flags & 0xF)<<4) | (camera & 0xF)
		});
	}


	// !!!!! MISC !!!!!

	@Override
	public synchronized void echo(int byteInt) {
		wrap(new int[] {
			0xF0,
			byteInt & 0xFF
		});
	}

	@Override
	public synchronized void version() {
		wrap(new int[] {
			0xF1
		});
	}

}