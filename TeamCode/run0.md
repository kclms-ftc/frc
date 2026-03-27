# TeamCode Reliability Analysis & Pre-Test Report

This document outlines potential failure points, bugs, inconsistencies, and testing mishaps found in the main `TeamCode` directory codebase (excluding the `gamma` directory). Reviewing and addressing these points prior to testing will prevent crashes, infinite loops, and unexpected hardware behavior.

---

## 🚨 Critical Bugs (Will Cause Crashes or Hangs)

**1. NullPointerException in Autonomous (`Auto.java` -> `Shooter.java`)**
* **The Issue:** In `Auto.java`, line 40 calls `shooter.loop(null);` while waiting for the shooting sequence to finish:
  ```java
  while (shooter.shootingCurrently && opModeIsActive()) {
      shooter.loop(null); // we don't need gamepad input for auto
      idle();
  }
  ```
  However, in `Shooter.java`, the `loop()` method immediately attempts to access `gp.x` (line 45):
  ```java
  public void loop(Gamepad gp) {
      if(gp.x && !lastX) { ... }
  ```
* **The Failure:** Because `gp` is passed as `null`, attempting to read `gp.x` will instantly throw a `NullPointerException` and crash the Auto program.
* **The Fix:** Add a null check in `Shooter.java`'s loop: `if (gp != null && gp.x && !lastX)`, or split the state machine advancement logic away from the input-reading logic.

**2. Infinite Loop in Autonomous due to Hardcoded Drivetrain Targets**
* **The Issue:** `Auto.java` uses `moveDistance()` to create a target X, Y, and Heading, then loops while `!drivetrain.targetReached()`. 
* **The Failure:** In `Drivetrain.java`, the `targetReached()` method calculates distance based on **hardcoded class properties** (`private double targetX = 500; private double targetY = 200;`), ignoring the target passed into `goToTargetWithOdometry(x, y, heading)`. Therefore, unless Auto happens to move the robot *exactly* to (500, 200), `targetReached()` will never return true, and the robot will hang in an infinite loop while continuously trying to drive to the Auto target.
* **The Fix:** Refactor `Drivetrain.java` to either store the target coordinates when `goToTargetWithOdometry()` is called, or modify `targetReached()` to accept `(targetX, targetY, targetHeading)` as arguments.

---

## ⚠️ Potential Inconsistencies & Operational Mishaps

**1. Shooter Consistency (Voltage Drop)**
* **The Setup:** `Shooter.java` gets its motors spun up simply using `setPower(1.0)`.
* **The Mishap:** As the battery drains during a match, `setPower(1.0)` will spin the wheels progressively slower, causing your shots to land short.
* **Preemptive Action:** Switch `Shooter.java` to use `RUN_USING_ENCODER` and `.setVelocity(ticks_per_second)` just like the `gamma` implementation. This enables PIDF compensation to ensure standard shot distance regardless of battery voltage.

**2. Untuned Proportional Drivetrain Controllers**
* **The Setup:** In `Drivetrain.java` `goToTargetWithOdometry()`, the `kP_pos` and `kP_heading` values are uniformly set to `0.01` with a comment to `// TUNE THESE`.
* **The Mishap:** An arbitrary `0.01` proportional gain may cause the robot to move sluggishly, oscillate violently around the target point, or stall prematurely (due to motor deadband at low powers).
* **Preemptive Action:** Test the `autoMove` button (`gamepad1.b`) in a clear, open space to safely tune the PID constants until movement is snappy and precise.

**3. Odometry Uncalibrated Offsets**
* **The Setup:** `HardwareMapConfig.java` has exact hardware specifications:
  ```java
  pinpoint.setOffsets(-45, -126, DistanceUnit.MM);
  ```
* **The Mishap:** If these offsets don't exactly represent the mechanical mounting offset of your goBILDA Pinpoint odometry pods relative to the robot's turning center, rotations will cause severe positional drift. 
* **Preemptive Action:** Physically measure distance from robot center to dead wheels and update these numbers. 

**4. `mechanisms/AprilTagWebcam.java` is Incomplete**
* **The Setup:** There exists a mechanism file for April Tags.
* **The Mishap:** The code only builds the builder but never constructs the `VisionPortal`, starts the cameras, or parses detections. Attempting to integrate this currently will yield no usable tag data.

---

## 🛠️ Testing Checklist (Pre-Flight)

Before running these scripts on the actual robot field, perform these steps:

- [ ] **Hardware Mapping Check:** Ensure the Driver Station config has exactly `wheel_0`, `wheel_1`, `wheel_2`, `wheel_3`, `shooter_motor_0`, `shooter_motor_1`, `feeder_servo`, `stopper_servo`, `intake_motor`, `webcam`, and `pinpoint` capitalized identically to `HardwareMapConfig.java`.
- [ ] **Fix Null Pointers:** Implement the null-check in `Shooter.java` (or just remove the `gp.x` read when `gp` is null) so Auto doesn't crash on firing.
- [ ] **Fix Target Check Logic:** Adjust `Drivetrain.targetReached` to verify against the active target coordinates, otherwise it breaks the auto routine.
- [ ] **Servo Safety Test:** When shooting, keep hands away from the mechanism. Notice if `FEEDING` state gives the servo sufficient time (`300ms`) to physically push the ball without binding. Tune this delay as needed.
- [ ] **Mecanum Direction Validation:** In TeleOp, gently push the left joystick exactly forward, back, left, and right. If the robot spins or shifts diagonally, the physical motor wiring does not match the `Direction.FORWARD`/`REVERSE` settings in `Drivetrain.java`.
