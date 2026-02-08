package org.firstinspires.ftc.teamcode;

/*
- this uses outputs from vision.java

- motor connected to belt drives rotation of turret ("turret_motor")
- servo controls hood angle ("angle_servo")
    - this has two possible angles (for each launch zone) toggled by left_bumper
    - range 0.0 to 1.0

- mechanical limits to ensure turret does not rotate too far (into wires)

- if vision lost at any point it returns to front (0°)
    - +deg is to the robot's right, -deg is to the robot's left
*/

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.function.Supplier;

public class Turret {

    // TODO: adjust based on hardware map
    // hardware names
    private final String MOTOR_NAME = "turret_motor";
    private final String SERVO_NAME  = "angle_servo";

    // TODO: adjust these after testing
    // turret rotation limits
    private final double MIN_ANGLE_DEG = -110;
    private final double MAX_ANGLE_DEG = 110;
    private final double FRONT_ANGLE_DEG = 0;

    // TODO: adjust P value
    // PIDF motor tuning (only using P-proportionality)
    private final double P = 0.012;

    // TODO: adjust and increase after tests
    // max power to protect belt
    private final double MAX_POWER = 0.60;

    // TODO: increase if turret lags, decrease if turret twitches
    // smooths vision angle value so movement is not jittery
    private final double ANGLE_TREND = 0.25;

    // TODO: adjust to get turret to ignore outliers and errors
    // angle difference which allows a frame to be ignored
    private final double OUTLIER_DEG = 20;

    // TODO: decrease if harsh movement, increase if too slow for large jumps
    // maximum rate at which turret moves
    private final double MAX_DEG_PER_SEC = 180;

    // TODO: increase if resets too quickly, decrease if resets too slowly
    // time after which no valid detections resets turret position
    private final long NO_VALID_DETECTIONS_MS = 250;

    // TODO: set servo angles based on the 2 launch regions
    // servo preset positions
    private final double HOOD_POS_1 = 0.40; // flatter arc
    private final double HOOD_POS_2 = 0.60; // higher arc

    // hardware
    private final DcMotorEx turret;
    private final Servo hood;

    // TODO: calibrate and find value for the motor
    // use for conversion between ticks and degrees
    private final double TICKS_PER_DEG = 10;

    // variables that check status
    private Supplier<Boolean> safeToMove = () -> true; // supplier for live decision
    private double currentAngleDeg = FRONT_ANGLE_DEG; // current loop's angle
    private double lastAngleDeg = FRONT_ANGLE_DEG; // previous loop's angle
    private double filteredDeg = 0; // adjusted angle for smoothing
    private double lastValidVisionMS = 0; // timestamp of last valid detection
    private boolean hoodPos1 = true; // else hoodPos2 active

    // inputs from vision
    private boolean visionVisible = false;
    private double visionErrorDeg = 0; // +deg -> target to the right
    private double visionTimeMs = 0.0; // timestamp

    // stopwatch
    private final ElapsedTime loopTimer = new ElapsedTime();

    // constructor
    public Turret(HardwareMap hw) {
        // locate hardware
        this.turret = hw.get(DcMotorEx.class,MOTOR_NAME);
        this.hood = hw.get(Servo.class,SERVO_NAME);

        // set up motor and encoder
        this.turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // holds position (FLOAT would drift)
        this.turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // start hood in position 1
        this.hood.setPosition(HOOD_POS_1);
        hoodPos1 = true;

        // start stopwatch
        loopTimer.reset();
    }
}
