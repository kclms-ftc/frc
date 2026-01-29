package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestBenchMotors {
    private DigitalChannel touchSensor;
    private DcMotor motor;
    private double ticksPerRev; // revolutions

    public void init(HardwareMap hwMap){
        //touch sensor code
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor"); // assigns our variable touchSensor with our DigitalChannel class with the name of touch_sensor
        touchSensor.setMode(DigitalChannel.Mode.INPUT);
        // DC motor
        motor = hwMap.get(DcMotor.class, "motor"); // must provide the exact name that is given for the motor in the configuration file
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev(); // reads ticks of encoder
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // ----------- Touch sensor code ------------
    public boolean isTouchSensorPressed() {
        return !touchSensor.getState(); // will be the opposite of what we expect it to be (see video for better explanation)
    }
    public boolean isTouchSensorReleased() {
        return touchSensor.getState();
    }

    // ----------- DC Motor code -------------
    public void setMotorSpeed(double speed){
        // accepts values from -1.0 to 1.0
        motor.setPower(speed);
    }

    public double getMotorRevs(){
        return motor.getCurrentPosition() / ticksPerRev; // Normalising ticks:revolutions - current number of ticks/num of ticks in a revolutio
    }

    public void setMotorZeroBehaviour(DcMotor.ZeroPowerBehavior zeroBehaviour){
        motor.setZeroPowerBehavior();
    }
}
