package org.subaquatics.javarov.devices;

public interface InputDevice {

	// Update the controllers values
	public void update();

	// Should the robot be sent the config?
	public boolean shouldUpdateConfig();

	// How fast should the ROV be moving forawrd?
	public double throttleValue();

	// How fast should the robot be moving horizontally?
	// Later this will either mean turning or strafing, but for now it just
	// means turning.
	public double horizontalValue();

	// How fast should the robot be moving up and down?
	public double verticalValue();
	
}