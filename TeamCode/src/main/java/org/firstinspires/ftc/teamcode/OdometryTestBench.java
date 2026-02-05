package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OdometryTestBench {

    // constants - define here

    // drive base motors
    public DcMotor motorLeftFront;
    public DcMotor motorLeftBack;
    public DcMotor motorRightFront;
    public DcMotor motorRightBack;

    // odometers
    public Servo pod_x;
    public Servo pod_y;


    private HardwareMap hardwareMap;

    public void RobotHardWareEssentials(HardwareMap hwMap){
        hardwareMap = hwMap;

        //configure driver motors
        motorLeftFront = hardwareMap.dcMotor.get("motorLeftFront");
        motorLeftFront.setDirection((DcMotor.Direction.FORWARD));
        motorLeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLeftBack = hardwareMap.dcMotor.get("motorLeftBack");
        motorLeftBack.setDirection((DcMotor.Direction.FORWARD));
        motorLeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRightFront = hardwareMap.dcMotor.get("motorRightFront");
        motorRightFront.setDirection((DcMotor.Direction.REVERSE));
        motorRightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRightBack = hardwareMap.dcMotor.get("motorRightBack");
        motorRightBack.setDirection((DcMotor.Direction.REVERSE));
        motorRightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //shadow the motors with the odo encoders

        stop();
        resetDriveEncoders();


    }

    public void resetDriveEncoders(){
        motorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // constants that define the geometry of the robot:
    final static double L = 0;      // distance between encoder 1 and 2 in cm
    final static double B = 0;      // distance between midpoint of encoder 1 and 2 and encoder 3
    final static double R = 0;      // wheel radius in cm
    final static double N = 1;      // encoder ticks per revolution, REV encoder
    final static double cm_per_tick = 2.0 * Math.PI * R / N;

    // keep track of odometry encoders between updates:
    public int currentRightPosition = 0;
    public int currentLeftPosition = 0;
    public int currentAuxPosition = 0;

    private int oldRightPosition = 0;
    private int oldLeftPosition = 0;
    private int oldAuxPosition = 0;

    // XyhVector is a tuple (x,y,h) where h is the heading of the robot

    public XyhVector START_POS = new XyhVector(0,0, Math.toRadians(-174));
    public XyhVector pos = new XyhVector(START_POS);
    public void odometry() {
        oldRightPosition = currentRightPosition;
        oldLeftPosition = currentLeftPosition;
        oldAuxPosition = currentAuxPosition;

        currentRightPosition = -encoderRight.getCurrentPosition();
        currentLeftPosition = encoderLeft.getCurrentPosition();
        currentAuxPosition = encoderAux.getCurrentPosition();

        int dn1 = currentLeftPosition  - oldLeftPosition;
        int dn2 = currentRightPosition - oldRightPosition;
        int dn3 = currentAuxPosition - oldAuxPosition;

        // the robot has moved and turned a tiny bit between two measurements:
        double dtheta = cm_per_tick * ((dn2-dn1) / (LENGTH));
        double dx = cm_per_tick * ((dn1+dn2) / 2.0);
        double dy = cm_per_tick * (dn3 + ((dn2-dn1) / 2.0));

        telemetrydx = dx;
        telemetrydy = dy;
        telemetrydh = dtheta;

        // small movement of the robot gets added to the field coordinate system:
        pos.h += dtheta / 2;
        pos.x += dx * Math.cos(pos.h) - dy * Math.sin(pos.h);
        pos.y += dx * Math.sin(pos.h) + dy * Math.cos(pos.h);
        pos.h += dtheta / 2;
        pos.h = normDiff(pos.h);
    }


}
