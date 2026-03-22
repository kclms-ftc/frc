package org.firstinspires.ftc.teamcode.gamma;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Turret — subsystem to rotate the shooter turret.
 *
 * rotateLeft()   — rotates the turret left (counter-clockwise)
 * rotateRight()  — rotates the turret right (clockwise)
 * stop()         — stops rotation
 */
public class gammaTurret {

    private static final double ROTATE_POWER = 0.50;

    private final DcMotorEx motor;

    public gammaTurret(gammaHardwareMapConfig robot) {
        motor = robot.turret_motor;

        // Ensure that forward and reverse match your left/right expectations
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // BRAKE so the turret stops immediately and holds position
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void rotateLeft()  { motor.setPower(-ROTATE_POWER); }
    public void rotateRight() { motor.setPower(ROTATE_POWER);  }
    public void stop()        { motor.setPower(0); }

    public void displayTelemetry(Telemetry t) {
        t.addData("Turret | power", String.format("%.2f", motor.getPower()));
    }
}
