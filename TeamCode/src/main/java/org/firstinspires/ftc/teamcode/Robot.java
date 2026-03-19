package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot.java — Central subsystem container
 *
 * Active subsystems: Drivetrain, Shooter, Intake
 * Commented out: Odometry (re-enable when position tracking is needed)
 *
 * OpModes use: robot.drivetrain, robot.shooter, robot.intake
 */
public class Robot {

    // ----------------------------------
    // SUBSYSTEMS
    // ----------------------------------

    public final Drivetrain drivetrain;  // Mecanum wheel movement
    public final Shooter    shooter;     // Flywheel shooter + feeder
    public final Intake     intake;      // Ball intake roller
    // public final Odometry odometry;  // Pinpoint localization — commented out, not needed right now

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * Main constructor — call this from your OpMode's runOpMode().
     *
     * Creates one HardwareMapConfig, then passes it to every subsystem.
     * Hardware is looked up exactly once; subsystems share references.
     *
     * @param hw             HardwareMap provided by the OpMode framework
     * @param enableLiveView Reserved for future camera live-view toggle
     */
    public Robot(HardwareMap hw, boolean enableLiveView) {
        HardwareMapConfig config = new HardwareMapConfig(hw);

        drivetrain = new Drivetrain(config);  // 4-wheel mecanum
        shooter    = new Shooter(config);     // Flywheel + feeder servo
        intake     = new Intake(config);      // Intake roller
        // odometry = new Odometry(config);  // uncomment to enable Pinpoint localization
    }

    /**
     * Default constructor
     * Defaults to teleop usage
     */
    public Robot(HardwareMap hw) {
        this(hw, true);
    }

    // ----------------------------------
    // UTILITY METHODS
    // ----------------------------------

    /**
     * stopAll() — cuts power to all active subsystems.
     * Call at end of OpMode or on emergency stop.
     */
    public void stopAll() {
        drivetrain.stop();  // cut wheel power
        shooter.stop();     // stop flywheels, retract feeder
        intake.stop();      // stop intake roller
    }

    /**
     * displayTelemetry() — shows drivetrain, shooter, and intake data on Driver Station.
     */
    public void displayTelemetry(org.firstinspires.ftc.robotcore.external.Telemetry telemetry) {
        // Drivetrain
        double[] powers = drivetrain.getWheelPowers();
        telemetry.addData("Wheels NE/SE/SW/NW", "%.2f %.2f %.2f %.2f",
                powers[0], powers[1], powers[2], powers[3]);
        telemetry.addData("Drive Mode", drivetrain.getSpeedMode().name());

        // Shooter
        shooter.displayTelemetry(telemetry);

        // Intake
        intake.displayTelemetry(telemetry);

        // Odometry (uncomment if re-enabled)
        // odometry.displayTelemetry(telemetry);

        telemetry.update();
    }
}