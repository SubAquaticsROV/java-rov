package org.subaquatics.javarov.actions;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component;

public class ControllerActions {

	public static interface ConnectedCallback {
		/**
		 * If there was an exception while connecting to the robot, it is passed
		 * in as error. Otherwise, error is null.
		 */
		public void connected(Controller controller, Exception error);
	}

	public static void connect(String controllerName, ConnectedCallback callback) {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		Controller controller = null;
		for (Controller c: controllers) {
			if (c.getName() == controllerName) {
				controller = c;
				break;
			}
		}
		if (controller!=null) {
			callback.connected(controller, null);
		} else {
			callback.connected(null, new Exception("Error: No controller named "+controllerName));
		}
	}

	public static interface GetControllerCallback {
		public void gotControllers(String[] controllerNames);
	}

	public static void getControllers(GetControllerCallback callback) {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		String[] names = new String[controllers.length];
		for(int i=0; i<controllers.length; i++) {
			names[i] = controllers[i].getName();
		}
		callback.gotControllers(names);
	}
}