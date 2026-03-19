package org.firstinspires.ftc.teamcode.kishi;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

// import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;  // not used — webcam commented out
// import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;                  // not used — Pinpoint commented out

// central hardware map — every subsystem pulls from here
// names MUST match driver station config exactly
public class HardwareMapConfig {

    // public DcMotorEx turret_motor;  // not needed for drivetrain/shooter/intake

    public DcMotorEx intake_motor;         // intake roller motor

    public DcMotorEx shooter_motor_0, shooter_motor_1;  // flywheel pair

    public DcMotorEx wheel_0, wheel_1, wheel_2, wheel_3;  // mecanum drive wheels

    public Servo feeder_servo;             // pushes balls into flywheel
    // public Servo angle_servo;           // not needed — shooter angle not in use

    // public WebcamName webcam;           // not needed — vision not in use

    // Pinpoint + odometry — uncomment when you want position tracking
    // public GoBildaPinpointDriver pinpoint;

    public HardwareMapConfig(HardwareMap hw) {

        // turret_motor = hw.get(DcMotorEx.class, "turret_motor");  // commented out — not in use

        intake_motor = hw.get(DcMotorEx.class, "intake_motor");

        shooter_motor_0 = hw.get(DcMotorEx.class, "shooter_motor_0");
        shooter_motor_1 = hw.get(DcMotorEx.class, "shooter_motor_1");

        wheel_0 = hw.get(DcMotorEx.class, "wheel_0");
        wheel_1 = hw.get(DcMotorEx.class, "wheel_1");
        wheel_2 = hw.get(DcMotorEx.class, "wheel_2");
        wheel_3 = hw.get(DcMotorEx.class, "wheel_3");

        feeder_servo = hw.get(Servo.class, "feeder_servo");
        // angle_servo = hw.get(Servo.class, "angle_servo");  // commented out — not in use

        // webcam = hw.get(WebcamName.class, "webcam");  // commented out — vision not in use

        // --- Pinpoint odometry (uncomment to re-enable) ---
        // pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");
        // pinpoint.setOffsets(-84.0, -168.0);
        // pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        // pinpoint.setEncoderDirections(
        //     GoBildaPinpointDriver.EncoderDirection.FORWARD,
        //     GoBildaPinpointDriver.EncoderDirection.FORWARD
        // );
        // pinpoint.resetPosAndIMU();
    }
}
// only the control hub, expansion and driver hub need to be synced