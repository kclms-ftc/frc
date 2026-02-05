package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.mechanisms.TestBenchIMU;

@TeleOp
//@Disabled

public class FieldCentricDrive extends OpMode {
    TestBenchIMU bench = new TestBenchIMU();
    private DcMotor BLeft; // rename these once wheel motors identified
    private DcMotor BRight;
    private DcMotor FLeft;
    private DcMotor FRight;

    //@Override
    public void init() {
        bench.init(hardwareMap);
        bench.resetPosition(); // Set X and Y to 0

        //motors
        BLeft = hardwareMap.get(DcMotor.class, "backleft");
        BRight = hardwareMap.get(DcMotor.class, "backright");
        FLeft = hardwareMap.get(DcMotor.class, "frontleft");
        FRight = hardwareMap.get(DcMotor.class, "frontright");

        // reverse motor directions
        BLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        FLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void moveRobot(){
        //input: the steering wheel
        double forward = -gamepad1.left_stick_y; // inverted Y-axis
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        double heading = bench.getHeading(AngleUnit.RADIANS);
        double posX = bench.getPosX();
        double posY = bench.getPosY();

        double cosAngle = Math.cos((Math.PI / 2) - heading);
        double sinAngle = Math.sin((Math.PI / 2) - heading);

        double globalStrafe = -forward * sinAngle + strafe * cosAngle;
        double globalForward = forward * cosAngle + strafe * sinAngle;

        double[] newWheelSpeeds = new double[4];

        newWheelSpeeds[0] = globalForward + globalStrafe + rotate; //FLeft
        newWheelSpeeds[1] = globalForward - globalStrafe - rotate; //FRight
        newWheelSpeeds[2] = globalForward - globalStrafe + rotate; //BLeft
        newWheelSpeeds[3] = globalForward + globalStrafe - rotate; //BRight

        FLeft.setPower(newWheelSpeeds[0]);
        FRight.setPower(newWheelSpeeds[1]);
        BLeft.setPower(newWheelSpeeds[2]);
        BRight.setPower(newWheelSpeeds[3]);

        telemetry.addData("Robot XPos: ", posX); // in millimetres
        telemetry.addData("Robot YPos: ", posY);
        telemetry.addData("Robot Heading: ", heading);
        telemetry.addData("Forward speed: ", globalForward);
        telemetry.addData("Strafe Speed: ", globalStrafe);

    }

    @Override
    public void loop() {
        moveRobot();
        bench.updatePosition();


        // Telemetry
        telemetry.addData("Robot X: ", bench.getPosX());
        telemetry.addData("Robot Y: ", bench.getPosY());
        telemetry.addData("Robot Heading: ", bench.getHeading(AngleUnit.DEGREES));
        telemetry.update();



    }

}

