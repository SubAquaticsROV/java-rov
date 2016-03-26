package org.subaquatics.javarov;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component;
import net.java.games.input.Controller;

public class JoystickHandler implements Runnable { // Reads from a joystick and writes to the ROV
	
    private Controller controller;
	private IRobot robot;

	/**
	 * Allows the configuration to be mapped at runtime
	 */
	private HashMap<String, RobotActions> mapping;


    /*
    Robot state
    */
    int forwardSpeed;
    int turningSpeed;
    int upwardSpeed;
    boolean strafeLeft;
    boolean strafeRight;
    boolean openClaw;
    boolean closeClaw;
    boolean disableClaw;
    boolean disableClawJustPressed;

	public JoystickHandler(IRobot robot, Controller controller, String mappingFile) {
		this.robot = robot;
		this.controller = controller;
        mapping = new HashMap<String, RobotActions>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+mappingFile+".txt")));
            String line;
            Pattern p = Pattern.compile("^(\\w+)=(\\w+)$");
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    mapping.put(m.group(1), RobotActions.valueOf(m.group(2)));
                }
            }
            reader.close();
        } catch(Exception e) {
            System.out.println("Could not open joystick file! Using default configuration.");
            mapping.put("rx", RobotActions.TURN);
            mapping.put("y", RobotActions.FORWARD);
            mapping.put("z", RobotActions.ELEVATE);
            mapping.put("4", RobotActions.STRAFE_LEFT);
            mapping.put("5", RobotActions.STRAFE_LEFT);
        }
	}

	public void run()
	{
		EventQueue queue = controller.getEventQueue();
		Event event = new Event();
		boolean bumper = false;
		final float deadzone = 0.25f;
		while(true) {
			controller.poll();
			while(queue.getNextEvent(event)) {
                String name = event.getComponent().getIdentifier().getName();
                if(!mapping.containsKey(name)) {
                    continue;
                }
				switch(mapping.get(name)) {
                    case FORWARD:
                        forwardSpeed = (int) (event.getValue()*255);
                        break;
                    case TURN:
                        turningSpeed = (int) (event.getValue()*255);
                        break;
                    case ELEVATE:
                        upwardSpeed = (int) (event.getValue()*255);
                        break;
                    case STRAFE_LEFT:
                        strafeLeft = event.getValue()>=0.5;
                        break;
                    case STRAFE_RIGHT:
                        strafeRight = event.getValue()>=0.5;
                        break;
                    case OPEN_CLAW:
                        openClaw = event.getValue()>=0.5;
                        break;
                    case CLOSE_CLAW:
                        closeClaw = event.getValue()>=0.5;
                        break;
                    case DISABLE_CLAW:
                        if (event.getValue()>=0.5) {
                            disableClawJustPressed = true;
                        }
                        break;
				}
			}

            // !!!!! START OF CONTROLLER LOGIC !!!!!

            if(!strafeLeft && !strafeRight) { // Regular driving
                if (forwardSpeed > 0) { // Move forward
                    robot.controlMotor(3, 2, forwardSpeed);
                    robot.controlMotor(4, 1, forwardSpeed);
                } else if (forwardSpeed < 0) { // Move backward
                    robot.controlMotor(3, 1, -forwardSpeed);
                    robot.controlMotor(4, 2, -forwardSpeed);
                } else {
                    robot.controlMotor(3, 0, 0);
                    robot.controlMotor(4, 0, 0);
                }

                if (turningSpeed > 0) { // Turn right
                    robot.controlMotor(1, 1, turningSpeed);
                    robot.controlMotor(2, 1, turningSpeed);
                } else if (turningSpeed < 0) { // Turn left
                    robot.controlMotor(1, 2, -turningSpeed);
                    robot.controlMotor(2, 2, -turningSpeed);
                } else {
                    robot.controlMotor(1, 0, 0);
                    robot.controlMotor(2, 0, 0);
                }
            } else { // Strafing
                ;
            }


            if (upwardSpeed > 0) {
                robot.controlMotor(5, 1, upwardSpeed);
                robot.controlMotor(6, 1, upwardSpeed);
                robot.controlMotor(7, 1, upwardSpeed);
                robot.controlMotor(8, 1, upwardSpeed);
            } else if (upwardSpeed < 0) {
                robot.controlMotor(5, 2, -upwardSpeed);
                robot.controlMotor(6, 2, -upwardSpeed);
                robot.controlMotor(7, 2, -upwardSpeed);
                robot.controlMotor(8, 2, -upwardSpeed);
            } else {
                robot.controlMotor(5, 0, 0);
                robot.controlMotor(6, 0, 0);
                robot.controlMotor(7, 0, 0);
                robot.controlMotor(8, 0, 0);
            }

            if (!disableClaw) {
                if (openClaw && !closeClaw) {
                    robot.controlStepper(true);
                    robot.controlStepper(true);
                    robot.controlStepper(true);
                    robot.controlStepper(true);
                } else if(closeClaw && !openClaw) {
                    robot.controlStepper(false);
                    robot.controlStepper(false);
                    robot.controlStepper(false);
                    robot.controlStepper(false);
                }
            }

            if (disableClawJustPressed) {
                disableClawJustPressed = false;
                disableClaw = !disableClaw;
                robot.setStepperState(disableClaw);
            }

            // !!!!! END OF CONTROLLING LOGIC !!!!!

			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    private static enum RobotActions {
        FORWARD,
        TURN,
        ELEVATE,
        STRAFE_LEFT,
        STRAFE_RIGHT,
        OPEN_CLAW,
        CLOSE_CLAW,
        DISABLE_CLAW;
    }
}