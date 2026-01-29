package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.TestBenchMotors;

@TeleOp
public class DcMotorPractice extends OpMode {
   TestBenchMotors bench = new TestBenchMotors(); // creates new instance of this class
    @Override
    public void init() {
        bench.init(hardwareMap);
    }
    @Override
    public void loop() {
        double motorSpeed = gamepad1.left_stick_y;
        bench.setMotorSpeed(motorSpeed); // controls the speed of the motor directly off the analogue joystick itself

        /*if (bench.isTouchSensorPressed()){
            bench.setMotorSpeed(0.5);
        }
        else{
            bench.setMotorSpeed(0.0);
        }
        */

        if (gamepad1.a){
            bench.setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        else if (gamepad1.b){
            bench.setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        telemetry.addData("Motor Revs", bench.getMotorRevs());
    }


}
