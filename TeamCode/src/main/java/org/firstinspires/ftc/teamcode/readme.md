# TeamCode Dev Plan

this is the roadmap for the teamcode folder. push this to github every few days.

## who's doing what
- **`Drivetrain.java`** (KAVAN + DANIEL): mecanum math, field-centric mode, and slowing down for precision.
- **`Intake.java`** (JESS): the rollers that eat the balls. needs a simple in, out, and off button. make sure it reverses automatically if it gets stuck.
- **`Turret.java`** (MOYIN + KISHI): spin the turret_motor to point at the apriltag, and use the angle_servo to point it up and down.
- **`Shooter.java`** (GERGANA): two flywheels (shooter_motor_0 and shooter_motor_1) and a feeder_servo that pushes the balls in.
- **`Vision.java`** (GEORGE): apriltag pipeline to tell the turret where to point.
- **`TeleOpMain.java`** (TEAM): tie everything to the gamepads.
- **`AutoMain.java`** (TEAM): the auto routines for driving and scoring.

## hardware map rules
check `HardwareMapConfig.java` to see what names to use. if you use `hardwareMap.get("left_motor")` and it isnt in there it will crash the whole OpMode.

- wheels: `wheel_0` (NE), `wheel_1` (SE), `wheel_2` (SW), `wheel_3` (NW) 
- turret: `turret_motor` and `angle_servo`
- intake: `intake_motor`
- shooter: `shooter_motor_0`, `shooter_motor_1`, `feeder_servo`
- camera is literally just called `"webcam"`

## how things should work

### drivetrain
- telehop: mecanum drive with a slow mode (precision) and fast mode (turbo). use the gyro to make the robot drive straight if the sticks get pushed weirdly. we want brake mode on the motors so we dont slide past the balls.
- auto: `driveDistance(cm)` and `turnToAngle(deg)`.

### intake
- hit a button to toggle between intake, eject, and off. 
- if the motor slows down abruptly, reverse it to spit out the jammed ball.

### turret
- needs manual controls but the main focus is locking onto an apriltag so we can score automatically. limit the rotation so we dont rip out the cables.
- ideally we can press a button to snap it to the sides or front.

### shooter
- turn it on and keep it spinning with pidf control (no `setPower`!).
- the feeder servo should click the balls into the wheel.
- `spinUpAndShoot(n)` for auto so it waits for the rpm to get high enough.

### vision
- just passing data to the turret and printing to telemetry. no motor stuff here.

## the golden rule

1. `HardwareMapConfig` matches the real robot
2. Subsystems have the heavy lifting
3. `Robot.java` packages all the subsystems
4. OpModes use `Robot.java` methods

**DO NOT PUT `motor.setPower()` IN `TeleOpMain.java`.**  use `robot.intake.intakeIn()` instead. keep all the logic organized.
