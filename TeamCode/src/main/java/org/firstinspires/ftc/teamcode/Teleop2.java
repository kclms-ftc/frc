package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Teleop2 - Manual")
public class Teleop2 extends OpMode {

    // --- drivetrain ---
    private DcMotorEx neWheel, seWheel, swWheel, nwWheel;

    // --- intake ---
    private DcMotorEx intakeMotor;

    private enum IntakeMode {
        OFF, LOW, HIGH
    }

    private IntakeMode intakeMode = IntakeMode.OFF;
    private boolean lastTriangle = false;

    // --- shooter ---
    private DcMotorEx shooterMotor0, shooterMotor1;

    // --- servos ---
    private Servo feederServo, stopperServo;
    private boolean feederUp = false;
    private boolean stopperOpen = false;
    private boolean lastX = false;
    private boolean lastR1 = false;

    @Override
    public void init() {

        neWheel = hardwareMap.get(DcMotorEx.class, "wheel_0"); // front-right
        seWheel = hardwareMap.get(DcMotorEx.class, "wheel_1"); // back-right
        swWheel = hardwareMap.get(DcMotorEx.class, "wheel_2"); // back-left
        nwWheel = hardwareMap.get(DcMotorEx.class, "wheel_3"); // front-left

        neWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        seWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        swWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        nwWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        neWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        seWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        swWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        nwWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // intake
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake_motor");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // shooter
        shooterMotor0 = hardwareMap.get(DcMotorEx.class, "shooter_motor_0");
        shooterMotor1 = hardwareMap.get(DcMotorEx.class, "shooter_motor_1");

        // servos
        feederServo = hardwareMap.get(Servo.class, "feeder_servo");
        stopperServo = hardwareMap.get(Servo.class, "stopper_servo");

        telemetry.addData("Status", "Ready");
        telemetry.update();
    }

    // ---

    @Override
    public void loop() {

        // --- DRIVETRAIN ---

        double fwd = -gamepad1.left_stick_y;
        double str = gamepad1.left_stick_x;
        double rot = gamepad1.right_stick_x;
        driveMecanum(fwd, str, rot);

        // --- INTAKE ---
        if (gamepad1.y && !lastTriangle) {
            switch (intakeMode) {
                case OFF:
                    intakeMode = IntakeMode.LOW;
                    break;
                case LOW:
                    intakeMode = IntakeMode.HIGH;
                    break;
                case HIGH:
                    intakeMode = IntakeMode.OFF;
                    break;
            }
        }
        lastTriangle = gamepad1.y;

        switch (intakeMode) {
            case OFF:
                intakeMotor.setPower(0);
                break;
            case LOW:
                intakeMotor.setPower(0.2);
                break;
            case HIGH:
                intakeMotor.setPower(1.0);
                break;
        }

        double shootPower = gamepad1.left_trigger > 0.9 ? -1.0 : 0.0;
        shooterMotor0.setPower(shootPower);
        shooterMotor1.setPower(shootPower);

        if (gamepad1.x && !lastX) {
            feederUp = !feederUp;
            feederServo.setPosition(feederUp ? 1.0 : 0.0);
        }
        lastX = gamepad1.x;

        if (gamepad1.right_bumper && !lastR1) {
            stopperOpen = !stopperOpen;
            stopperServo.setPosition(stopperOpen ? 1.0 : 0.0);
        }
        lastR1 = gamepad1.right_bumper;

        // drivetrain inputs
        telemetry.addLine("-- DRIVE --");
        telemetry.addData("fwd/str/rot", "%.2f / %.2f / %.2f", fwd, str, rot);
        telemetry.addData("wheel vel NE/SE", "%.0f / %.0f", neWheel.getVelocity(), seWheel.getVelocity());
        telemetry.addData("wheel vel SW/NW", "%.0f / %.0f", swWheel.getVelocity(), nwWheel.getVelocity());

        // intake
        telemetry.addLine("-- INTAKE --");
        telemetry.addData("mode", intakeMode);
        telemetry.addData("velocity", "%.0f ticks/s", intakeMotor.getVelocity());

        // shooter
        telemetry.addLine("-- SHOOTER --");
        telemetry.addData("state", shootPower != 0 ? "FIRING" : "idle");
        telemetry.addData("L2 trigger", "%.2f", gamepad1.left_trigger);
        telemetry.addData("vel motor0", "%.0f ticks/s", shooterMotor0.getVelocity());
        telemetry.addData("vel motor1", "%.0f ticks/s", shooterMotor1.getVelocity());

        // servos
        telemetry.addLine("-- SERVOS --");
        telemetry.addData("feeder", "%s  (pos %.2f)", feederUp ? "UP" : "down", feederServo.getPosition());
        telemetry.addData("stopper", "%s  (pos %.2f)", stopperOpen ? "OPEN" : "closed", stopperServo.getPosition());

        telemetry.update();
    }

    @Override
    public void stop() {
        // cut all power immediately
        neWheel.setPower(0);
        seWheel.setPower(0);
        swWheel.setPower(0);
        nwWheel.setPower(0);
        intakeMotor.setPower(0);
        shooterMotor0.setPower(0);
        shooterMotor1.setPower(0);
    }

    private void driveMecanum(double fwd, double str, double rot) {
        double w0 = fwd - str - rot; // NE (front-right)
        double w1 = fwd + str - rot; // SE (back-right)
        double w2 = fwd - str + rot; // SW (back-left)
        double w3 = fwd + str + rot; // NW (front-left)

        double max = Math.max(Math.max(Math.abs(w0), Math.abs(w1)),
                Math.max(Math.abs(w2), Math.abs(w3)));
        if (max > 1.0) {
            w0 /= max;
            w1 /= max;
            w2 /= max;
            w3 /= max;
        }

        neWheel.setPower(w0);
        seWheel.setPower(w1);
        swWheel.setPower(w2);
        nwWheel.setPower(w3);
    }
}
