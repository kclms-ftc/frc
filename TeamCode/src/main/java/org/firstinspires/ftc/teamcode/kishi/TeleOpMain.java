package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TeleOpMain — Drivetrain + Shooter + Intake
 *
 * GAMEPAD 1 (driver):
 *   Left stick Y        → forward / backward
 *   Left stick X        → strafe left / right
 *   Right stick X       → rotate
 *   Left bumper (hold)  → PRECISION speed (30%)
 *   Right bumper (hold) → TURBO speed (100%)
 *   Neither bumper      → NORMAL speed (65%)
 *   Right trigger >50%  → spin up flywheels
 *   A button            → fire one ball (non-blocking)
 *
 * GAMEPAD 2 (operator):
 *   Right bumper (hold) → intake (pull balls in)
 *   Left bumper (hold)  → eject (clear jam)
 *   Neither             → intake stopped
 */
@TeleOp(name = "TeleOp — Drivetrain + Shooter + Intake")
public class TeleOpMain extends LinearOpMode {

    private static final double JOYSTICK_DEADZONE = 0.05;

    private Robot robot;

    @Override
    public void runOpMode() {

        // Initialize all subsystems
        robot = new Robot(hardwareMap);

        telemetry.addLine("Ready — press PLAY to start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // ---- 1. INTAKE (Gamepad 2) ----
            if (gamepad2.right_bumper) {
                robot.intake.intake();
            } else if (gamepad2.left_bumper) {
                robot.intake.eject();
            } else {
                robot.intake.stop();
            }

            // ---- 2. DRIVE (Gamepad 1) ----
            double drive  = -applyDeadzone(gamepad1.left_stick_y);  // invert Y so forward = positive
            double strafe =  applyDeadzone(gamepad1.left_stick_x);
            double rotate =  applyDeadzone(gamepad1.right_stick_x);

            robot.drivetrain.drive(drive, strafe, rotate);

            // Speed mode
            if (gamepad1.left_bumper) {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.PRECISION);
            } else if (gamepad1.right_bumper) {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.TURBO);
            } else {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.NORMAL);
            }

            // ---- 3. SHOOTER (Gamepad 1) ----
            if (gamepad1.right_trigger > 0.5) {
                robot.shooter.spinUp();
            } else {
                robot.shooter.spinDown();
            }

            // Fire button — non-blocking, safe to hold
            if (gamepad1.a) {
                robot.shooter.fireBallNonBlocking();
            }

            // Must be called every loop to advance feeder state machine
            robot.shooter.updateFeeder();

            // ---- 4. TELEMETRY ----
            robot.displayTelemetry(telemetry);
        }

        // Clean stop when OpMode ends
        robot.stopAll();
    }

    private double applyDeadzone(double v) {
        return Math.abs(v) > JOYSTICK_DEADZONE ? v : 0;
    }
}
