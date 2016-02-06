package org.subaquatics.javarov;

import java.util.ArrayList;

public class Configuration {

	public ArrayList<Motor> motors;
	public Range pwmBounds;

	public class MotorPins {
		public int id; // The id of the motor
		public int pwm; // The pwm pin
		public int left; // The turn "left" pin
		public int right; // The turn "right" pin
	}

	public class Range {
		public int min;
		public int max;
	}

}