package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/*
GAMEPAD:
d pad down - toggles between normal and precision speed
button b - auto position and shoot
 */
public class Drivetrain {

    private DcMotorEx neWheel, seWheel, swWheel, nwWheel;

    // odometry
    private GoBildaPinpointDriver pinpoint;

    // These allow the driver to switch according to precisions and speed
    private enum SpeedMode {NORMAL, PRECISION};
    private boolean lastDpadDown = false;
    private SpeedMode speedMode = SpeedMode.NORMAL;
    private double speedMultiplier = 1.0;
    private double precisionSpeedMultiplier = 0.4;
    private double normalSpeedMultiplier = 1.0;
    private boolean autoMoveActive;
    private double  deadZoneValue = 0.05;
    private double targetX, targetY, targetHeading;

    // MAIN METHODS

    // constructor method
    public Drivetrain(HardwareMapConfig hw) {
        // wheel
        neWheel = hw.wheel_0;
        seWheel = hw.wheel_1;
        swWheel = hw.wheel_2;
        nwWheel = hw.wheel_3;

        // pinpoint
        pinpoint = hw.pinpoint;

        // right motors forward, left motors backward
        neWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        seWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        swWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        nwWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        // motors brake when no power rather than float
        neWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        neWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        neWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        neWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // main loop called 50 times per second
    public void loop(Gamepad gp) {

        // set speed mode according to driver
        toggleSpeedMode(gp.dpad_down);

        // check auto move button
        if (gp.b) {
            autoMoveActive = true;
        }

        // drive to target with odometry if autoMove active
        if (autoMoveActive) {
            goToTargetWithOdometry(targetX, targetY, targetHeading);
            if (targetReached()) {
                autoMoveActive = false;
            }
        }

        // if automove not active drive with gamepad
        else {
            double forward  = -gp.left_stick_y;  // forward/backward (invert Y so forward = positive)
            double strafe =  gp.left_stick_x;    // left/right
            double rotate =  gp.right_stick_x;   // rotation

            drive(forward, strafe, rotate);
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        neWheel.setPower(0);
        seWheel.setPower(0);
        swWheel.setPower(0);
        nwWheel.setPower(0);
    }

    // HELPER METHODS

    // Allows driver to switch between control styles
    public void toggleSpeedMode(boolean currentButtonState) {

        // detect if the button was just pressed
        if (currentButtonState && !lastDpadDown) {
            // toggle speedmode between normal and precision
            if (speedMode == SpeedMode.NORMAL) {
                speedMode = SpeedMode.PRECISION;
                speedMultiplier = precisionSpeedMultiplier;
            } else {
                speedMode = SpeedMode.PRECISION;
                speedMultiplier = normalSpeedMultiplier;
            }
        }

        // store current dpad state for the next loop
        lastDpadDown = currentButtonState;
    }

    public void goToTargetWithOdometry(double x, double y, double heading) {
        double currentX = pinpoint.getPosX(DistanceUnit.MM);
        double currentY = pinpoint.getPosY(DistanceUnit.MM);
        double currentHeading = pinpoint.getHeading(AngleUnit.RADIANS);

        // difference between current and target
        double errorX = x - currentX;
        double errorY = y - currentY;
        double errorHeading = heading - currentHeading;

        double sin = Math.sin(-currentHeading);
        double cos = Math.cos(-currentHeading);

        // corrects the error vector into the robot's frame
        // strafe
        double robotX = errorX * cos - errorY * sin;
        // forward
        double robotY = errorX * sin + errorY * cos;

        // proportional control (slows down as approaches point)
        // TUNE THESE
        double kP_pos = 0.01;
        double kP_heading = 0.01;
        double forward = clamp(robotY * kP_pos, -0.5, 0.5);
        double strafe = clamp(robotX * kP_pos, -0.5, 0.5);
        double rotate = clamp(errorHeading * kP_heading, -0.4, 0.4);

        // drive towards target
        drive(forward, strafe, rotate);
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    // checks if bot is in shooting position
    public boolean targetReached() {
        return true;
    }

    // returns zero if joystick value too little to care about
    private double deadzone(double value) {return Math.abs(value) > deadZoneValue ? value : 0;}

    // main drive method with calculations
    public void drive(double forward, double strafe, double rotate) {
        // apply deadzones too all three axes
        forward  = deadzone(forward);
        strafe = deadzone(strafe);
        rotate = deadzone(rotate);

        // mecanum wheels
        double w0 = forward - strafe - rotate;   // NE (front-right)
        double w1 = forward + strafe - rotate;   // SE (back-right)
        double w2 = forward - strafe + rotate;   // SW (back-left)
        double w3 = forward + strafe + rotate;   // NW (front-left)

        // normalisation -> scale all down proportionality if over 1.0
        double max = Math.max(
                Math.max(Math.abs(w0), Math.abs(w1)),
                Math.max(Math.abs(w2), Math.abs(w3)));

        if (max > 1.0) {
            w0 /= max;
            w1 /= max;
            w2 /= max;
            w3 /= max;
        }

        // apply power scaled by speedMultiplier (normal or precision)
        neWheel.setPower(w0 * speedMultiplier);
        seWheel.setPower(w1 * speedMultiplier);
        swWheel.setPower(w2 * speedMultiplier);
        nwWheel.setPower(w3 * speedMultiplier);
    }

}
