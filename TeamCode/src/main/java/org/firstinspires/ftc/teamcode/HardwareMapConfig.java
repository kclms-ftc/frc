package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * HardwareMapConfig — the only place hw.get() is ever called.
 *
 * DS config names must match these strings exactly.
 * All subsystems receive this object in their constructor
 * and pull references from it — they never call hw.get() themselves.
 */
public class HardwareMapConfig {

    // drivetrain (Mecanum, 4 motors)
    public DcMotorEx wheel_0;  // front-right (NE)
    public DcMotorEx wheel_1;  // back-right  (SE)
    public DcMotorEx wheel_2;  // back-left   (SW)
    public DcMotorEx wheel_3;  // front-left  (NW)

    // shooter (dual flywheel + feeder servo + stopper servo)
    public DcMotorEx shooter_motor_0;
    public DcMotorEx shooter_motor_1;
    public Servo feeder_servo;
    public Servo stopper_servo;

    // intake (single roller motor)
    public DcMotorEx intake_motor;

    // getting rid of turret...2

    // webcam
    public WebcamName webcam;

    // odometry pods management
    public GoBildaPinpointDriver pinpoint;


    public HardwareMapConfig(HardwareMap hw) {

        // Drivetrain
        wheel_0 = hw.get(DcMotorEx.class, "wheel_0");
        wheel_1 = hw.get(DcMotorEx.class, "wheel_1");
        wheel_2 = hw.get(DcMotorEx.class, "wheel_2");
        wheel_3 = hw.get(DcMotorEx.class, "wheel_3");

        // Shooter
        shooter_motor_0 = hw.get(DcMotorEx.class, "shooter_motor_0");
        shooter_motor_1 = hw.get(DcMotorEx.class, "shooter_motor_1");
        feeder_servo = hw.get(Servo.class, "feeder_servo");
        stopper_servo = hw.get(Servo.class, "stopper_servo");

        // Intake
        intake_motor = hw.get(DcMotorEx.class, "intake_motor");

        // getting rid of turret...2

        // Webcam
        webcam = hw.get(WebcamName.class, "webcam");

        // Pinpoint odometry
        pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");

        // Pinpoint calibration
        pinpoint.setOffsets(-45, -126, DistanceUnit.MM); // pod mounting positions
        //pinpoint.setTrack(90); // distance between the dead wheels (to calibrate rotation)
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);  // 2000 CPR, 32mm wheel
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU(); // resets to (0,0,0)
    }
}
