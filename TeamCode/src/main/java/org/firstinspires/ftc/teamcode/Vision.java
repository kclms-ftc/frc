package org.firstinspires.ftc.teamcode;

import android.util.Size;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Vision — AprilTag detection subsystem for DECODE 2025-2026.
 *
 * RESPONSIBILITIES:
 *   1. Detect the OBELISK AprilTag (IDs 21/22/23) to determine the active MOTIF.
 *   2. Locate the ALLIANCE GOAL AprilTag (ID 20 = blue, 24 = red) for targeting.
 *   3. Provide bearing/range data to the drivetrain for GOAL alignment.
 *
 * APRILTAG IDs:
 *   20 — Blue Alliance GOAL  (navigation + targeting)
 *   21 — OBELISK: MOTIF = GPP
 *   22 — OBELISK: MOTIF = PGP
 *   23 — OBELISK: MOTIF = PPG
 *   24 — Red Alliance GOAL   (navigation + targeting)
 *
 * MOTIF ENUM (declared here, used by RobotState and PatternTracker):
 *   GPP, PGP, PPG, UNKNOWN
 */
public class Vision {

    // -------------------------------------------------------
    // MOTIF — the colour sequence revealed by the OBELISK
    // -------------------------------------------------------
    public enum Motif {
        GPP,    // AprilTag 21 — sequence: G P P (repeating)
        PGP,    // AprilTag 22 — sequence: P G P (repeating)
        PPG,    // AprilTag 23 — sequence: P P G (repeating)
        UNKNOWN // OBELISK not yet read or not visible
    }

    // -------------------------------------------------------
    // ARTIFACT COLOUR — used for PATTERN matching
    // -------------------------------------------------------
    public enum ArtifactColor { GREEN, PURPLE, UNKNOWN }

    // -------------------------------------------------------
    // GOAL APRILTAG IDs
    // -------------------------------------------------------
    public static final int GOAL_BLUE_ID = 20;
    public static final int GOAL_RED_ID  = 24;

    // -------------------------------------------------------
    // CAMERA & VISION CONSTANTS
    // -------------------------------------------------------
    private static final int    CAMERA_WIDTH       = 640;
    private static final int    CAMERA_HEIGHT      = 480;
    private static final long   MANUAL_EXPOSURE_MS = 6;    // ms — tuned for bright field
    private static final double FIXED_FOCUS_LENGTH = 0;    // 0 = near-focus (tags are close)

    // -------------------------------------------------------
    // HARDWARE REFS
    // -------------------------------------------------------
    private final WebcamName       webcam;
    private       VisionPortal     visionPortal;
    private       AprilTagProcessor aprilTagProcessor;

    // -------------------------------------------------------
    // STATE (readable by OpModes via public fields)
    // -------------------------------------------------------
    /** True when at least one tag with known metadata was detected last scan. */
    public boolean visible = false;

    /**
     * Bearing to the most-recently detected tag (degrees).
     * Positive = tag is to the right. Use for aiming corrections.
     */
    public double errorDeg = 0.0;

    /** Range to the most-recently detected tag (inches). */
    public double rangeIn = 0.0;

    /** System clock at time of last successful detection (ms). */
    public long timeMs = 0;

    /** Status string for telemetry / debugging. */
    public String status    = "Initialising...";
    public String lastError = "None";

    /** Cached last full detection object — null if no tag seen yet. */
    private AprilTagDetection lastDetection = null;

    // -------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------

    public Vision(HardwareMapConfig robot) {
        webcam = robot.webcam;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                // TAG_36h11 is the FTC-legal family for DECODE 2025-2026
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam)
                .setCameraResolution(new Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(true)
                .setAutoStopLiveView(false)
                .build();

        // Wait for the camera to reach STREAMING before touching exposure/focus
        // controls — calling them before streaming causes a silent no-op or crash.
        waitForCameraStreaming();

        // Then lock exposure so tags are crisp under field lighting
        configureCamera();

        status = "Vision ready";
    }

    // -------------------------------------------------------
    // CAMERA INIT HELPERS
    // -------------------------------------------------------

