# Robot Tuning & Adjustments Guide
> **testp2 codebase — DECODE 2025-2026**  
> Written: 2026-03-24  
> Everything in this file needs a real value from physical testing.  
> Items marked 🔴 are **blockers** — the robot will behave wrong without them.  
> Items marked 🟡 are **important** but won't stop basic function.  
> Items marked 🟢 are nice-to-have refinements.

---

## How to Use This File

1. Work through each section in order — earlier sections affect later ones.
2. After changing a constant, rebuild and re-deploy to the Control Hub.
3. Write your measured value in the **"Set to"** column below as you go.
4. Date your entries so you know which session each value came from.

---

## 1. Odometry Pod Offsets 🔴

**File:** `HardwareMapConfig.java` — line 72  
**Constant:** `pinpoint.setOffsets(xOffset, yOffset, DistanceUnit.MM)`  
**Current values:** `(-45, -126)` mm — **these are placeholders**

These are the physical distances from the **centre of rotation of the robot** to each dead-wheel pod. Getting them wrong makes every odometry coordinate wrong, which breaks field-centric drive, the angle-to-goal calculation, and any future autonomous path.

### How to measure

1. Place the robot on the field pointing forward (0°).
2. Measure (with a ruler or calipers) the distance from the robot's turning centre to each pod axle:
   - **X offset** = distance of the **forward/backward pod** from centre of rotation, positive if pod is to the right of centre.
   - **Y offset** = distance of the **left/right pod** from centre of rotation, positive if pod is in front of centre.
3. Enter values in mm with the correct sign.

### Verification test

After setting offsets:
1. Drive the robot in a straight line forward ~1 metre. Read X on Driver Station.
   - If Y reads non-zero drift → X offset is slightly wrong.
2. Rotate the robot 360° in place. Position should return to ~(0, 0, 0°) with < 5 mm error.
   - If X or Y drifts during spin → offsets are wrong.

| Value | Current | Set to | Date |
|-------|---------|--------|------|
| X offset (mm) | `-45` | | |
| Y offset (mm) | `-126` | | |

---

## 2. Odometry Encoder Directions 🔴

**File:** `HardwareMapConfig.java` — line 75  
**Constant:** `pinpoint.setEncoderDirections(xDir, yDir)`  
**Current values:** `FORWARD, FORWARD`

If either pod encoder counts backwards (driving forward gives you a negative Y, or strafing right gives a negative X), flip that pod's direction to `REVERSE`.

### Verification test

1. Drive forward 500 mm — Y should increase by ~500.
2. Strafe right 500 mm — X should increase by ~500.
3. Rotate CW — heading should decrease (FTC standard: CCW positive).

| Value | Current | Correct? | Set to |
|-------|---------|----------|--------|
| X encoder direction | `FORWARD` | | |
| Y encoder direction | `FORWARD` | | |

---

## 3. Turret Gear Ratio 🔴

**File:** `Turret.java` — line 46  
**Constant:** `TURRET_GEAR_RATIO = 3.0`  
**Current value:** `3.0` — **placeholder, not measured**

This is the number of **motor shaft revolutions per one full turret revolution**.  
Example: if you have a 5:1 toothed belt between motor and turret, set this to `5.0`.

If this is wrong, the ±480° software limit will trigger at the wrong point — either cutting you off too early or letting the wires snap.

### How to measure

1. Mark the turret at its centre "zero" position.
2. Mark the motor shaft with tape.
3. Count how many full motor turns it takes to rotate the turret one full revolution.

| Value | Current | Measured | Date |
|-------|---------|----------|------|
| `TURRET_GEAR_RATIO` | `3.0` | | |

---

## 4. Turret Motor Ticks Per Revolution 🔴

**File:** `Turret.java` — line 40  
**Constant:** `MOTOR_TICKS_PER_REV = 1993.6`

`1993.6` is the value for a **goBILDA 5202 Yellow Jacket 60 RPM** motor.  
If the turret uses a different motor, look up its encoder CPR in the datasheet and change this.

Common values:
| Motor | Ticks/rev |
|-------|-----------|
| goBILDA YJ 30 RPM | 5281.1 |
| goBILDA YJ 60 RPM | 1993.6 |
| goBILDA YJ 84 RPM | 1425.1 |
| goBILDA YJ 312 RPM | 537.7 |
| REV HD Hex 40:1 | 2240 |
| REV Core Hex | 288 |
| NeveRest 40 | 1120 |

| Value | Current | Correct for your motor |
|-------|---------|------------------------|
| `MOTOR_TICKS_PER_REV` | `1993.6` | |

---

## 5. Goal Position Coordinates 🔴

**File:** `TeleOpMain.java` — lines 60–63  
**Constants:** `GOAL_X_MM_BLUE`, `GOAL_Y_MM_BLUE`, `GOAL_X_MM_RED`, `GOAL_Y_MM_RED`  
**Current values:** Blue `(-100, 1900)`, Red `(100, 1900)` — **rough guesses**

These tell the HUD where the GOAL is in field space so it can show the driver what angle to rotate to. **Origin = wherever the robot is sitting at INIT** (Pinpoint zeroed at startup).

