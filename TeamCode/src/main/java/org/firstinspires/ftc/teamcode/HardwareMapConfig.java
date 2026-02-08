package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class HardwareMapConfig {

    public DcMotorEx turret_motor;

    public DcMotorEx intake_motor;

    public DcMotorEx shooter_motor_0, shooter_motor_1;

    public DcMotorEx wheel_0, wheel_1, wheel_2, wheel_3;

    public Servo feeder_servo;
    public Servo angle_servo;

    public WebcamName webcam;

    public HardwareMapConfig(HardwareMap hw) {

        turret_motor = hw.get(DcMotorEx.class, "turret_motor");

        intake_motor = hw.get(DcMotorEx.class, "intake_motor");

        shooter_motor_0 = hw.get(DcMotorEx.class, "shooter_motor_0");
        shooter_motor_1 = hw.get(DcMotorEx.class, "shooter_motor_1");

        wheel_0 = hw.get(DcMotorEx.class, "wheel_0");
        wheel_1 = hw.get(DcMotorEx.class, "wheel_1");
        wheel_2 = hw.get(DcMotorEx.class, "wheel_2");
        wheel_3 = hw.get(DcMotorEx.class, "wheel_3");

        feeder_servo = hw.get(Servo.class, "feeder_servo");
        angle_servo = hw.get(Servo.class, "angle_servo");

        webcam = hw.get(WebcamName.class, "webcam");
    }
}
