package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Shooter.java — Flywheel Shooter Subsystem
 *
 * HOW IT WORKS:
 * -------------
 * Two flywheel motors spin in opposite directions, creating a pair of
 * counter-rotating wheels. When a ball passes between them, friction
 * launches it forward. Speed consistency matters a lot — too slow and
 * the ball under-shoots; too fast and it over-shoots.
 *
 * To get consistent speed we use VELOCITY CONTROL (setVelocity) instead
 * of raw power (setPower). The motor controller runs a PIDF feedback loop
 * internally to hold the flywheel at the exact RPM we request.
 *
 * The FEEDER SERVO pushes individual balls into the spinning wheels.
 * It uses a non-blocking state machine so the main loop never stalls.
 *
 * HARDWARE INDEPENDENCE:
 * ----------------------
 * This class does NOT call HardwareMap.get() anywhere. All motor and
 * servo references come in from HardwareMapConfig via the constructor.
 * If a device name changes, only HardwareMapConfig.java needs updating.
 */
public class Shooter {

    // ----------------------------------
    // CONSTANTS / PRESETS
    // ----------------------------------

    // Flywheel target velocities in encoder ticks per second (not RPM).
    // HIGH goal is farther away so it needs more velocity.
    // LOW goal can be a softer lob.
    // Tune these based on actual field testing.
    public static final double VELOCITY_HIGH = 1800;  // for high goal shots
    public static final double VELOCITY_LOW  = 1350;  // for low goal shots

    // How close the flywheel speed must be to target before the robot
    // considers it "ready to fire". Too tight = always waiting. Too loose = bad aim.
    private static final double VELOCITY_TOLERANCE = 60;

    // PIDF TUNING
    // -----------
    // The motor controller uses this PIDF loop to hold flywheel speed:
    //
    //   F (feedforward) — sets a base power proportional to the target velocity.
    //                      Most of the work happens here. Without it, P/I/D
    //                      would have to do all the heavy lifting and lag badly.
    //   P (proportional) — corrects based on current error (target - actual).
    //                       Larger P = faster correction, but can oscillate.
    //   I (integral) — corrects for sustained small errors over time.
    //                   We leave this at 0 to avoid windup on flywheels.
    //   D (derivative) — dampens rapid changes. Also 0 here for simplicity.
    //
    // These values were tuned empirically. F=13.7 was found by measuring
    // the motor's actual tick-per-second rate at known power levels.
    private static final double PIDF_F = 13.7;
    private static final double PIDF_P = 30.0;
    private static final double PIDF_I = 0.0;
    private static final double PIDF_D = 0.0;

    // FEEDER SERVO POSITIONS
    // Position 0.0 = retracted (holding ball back)
    // Position 0.55 = pushed forward (shoving ball into flywheel gap)
    // Values are 0.0–1.0 mapping to the servo's full range of motion.
    private static final double FEEDER_RETRACT = 0.0;
    private static final double FEEDER_PUSH    = 0.55;

    // FEEDER TIMING
    // FEEDER_HOLD_MS  = how long to hold the servo in the push position
    //                   (ball needs time to travel through the flywheel)
    // SHOT_INTERVAL_MS = minimum gap between shots to prevent jamming
    //                    (includes push time + retract time + brief wait)
    private static final long FEEDER_HOLD_MS   = 180;
    private static final long SHOT_INTERVAL_MS = 350;

    // ----------------------------------
    // MEMBER VARIABLES
    // ----------------------------------

    // Flywheel motors
    private final DcMotorEx motor0;  // Left or right motor
    private final DcMotorEx motor1;  // Opposite motor to grip ball

    // Servo that pushes balls into flywheels
    private final Servo feeder;

    // Current target velocity (RPM) of the flywheels
    private double targetVelocity = 0;

    // True if flywheel motors are spinning
    private boolean spinning = false;

    // ----------------------------------
    // FEEDER STATE MACHINE
    // ----------------------------------

    // Feeder states:
    // IDLE = servo is waiting
    // PUSHING = servo is pushing a ball
    // RETRACTING = servo returning to retracted position
    private enum FeederState { IDLE, PUSHING, RETRACTING }

    private FeederState feederState = FeederState.IDLE;

    // Timers to track servo movement and shot cooldown
    private final ElapsedTime feederTimer  = new ElapsedTime();  // Tracks current push/retract duration
    private final ElapsedTime shotCooldown = new ElapsedTime();  // Tracks time between shots

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * Constructor — receives pre-built hardware config.
     * This class NEVER calls hardwareMap.get() directly.
     * All hardware access goes through the passed-in HardwareMapConfig object.
     *
     * @param robot Shared HardwareMapConfig populated by Robot.java
     */
    public Shooter(HardwareMapConfig robot) {

        // Pull motor and servo references out of the config.
        // After this constructor, we only use these local references.
        this.motor0 = robot.shooter_motor_0;
        this.motor1 = robot.shooter_motor_1;
        this.feeder = robot.feeder_servo;

        // MOTOR DIRECTIONS
        // The two flywheel motors face each other (or are geared oppositely).
        // Setting one FORWARD and one REVERSE makes both wheels spin INWARD
        // so the ball gets grabbed from both sides and launched.
        // If both are FORWARD the ball just gets deflected instead of launched.
        motor0.setDirection(DcMotorSimple.Direction.FORWARD);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);

