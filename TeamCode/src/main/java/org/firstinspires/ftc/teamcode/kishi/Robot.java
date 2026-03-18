package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot.java — simplified version
 *
 * Central container for only the Drivetrain and Shooter subsystems.
 *
 * Purpose:
 * - Keeps code clean by having one object that contains all hardware functionality
 * - OpModes interact ONLY with Robot, never directly with motors
 * - Provides stopAll() for emergency stop or end-of-OpMode
 */
public class Robot {

    // ----------------------------------
    // SUBSYSTEMS
    // ----------------------------------

    public final Drivetrain drivetrain;  // Handles mecanum wheel movement
    public final Shooter shooter;         // Handles flywheel shooter + feeder

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * Main constructor
     * @param hw HardwareMap from OpMode
     * @param enableLiveView Optional for future, not used here
     */
    public Robot(HardwareMap hw, boolean enableLiveView) {
        // Create a hardware map object with all robot motors/servos
        HardwareMapConfig config = new HardwareMapConfig(hw);

        // Instantiate only the subsystems we need
        drivetrain = new Drivetrain(config); // 4 wheels mecanum drivetrain
        shooter    = new Shooter(config);    // Flywheel + feeder
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
     * Useful for:
     * - End of teleop
     * - Emergency stop
     */
    public void stopAll() {
        drivetrain.stop();  // Stop all 4 wheels
        shooter.stop();     // Stop flywheels and reset feeder
    }

    /**
     * displayTelemetry()
     * Optional helper to display drivetrain + shooter info in one place.
     * @param telemetry Telemetry object from OpMode
     */
    public void displayTelemetry(org.firstinspires.ftc.robotcore.external.Telemetry telemetry) {
        // Drivetrain telemetry
        double[] powers = drivetrain.getWheelPowers();
        telemetry.addData("Wheel Powers NE/SE/SW/NW", "%.2f %.2f %.2f %.2f",
                powers[0], powers[1], powers[2], powers[3]);
        telemetry.addData("Drive Mode", drivetrain.getSpeedMode().name());

        // Shooter telemetry
        shooter.displayTelemetry(telemetry);

        telemetry.update();
    }
}