package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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

    public Robot(HardwareMap hw) {
        HardwareMapConfig config = new HardwareMapConfig(hw);

        drivetrain = new Drivetrain(config);
        shooter    = new Shooter(config);
        intake     = new Intake(config);
    }

    /** Cut power to all active subsystems. Call at end of OpMode. */
    public void stopAll() {
        drivetrain.stop();
        shooter.stop();
        intake.stop();
    }

    /** Push all subsystem data to Driver Station in one call. */
    public void displayTelemetry(Telemetry t) {
        drivetrain.displayTelemetry(t);
        shooter.displayTelemetry(t);
        intake.displayTelemetry(t);
        t.update();
    }
}
