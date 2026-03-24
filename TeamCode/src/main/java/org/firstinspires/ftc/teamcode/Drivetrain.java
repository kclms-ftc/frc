package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
d pad down - toggles between normal and precision speed
button b - auto position and shoot
 */
public class Drivetrain {

    private DcMotorEx neWheel, seWheel, swWheel, nwWheel;

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
        neWheel = hw.wheel_0;
        seWheel = hw.wheel_1;
        swWheel = hw.wheel_2;
        nwWheel = hw.wheel_3;
    }
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
        // create this method
    }

    public boolean targetReached() {
        // create this method
        // checks with odometry if bot is in our shooting position
        return true;
    }

    // returns zero if joystick value too little to care about
    private double deadzone(double value) {return Math.abs(value) > deadZoneValue ? value : 0;}

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
