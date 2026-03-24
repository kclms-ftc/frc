package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * TeleOpMain — DECODE 2025-2026 competition TeleOp.
 *
 * Converted from LinearOpMode to iterative OpMode (per Kishi feedback).
 * LinearOpMode relies on a while loop which is hard to maintain and debug.
 * Iterative OpMode splits the logic into init(), start(), loop(), stop() —
 * each one is called automatically by the FTC scheduler.
 *
 * ━━━━━━━━━━━━━━ GAMEPAD MAPPING ━━━━━━━━━━━━━━
 *
 *   Left stick Y         → forward / backward  (field-centric)
 *   Left stick X         → strafe left / right (field-centric)
 *   Right stick X        → rotate in place
 *
 *   D-Pad Down           → PRECISION mode (30%) — lining up shot
 *   D-Pad Up             → TURBO mode (100%)   — sprint across field
 *   (neither)            → NORMAL mode (65%)
 *
 *   L1 / R1 (bumpers)   → turret CCW / CW      (soft-limited ±480°)
 *   L2 (left trigger)   → spin shooter fast (max speed)
 *   R2 (right trigger)  → spin shooter slower (cruise speed)
 *
 *   Cross (A)            → fire one ball (only if flywheels ready)
 *   Triangle (Y)         → cycle intake: OFF → INTAKE → EJECT → OFF
 *   Square (X)           → [UNMAPPED — available]
 *   Circle (B)           → [UNMAPPED — available]
 *
 * ━━━━━━━━━━━━━━ GAME RULES ENFORCED ━━━━━━━━━━
 *
 *   G408 — max 3 ARTIFACTS in CONTROL (intake disable at 3)
 *   G416 — LAUNCH only from LAUNCH ZONE (fire blocked outside)
 *   G404 — all motors stopped at OpMode end (stop() called by scheduler)
 */
@TeleOp(name = "TeleOp — DECODE Main")
public class TeleOpMain extends OpMode {

    // ── CONSTANTS ──────────────────────────────────────────────────────────

    private static final double JOYSTICK_DEADZONE = 0.05;

    private static final double TELEOP_TOTAL_SECONDS    = 120.0;
    private static final double BASE_RETURN_TRIGGER_SEC = 95.0;   // T-25 s warning

    // Shooter speed levels — tuned for consistent ball launch
    private static final double SHOOTER_SPEED_FAST  = 1800; // ticks/s — max for max range
    private static final double SHOOTER_SPEED_SLOW  = 1200; // ticks/s — slower, higher arc

    // ── GOAL POSITIONS (mm from odometry origin = robot start position) ────
    // These are the GOAL AprilTag positions in field coordinates.
    // Origin: wherever the robot is when the OpMode starts (Pinpoint zeroed at INIT).
    // Tune these after your first field session.
    // Blue alliance: GOAL is roughly 2000 mm ahead and left of starting position.
    // Red alliance: flip the X sign.
    private static final double GOAL_X_MM_BLUE = -100.0;  // TODO: tune after real field test
    private static final double GOAL_Y_MM_BLUE = 1900.0;  // TODO: tune after real field test
    private static final double GOAL_X_MM_RED  =  100.0;  // mirror of blue X
    private static final double GOAL_Y_MM_RED  = 1900.0;

    // ── LAUNCH ZONE BOUNDARIES (mm, from odometry origin) ─────────────────
    // GOAL-side LAUNCH ZONE: 6 tiles wide × 3 tiles deep.
    // Approximate values for blue alliance — tune with real odometry data.
    private static final double LAUNCH_ZONE_X_MIN =    0.0;
    private static final double LAUNCH_ZONE_X_MAX = 3657.6;
    private static final double LAUNCH_ZONE_Y_MIN =    0.0;
    private static final double LAUNCH_ZONE_Y_MAX = 1828.8;

    // ── SUBSYSTEMS & STATE ─────────────────────────────────────────────────

    private Robot       robot;
    private ElapsedTime teleOpTimer;

    // Intake toggle state machine
    private enum IntakeState { OFF, INTAKE, EJECT }
    private IntakeState intakeState = IntakeState.OFF;
    private boolean lastY = false;

    // Artifact possession counter — G408 max 3 in control
    private int possessionCount = 0;

    // Edge-detect fire button so one press = one ball
    private boolean lastA = false;

    // ── ITERATIVE OPMODE LIFECYCLE ─────────────────────────────────────────

