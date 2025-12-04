package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class VariablePractice extends OpMode {
    @Override
    public void init() {
        int teamNumber = 23014;
        double motorSpeed = 0.75;
        boolean clawClosed = true;
        String teamName = "KCLMS Asymptotes";
        int motorAngle = 90;

        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("Motor speed", motorSpeed);
        telemetry.addData("Claw closed", clawClosed);
        telemetry.addData("Name", teamName);
        telemetry.addData("Motor angle", motorAngle);

    }

    public void loop() {

    }
}
