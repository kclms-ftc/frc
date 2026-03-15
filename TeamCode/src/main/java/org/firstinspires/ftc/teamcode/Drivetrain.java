package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// mecanum drivetrain - 4 wheels in X config
//   NW(3) ---- NE(0)
//     |  ROBOT   |
//   SW(2) ---- SE(1)
public class Drivetrain {

    public enum SpeedMode {
        NORMAL,
        PRECISION,
        TURBO
    }

    private static final double NORMAL_SPEED    = 1.0;
    private static final double PRECISION_SPEED = 0.3;
    private static final double TURBO_SPEED     = 1.0;

    private final HardwareMapConfig robot;
    private SpeedMode speedMode;
    private double speedMultiplier;

    public Drivetrain(HardwareMapConfig robot) {
        this.robot = robot;

        // right side forward, left side reversed
        robot.wheel_0.setDirection(DcMotorSimple.Direction.FORWARD);  // NE
        robot.wheel_1.setDirection(DcMotorSimple.Direction.FORWARD);  // SE
        robot.wheel_2.setDirection(DcMotorSimple.Direction.REVERSE);  // SW
        robot.wheel_3.setDirection(DcMotorSimple.Direction.REVERSE);  // NW

        robot.wheel_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // no encoders — open loop power control
        robot.wheel_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.speedMode = SpeedMode.NORMAL;
        this.speedMultiplier = NORMAL_SPEED;
    }

    // main drive method — call every loop tick
    // drive=fwd/back, strafe=left/right, rotate=spin
    public void drive(double drive, double strafe, double rotate) {
        double w0 = drive - strafe - rotate;   // NE
        double w1 = drive + strafe - rotate;   // SE
        double w2 = drive - strafe + rotate;   // SW
        double w3 = drive + strafe + rotate;   // NW

        // normalise so nothing exceeds 1.0
        double max = Math.max(
                Math.max(Math.abs(w0), Math.abs(w1)),
                Math.max(Math.abs(w2), Math.abs(w3)));

        if (max > 1.0) {
            w0 /= max;
            w1 /= max;
            w2 /= max;
            w3 /= max;
        }

        robot.wheel_0.setPower(w0 * speedMultiplier);
        robot.wheel_1.setPower(w1 * speedMultiplier);
        robot.wheel_2.setPower(w2 * speedMultiplier);
        robot.wheel_3.setPower(w3 * speedMultiplier);
    }

    public void setSpeedMode(SpeedMode mode) {
        this.speedMode = mode;
        switch (mode) {
            case NORMAL:    speedMultiplier = NORMAL_SPEED;    break;
            case PRECISION: speedMultiplier = PRECISION_SPEED; break;
            case TURBO:     speedMultiplier = TURBO_SPEED;     break;
        }
    }

    public SpeedMode getSpeedMode() {
        return speedMode;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void stop() {
        robot.wheel_0.setPower(0);
        robot.wheel_1.setPower(0);
        robot.wheel_2.setPower(0);
        robot.wheel_3.setPower(0);
    }

    public double[] getWheelPowers() {
        return new double[] {
            robot.wheel_0.getPower(),
            robot.wheel_1.getPower(),
            robot.wheel_2.getPower(),
            robot.wheel_3.getPower()
        };
    }
}
