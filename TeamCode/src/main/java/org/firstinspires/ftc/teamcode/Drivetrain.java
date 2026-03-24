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
            double forward  = -applyDeadzone(gp.left_stick_y);  // forward/backward (invert Y so forward = positive)
            double strafe =  applyDeadzone(gp.left_stick_x);    // left/right
            double rotate =  applyDeadzone(gp.right_stick_x);   // rotation

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

    public double applyDeadzone(double value) {
        // copy method from gamma drivetrain
        return 0.0;
    }

    public void drive(double forward, double strafe, double rotate) {
        // copy method from gamma drivetrain and check it
    }

}
