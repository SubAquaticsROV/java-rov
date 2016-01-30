package org.subaquatics.javarov;

import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import org.subaquatics.javarov.commands.Command;
import org.subaquatics.javarov.commands.ControlMotorCommand;
import org.subaquatics.javarov.commands.ControlMotorCommand.MotorDirection;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class Joystick {

	Controller controller; // Where we listen to stuff from the ROV
	SendChannel<Command> robot; // A channel to send commands to the robot
    ReceiveChannel<Boolean> runningChannel; // A channel that indicates if the thread should die

	public Joystick(Controller controller, SendChannel<Command> robot, ReceiveChannel<Boolean> runningChannel) {
		this.controller = controller;
		this.robot = robot;
        this.runningChannel = runningChannel;
	}

	public void run() {
		EventQueue queue = controller.getEventQueue();
		Event event = new Event();
		boolean bumper = false;
        boolean running = true;
        Boolean runningMessage = null;
		final float deadzone = 0.25f;
		while(running) {
            try {
    			controller.poll();
    			while(queue.getNextEvent(event)) {
    				switch(event.getComponent().getIdentifier().getName()) {
    					case "x":
    						break;
    					case "y":
    						if(!bumper)
    							move_y(event.getValue());
    						break;
    					case "z":
    						move_z(event.getValue());
    						break;
    					case "rx":
    						if(!bumper)
    							rotate_z(event.getValue());
    						break;
    					case "ry":
    						break;
    					case "4": // Left bumper
    						strafe(-event.getValue());
    						bumper = event.getValue()>0;
    						break;
    					case "5": // Right bumper
    						strafe(event.getValue());
    						bumper = event.getValue()>0;
    						break;
    					default:
    						System.out.println(event.getComponent().getIdentifier().getName() + ": " + event.getValue());
    				}
    			}

    			Thread.sleep(20);
                // Check if the thread should stop
                Boolean message = runningChannel.tryReceive();
                if (message!=null) {
                    running = message;
                }
            } catch(ChannelClosedException e) {
                e.printStackTrace();
                running = false;
            }catch(InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
		}
	}

	public void move_y(double y) throws ChannelClosedException, InterruptedException {
		y *= 255;

    	if (y > 0) {
            robot.send(new ControlMotorCommand(3, MotorDirection.RIGHT, (int) y));
            robot.send(new ControlMotorCommand(4, MotorDirection.LEFT, (int) y));
        } else if (y < 0) {
            robot.send(new ControlMotorCommand(3, MotorDirection.LEFT, (int) -y));
            robot.send(new ControlMotorCommand(4, MotorDirection.RIGHT, (int) -y));
    	} else {
            robot.send(new ControlMotorCommand(3, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(4, MotorDirection.STOP, 0));
    	}
    }

    public void rotate_z(double z) throws ChannelClosedException, InterruptedException {
    	int value = (int) (z *255);
    	int one = 1;
    	int two = 2;
    	if (value > 0) {
            robot.send(new ControlMotorCommand(1, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(2, MotorDirection.LEFT, value));
    	} else if (value < 0) {
            robot.send(new ControlMotorCommand(1, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(2, MotorDirection.RIGHT, -value));
    	} else {
            robot.send(new ControlMotorCommand(1, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(2, MotorDirection.STOP, 0));
    	}
    }

    public void move_z(double z) throws ChannelClosedException, InterruptedException {
    	int value = (int) (z *255);
    	int m1 = 5;
    	int m2 = 6;
    	int m3 = 7;
    	int m4 = 8;
    	if (value > 0) {
            robot.send(new ControlMotorCommand(m1, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(m2, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(m3, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(m4, MotorDirection.LEFT, value));
    	} else if (value < 0) {
            robot.send(new ControlMotorCommand(m1, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(m2, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(m3, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(m4, MotorDirection.RIGHT, -value));
    	} else {
            robot.send(new ControlMotorCommand(m1, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(m2, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(m3, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(m4, MotorDirection.STOP, 0));
    	}
    }

    public void strafe(double x) throws ChannelClosedException, InterruptedException {
    	int value = (int) (x * 255);
    	if (value > 0) {
            robot.send(new ControlMotorCommand(1, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(2, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(3, MotorDirection.LEFT, value));
            robot.send(new ControlMotorCommand(4, MotorDirection.LEFT, value));
    	} else if (value < 0) {
            robot.send(new ControlMotorCommand(1, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(2, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(3, MotorDirection.RIGHT, -value));
            robot.send(new ControlMotorCommand(4, MotorDirection.RIGHT, -value));
    	} else {
            robot.send(new ControlMotorCommand(1, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(2, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(3, MotorDirection.STOP, 0));
            robot.send(new ControlMotorCommand(4, MotorDirection.STOP, 0));
    	}
    }

}