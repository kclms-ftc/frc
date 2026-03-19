package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Drivetrain.java — Mecanum Wheel Drivetrain
 *
 * HOW IT WORKS:
 * -------------
 * This robot uses MECANUM wheels, which have angled rollers on them.
 * Each wheel is oriented so that combining their forces lets the robot
 * move in any direction — forward, sideways (strafe), and rotate — all
 * at the same time without turning the wheels themselves.
 *
 * WHEEL LAYOUT (top-down view):
 *
 *   NW(3) ---- NE(0)
 *     |  ROBOT  |
 *   SW(2) ---- SE(1)
 *
 * HARDWARE INDEPENDENCE:
 * ----------------------
 * This class does NOT access HardwareMap (the raw Android hardware lookup)
 * directly. All motor references are passed in via HardwareMapConfig,
 * which is built once in Robot.java and shared across all subsystems.
 * This means if motor names change, you only update HardwareMapConfig.java.
 */

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

    // Shared hardware config — all motor access goes through this object.
    // We NEVER call hardwareMap.get() inside this class. Hardware is wired
    // up once in HardwareMapConfig and passed here via the constructor.
    private final HardwareMapConfig robot;

    // Current speed mode (NORMAL, PRECISION, TURBO)
    private SpeedMode speedMode;

    // The multiplier that scales raw mecanum power output (0.0 to 1.0)
    private double speedMultiplier;

    /**
     * Constructor — receives pre-built hardware config.
     * Does NOT take a raw HardwareMap. Hardware is already resolved.
     *
     * @param robot Shared HardwareMapConfig with all motors wired up
     */
    public Drivetrain(HardwareMapConfig robot) {
        this.robot = robot;

        // MOTOR DIRECTIONS
        // ----------------
        // The right-side motors (wheel_0, wheel_1) and left-side motors
        // (wheel_2, wheel_3) are physically mirrored. If we set the same
        // direction for all 4, the left side would spin backward relative
        // to the right side and the robot would spin in place.
        //
        // Fix: reverse the left-side so "forward power" = actual forward motion.
        // If the robot spins instead of driving straight, check this section first.

        robot.wheel_0.setDirection(DcMotorSimple.Direction.FORWARD);  // NE (front-right)
        robot.wheel_1.setDirection(DcMotorSimple.Direction.FORWARD);  // SE (back-right)
        robot.wheel_2.setDirection(DcMotorSimple.Direction.REVERSE);  // SW (back-left)
        robot.wheel_3.setDirection(DcMotorSimple.Direction.REVERSE);  // NW (front-left)

        // ZERO POWER BEHAVIOR
        // -------------------
        // BRAKE makes the motor actively resist movement when power = 0.
        // This stops the robot quickly instead of coasting to a halt.
        // Good for precision driving and stopping on a dime.

        robot.wheel_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // MOTOR MODE
        // ----------
        // RUN_WITHOUT_ENCODER = raw power control, no PID, no speed regulation.
        // We just say "spin at 80% power" and the motor does it.
        // Drivetrain doesn't need velocity control — the driver corrects naturally.
        // (Compare this to the Shooter, which uses RUN_USING_ENCODER + PIDF
        //  because consistent flywheel speed is critical for accuracy.)

        robot.wheel_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Start in NORMAL mode by default
        this.speedMode = SpeedMode.NORMAL;
        this.speedMultiplier = NORMAL_SPEED;
    }

    /**
     * drive() — Main mecanum drive method. Call this every loop.
     *
     * HOW MECANUM MATH WORKS:
     * -----------------------
     * Mecanum wheels have rollers at 45°. Each wheel's roller direction
     * determines how it contributes to movement. The math below combines
     * three independent motions into one set of motor powers:
     *
     *   drive  (Y axis) → forward / backward
     *   strafe (X axis) → slide left / right without rotating
     *   rotate (X axis, right stick) → spin the robot in place
     *
     * For a standard mecanum layout, the wheel power equations are:
     *
     *   NE (wheel_0) = drive - strafe - rotate
     *   SE (wheel_1) = drive + strafe - rotate
     *   SW (wheel_2) = drive - strafe + rotate
     *   NW (wheel_3) = drive + strafe + rotate
     *
     * The +/- signs come from the roller geometry:
     * - Strafing right: NE and SW get negative strafe, NE and NW get positive
     * - Rotating: right-side wheels get negative rotate, left-side get positive
     *
     * @param drive  Forward/backward input, range [-1, 1]. Positive = forward.
     * @param strafe Left/right input, range [-1, 1]. Positive = strafe right.
     * @param rotate Rotation input, range [-1, 1]. Positive = turn right.
     */
    public void drive(double drive, double strafe, double rotate) {

        // Apply deadzone to all three axes.
        // Joysticks rarely read exactly 0.0 at rest — this prevents the robot
        // from creeping forward when no input is given.
        drive  = deadzone(drive);
        strafe = deadzone(strafe);
        rotate = deadzone(rotate);

        // MECANUM WHEEL POWER EQUATIONS
        // Each wheel needs a unique combination of the three inputs.
        // The signs reflect how each wheel's rollers push the robot.
        double w0 = drive - strafe - rotate;   // NE (front-right)
        double w1 = drive + strafe - rotate;   // SE (back-right)
        double w2 = drive - strafe + rotate;   // SW (back-left)
        double w3 = drive + strafe + rotate;   // NW (front-left)

        // POWER NORMALIZATION
        // -------------------
        // When all three inputs are maxed out, their sum can exceed 1.0,
        // which is outside the valid motor power range. If we just clip at 1.0,
        // we'd lose the relative balance between wheels and the robot would
        // behave unexpectedly. Instead, we scale ALL wheels down proportionally
        // so the ratios stay correct.
        //
        // Example: if w3 = 3.0 and everything else is smaller,
        //          we divide all by 3.0 → w3 becomes 1.0, others scale down.

        double max = Math.max(
                Math.max(Math.abs(w0), Math.abs(w1)),
                Math.max(Math.abs(w2), Math.abs(w3)));

        if (max > 1.0) {   // only normalize if we're actually over the limit
            w0 /= max;
            w1 /= max;
            w2 /= max;
            w3 /= max;
        }

        // APPLY POWER to hardware via HardwareMapConfig
        // speedMultiplier scales the final output (NORMAL=1.0, PRECISION=0.3, TURBO=1.0)
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

    // Deadzone helper — returns 0 if the joystick value is too small to care about.
    // Without this, analog sticks that don't perfectly center at 0.0 would cause
    // the robot to slowly drift even when the driver isn't touching anything.
    private double deadzone(double value) {
        return Math.abs(value) > 0.05 ? value : 0;
    }
}