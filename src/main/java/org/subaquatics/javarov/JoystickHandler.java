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
    private boolean running;


    /*
    Robot state
    */
    int forwardSpeed;
    int turningSpeed;
    int upwardSpeed;
    int strafe;
    int camera;
    boolean cameraJustChanged;
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
        running = true;
		EventQueue queue = controller.getEventQueue();
		Event event = new Event();
		while(running) {
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
                        if (event.getValue()>=0.1) {
                            strafe = (int) (-event.getValue()*255);
                        } else {
                            strafe = 0;
                        }
                        break;
                    case STRAFE_RIGHT:
                        if (event.getValue()>=0.1) {
                            strafe = (int) (event.getValue()*255);
                        } else {
                            strafe = 0;
                        }
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
                    case CAMERA_DPAD:
                        if (event.getValue() == 1.0) {
                            camera = 12;
                        } else if (event.getValue() == 0.25) {
                            camera = 13;
                        } else if (event.getValue() == 0.50) {
                            camera = 14;
                        } else if (event.getValue() == 0.75) {
                            camera = 15;
                        }
                        cameraJustChanged = true;
                        break;
				}
			}

            // !!!!! START OF CONTROLLER LOGIC !!!!!

            if (turningSpeed > 10) {
                robot.controlMotor(1, 2, turningSpeed);
                robot.controlMotor(2, 1, turningSpeed);
                robot.controlMotor(3, 1, turningSpeed);
                robot.controlMotor(4, 2, turningSpeed);
            } else if (turningSpeed < -10) {
                robot.controlMotor(1, 1, -turningSpeed);
                robot.controlMotor(2, 2, -turningSpeed);
                robot.controlMotor(3, 2, -turningSpeed);
                robot.controlMotor(4, 1, -turningSpeed);
            }

            if (forwardSpeed > 10) {
                robot.controlMotor(1, 2, forwardSpeed);
                robot.controlMotor(2, 2, forwardSpeed);
                robot.controlMotor(3, 1, forwardSpeed);
                robot.controlMotor(4, 1, forwardSpeed);
            } else if (forwardSpeed < -10) {
                robot.controlMotor(1, 1, -forwardSpeed);
                robot.controlMotor(2, 1, -forwardSpeed);
                robot.controlMotor(3, 2, -forwardSpeed);
                robot.controlMotor(4, 2, -forwardSpeed);
            }

            if (upwardSpeed > 10) {
                robot.controlMotor(5, 1, upwardSpeed);
                robot.controlMotor(6, 1, upwardSpeed);
                robot.controlMotor(7, 1, upwardSpeed);
                robot.controlMotor(8, 1, upwardSpeed);
            } else if (upwardSpeed < -10) {
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

            if (strafe > 10) {
                robot.controlMotor(1, 1, strafe);
                robot.controlMotor(2, 2, strafe);
                robot.controlMotor(3, 1, strafe);
                robot.controlMotor(4, 2, strafe);
            } else if (strafe < -10) {
                robot.controlMotor(1, 2, -strafe);
                robot.controlMotor(2, 1, -strafe);
                robot.controlMotor(3, 2, -strafe);
                robot.controlMotor(4, 1, -strafe);
            }
            
            if ((strafe > -10 && strafe < 10) &&
                (forwardSpeed > -10 && forwardSpeed < 10) &&
                (turningSpeed > -10 && turningSpeed < 10)) {
                robot.controlMotor(1, 0, 0);
                robot.controlMotor(2, 0, 0);
                robot.controlMotor(3, 0, 0);
                robot.controlMotor(4, 0, 0);
            }

            if (!disableClaw) {
                if (openClaw && !closeClaw) {
                    robot.controlStepper(true,true);
                } else if(closeClaw && !openClaw) {
                    robot.controlStepper(false,true);
                } else {
                    robot.controlStepper(false, false);
                }
            }

            if (disableClawJustPressed) {
                disableClawJustPressed = false;
                disableClaw = !disableClaw;
                robot.setStepperState(disableClaw);
            }

            if (cameraJustChanged) {
                cameraJustChanged = false;
                robot.switchCamera(false, camera);
            }

            // !!!!! END OF CONTROLLING LOGIC !!!!!

			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
        System.out.println("Joystick thread stopped.");
	}

    public void stop() {
        //synchronized (running) {
            running = false;
        //}
    }

    private static enum RobotActions {
        FORWARD,
        TURN,
        ELEVATE,
        STRAFE_LEFT,
        STRAFE_RIGHT,
        OPEN_CLAW,
        CLOSE_CLAW,
        DISABLE_CLAW,
        CAMERA_DPAD;
    }
}