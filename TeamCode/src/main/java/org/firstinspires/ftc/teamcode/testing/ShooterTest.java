package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareMapConfig;

@TeleOp
public class ShooterTest extends OpMode {

    HardwareMapConfig config; 
    private DcMotor shooter_motor_0;
    private DcMotor shooter_motor_1;

    //flywheelMotor_0 = HardwareMapConfig.shooter_motor_0;
    double targetVelocity = 1000;
    double F = 0;
    double P = 0;
    double[] stepSizes = {10.0,1.0,0.1,0.01,0.001}; // allows us to increase/decrease PF controller by different levels of sensitivity
    int stepIndex = 1; // starts changing at values of 1 at a time (2nd value in stepSizes)

    @Override
    public void init() {
        HardwareMapConfig config = new HardwareMapConfig(hardwareMap); // constructor - reference to HardwareMapConfig.java class
        shooter_motor_0 = config.shooter_motor_0;
        shooter_motor_1 = config.shooter_motor_1;

        shooter_motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter_motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooter_motor_0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter_motor_1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {

    }

    public void setRPM() {

    }

    public void stopFlyWheel() {}

    public void atSpeed() {}

    public void fireOnce() {}

    public void fireMultiple() {}

    public void spinUpAndShoot () {}
}

/* PF controller
Two different target velocities to toggle between - 900rpm, 1500rpm

 */