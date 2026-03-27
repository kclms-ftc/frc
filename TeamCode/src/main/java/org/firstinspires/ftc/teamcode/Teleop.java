package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "new teleop")
public class Teleop extends OpMode {

    // hardware + subsystems
    private HardwareMapConfig hw;
    private Drivetrain drivetrain;
    private Intake intake;
    private Shooter shooter;
    // getting rid of turret...2

    @Override
    public void init() {
        // initialisations
        hw = new HardwareMapConfig((hardwareMap));
        drivetrain = new Drivetrain(hw);
        intake = new Intake(hw);
        shooter = new Shooter(hw);
        // getting rid of turret...2

        telemetry.addData("Status", "Init completed");
        telemetry.update();
    }

    @Override
    public void loop() {
        // main loops for all subsystems
        drivetrain.loop(gamepad1);
        intake.loop(gamepad1);
        // getting rid of turret...2
        shooter.loop(gamepad1);

        // update telemetry for all subsystems
        drivetrain.updateTelemetry(telemetry);
        intake.updateTelemetry(telemetry);
        shooter.updateTelemetry(telemetry);
//        drivetrain.updateTelemetry(telemetry);
        telemetry.update();

    }

    @Override
    public void stop() {
        // stop all subsystems immediately
        drivetrain.stop();
        intake.stop();
        shooter.stop();
        // getting rid of turret...2
    }
}