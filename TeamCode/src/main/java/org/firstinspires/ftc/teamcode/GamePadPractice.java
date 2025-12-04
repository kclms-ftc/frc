package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp
public class GamePadPractice extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        // runs x50 per second
        double speedForward = -gamepad1.left_stick_y / 2.0;
        double diffXJoysticks = gamepad1.left_stick_x - gamepad1.right_stick_x;
        double sumTriggers = gamepad1.left_trigger + gamepad1.right_trigger;

        telemetry.addData("x1", gamepad1.left_stick_x);
        telemetry.addData("y1", speedForward);

        telemetry.addData("A button", gamepad1.a);
        telemetry.addData("B button", gamepad1.b);

        telemetry.addData("x2", gamepad2.right_stick_x);
        telemetry.addData("y2", gamepad1.right_stick_y);

        telemetry.addData("Diff Joysticks",diffXJoysticks);
        telemetry.addData("Sum of Triggers",sumTriggers);
    }

}

/* x++ increments by 1 --> x = x+1
    x- decrements by 1 --> x = x-1
    += add and assign --> x += 2
    *= multiply and assign
    /= divide and assign
    %= modulo and assign --> x = x % 2
 */