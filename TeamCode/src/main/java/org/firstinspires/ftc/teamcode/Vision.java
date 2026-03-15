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

// apriltag camera pipeline
// call update() every loop BEFORE reading any tag data
// call stop() when youre done to free the camera
public class Vision {

    private static final int CAMERA_WIDTH  = 640;
    private static final int CAMERA_HEIGHT = 480;
    private static final long MANUAL_EXPOSURE_MS = 50;
    private static final double FIXED_FOCUS_LENGTH = 50;

    private final WebcamName webcamName;
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal      visionPortal;

    private List<AprilTagDetection> currentDetections;

    // enableLiveview = true for teleop (shows preview on DS)
    // false for auto to save cpu
    public Vision(HardwareMapConfig robot, boolean enableLiveView) {
        this.webcamName = robot.webcam;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .setCameraResolution(new Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(enableLiveView)
                .setAutoStopLiveView(false)
                .build();
    }

    // locks exposure + focus so detection doesnt flicker
    // fails silently if camera doesnt support manual controls
    public void configureCameraControls() {
        try {
            ExposureControl exposure = visionPortal.getCameraControl(ExposureControl.class);
            if (exposure != null) {
                exposure.setMode(ExposureControl.Mode.Manual);
                exposure.setExposure(MANUAL_EXPOSURE_MS, TimeUnit.MILLISECONDS);
            }

            FocusControl focus = visionPortal.getCameraControl(FocusControl.class);
            if (focus != null) {
                focus.setMode(FocusControl.Mode.Fixed);
                focus.setFocusLength(FIXED_FOCUS_LENGTH);
            }
        } catch (Exception ignored) {}
    }

    // must call every loop tick before anything reads tag data
    public void update() {
        currentDetections = aprilTagProcessor.getDetections();
    }

    // returns detection for a specific tag id, or null if not visble
    public AprilTagDetection getTagById(int tagId) {
        if (currentDetections == null) return null;

        for (AprilTagDetection detection : currentDetections) {
            if (detection.id == tagId && detection.metadata != null) {
                return detection;
            }
        }
        return null;
    }

    public List<AprilTagDetection> getAllDetections() {
        return currentDetections;
    }

    public boolean isStreaming() {
        return visionPortal != null
                && visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING;
    }

    public float getFps() {
        return visionPortal != null ? visionPortal.getFps() : 0f;
    }

    public void displayTelemetry(AprilTagDetection target, Telemetry telemetry) {
        int tagCount = (currentDetections != null) ? currentDetections.size() : 0;
        telemetry.addData("Vision | Tags seen", tagCount);
        telemetry.addData("Vision | FPS", String.format("%.1f", getFps()));

        if (target != null) {
            telemetry.addData("Target | ID",      target.id);
            telemetry.addData("Target | Bearing", String.format("%.1f°", target.ftcPose.bearing));
            telemetry.addData("Target | Range",   String.format("%.1f in", target.ftcPose.range));
        } else {
            telemetry.addData("Target", "NOT VISIBLE");
        }
    }

    // free camera resource — call this in cleanup
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
            visionPortal = null;
        }
    }
}
