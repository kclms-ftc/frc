# Codebase Overview and Understandings

This document provides a comprehensive explanation of how the robot's code operates, focusing heavily on hardware configuration and subsystem interactions. It also highlights logic errors, inconsistencies, and potential crash risks identified within the current state of the code.

## 1. Core Architecture: `HardwareMapConfig.java`

`HardwareMapConfig.java` operates as the definitive hardware registry for the robot.

* **What it does:** It centralizes the `hw.get()` assignments. Instead of each individual part of the robot fetching its own motors or servos, this file maps all strings matching the physical Driver Station configuration (e.g., `"wheel_0"`, `"shooter_motor_0"`) to `DcMotorEx` and `Servo` objects.
* **Why it does it:** Subsystems (`Drivetrain`, `Shooter`, `Intake`) require hardware references to operate. The main `Teleop` initializes `HardwareMapConfig` once and passes it via constructors into every subsystem. This guarantees all subsystems share the exact same hardware references and stops "hardware device not found" crashes scattered across multiple files.
* **Odometry Calibration:** This class also initializes the goBILDA Pinpoint odometry computer. It sets the MM coordinate offsets for the Dead Wheel pods, specifies the type of pod used (`goBILDA_4_BAR_POD`), configures forward encoder directions, and zeroes out the starting position and IMU heading.

## 2. Subsystem Breakdowns

* **`Drivetrain.java`**: Implements Mecanum drive calculations. It filters gamepad joystick inputs through a deadzone to prevent stick drift, calculates power distributions for `ne, nw, se, sw` wheels, and supports a toggleable "Precision Mode" to slow the robot down. It also features a `goToTargetWithOdometry` method, which uses the Pinpoint coordinates and a Proportional controller (P-loop) to automatically navigate the robot to a set (X, Y, Heading) point when `gp.b` is pressed.
* **`Shooter.java`**: Exists as a state-machine (`IDLE`, `SPINNING_UP`, `READY`, `FEEDING`, `DONE`). When the `x` button is pressed, it revs up dual flywheel motors, waits a designated amount of time, opens a stopper servo, then feeds three balls into the flywheels via a feeder servo before returning to idle.
* **`Intake.java`**: Operates the front intake roller. It features logic intended to toggle between normal and low speed, as well as an override to reverse the motor for outtaking.
* **`Teleop.java`**: The main execution bridge. It initializes the `HardwareMapConfig`, constructs the individual subsystems, and repeatedly funnels `gamepad1` input data into the `loop(gamepad)` methods of each subsystem running 50 times per second.

---

## 3. Identified Errors and Vulnerabilities

### 🟥 CRITICAL ERRORS

1. **Intake Never Turns Off (`Intake.java` line 44–48)**:
   There is no `OFF` state reachable during normal driving. Regardless of whether any button is held, the logic unconditionally sends power to the intake motor every loop:
   ```java
   if (lowSpeedMode) {
       intakeMotor.setPower(lowIntakePower); // 0.3
   } else {
       intakeMotor.setPower(intakePower); // 0.85
   }
   ```
   **The moment `Teleop` starts, the intake motor turns on and never stops until `stop()` is called.** A button-hold condition (e.g. a trigger) must gate this block, and `intakeMotor.setPower(0)` must be sent when nothing is pressed.

2. **Webcam Init Will Crash If Not Configured (`HardwareMapConfig.java` line 64)**:
   ```java
   webcam = hw.get(WebcamName.class, "webcam");
   ```
   This call will throw a `hardwareMap device not found` exception at runtime if the Driver Station config does not have a webcam named exactly `"webcam"`. There is no try/catch and no null check — the entire `init()` will crash and the OpMode will never start. If the webcam is not needed for a match, this line must be removed or guarded.

3. **`shotsRemaining` Logic Is Inverted (`Shooter.java` lines 91–100)**:
   The comment and branch conditions contradict each other:
   ```java
   shotsRemaining -= 1;
   // if 3 balls been shot
   if (shotsRemaining > 0) {
       state = ShootState.READY;   // keep shooting
   }
   else { // still more balls to shoot   <-- comment is WRONG
       state = ShootState.DONE;
   }
   ```
   The `else` comment says "still more balls to shoot" but that branch is only reached when `shotsRemaining == 0`, meaning all balls are gone. The logic is actually correct but the comment is completely backwards and will mislead anyone debugging this.

---

### 🟧 DOCUMENTATION AND LOGIC INCONSISTENCIES

1. **Gamepad Comment vs Reality (`Intake.java` line 11)**:
   The header comment claims `"a - toggle low or normal intake speed"`. However, the actual loop code uses `gp.left_bumper` to execute this toggle (line 35). The `a` button does nothing. This will confuse drivers.

2. **Duplicate `updateTelemetry` Call (`Teleop.java` line 41)**:
   ```java
   drivetrain.updateTelemetry(telemetry);  // line 38 — correct
   // ...
   //        drivetrain.updateTelemetry(telemetry);  // line 41 — commented duplicate
   ```
   There is a commented-out second call to `drivetrain.updateTelemetry()`. This is dead noise and should be deleted.

3. **`updateTelemetry` Is Empty In All Subsystems**:
   `Drivetrain.java` (line 100), `Shooter.java` (line 116), and `Intake.java` (line 51) all have empty `updateTelemetry()` bodies. No subsystem data is ever posted to the Driver Station screen. This makes debugging on the field nearly impossible.

4. **Obsolete Pinpoint Comment (`HardwareMapConfig.java` line 71)**:
   ```java
   //pinpoint.setTrack(90);
   ```
   This leftover commented line references an API method that doesn't exist on the current Pinpoint firmware. It should be deleted entirely to avoid confusion.

---

### 🟨 POTENTIAL VULNERABILITIES (Refactor Targets)

1. **Time-based Blind Shooting (`Shooter.java` line 69)**:
   The state machine assumes flywheel motors always take exactly 1.5 seconds to reach full speed. On a weak battery they may still be too slow; on a fresh battery the robot wastes nearly a second. **Recommendation:** Replace `timeElapsed(1500)` with a velocity check using `shooterMotor0.getVelocity()` and only transition to `READY` once a target RPM threshold is met.

2. **Simultaneous Subsystem Clashes (`Teleop.java`)**:
   The shooter sequence (`gp.x`) and autonomous driving sequence (`gp.b`) can be triggered at the same time. Driver error could result in the drivetrain aggressively swerving toward `x=500, y=200` while simultaneously deploying the feeding servos — causing a missed shot and potential mechanical conflict.

3. **Hard-coded Auto Target Position (`Drivetrain.java` lines 37–39)**:
   ```java
   private double targetX = 500;
   private double targetY = 200;
   private double targetHeading = 0;
   ```
   The auto-drive target is baked in as a constant. There is no way for a driver to change it without recompiling. This makes autonomous positioning inflexible and must be tuned to reflect actual field coordinates before competition.

4. **`autoMoveActive` Never Resets On Interrupt (`Drivetrain.java`)**:
   Once `gp.b` is pressed and `autoMoveActive = true`, the only way it turns off is if `targetReached()` returns true. If the robot gets pushed, stuck, or the target is unreachable, `autoMoveActive` stays `true` forever and the driver loses manual control for the rest of the match.
