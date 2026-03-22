package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter {

    private DcMotorEx shooterMotor0, shooterMotor1;
    private Servo feederServo, stopperServo;

    // MAIN METHODS

    // constructor method
    public Shooter(HardwareMapConfig hw) {
        shooterMotor0 = hw.shooter_motor_0;
        shooterMotor1 = hw.shooter_motor_1;
        feederServo = hw.feeder_servo;
        stopperServo = hw.stopper_servo;
    }
    public void loop(Gamepad gp) {

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
}
