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
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.concurrent.TimeUnit;

@TeleOp
public class Vision extends LinearOpMode {
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    public WebcamName webcam;

    public boolean visible;
    public double errorDeg;
    public long timeMs;

    public Vision(HardwareMap hw) {
        this.webcam = hw.get(WebcamName.class, "webcam");
    }

    public boolean init_Camera() {
        // Setup both the AprilTagProcessor and Vision Portal

        try {
            // Initialise April Tag Processor using Builder()

            // Change settings of AprilTagProcessor (Creates Visual Display of cube and axes formed from april tag)
            aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setDrawAxes(true)
                    .setDrawCubeProjection(true)
                    .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                    .build();

            // #### Note: May need to adjust resolution
            visionPortal = new VisionPortal.Builder()
                    .setCamera(webcam)
                    .setCameraResolution(new Size(640, 480))
                    .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                    .addProcessor(aprilTagProcessor)
                    .enableLiveView(true)
                    .setAutoStopLiveView(false)
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

            // Control Exposure and Focus to ensure the april tag is recognisable

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
            sleep(1000);
        }
    }
}
