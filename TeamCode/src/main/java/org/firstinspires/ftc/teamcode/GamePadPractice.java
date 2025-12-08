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
        // runs 50x a second
        double speedForward = -gamepad1.left_stick_y / 2.0;
        double differenceInX = gamepad1.left_stick_x - gamepad1.right_stick_x
        telemetry.addData("left x", gamepad1.left_stick_x);
        telemetry.addData("left y", speedForward);
        telemetry.addData("right x", gamepad1.right_stick_x);
        telemetry.addData("right y", gamepad1.right_stick_y);
        telemetry.addData("difference in x", differenceInX);

        telemetry.addData("sum of rear", gamepad1.left_trigger + gamepad1.right_trigger);

        telemetry.addData("a button", gamepad1.a);
        telemetry.addData("b button", gamepad1.b);

    }
}
