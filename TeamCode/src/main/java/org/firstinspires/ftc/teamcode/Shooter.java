package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
x button - triggers shooting sequence
*/

public class Shooter {

    private DcMotorEx shooterMotor0, shooterMotor1;
    private Servo feederServo, stopperServo;

    public boolean shootingCurrently = false;
    // MAIN METHODS

    // constructor method
    public Shooter(HardwareMapConfig hw) {
        shooterMotor0 = hw.shooter_motor_0;
        shooterMotor1 = hw.shooter_motor_1;
        feederServo = hw.feeder_servo;
        stopperServo = hw.stopper_servo;
    }
    public void loop(Gamepad gp) {
        if(gp.x) {
            if(!shootingCurrently) startShootingSequence();
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        shooterMotor0.setPower(0);
        shooterMotor1.setPower(0);
        feederServo.setPosition(0);  // safe position
        stopperServo.setPosition(0); // safe position
    }

    // HELPER METHODS

    public void startShootingSequence() {
        // use shooter gamma to make this and more helper functions probably
    }
}
