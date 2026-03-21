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

@TeleOp
public class Vision extends LinearOpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    public WebcamName webcam;

    // Kishi Variables

    public boolean visible; // Finsihed
    public double errorDeg; // Possibly Finished?
    public long timeMs; // Need slight clarification


    // Extra Variables if necessary

    public Vision(HardwareMapConfig robot) {
        webcam = robot.webcam;
    }

    public boolean init_Camera() {
        // Setup both the AprilTagProcessor and Vision Portal

        try {
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

            // Check for the camera to begin streaming

            sleep(5000);

            // Check Camera State

            if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                telemetry.addData("Error: ", "Camera Timeout");
                telemetry.update();
                return false;
            }

            telemetry.addData("Success: ", "Camera is Streaming");
            telemetry.update();

            // Control Exposure(brightness) and Focus to ensure the april tag is recognisable
            // set gain?
            //getMinFocusLength()
            //getMaxFocusLength()

            ExposureControl exposure_Control = visionPortal.getCameraControl(ExposureControl.class);
            FocusControl focus_Control = visionPortal.getCameraControl(FocusControl.class);

            // Prevent crash if either end up being null
            if (exposure_Control == null) {
                telemetry.addData("Exposure Mode: ", "Auto");
            }
            else {
                exposure_Control.setMode(ExposureControl.Mode.Manual);
                exposure_Control.setExposure(50, TimeUnit.MILLISECONDS);

                telemetry.addData("Exposure Mode: ", "Manual");
            }


            if (focus_Control == null) {
                telemetry.addData("Focus Mode: ", "Auto");
            }
            else {
                focus_Control.setMode(FocusControl.Mode.Fixed);
                focus_Control.setFocusLength(50);

                telemetry.addData("Focus Mode", "Manual");
            }

            telemetry.update();

            return true;
        }
        catch (Exception error) {
            telemetry.addData("Error: ", error);
            telemetry.update();

            return false;
        }
    }

    public void scan_April_Tags() {
        // #### Note Finish this section after testing (10/02)
        telemetry.addData("State: ", "Running");
        telemetry.update();

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
            telemetry.update();
        }

    }

    @Override
    public void runOpMode() throws InterruptedException {
        boolean camera_initialised = init_Camera();

        if (!camera_initialised) {
            telemetry.addData("Error: ", "Camera Failed to Initialise");
            telemetry.update();

            while (!isStopRequested() && !opModeIsActive()) {
                idle();
            }
            return;
        }

        telemetry.addData("Success: ", "Camera has Initialised :D");
        telemetry.update();

        // Prevents Preemptive Scanning
        waitForStart();

        while (opModeIsActive()) {
            scan_April_Tags();

            // Check Kishi Variables
            telemetry.addData("Visible: ", visible);
            telemetry.addData("errorDeg: ", errorDeg);
            telemetry.addData("timeMs: ", timeMs);
            telemetry.addData("fps: ", visionPortal.getFps()); // gg: may be useful to illustrate the general effects of (a) resolution and (b) processors running, on CPU performance

            sleep(1000);
        }
    }
}
