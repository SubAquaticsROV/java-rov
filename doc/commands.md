Commands
========
Motors
======
These are the commands that relate to the motors

** `set_motor_pins` (0x10) **

This command tells the robot which pins are related to which motor

```
	0          4         8
0	+----------+---------+
	| Motor Id |   PWM   |
1	+----------+---------+
	|        Pin A       |
2	+----------+---------+
	|        Pin B       |
3	+----------+---------+
```

** `control_motor` (0x11) **

This command tells the robot to turn a motor left, right, or off.

```
	0          4         8
0	+----------+---------+
	| Motor Id |  Flags  |
1	+----------+---------+
	|         PWM        |
2	+----------+---------+
```

** `set_pwm_bounds` (0x12) **

This command tells the robot to limit the pwm values to a minimum
and a maximum.

```
	0          4         8
0	+----------+---------+
	|       Minimum      |
1	+----------+---------+
	|       Maximum      |
2	+----------+---------+
```

** `set_safety_timeout` (0x13) **

This command tells the robot how long it should wait before the
values of and and b flip. It is supposed to prevent a short condition
that can occur.

```
	0          4         8
0	+----------+---------+
	|  Timeout (millis)  |
1	+                    +
	|                    |
2	+----------+---------+
```

Servos
======
The gripper needed a command to control it.

** `set_gripper_pin` (0x20) **

This tells the arduino which pin the servo is on.

```
    0          4         8
0   +----------+---------+
    |       PWM Pin      |
1   +----------+---------+
```

** `control_gripper` (0x21) **

This tells the arduino to set the servo to a certain pwm.
Max: 150
Min: 36

```
    0          4         8
0   +----------+---------+
    |         PWM        |
1   +----------+---------+
```

Sensors
=======
These commands will allow you to configure and turn on sensor feedback.

** `set_sensor_pin`(0x30) **

Configures the sensor pin.

```
	0          4         8
0	+----------+---------+
	|      Sensor ID     |
1	+----------+---------+
	|        Pins        |
2	+----------+---------+
```

** `sensor_state` (0x31) **

Turns on the sensor and begins streaming it to the surface.

```
	0          4         8
0	+----------+---------+
	|      Sensor ID     |
1	+----------+---------+
	|        Mode        |
2	+----------+---------+
```

Cameras
=======
The cameras need their own commands! You use them to tell the ROV which pins
the multiplexer is connected to, and which camera is supposed to be on.

** `set_camera_pins` (0x40) **

This configures all of the multiplexer pins! The multiplexer requires the most
pins, as well as the most bytes to configure.

```
    0          4         8
0   +----------+---------+
    |        Pin 1       |
1   +----------+---------+
    |        Pin 2       |
2   +----------+---------+
    |        Pin 3       |
3   +----------+---------+
    |        Pin 4       |
4   +----------+---------+
```

** `switch_camera` (0x41) **

This tells the arduino to switch the camera.

```
    0          4         8
0   +----------+---------+
    |       Camera       |
1   +----------+---------+
```

Misc
====
These commands don't really fit into any category. These are mostly for testing.

** `echo` (0xF0) **

This command tells the arduino board to repeat the byte that follows to the computer.

```
	0          4         8
0	+----------+---------+
	|        Data        |
1	+----------+---------+
```
