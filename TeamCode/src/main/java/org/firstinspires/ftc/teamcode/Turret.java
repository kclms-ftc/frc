package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Turret {
    private DcMotorEx turretMotor;

    // MAIN METHODS

    // constructor method
    public Turret(HardwareMapConfig hw) {
        turretMotor = hw.turret_motor;
    }

    public void loop(Gamepad gp, boolean shooterActive) {
        if (shooterActive) {
            if (hasValidTarget()) {
                moveToTarget();
            } else {
                setDefault();
            }
        } else {
            setDefault();
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        turretMotor.setPower(0);
    }

    // HELPER METHODS

    private boolean hasValidTarget() {
        // implement from gamma vision and gamma turret
        return false;
    }

    private void moveToTarget() {
        // implement from gamma vision and gamma turret
    }

    private void setDefault() {
        // set position back to start from gamma turret
    }
}
