# auto shooter guide
### vision + turret + shooter
---

## files

| file | what it does |
|---|---|
| `Vision.java` | apriltag camera pipeline and pose processing |
| `Turret.java` | pd auto-aim to point at the tag |
| `Shooter.java` | pidf flywheel control and servo feeder for the balls |
| `AutoShooter.java` | runs the auto sequence |

## architecture

```
AutoShooter
  │
  ├── vision.update()              
  ├── vision.getTagById(id)        ← gets bearing and range
  │
  ├── turret.autoAim(detection)    ← pd loop
  │
  └── shooter.spinUp()             ← pidf velocity
        shooter.isReadyToFire()    ← rpm check
        shooter.fireBallBlocking() ← servo pulse
```

## auto sequence

runs in 6 phases:

```
1 SPIN UP        — ramp motors up
2 ACQUIRE        — look for the tag (id 20)
3 AIM            — pd loop runs until steady for 300ms
4 CONFIRM RPM    — wait for flywheels to reach speed
5 FIRE           — shoot the balls. rechecks aim between shots
6 DONE           — stop motors
```

## vision

- you have to call `update()` every tick before reading pose data
- `getTagById(id)` gets the camera detection
- `enableLiveView(false)` is used in auto to save battery and cpu
- ALWAYS call `vision.stop()` at the end so the cam doesnt crash the next time you run

## turret pd loop

error = GOAL_BEARING − bearing
pTerm = error × kP
dTerm = (error − lastError) / dt × kD
power = clip(pTerm + dTerm, −MAX_POWER, +MAX_POWER)

start with these values:
kP = 0.013 (tune until it overshoots, then back off)
kD = 0.00001 (very sensetive)
TOLERANCE = 0.5 deg
MAX_POWER = 0.4
MIN_TICKS = -600 (stops the wires from breaking)
MAX_TICKS = 600

## shooter pidf

never use setPower for flywheels because when the batery dies the balls will shoot lower and lower. setVelocity uses pidf to keep the rpm exactly the same.

preset speeds:
1800 for high goal
1350 for low goal/powershots

tune `PIDF_F` first! set pid to 0 and raise F until the wheels spin at the target speed.
FEEDER_HOLD_MS is 180ms - raise this if the balls are jamming in the barrel.

in teleop use `fireBallNonBlocking()` so the loop doesnt hang.

## teleop integration

```java
robot.vision.update();
AprilTagDetection target = robot.vision.getTagById(20);

if (autoAimActive) {
    robot.turret.autoAim(target);
} else {
    robot.turret.manualControl(-gamepad2.right_stick_x);
}

robot.shooter.updateFeeder();
if (gamepad2.left_bumper)  robot.shooter.toggleSpin();
if (gamepad2.right_bumper && robot.shooter.isReadyToFire()) {
    robot.shooter.fireBallNonBlocking();
}
```

## checklist before competitions

- webcam name matches driver hub
- turret_motor, feeder_servo, etc are spelled right
- target tag id is updated for the new season
- flywheel directions are right (one forward one reverse)
- test the feeder to make sure the balls clear the wheels before it retracts