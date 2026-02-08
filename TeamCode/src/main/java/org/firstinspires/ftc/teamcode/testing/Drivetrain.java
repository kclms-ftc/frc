package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Mecanum Drivetrain", group = "Linear Opmode")
public class Drivetrain extends LinearOpMode {

    private DcMotor wheel_0;
    private DcMotor wheel_1;
    private DcMotor wheel_2;
    private DcMotor wheel_3;

    private ElapsedTime runtime = new ElapsedTime();

    private double speedMultiplier = 1.0;
    private final double NORMAL_SPEED = 1.0;
    private final double PRECISION_SPEED = 0.3;
    private final double TURBO_SPEED = 1.0;

    @Override
    public void runOpMode() {

        initializeHardware();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Wheel Config", "NE:0 SE:1 SW:2 NW:3");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {

            handleSpeedModes();

            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;

            calculateAndSetWheelPowers(drive, strafe, rotate);

            displayTelemetry(drive, strafe, rotate);
        }
    }

    private void initializeHardware() {
        wheel_0 = hardwareMap.get(DcMotor.class, "wheel_0");
        wheel_1 = hardwareMap.get(DcMotor.class, "wheel_1");
        wheel_2 = hardwareMap.get(DcMotor.class, "wheel_2");
        wheel_3 = hardwareMap.get(DcMotor.class, "wheel_3");

        wheel_0.setDirection(DcMotorSimple.Direction.FORWARD);
        wheel_1.setDirection(DcMotorSimple.Direction.FORWARD);
        wheel_2.setDirection(DcMotorSimple.Direction.REVERSE);
        wheel_3.setDirection(DcMotorSimple.Direction.REVERSE);

        wheel_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel_2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel_3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        wheel_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheel_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheel_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheel_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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

        double wheel_0_power = drive - strafe - rotate;
        double wheel_1_power = drive + strafe - rotate;
        double wheel_2_power = drive - strafe + rotate;
        double wheel_3_power = drive + strafe + rotate;

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

        wheel_0.setPower(wheel_0_power);
        wheel_1.setPower(wheel_1_power);
        wheel_2.setPower(wheel_2_power);
        wheel_3.setPower(wheel_3_power);
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

        telemetry.addData("NE (wheel_0)", String.format("%.2f", wheel_0.getPower()));
        telemetry.addData("SE (wheel_1)", String.format("%.2f", wheel_1.getPower()));
        telemetry.addData("SW (wheel_2)", String.format("%.2f", wheel_2.getPower()));
        telemetry.addData("NW (wheel_3)", String.format("%.2f", wheel_3.getPower()));
        telemetry.addLine();

        telemetry.addData("Controls", "Left Stick: Drive/Strafe");
        telemetry.addData("", "Right Stick X: Rotate");
        telemetry.addData("", "Right Bumper: Precision");
        telemetry.addData("", "Left Bumper: Turbo");

        telemetry.update();
    }
}
