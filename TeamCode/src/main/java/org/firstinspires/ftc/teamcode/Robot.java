package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot.java — Central subsystem container
 *
 * Robot is the single object that OpModes interact with.
 * It owns every subsystem (Drivetrain, Shooter, Odometry) and
 * wires them all to the same HardwareMapConfig so hardware
 * lookup only ever happens in one place.
 *
 * OpModes never touch HardwareMapConfig directly —
 * they just call robot.drivetrain, robot.shooter, robot.odometry.
 */
public class Robot {

    // ----------------------------------
    // SUBSYSTEMS
    // ----------------------------------

    public final Drivetrain drivetrain;  // Mecanum wheel movement
    public final Shooter    shooter;     // Flywheel shooter + feeder
    public final Odometry   odometry;    // Pinpoint position + heading tracking

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
        odometry   = new Odometry(config);    // Pinpoint dead-wheel localization
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
     * stopAll()
     * Stops all active subsystems immediately.
     * Note: Odometry has no stop — it's always passive (read-only hardware).
     */
    public void stopAll() {
        drivetrain.stop();  // cut wheel power
        shooter.stop();     // stop flywheels, retract feeder
        // odometry continues tracking even after stop — this is intentional
    }

    /**
     * displayTelemetry()
     * Dumps all subsystem data to the Driver Station in one call.
     *
     * IMPORTANT: call odometry.update() BEFORE this each loop,
     * otherwise position data shown here will be one loop stale.
     *
     * @param telemetry Telemetry object from the OpMode
     */
    public void displayTelemetry(org.firstinspires.ftc.robotcore.external.Telemetry telemetry) {
        // Drivetrain
        double[] powers = drivetrain.getWheelPowers();
        telemetry.addData("Wheels NE/SE/SW/NW", "%.2f %.2f %.2f %.2f",
                powers[0], powers[1], powers[2], powers[3]);
        telemetry.addData("Drive Mode", drivetrain.getSpeedMode().name());

        // Shooter
        shooter.displayTelemetry(telemetry);

        // Odometry
        odometry.displayTelemetry(telemetry);

        telemetry.update();
    }
}