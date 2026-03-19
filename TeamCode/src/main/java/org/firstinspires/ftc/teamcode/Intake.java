package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Intake.java — Ball Intake Subsystem
 *
 * HOW IT WORKS:
 * -------------
 * One motor spins a roller or set of wheels that pulls balls
 * into the robot from the field. Running it in reverse ejects balls.
 *
 * HARDWARE INDEPENDENCE:
 * ----------------------
 * Motor reference comes in via HardwareMapConfig — this class
 * never calls HardwareMap.get() directly.
 */
public class Intake {

    // Motor power for intake and eject
    private static final double INTAKE_POWER = 0.85;
    private static final double EJECT_POWER  = -0.6;

    private final DcMotorEx motor;

    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------

    /**
     * @param robot Shared HardwareMapConfig with intake_motor wired up
     */
    public Intake(HardwareMapConfig robot) {
        this.motor = robot.intake_motor;

        // Direction: FORWARD = pull balls in. Flip to REVERSE if intake runs backwards.
        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Raw power control — no encoder needed for intake
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // BRAKE so the roller stops instantly when power is cut,
        // preventing balls from rolling back out
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // ----------------------------------
    // CONTROL METHODS
    // ----------------------------------

    /** Pull balls into the robot */
    public void intake() {
        motor.setPower(INTAKE_POWER);
    }

    /** Push balls back out (jam clearing or overflow) */
    public void eject() {
        motor.setPower(EJECT_POWER);
    }

    /** Stop the intake roller */
    public void stop() {
        motor.setPower(0);
    }

    /** Returns true if the intake is currently running */
    public boolean isRunning() {
        return Math.abs(motor.getPower()) > 0.01;
    }

    /** Displays current intake power to Driver Station */
    public void displayTelemetry(org.firstinspires.ftc.robotcore.external.Telemetry telemetry) {
        telemetry.addData("Intake | power", String.format("%.2f", motor.getPower()));
    }
}
