package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;

// central hardware map — every subsystem pulls from here
// names MUST match driver station config exactly
public class HardwareMapConfig {

    public DcMotorEx turret_motor;

    public DcMotorEx intake_motor;

    public DcMotorEx shooter_motor_0, shooter_motor_1;

    public DcMotorEx wheel_0, wheel_1, wheel_2, wheel_3;

    public Servo feeder_servo;
    public Servo angle_servo;

    public WebcamName webcam;

    // Pinpoint odometry computer — fuses pod_x + pod_y encoders with internal IMU
    // DS config name must be exactly "pinpoint" (I2C device, any port except port 0)
    public GoBildaPinpointDriver pinpoint;

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
        angle_servo  = hw.get(Servo.class, "angle_servo");

        webcam = hw.get(WebcamName.class, "webcam");

        // Initialize Pinpoint co-processor
        // pod_x = forward/back dead wheel (parallel to robot's Y axis)
        // pod_y = left/right dead wheel   (parallel to robot's X axis)
        // Offsets are mm from the robot's tracking center (usually robot center)
        // Positive X = right of center, Positive Y = forward of center
        pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(-45, -126);   // tune these to your actual pod mounting positions
        pinpoint.setTrackWidth(90); // distance between the dead wheels (to calibrate rotation
        pinpoint.setEncoderResolution(
            GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD  // 2000 CPR, 32mm wheel
        );
        pinpoint.setEncoderDirections(
            GoBildaPinpointDriver.EncoderDirection.FORWARD,  // pod_x direction
            GoBildaPinpointDriver.EncoderDirection.FORWARD   // pod_y direction — flip to REVERSED if Y reads backwards
        );
        pinpoint.resetPosAndIMU();  // zero field position and calibrate IMU on startup
    }
}
// only the control hub, expansion and driver hub need to be synced