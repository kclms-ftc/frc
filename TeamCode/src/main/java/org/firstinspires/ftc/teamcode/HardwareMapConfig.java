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

    // --- Drivetrain --- (mecanum, 4 motors)
    public DcMotorEx wheel_0;  // front-right (NE)
    public DcMotorEx wheel_1;  // back-right  (SE)
    public DcMotorEx wheel_2;  // back-left   (SW)
    public DcMotorEx wheel_3;  // front-left  (NW)

    // --- Shooter --- (dual flywheel + feeder servo)
    public DcMotorEx shooter_motor_0;
    public DcMotorEx shooter_motor_1;
    public Servo     feeder_servo;

    // --- Intake --- (single roller motor)
    public DcMotorEx intake_motor;

    // --- Turret --- (rotation motor)
    public DcMotorEx turret_motor;

    // --- Webcam ---
    public WebcamName webcam;

    // --- Odometry ---
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
        feeder_servo    = hw.get(Servo.class, "feeder_servo");

        // Intake
        intake_motor = hw.get(DcMotorEx.class, "intake_motor");

        // Turret
        turret_motor = hw.get(DcMotorEx.class, "turret_motor");

        //Webcam
        webcam = hw.get(WebcamName.class, "webcam");

        //Pinpoint
        pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");

        /* PINPOINT CONFIGURATION
        These must be set here so the odometry subsystem file starts with a calibrated unit.
         */
        pinpoint.setOffsets(-45, -126, DistanceUnit.MM);   // tune these to your actual pod mounting positions
        //pinpoint.setTrack(90); // distance between the dead wheels (to calibrate rotation
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);  // 2000 CPR, 32mm wheel
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();
    }
}
