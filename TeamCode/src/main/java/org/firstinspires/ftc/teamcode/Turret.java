package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Turret — subsystem that rotates the shooter turret.
 *
 * SOFT LIMITS (wire protection):
 * The turret wires snake down the rotation axis. Rotating more than ±480°
 * total from the zero position risks snapping them. This class tracks the
 * motor encoder and blocks movement once the limit is hit.
 *
 * HOW THE LIMIT WORKS:
 *   - On construction the encoder is zeroed at "centre" (where the robot was
 *     built/parked). That zero position is the datum.
 *   - rotateLeft / rotateRight check the current tick count before applying
 *     power. If we're already at the travel limit in that direction, power is
 *     set to zero instead (soft brick — the limit just stops rotation silently).
 *   - Call resetToCenter() if you physically re-centre the turret.
 *
 * TUNING:
 *   MOTOR_TICKS_PER_REV — change to match your motor's encoder resolution.
 *   TURRET_GEAR_RATIO   — if the turret has a gearbox reduction, set it here
 *                         (motor_revolutions per 1 turret revolution).
 *   The soft limit is ±480° of TURRET rotation = ±480/360 × ratio × ticks/rev
 *   motor shaft ticks.
 */
public class Turret {

    // ── POWER ──────────────────────────────────────────────────────────────
    private static final double ROTATE_POWER = 0.50;

    // ── ENCODER / LIMIT CONSTANTS ──────────────────────────────────────────
    // goBILDA 5202 Yellow Jacket 60 RPM = 1993.6 ticks/rev at gearbox output.
    // Change this if you're using a different motor.
    private static final double MOTOR_TICKS_PER_REV = 1993.6;

    // How many motor shaft revolutions does it take for the turret to spin once?
    // e.g. 5.0 means a 5:1 belt/gear reduction between motor and turret plate.
    // Set to 1.0 if motor drives the turret directly.
    // TODO: measure your actual turret gear ratio and update this value.
    private static final double TURRET_GEAR_RATIO = 3.0; // tunable

    // Max total turret travel: ±480° from centre. 480° = 480/360 = 1.333 turret revs.
    // In motor encoder ticks: 1.333 × TURRET_GEAR_RATIO × MOTOR_TICKS_PER_REV
    private static final int SOFT_LIMIT_TICKS = (int)(
            (480.0 / 360.0) * TURRET_GEAR_RATIO * MOTOR_TICKS_PER_REV
    );

    // ── HARDWARE ───────────────────────────────────────────────────────────
    private final DcMotorEx motor;

    // ── CONSTRUCTOR ────────────────────────────────────────────────────────

    public Turret(HardwareMapConfig robot) {
        motor = robot.turret_motor;

        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        // Use RUN_USING_ENCODER so we can read the encoder position for limits.
        // Power is still set manually — no velocity PID, just raw power with limit checks.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // zero at "centre"
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // raw power, but we still read encoder

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // ── PUBLIC API ─────────────────────────────────────────────────────────

    /**
     * Rotate left (counter-clockwise). Blocked if already at the left soft limit.
     * Call every loop while the driver holds the button.
     */
    public void rotateLeft() {
        if (motor.getCurrentPosition() <= -SOFT_LIMIT_TICKS) {
            motor.setPower(0); // hit left limit — stop silently
        } else {
            motor.setPower(-ROTATE_POWER);
        }
    }

    /**
     * Rotate right (clockwise). Blocked if already at the right soft limit.
     * Call every loop while the driver holds the button.
     */
    public void rotateRight() {
        if (motor.getCurrentPosition() >= SOFT_LIMIT_TICKS) {
            motor.setPower(0); // hit right limit — stop silently
        } else {
            motor.setPower(ROTATE_POWER);
        }
    }

    /** Stop rotation immediately. */
    public void stop() {
        motor.setPower(0);
    }

    /**
     * Zero the encoder at the current physical position.
     * Call this if you manually re-centre the turret between matches.
     * Don't call mid-match or the limits will be wrong.
     */
    public void resetToCenter() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /** Current turret encoder position in ticks (0 = centre). */
    public int getPositionTicks() {
        return motor.getCurrentPosition();
    }

    /** Current turret angle in degrees from centre. Positive = CW. */
    public double getPositionDegrees() {
        return (motor.getCurrentPosition() / (TURRET_GEAR_RATIO * MOTOR_TICKS_PER_REV)) * 360.0;
    }

    // ── TELEMETRY ──────────────────────────────────────────────────────────

    public void displayTelemetry(Telemetry t) {
        int ticks = motor.getCurrentPosition();
        double deg = getPositionDegrees();
        boolean atLeft  = ticks <= -SOFT_LIMIT_TICKS;
        boolean atRight = ticks >=  SOFT_LIMIT_TICKS;

        t.addLine("--- Turret ---");
        t.addData("Turret | power",      String.format("%.2f", motor.getPower()));
        t.addData("Turret | pos ticks",  ticks);
        t.addData("Turret | pos deg",    String.format("%.1f°", deg));
        t.addData("Turret | limit",      atLeft ? "LEFT MAX" : atRight ? "RIGHT MAX" : "OK");
        t.addData("Turret | limit ticks", "±" + SOFT_LIMIT_TICKS);
    }
}
