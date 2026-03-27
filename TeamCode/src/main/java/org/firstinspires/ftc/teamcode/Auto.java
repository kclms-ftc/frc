package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "new teleop")
public class Auto extends LinearOpMode {
    private HardwareMapConfig hw;
    private Drivetrain drivetrain;
    private Shooter shooter;

    // change these after testing
    private final double BACKWARD_DISTANCE = 500; // move backward 500mm
    private final double LEFT_DISTANCE = 300;     // move left 300mm

    @Override
    public void runOpMode() throws InterruptedException {

        // initialise hardware and subsystems
        hw = new HardwareMapConfig(hardwareMap);
        drivetrain = new Drivetrain(hw);
        shooter = new Shooter(hw);

        telemetry.addData("Status", "Init complete");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            // move backwards
            moveDistance(-BACKWARD_DISTANCE, 0); // negative Y = backward

            // shoot 3 preloaded balls
            shooter.startShootingSequence();
            while (shooter.shootingCurrently && opModeIsActive()) {
                shooter.loop(null); // we don't need gamepad input for auto
                idle();
            }

            // move left
            moveDistance(0, -LEFT_DISTANCE); // negative X = left
        }
    }

    // moves the robot using odometry
    private void moveDistance(double deltaY, double deltaX) {
        // current position
        double startX = hw.pinpoint.getPosX(DistanceUnit.MM);
        double startY = hw.pinpoint.getPosY(DistanceUnit.MM);

        // Target position
        double targetX = startX + deltaX;
        double targetY = startY + deltaY;
        double targetHeading = hw.pinpoint.getHeading(AngleUnit.RADIANS); // keep heading constant

        // Move until reached
        drivetrain.goToTargetWithOdometry(targetX, targetY, targetHeading);
        while (!drivetrain.targetReached() && opModeIsActive()) {
            drivetrain.goToTargetWithOdometry(targetX, targetY, targetHeading);
            idle();
        }

        // stop motors
        drivetrain.stop();
    }
}
