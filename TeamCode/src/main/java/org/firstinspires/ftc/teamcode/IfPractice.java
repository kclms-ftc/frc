package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class IfPractice extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        double motorSpeed = gamepad1.left_stick_y;

        if (!gamepad1.a) {
            motorSpeed *= 0.5;
        }


        /*boolean aButton = gamepad1.a; // press TRUE, depress FALSE

        if (aButton) {
            telemetry.addData("A Button", "pressed!");

        } else {
            telemetry.addData("A Button", "NOT Pressed");
        }
        telemetry.addData("A Button State", aButton);*/
    }
}

/*
1. make turbo button. If the a button is NOT pressed, multiply the motor speed by 0.5, otherwise, use the standard soeed
 */

/*
Boolean Operators in Java:
AND - &&
OR - ||
NOT - !
 */