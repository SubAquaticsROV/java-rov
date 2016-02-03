package org.subaquatics.javarov.actions;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component;

public class ControllerActions {

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