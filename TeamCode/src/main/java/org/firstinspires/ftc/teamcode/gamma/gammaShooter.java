package org.firstinspires.ftc.teamcode.gamma;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Shooter — dual flywheel + feeder servo subsystem.
 *
 * FLYWHEEL:
 *   Two motors spin in opposite directions to grip and launch balls.
 *   Velocity is set in ticks/sec using RUN_USING_ENCODER mode with
 *   a PIDF controller so the speed stays consistent under load.
 *
 * FEEDER:
 *   A servo pushes a ball into the spinning flywheels.
 *   fireBallNonBlocking() starts the push; updateFeeder() must be
 *   called every loop to advance the state machine without blocking.
 *
 * READY CHECK:
 *   isReadyToFire() returns true when both motors are within
 *   READY_TOLERANCE ticks/sec of the target — safe to fire.
 */
public class gammaShooter {

    // Flywheel target velocity (ticks per second at the motor shaft)
    // ~1800 ticks/sec works well for high goal. Tune for your motor + game.
    private static final double TARGET_VELOCITY = 1800;

    // How close to target velocity before we allow a shot
    private static final double READY_TOLERANCE = 60;  // ticks/sec

    // PIDF coefficients for velocity control
    // These are starting values — tune p/f if motor doesn't hold speed
    private static final double PIDF_P = 35;
    private static final double PIDF_I = 0;
    private static final double PIDF_D = 0;
    private static final double PIDF_F = 13.7;

    // Feeder servo positions
    private static final double FEEDER_RETRACTED = 0.0;
    private static final double FEEDER_EXTENDED  = 0.5;

    // How long the feeder stays extended before retracting (ms)
    private static final double FEEDER_PUSH_MS = 200;

    private final DcMotorEx motor0, motor1;
    private final Servo     feeder;

    private double currentTarget = 0;

    // Feeder state machine
    private enum FeederState { IDLE, PUSHING, RETRACTING }
    private FeederState feederState = FeederState.IDLE;
    private final ElapsedTime feederTimer = new ElapsedTime();

    public gammaShooter(gammaHardwareMapConfig robot) {
        motor0 = robot.shooter_motor_0;
        motor1 = robot.shooter_motor_1;
        feeder = robot.feeder_servo;

        // Motors spin opposite directions to grip the ball from both sides
        motor0.setDirection(DcMotorSimple.Direction.FORWARD);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);

        motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // RUN_USING_ENCODER lets us set velocity instead of raw power
        motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Tune the internal PIDF so velocity stays locked under load
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        motor0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Start feeder retracted
        feeder.setPosition(FEEDER_RETRACTED);
    }

    // ----------------------------------
    // FLYWHEEL CONTROL
    // ----------------------------------

    /** Spin flywheels up to shooting speed. */
    public void spinUp() {
        setVelocity(TARGET_VELOCITY);
    }

    /** Stop flywheels (coast to zero). */
    public void spinDown() {
        setVelocity(0);
    }

    /** Set a custom velocity target (ticks/sec). */
    public void setVelocity(double ticksPerSec) {
        currentTarget = ticksPerSec;
        motor0.setVelocity(ticksPerSec);
        motor1.setVelocity(ticksPerSec);
    }

    /** True when both motors are within tolerance of the target. Safe to fire. */
    public boolean isReadyToFire() {
        if (currentTarget == 0) return false;
        double err0 = Math.abs(motor0.getVelocity() - currentTarget);
        double err1 = Math.abs(motor1.getVelocity() - currentTarget);
        return err0 < READY_TOLERANCE && err1 < READY_TOLERANCE;
    }

    // ----------------------------------
    // FEEDER (non-blocking)
    // ----------------------------------

    /**
     * Start the feeder push cycle (non-blocking).
     * Call updateFeeder() every loop to advance the state machine.
     * Does nothing if feeder is already mid-cycle.
     */
    public void fireBallNonBlocking() {
        if (feederState == FeederState.IDLE) {
            feeder.setPosition(FEEDER_EXTENDED);
            feederTimer.reset();
            feederState = FeederState.PUSHING;
        }
    }

    /**
     * Advances the feeder state machine.
     * MUST be called every loop or the feeder will never retract.
     */
    public void updateFeeder() {
        if (feederState == FeederState.PUSHING && feederTimer.milliseconds() >= FEEDER_PUSH_MS) {
            feeder.setPosition(FEEDER_RETRACTED);
            feederState = FeederState.RETRACTING;
            feederTimer.reset();
        } else if (feederState == FeederState.RETRACTING && feederTimer.milliseconds() >= FEEDER_PUSH_MS) {
            feederState = FeederState.IDLE;
        }
    }

    // ----------------------------------
    // STOP
    // ----------------------------------

    public void stop() {
        setVelocity(0);
        feeder.setPosition(FEEDER_RETRACTED);
        feederState = FeederState.IDLE;
    }

    // ----------------------------------
    // TELEMETRY
    // ----------------------------------

    public void displayTelemetry(Telemetry t) {
        t.addData("Shooter | target",  String.format("%.0f ticks/s", currentTarget));
        t.addData("Shooter | m0 vel",  String.format("%.0f", motor0.getVelocity()));
        t.addData("Shooter | m1 vel",  String.format("%.0f", motor1.getVelocity()));
        t.addData("Shooter | ready",   isReadyToFire() ? "YES" : "no");
        t.addData("Shooter | feeder",  feederState.name());
    }
}
