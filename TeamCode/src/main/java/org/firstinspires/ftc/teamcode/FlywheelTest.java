package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/*
 * This OpMode demonstrates a Flywheel test setup.
 * Use L2 (Left Trigger) to spin the motor to a specific velocity.
 * It uses BRAKE mode for instant stops and RUN_USING_ENCODER for constant speed.
 */

@TeleOp(name="Flywheel Pro Diagnostic")
public class FlywheelTest extends LinearOpMode {

    // Declare the motor as DcMotorEx to access setVelocity()
    private DcMotorEx flywheelMotor;

    // TARGET VELOCITY: Adjust this based on your motor's max (usually 2000-2800)
    // Unit: Ticks per second
    double targetVelocity = 1800; 

    @Override
    public void runOpMode() {
        // 1. HARDWARE MAPPING
        // Get the motor from the hardwareMap using the name from your configuration
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "test_motor");

        // 2. MOTOR SETUP
        // RUN_USING_ENCODER uses the Hub's internal logic to maintain constant speed
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        // BRAKE mode ensures the motor stops as fast as possible when power is 0
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 3. INITIALIZATION TELEMETRY
        telemetry.addData("Status", "Initialized & Configured");
        telemetry.addData("Target Set To", targetVelocity + " ticks/s");
        telemetry.update();

        // Wait for the driver to press the PLAY button
        waitForStart();

        // 4. MAIN LOOP (Runs until you press STOP)
        while (opModeIsActive()) {
            
            // Check the Left Trigger (L2) pressure (0.0 to 1.0)
            double triggerPressure = gamepad1.left_trigger;

            if (triggerPressure > 0.1) {
                // If L2 is held, set motor to exact velocity
                flywheelMotor.setVelocity(targetVelocity);
            } else {
                // If L2 is released, set velocity to 0 (Brake will engage)
                flywheelMotor.setVelocity(0);
            }

            // 5. DATA LOGGING (View this on your Driver Station screen)
            double actualVelocity = flywheelMotor.getVelocity();
            double velocityError = targetVelocity - actualVelocity;

            telemetry.addLine("--- FLYWHEEL DIAGNOSTICS ---");
            telemetry.addData("Motor State", triggerPressure > 0.1 ? "SPINNING" : "STOPPED");
            telemetry.addData("Target Velocity", "%.2f ticks/s", targetVelocity);
            telemetry.addData("Actual Velocity", "%.2f ticks/s", actualVelocity);
            
            // Error shows how much the motor is lagging behind the target
            telemetry.addData("Velocity Error", "%.2f ticks/s", velocityError);
            
            // Power shows how hard the Hub is working (0.0 to 1.0) to hit that speed
            telemetry.addData("Motor Power Usage", "%.2f%%", flywheelMotor.getPower() * 100);
            
            telemetry.addLine("--- SYSTEM INFO ---");
            // Voltage is critical for flywheels; low voltage means lower max speed
            double voltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
            telemetry.addData("Battery Voltage", "%.2f V", voltage);
            telemetry.addData("Trigger Pressure", "%.2f", triggerPressure);

            // Update the screen at the end of every loop
            telemetry.update();
        }

        // 6. SAFETY SHUTDOWN
        // Ensure the motor is off when the program stops
        flywheelMotor.setPower(0);
    }
}
