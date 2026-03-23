package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * AprilTagWebcam — standalone AprilTag detector utility.
 *
 * NOTE: In the DECODE 2025-2026 codebase this class is kept as a lightweight
 *       utility / test harness. The main competition Vision subsystem
 *       (Vision.java) is used by Robot.java. This class can be used in
 *       diagnostic OpModes without instantiating the full Robot object.
 *
 * USAGE:
 *   AprilTagWebcam cam = new AprilTagWebcam();
 *   cam.init(hardwareMap, telemetry);
 *   List<AprilTagDetection> tags = cam.getDetections();
 *   cam.close();
 */
public class AprilTagWebcam {

    private AprilTagProcessor      aprilTagProcessor;
    private VisionPortal           visionPortal;
    private List<AprilTagDetection> detectedTags = new ArrayList<>();
    private Telemetry              telemetry;

    /**
     * Initialise the webcam and AprilTag processor.
     *
     * @param hwMap    Hardware map from the OpMode
     * @param telemetry Telemetry object for logging
     */
    public void init(HardwareMap hwMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build(); // was missing .build() — caused NPE on any subsequent call

        WebcamName webcam = hwMap.get(WebcamName.class, "webcam");

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam)
                .addProcessor(aprilTagProcessor)
                .build();
    }

    /**
     * Returns the list of detected AprilTags from the most recent camera frame.
     * Returns an empty list (never null) if none were detected or init not called.
     */
    public List<AprilTagDetection> getDetections() {
        if (aprilTagProcessor == null) return detectedTags;
        List<AprilTagDetection> raw = aprilTagProcessor.getDetections();
        if (raw != null) {
            detectedTags = raw;
        }
        return detectedTags;
    }

    /**
     * Logs all currently detected tags to telemetry.
     */
    public void displayTelemetry() {
        if (telemetry == null) return;
        List<AprilTagDetection> tags = getDetections();
        if (tags.isEmpty()) {
            telemetry.addLine("AprilTagWebcam: no tags detected");
        } else {
            for (AprilTagDetection tag : tags) {
                telemetry.addData("Tag ID", tag.id);
                if (tag.metadata != null) {
                    telemetry.addData("  Bearing", String.format("%.2f°",  tag.ftcPose.bearing));
                    telemetry.addData("  Range",   String.format("%.1f in", tag.ftcPose.range));
                }
            }
        }
    }

    /** Clean up the VisionPortal. Call in OpMode stop(). */
    public void close() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }
}
