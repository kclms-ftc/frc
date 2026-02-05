package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TestBenchIMU {
    private IMU imu;
    private DcMotor pod_x, pod_y;

    //Position variables
    private double robotX = 0, robotY = 0;
    private int lastXPos = 0, lastYPos = 0;

    //constants - adjust to our robot
    public static double TICKS_PER_INCH = 1800;

    public void init(HardwareMap hwMap){
        imu = hwMap.get(IMU.class, "imu" );

        // Replace these with actual encoder ports (usually motor ports)
        pod_x = hwMap.get(DcMotor.class, "backleft");
        pod_y = hwMap.get(DcMotor.class, "backright");


        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        /* IMU configuration
         imu.setOffSets()
         imu.setEncoderResolution()
         */

    }

    public void resetPosition(){

        //Odometry Starting Position
        imu.resetYaw(); // how do i reset start position
        robotX = 0;
        robotY = 0;
        lastXPos = pod_x.getCurrentPosition();
        lastYPos = pod_y.getCurrentPosition();

    }

    public void updatePosition(){
        //output: the GPS
        int currentX = pod_x.getCurrentPosition();
        int currentY = pod_y.getCurrentPosition();
        double heading = getHeading(AngleUnit.RADIANS);

        //Calculate change in distance
        double deltaX = (currentX - lastXPos) / TICKS_PER_INCH;
        double deltaY = (currentY - lastYPos) / TICKS_PER_INCH;

        //Rotation Matrix: Convert local movement to field oriented movement
        robotX += deltaX * Math.cos(heading) - deltaY * Math.sin(heading);
        robotY += deltaX * Math.sin(heading) + deltaY * Math.cos(heading);

        lastXPos = currentX;
        lastYPos = currentY;
    }

    public double getPosX() { return robotX; }
    public double getPosY() { return robotY; }


    public double getHeading(AngleUnit angleUnit){
        return imu.getRobotYawPitchRollAngles().getYaw(angleUnit); // rotation in x,y axes
    }

    }


