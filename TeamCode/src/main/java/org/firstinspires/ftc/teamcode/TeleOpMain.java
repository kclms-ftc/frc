package org.firstinspires.ftc.teamcode;

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
 * GAMEPAD 1 (driver/operator):
 *   Left stick Y        → forward / backward (drivetrain)
 *   Left stick X        → strafe left / right (drivetrain)
 *   Right stick X       → rotate (drivetrain)
 *   D-Pad Down          → PRECISION speed (30%)
 *   D-Pad Up            → TURBO speed (100%)
 *   (Neither)           → NORMAL speed (65%)
 *   Right trigger >50%  → spin up flywheels (shooter)
 *   A button            → fire one ball (non-blocking)
 *   Left bumper (hold)  → rotate turret left (counter-clockwise)
 *   Right bumper (hold) → rotate turret right (clockwise)
 *   Y button            → cycle intake (OFF -> INTAKE -> EJECT)
 */
@TeleOp(name = "TeleOp — Single Gamepad")
public class TeleOpMain extends LinearOpMode {

    private static final double JOYSTICK_DEADZONE = 0.05;

    private Robot robot;

    // Intake state machine
    private enum IntakeState { OFF, INTAKE, EJECT }
    private IntakeState intakeState = IntakeState.OFF;
    private boolean lastY = false;

    @Override
    public void runOpMode() {

        // Initialize all subsystems
        robot = new Robot(hardwareMap);

        telemetry.addLine("Ready — press PLAY to start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // ---- 1. INTAKE (Gamepad 1 Y Toggle) ----
            boolean currentY = gamepad1.y;
            if (currentY && !lastY) {
                if (intakeState == IntakeState.OFF) {
                    intakeState = IntakeState.INTAKE;
                } else if (intakeState == IntakeState.INTAKE) {
                    intakeState = IntakeState.EJECT;
                } else {
                    intakeState = IntakeState.OFF;
                }
            }
            lastY = currentY;

            switch (intakeState) {
                case INTAKE: robot.intake.intake(); break;
                case EJECT:  robot.intake.eject(); break;
                case OFF:    robot.intake.stop(); break;
            }

            // ---- 5. TURRET (Gamepad 1 Bumpers L1/R1) ----
            if (gamepad1.right_bumper) {
                robot.turret.rotateRight();
            } else if (gamepad1.left_bumper) {
                robot.turret.rotateLeft();
            } else {
                robot.turret.stop();
            }

            // ---- 2. DRIVE (Gamepad 1) ----
            double drive  = -applyDeadzone(gamepad1.left_stick_y);  // invert Y so forward = positive
            double strafe =  applyDeadzone(gamepad1.left_stick_x);
            double rotate =  applyDeadzone(gamepad1.right_stick_x);

            robot.drivetrain.drive(drive, strafe, rotate);

            // Speed mode (Gamepad 1 D-Pad)
            if (gamepad1.dpad_down) {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.PRECISION);
            } else if (gamepad1.dpad_up) {
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
