package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
GAMEPAD:
x button - triggers shooting sequence
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

    // MAIN METHODS

    // constructor method
    public Shooter(HardwareMapConfig hw) {
        shooterMotor0 = hw.shooter_motor_0;
        shooterMotor1 = hw.shooter_motor_1;
        feederServo = hw.feeder_servo;
        stopperServo = hw.stopper_servo;
    }

    // main loop called 50 times per second
    public void loop(Gamepad gp) {
        // check if x just pressed
        if(gp.x && !lastX) {
            if (state == ShootState.IDLE) {
                startShootingSequence();
            }
        }
        lastX = gp.x;

        // state machine
        switch (state) {
            // not shooting
            case IDLE:
                // change power here if motors running at low speed when idle
                shooterMotor0.setPower(0);
                shooterMotor1.setPower(0);
                stopperServo.setPosition(0);
                feederServo.setPosition(0);
                break;

            // get shooter motors up to speed
            case SPINNING_UP:
                shooterMotor0.setPower(1.0);
                shooterMotor1.setPower(1.0);

                // assumes spin up takes 1.5s
                if (timeElapsed(1500)) {
                    state = ShootState.READY;
                    stateStartTime = System.currentTimeMillis();
                }
                break;

            // open stopper, ready to shoot
            case READY:
                stopperServo.setPosition(1);
                // assumes 0.3 second servo movement
                if (timeElapsed(300)) {
                    state = ShootState.FEEDING;
                    stateStartTime = System.currentTimeMillis();
                }
                break;

            // servo arm pushes ball
            case FEEDING:
                feederServo.setPosition(1);
                // assumes 0.3 second servo movement
                if (timeElapsed(300)) {
                    feederServo.setPosition(0);
                    state = ShootState.DONE;
                    stateStartTime = System.currentTimeMillis();
                }
                break;

            // shooting sequence completed
            case DONE:
                // close stopper
                stopperServo.setPosition(0);
                if (timeElapsed(300)) {
                    shootingCurrently = false;
                    state = ShootState.IDLE;
                }
                break;
        }
    }

    public void updateTelemetry(Telemetry t) {

    }

    public void stop() {
        shooterMotor0.setPower(0);
        shooterMotor1.setPower(0);
        feederServo.setPosition(0);  // safe position
        stopperServo.setPosition(0); // safe position
    }

    // HELPER METHODS

    // start shooting
    public void startShootingSequence() {
        state = ShootState.SPINNING_UP;
        stateStartTime = System.currentTimeMillis();
        shootingCurrently = true;
    }

    // check how long current state has been active
    public boolean timeElapsed(long ms) {
        return System.currentTimeMillis() - stateStartTime >= ms;
    }
}
