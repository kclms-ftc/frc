package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Drivetrain — Mecanum 4-wheel drive subsystem.
 *
 * Accepts drive/strafe/rotate inputs in the range [-1, 1]
 * and converts them to individual wheel powers using mecanum math.
 *
 * Speed modes scale the final output:
 *   PRECISION = 30%  (fine positioning)
 *   NORMAL    = 65%  (standard driving)
 *   TURBO     = 100% (max speed)
 *
 * Wheel layout (viewed from above):
 *   wheel_3 (NW) ------- wheel_0 (NE)
 *       |                       |
 *   wheel_2 (SW) ------- wheel_1 (SE)
 */
public class Drivetrain {

    // Speed scaling
    public enum SpeedMode { PRECISION, NORMAL, TURBO }
    private static final double SPEED_PRECISION = 0.30;
    private static final double SPEED_NORMAL    = 0.65;
    private static final double SPEED_TURBO     = 1.00;

    // Joystick deadzone — inputs smaller than this are treated as zero
    private static final double DEADZONE = 0.05;

    private final DcMotorEx ne, se, sw, nw;
    private SpeedMode speedMode = SpeedMode.NORMAL;

    // Track last-set powers for telemetry
    private double[] lastPowers = {0, 0, 0, 0};

    public Drivetrain(HardwareMapConfig robot) {
        ne = robot.wheel_0;
        se = robot.wheel_1;
        sw = robot.wheel_2;
        nw = robot.wheel_3;

        // Right-side motors face the opposite direction physically
        ne.setDirection(DcMotorSimple.Direction.FORWARD);
        se.setDirection(DcMotorSimple.Direction.FORWARD);
        sw.setDirection(DcMotorSimple.Direction.REVERSE);
        nw.setDirection(DcMotorSimple.Direction.REVERSE);

        // Brake on zero power — stops immediately when stick released
        setZeroPower(DcMotor.ZeroPowerBehavior.BRAKE);

        // Run without encoder — raw power control for teleop
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // ----------------------------------
    // MAIN DRIVE METHOD
    // ----------------------------------

    /**
     * drive() — call once per loop with joystick values.
     *
     * @param drive   forward/back  [-1, 1]
     * @param strafe  left/right    [-1, 1]
     * @param rotate  turn          [-1, 1]
     */
    public void drive(double drive, double strafe, double rotate) {
        // Apply deadzones so tiny stick drift doesn't move the robot
        drive  = applyDeadzone(drive);
        strafe = applyDeadzone(strafe);
        rotate = applyDeadzone(rotate);

        // Mecanum math — each wheel gets a unique combination
        // Corrected math to be coherent and match standard mecanum directions
        double pNW = drive + strafe + rotate;
        double pNE = drive - strafe - rotate;
        double pSW = drive - strafe + rotate;
        double pSE = drive + strafe - rotate;

        // Normalize so no value exceeds 1.0 (preserves direction ratios)
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(pNE), Math.abs(pSE)),
                Math.max(Math.abs(pSW), Math.abs(pNW))));
        pNE /= max;
        pSE /= max;
        pSW /= max;
        pNW /= max;

        // Scale by speed mode
        double scale = getSpeedScale();
        pNE *= scale;
        pSE *= scale;
        pSW *= scale;
        pNW *= scale;

        ne.setPower(pNE);
        se.setPower(pSE);
        sw.setPower(pSW);
        nw.setPower(pNW);

        lastPowers = new double[]{pNE, pSE, pSW, pNW};
    }

    // ----------------------------------
    // SPEED MODE
    // ----------------------------------

    public void setSpeedMode(SpeedMode mode) { this.speedMode = mode; }
    public SpeedMode getSpeedMode()          { return speedMode; }

    private double getSpeedScale() {
        switch (speedMode) {
            case PRECISION: return SPEED_PRECISION;
            case TURBO:     return SPEED_TURBO;
            default:        return SPEED_NORMAL;
        }
    }

    // ----------------------------------
    // UTILITY
    // ----------------------------------

    public void stop() {
        ne.setPower(0);
        se.setPower(0);
        sw.setPower(0);
        nw.setPower(0);
        lastPowers = new double[]{0, 0, 0, 0};
    }

    public double[] getWheelPowers() { return lastPowers; }

    private double applyDeadzone(double v) {
        return Math.abs(v) > DEADZONE ? v : 0;
    }

    private void setZeroPower(DcMotor.ZeroPowerBehavior b) {
        ne.setZeroPowerBehavior(b);
        se.setZeroPowerBehavior(b);
        sw.setZeroPowerBehavior(b);
        nw.setZeroPowerBehavior(b);
    }

    private void setMode(DcMotor.RunMode m) {
        ne.setMode(m);
        se.setMode(m);
        sw.setMode(m);
        nw.setMode(m);
    }

    public void displayTelemetry(Telemetry t) {
        t.addData("Drive | NE SE SW NW", "%.2f %.2f %.2f %.2f",
                lastPowers[0], lastPowers[1], lastPowers[2], lastPowers[3]);
        t.addData("Drive | mode", speedMode.name());
    }
}
