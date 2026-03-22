package org.firstinspires.ftc.teamcode.gamma;

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
public class gammaRobot {

    public final gammaDrivetrain drivetrain;
    public final gammaShooter shooter;
    public final gammaIntake intake;
    public final gammaTurret turret;
    public final gammaVision vision;
    public final gammaOdometry odometry;

    public gammaRobot(HardwareMap hw) {
        gammaHardwareMapConfig config = new gammaHardwareMapConfig(hw);

        drivetrain = new gammaDrivetrain(config);
        shooter    = new gammaShooter(config);
        intake     = new gammaIntake(config);
        turret     = new gammaTurret(config);
        vision     = new gammaVision(config);
        odometry   = new gammaOdometry(config);
    }

    /** Cut power to all active subsystems. Call at end of OpMode. */
    public void stopAll() {
        drivetrain.stop();
        shooter.stop();
        intake.stop();
        turret.stop();
        vision.stop();
        odometry.stop(); //fix ts
    }

    /** Push all subsystem data to Driver Station in one call. */
    public void displayTelemetry(Telemetry t) {
        drivetrain.displayTelemetry(t);
        shooter.displayTelemetry(t);
        intake.displayTelemetry(t);
        turret.displayTelemetry(t);
        vision.displayTelemetry(t);
        odometry.displayTelemetry(t);
        t.update();
    }
}
