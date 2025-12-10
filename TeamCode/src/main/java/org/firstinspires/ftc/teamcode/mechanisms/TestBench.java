package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestBench {
    private DigitalChannel touchSensor; // Give sensors more descriptive names e.g. touchSensorOnClaw, touchSensorIntake


    public void init(HardwareMap hwMap) {
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor"); // assigns our variable touchSensor with our DigitalChannel class with the name of touch_sensor
        touchSensor.setMode(DigitalChannel.Mode.INPUT); // tells the control hub that the digital device that is connected in an input
    }
    // no setter function needed as we only want to read the current state, as it is an input

    public boolean isTouchSensorPressed() {
        return !touchSensor.getState(); // will be the opposite of what we expect it to be (see video for better explanation)
    }

    public boolean isTouchSensorReleased() {
        return touchSensor.getState();
    }
}
