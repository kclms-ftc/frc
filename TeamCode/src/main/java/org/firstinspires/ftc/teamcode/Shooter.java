package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

// shooting mechanism — flywheels and a servo that feeds the balls
// we use setVelocity instead of setPower to make sure the rpm doesnt drop when batery is low
// the trajectory of the balls needs to be exactly the same every time
public class Shooter {

    // presets for the high and low goal
    public static final double VELOCITY_HIGH = 1800;
    public static final double VELOCITY_LOW  = 1350;

    // its ready when we are within this velocity
    private static final double VELOCITY_TOLERANCE = 60;

    // pidf values — F is the most imoprtatn tune it first
    private static final double PIDF_F = 13.7;
    private static final double PIDF_P = 30.0;
    private static final double PIDF_I = 0.0;
    private static final double PIDF_D = 0.0;

    // hold the ball back, or feed it into the flywheels
    private static final double FEEDER_RETRACT = 0.0;
    private static final double FEEDER_PUSH = 0.55;

    // wait this long for the ball to go through
    // raise it if it jams
    private static final long FEEDER_HOLD_MS = 180;
    private static final long SHOT_INTERVAL_MS = 350;

    private final DcMotorEx motor0;
    private final DcMotorEx motor1;
    private final Servo     feeder;

    private double  targetVelocity = 0;
    private boolean spinning       = false;

    // non blocking state machine for driving the teleop feeder
    private enum FeederState { IDLE, PUSHING, RETRACTING }
    private FeederState feederState = FeederState.IDLE;
    private final ElapsedTime feederTimer    = new ElapsedTime();
    private final ElapsedTime shotCooldown   = new ElapsedTime();

    public Shooter(HardwareMapConfig robot) {
        this.motor0 = robot.shooter_motor_0;
        this.motor1 = robot.shooter_motor_1;
        this.feeder = robot.feeder_servo;

        // motors spin in opposite directions to grip the ball
        motor0.setDirection(DcMotorSimple.Direction.FORWARD);
        motor1.setDirection(DcMotorSimple.Direction.REVERSE);

        motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // load the tuned coefficients
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        motor0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        feeder.setPosition(FEEDER_RETRACT);
        shotCooldown.reset();
    }

    public void spinUp(double velocity) {
        targetVelocity = velocity;
        motor0.setVelocity(velocity);
        motor1.setVelocity(velocity);
        spinning = true;
    }

    public void spinUp() {
        spinUp(VELOCITY_HIGH);
    }

    public void spinDown() {
        motor0.setVelocity(0);
        motor1.setVelocity(0);
        targetVelocity = 0;
        spinning = false;
    }

    public void toggleSpin() {
        if (spinning) spinDown();
        else spinUp();
    }

    // check this before feeding a ball
    public boolean isReadyToFire() {
        if (!spinning || targetVelocity == 0) return false;

        double v0 = motor0.getVelocity();
        double v1 = motor1.getVelocity();

        return Math.abs(v0 - targetVelocity) < VELOCITY_TOLERANCE
            && Math.abs(v1 - targetVelocity) < VELOCITY_TOLERANCE;
    }

    public double getAverageVelocity() {
        return (motor0.getVelocity() + motor1.getVelocity()) / 2.0;
    }

    // blocking code for auto ONLY
    // DO NOT use this in teleop because it sleeps the thread
    public void fireBallBlocking() {
        feeder.setPosition(FEEDER_PUSH);

        try { Thread.sleep(FEEDER_HOLD_MS); }
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

        feeder.setPosition(FEEDER_RETRACT);

        try { Thread.sleep(SHOT_INTERVAL_MS - FEEDER_HOLD_MS); }
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    // starts the non blocking feeder
    // remember freqquently calling updateFeeder in the main loop
    public void fireBallNonBlocking() {
        if (feederState != FeederState.IDLE) return;
        if (shotCooldown.milliseconds() < SHOT_INTERVAL_MS) return; // on cooldown

        feeder.setPosition(FEEDER_PUSH);
        feederTimer.reset();
        feederState = FeederState.PUSHING;
    }

    // progress the state machine for feeding balls
    public void updateFeeder() {
        switch (feederState) {
            case PUSHING:
                if (feederTimer.milliseconds() >= FEEDER_HOLD_MS) {
                    feeder.setPosition(FEEDER_RETRACT);
                    feederTimer.reset();
                    feederState = FeederState.RETRACTING;
                }
                break;

            case RETRACTING:
                if (feederTimer.milliseconds() >= (SHOT_INTERVAL_MS - FEEDER_HOLD_MS)) {
                    feederState = FeederState.IDLE;
                    shotCooldown.reset();
                }
                break;

            case IDLE:
                break;
        }
    }

    public void stop() {
        spinDown();
        feeder.setPosition(FEEDER_RETRACT);
        feederState = FeederState.IDLE;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("Shooter | tgt",     String.format("%.0f", targetVelocity));
        telemetry.addData("Shooter | m0 vel",  String.format("%.0f", motor0.getVelocity()));
        telemetry.addData("Shooter | m1 vel",  String.format("%.0f", motor1.getVelocity()));
        telemetry.addData("Shooter | ready",   isReadyToFire() ? "YES" : "no");
        telemetry.addData("Shooter | feedr",   feederState.name());
    }
}
