package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// mecanum drivetrain - 4 wheels in X config
//   NW(3) ---- NE(0)
//     |  ROBOT   |
//   SW(2) ---- SE(1)
//
// This layout matters because each wheel contributes differently
// to forward motion, strafing, and rotation.

public class Drivetrain {

    // Different speed modes for driver control
    // These allow the driver to switch between precision and speed
    public enum SpeedMode {
        NORMAL,     // standard driving
        PRECISION,  // slow and controlled (lining up shots)
        TURBO       // maximum speed (crossing field fast)
    }

    // Speed multipliers applied to ALL wheel powers
    // These scale the final motor output
    private static final double NORMAL_SPEED    = 1.0;
    private static final double PRECISION_SPEED = 0.3;
    private static final double TURBO_SPEED     = 1.0;

    // Reference to the shared hardware config
    // This gives access to all motors without calling hardwareMap again
    private final HardwareMapConfig robot;

    // Current speed mode
    private SpeedMode speedMode;

    // Current multiplier applied to motor power
    private double speedMultiplier;

    public Drivetrain(HardwareMapConfig robot) {
        this.robot = robot;

        // MOTOR DIRECTIONS
        // ----------------
        // Motors on opposite sides of the robot are mirrored,
        // so we reverse one side so that "forward power"
        // actually makes all wheels spin forward physically.
        //
        // If this is wrong, robot will spin instead of moving straight.

        robot.wheel_0.setDirection(DcMotorSimple.Direction.FORWARD);  // NE
        robot.wheel_1.setDirection(DcMotorSimple.Direction.FORWARD);  // SE
        robot.wheel_2.setDirection(DcMotorSimple.Direction.REVERSE);  // SW
        robot.wheel_3.setDirection(DcMotorSimple.Direction.REVERSE);  // NW

        // ZERO POWER BEHAVIOR
        // -------------------
        // BRAKE = motors resist movement when power = 0
        // This prevents drifting when you stop input

        robot.wheel_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // MOTOR MODE
        // ----------
        // RUN_WITHOUT_ENCODER means:
        // - we directly control power
        // - no speed/position regulation
        // This is standard for TeleOp driving

        robot.wheel_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // DEFAULT SPEED SETTINGS
        this.speedMode = SpeedMode.NORMAL;
        this.speedMultiplier = NORMAL_SPEED;
    }

    // MAIN DRIVE METHOD
    // -----------------
    // This is called EVERY LOOP (like 50+ times per second)
    //
    // Parameters:
    // drive   → forward/backward movement
    // strafe  → left/right movement
    // rotate  → turning (spinning)
    //
    // These come directly from joystick inputs

    public void drive(double drive, double strafe, double rotate) {
        // deadzone to prevent drifting
        drive  = deadzone(drive);
        strafe = deadzone(strafe);
        rotate = deadzone(rotate);

        // MECANUM MATH
        // ------------
        // Each wheel contributes differently to movement.
        // We combine 3 motions:
        // - forward/backward (drive)
        // - sideways (strafe)
        // - rotation (rotate)
        //
        // Each wheel gets a different combination of + and - values

        double w0 = drive - strafe - rotate;   // NE (front right)
        double w1 = drive + strafe - rotate;   // SE (back right)
        double w2 = drive - strafe + rotate;   // SW (back left)
        double w3 = drive + strafe + rotate;   // NW (front left)

        // NORMALIZATION
        // -------------
        // Problem:
        // If we add values like 1 + 1 + 1 = 3,
        // motor power exceeds allowed range [-1, 1]
        //
        // Solution:
        // Find the largest magnitude and scale everything down

        double max = Math.max(
                Math.max(Math.abs(w0), Math.abs(w1)),
                Math.max(Math.abs(w2), Math.abs(w3)));

        // Only scale if needed
        if (max > 1.0) {
            w0 /= max;
            w1 /= max;
            w2 /= max;
            w3 /= max;
        }

        // APPLY POWER
        // -----------
        // Multiply by speedMultiplier to adjust overall speed
        // Then send final values to motors

        robot.wheel_0.setPower(w0 * speedMultiplier);
        robot.wheel_1.setPower(w1 * speedMultiplier);
        robot.wheel_2.setPower(w2 * speedMultiplier);
        robot.wheel_3.setPower(w3 * speedMultiplier);
    }

    // CHANGE SPEED MODE
    // -----------------
    // Allows driver to switch between control styles
    public void setSpeedMode(SpeedMode mode) {
        this.speedMode = mode;

        // Update multiplier based on mode
        switch (mode) {
            case NORMAL:
                speedMultiplier = NORMAL_SPEED;
                break;

            case PRECISION:
                speedMultiplier = PRECISION_SPEED;
                break;

            case TURBO:
                speedMultiplier = TURBO_SPEED;
                break;
        }
    }

    // GET CURRENT MODE
    // ----------------
    public SpeedMode getSpeedMode() {
        return speedMode;
    }

    // GET CURRENT SPEED MULTIPLIER
    // ----------------------------
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    // STOP ALL MOTORS
    // ---------------
    // Immediately sets all wheel power to zero
    // Used for:
    // - safety
    // - stopping robot at end of match
    public void stop() {
        robot.wheel_0.setPower(0);
        robot.wheel_1.setPower(0);
        robot.wheel_2.setPower(0);
        robot.wheel_3.setPower(0);
    }

    // DEBUG METHOD
    // ------------
    // Returns current motor powers
    // Useful for telemetry/debugging
    public double[] getWheelPowers() {
        return new double[] {
                robot.wheel_0.getPower(),
                robot.wheel_1.getPower(),
                robot.wheel_2.getPower(),
                robot.wheel_3.getPower()
        };
    }

    //deadzone return 0 if less than 0.05
    private double deadzone(double value) {
        return Math.abs(value) > 0.05 ? value : 0;
    }
}