package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {

    private DcMotorEx intakeMotor;

    // constructor method
    public Intake(HardwareMapConfig hw) {
        intakeMotor = hw.intake_motor;
    }
    public void loop(Gamepad gp) {

    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        intakeMotor.setPower(0);
    }
}
