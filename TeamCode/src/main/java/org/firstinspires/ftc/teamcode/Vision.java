package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.google.gson.annotations.Expose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Vision {
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 480;
    private static final long MANUAL_EXPOSURE_MS = 50;
    private static final double FIXED_FOCUS_LENGTH = 50;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    public WebcamName webcam;


    public boolean visible; // Finsihed
    public double errorDeg; // Possibly Finished?
    public long timeMs; // Need slight clarification

    //gg variables added:
    public String status = "Initialising...";
    public String lastError =  "None";
    public String exposureMode = "Auto";
    public String focusMode = "Auto";

    // Store the detection object to access X, Y, Z, Range later
    private AprilTagDetection lastDetection = null;


    // Extra Variables if necessary

    public Vision(HardwareMapConfig robot) {
        webcam = robot.webcam;
        // Initialise April Tag Processor using Builder()

        // Change settings of AprilTagProcessor (Creates Visual Display of cube and axes formed from april tag) - have only set the ones whose default values are false to true
        aprilTagProcessor = new AprilTagProcessor.Builder() // Create a new AprilTag Processor Builder object.
                .setDrawAxes(true) // Default: false.
                .setDrawCubeProjection(true) // Default: false.
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11) // Optional: specify a custom Library of AprilTags.
                .build();

        // #### Note: May need to adjust resolution (after testing)
        visionPortal = new VisionPortal.Builder() // Create a new VisionPortal Builder object.
                .setCamera(webcam) // Specify the camera to be used for this VisionPortal (variable that we need to get from hardwaremapconfig)
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG) // MJPEG format uses less bandwidth than the default YUY2.
                .addProcessor(aprilTagProcessor) // Add the AprilTag Processor to the VisionPortal Builder.
                .enableLiveView(true)
                .setAutoStopLiveView(false) // Automatically stop LiveView (RC preview) when all vision processors are disabled.
                .build();

        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            status = "Error - Camera timeout";
        }

        status = "Success - Camera is streaming!";
    }


    public void init_Camera() { // similar to configureCameraControls() func in beta
        // Setup both the AprilTagProcessor and Vision Portal
        try {
            // Control Exposure(brightness) and Focus to ensure the april tag is recognisable
            // set gain?
            //getMinFocusLength()
            //getMaxFocusLength()

            ExposureControl exposure_Control = visionPortal.getCameraControl(ExposureControl.class);
            FocusControl focus_Control = visionPortal.getCameraControl(FocusControl.class);

            // Prevent crash if either end up being null
            if (exposure_Control == null) {
                exposureMode = "Auto";

            }
            else {
                exposure_Control.setMode(ExposureControl.Mode.Manual);
                exposure_Control.setExposure(50, TimeUnit.MILLISECONDS);

                exposureMode = "Manual";
            }


            if (focus_Control == null) {
                focusMode = "Auto";
            }
            else {
                focus_Control.setMode(FocusControl.Mode.Fixed);
                focus_Control.setFocusLength(50);

                focusMode = "Manual";
            }

        }
        catch (Exception error) {
            status = "Error";
            lastError = error.getMessage();

        }
    }

    /*public void scan_April_Tags() {
        // #### Note Finish this section after testing (10/02)
        status = "Running";

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

        if (detections.isEmpty()) {
            visible = false;
        }
        else {
            visible = true;
        }

        for (AprilTagDetection detection : detections) {
            if (detection.metadata == null) { //a null-check used to determine if the camera has detected a tag but cannot identify it or determine its specific properties
                continue;
            }

            telemetry.addData("X: ", detection.ftcPose.x);
            telemetry.addData("Y: ", detection.ftcPose.y);
            telemetry.addData("Z: ", detection.ftcPose.z);

            telemetry.addData("Bearing: ", detection.ftcPose.bearing); //bearing starts counting exactly from the camera's forward-facing axis (the optical center line)
            telemetry.addData("Range: ", detection.ftcPose.range); //calculates the straight-line distance from the center of camera lens to the center of the AprilTag.
        }

    }*/

    public void scanAprilTags() {
        status = "Running";
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

        visible = (detections != null && !detections.isEmpty());

        if (visible) {
            // Focus on the first identified tag
            for (AprilTagDetection detection : detections) {
                if (detection.metadata != null) {
                    errorDeg = detection.ftcPose.bearing;
                    timeMs = System.currentTimeMillis(); // Current time of detection
                    lastDetection = detection;
                    break;
                }
            }
        }
    }

    /*public void runOpMode() throws InterruptedException {
        boolean camera_initialised = init_Camera();

        if (!camera_initialised) {
            telemetry.addData("Error: ", "Camera Failed to Initialise");

            while (!isStopRequested() && !opModeIsActive()) {
                idle();
            }
            return;
        }

        telemetry.addData("Success: ", "Camera has Initialised :D");

        // Prevents Preemptive Scanning
        //waitForStart();
    }*/

    public void stop(){
        if (visionPortal != null){
            visionPortal.close();
            visionPortal = null;
        }
    }

    public void displayTelemetry(Telemetry t){
        t.addLine("--- Vision Subsystem ---");
        t.addData("Status", status);
        t.addData("Visible:", visible);
        t.addData("Bearing (Error Deg)", String.format("%.2f°", errorDeg));
        t.addData("timeMs: ", timeMs);
        t.addData("FPS", (visionPortal != null) ? visionPortal.getFps() : 0);

        t.addData("X: ", lastDetection.ftcPose.x);
        t.addData("Y: ", lastDetection.ftcPose.y);
        t.addData("Z: ", lastDetection.ftcPose.z);

        // Only show tag data if we actually see one
        if (visible) {
            t.addLine("Tag Detected!");
        }else{
            t.addLine("Searching for apriltags...");
        }

    }




}
