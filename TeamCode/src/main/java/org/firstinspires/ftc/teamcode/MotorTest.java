package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class MotorTest {
    private DcMotor motor;
    private double ticksPerRev; // revolution

    public void init(HardwareMap hwMap, String motorName) {
        //DC motor
        motor = hwMap.get(DcMotor.class, motorName);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
//        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setMotorSpeed(double speed) {
        // accepts values from -1.0 to 1.0
        motor.setPower(speed);
    }

    public double getMotorRevs() {
        return motor.getCurrentPosition() / ticksPerRev; // normalising ticks to revolutions
    }

    public void setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior zeroBehaviour) {
        motor.setZeroPowerBehavior(zeroBehaviour);
    }
}

