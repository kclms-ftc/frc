package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareMapConfig;

@TeleOp
public class Shooter {

    HardwareMapConfig config;
    private final DcMotorEx shooter_0;
    private final DcMotorEx shooter_1;
    private final Servo feeder;

    //flywheelMotor_0 = HardwareMapConfig.shooter_motor_0;
    double targetVelocity = 1500;
    double F = 0;
    double P = 0;
    double[] stepSizes = {10.0,1.0,0.1,0.01,0.001}; // allows us to increase/decrease PF controller by different levels of sensitivity
    int stepIndex = 1; // starts changing at values of 1 at a time (2nd value in stepSizes)
    double REST_POS = 0.0;
    double PUSH_POS = 0.5; // Adjust until the arm hits the ball correctly

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
        // finish this

    }

    public void setRPM(Gamepad gamepad) {
        double triggerPressure = gamepad.left_trigger;

        if (triggerPressure > 0.1){
            shooter_0.setVelocity(targetVelocity);
            shooter_1.setVelocity(targetVelocity);
        }else {
            shooter_0.setVelocity(0);
            shooter_1.setVelocity(0);
        }

        double actualVelocity0 = shooter_0.getVelocity();
        double actualVelocity1 = shooter_1.getVelocity();
        double velocityError0 = targetVelocity - actualVelocity0;
        double velocityError1 = targetVelocity - actualVelocity1;
    }

    public void stopFlyWheel() {
        shooter_0.setVelocity(0);
        shooter_1.setVelocity(0);
    }

    public void atSpeed() {}

    public void fireOnce() {}

    public void fireMultiple() {}

    public void spinUpAndShoot () {}

    public void setfeederArm(Gamepad gamepad) {
        if (gamepad.x){
            feeder.setPosition(PUSH_POS);
        } else {
            feeder.setPosition(REST_POS);
        }

    }


    public void addTelemetry(Telemetry telemetry) {
        telemetry.addLine("--- FLYWHEEL DIAGNOSTICS ---");
        telemetry.addData("Target Flywheel RPM: ",  targetVelocity);
        telemetry.addData("Motor State", triggerPressure > 0.1 ? "SPINNING" : "STOPPED");
        telemetry.addData("Actual Velocity Shooter_0", "%.2f ticks/s", actualVelocity);
        telemetry.addData("Actual Velocity Shooter_1", );

        telemetry.addLine("--- SERVO ARM STATUS ---");
        telemetry.addData("Servo Pos: ", feeder.getPosition());

        // Error shows how much the motor is lagging behind the target
        telemetry.addData("Velocity Error", "%.2f ticks/s", velocityError);

        // Power shows how hard the Hub is working (0.0 to 1.0) to hit that speed
        telemetry.addData("Motor Power Usage", "%.2f%%", flywheelMotor.getPower() * 100);

        // Update the screen at the end of every loop
        telemetry.update();
    }

}

/* PF controller
Two different target velocities to toggle between - 900rpm, 1500rpm

 */