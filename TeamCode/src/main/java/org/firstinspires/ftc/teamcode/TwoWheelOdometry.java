package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Arrays;

public class TwoWheelOdometry {
    //1. define constants
    public static double TICKS_PER_REV = 0;
    public static double WHEEL_RADIUS = 0;
    public static double GEAR_RATIO = 1;


    //2. define wheel positions (in inches)
    public static Pose2D PARALLEL_POSE = new Pose2D(0,0,0);
    public static Pose2D PERPENDICULAR_POSE = new Pose2D(0,0,Math.toRadians(90));

    // private SampleMecanumDrive drive;

    public MyTracingWheelLocalizer(HardwareMap hwMap, SampleMecanumDrive drive){
        super(Arrays.asList(PARALLEL_POSE, PERPENDICULAR_POSE));
        this.drive = drive;
        // initialise encoders here (hwMap.get(DcMotor "")

    }
    @Override
    public double getHeading(){
        return drive.getRawExternalHeadinng();
    }

    // getWheelPositions() and getWHeelVelocities()
}
