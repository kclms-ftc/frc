package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;

// intake subsystem — 3 states: off, forward, reverse
public class Intake {

    public enum IntakeState {
        OFF,
        INTAKE,
        REVERSE
    }

    private static final double INTAKE_POWER  =  1.0;
    private static final double REVERSE_POWER = -1.0;

    private final DcMotorEx motor;
    private IntakeState state;

    public Intake(HardwareMapConfig robot) {
        this.motor = robot.intake_motor;
        this.motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        this.state = IntakeState.OFF;
    }

    public void intakeIn() {
        state = IntakeState.INTAKE;
        motor.setPower(INTAKE_POWER);
    }

    public void intakeReverse() {
        state = IntakeState.REVERSE;
        motor.setPower(REVERSE_POWER);
    }

    public void intakeOff() {
        state = IntakeState.OFF;
        motor.setPower(0);
    }

    public void stop() {
        intakeOff();
    }

    public IntakeState getState() {
        return state;
    }

    public double getVelocity() {
        return motor.getVelocity();
    }
}
