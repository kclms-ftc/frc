package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
d pad up held - intake reversed
 */

public class Intake {

    private DcMotorEx intakeMotor;

    public static double intakePower = 0.6; // change to default intake motor speed
    // MAIN METHODS

    // constructor method
    public Intake(HardwareMapConfig hw) {
        intakeMotor = hw.intake_motor;
    }

    public void loop(Gamepad gp) {
        intakeMotor.setPower(intakePower);

        if(gp.dpad_up) {
            intakeMotor.setPower(-intakePower);
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        intakeMotor.setPower(0);
    }

    // HELPER METHODS
}
