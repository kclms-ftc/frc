package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;

/**
 * Odometry.java — Pinpoint Position Tracking Subsystem
 *
 * HOW IT WORKS:
 * -------------
 * The goBILDA Pinpoint computer reads two dead-wheel odometry pods
 * (pod_x and pod_y) and fuses their data with its internal IMU at 1500 Hz.
 * The result is a continuous, low-drift estimate of the robot's position
 * (X, Y) and heading on the field.
 *
 * This class is a thin, clean wrapper around GoBildaPinpointDriver.
 * It lives separately from Drivetrain intentionally — position tracking
 * is its own concern and may be used by autonomous, shooter aim, and
 * telemetry independently.
 *
 * HARDWARE INDEPENDENCE:
 * ----------------------
 * Like all kishi subsystems, this class never calls HardwareMap.get().
 * The Pinpoint driver reference comes in via HardwareMapConfig.
 *
 * PODS:
 *   pod_x — forward/back dead wheel (measures Y-axis travel)
 *   pod_y — left/right dead wheel   (measures X-axis travel / strafing)
 *
 * COORDINATE SYSTEM (FTC standard):
 *   X increases toward the audience wall
 *   Y increases toward the blue alliance wall
 *   Heading 0° = facing the audience (positive X direction)
 *   Set at match start with reset() or setPosition()
 */
public class Odometry {

    // ----------------------------------
    // MEMBER VARIABLES
    // ----------------------------------

    // The Pinpoint co-processor — does the heavy math internally at 1500 Hz
    private final GoBildaPinpointDriver pinpoint;

    // Tracks how often update() gets called from OpMode (diagnostic only)
    private final ElapsedTime loopTimer = new ElapsedTime();

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * @param robot Shared HardwareMapConfig — Pinpoint already initialized inside it
     */
    public Odometry(HardwareMapConfig robot) {
        this.pinpoint = robot.pinpoint;
        // pinpoint.resetPosAndIMU() was already called in HardwareMapConfig
        // so we are zeroed and calibrated by the time this constructor runs
    }

    // ----------------------------------
    // MAIN UPDATE — call every loop
    // ----------------------------------

    /**
     * update()
     *
     * Must be called once per OpMode loop iteration.
     * This tells the Pinpoint to push its latest 1500 Hz-computed position
     * estimate over I²C to the Control Hub so our code can read it.
     *
     * Without calling this, all getPosition/getVelocity calls return stale data.
     * It takes < 1ms so there is no loop-time penalty.
     */
    public void update() {
        pinpoint.update();
    }

    // ----------------------------------
    // POSITION GETTERS
    // ----------------------------------

    /**
     * Returns the full Pose2D object (X, Y, heading).
     * You can extract individual values from it using DistanceUnit / AngleUnit.
     */
    public Pose2D getPose() {
        return pinpoint.getPosition();
    }

    /** X position in millimetres (positive = right / toward audience) */
    public double getX() {
        return pinpoint.getPosition().getX(DistanceUnit.MM);
    }

    /** Y position in millimetres (positive = forward / toward blue wall) */
    public double getY() {
        return pinpoint.getPosition().getY(DistanceUnit.MM);
    }

    /** Heading in degrees. 0 = facing audience. Positive = counter-clockwise (FTC standard). */
    public double getHeadingDegrees() {
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }

    /** Heading in radians — use this for math (trig functions expect radians). */
    public double getHeadingRadians() {
        return pinpoint.getPosition().getHeading(AngleUnit.RADIANS);
    }

    // ----------------------------------
    // VELOCITY GETTERS
    // ----------------------------------

    /** X velocity in mm/s — how fast the robot is moving sideways right now */
    public double getVelocityX() {
        return pinpoint.getVelocity().getX(DistanceUnit.MM);
    }

    /** Y velocity in mm/s — how fast the robot is moving forward/backward right now */
    public double getVelocityY() {
        return pinpoint.getVelocity().getY(DistanceUnit.MM);
    }

    /** Angular velocity in degrees/s — how fast the robot is turning right now */
    public double getAngularVelocityDegPerSec() {
        return pinpoint.getVelocity().getHeading(AngleUnit.DEGREES);
    }

