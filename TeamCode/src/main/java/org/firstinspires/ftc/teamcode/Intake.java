package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Intake — single roller motor that pulls balls into the robot.
 *
 * intake() — spin inward  (pulls balls in)
 * eject()  — spin outward (clears jams or overflow)
 * stop()   — stop roller
 */
public class Intake {

    private static final double INTAKE_POWER = 1.00; // maxed — ski feedback: needs more pull
    private static final double EJECT_POWER  = 0.75; // bumped up to match intake force

    private final DcMotorEx motor;

    public Intake(HardwareMapConfig robot) {
        motor = robot.intake_motor;

        // FORWARD = pulling balls in. Flip to REVERSE if it ejects instead.
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // BRAKE so roller stops immediately and balls don't roll back out
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void intake() { motor.setPower(INTAKE_POWER);  }
    public void eject()  { motor.setPower(-EJECT_POWER);  }
    public void stop()   { motor.setPower(0); }

    public void displayTelemetry(Telemetry t) {
        t.addData("Intake | power", String.format("%.2f", motor.getPower()));
    }
}
