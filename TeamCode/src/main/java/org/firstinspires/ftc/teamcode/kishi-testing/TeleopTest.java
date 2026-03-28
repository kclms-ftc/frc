//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
////@TeleOp(name = "test teleop")
//public class TeleopTest extends OpMode {
//
//    // hardware + subsystems
//    private HardwareMapConfig hw;
//    private Drivetrain drivetrain;
//    private Intake intake;
//    private Shooter shooter;
//    // getting rid of turret...2
//
//    @Override
//    public void init() {
//        // initialisations
//        hw = new HardwareMapConfig((hardwareMap));
//        drivetrain = new Drivetrain(hw);
//        intake = new Intake(hw);
//        shooter = new Shooter(hw);
//        // getting rid of turret...2
//
//        telemetry.addData("Status", "Init completed");
//        telemetry.update();
//    }
//
//    @Override
//    public void loop() {
//        // main loops for all subsystems
//        intake.testLoop(gamepad1, telemetry);
//        // update telemetry for all subsystems
//        drivetrain.updateTelemetry(telemetry);
//        intake.updateTelemetry(telemetry);
//        shooter.updateTelemetry(telemetry);
////        drivetrain.updateTelemetry(telemetry);
//        telemetry.addData("a", gamepad1.a);
//
//
//        addMotorTelemetry("intake", hw.intake_motor);
//        addMotorTelemetry("intake", hw.shooter_motor_1);
//        addMotorTelemetry("intake", hw.shooter_motor_0);
//        telemetry.update();
//
//    }
//
//    @Override
//    public void stop() {
//        // stop all subsystems immediately
//        drivetrain.stop();
//        intake.stop();
//        shooter.stop();
//        // getting rid of turret...2
//    }
//
//    private void addMotorTelemetry(String name, DcMotorEx m) {
//        if (m == null) {
//            telemetry.addData(name, "NULL (not mapped!)");
//            return;
//        }
//
//        telemetry.addLine("MOTOR: " + name);
//        telemetry.addData("Power", m.getPower());
//        telemetry.addData("Velocity", m.getVelocity());
//        telemetry.addData("Position", m.getCurrentPosition());        telemetry.addData("Mode", m.getMode());
//        telemetry.addData("ZeroPower", m.getZeroPowerBehavior());
//        telemetry.addData("Direction", m.getDirection());
//    }
//}