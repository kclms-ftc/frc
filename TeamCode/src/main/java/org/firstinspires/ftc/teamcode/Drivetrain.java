package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drivetrain {

    private DcMotorEx neWheel, seWheel, swWheel, nwWheel;

    // constructor method
    public Drivetrain(HardwareMapConfig hw) {
        neWheel = hw.wheel_0;
        seWheel = hw.wheel_1;
        swWheel = hw.wheel_2;
        nwWheel = hw.wheel_3;
    }
    public void loop(Gamepad gp) {

    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        neWheel.setPower(0);
        seWheel.setPower(0);
        swWheel.setPower(0);
        nwWheel.setPower(0);
    }
}
