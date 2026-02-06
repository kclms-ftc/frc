package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="Flywheel Pro Diagnostic")

public class DcMotorPractice extends OpMode {
    MotorTest motor1 = new MotorTest();

    @Override
    public void init() {
        motor1.init(hardwareMap, "test_motor");
        motor1.setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        motor1.setMotorSpeed(0.5);
        telemetry.addData("Motor revs", motor1.getMotorRevs());
    }
}
