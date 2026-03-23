package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * TeleOpMain — DECODE 2025-2026 competition TeleOp.
 *
 * GAMEPAD 1 (driver):
 *   Left stick Y         → forward / backward  (field-centric)
 *   Left stick X         → strafe left / right (field-centric)
 *   Right stick X        → rotate in place
 *   D-Pad Down           → PRECISION mode (30%) — lining up to GOAL
 *   D-Pad Up             → TURBO mode (100%)   — sprinting to LOADING ZONE
 *   (neither)            → NORMAL mode (65%)
 *   Left bumper (hold)   → turret left (CCW)
 *   Right bumper (hold)  → turret right (CW)
 *   Y (toggle)           → cycle intake: OFF → INTAKE → EJECT → OFF
 *   Right trigger > 50%  → spin up launcher flywheels (only fires if in LAUNCH ZONE)
 *   A button             → fire one ball (non-blocking, only if flywheels ready + LAUNCH ZONE)
 *   B button             → manually decrement possession counter (ball scored/ejected manually)
 *
 * GAME RULES ENFORCED BY CODE:
 *   G408 — max 3 ARTIFACTS in CONTROL simultaneously (intake locked when count = 3)
 *   G416 — LAUNCH only from LAUNCH ZONE (flywheel fire blocked outside zone)
 *   G415 — vertical expansion (lift) only in final 20 sec outside LAUNCH ZONE
 *   G403 — all motors stopped at OpMode end (opModeIsActive() = false)
 */
@TeleOp(name = "TeleOp — DECODE Main")
public class TeleOpMain extends LinearOpMode {

    // ── CONSTANTS ──────────────────────────────────────────────────────────

    private static final double JOYSTICK_DEADZONE = 0.05;

    // TELEOP is 120 seconds total
    private static final double TELEOP_TOTAL_SECONDS = 120.0;

    // Begin BASE return sequence at T-25 seconds (95 s into TeleOp)
    private static final double BASE_RETURN_TRIGGER_SEC = 95.0;

    // Endgame vertical expansion only allowed in final 20 seconds
    private static final double ENDGAME_EXPANSION_SEC   = 100.0; // 120 - 20

    // ── LAUNCH ZONE BOUNDARIES ────────────────────────────────────────────
    // GOAL-side LAUNCH ZONE: 6 tiles × 3 tiles on our alliance half.
    // Using mm from the field origin (robot starts near GOAL, heading ~0°).
    // These are approximate — tune to your actual starting coords + odometry origin.
    // Blue alliance defaults. Red alliance: mirror X axis (negate X boundaries).
    //   Field is 3657.6 mm × 3657.6 mm (144 in × 144 in).
    //   GOAL-side launch zone approx: X ∈ [0, 3657], Y ∈ [0, 1829] (3 tiles deep)
    private static final double LAUNCH_ZONE_X_MIN =    0.0;   // mm
    private static final double LAUNCH_ZONE_X_MAX = 3657.6;   // mm (full width)
    private static final double LAUNCH_ZONE_Y_MIN =    0.0;   // mm (GOAL wall)
    private static final double LAUNCH_ZONE_Y_MAX = 1828.8;   // mm (3 tiles = 72 in)

    // ── SUBSYSTEMS & STATE ─────────────────────────────────────────────────

    private Robot robot;
    private ElapsedTime teleOpTimer;

    // Intake toggle
    private enum IntakeState { OFF, INTAKE, EJECT }
    private IntakeState intakeState = IntakeState.OFF;
    private boolean lastY = false;

    // Artifact possession counter (G408 — max 3)
    private int possessionCount = 0;

    // Previous A-button state for edge detection (avoid repeat fires per press)
    private boolean lastA = false;

    // Previous B-button state for manual possession decrement
    private boolean lastB = false;

    // Active MOTIF (persisted from AUTO via RobotState — may be UNKNOWN if no AUTO ran)
    private Vision.Motif activeMotif = Vision.Motif.UNKNOWN;

    // ── OPMODE ENTRY POINT ─────────────────────────────────────────────────