    /**
     * init() — called once when the driver presses INIT.
     * Hardware is set up here. Robot sits completely still.
     */
    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        teleOpTimer = new ElapsedTime(); // starts counting from INIT

        // Recover MOTIF detected during AUTO (irrelevant without pattern tracking
        // but kept for the HUD "alliance side" context used by goal position choice)
        telemetry.addLine("TeleOp INIT complete — press PLAY");
        telemetry.addData("Alliance", RobotState.isBlueAlliance ? "BLUE" : "RED");
        telemetry.update();
    }

    /**
     * start() — called the moment the driver presses PLAY.
     * Re-zero the timer here so timeRemaining counts from match start.
     */
    @Override
    public void start() {
        teleOpTimer.reset();
        // Reset possession counter (defensive — in case a previous run left it dirty)
        possessionCount = 0;
    }

    /**
     * loop() — called repeatedly during PLAY (~20 ms cadence by the scheduler).
     * All control logic lives here. No while loop needed — the scheduler calls
     * this for us until the driver presses STOP.
     */
    @Override
    public void loop() {

        // ── 0. ODOMETRY UPDATE ─────────────────────────────────────────────
        // Must be first thing every loop — all position reads after this are fresh.
        robot.odometry.update();

        double elapsed       = teleOpTimer.seconds();
        double timeRemaining = TELEOP_TOTAL_SECONDS - elapsed;

        // ── 1. INTAKE TOGGLE (Triangle / Y) ───────────────────────────────
        boolean currentY = gamepad1.y;
        if (currentY && !lastY) {
            switch (intakeState) {
                case OFF:    intakeState = IntakeState.INTAKE; break;
                case INTAKE: intakeState = IntakeState.EJECT;  break;
                case EJECT:  intakeState = IntakeState.OFF;    break;
            }
        }
        lastY = currentY;

        // G408 — auto-disable intake once at 3 artifacts
        if (possessionCount >= 3 && intakeState == IntakeState.INTAKE) {
            intakeState = IntakeState.OFF;
        }

        switch (intakeState) {
            case INTAKE: robot.intake.intake(); break;
            case EJECT:  robot.intake.eject();  break;
            case OFF:    robot.intake.stop();   break;
        }

        // ── 2. FIELD-CENTRIC DRIVE ─────────────────────────────────────────
        double headingRad = robot.odometry.getHeadingRadians();
        double drive  = -applyDeadzone(gamepad1.left_stick_y); // invert Y
        double strafe =  applyDeadzone(gamepad1.left_stick_x);
        double rotate =  applyDeadzone(gamepad1.right_stick_x);

        if (gamepad1.dpad_down) {
            robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.PRECISION);
        } else if (gamepad1.dpad_up) {
            robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.TURBO);
        } else {
            robot.drivetrain.setSpeedMode(Drivetrain.SpeedMode.NORMAL);
        }

        robot.drivetrain.driveFieldCentric(drive, strafe, rotate, headingRad);

        // ── 3. TURRET (L1 / R1 bumpers, soft-limited ±480°) ───────────────
        if (gamepad1.right_bumper) {
            robot.turret.rotateRight();
        } else if (gamepad1.left_bumper) {
            robot.turret.rotateLeft();
        } else {
            robot.turret.stop();
        }

        // ── 4. SHOOTER (L2 = fast, R2 = slower) ───────────────────────────
        // Only spin up if inside LAUNCH ZONE (G416).
        boolean inLaunchZone = isInLaunchZone();

        if (gamepad1.left_trigger > 0.5 && inLaunchZone) {
            // L2 — full power, maximum launch distance
            robot.shooter.setVelocity(SHOOTER_SPEED_FAST);

        } else if (gamepad1.right_trigger > 0.5 && inLaunchZone) {
            // R2 — slower spin, shorter/higher arc (use when closer to goal)
            robot.shooter.setVelocity(SHOOTER_SPEED_SLOW);

        } else {
            // Neither trigger held, or outside launch zone — spin down
            robot.shooter.spinDown();
        }

        // ── 5. FIRE (Cross / A — edge-triggered) ──────────────────────────
        boolean currentA = gamepad1.a;
        if (currentA && !lastA && robot.shooter.isReadyToFire() && inLaunchZone) {
            robot.shooter.fireBallNonBlocking();
            if (possessionCount > 0) possessionCount--;
            RobotState.artifactsThroughSquare++;
        }
        lastA = currentA;

        // Always advance the feeder state machine (non-blocking retract)
        robot.shooter.updateFeeder();

        // ── 6. ENDGAME RUMBLE ALERT (at T-25 seconds) ─────────────────────
        if (elapsed >= BASE_RETURN_TRIGGER_SEC && elapsed < BASE_RETURN_TRIGGER_SEC + 0.1) {
            gamepad1.rumble(600); // buzz controller so driver knows to head to BASE
        }

        // ── 7. VISION — scan for GOAL AprilTag alignment ──────────────────
        int goalTagId = RobotState.isBlueAlliance ? Vision.GOAL_BLUE_ID : Vision.GOAL_RED_ID;
        robot.vision.scanForGoal(goalTagId);

        // ── 8. DRIVER HUD ──────────────────────────────────────────────────
        buildHud(timeRemaining, inLaunchZone);
    }

    /**
     * stop() — called when the driver presses STOP (or match ends).
     * G404: cut all motor power immediately.
     */
    @Override
    public void stop() {
        robot.stopAll();
    }

    // ── PRIVATE HELPERS ────────────────────────────────────────────────────

    /**
     * Builds the driver-station telemetry HUD every loop.
     * Kept in its own method so loop() stays readable.
     */
    private void buildHud(double timeRemaining, boolean inLaunchZone) {

        // Angle to goal using odometry position
        double goalX = RobotState.isBlueAlliance ? GOAL_X_MM_BLUE : GOAL_X_MM_RED;
        double goalY = RobotState.isBlueAlliance ? GOAL_Y_MM_BLUE : GOAL_Y_MM_RED;
        double angleToGoal    = robot.odometry.angleTo(goalX, goalY);
        double distToGoalMm   = robot.odometry.distanceTo(goalX, goalY);
        double headingError   = angleToGoal - robot.odometry.getHeadingDegrees();
        // Normalise to [-180, 180]
        while (headingError >  180) headingError -= 360;
        while (headingError < -180) headingError += 360;

        telemetry.addLine("═══ TeleOp ═══");
        telemetry.addData("Time remaining",  String.format("%.1f s", timeRemaining));
        telemetry.addData("Alliance",        RobotState.isBlueAlliance ? "BLUE" : "RED");

        telemetry.addLine("─── Drive ───");
        telemetry.addData("Speed mode",      robot.drivetrain.getSpeedMode().name());
        telemetry.addData("In LAUNCH ZONE",  inLaunchZone ? "✓ YES" : "✗ no");

        telemetry.addLine("─── Odometry ───");
        telemetry.addData("X",               String.format("%.0f mm", robot.odometry.getX()));
        telemetry.addData("Y",               String.format("%.0f mm", robot.odometry.getY()));
        telemetry.addData("Heading",         String.format("%.1f°",   robot.odometry.getHeadingDegrees()));

        telemetry.addLine("─── Goal (odometry angle) ───");
        telemetry.addData("Dist to goal",    String.format("%.0f mm", distToGoalMm));
        telemetry.addData("Angle to goal",   String.format("%.1f°",   angleToGoal));
        telemetry.addData("Heading error",   String.format("%.1f°",   headingError));

        telemetry.addLine("─── Vision (AprilTag) ───");
        telemetry.addData("Tag visible",     robot.vision.visible ? "YES" : "no");
        if (robot.vision.visible) {
            telemetry.addData("Bearing",     String.format("%.1f°",   robot.vision.errorDeg));
            telemetry.addData("Range",       String.format("%.1f in", robot.vision.rangeIn));
        }

        telemetry.addLine("─── Shooter ───");
        telemetry.addData("Flywheel",        robot.shooter.isReadyToFire() ? "READY ✓" : "spinning...");

        telemetry.addLine("─── Intake ───");
        telemetry.addData("State",           intakeState.name());
        telemetry.addData("Possessed",       possessionCount + " / 3");

        telemetry.addLine("─── Turret ───");
        telemetry.addData("Pos",             String.format("%.1f°", robot.turret.getPositionDegrees()));
        telemetry.addData("Ticks",           robot.turret.getPositionTicks());

        telemetry.addLine("─── GOAL RP ───");
        telemetry.addData("Artifacts fired", RobotState.artifactsThroughSquare + " / 36");

        telemetry.update();
    }

    /**
     * Returns true when the robot is inside the GOAL-side LAUNCH ZONE.
     * G416 — robot must be in a LAUNCH ZONE or overlapping a LAUNCH LINE to legally LAUNCH.
     *
     * These bounds are for BLUE alliance. Adjust LAUNCH_ZONE constants at top
     * of file for RED alliance or once you have real odometry data from the field.
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
