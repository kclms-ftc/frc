package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

// main driver logic
// DO NOT put any motor.setPower nonsense here
// just mapping inputs to the subsystem methods
@TeleOp(name = "TeleOp Main", group = "Competition")
public class TeleOpMain1 extends LinearOpMode {

    // aiming tag
    private static final int TARGET_TAG_ID = 20;

    @Override
    public void runOpMode() {
        Robot1 robot = new Robot1(hardwareMap, true);   // live preview enabled
        ElapsedTime runtime = new ElapsedTime();

        boolean prevY          = false;
        boolean prevLeftBumper = false;
        boolean autoAimActive  = false;

        // make sure we have a stream before starting. check the ds screen if it fails
        telemetry.addData("status", "waitng for camera...");
        telemetry.update();

        ElapsedTime initTimer = new ElapsedTime();
        while (!robot.vision.isStreaming() && initTimer.seconds() < 5 && !isStopRequested()) {
            idle();
        }
        if (robot.vision.isStreaming()) {
            robot.vision.configureCameraControls();
        }

        telemetry.addData("status", "all good hit play");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            // get the latest camera frame or nothing works
            robot.vision.update();
            AprilTagDetection target = robot.vision.getTagById(TARGET_TAG_ID);

            // driving binds on main gamepad
            robot.drivetrain.drive(
                    gamepad1.left_stick_x,
                    -gamepad1.left_stick_y,
                    gamepad1.right_stick_x);

            if (gamepad1.right_bumper) {
                robot.drivetrain.setSpeedMode(Drivetrain1.SpeedMode.PRECISION);
            } else if (gamepad1.left_bumper) {
                robot.drivetrain.setSpeedMode(Drivetrain1.SpeedMode.TURBO);
            } else {
                robot.drivetrain.setSpeedMode(Drivetrain1.SpeedMode.NORMAL);
            }

            // intake binds on operator gamepad
            if (gamepad2.dpad_up) {
                robot.intake.intakeIn();
            } else if (gamepad2.dpad_down) {
                robot.intake.intakeReverse();
            } else if (gamepad2.dpad_left || gamepad2.dpad_right) {
                robot.intake.intakeOff();
            }

            // auto aiming logic (toggled by Y)
            if (gamepad2.y && !prevY) {
                autoAimActive = !autoAimActive;
            }
            prevY = gamepad2.y;

            if (autoAimActive) {
                robot.turret.autoAim(target);
            } else {
                robot.turret.manualControl(-gamepad2.right_stick_x);
            }

            // spinning the flywheels
            if (gamepad2.left_bumper && !prevLeftBumper) {
                robot.shooter.toggleSpin();
            }
            prevLeftBumper = gamepad2.left_bumper;

            // shoot a ball without freezing the main thread
            robot.shooter.updateFeeder();    
            if (gamepad2.right_bumper && robot.shooter.isReadyToFire()) {
                robot.shooter.fireBallNonBlocking();
            }

            telemetry.addData("mode", robot.drivetrain.getSpeedMode().name());
            telemetry.addData("intake", robot.intake.getState().name());
            telemetry.addData("auto-arm", autoAimActive ? "ACTIVE" : "manual");
            telemetry.addLine();

            robot.vision.displayTelemetry(target, telemetry);
            robot.turret.displayTelemetry(telemetry);
            robot.shooter.displayTelemetry(telemetry);
            telemetry.update();
        }

        robot.stopAll();
    }
}
