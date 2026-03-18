package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Shooter subsystem for FTC robot
 *
 * This class handles:
 * - Flywheel motors for shooting balls
 * - Feeder servo for loading balls
 * - Non-blocking and blocking firing
 * - Telemetry display for debugging
 *
 * Notes:
 * - Uses setVelocity instead of setPower for consistent speed
 * - Feeder uses a simple state machine to avoid blocking the main loop
 */
public class Shooter {

    // ----------------------------------
    // CONSTANTS / PRESETS
    // ----------------------------------

    // Target velocities for high and low goal shots (in RPM)
    public static final double VELOCITY_HIGH = 1800;  // High goal
    public static final double VELOCITY_LOW  = 1350;  // Low goal

    // Tolerance: how close the actual motor speed must be to target to be considered "ready"
    private static final double VELOCITY_TOLERANCE = 60;

    // PIDF coefficients for flywheel velocity control
    // F = feedforward, P = proportional, I = integral, D = derivative
    // Feedforward is most important for flywheel because it sets base power based on target velocity
    private static final double PIDF_F = 13.7;
    private static final double PIDF_P = 30.0;
    private static final double PIDF_I = 0.0;
    private static final double PIDF_D = 0.0;

    // Feeder servo positions
    // FEEDER_RETRACT = pulls servo back so ball is held
    // FEEDER_PUSH = pushes ball into flywheels
    private static final double FEEDER_RETRACT = 0.0;
    private static final double FEEDER_PUSH    = 0.55;

    // Timing constants for feeding balls
    // FEEDER_HOLD_MS = how long the servo pushes the ball
    // SHOT_INTERVAL_MS = total interval between shots (prevents jams)
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
    public Shooter(HardwareMapConfig robot) {

        // Map motors and servo from hardware configuration
        this.motor0 = robot.shooter_motor_0;
        this.motor1 = robot.shooter_motor_1;
        this.feeder = robot.feeder_servo;

        // Set flywheel motor directions
        // Motors spin opposite directions to grip ball evenly
        motor0.setDirection(DcMotorSimple.Direction.FORWARD);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);

        // Run using encoders for velocity control
        motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Float behavior: motors coast when power = 0
        // This is okay because we control velocity actively
        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Load tuned PIDF coefficients for velocity control
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        motor0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        // Ensure feeder starts retracted
        feeder.setPosition(FEEDER_RETRACT);

        // Reset shot cooldown timer so first shot is ready immediately
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

    // Progress feeder state machine, must be called every loop
    public void updateFeeder() {
        switch (feederState) {

            case PUSHING:
                // Wait for feeder push duration
                if (feederTimer.milliseconds() >= FEEDER_HOLD_MS) {
                    feeder.setPosition(FEEDER_RETRACT);  // retract after pushing
                    feederTimer.reset();                 // reset timer for retraction
                    feederState = FeederState.RETRACTING;
                }
                break;

            case RETRACTING:
                // Wait for retraction duration (remaining cooldown)
                if (feederTimer.milliseconds() >= (SHOT_INTERVAL_MS - FEEDER_HOLD_MS)) {
                    feederState = FeederState.IDLE;  // done, back to idle
                    shotCooldown.reset();            // reset cooldown for next shot
                }
                break;

            case IDLE:
                // Do nothing, waiting for next shot
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