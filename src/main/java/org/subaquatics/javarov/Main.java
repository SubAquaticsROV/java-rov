
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
				while( (len=this.in.read(buffer)) > -1 )
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
				boolean running = true;
				Scanner input = new Scanner(System.in);
				IRobot bot = new Robot(this.out);
				while( running )
				{
					System.out.print("> ");
					switch(input.next())
					{
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
								new Thread(new JoystickWriter(controllers[i])).start();
								System.out.println("Starting controller listening thread.");
							}
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

		public JoystickWriter(Controller controller)
		{
			this.controller = controller;
		}

		public void run()
		{
			boolean running = true;
			EventQueue queue = controller.getEventQueue();
			Event event = new Event();
			while(running)
			{
				controller.poll();
				while(queue.getNextEvent(event))
				{
					StringBuffer buffer = new StringBuffer();
					buffer.append(controller.getName());
					buffer.append("\t");
					Component component = event.getComponent();
					buffer.append(component.getIdentifier().getName());
					buffer.append("\t");
					buffer.append(event.getValue());
					System.out.println(buffer.toString());
				}

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
	}

	public static void main(String[] args)
	{
		try
		{
		    //Anonymous object instantiation
		    //Why?
			(new Main()).connect("COM3");
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}