package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
    // declare type, then assign name then /n, assign value
    int x;
    double motorSpeed; // float
    boolean clawClosed

    x = 8;
    motorSpeed = 0.5;
    clawClosed = true;

 */

@TeleOp
public class VariablesBasics extends OpMode {
    @Override
    public void init() {
        int teamNumber = 200;
        double motorSpeed = 0.75;
        boolean clawClosed = true;
        String name = "Moyin";
        int motorAngle = 65;

        telemetry.addData("Team number", teamNumber);
        telemetry.addData("Motor speed", motorSpeed);
        telemetry.addData("Claw state", clawClosed);
        telemetry.addData("Name", name);
        telemetry.addData("Motor angle", motorAngle)
    }

    @Override
    public void loop() {

    }
}
