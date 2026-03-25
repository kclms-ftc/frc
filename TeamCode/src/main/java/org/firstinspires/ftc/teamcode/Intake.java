package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
d pad up held - intake reversed
a - toggle low or normal intake speed
 */

public class Intake {

    private DcMotorEx intakeMotor;

    private static double intakePower = 0.85;
    private static double lowIntakePower = 0.3;
    private static double outtakePower = -0.6;

    private boolean lowSpeedMode = false;
    private boolean lastToggleButton = false;

    // MAIN METHODS

    // constructor method
    public Intake(HardwareMapConfig hw) {
        intakeMotor = hw.intake_motor;
    }

    // main loop called 50 times per second
    public void loop(Gamepad gp) {
        // toggle intake speed
        if (gp.left_bumper && !lastToggleButton) {
            lowSpeedMode = !lowSpeedMode;
        }
        lastToggleButton = gp.left_bumper;

        if (gp.dpad_up) {
            intakeMotor.setPower(outtakePower);
            return;
        }
        if (lowSpeedMode) {
            intakeMotor.setPower(lowIntakePower);
        } else {
            intakeMotor.setPower(intakePower);
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        intakeMotor.setPower(0);
    }

    // HELPER METHODS
}
