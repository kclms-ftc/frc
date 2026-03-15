package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

// auto aim subsytem for the turet
// uses a simple PD loop with bearing as the error
// make sure to tune kP before kD
public class Turret {

    // gains for the pd loop. kD is super sensetive so change it slowly
    private double kP = 0.013;
    private double kD = 0.00001;

    // deadzone to stop motor hunting
    private static final double TOLERANCE = 0.5;

    // max motor powr
    private static final double MAX_POWER = 0.4;

    // goal is dead ahead
    private static final double GOAL_BEARING = 0.0;

    // soft limits to stop the cables from wraping
    private static final int MIN_TICKS = -600;
    private static final int MAX_TICKS = 600;

    private final DcMotorEx motor;

    private double lastError = 0.0;
    private long   lastTimeNanos = System.nanoTime();
    private boolean locked = false;

    public Turret(HardwareMapConfig robot) {
        this.motor = robot.turret_motor;

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // pd controller tick. call this in the main loop while auto aiming
    public boolean autoAim(AprilTagDetection detection) {

        // if we lost the tag just coast and reset the derivative term
        // otherwise it will spike when we see the tag again
        if (detection == null || detection.metadata == null) {
            motor.setPower(0);
            lastError = 0;
            locked = false;
            return false;
        }

        double error = GOAL_BEARING - detection.ftcPose.bearing;

        if (Math.abs(error) < TOLERANCE) {
            motor.setPower(0);
            locked = true;
            lastError = error;
            return true;
        }

        long   nowNanos = System.nanoTime();
        double dt       = (nowNanos - lastTimeNanos) / 1_000_000_000.0;
        if (dt <= 0) dt = 0.02;

        double pTerm = error * kP;
        double dTerm = ((error - lastError) / dt) * kD;
        double power = pTerm + dTerm;

        power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        // stop the motor if we hit the safe limit
        int ticks = motor.getCurrentPosition();
        if (ticks <= MIN_TICKS && power < 0) power = 0;
        if (ticks >= MAX_TICKS && power > 0) power = 0;

        motor.setPower(power);

        lastError     = error;
        lastTimeNanos = nowNanos;
        locked        = false;

        return false;
    }

    // manual stick override for teleop
    public void manualControl(double power) {
        int ticks = motor.getCurrentPosition();
        if (ticks <= MIN_TICKS && power < 0) power = 0;
        if (ticks >= MAX_TICKS && power > 0) power = 0;

        motor.setPower(power);
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getPositionTicks() {
        return motor.getCurrentPosition();
    }

    // live tunning from the gamepad
    public void setKP(double newKP) { this.kP = newKP; }
    public void setKD(double newKD) { this.kD = newKD; }
    public double getKP() { return kP; }
    public double getKD() { return kD; }

    public void stop() {
        motor.setPower(0);
        locked = false;
    }

    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("Turret | Locked",   locked ? "yes" : "no");
        telemetry.addData("Turret | Ticks",    motor.getCurrentPosition());
        telemetry.addData("Turret | Power",    String.format("%.3f", motor.getPower()));
        telemetry.addData("Turret | kP",       String.format("%.4f", kP));
        telemetry.addData("Turret | kD",       String.format("%.6f", kD));
        telemetry.addData("Turret | Error",    String.format("%.2f°", lastError));
    }
}
