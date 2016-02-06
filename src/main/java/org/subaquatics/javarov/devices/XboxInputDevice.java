package org.subaquatics.javarov.devices;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class XboxInputDevice implements InputDevice {

	private Controller controller;

	private boolean shouldUpdate;
	private double throttle;
	private double horizontal;
	private double vertical;

	public XboxInputDevice(String controllerName) {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller c: controllers) {
			if (c.getName().equals(controllerName)) {
				controller = c;
			}
		}
	}

	public void update() {
		EventQueue queue = controller.getEventQueue();
		Event event = new Event();
		controller.poll();
		while(queue.getNextEvent(event)) {
			switch(event.getComponent().getIdentifier().getName()) {
				/*case "x": // Left joystick
					break;
				case "y": // Left joystick
					break;
				case "z": // Triggers
					break;*/
				case "rx": // Right joystick
					horizontal = event.getValue();
					break;
				case "ry": // Right joystick
					vertical = event.getValue();
					break;
				case "4": // Left bumper
					throttle += 0.05;
					if (throttle > 1) {
						throttle = 1;
					}
					break;
				case "5": // Right bumper
					throttle -= 0.05;
					if (throttle < -1) {
						throttle = -1;
					}
					break;
				default:
					System.out.println(event.getComponent().getIdentifier().getName() + ": " + event.getValue());
			}
		}
	}

	public boolean shouldUpdateConfig() {return shouldUpdate;}
	public double throttleValue() {return throttle;}
	public double horizontalValue() {return horizontal;}
	public double verticalValue() {return vertical;}
	
}