### How to measure (easiest way)

1. Drive the robot to the exact position you'll start from at a match (on the LAUNCH LINE, touching the GOAL or perimeter).
2. Zero the odometry there (it auto-zeros at INIT).
3. Manually drive to stand directly in front of the GOAL AprilTag.
4. Read X and Y from the telemetry — those are your goal coordinates.

Alternatively, use the AprilTag `range` and `bearing` values while positioned at your known start to calculate the offset geometrically.

| Value | Current | Measured | Date |
|-------|---------|----------|------|
| `GOAL_X_MM_BLUE` | `-100.0` | | |
| `GOAL_Y_MM_BLUE` | `1900.0` | | |
| `GOAL_X_MM_RED` | `100.0` | | |
| `GOAL_Y_MM_RED` | `1900.0` | | |

---

## 6. Launch Zone Boundaries 🟡

**File:** `TeleOpMain.java` — lines 68–71  
**Constants:** `LAUNCH_ZONE_X_MIN/MAX`, `LAUNCH_ZONE_Y_MIN/MAX`  
**Current values:** Full-width, 3 tiles deep from GOAL wall — approximate

These define when the shooter is allowed to fire (G416). If they're too tight, you'll be in the zone physically but the code won't fire. If they're too loose, you risk a foul.

The GOAL-side LAUNCH ZONE is 6 tiles × 3 tiles on your alliance side. One tile = 609.6 mm (24 in).

### Expected values (blue alliance, starting position = field origin)

| Boundary | Approximate value (mm) | Measured |
|----------|------------------------|----------|
| `LAUNCH_ZONE_X_MIN` | `0.0` (left wall) | |
| `LAUNCH_ZONE_X_MAX` | `3657.6` (right wall) | |
| `LAUNCH_ZONE_Y_MIN` | `0.0` (GOAL wall) | |
| `LAUNCH_ZONE_Y_MAX` | `1828.8` (3 tiles from GOAL) | |

> For red alliance, the X axis is mirrored. Adjust accordingly in the code.

---

## 7. Shooter Flywheel Velocities 🟡

**File:** `TeleOpMain.java` — lines 51–52  
**Constants:** `SHOOTER_SPEED_FAST`, `SHOOTER_SPEED_SLOW`  
**Current values:** Fast = `1800`, Slow = `1200` ticks/sec

Also in `Shooter.java` line 33:  
**`TARGET_VELOCITY = 1800`** — used by `spinUp()` (not called from TeleOpMain currently, but kept as a default reference)

### How to tune

1. Spin up to a speed and fire a ball. Watch the trajectory.
2. If ball falls short → increase speed.
3. If ball overshoots → decrease speed.
4. Tune FAST for your maximum useful shot distance, SLOW for close-range precision.

| Constant | Current | Tuned |
|----------|---------|-------|
| `SHOOTER_SPEED_FAST` | `1800` ticks/s | |
| `SHOOTER_SPEED_SLOW` | `1200` ticks/s | |

---

## 8. Shooter PIDF Coefficients 🟡

**File:** `Shooter.java` — lines 40–43  
**Constants:** `PIDF_P`, `PIDF_I`, `PIDF_D`, `PIDF_F`  
**Current values:** P=35, I=0, D=0, F=13.7

These control how quickly and accurately the flywheel velocity PID holds its target speed under load (when it bites into a ball).

### How to tune

1. Set `SHOOTER_SPEED_FAST` and spin up.
2. Watch telemetry: `Shooter | m0 vel` and `m1 vel` should reach target and stay there.
3. If motors oscillate/hunt: reduce P.
4. If motors are too slow to react when ball hits: increase P or F.
5. F ≈ `(max_power / max_velocity) × 32767`. For goBILDA motors at 12V: F ≈ 13–14 is a reasonable start.
6. I and D can usually stay at 0 for flywheels.

| Constant | Current | Tuned |
|----------|---------|-------|
| `PIDF_P` | `35` | |
| `PIDF_I` | `0` | |
| `PIDF_D` | `0` | |
| `PIDF_F` | `13.7` | |
| `READY_TOLERANCE` | `60` ticks/s | |

**`READY_TOLERANCE`** — how close to target before firing is allowed. 60 is conservative. Lower it if the HUD shows "ready" taking too long; raise it if you're firing while still ramping up.

---

## 9. Feeder Servo Positions 🟡

**File:** `Shooter.java` — lines 46–47  
**Constants:** `FEEDER_RETRACTED`, `FEEDER_EXTENDED`  
**Current values:** Retracted = `0.0`, Extended = `0.5`

Servo positions are in the range [0.0, 1.0]. If the feeder doesn't push the ball fully into the flywheels, increase `FEEDER_EXTENDED`. If it over-extends and jams, reduce it.

| Constant | Current | Tuned |
|----------|---------|-------|
| `FEEDER_RETRACTED` | `0.0` | |
| `FEEDER_EXTENDED` | `0.5` | |
| `FEEDER_PUSH_MS` | `200` ms | |

