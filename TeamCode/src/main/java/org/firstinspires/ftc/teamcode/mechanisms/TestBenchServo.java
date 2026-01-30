package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TestBenchServo {
    private Servo servoPos; // variable for positional servo
    private CRServo servoRot; // variable for cont. rotational servo

    public void init(HardwareMap hwMap){ //lets us initialise our mechanism individually
        servoPos = hwMap.get(Servo.class, "servo_pos");
        servoRot = hwMap.get(CRServo.class, "servo_rot");
        servoPos.scaleRange(0.5,1.0); // set range from midpoint to 180*
        servoPos.setDirection(Servo.Direction.REVERSE); // reverses movement of servos - helpful if you have 2 servos side by side so instead of having to flip positions you can do it in code
        servoRot.setDirection(CRServo.Direction.REVERSE);
    }

    public void setServoPos(double angle){
        servoPos.setPosition(angle);
    }

    public void setServoRot(double power){
        servoRot.setPower(power);
    }
}
