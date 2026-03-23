package org.firstinspires.ftc.teamcode;

/**
 * RobotState — static shared state between autonomous and TeleOp OpModes.
 *
 * FTC SDK runs AUTO and TELEOP in the same process — static fields persist
 * between OpModes as long as the RC app is alive. This lets TELEOP read the
 * MOTIF and score progress that AUTO detected/set.
 *
 * THREAD SAFETY: All fields are written from the main OpMode thread only.
 * No synchronisation needed for single-thread access.
 *
 * RESET: Values survive OpMode restarts within the same RC session.
 * Re-running AUTO will overwrite activeMotif and reset counters correctly.
 */
public class RobotState {

    // ── MOTIF (detected by AUTO from OBELISK AprilTag) ────────────────────
    /**
     * The active MOTIF for this MATCH (GPP, PGP, PPG, or UNKNOWN).
     * Set during AUTO from OBELISK AprilTag scan; read during TELEOP for
     * PATTERN tracking and driver HUD.
     */
    public static Vision.Motif activeMotif = Vision.Motif.UNKNOWN;

    // ── SCORE TRACKING ────────────────────────────────────────────────────
    /**
     * Running count of ARTIFACTS that have passed through the SQUARE
     * (i.e., scored as CLASSIFIED into the GOAL and through the RAMP).
     * GOAL RP threshold: >= 36 (standard events), >= 42 (regionals).
     */
    public static int artifactsThroughSquare = 0;

    /**
     * Estimated PATTERN points based on ARTIFACTS scored in the correct colour
     * order. Updated by TELEOP when a ball is fired.
     * PATTERN RP threshold: >= 18 (standard events), >= 22 (regionals).
     *
     * Note: this is a driver-station estimate — final scoring is by judges
     * who inspect the physical RAMP at match end.
     */
    public static int currentPatternPoints = 0;

    // ── ALLIANCE ──────────────────────────────────────────────────────────
    /**
     * True = blue alliance (GOAL AprilTag ID 20, columns A-C).
     * False = red alliance (GOAL AprilTag ID 24, columns D-F).
     * Set by the AUTO OpMode before match start or by a config OpMode.
     */
    public static boolean isBlueAlliance = true;

    // ── CONVENIENCE RESET ─────────────────────────────────────────────────
    /**
     * Resets all score counters to zero. Call at the start of AUTO
     * to ensure counts don't carry over from a previous match in
     * the same RC session.
     */
    public static void reset() {
        activeMotif            = Vision.Motif.UNKNOWN;
        artifactsThroughSquare = 0;
        currentPatternPoints   = 0;
        // isBlueAlliance is intentionally NOT reset — set once per session
    }
}
