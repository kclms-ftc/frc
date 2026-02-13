package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Mecanum Drivetrain", group = "Linear Opmode")
public class Drivetrain extends LinearOpMode {

    // Removed individual DcMotor declarations
    private HardwareMapConfig robot; // Use HardwareMapConfig

    private ElapsedTime runtime = new ElapsedTime();

    private double speedMultiplier = 1.0;
    private final double NORMAL_SPEED = 1.0;
    private final double PRECISION_SPEED = 0.3;
    private final double TURBO_SPEED = 1.0;

    @Override
    public void runOpMode() {

        // Initialize HardwareMapConfig
        robot = new HardwareMapConfig(hardwareMap);
        initializeHardware(); // Helper to set directions/modes

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Wheel Config", "NE:0 SE:1 SW:2 NW:3");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            handleSpeedModes();

            // Inputs
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            // Rotation: Right Stick X (Clockwise / Counter-Clockwise)
            double rotate = gamepad1.right_stick_x;

            // Add D-Pad control for "left gameoad" (Left Gamepad/D-Pad)
            if (gamepad1.dpad_up) {
                drive += 1.0;
            } else if (gamepad1.dpad_down) {
                drive -= 1.0;
            }

            if (gamepad1.dpad_left) {
                strafe -= 1.0;
            } else if (gamepad1.dpad_right) {
                strafe += 1.0;
            }

            // Clamp values to -1.0 to 1.0
            drive = Math.max(-1.0, Math.min(1.0, drive));
            strafe = Math.max(-1.0, Math.min(1.0, strafe));

            if (Math.abs(drive) < 0.05) drive = 0;
            if (Math.abs(strafe) < 0.05) strafe = 0;
            if (Math.abs(rotate) < 0.05) rotate = 0;

            calculateAndSetWheelPowers(drive, strafe, rotate);

            displayTelemetry(drive, strafe, rotate);
        }
    }

    private void initializeHardware() {
        // Directions
        // 0: NE (Right Front) -> Forward
        // 1: SE (Right Back) -> Forward
        // 2: SW (Left Back) -> Reverse
        // 3: NW (Left Front) -> Reverse
        robot.wheel_0.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.wheel_1.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.wheel_2.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.wheel_3.setDirection(DcMotorSimple.Direction.REVERSE);

        // Zero Power Behavior
        robot.wheel_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.wheel_3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Run Mode
        robot.wheel_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.wheel_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void handleSpeedModes() {
        if (gamepad1.right_bumper) {
            speedMultiplier = PRECISION_SPEED;
        } else if (gamepad1.left_bumper) {
            speedMultiplier = TURBO_SPEED;
        } else {
            speedMultiplier = NORMAL_SPEED;
        }
    }

    private void calculateAndSetWheelPowers(double drive, double strafe, double rotate) {

        // Mecanum drive calculations
        // Calculate wheel powers.
        // Note: This assumes standard X-configuration for Mecanum wheels.
        // To rotate Clockwise (Right Stick > 0), Left wheels move Forward (+), Right
        // wheels Backward (-).
        // To rotate Counter-Clockwise (Right Stick < 0), Left wheels Backward (-),
        // Right wheels Forward (+).
        double wheel_0_power = drive - strafe - rotate; // NE (Right Front) - subtract rotate
        double wheel_1_power = drive + strafe - rotate; // SE (Right Back) - subtract rotate
        double wheel_2_power = drive - strafe + rotate; // SW (Left Back) + add rotate
        double wheel_3_power = drive + strafe + rotate; // NW (Left Front) + add rotate

        double maxPower = Math.max(
                Math.max(Math.abs(wheel_0_power), Math.abs(wheel_1_power)),
                Math.max(Math.abs(wheel_2_power), Math.abs(wheel_3_power)));

        if (maxPower > 1.0) {
            wheel_0_power /= maxPower;
            wheel_1_power /= maxPower;
            wheel_2_power /= maxPower;
            wheel_3_power /= maxPower;
        }

        wheel_0_power *= speedMultiplier;
        wheel_1_power *= speedMultiplier;
        wheel_2_power *= speedMultiplier;
        wheel_3_power *= speedMultiplier;

        robot.wheel_0.setPower(wheel_0_power);
        robot.wheel_1.setPower(wheel_1_power);
        robot.wheel_2.setPower(wheel_2_power);
        robot.wheel_3.setPower(wheel_3_power);
    }

    private void displayTelemetry(double drive, double strafe, double rotate) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addLine();

        String speedMode = "Normal";
        if (gamepad1.right_bumper)
            speedMode = "Precision";
        if (gamepad1.left_bumper)
            speedMode = "Turbo";
        telemetry.addData("Speed Mode", speedMode + " (" + String.format("%.1f%%", speedMultiplier * 100) + ")");
        telemetry.addLine();

        telemetry.addData("Drive (Forward/Back)", String.format("%.2f", drive));
        telemetry.addData("Strafe (Left/Right)", String.format("%.2f", strafe));
        telemetry.addData("Rotate", String.format("%.2f", rotate));
        telemetry.addLine();

        telemetry.addData("NE (wheel_0)", String.format("%.2f", robot.wheel_0.getPower()));
        telemetry.addData("SE (wheel_1)", String.format("%.2f", robot.wheel_1.getPower()));
        telemetry.addData("SW (wheel_2)", String.format("%.2f", robot.wheel_2.getPower()));
        telemetry.addData("NW (wheel_3)", String.format("%.2f", robot.wheel_3.getPower()));
        telemetry.addLine();

        telemetry.addData("Controls", "Left Stick/D-Pad: Drive/Strafe");
        telemetry.addData("", "Right Stick X: Rotate");
        telemetry.addData("", "Right Bumper: Precision");
        telemetry.addData("", "Left Bumper: Turbo");

        telemetry.update();
    }
}
