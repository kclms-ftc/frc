package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

// main container for the robot hardware
// dont make individual subsystems, just ask for them here
// like robot.turret.autoAim(...)
public class Robot1 {

    public final Drivetrain1 drivetrain;
    public final Intake     intake;
    public final Vision     vision;
    public final Turret     turret;
    public final Shooter1 shooter;

    // livevew eats up the cpu so its false for auto
    public Robot1(HardwareMap hw, boolean enableLiveView) {
        HardwareMapConfig config = new HardwareMapConfig(hw);

        drivetrain = new Drivetrain1(config);
        intake     = new Intake(config);
        vision     = new Vision(config, enableLiveView);
        turret     = new Turret(config);
        shooter    = new Shooter1(config);
    }

    // default to teleop mode with the camera preview on
    public Robot1(HardwareMap hw) {
        this(hw, true);
    }

    // stops everything
    // good for emergency aborted autos
    public void stopAll() {
        drivetrain.stop();
        intake.stop();
        turret.stop();
        shooter.stop();
        vision.stop();
    }
}
