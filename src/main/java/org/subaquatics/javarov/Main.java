
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
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if(portIdentifier.isCurrentlyOwned())
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
			if (commPort instanceof SerialPort)
			{
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				running = true;

				(new Thread(new SerialReader(in))).start();
				(new Thread(new SerialWriter(out))).start();
			}
			else
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public static class SerialReader implements Runnable
	{
		InputStream in;

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

	public static class SerialWriter implements Runnable
	{
		OutputStream out;

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
				IRobot bot = new Robot(this.out);
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

		public JoystickWriter(Controller controller)
		{
			this.controller = controller;
		}

		public void run()
		{
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

			public void move_xy(double x, double y)
			{
				double length = Math.sqrt(x*x+y*y);
		        double direction = Math.atan2(y, x)+(PI/4); // leave it in radians
		        direction = direction * 180 / Math.PI;
		        int way = Math.floor(direction/90);
		        if (way > 4)
		        {
		            way -= 4;
		        }
		        if (way < 0)
		        {
		            way += 4;
		        }
		        int P_X = 1;
		        int P_X = 1;
		        int P_X = 1;
		        int P_X = 1;
		        switch(way)
		        {
		            case 3: // Going forward
		            case 1: // Going back
		            {
		                robot.controlMotor(context, P_X, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
		                robot.controlMotor(context, N_X, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
		                robot.controlMotor(context, P_Y, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
		                robot.controlMotor(context, N_Y, way==3, way==1, (int) abs(y/JOYSTICK_REDUCTION_VALUE));
		            } break;
		            case 0: // Going right
		            case 2: // Going left
		            {
		                robot.controlMotor(context, P_X, way==0, way==2, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 2
		                robot.controlMotor(context, N_X, way==0, way==2, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 2
		                robot.controlMotor(context, P_Y, way==2, way==0, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 0
		                robot.controlMotor(context, N_Y, way==2, way==0, (int) abs(x/JOYSTICK_REDUCTION_VALUE)); // 4
		            } break;
        }
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