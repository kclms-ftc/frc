package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Turret;

@TeleOp
public class TurretTest extends OpMode {
    private Turret turret;

    @Override
    public void init() {
        turret = new Turret(hardwareMap);
    }

    @Override
    public void loop() {
        turret.manualControl(gamepad1);
        turret.addTelemetry(telemetry);
    }
}
