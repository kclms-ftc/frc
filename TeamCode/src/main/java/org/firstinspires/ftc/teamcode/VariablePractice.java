package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class VariablePractice extends OpMode {

    @Override
    public void init() {
        int teamNumber = 23014;
        double motorspeed = 0.75;
        boolean clawClosed = true;

        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("Motor Speed", motorspeed);
        telemetry.addData("Claw Closed", clawClosed);
    }

    @Override
    public void loop() {

    }
}
