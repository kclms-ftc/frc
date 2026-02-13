package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareMapConfig;

@TeleOp
public class Shooter {

    HardwareMapConfig config;
    private final DcMotorEx shooter_0;
    private final DcMotorEx shooter_1;
    private final Servo feeder;

    //flywheelMotor_0 = HardwareMapConfig.shooter_motor_0;
    double F = 0;
    double P = 0;
    double[] stepSizes = {10.0,1.0,0.1,0.01,0.001}; // allows us to increase/decrease PF controller by different levels of sensitivity
    int stepIndex = 1; // starts changing at values of 1 at a time (2nd value in stepSizes)
    double REST_POS = 0.0;
    double PUSH_POS = 0.5; // Adjust until the arm hits the ball correctly
    double targetRPM = 1500;
    double lowTargetRPM = 900;
    double highTargetRPM = 1500;
    double curTargetRPM = 0;

    // Telemetry storage variables
    double actualVelocity0 = 0;
    double actualVelocity1 = 0;
    boolean isSpinning = false;

    public Shooter(HardwareMap hw){
        this.config = new HardwareMapConfig(hw);
        this.shooter_0 = config.shooter_motor_0;
        this.shooter_1 = config.shooter_motor_1;
        this.feeder = config.feeder_servo;

        shooter_0.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter_1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter_0.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        shooter_1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        shooter_0.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        shooter_1.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }

    public void setRPM(Gamepad gamepad, double target) {
        this.targetRPM = target;
        double triggerPressure = gamepad.left_trigger;
//
//      if (triggerPressure > 0.1) {
//            shooter_0.setVelocity(targetRPM);
//            shooter_1.setVelocity(targetRPM);
//        } else {
//            shooter_0.setVelocity(0);
//            shooter_1.setVelocity(0);
//        }

        actualVelocity0 = shooter_0.getVelocity();
        actualVelocity1 = shooter_1.getVelocity();
        double velocityError0 = targetRPM - actualVelocity0;
        double velocityError1 = targetRPM - actualVelocity1;

        // Brogan code
        if (gamepad.yWasPressed()) { // toggles target speed
            if (targetRPM == highTargetRPM) {
                targetRPM = lowTargetRPM;
            } else {
                targetRPM = highTargetRPM;
            }

            if (gamepad.bWasPressed()) {
                stepIndex = (stepIndex + 1) % stepSizes.length; // cycles through stepSizes list
            }
        }
    }

    public void changeRPM(Gamepad gamepad) {
        if (gamepad.dpadLeftWasPressed()){
            F += stepSizes[stepIndex];
        }
        if (gamepad.dpadRightWasPressed()){
            F -= stepSizes[stepIndex];
        }
        if (gamepad.dpadUpWasPressed()){
            P += stepSizes[stepIndex];
        }
        if (gamepad.dpadDownWasPressed()){
            P -= stepSizes[stepIndex];
        }

        // set new pidf coefficients

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        shooter_0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        shooter_1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        double curVelocity0 = shooter_0.getVelocity();
        double curVelocity1 = shooter_1.getVelocity();


    }

    public void stopFlyWheel() {
        shooter_0.setVelocity(0);
        shooter_1.setVelocity(0);
    }

    public void atSpeed() {}

    public void fireOnce(Gamepad gamepad) { // sets feeder arm
        ElapsedTime servoTimer = new ElapsedTime();
        if (gamepad.x) {
            servoTimer.reset();
        }

        if (servoTimer.milliseconds() > 500){
            feeder.setPosition(PUSH_POS);
            feeder.setPosition(REST_POS);
        } else {
            feeder.setPosition(REST_POS);
        }

    }

    // AUTONOMOUS
    public void spinUpAndShoot () {}


    public void addShooterTelemetry(Telemetry telemetry) {
        telemetry.addLine("--- FLYWHEEL DIAGNOSTICS ---");
        telemetry.addData("Target Flywheel RPM: ", targetRPM);
        telemetry.addData("Motor State: ", isSpinning ? "SPINNING" : "STOPPED");
        telemetry.addData("Actual Velocity 0: ", "%.2f ticks/s", actualVelocity0);
        telemetry.addData("Actual Velocity 1: ", "%.2f ticks/s", actualVelocity1);
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad U/D",P);
        telemetry.addData("Tuning F", "%.4f (D-Pad L/R",  F);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
        telemetry.addLine("--- SERVO ARM STATUS ---");
        telemetry.addData("Servo Pos: ", feeder.getPosition());

        // Error shows how much the motor are lagging behind the target
        //telemetry.addData("Velocity Error Shooter_0", velocityError);

        // telemetry.update() not required!
    }

}

/* PF controller (Brogan video) - finish PIDF
Two different target velocities to toggle between - 900rpm, 1500rpm

- check how to access actualvelocity variables after updated in setRPM#
- atSpeed() func

 */