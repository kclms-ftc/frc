package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

// main auto coordinator that shoots balls in 6 phases
// NO gamepads here, pure logic
@Autonomous(name = "Auto Shooter", group = "Competition")
public class AutoShooter extends LinearOpMode {

    // tag to track. might change next season
    private static final int TARGET_TAG_ID = 20;

    // shoot this many ballsFired
    private static final int BALLS_TO_SHOOT = 3;

    private static final double SHOOT_VELOCITY = Shooter1.VELOCITY_HIGH;

    // do we need to check the aim and rpm again after the first shot?
    private static final boolean CHECK_BETWEEN_SHOTS = true;

    // timeouts so we dont hang forever
    private static final long ACQUIRE_TIMEOUT_MS = 4000;
    private static final long AIM_TIMEOUT_MS = 5000;
    private static final long AIM_STABLE_DURATION_MS = 300;
    private static final long SPINUP_TIMEOUT_MS = 3000;

    private enum Phase {
        SPIN_UP,
        ACQUIRE,
        AIM,
        CONFIRM_RPM,
        FIRE,
        DONE,
        ABORTED
    }

    @Override
    public void runOpMode() {

        HardwareMapConfig config = new HardwareMapConfig(hardwareMap);
        Vision  vision  = new Vision(config, false);   // false means NO livedview so we save processing time
        Turret  turret  = new Turret(config);
        Shooter1 shooter = new Shooter1(config);

        Phase currentPhase = Phase.SPIN_UP;
        int   ballsFired   = 0;
        String abortReason = "";

        ElapsedTime phaseTimer   = new ElapsedTime();
        ElapsedTime stableTimer  = new ElapsedTime();
        ElapsedTime runtime      = new ElapsedTime();

        telemetry.addData("init", "waiting on cam");
        telemetry.update();

        ElapsedTime initTimer = new ElapsedTime();
        while (!vision.isStreaming() && initTimer.seconds() < 5 && !isStopRequested()) {
            idle();
        }

        if (vision.isStreaming()) {
            vision.configureCameraControls();
            telemetry.addData("cam", "all set");
        } else {
            telemetry.addData("cam", "died but we'll try later");
        }

        telemetry.addData("tag", TARGET_TAG_ID);
        telemetry.addData("balls", BALLS_TO_SHOOT);
        telemetry.update();

        waitForStart();
        runtime.reset();
        phaseTimer.reset();

        while (opModeIsActive() && currentPhase != Phase.DONE && currentPhase != Phase.ABORTED) {

            // get the latest frames
            vision.update();
            AprilTagDetection target = vision.getTagById(TARGET_TAG_ID);

            switch (currentPhase) {

                case SPIN_UP:
                    shooter.spinUp(SHOOT_VELOCITY);
                    currentPhase = Phase.ACQUIRE;
                    phaseTimer.reset();
                    telemetry.addData("state", "spinning");
                    break;

                case ACQUIRE:
                    telemetry.addData("state", "lookoing for tag");

                    if (target != null) {
                        currentPhase = Phase.AIM;
                        phaseTimer.reset();
                        stableTimer.reset();
                        telemetry.addData("got it", "");
                    } else if (phaseTimer.milliseconds() > ACQUIRE_TIMEOUT_MS) {
                        abortReason = "couldnt find the tag fast enough";
                        currentPhase = Phase.ABORTED;
                    }
                    break;

                case AIM:
                    telemetry.addData("state", "turing to the tag");

                    boolean locked = turret.autoAim(target);

                    // reset if we bounce out of tolreance
                    if (!locked) {
                        stableTimer.reset();           
                    }

                    // need to stay locked for a bit
                    if (locked && stableTimer.milliseconds() >= AIM_STABLE_DURATION_MS) {
                        currentPhase = Phase.CONFIRM_RPM;
                        phaseTimer.reset();
                    } else if (phaseTimer.milliseconds() > AIM_TIMEOUT_MS) {
                        abortReason = "aim took too long";
                        currentPhase = Phase.ABORTED;
                    }
                    break;

                case CONFIRM_RPM:
                    telemetry.addData("state", "waiting for the flyhweel to speed up");

                    turret.autoAim(target);

                    if (shooter.isReadyToFire()) {
                        currentPhase = Phase.FIRE;
                        phaseTimer.reset();
                    } else if (phaseTimer.milliseconds() > SPINUP_TIMEOUT_MS) {
                        abortReason = "flywheels stuck";
                        currentPhase = Phase.ABORTED;
                    }
                    break;

                case FIRE:
                    telemetry.addData("state", "shooting ball " + (ballsFired + 1));

                    if (CHECK_BETWEEN_SHOTS && ballsFired > 0) {
                        turret.autoAim(target);
                        if (!turret.isLocked() || !shooter.isReadyToFire()) {
                            telemetry.addData("holding", "waitning on aim or rpm");
                            break;    
                        }
                    }

                    // blocking fire since we're in auto
                    shooter.fireBallBlocking();
                    ballsFired++;

                    if (ballsFired >= BALLS_TO_SHOOT) {
                        currentPhase = Phase.DONE;
                    }
                    break;

                default:
                    break;
            }

            telemetry.addLine();
            vision.displayTelemetry(target, telemetry);
            turret.displayTelemetry(telemetry);
            shooter.displayTelemetry(telemetry);
            telemetry.addData("shot", ballsFired + "/" + BALLS_TO_SHOOT);
            telemetry.addData("time", String.format("%.1f", runtime.seconds()));
            telemetry.update();
        }

        // shut it all down so we dont get a penatly
        shooter.stop();
        turret.stop();
        vision.stop();

        telemetry.clear();

        if (currentPhase == Phase.ABORTED) {
            telemetry.addData("fail", abortReason);
        } else {
            telemetry.addData("yay", "shot all " + ballsFired + " balls");
        }
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }
}
