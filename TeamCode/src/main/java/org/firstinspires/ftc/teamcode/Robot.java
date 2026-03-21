package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

/**
 * Robot — central container for all subsystems.
 *
 * OpModes create one Robot object and use:
 *   robot.drivetrain.*
 *   robot.shooter.*
 *   robot.intake.*
 *
 * Hardware is looked up once (in HardwareMapConfig) and
 * shared to every subsystem — nothing else calls hw.get().
 */
public class Robot {

    public final Drivetrain drivetrain;
    public final Shooter    shooter;
    public final Intake     intake;
    public final Turret     turret;
    public final Vision     vision;

    public Robot(HardwareMap hw) {
        HardwareMapConfig config = new HardwareMapConfig(hw);

        drivetrain = new Drivetrain(config);
        shooter    = new Shooter(config);
        intake     = new Intake(config);
        turret     = new Turret(config);
        vision     = new Vision(config);
    }

    /** Cut power to all active subsystems. Call at end of OpMode. */
    public void stopAll() {
        drivetrain.stop();
        shooter.stop();
        intake.stop();
        turret.stop();
        vision.stop();
    }

    /** Push all subsystem data to Driver Station in one call. */
    public void displayTelemetry(Telemetry t) {
        drivetrain.displayTelemetry(t);
        shooter.displayTelemetry(t);
        intake.displayTelemetry(t);
        turret.displayTelemetry(t);
        vision.displayTelemetry(t);
        t.update();
    }
}
