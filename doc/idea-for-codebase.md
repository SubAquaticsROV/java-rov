

Input:
- throttleValue(): double
- horizontalValue(): double
- horizontalMode(): {LATERAL, YAW}
- verticalValue(): double
- pitchValue(): double
- shouldUpdateConfig(): boolean

Configuration:
- motors: MotorPin[8]
	- id: int
	- leftPin: int
	- rightPin: int
	- pwmPin: int
- pwmSafetyBounds: Range
	- min: int
	- max: int
- writeToRobot(): void

Robot:
- sendCommand(command: Command): void
- readInfo(): Info //Should forward to view