    /**
     * Blocks until the portal reaches STREAMING state (max ~3 seconds).
     * Must be called after build() and before configureCamera().
     */
    private void waitForCameraStreaming() {
        long deadline = System.currentTimeMillis() + 3000;
        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING
                && System.currentTimeMillis() < deadline) {
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        }
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            status    = "Error — camera did not reach STREAMING";
            lastError = "Timeout waiting for camera";
        }
    }

    /**
     * Locks exposure and focus so AprilTags are recognisable under bright
     * competition lighting. Auto-exposure causes flicker that breaks detection.
     */
    private void configureCamera() {
        try {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl != null) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                exposureControl.setExposure(MANUAL_EXPOSURE_MS, TimeUnit.MILLISECONDS);
            }

            FocusControl focusControl = visionPortal.getCameraControl(FocusControl.class);
            if (focusControl != null) {
                focusControl.setMode(FocusControl.Mode.Fixed);
                focusControl.setFocusLength(FIXED_FOCUS_LENGTH);
            }

        } catch (Exception e) {
            status    = "Camera config error";
            lastError = e.getMessage() != null ? e.getMessage() : "unknown";
        }
    }

    // -------------------------------------------------------
    // MOTIF DETECTION  (call during AUTO start phase)
    // -------------------------------------------------------

    /**
     * Scans all current detections for an OBELISK tag (IDs 21, 22, 23).
     * Returns the corresponding Motif, or UNKNOWN if none is visible.
     *
     * Call this during the first ~3 seconds of AUTO — the OBELISK is stable
     * and will not move during a MATCH.  Do NOT use OBELISK tags for navigation.
     */
    public Motif detectMotif() {
        if (aprilTagProcessor == null) return Motif.UNKNOWN;

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections == null) return Motif.UNKNOWN;

        for (AprilTagDetection tag : detections) {
            switch (tag.id) {
                case 21: return Motif.GPP;
                case 22: return Motif.PGP;
                case 23: return Motif.PPG;
            }
        }
        return Motif.UNKNOWN;
    }

    // -------------------------------------------------------
    // GOAL TARGETING  (call during launch-zone alignment)
    // -------------------------------------------------------

    /**
     * Scans for a specific GOAL AprilTag (ID 20 for blue, 24 for red).
     * Updates {@code visible}, {@code errorDeg}, {@code rangeIn}, and
     * {@code lastDetection} if found.
     *
     * @param allianceGoalId  Pass GOAL_BLUE_ID (20) or GOAL_RED_ID (24).
     * @return true if that specific goal tag was found and pose is available.
     */
    public boolean scanForGoal(int allianceGoalId) {
        if (aprilTagProcessor == null) return false;

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections == null || detections.isEmpty()) {
            visible = false;
            return false;
        }

        for (AprilTagDetection detection : detections) {
            if (detection.id == allianceGoalId && detection.metadata != null) {
                visible       = true;
                errorDeg      = detection.ftcPose.bearing; // degrees left(-) / right(+)
                rangeIn       = detection.ftcPose.range;   // inches to tag
                timeMs        = System.currentTimeMillis();
                lastDetection = detection;
                return true;
            }
        }

        visible = false;
        return false;
    }

    /**
     * General scan — updates state from the first valid tag found.
     * Useful in TELEOP to keep errorDeg / rangeIn fresh for driver HUD.
     */
    public void scanAprilTags() {
        status = "Scanning";
        if (aprilTagProcessor == null) return;

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        visible = (detections != null && !detections.isEmpty());

        if (visible) {
            for (AprilTagDetection detection : detections) {
                if (detection.metadata != null) {
                    errorDeg      = detection.ftcPose.bearing;
                    rangeIn       = detection.ftcPose.range;
                    timeMs        = System.currentTimeMillis();
                    lastDetection = detection;
                    break;
                }
            }
        }
    }

    // -------------------------------------------------------
    // PATTERN HELPER
    // -------------------------------------------------------

    /**
     * Given the active Motif and a RAMP index (1-9), returns the expected
     * ARTIFACT colour at that position.
     *
     * MOTIF repeats 3× across the 9 RAMP slots:
     *   GPP → [G,P,P, G,P,P, G,P,P]
     *   PGP → [P,G,P, P,G,P, P,G,P]
     *   PPG → [P,P,G, P,P,G, P,P,G]
     *
     * @param motif     The active MOTIF (from OBELISK scan)
     * @param rampIndex RAMP index 1-9 (1 = GATE end, 9 = SQUARE end)
     * @return expected ArtifactColor at that index
     */
    public static ArtifactColor expectedColorAtIndex(Motif motif, int rampIndex) {
        int pos = (rampIndex - 1) % 3; // 0, 1, or 2 within the motif triplet
        switch (motif) {
            case GPP:
                return pos == 0 ? ArtifactColor.GREEN : ArtifactColor.PURPLE;
            case PGP:
                return pos == 1 ? ArtifactColor.GREEN : ArtifactColor.PURPLE;
            case PPG:
                return pos == 2 ? ArtifactColor.GREEN : ArtifactColor.PURPLE;
            default:
                return ArtifactColor.UNKNOWN;
        }
    }

    // -------------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------------

    /**
     * Pause vision processing to save CPU during TELEOP if not needed.
     * Re-enable with resume().
     */
    public void pause() {
        if (visionPortal != null) {
            visionPortal.stopLiveView();
            aprilTagProcessor.setEnabled(false);
        }
    }

    public void resume() {
        if (visionPortal != null) {
            aprilTagProcessor.setEnabled(true);
            visionPortal.resumeLiveView();
        }
    }

    /** Full shutdown — call in stopAll(). VisionPortal cannot be reopened after this. */
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }

    // -------------------------------------------------------
    // TELEMETRY
    // -------------------------------------------------------

    public void displayTelemetry(Telemetry t) {
        t.addLine("--- Vision ---");
        t.addData("Status",  status);
        t.addData("Visible", visible);

        if (visible && lastDetection != null && lastDetection.metadata != null) {
            t.addData("Tag ID",   lastDetection.id);
            t.addData("Bearing",  String.format("%.2f°",  errorDeg));
            t.addData("Range",    String.format("%.1f in", rangeIn));
            t.addData("X",        String.format("%.1f in", lastDetection.ftcPose.x));
            t.addData("Y",        String.format("%.1f in", lastDetection.ftcPose.y));
            t.addData("Z",        String.format("%.1f in", lastDetection.ftcPose.z));
        } else {
            t.addLine("No tag detected");
        }
        t.addData("FPS", (visionPortal != null) ? String.format("%.1f", visionPortal.getFps()) : "0");
        t.addData("Last error", lastError);
    }
}
