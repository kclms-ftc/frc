package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Automonous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@Disabled
@Automonous // /TeleOp need this for the code to show up in driver station
// class also needs to be public for the same reason
// helloworld = child of opmode
public class HelloWorld extends OpMode {

    @Override // means the opmode class were extending from has an init method,
    public void init(){ // we are overriding this init method
        telemetry.addData("Hello", "Moyin"); // all statments end in ; or {

    }
    public void loop(){

    }
}


