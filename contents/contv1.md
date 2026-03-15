# TeamCode Source Summary v1

> **Branch:** `hf-prime`  

this was the early version of the code before we added the shooter and turret logic.

## folder structure

```
teamcode/
├── Drivetrain.java              
├── HardwareMapConfig.java       
├── Intake.java                  
├── Robot.java                   
├── TeleOpMain.java              
├── readme.md                    
│
├── testing/
│   ├── AprilTagUSB20Camera.java 
│   ├── DcMotorPractice.java     
│   ├── FlywheelTest.java        
│   ├── IntakeTest.java          
│   ├── MotorTest.java           
│   │
│   └── webcam_test/
```

## core classes

### HardwareMapConfig.java
central mapping so we dont have `hardwareMap.get` scattered across 10 files. if a wire changes, we only fix it here.

### Drivetrain.java
mecanum drive with math for normal, precision (30%), and turbo (100%) modes. 
motors on the left are reversed. all are set to brake so we dont slide past the balls.

### Intake.java
spins the roller to eat balls or spit them out. 
3 states: off, intake, reverse. 

## testing opmodes

### MotorTest.java
helper class to make testing random motors easier.

### DcMotorPractice.java
just spins a test motor at 50% power.

### FlywheelTest.java
velocity diagnostic. uses pidf `setVelocity()` instead of just `setPower()`.
hold the left trigger to spin up to 1800 ticks/sec. 

### IntakeTest.java
d-pad up toggles the intake, down reverses it. 
has a cool anti-jam feature where if the motor slows down, it auto-reverses for hald a second to spit the jammed ball out.

### AprilTagUSB20Camera.java
tests the tag detection using the standard usb camera. 
sets the resolution and forces the exposure to 50ms so there's no blur.

## webcam test suite
a bunch of java files inside `testing/webcam_test/` that run on a regular laptop to simulate apriltag detection. helps us write the math without needing the actual robot turned on.

## what we planned (from readme)

- **Drivetrain:** done
- **Intake:** done, but anti-jam needs to be moved out of the test file
- **Turret:** not started yet
- **Shooter:** not started yet
- **Vision:** camera test works, but the main subsystem isnt written
- **TeleOpMain:** done, mapping driver sticks to the subsystems
- **Robot.java:** done, holds everything together
