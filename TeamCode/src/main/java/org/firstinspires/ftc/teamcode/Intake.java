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

    public static double intakePower = 0.85;
    public static double outtakePower = -0.6;
    // MAIN METHODS

    // constructor method
    public Intake(HardwareMapConfig hw) {
        intakeMotor = hw.intake_motor;
    }

    // main loop called 50 times per second
    public void loop(Gamepad gp) {
        intakeMotor.setPower(intakePower);

        if(gp.dpad_up) {
            intakeMotor.setPower(outtakePower);
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        intakeMotor.setPower(0);
    }

    // HELPER METHODS
}
