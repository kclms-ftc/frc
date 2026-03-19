# Robot.java — What It Does and Why It Exists

> **File:** `kishi/Robot.java`  
> **Role:** Central subsystem container. The single object every OpMode creates and talks to.

---

## The Core Idea

Without `Robot.java`, every OpMode would need to:
1. Create a `HardwareMapConfig` itself
2. Create a `Drivetrain`, `Shooter`, and `Odometry` itself
3. Know the constructor signatures of every subsystem
4. Repeat all of that for every OpMode you write

Instead, every OpMode just writes one line:

```java
Robot robot = new Robot(hardwareMap);
```

And everything comes up — hardware mapped, subsystems initialized, ready to use.

---

## What Robot.java Actually Contains

```
Robot.java
├── HardwareMapConfig config          ← created once, passed to everyone
├── public Drivetrain drivetrain      ← 4-wheel mecanum drive
├── public Shooter    shooter         ← flywheel + feeder servo
├── public Odometry   odometry        ← Pinpoint dead-wheel localization
├── stopAll()                         ← emergency stop all active subsystems
└── displayTelemetry(telemetry)       ← dumps all subsystem data to Driver Station
```

The three subsystem fields are **`public final`** — OpModes can read them directly (`robot.drivetrain.drive(...)`) but can't reassign them.

---

## Construction Flow (Step by Step)

When `new Robot(hardwareMap)` is called:

```
1. Robot(HardwareMap hw) called by OpMode
       ↓
2. Calls Robot(hw, true)  [convenience overload]
       ↓
3. new HardwareMapConfig(hw)
       → calls hw.get() for every motor, servo, webcam, Pinpoint
       → ONLY place hw.get() ever runs in the kishi package
       ↓
4. new Drivetrain(config)
       → sets wheel directions (NE/SE FORWARD, SW/NW REVERSE)
       → sets BRAKE zero-power behavior
       → sets RUN_WITHOUT_ENCODER mode
       → defaults to NORMAL speed
       ↓
5. new Shooter(config)
       → sets flywheel directions (FORWARD + REVERSE opposing pair)
       → sets RUN_USING_ENCODER + PIDF coefficients for velocity control
       → retracts feeder servo to position 0.0
       → resets shot cooldown timer
       ↓
6. new Odometry(config)
       → receives pre-initialized Pinpoint reference
       → Pinpoint already zeroed + IMU calibrated in HardwareMapConfig
       → ready to call update() + read position immediately
       ↓
7. Robot constructor returns — all subsystems live
```

Total time on hardware: typically under 200ms (dominated by Pinpoint IMU calibration).

---

## The Two Constructors

```java
// Full constructor — for future expansion (e.g. camera live-view toggle)
public Robot(HardwareMap hw, boolean enableLiveView) { ... }

// Convenience constructor — what you use in practice
public Robot(HardwareMap hw) {
    this(hw, true);  // just calls the full one with defaults
}
```

You almost always call `new Robot(hardwareMap)`. The `enableLiveView` parameter is reserved for adding camera streaming later without changing every OpMode.

---

## `stopAll()` — What It Does

```java
public void stopAll() {
    drivetrain.stop();  // sets all 4 wheel powers to 0 (motors brake)
    shooter.stop();     // sets flywheel velocity to 0, retracts feeder, resets state machine
    // odometry is NOT stopped — it's read-only hardware, always passive
}
```

Call this at the end of every OpMode:
```java
@Override
public void runOpMode() {
    Robot robot = new Robot(hardwareMap);
    waitForStart();
    while (opModeIsActive()) {
        // ... your loop ...
    }
    robot.stopAll();  // ← safety: ensures nothing is spinning when OpMode ends
}
```

---

## `displayTelemetry()` — What It Shows

Calls every subsystem's own `displayTelemetry()` in one shot. On the Driver Station you'll see:

```
Wheels NE/SE/SW/NW:  0.60  0.60 -0.60 -0.60
Drive Mode:          NORMAL
Shooter | tgt:       1800
Shooter | m0 vel:    1793
Shooter | m1 vel:    1801
Shooter | ready:     YES
Shooter | feedr:     IDLE
--- Odometry (Pinpoint) ---
Status:              READY
Frequency:           1487 Hz
X:                   482.3 mm
Y:                   910.7 mm
Heading:             45.2 °
Vel X:               312.4 mm/s
Vel Y:               608.1 mm/s
AngVel:              0.0 °/s
Ticks X:             9601
Ticks Y:             18124
```

> **Important:** call `robot.odometry.update()` **before** `robot.displayTelemetry()` each loop, otherwise the position shown is one frame old.

---

## What Robot.java Does NOT Do

| What | Where it actually lives |
|------|------------------------|
| Mecanum math | `Drivetrain.java` |
| PIDF tuning | `Shooter.java` |
| Pinpoint read/calibrate | `Odometry.java` |
| Hardware name strings | `HardwareMapConfig.java` |
| Gamepad input | `TeleOpMain.java` |
| Autonomous steps | Your auto OpMode |

Robot.java is intentionally thin — it's glue, not logic.

---

## Adding a New Subsystem

When you add something (e.g., a `Turret` class):

1. Add the hardware field to `HardwareMapConfig.java`
2. Create `Turret.java` with a `Turret(HardwareMapConfig robot)` constructor
3. Add `public final Turret turret;` to `Robot.java`
4. Add `turret = new Turret(config);` in the constructor
5. Optionally add `turret.stop()` in `stopAll()` and `turret.displayTelemetry()` in `displayTelemetry()`

No OpMode needs to change — they all get the new subsystem automatically via `robot.turret`.