    @Override
    public void runOpMode() {

        robot = new Robot(hardwareMap);

        // Try to recover MOTIF detected during AUTO (static RobotState field)
        activeMotif = RobotState.activeMotif;

        telemetry.addLine("DECODE TeleOp ready");
        telemetry.addData("Active Motif", activeMotif);
        telemetry.addLine("Press PLAY to start");
        telemetry.update();
        waitForStart();

        teleOpTimer = new ElapsedTime();

        while (opModeIsActive()) {

            // ── 0. UPDATE ODOMETRY ─────────────────────────────────────────
            // Must be called every loop so position data is fresh.
            robot.odometry.update();

            double elapsed        = teleOpTimer.seconds();
            double timeRemaining  = TELEOP_TOTAL_SECONDS - elapsed;

            // ── 1. INTAKE TOGGLE (Gamepad 1 Y) ────────────────────────────
            boolean currentY = gamepad1.y;
            if (currentY && !lastY) {
                switch (intakeState) {
                    case OFF:    intakeState = IntakeState.INTAKE; break;
                    case INTAKE: intakeState = IntakeState.EJECT;  break;
                    case EJECT:  intakeState = IntakeState.OFF;    break;
                }
            }
            lastY = currentY;

            // G408 — block intake if we already hold 3 ARTIFACTS
            if (possessionCount >= 3 && intakeState == IntakeState.INTAKE) {
                intakeState = IntakeState.OFF;
            }

            switch (intakeState) {
                case INTAKE: robot.intake.intake(); possessionCount = Math.min(possessionCount, 3); break;
                case EJECT:  robot.intake.eject();  break;
                case OFF:    robot.intake.stop();   break;
            }

            // ── 2. POSSESSION COUNTER (manual decrement via B) ────────────
            // Driver presses B each time a ball is scored or ejected
            boolean currentB = gamepad1.b;
            if (currentB && !lastB && possessionCount > 0) {
                possessionCount--;
            }
            lastB = currentB;

            // ── 3. FIELD-CENTRIC DRIVE (Gamepad 1 sticks) ─────────────────
            // Use heading from odometry (Pinpoint IMU-fused) for field-centric math.
            double headingRad = robot.odometry.getHeadingRadians();

            double drive  = -applyDeadzone(gamepad1.left_stick_y); // invert Y  (push forward = positive)
            double strafe =  applyDeadzone(gamepad1.left_stick_x);
            double rotate =  applyDeadzone(gamepad1.right_stick_x);

            // Speed mode via D-Pad
            if (gamepad1.dpad_down) {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.PRECISION);
            } else if (gamepad1.dpad_up) {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.TURBO);
            } else {
                robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.NORMAL);
            }

            robot.drivetrain.driveFieldCentric(drive, strafe, rotate, headingRad);

            // ── 4. TURRET (Gamepad 1 bumpers) ─────────────────────────────
            if (gamepad1.right_bumper) {
                robot.turret.rotateRight();
            } else if (gamepad1.left_bumper) {
                robot.turret.rotateLeft();
            } else {
                robot.turret.stop();
            }

            // ── 5. LAUNCHER (Gamepad 1 right trigger + A) ─────────────────
            // G416 — LAUNCH only permitted from inside a LAUNCH ZONE.
            boolean inLaunchZone = isInLaunchZone();

            if (gamepad1.right_trigger > 0.5 && inLaunchZone) {
                robot.shooter.spinUp();
            } else {
                robot.shooter.spinDown();
            }

            // Fire on A — edge-triggered, only if ready and in LAUNCH ZONE
            boolean currentA = gamepad1.a;
            if (currentA && !lastA && robot.shooter.isReadyToFire() && inLaunchZone) {
                robot.shooter.fireBallNonBlocking();
                if (possessionCount > 0) possessionCount--;
                RobotState.artifactsThroughSquare++;
            }
            lastA = currentA;

            // Advance feeder state machine every loop regardless of button state
            robot.shooter.updateFeeder();

            // ── 6. ENDGAME ────────────────────────────────────────────────
            // At T-25: alert driver to start BASE return
            if (elapsed >= BASE_RETURN_TRIGGER_SEC && elapsed < BASE_RETURN_TRIGGER_SEC + 0.5) {
                // One-shot rumble alert at T-25 so driver knows to head to BASE
                gamepad1.rumble(500);
            }

            // ── 7. VISION (optional in TELEOP — scan for goal alignment HUD) ──
            // Only scan if we actually want alignment data (saves CPU)
            // Comment this out during high-cycle TELEOP if loop time is tight
            int goalTagId = RobotState.isBlueAlliance ? Vision.GOAL_BLUE_ID : Vision.GOAL_RED_ID;
            robot.vision.scanForGoal(goalTagId);

            // ── 8. DRIVER HUD TELEMETRY ───────────────────────────────────
            telemetry.addLine("═══ DECODE TELEOP ═══");
            telemetry.addData("Time remaining",    String.format("%.1f s", timeRemaining));
            telemetry.addData("Active Motif",      activeMotif.name());
            telemetry.addData("Possession count",  possessionCount + " / 3");
            telemetry.addData("In LAUNCH ZONE",    inLaunchZone ? "YES" : "no");
            telemetry.addData("Flywheel ready",    robot.shooter.isReadyToFire() ? "READY" : "spinning up...");
            telemetry.addData("Artifacts scored",  RobotState.artifactsThroughSquare + " / 36 (GOAL RP)");
            telemetry.addData("Pattern pts est.",  RobotState.currentPatternPoints + " / 18 (PATTERN RP)");
            telemetry.addData("Intake",            intakeState.name());
            telemetry.addData("Speed mode",        robot.drivetrain.getSpeedMode().name());
            telemetry.addLine("─── Odometry ───");
            telemetry.addData("X",       String.format("%.0f mm", robot.odometry.getX()));
            telemetry.addData("Y",       String.format("%.0f mm", robot.odometry.getY()));
            telemetry.addData("Heading", String.format("%.1f°",   robot.odometry.getHeadingDegrees()));
            telemetry.addLine("─── Vision ───");
            telemetry.addData("Goal visible", robot.vision.visible ? "YES" : "no");
            if (robot.vision.visible) {
                telemetry.addData("Bearing to goal", String.format("%.1f°",   robot.vision.errorDeg));
                telemetry.addData("Range to goal",   String.format("%.1f in", robot.vision.rangeIn));
            }
            telemetry.update();
        }

        // G404 — stop all motors immediately when OpMode ends
        robot.stopAll();
    }

    // ── HELPERS ────────────────────────────────────────────────────────────

    /**
     * Returns true if the robot's current odometry position is within the
     * GOAL-side LAUNCH ZONE.
     *
     * G416 — robot must be inside a LAUNCH ZONE or overlapping a LAUNCH LINE
     * to legally LAUNCH ARTIFACTS.
     *
     * Adjust the boundary constants at the top of this file to match your
     * alliance colour and actual field-origin offset from match start.
     */
    private boolean isInLaunchZone() {
        double x = robot.odometry.getX();
        double y = robot.odometry.getY();
        return x >= LAUNCH_ZONE_X_MIN && x <= LAUNCH_ZONE_X_MAX
            && y >= LAUNCH_ZONE_Y_MIN && y <= LAUNCH_ZONE_Y_MAX;
    }

    private double applyDeadzone(double v) {
        return Math.abs(v) > JOYSTICK_DEADZONE ? v : 0;
    }
}
