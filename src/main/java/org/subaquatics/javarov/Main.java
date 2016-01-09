
package org.subaquatics.javarov;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Scanner;

import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component;

public class Main
{

	public static boolean running;

	public void connect(String portName) throws Exception
	{
	    //Calling RXTX library to get the port the arduino is on - assumed to be
	    //"COM3" in this instance
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if(portIdentifier.isCurrentlyOwned()) //Makes sure com port is not being used by anything (such as the Arduino IDE)
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
		    //Connects to the port "COM3"
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
			if (commPort instanceof SerialPort) //Makes sure it is a serial port
			{
			    //Casting commPort from type CommPort to serialPort of type SerialPort
				SerialPort serialPort = (SerialPort) commPort;
				//Configure the port so it can communicate with the arduino (note the baud rate, 9600 in this case)
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                //Create an input stream (similar to System.in) that connects to serialPort
				InputStream in = serialPort.getInputStream();
				//Create an output stream (similar to System.out) that connects to serialPort
				OutputStream out = serialPort.getOutputStream();

				running = true;

                
                //Create new threads
                //This allows full-duplex (two-way) communication
                //The in and out streams are passed in
				(new Thread(new SerialReader(in))).start();
				(new Thread(new SerialWriter(out))).start();
			}
			else //If it is not a serial port, say so
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

    //Inner class declaration
    //Uses the runnable interface to allow for multi-threading
    //Multi-threading = running multiple things simultaneously
	public static class SerialReader implements Runnable
	{
		InputStream in;
        
        //Constructor
		public SerialReader(InputStream in)
		{
			this.in = in;
		}
        
		public void run()
		{
			byte[] buffer = new byte[1024];
			int len = -1;
			try
			{
				while( (len=this.in.read(buffer)) > -1 && running)
				{
					System.out.print(new String(buffer,0,len));
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
    
    //Inner class declaration
    //Uses the runnable interface to allow for multi-threading
    //Multi-threading = running multiple things simultaneously
	public static class SerialWriter implements Runnable
	{
		OutputStream out;
    
        //Constructor
		public SerialWriter(OutputStream out)
		{
			this.out = out;
		}

		public void run()
		{
			byte[] buffer = new byte[1024];
			int len = -1;
			try
			{
				Scanner input = new Scanner(System.in);
				IRobot bot = new BufferedRobot(this.out);
				while( running )
				{
					switch(input.next())
					{
						case "echo":
						{
							String echoString = input.next();
							for(int i=0; i < echoString.length(); i++)
							{
								bot.echo((int)echoString.charAt(i));
							}
							break;
						}
						case "configureMotorPWMBounds":
						{
							bot.configureMotorPWMBounds(
								input.nextInt(),
								input.nextInt()
								);
							break;
						}
						case "configureMotorPins":
						{
							bot.configureMotorPins(
								input.nextInt(),
								input.nextInt(),
								input.nextInt(),
								input.nextInt()
								);
							break;
						}
						case "controlMotor":
						{
							bot.controlMotor(
								input.nextInt(),
								input.nextInt(),
								input.nextInt()
								);
							break;
						}
						case "listControllers":
						{
							Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
							for(int i=0; i<controllers.length; i++)
							{
								System.out.println(i + ": " + controllers[i].getName() + ", " + controllers[i].getType());
							}
							break;
						}
						case "startController":
						{
							int i = input.nextInt();
							Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
							if(i > controllers.length)
							{
								System.out.println("Invalid controller number \""+i+"\".");
								System.out.println("List available controllers with \"listControllers\".");
							}
							else
							{
								new Thread(new JoystickWriter(bot, controllers[i])).start();
								System.out.println("Starting controller listening thread.");
							}
							break;
						}
						case "exit":
						{
							running = false;
							break;
						}
						default:
						{
							System.out.println("Unknown command.");
						}
					}
					input.nextLine();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	public static class JoystickWriter implements Runnable // Reads from a joystick and writes to the ROV
	{
		IRobot robot;
		Controller controller;

		public JoystickWriter(IRobot robot, Controller controller)
		{
			this.robot = robot;
			this.controller = controller;
		}

		public void run()
		{
			EventQueue queue = controller.getEventQueue();
			Event event = new Event();
			boolean bumper = false;
			final float deadzone = 0.25f;
			while(running)
			{
				controller.poll();
				while(queue.getNextEvent(event))
				{
					switch(event.getComponent().getIdentifier().getName())
					{
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

				((BufferedRobot)robot).update();

				try
				{
					Thread.sleep(20);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		public void move_y(double y)
		{
			y *= 255;

        	if (y > 0) {
	            robot.controlMotor(3, 2, (int) y);
	            robot.controlMotor(4, 1, (int) y);
        	} else if (y < 0) {
	            robot.controlMotor(3, 1, (int) -y);
	            robot.controlMotor(4, 2, (int) -y);
        	} else {
	            robot.controlMotor(3, 0, 0);
	            robot.controlMotor(4, 0, 0);
        	}
        }

        public void rotate_z(double z) {
        	int value = (int) (z *255);
        	int one = 1;
        	int two = 2;
        	if (value > 0) {
        		robot.controlMotor(one, 1, value);
        		robot.controlMotor(two, 1, value);
        	} else if (value < 0) {
        		robot.controlMotor(one, 2, -value);
        		robot.controlMotor(two, 2, -value);
        	} else {
        		robot.controlMotor(one, 0, 0);
        		robot.controlMotor(two, 0, 0);
        	}
        }

        public void move_z(double z) {
        	int value = (int) (z *255);
        	int m1 = 5;
        	int m2 = 6;
        	int m3 = 7;
        	int m4 = 8;
        	if (value > 0) {
        		robot.controlMotor(m1, 1, value);
        		robot.controlMotor(m2, 1, value);
        		robot.controlMotor(m3, 1, value);
        		robot.controlMotor(m4, 1, value);
        	} else if (value < 0) {
        		robot.controlMotor(m1, 2, -value);
        		robot.controlMotor(m2, 2, -value);
        		robot.controlMotor(m3, 2, -value);
        		robot.controlMotor(m4, 2, -value);
        	} else {
        		robot.controlMotor(m1, 0, 0);
        		robot.controlMotor(m2, 0, 0);
        		robot.controlMotor(m3, 0, 0);
        		robot.controlMotor(m4, 0, 0);
        	}
        }

        public void strafe(double x) {
        	int value = (int) (x * 255);
        	if (value > 0) {
        		robot.controlMotor(1, 1, value);
        		robot.controlMotor(2, 1, value);
        		robot.controlMotor(3, 1, value);
        		robot.controlMotor(4, 1, value);
        	} else if (value < 0) {
        		robot.controlMotor(1, 2, -value);
        		robot.controlMotor(2, 2, -value);
        		robot.controlMotor(3, 2, -value);
        		robot.controlMotor(4, 2, -value);
        	} else {
        		robot.controlMotor(1, 0, 0);
        		robot.controlMotor(2, 0, 0);
        		robot.controlMotor(3, 0, 0);
        		robot.controlMotor(4, 0, 0);
        	}
        }
	}

	public static void main(String[] args)
	{
		try
		{
			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements() )
			{
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				System.out.println(portIdentifier.getName());
			}
			System.out.print("Tpye in COM port you would like: ");
			Scanner input = new Scanner(System.in);
			String portIdentifier = input.next();
			(new Main()).connect(portIdentifier);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
