package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
gamepad:
a - cycle intake mode: off -> slow (0.8) -> fast (1.0) -> outtake (0.75) -> off
defaults to off
 */

public class Intake {

    private DcMotorEx intakeMotor;

    // four modes in cycle order
    private enum IntakeMode {
        OFF, SLOW, FAST, OUTTAKE
    }

    // 0, +0.8, +1.0, -0.75
    private IntakeMode mode = IntakeMode.OFF;

    private boolean lastA = false;

    // main methods

    public Intake(HardwareMapConfig hw) {
        intakeMotor = hw.intake_motor;
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    // called 50 times per second
    public void loop(Gamepad gp) {
        // advance mode on each fresh press of a and use cases
        if (gp.a && !lastA) {
            switch (mode) {
                case OFF:
                    mode = IntakeMode.SLOW;
                    break;
                case SLOW:
                    mode = IntakeMode.FAST;
                    break;
                case FAST:
                    mode = IntakeMode.OUTTAKE;
                    break;
                case OUTTAKE:
                    mode = IntakeMode.OFF;
                    break;
            }
        }
        lastA = gp.a;

        // apply power for current mode and using cases
        switch (mode) {
            case OFF:
                intakeMotor.setPower(0);
                break;
            case SLOW:
                intakeMotor.setPower(0.2);
                break;
            case FAST:
                intakeMotor.setPower(1.0);
                break;
            case OUTTAKE:
                intakeMotor.setPower(-0.75);
                break;
        }
    }

    public void testLoop(Gamepad gp, Telemetry t) {
        double power = 0.0;

        if (gp.a) {
            power = 0.2;
        } else if (gp.b) {
            power = 0.5;
        } else if (gp.x) {
            power = -0.2;
        }

        intakeMotor.setPower(power);

        t.addData("Intake test power", power);
        t.addData("Velocity", intakeMotor.getVelocity());
        t.addData("Position", intakeMotor.getCurrentPosition());
    }

    public void updateTelemetry(Telemetry t) {
        t.addData("intake mode", mode); // might be helpful
        t.addData("motorspeed", intakeMotor.getVelocity());
    }

    public void stop() {
        mode = IntakeMode.OFF;
        intakeMotor.setPower(0);
    }

    // HELPER METHODS
}

    

    