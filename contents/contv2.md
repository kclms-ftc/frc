# TeamCode Source Guide v2

> **Updated:** 2026
> **Branch:** `hf-prime`

## how the code fits together

theres 4 layers. dont break this or the code gets messy.

1. **hardware mapping:** `HardwareMapConfig.java` has all the motors and servos. if we change a port we only change it here
2. **subsystems:** `Drivetrain`, `Intake`, etc. this is where all the math and pid loops go
3. **robot container:** `Robot.java` holds all the subsystems so we dont have to make them over and over
4. **opmodes:** `TeleOpMain`, `AutoShooter`. this is what the drivers click start on. NEVER put `motor.setPower()` here. only call `robot.drivetrain.drive()` etc.

## files

### Drivetrain.java
mecanum drive with normal, precision (slow for scoring), and turbo modes. does all the normalisation so the math doesnt break when moving sideways and turning at the same time.

### Intake.java
spins the roller to eat balls or spit them out. 3 simple states: off, intake, reverse.

### Vision.java
apriltag camera stuff.
- 640x480 resolution so it doesnt lag
- locks exposure to 50ms so there's no motion blur when driving fast
- turns off live preview during auto to save cpu

### Turret.java
pd loop that spins the motor to pointing straight at an apriltag.
has soft limits at -600 and +600 ticks so the cables dont rip.

### Shooter.java
pidf velocity control for the flywheels.
we use pidf because `setPower()` gets weaker when the batery dies, which makes the balls shoot lower. with pidf we always hit the goal.
has a little servo feeder that pushes the balls into the wheels. make sure the delay is long enough or the balls get stuck.

### OpModes
- `TeleOpMain.java`: competition driver code
- `AutoShooter.java`: 6 step autonomous that tracks the tag, aims, waits for the flywheels to spin up, and shoots the balls.
- tests folder: a bunch of diagnostic scripts so we can tune PIDs without running the whole teleop loop

## driver controls

### gamepad 1 (driver)
- sticks: standard mecanum driving
- right bumper: precision mode (30% speeed)
- left bumper: turbo mode

### gamepad 2 (operator)
- dpad up: intake balls
- dpad down: eject
- dpad left/right: stop intake
- Y: toggle auto aim (tracks tag 20)
- right stick X: manual turret spinning (only works if auto aim is off)
- left bumper: toggle flywheels
- right bumper: shoot a ball (wont work unless flyhweels are at speed)

## what's next

- put anti jam logic (from `IntakeTest`) into the main `Intake` class
- add deadzones because stick drift makes the robot slide when were not touching anything
- wire up the angle servo for turret pitch
- make a regular auto mode (`AutoMain`) since we only have the shooting one right now
