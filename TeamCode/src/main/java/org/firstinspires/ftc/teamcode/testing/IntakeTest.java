package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.HardwareMapConfig;

@TeleOp(name = "Intake Test", group = "Testing")
public class IntakeTest extends LinearOpMode {

    HardwareMapConfig robot;
    IntakeController intake;

    boolean togglePressed = false;

    @Override
    public void runOpMode() {

        robot = new HardwareMapConfig(hardwareMap);
        intake = new IntakeController(robot.intake_motor);

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.dpad_up && !togglePressed) {
                if (intake.getState() == IntakeState.OFF) {
                    intake.setState(IntakeState.INTAKE);
                } else {
                    intake.setState(IntakeState.OFF);
                }
            }
            togglePressed = gamepad1.dpad_up;

            if (gamepad1.dpad_down) {
                intake.setState(IntakeState.REVERSE);
            }

            intake.update();

            telemetry.addData("State", intake.getState());
            telemetry.addData("Velocity", robot.intake_motor.getVelocity());
            telemetry.update();
        }
    }

    enum IntakeState {
        OFF,
        INTAKE,
        REVERSE
    }

    class IntakeController {

        private DcMotorEx motor;
        private IntakeState state = IntakeState.OFF;
        private ElapsedTime jamTimer = new ElapsedTime();

        // Tune these
        private static final double INTAKE_POWER = 1.0;
        private static final double REVERSE_POWER = -1.0;
        private static final double STALL_VELOCITY = 50; // ticks/sec
        private static final double JAM_REVERSE_TIME = 0.5; // seconds

        public IntakeController(DcMotorEx motor) {
            this.motor = motor;
            motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }

        public IntakeState getState() {
            return state;
        }

        public void setState(IntakeState newState) {
            if (newState != state) {
                state = newState;
                jamTimer.reset();
            }
        }

        public void update() {
            switch (state) {

                case OFF:
                    motor.setPower(0);
                    break;

                case INTAKE:
                    motor.setPower(INTAKE_POWER);

                    if (Math.abs(motor.getVelocity()) < STALL_VELOCITY) {
                        state = IntakeState.REVERSE;
                        jamTimer.reset();
                    }
                    break;

                case REVERSE:
                    motor.setPower(REVERSE_POWER);

                    if (jamTimer.seconds() > JAM_REVERSE_TIME) {
                        state = IntakeState.INTAKE;
                    }
                    break;
            }
        }
    }
}
