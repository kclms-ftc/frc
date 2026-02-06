package org.firstinspires.ftc.teamcode;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FTC AprilTag Detection with 3D Cube Visualization
 * OPTIMIZED FOR "USB 2.0 Camera" DEVICE
 * 
 * Configuration Required:
 * - Device: "USB 2.0 Camera" (NOT the ghost "USB2.0 HD UVC WebCam")
 * - Name: "Webcam 1" (exactly)
 * 
 * Features:
 * - Real-time AprilTag detection
 * - 3D cube visualization around detected tags
 * - Position and orientation tracking
 * - Optimized for generic 1080P webcams
 */
@TeleOp(name="AprilTag Cube - USB 2.0 Camera", group="Vision")
public class AprilTagUSB20Camera extends LinearOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    
    @Override
    public void runOpMode() {
        
        boolean cameraInitialized = initializeAprilTagDetection();
        
        if (!cameraInitialized) {
            telemetry.addData("❌ ERROR", "Camera failed to initialize");
            telemetry.addLine();
            telemetry.addData("Check:", "");
            telemetry.addData("1.", "USB cable connected");
            telemetry.addData("2.", "Camera = 'Webcam 1' in config");
            telemetry.addData("3.", "Using 'USB 2.0 Camera' device");
            telemetry.addData("4.", "Config activated");
            telemetry.addData("5.", "Robot Controller restarted");
            telemetry.update();
            
            while (!isStopRequested() && !opModeIsActive()) {
                idle();
            }
            return;
        }
        
        telemetry.addData("✅ Status", "Camera Ready!");
        telemetry.addData("Device", "USB 2.0 Camera");
        telemetry.addData(">", "Press Play to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            processAprilTagDetections();
            sleep(50);
        }
        
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    private boolean initializeAprilTagDetection() {
        
        try {
            // Create AprilTag processor with 3D visualization
            aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)              // Draw RGB coordinate axes
                .setDrawCubeProjection(true)    // Draw 3D cube around tag
                .setDrawTagOutline(true)        // Draw tag outline
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .build();
            
            telemetry.addData("Status", "Connecting to camera...");
            telemetry.addData("Device", "USB 2.0 Camera");
            telemetry.update();
            
            // Create vision portal - optimized for generic cameras
            visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))  // VGA resolution
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(true)
                .setAutoStopLiveView(false)
                .build();
            
            // Wait for camera to start streaming
            int timeout = 50;
            while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING && timeout > 0) {
                telemetry.addData("Camera State", visionPortal.getCameraState());
                telemetry.addData("Timeout", "%d sec", timeout / 10);
                telemetry.update();
                sleep(100);
                timeout--;
            }
            
            if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                telemetry.addData("❌ FAILED", "Camera not streaming");
                telemetry.addData("State", visionPortal.getCameraState());
                telemetry.update();
                return false;
            }
            
            telemetry.addData("✅ Camera", "STREAMING");
            telemetry.update();
            
            configureCameraControls();
            sleep(1000);
            return true;
            
        } catch (Exception e) {
            telemetry.addData("❌ Exception", e.getMessage());
            telemetry.update();
            return false;
        }
    }
    
    private void configureCameraControls() {
        try {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl != null) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                exposureControl.setExposure(50, TimeUnit.MILLISECONDS);
                telemetry.addData("Exposure", "Manual");
            } else {
                telemetry.addData("Exposure", "Auto");
            }
            
            FocusControl focusControl = visionPortal.getCameraControl(FocusControl.class);
            if (focusControl != null) {
                focusControl.setMode(FocusControl.Mode.Fixed);
                focusControl.setFocusLength(50);
                telemetry.addData("Focus", "Fixed");
            } else {
                telemetry.addData("Focus", "Auto");
            }
        } catch (Exception e) {
            telemetry.addData("Controls", "Auto only");
        }
        telemetry.update();
    }

    private void processAprilTagDetections() {
        
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        
        telemetry.addData("AprilTags", detections.size());
        telemetry.addData("FPS", String.format("%.1f", visionPortal.getFps()));
        telemetry.addData("State", visionPortal.getCameraState());
        telemetry.addLine();
        
        for (AprilTagDetection detection : detections) {
            
            if (detection.metadata != null) {
                telemetry.addLine(String.format("═══ Tag ID %d ═══", detection.id));
                
                telemetry.addLine(String.format("Position: X=%.1f Y=%.1f Z=%.1f in", 
                    detection.ftcPose.x, 
                    detection.ftcPose.y, 
                    detection.ftcPose.z));
                
                telemetry.addLine(String.format("Rotation: P=%.1f R=%.1f Y=%.1f°",
                    detection.ftcPose.pitch, 
                    detection.ftcPose.roll, 
                    detection.ftcPose.yaw));
                
                telemetry.addLine(String.format("Range: %.1f in  Bearing: %.1f°",
                    detection.ftcPose.range,
                    detection.ftcPose.bearing));
                    
            } else {
                telemetry.addLine(String.format("═══ Tag ID %d (unknown) ═══", detection.id));
            }
            
            telemetry.addLine();
        }
        
        telemetry.addLine("═══════════════════");
        telemetry.addData("📷 View", "Tap camera icon for 3D cubes!");
        
        if (detections.size() == 0) {
            telemetry.addLine();
            telemetry.addData("No tags?", "");
            telemetry.addData("", "• Move closer (12-24 in)");
            telemetry.addData("", "• Better lighting");
            telemetry.addData("", "• Tag flat & visible");
        }
        
        telemetry.update();
    }
}
