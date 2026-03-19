# Operation Guide — Kishi TeleOp

**OpMode name on Driver Station:** `TeleOp — Drivetrain + Shooter + Intake`

---

## Controls

### Gamepad 1 — Drive & Shooter

| Input | Action |
|-------|--------|
| Left stick Y | Drive forward / backward |
| Left stick X | Strafe left / right |
| Right stick X | Rotate left / right |
| **Left bumper** (hold) | Precision mode — 30% speed |
| **Right bumper** (hold) | Turbo mode — 100% speed |
| Neither bumper | Normal mode — 100% speed |
| **Right trigger** (> 50%) | Spin up flywheel (high goal) |
| Release right trigger | Stop flywheel |
| **A button** | Fire one ball (non-blocking) |

> Flywheel must be spun up before pressing A. The Driver Station shows **"ready: YES"** when it's up to speed.

---

### Gamepad 2 — Intake

| Input | Action |
|-------|--------|
| **Right bumper** | Run intake — pull balls in |
| **Left bumper** | Eject — push balls back out |
| Neither bumper | Intake stops |

---

## Driver Station Telemetry

| Line | What it shows |
|------|--------------|
| `Wheel Powers` | Motor power for each mecanum wheel |
| `Drive Mode` | NORMAL / PRECISION / TURBO |
| `Shooter | tgt` | Target flywheel RPM |
| `Shooter | m0 vel` | Actual motor 0 speed |
| `Shooter | m1 vel` | Actual motor 1 speed |
| `Shooter | ready` | YES = safe to fire, no = still spinning up |
| `Shooter | feedr` | IDLE / PUSHING / RETRACTING |
| `Intake | power` | Current intake motor power |

---

## Required Driver Station Config

Make sure these names match exactly in your robot config:

| Config name | What it is |
|-------------|------------|
| `wheel_0` | Front-right motor (NE) |
| `wheel_1` | Back-right motor (SE) |
| `wheel_2` | Back-left motor (SW) |
| `wheel_3` | Front-left motor (NW) |
| `shooter_motor_0` | Flywheel A |
| `shooter_motor_1` | Flywheel B |
| `feeder_servo` | Ball-push servo |
| `intake_motor` | Intake roller |

---

## What is Commented Out

The following are **disabled** on this branch but can be re-enabled by uncommenting in `HardwareMapConfig.java` and `Robot.java`:

- `turret_motor` — turret azimuth motor
- `angle_servo` — shooter angle adjustment
- `webcam` — USB vision camera
- `pinpoint` — Pinpoint odometry computer (position tracking)

To re-enable odometry, also uncomment `Odometry` in `Robot.java` and call `robot.odometry.update()` in the loop.
