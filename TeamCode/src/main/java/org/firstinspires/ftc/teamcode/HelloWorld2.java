package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// Opmodes -> e.g. teleop / autonomous
// all opmodes need 2 methods -> init + loop

@TeleOp // need @teleop or @autonomous for all driver station code
public class HelloWorld2 extends OpMode{

    @Override // overrides og codes opmode
    public void init() { // void = not doing anything
        telemetry.addData("Hello", "World"); // all end in ; or {
    }

    @Override
    public void loop() {

    }
}
