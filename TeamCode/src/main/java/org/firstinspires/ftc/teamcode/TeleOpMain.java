package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TeleOp mode for testing drivetrain + shooter
 *
 * Features:
 * - Mecanum drivetrain with deadzone and speed modes
 * - Shooter spin up / spin down
 * - Non-blocking feeder control
 * - Telemetry for debugging
 */
@TeleOp(name = "TeleOp Drivetrain + Shooter")
public class TeleOpMain extends LinearOpMode {

    // ----------------------------------
    // MEMBER VARIABLES
    // ----------------------------------
    private Robot robot;  // Our central Robot class (contains drivetrain + shooter + vision + etc.)

    // Deadzone for joysticks to prevent drift
    private static final double JOYSTICK_DEADZONE = 0.05;

    // ----------------------------------
    // UTILITY METHOD
    // ----------------------------------
    /**
     * Applies a deadzone to joystick input
     * If absolute value is below deadzone, return 0
     * Otherwise, return the original value
     */
    private double applyDeadzone(double value) {
        return Math.abs(value) > JOYSTICK_DEADZONE ? value : 0;
    }

    // ----------------------------------
    // MAIN LOOP
    // ----------------------------------
    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize robot and all subsystems (drivetrain, shooter, intake, etc.)
        robot = new Robot(hardwareMap);

        // Wait for driver to press play
        telemetry.addLine("Ready to start!");
        telemetry.update();
        waitForStart();

        // ----------------------------------
        // LOOP: Runs repeatedly until opMode ends
        // ----------------------------------
        while (opModeIsActive()) {

            // Update odometry (ask Pinpoint for latest position)
            robot.odometry.update();

            // ----------------------
            // 1. DRIVE CONTROL
            // ----------------------
            // Read joystick values
            // Left stick: forward/back (y) and left/right strafe (x)
            // Right stick: rotation (x)
            double drive  = -applyDeadzone(gamepad1.left_stick_y); // forward/back
            double strafe = applyDeadzone(gamepad1.left_stick_x);  // left/right
            double rotate = applyDeadzone(gamepad1.right_stick_x); // rotation

            // Pass processed values to drivetrain
            robot.drivetrain.drive(drive, strafe, rotate);

            // ----------------------
            // 2. DRIVE SPEED MODE
            // ----------------------
            // Change drivetrain speed mode using gamepad buttons
            if (gamepad1.left_bumper) robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.PRECISION);
            else if (gamepad1.right_bumper) robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.TURBO);
            else robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.NORMAL);

            // ----------------------
            // 3. SHOOTER CONTROL
            // ----------------------
            // Right trigger spins up shooter
            if (gamepad1.right_trigger > 0.5) {
                robot.shooter.spinUp();  // default high goal velocity
            } else {
                robot.shooter.spinDown(); // stop shooter
            }

            // ----------------------
            // 4. FEEDER CONTROL
            // ----------------------
            // Press 'A' to fire a ball (non-blocking)
            if (gamepad1.a) {
                robot.shooter.fireBallNonBlocking();
            }

            // Progress feeder state machine (must call every loop)
            robot.shooter.updateFeeder();

            // ----------------------
            // 5. TELEMETRY
            // ----------------------
            // Show drivetrain info
            double[] powers = robot.drivetrain.getWheelPowers();
            telemetry.addData("Wheel Powers", "NE: %.2f, SE: %.2f, SW: %.2f, NW: %.2f",
                    powers[0], powers[1], powers[2], powers[3]);

            telemetry.addData("Drive Mode", robot.drivetrain.getSpeedMode().name());

            // Show subsystem telemetry
            robot.odometry.displayTelemetry(telemetry);
            robot.shooter.displayTelemetry(telemetry);

            telemetry.update();
        }

        // ----------------------------------
        // 6. STOP ALL
        // ----------------------------------
        // Emergency stop all subsystems when teleop ends
        robot.stopAll();
    }
}