    // ----------------------------------
    // RAW ENCODER TICKS
    // ----------------------------------

    /**
     * Raw encoder tick count from pod_x (forward/back pod).
     * Useful for debugging if position reads look wrong —
     * compare raw ticks to expected travel distance.
     */
    public int getRawTicksX() {
        return (int) pinpoint.getEncoderX();
    }

    /**
     * Raw encoder tick count from pod_y (left/right pod).
     * 19.894 ticks = 1mm of wheel travel.
     */
    public int getRawTicksY() {
        return (int) pinpoint.getEncoderY();
    }

    // ----------------------------------
    // STATUS / DIAGNOSTICS
    // ----------------------------------

    /**
     * Returns the Pinpoint device status.
     * Should be READY before you trust position data.
     * Will briefly show CALIBRATING after resetPosAndIMU().
     */
    public GoBildaPinpointDriver.DeviceStatus getStatus() {
        return pinpoint.getDeviceStatus();
    }

    /**
     * Returns the Pinpoint's internal update frequency in Hz.
     * Should hover around 1500 Hz if the I²C connection is healthy.
     * Significantly lower = wiring issue or port conflict.
     */
    public double getPinpointFrequencyHz() {
        return pinpoint.getFrequency();
    }

    // ----------------------------------
    // RESET / CALIBRATION
    // ----------------------------------

    /**
     * Zeros both the position estimate AND re-calibrates the IMU.
     * Use this at the START of an OpMode only — takes a few hundred ms
     * to calibrate. Do not call mid-match.
     */
    public void reset() {
        pinpoint.resetPosAndIMU();
    }

    /**
     * Recalibrates the IMU without resetting the position.
     * Useful if heading starts drifting but you want to keep X/Y.
     */
    public void recalibrateIMU() {
        pinpoint.recalibrateIMU();
    }

    /**
     * Teleports the tracked position to a specific pose.
     * Useful at start of autonomous to set the robot's known starting position
     * relative to the field coordinate system.
     *
     * @param pose Pose2D with desired X (mm), Y (mm), and heading (radians)
     */
    public void setPosition(Pose2D pose) {
        pinpoint.setPosition(pose);
    }

    // ----------------------------------
    // DISTANCE UTILITY
    // ----------------------------------

    /**
     * Returns straight-line distance from current position to a target (mm).
     * Handy for: choosing flywheel speed, deciding when to stop driving, etc.
     *
     * @param targetX target X in mm
     * @param targetY target Y in mm
     * @return Euclidean distance in mm
     */
    public double distanceTo(double targetX, double targetY) {
        double dx = targetX - getX();
        double dy = targetY - getY();
        return Math.hypot(dx, dy);
    }

    /**
     * Returns the angle FROM the robot's position TO a target, in degrees.
     * Useful for turret aiming or computing a heading correction to face the goal.
     *
     * @param targetX target X in mm
     * @param targetY target Y in mm
     * @return absolute field angle to the target in degrees
     */
    public double angleTo(double targetX, double targetY) {
        double dx = targetX - getX();
        double dy = targetY - getY();
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    // ----------------------------------
    // TELEMETRY
    // ----------------------------------

    /**
     * Displays full odometry info on the Driver Station.
     * Call inside the OpMode loop alongside other subsystem telemetry.
     */
    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addLine("--- Odometry (Pinpoint) ---");
        telemetry.addData("Status",     getStatus().toString());
        telemetry.addData("Frequency",  String.format("%.0f Hz", getPinpointFrequencyHz()));
        telemetry.addData("X",          String.format("%.1f mm", getX()));
        telemetry.addData("Y",          String.format("%.1f mm", getY()));
        telemetry.addData("Heading",    String.format("%.1f °",  getHeadingDegrees()));
        telemetry.addData("Vel X",      String.format("%.1f mm/s", getVelocityX()));
        telemetry.addData("Vel Y",      String.format("%.1f mm/s", getVelocityY()));
        telemetry.addData("AngVel",     String.format("%.1f °/s",  getAngularVelocityDegPerSec()));
        telemetry.addData("Ticks X",    getRawTicksX());
        telemetry.addData("Ticks Y",    getRawTicksY());
    }
}
