package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

// main container for the robot hardware
// dont make individual subsystems, just ask for them here
// like robot.turret.autoAim(...)
public class Robot {

    public final Drivetrain drivetrain;
    public final Intake     intake;
    public final Vision     vision;
    public final Turret     turret;
    public final Shooter    shooter;

    // livevew eats up the cpu so its false for auto
    public Robot(HardwareMap hw, boolean enableLiveView) {
        HardwareMapConfig config = new HardwareMapConfig(hw);

        drivetrain = new Drivetrain(config);
        intake     = new Intake(config);
        vision     = new Vision(config, enableLiveView);
        turret     = new Turret(config);
        shooter    = new Shooter(config);
    }

    // defalt to teleop mode with the camera preview on
    public Robot(HardwareMap hw) {
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
