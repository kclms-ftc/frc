package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
x button - triggers shooting sequence (shoots 3 balls)
*/

public class Shooter {

    private DcMotorEx shooterMotor0, shooterMotor1;
    private Servo feederServo, stopperServo;

    // state machine
    private enum ShootState {
        IDLE,
        SPINNING_UP,
        READY,
        FEEDING,
        DONE
    }

    private ShootState state = ShootState.IDLE;
    private long stateStartTime = 0;
    private boolean lastX = false;
    public boolean shootingCurrently = false;
    private int shotsRemaining = 0;

    // MAIN METHODS

    // constructor method
    public Shooter(HardwareMapConfig hw) {
        shooterMotor0 = hw.shooter_motor_0;
        shooterMotor1 = hw.shooter_motor_1;
        feederServo = hw.feeder_servo;
        stopperServo = hw.stopper_servo;
    }

    // Called by Teleop — named loop2 to match Teleop.java's shooter.loop2() call
    public void loop2(Gamepad gp) {
        // detect fresh X press
        if (gp.x && !lastX) {
            if (state == ShootState.IDLE) {
                startShootingSequence();
            }
        }
        lastX = gp.x;

        // state machine
        switch (state) {

            // not shooting — motors off, servos closed
            case IDLE:
                shooterMotor0.setPower(0);
                shooterMotor1.setPower(0);
                stopperServo.setPosition(0);
                feederServo.setPosition(0);
                break;

            // spin flywheels up to speed (1.5 s)
            case SPINNING_UP:
                shooterMotor0.setPower(-1.0);
                shooterMotor1.setPower(-1.0);
                if (timeElapsed(1500)) {
                    state = ShootState.READY;
                    stateStartTime = System.currentTimeMillis();
                }
                break;

            // open stopper servo, wait 0.3 s for full travel
            case READY:
                stopperServo.setPosition(1);
                if (timeElapsed(300)) {
                    state = ShootState.FEEDING;
                    stateStartTime = System.currentTimeMillis();
                }
                break;

            // push one ball with feeder servo, then decide whether to shoot again
            case FEEDING:
                feederServo.setPosition(1);
                if (timeElapsed(300)) {
                    feederServo.setPosition(0);   // retract feeder
                    shotsRemaining -= 1;
                    if (shotsRemaining > 0) {
                        // more balls to shoot — go back to READY
                        state = ShootState.READY;
                        stateStartTime = System.currentTimeMillis();
                    } else {
                        // all balls fired — wrap up
                        state = ShootState.DONE;
                        stateStartTime = System.currentTimeMillis();
                    }
                }
                break;

            // close stopper, stop motors, return to IDLE
            case DONE:
                stopperServo.setPosition(0);
                if (timeElapsed(300)) {
                    shooterMotor0.setPower(0);
                    shooterMotor1.setPower(0);
                    shootingCurrently = false;
                    state = ShootState.IDLE;
                }
                break;
        }
    }

    // Legacy stub — kept so nothing else breaks if loop() is ever called
    public void loop(Gamepad gp) {
        loop2(gp);
    }

    public void updateTelemetry(Telemetry t) {
        t.addData("Shooter state", state);
        t.addData("Shots remaining", shotsRemaining);
        t.addData("Shooting", shootingCurrently);
        t.addData("Feeder position", feederServo.getPosition());
        t.addData("Stopper position", stopperServo.getPosition());
    }

    public void stop() {
        shooterMotor0.setPower(0);
        shooterMotor1.setPower(0);
        feederServo.setPosition(0);   // safe / retracted position
        stopperServo.setPosition(0);  // safe / closed position
        state = ShootState.IDLE;
        shootingCurrently = false;
    }

    // HELPER METHODS

    // kick off a 3-ball shooting sequence
    public void startShootingSequence() {
        shotsRemaining = 3;
        state = ShootState.SPINNING_UP;
        stateStartTime = System.currentTimeMillis();
        shootingCurrently = true;
    }

    // returns true once the current state has been active for at least ms milliseconds
    public boolean timeElapsed(long ms) {
        return System.currentTimeMillis() - stateStartTime >= ms;
    }
}