        // VELOCITY CONTROL MODE
        // RUN_USING_ENCODER enables the motor controller's built-in PID.
        // This is different from the drivetrain, which uses RUN_WITHOUT_ENCODER.
        // We need this because flywheel accuracy depends on consistent RPM,
        // not just consistent power (power changes as battery drains).
        motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // ZERO POWER BEHAVIOR: FLOAT
        // When we stop the motors we let them coast to a halt.
        // BRAKE would cause mechanical stress on the flywheel gears/shaft
        // when coming down from high speed. FLOAT is safer here.
        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // APPLY PIDF COEFFICIENTS
        // Feed them to both motors for the RUN_USING_ENCODER mode.
        // Same coefficients on both since the motors are identical hardware.
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        motor0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        // Ensure feeder is fully retracted on startup.
        // If it powered on mid-push it could jam the first ball.
        feeder.setPosition(FEEDER_RETRACT);

        // Reset cooldown so the robot can fire immediately on the first shot
        // (no need to wait 350ms for the very first trigger press).
        shotCooldown.reset();
    }

    // ----------------------------------
    // SPINNING METHODS
    // ----------------------------------

    // Spin up flywheels to specific velocity
    public void spinUp(double velocity) {
        targetVelocity = velocity;
        motor0.setVelocity(velocity);
        motor1.setVelocity(velocity);
        spinning = true; // mark that flywheels are spinning
    }

    // Spin up to default high goal velocity
    public void spinUp() {
        spinUp(VELOCITY_HIGH);
    }

    // Stop flywheels
    public void spinDown() {
        motor0.setVelocity(0);
        motor1.setVelocity(0);
        targetVelocity = 0;
        spinning = false;
    }

    // Toggle flywheel spinning (on/off)
    public void toggleSpin() {
        if (spinning) spinDown();
        else spinUp();
    }

    // ----------------------------------
    // READINESS CHECK
    // ----------------------------------

    // Returns true if flywheels are up to speed and ready to fire
    public boolean isReadyToFire() {
        if (!spinning || targetVelocity == 0) return false;

        double v0 = motor0.getVelocity();
        double v1 = motor1.getVelocity();

        // Check that both motors are within tolerance of target velocity
        return Math.abs(v0 - targetVelocity) < VELOCITY_TOLERANCE
                && Math.abs(v1 - targetVelocity) < VELOCITY_TOLERANCE;
    }

    // Get average velocity of both flywheels
    public double getAverageVelocity() {
        return (motor0.getVelocity() + motor1.getVelocity()) / 2.0;
    }

    // ----------------------------------
    // BLOCKING FIRE (for autonomous)
    // ----------------------------------

    // Pushes ball and waits (blocking)
    // DO NOT USE IN TELEOP
    public void fireBallBlocking() {

        // Push ball into flywheel
        feeder.setPosition(FEEDER_PUSH);

        try { Thread.sleep(FEEDER_HOLD_MS); }  // wait while ball moves
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

        // Retract feeder
        feeder.setPosition(FEEDER_RETRACT);

        try { Thread.sleep(SHOT_INTERVAL_MS - FEEDER_HOLD_MS); }  // wait for shot cooldown
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    // ----------------------------------
    // NON-BLOCKING FIRE (for teleop)
    // ----------------------------------

    // Starts firing sequence if feeder is idle and cooldown passed
    public void fireBallNonBlocking() {

        // Only fire if feeder is not already moving
        if (feederState != FeederState.IDLE) return;

        // Only fire if cooldown is complete
        if (shotCooldown.milliseconds() < SHOT_INTERVAL_MS) return;

        // Start push phase
        feeder.setPosition(FEEDER_PUSH);
        feederTimer.reset();
        feederState = FeederState.PUSHING;
    }

    // Progress feeder state machine — MUST be called every loop iteration.
    // This is how non-blocking timing works:
    //   Instead of Thread.sleep() (which freezes everything), we check
    //   how much time has passed since the last state change and advance
    //   the state when enough time has elapsed.
    //
    //   IDLE → (fireBallNonBlocking called) → PUSHING → RETRACTING → IDLE
    public void updateFeeder() {
        switch (feederState) {

            case PUSHING:
                // Ball is being pushed. Wait for FEEDER_HOLD_MS to elapse.
                // Once done, retract and start timing the retraction phase.
                if (feederTimer.milliseconds() >= FEEDER_HOLD_MS) {
                    feeder.setPosition(FEEDER_RETRACT);
                    feederTimer.reset();
                    feederState = FeederState.RETRACTING;
                }
                break;

            case RETRACTING:
                // Wait for the remaining cooldown (SHOT_INTERVAL - FEEDER_HOLD time)
                // before declaring we're ready for the next shot.
                if (feederTimer.milliseconds() >= (SHOT_INTERVAL_MS - FEEDER_HOLD_MS)) {
                    feederState = FeederState.IDLE;
                    shotCooldown.reset();  // start the inter-shot cooldown timer
                }
                break;

            case IDLE:
                // Nothing to do. Waiting for fireBallNonBlocking() to be called.
                break;
        }
    }

    // ----------------------------------
    // STOP EVERYTHING
    // ----------------------------------

    public void stop() {
        spinDown();                     // stop flywheels
        feeder.setPosition(FEEDER_RETRACT); // retract feeder
        feederState = FeederState.IDLE;     // reset state
    }

    // ----------------------------------
    // STATUS
    // ----------------------------------

    // Returns true if flywheels are spinning
    public boolean isSpinning() {
        return spinning;
    }

    // Display telemetry for debugging
    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("Shooter | tgt",     String.format("%.0f", targetVelocity));  // target velocity
        telemetry.addData("Shooter | m0 vel",  String.format("%.0f", motor0.getVelocity())); // actual motor0
        telemetry.addData("Shooter | m1 vel",  String.format("%.0f", motor1.getVelocity())); // actual motor1
        telemetry.addData("Shooter | ready",   isReadyToFire() ? "YES" : "no");          // ready to fire
        telemetry.addData("Shooter | feedr",   feederState.name());                        // feeder state machine
    }
}