**`FEEDER_PUSH_MS`** — how long the servo stays extended before retracting. Too short and the ball doesn't get a full push; too long and the mechanism jams the next ball.

---

## 10. Drive Speed Modes 🟢

**File:** `Drivetrain.java` — lines 45–47  
**Constants:** `NORMAL_SPEED`, `PRECISION_SPEED`, `TURBO_SPEED`  
**Current values:** Normal = `0.65`, Precision = `0.30`, Turbo = `1.00`

GG feedback: needs slower precise movements. Reduce `PRECISION_SPEED` further if 30% is still too fast for lining up shots.

| Mode | Current | Tuned |
|------|---------|-------|
| `NORMAL_SPEED` | `0.65` (65%) | |
| `PRECISION_SPEED` | `0.30` (30%) | |
| `TURBO_SPEED` | `1.00` (100%) | |

---

## 11. Joystick Deadzone 🟢

**File:** `TeleOpMain.java` — line 45 / `Drivetrain.java` — line 252  
**Constant:** `JOYSTICK_DEADZONE = 0.05`

If the robot creeps when joysticks are released, increase this. If inputs feel sluggish to respond, decrease it.

| Constant | Current | Tuned |
|----------|---------|-------|
| `JOYSTICK_DEADZONE` | `0.05` | |

---

## 12. Camera Exposure 🟢

**File:** `Vision.java` — line 27  
**Constant:** `MANUAL_EXPOSURE_MS = 6`

Competition fields are brightly lit. If AprilTags look washed out (overexposed) or the camera can't detect them reliably, adjust exposure. Lower = darker, faster shutter. 6 ms is aggressive — try 8–12 ms if detection is unreliable.

| Constant | Current | Tuned |
|----------|---------|-------|
| `MANUAL_EXPOSURE_MS` | `6` ms | |

---

## 13. BASE Return Rumble Trigger 🟢

**File:** `TeleOpMain.java` — line 48  
**Constant:** `BASE_RETURN_TRIGGER_SEC = 95.0`

The controller rumbles at 95 seconds (T-25) to tell the driver to head to BASE. Adjust if you want more or less time to navigate back.

| Constant | Current | Tuned |
|----------|---------|-------|
| `BASE_RETURN_TRIGGER_SEC` | `95.0 s` | |

---

## 14. Turret Rotation Power 🟢

**File:** `Turret.java` — line 35  
**Constant:** `ROTATE_POWER = 0.50`

50% is a moderate speed. Increase if the turret is too slow to track; decrease if it overshoots and oscillates.

| Constant | Current | Tuned |
|----------|---------|-------|
| `ROTATE_POWER` | `0.50` | |

---

## Quick-Reference: All Constants at a Glance

| Priority | Constant | File | Current Value | Notes |
|----------|----------|------|---------------|-------|
| 🔴 | `setOffsets(x, y, MM)` | HardwareMapConfig | `(-45, -126)` | Pod mounting positions — MUST measure |
| 🔴 | Encoder directions (X, Y) | HardwareMapConfig | `FWD, FWD` | Flip if position reads backwards |
| 🔴 | `TURRET_GEAR_RATIO` | Turret | `3.0` | Measure belt/gear reduction |
| 🔴 | `MOTOR_TICKS_PER_REV` | Turret | `1993.6` | Check against your motor datasheet |
| 🔴 | `GOAL_X_MM_BLUE/RED` | TeleOpMain | `-100 / 100` | Measure on real field |
| 🔴 | `GOAL_Y_MM_BLUE/RED` | TeleOpMain | `1900 / 1900` | Measure on real field |
| 🟡 | `LAUNCH_ZONE_*` | TeleOpMain | Approximate | Verify with odometry on field |
| 🟡 | `SHOOTER_SPEED_FAST` | TeleOpMain | `1800` ticks/s | Tune by shooting |
| 🟡 | `SHOOTER_SPEED_SLOW` | TeleOpMain | `1200` ticks/s | Tune by shooting |
| 🟡 | `PIDF_P/F` | Shooter | `35 / 13.7` | Tune if velocity hunts |
| 🟡 | `FEEDER_EXTENDED` | Shooter | `0.5` | Tune if ball doesn't feed |
| 🟡 | `FEEDER_PUSH_MS` | Shooter | `200 ms` | Tune if feeder jams |
| 🟡 | `READY_TOLERANCE` | Shooter | `60` ticks/s | Lower if "ready" takes too long |
| 🟢 | `NORMAL_SPEED` | Drivetrain | `0.65` | GG: may want lower |
| 🟢 | `PRECISION_SPEED` | Drivetrain | `0.30` | GG: lower if still too fast |
| 🟢 | `MANUAL_EXPOSURE_MS` | Vision | `6 ms` | Raise if tags not detected |
| 🟢 | `ROTATE_POWER` | Turret | `0.50` | Adjust to taste |
| 🟢 | `BASE_RETURN_TRIGGER_SEC` | TeleOpMain | `95.0 s` | Adjust lead time |
| 🟢 | `JOYSTICK_DEADZONE` | TeleOpMain | `0.05` | Adjust if creep/lag |
