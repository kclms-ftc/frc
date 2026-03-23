# DECODE™ FTC 2025-2026 — AI Programming Reference
> **Audience:** This document is written for an AI system generating complete, competition-legal robot code for the FIRST Tech Challenge (FTC) 2025-2026 DECODE™ game.  
> **Source:** Competition Manual Team Update 27 (TU27), the authoritative season ruleset.  
> All capitalised terms (e.g. ARTIFACT, RAMP, MOTIF) are official game-defined vocabulary.

---

## Table of Contents
1. [Game Concept & Season Theme](#1-game-concept--season-theme)
2. [Field Architecture](#2-field-architecture)
3. [Scoring Elements (ARTIFACTS)](#3-scoring-elements-artifacts)
4. [AprilTag Reference](#4-apriltag-reference)
5. [Match Structure & Timing](#5-match-structure--timing)
6. [Scoring System — Complete Point Table](#6-scoring-system--complete-point-table)
7. [Autonomous Period (AUTO) — Programming Goals](#7-autonomous-period-auto--programming-goals)
8. [Teleoperated Period (TELEOP) — Programming Goals](#8-teleoperated-period-teleop--programming-goals)
9. [GATE Operation](#9-gate-operation)
10. [PATTERN Scoring Logic](#10-pattern-scoring-logic)
11. [BASE Return Mechanics](#11-base-return-mechanics)
12. [RANKING POINTS — Thresholds & Strategy](#12-ranking-points--thresholds--strategy)
13. [Robot Starting Configuration & Expansion Limits](#13-robot-starting-configuration--expansion-limits)
14. [Control System Hardware](#14-control-system-hardware)
15. [Motors, Servos & Actuator Limits](#15-motors-servos--actuator-limits)
16. [Power Architecture](#16-power-architecture)
17. [Legal Sensors & Vision Devices](#17-legal-sensors--vision-devices)
18. [Software Architecture & SDK Requirements](#18-software-architecture--sdk-requirements)
19. [OpMode Requirements & Match-Ready Checklist](#19-opmode-requirements--match-ready-checklist)
20. [Key Game Rules Affecting Code](#20-key-game-rules-affecting-code)
21. [Violation Reference — Penalties Code Must Avoid](#21-violation-reference--penalties-code-must-avoid)
22. [Field Zones — Coordinates & Boundaries](#22-field-zones--coordinates--boundaries)
23. [LOADING ZONE Human-Player Interaction Model](#23-loading-zone-human-player-interaction-model)
24. [Drive Train & Drivetrain Constraints](#24-drive-train--drivetrain-constraints)
25. [Qualification Ranking Logic (for strategy optimisation)](#25-qualification-ranking-logic-for-strategy-optimisation)
26. [Complete Programming Checklist](#26-complete-programming-checklist)

---

## 1. Game Concept & Season Theme

**DECODE™ presented by RTX** is the 2025-2026 FTC game. Two ALLIANCES (each 2 teams) compete in 2.5-minute MATCHES. The overarching season theme is archaeology — teams "decode" artefacts from an OBELISK that randomly reveals a **MOTIF** (colour pattern).

### Core Objective
1. **Collect ARTIFACTS** (polypropylene balls, purple and green) from pre-staged locations and the LOADING ZONE.
2. **Score ARTIFACTS into the GOAL** so they enter the RAMP as CLASSIFIED ARTIFACTS, building a **PATTERN** that matches the randomly revealed MOTIF.
3. **Operate the GATE** to clear the RAMP and continue scoring.
4. **Return ROBOTS to BASE** before time runs out.

### MOTIF System
Before every MATCH, the OBELISK is randomised to one of three MOTIFS — a colour sequence of 2 purple (P) and 1 green (G):

| AprilTag ID on OBELISK | MOTIF | Pattern on RAMP (repeated 3x) |
|------------------------|-------|-------------------------------|
| 21 | GPP | G P P G P P G P P |
| 22 | PGP | P G P P G P P G P |
| 23 | PPG | P P G P P G P P G |

> **Code implication:** Read the OBELISK AprilTag ID at the start of AUTO to determine which MOTIF is active and therefore which colour ARTIFACT belongs in each of the 9 RAMP indices.

---

## 2. Field Architecture

### Field Size
- **12 ft x 12 ft** (144 in x 144 in / 365.75 cm x 365.75 cm)
- 36 interlocking foam TILES, each 24 in x 24 in x 0.59 in

### Field Elements Per Alliance
Each ALLIANCE (red or blue) has its own set of the following:

| Element | Description |
|---------|-------------|
| **GOAL** | 3-sided structure, ~27 in x 27 in x 54 in tall. Open top for scoring. Has an archway exit where ARTIFACTS pass to the RAMP. |
| **CLASSIFIER** | Attached to GOAL. Contains: SQUARE (scoring gate at RAMP top), RAMP (holds up to 9 CLASSIFIED ARTIFACTS), GATE (alliance-operated, gravity-closed). |
| **RAMP** | Holds 9 CLASSIFIED positions. Indexed 1-9 from GATE to SQUARE. |
| **GATE** | Gravity-closed barrier at end of RAMP. Opens via ROBOT push (horizontal displacement ~2 in). GATE height contact range: 3.75-5.5 in above TILE when closed. |
| **LOADING ZONE** | ~23 in x 23 in corner zone for human player ARTIFACT delivery. Alliance-specific. |
| **BASE ZONE** | 18 in x 18 in coloured-tape zone for end-game return scoring. Alliance-specific. |
| **DEPOT** | 30 in white tape strip at the base of the GOAL front face. ARTIFACTS over DEPOT score 1 point each at TELEOP end. |

### Shared Field Elements

| Element | Description |
|---------|-------------|
| **OBELISK** | Equilateral triangular prism outside field perimeter, GOAL-side. 23 in tall, 11 in face width. Each face has an AprilTag (IDs 21, 22, 23) indicating a MOTIF. Randomised each MATCH. |
| **SPIKE MARKS** | 6 white tape marks (10 in long) on the field; 3 per alliance side. Used for pre-staged ARTIFACT placement. |
| **LAUNCH LINES** | White tape bounding 2 LAUNCH ZONES per alliance + 2 strips at GOAL base (DEPOT). |
| **LAUNCH ZONES** | Two triangular volumes per alliance. Audience-side: 2 tiles wide x 1 tile deep. GOAL-side: 6 tiles wide x 3 tiles deep. ROBOTS must be inside a LAUNCH ZONE or overlapping a LAUNCH LINE to legally LAUNCH ARTIFACTS. |
| **SECRET TUNNEL ZONE** | ~46.5 in x 6.125 in volume adjacent to GATE exit. Alliance-specific. Opponents may not contact your ROBOT while either is in your SECRET TUNNEL ZONE. |
| **GATE ZONE** | 2.75 in x 10 in strip adjacent to GATE. Protected zone — opponents may not contact your ROBOT while either is in your GATE ZONE. |

### Tile Coordinate System
- Columns A-C: Blue Alliance side (autonomous territory)
- Columns D-F: Red Alliance side (autonomous territory)
- Used for ROBOT starting position and auto navigation zones.

---

## 3. Scoring Elements (ARTIFACTS)

| Property | Value |
|----------|-------|
| Type | Gopher ResisDent polypropylene balls |
| Nominal diameter | 5 in (12.70 cm) |
| Actual diameter (at mold seam) | 4.9 in +/- 0.25 in |
| Purple (P) count per MATCH | 24 |
| Green (G) count per MATCH | 12 |
| Total ARTIFACTS per MATCH | 36 |

> **Code implication:** Vision systems must detect two colours (purple / green). ARTIFACTS are not perfectly spherical — gripper/intake design must accommodate variation.

### Pre-Match Staging

| Location | Count | Colour Order |
|----------|-------|--------------|
| Near SPIKE MARK (audience side) | 3 | GPP |
| Middle SPIKE MARK | 3 | PGP |
| Far SPIKE MARK (GOAL side) | 3 | PPG |
| Each LOADING ZONE | 3 (2P, 1G) | PGP |
| Each ALLIANCE AREA tray | 6 (4P, 2G) | No set order |
| Each ROBOT pre-load | Up to 3 | Robot decides |

---

## 4. AprilTag Reference

| ID | Location | Purpose |
|----|----------|---------|
| 20 | Blue ALLIANCE GOAL (front face) | ROBOT navigation & targeting |
| 21 | OBELISK face 1 | MOTIF = GPP |
| 22 | OBELISK face 2 | MOTIF = PGP |
| 23 | OBELISK face 3 | MOTIF = PPG |
| 24 | Red ALLIANCE GOAL (front face) | ROBOT navigation & targeting |

AprilTag family: **36h11**
Tag size (on field): **8.125 in (~20.65 cm) square**

**Critical notes for code:**
- GOAL AprilTags (IDs 20, 24) are stable — use for ROBOT targeting and alignment.
- OBELISK AprilTags (IDs 21-23) position varies per MATCH — **do not use for navigation**, only for MOTIF detection.
- The FTC SDK supports AprilTag detection natively in the vision pipeline.
- Only the OBELISK face visible from the field will be deterministic — read the ID to determine MOTIF.

---

## 5. Match Structure & Timing

| Period | Duration | Description |
|--------|----------|-------------|
| Pre-MATCH setup | Variable | ROBOTS on field, DRIVE TEAMS ready |
| **AUTO** | **30 seconds** | No driver input. ROBOTS run pre-programmed OpMode. |
| AUTO to TELEOP transition | 8 seconds | No powered ROBOT movement allowed during this window. |
| **TELEOP** | **2 minutes (120 seconds)** | Driver control. Human Players active in LOADING ZONE. |
| Post-MATCH | Variable | Scoring assessed, field reset. |

### Audio Cues

| Event | Timer Value | Cue |
|-------|-------------|-----|
| MATCH start | 2:30 | "Cavalry Charge" |
| AUTO ends | 2:00 | "Buzzer x3" |
| AUTO to TELEOP transition | 0:07-0:01 | "Drivers, pick up your controllers, 3-2-1" |
| TELEOP begins | 2:00 (of TELEOP) | "3 Bells" |
| Final 20 seconds | 0:20 | "Train Whistle" |
| MATCH end | 0:00 | "3-second Buzzer" |

> **Timer reference for code:** The MATCH timer counts down from 2:30. TELEOP begins at 2:00 remaining. Final 20 seconds (vertical expansion and BASE zone protection activate) = 0:20 remaining. ROBOTS must be motionless by 0:00.

---

## 6. Scoring System — Complete Point Table

### MATCH Points

| Achievement | AUTO | TELEOP | Notes |
|-------------|------|--------|-------|
| LEAVE (off LAUNCH LINE) | 3 | — | Per ROBOT, assessed end of AUTO |
| ARTIFACT — CLASSIFIED | 3 | 3 | Per ARTIFACT on RAMP via direct path from SQUARE |
| ARTIFACT — OVERFLOW | 1 | 1 | Per ARTIFACT, passed through SQUARE but rolled over others |
| ARTIFACT — DEPOT | — | 1 | Per ARTIFACT over DEPOT tape at TELEOP end |
| PATTERN (each matching ARTIFACT) | 2 | 2 | Colour matches MOTIF at that RAMP index |
| Partially returned to BASE | — | 5 | Per ROBOT partially in BASE ZONE |
| Fully returned to BASE | — | 10 | Per ROBOT fully supported by BASE ZONE TILE |
| BONUS — 2 ROBOTS fully in BASE | — | 10 | Additional bonus if both alliance ROBOTS fully return |

### RANKING POINTS (RPs)

| RP | Condition (Standard Events) | Condition (Regional Championships) |
|----|-----------------------------|------------------------------------|
| MOVEMENT RP | Combined LEAVE + BASE >= 16 pts | >= 21 pts |
| GOAL RP | ARTIFACTS scored through SQUARE >= 36 | >= 42 |
| PATTERN RP | PATTERN points >= 18 | >= 22 |
| WIN | More MATCH points than opponent | +3 RPs |
| TIE | Equal MATCH points | +1 RP |

> **Strategy note:** MOVEMENT RP requires 16 pts from LEAVE + BASE combined. Both ROBOTS leaving (6 pts) + both ROBOTS partially returning (10 pts) = 16 pts exactly. Both fully returned (20 pts + 10 pt bonus = 30 pts) far exceeds the threshold.

---

## 7. Autonomous Period (AUTO) — Programming Goals

### Constraints
- Duration: **30 seconds**
- **No driver input allowed.** DRIVE TEAM may press the start button at MATCH start and stop button at any time.
- ROBOTS must not move during the 8-second AUTO to TELEOP transition.
- ROBOTS must not cross into the opponent's side of the field or disrupt opponent pre-staged ARTIFACTS.

### Priority Objectives (Ranked by Impact)

#### 1. Detect the MOTIF (zero direct points, enables everything else)

```java
// AprilTag IDs on OBELISK:
// 21 => GPP,  22 => PGP,  23 => PPG

public enum Motif { GPP, PGP, PPG, UNKNOWN }

public Motif detectMotifFromObelisk() {
    List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
    for (AprilTagDetection tag : detections) {
        switch (tag.id) {
            case 21: return Motif.GPP;
            case 22: return Motif.PGP;
            case 23: return Motif.PPG;
        }
    }
    return Motif.UNKNOWN;
}
```

#### 2. LEAVE (3 pts per ROBOT)
- ROBOT must cross the LAUNCH LINE completely — no part over any LAUNCH LINE at AUTO end.

#### 3. Score CLASSIFIED ARTIFACTS (3 pts each)
- LAUNCH ARTIFACTS into the open top of the GOAL from within a LAUNCH ZONE.
- ARTIFACT must: enter through GOAL top -> exit under archway -> pass through SQUARE -> land directly on RAMP (CLASSIFIED).
- ROBOTS must be inside a LAUNCH ZONE or overlapping a LAUNCH LINE to LAUNCH legally.
- Pre-loaded ARTIFACTS (up to 3 from ALLIANCE AREA) can be scored immediately.

#### 4. Build a PATTERN (2 pts per matching ARTIFACT)
- ARTIFACTS on RAMP at AUTO end score PATTERN points if colour matches the MOTIF index.
- Index 1 = GATE end, Index 9 = SQUARE end. MOTIF repeats 3x across 9 slots.
- Only CLASSIFIED ARTIFACTS count (those retained by GATE).

### PATTERN Index Mapping

```java
// Given the active MOTIF, expected colour at each RAMP index (1-9):
// MOTIF repeats 3 times across 9 indices
// GPP sequence: [G, P, P]
// PGP sequence: [P, G, P]
// PPG sequence: [P, P, G]

public ArtifactColor expectedColorAtIndex(Motif motif, int rampIndex) {
    int positionInMotif = (rampIndex - 1) % 3;  // 0, 1, or 2
    return motif.sequence[positionInMotif];
}
```

### AUTO OpMode Skeleton

```java
@Autonomous(name = "AutoMain")
public class AutoMain extends LinearOpMode {

    @Override
    public void runOpMode() {
        initHardware();
        initVisionPipeline();  // AprilTag detector for OBELISK + GOAL

        waitForStart();

        // PHASE 1: Detect MOTIF (first ~3 seconds)
        Motif motif = detectMotifFromObelisk();
        RobotState.activeMotif = motif;  // persist for TELEOP

        // PHASE 2: LEAVE — drive off LAUNCH LINE
        driveOffLaunchLine();

        // PHASE 3: Align to own GOAL via AprilTag (ID 20 or 24)
        alignToGoalAprilTag();

        // PHASE 4: Score pre-loaded ARTIFACTS
        for (int i = 0; i < preloadCount; i++) {
            aimAndLaunchArtifact();
        }

        // PHASE 5 (if time permits): Collect SPIKE MARK ARTIFACTS and score
        collectSpikeMarkArtifacts();
        returnToLaunchZone();
        scoreRemainingArtifacts();

        // PHASE 6: Position for TELEOP
        prepareForTeleop();
    }
}
```

---

## 8. Teleoperated Period (TELEOP) — Programming Goals

### Overview
- Duration: **2 minutes**
- Human drivers control ROBOTS via gamepads.
- Human players deliver ARTIFACTS to the LOADING ZONE.
- ROBOTS operate GATE to clear RAMP and score more ARTIFACTS.
- Final 20 seconds: vertical expansion up to 38 in permitted (outside LAUNCH ZONES). BASE protection rules apply.

### Priority Objectives

#### 1. Continuous ARTIFACT Collection & Scoring (3 pts CLASSIFIED each)
- Collect ARTIFACTS from SPIKE MARKS, LOADING ZONE, and OVERFLOW from opponent's GATE exit.
- Score into own GOAL from LAUNCH ZONE. Maximum 3 ARTIFACTS in CONTROL at once.
- GOAL RP requires 36+ ARTIFACTS through SQUARE total across the MATCH.

#### 2. GATE Operation (enables continued RAMP scoring after 9 ARTIFACTS)
- ROBOT pushes GATE arm — horizontal displacement ~2 in, contact height 3.75-5.5 in when closed.
- CLASSIFIED ARTIFACTS slide out of RAMP through opponent's SECRET TUNNEL ZONE.
- Design tip: Large vertical contact panel ensures reliable GATE engagement.
- Never apply closing force to GATE (own or opponent's). Never contact the opponent's GATE.

#### 3. PATTERN Optimisation
- PATTERN scored at TELEOP END based on ARTIFACTS on RAMP at rest.
- Driver HUD should display: next required colour, current RAMP fill, PATTERN RP progress.
- PATTERN RP requires >= 18 PATTERN points.

#### 4. BASE Return (final 20 seconds)
- Target: both ROBOTS fully in BASE ZONE (10 + 10 + 10 bonus = 30 pts).
- Enable expanded vertical height (up to 38 in) for parking mechanisms.
- Begin BASE approach when timer shows approximately 0:25 remaining.

### ARTIFACT Control Limit Enforcement (G408)

```java
// ROBOT may not simultaneously CONTROL more than 3 ARTIFACTS.
// CONTROL = fully supported by ROBOT, or ROBOT actively herding in a direction.
// NOT control: bulldozing, deflecting, ARTIFACTS in the air after launch.

private int controlledArtifacts = 0;

public boolean canPickUpArtifact() {
    return controlledArtifacts < 3;
}

public void onArtifactPickedUp() {
    controlledArtifacts++;
    if (controlledArtifacts > 3) {
        disableIntake();  // prevent G408 violation
    }
}

public void onArtifactScored() {
    if (controlledArtifacts > 0) controlledArtifacts--;
}
```

### TELEOP OpMode Skeleton

```java
@TeleOp(name = "TeleopMain")
public class TeleopMain extends LinearOpMode {

    @Override
    public void runOpMode() {
        initHardware();
        Motif activeMotif = RobotState.activeMotif;  // retrieved from AUTO

        waitForStart();
        ElapsedTime teleOpTimer = new ElapsedTime();

        while (opModeIsActive()) {

            // DRIVETRAIN (field-centric recommended)
            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            driveFieldCentric(gamepad1.left_stick_y, gamepad1.left_stick_x,
                              gamepad1.right_stick_x, heading);

            // INTAKE
            if (canPickUpArtifact()) {
                controlIntake(gamepad1, gamepad2);
            }

            // LAUNCHER — only allowed inside LAUNCH ZONE
            if (isInLaunchZone()) {
                controlLauncher(gamepad2);
            }

            // GATE — only own GATE, only push (never close)
            if (gamepad2.right_bumper && isNearOwnGate()) {
                operateGate();
            }

            // ENDGAME
            double timeRemaining = 120.0 - teleOpTimer.seconds();
            if (timeRemaining <= 25.0) {
                enableEndgameSystems();  // allow vertical expansion
            }
            if (timeRemaining <= 20.0) {
                driveToBase();
            }

            // DRIVER HUD
            telemetry.addData("Motif", activeMotif);
            telemetry.addData("Next color needed", nextArtifactToScore(activeMotif));
            telemetry.addData("Controlled artifacts", controlledArtifacts);
            telemetry.addData("Time remaining", timeRemaining);
            telemetry.update();
        }
    }
}
```

---

## 9. GATE Operation

### Physical Description
- Gravity-closed barrier at the GATE end (index 1) of the RAMP.
- Opens by ROBOT pushing the GATE arm horizontally ~2 in.
- Contact zone: 3.75-5.5 in above TILE (closed), ~3 in above TILE (open).
- CLASSIFIED ARTIFACTS exit through the GATE into the opponent's SECRET TUNNEL ZONE.
- OVERFLOW ARTIFACTS pass over the top of the GATE regardless.

### Critical Rules for Gate Code

```
G413  — ROBOT must NOT grab, grasp, attach to, or suspend from the GATE.
         Only push the GATE open. Never apply closing force (pull).

G417.A — ROBOT must NOT contact the OPPONENT's GATE.
         Check field position before triggering GATE mechanism.

G417.B — ROBOT must NOT apply closing force to either GATE.
         Detect when GATE is open; only push forward, never pull.

G418  — ROBOT must NOT contact ARTIFACTS on own or opponent RAMP.
         Maintain clearance from RAMP during GATE operation.
         Exception: inconsequential, inadvertent contact while operating GATE.
```

### GATE Operation Sequence

```java
public void operateGate() {
    // 1. Navigate to own GATE ZONE position
    approachOwnGate();

    // 2. Extend mechanism to contact GATE arm at correct height (3.75-5.5 in)
    extendGatePusher();

    // 3. Push forward to open GATE (~2 in displacement)
    pushGateOpen();

    // 4. Hold open until ARTIFACTS clear the RAMP
    // Note: GATE closing before all ARTIFACTS exit is NOT an ARENA FAULT
    // Teams should be prepared to hold open longer than expected
    holdGateOpen(3000);  // milliseconds — tune to actual GATE timing

    // 5. Retract WITHOUT applying any closing/pulling force
    retractGatePusher();
}
```

---

## 10. PATTERN Scoring Logic

### How PATTERN Is Scored
1. At end of AUTO: assess ARTIFACTS currently on the RAMP.
2. At end of TELEOP: assess ARTIFACTS remaining on RAMP after all motion stops.
3. Each RAMP index (1-9) scores **2 PATTERN points** if the ARTIFACT colour matches the MOTIF colour at that index.
4. ARTIFACTS must be retained by the GATE (OVERFLOW ARTIFACTS passed over the GATE — they do NOT count for PATTERN).

### MOTIF Expansion Table

| RAMP Index | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|------------|---|---|---|---|---|---|---|---|---|
| MOTIF GPP  | G | P | P | G | P | P | G | P | P |
| MOTIF PGP  | P | G | P | P | G | P | P | G | P |
| MOTIF PPG  | P | P | G | P | P | G | P | P | G |

### Code-Level PATTERN Strategy

```java
// Determine next colour to score based on current RAMP state
public ArtifactColor nextArtifactToScore(Motif motif, boolean[] rampOccupied) {
    for (int i = 1; i <= 9; i++) {
        if (!rampOccupied[i - 1]) {
            return expectedColorAtIndex(motif, i);
        }
    }
    return null;  // RAMP full — operate GATE to clear
}

// Check if GATE operation is needed to continue scoring
public boolean shouldOperateGate(boolean[] rampOccupied) {
    for (boolean slot : rampOccupied) {
        if (!slot) return false;  // space available
    }
    return true;  // all 9 slots occupied — must operate GATE
}
```

### Driver HUD Recommendation
Display on DRIVER STATION screen:
- Active MOTIF (GPP / PGP / PPG)
- Next expected colour (GREEN / PURPLE)
- Current estimated RAMP fill level (0-9)
- PATTERN RP threshold progress (PATTERN pts / 18 target)
- GOAL RP threshold progress (ARTIFACTS through SQUARE / 36 target)

---

## 11. BASE Return Mechanics

### Scoring Conditions

| Condition | Points |
|-----------|--------|
| ROBOT partially supported by BASE ZONE TILE | 5 |
| ROBOT fully supported only by BASE ZONE TILE | 10 |
| Both ROBOTS fully in BASE (additional bonus) | +10 |

### "Fully Returned" Definition
- ROBOT is **only** supported (directly or transitively through other objects on field) by the TILE within the BASE ZONE tape boundary.
- The BASE ZONE TILE area is bounded by the outside edge of the alliance-coloured tape.
- If any support comes from a TILE outside the BASE ZONE, the ROBOT is only "partially returned".

### Code Logic

```java
public void endgameBaseReturn() {
    // Navigate to BASE ZONE using odometry
    navigateToBaseZone();

    // Ensure all ROBOT footprint is within BASE ZONE
    // All contact points must be within BASE ZONE boundaries
    confirmFullyWithinBase();

    // Stop motors and hold position — do NOT move after 0:00
    stopAllMotors();
}
```

### Timing Trigger

```java
// TELEOP duration = 120 seconds
// Begin BASE approach at T-25 seconds (~95 seconds into TELEOP)

ElapsedTime teleOpTimer = new ElapsedTime();

// In main loop:
if (teleOpTimer.seconds() >= 95.0) {
    beginBaseReturnSequence();
}
```

---

## 12. RANKING POINTS — Thresholds & Strategy

### Standard Event Thresholds

| RP | Threshold | Typical Path |
|----|-----------|-------------|
| MOVEMENT RP | LEAVE + BASE >= 16 | 2x LEAVE (6) + both partial BASE (10) = 16 |
| GOAL RP | ARTIFACTS through SQUARE >= 36 | Full RAMP (9) x 4 cycles with GATE operation |
| PATTERN RP | PATTERN pts >= 18 | Full correctly-coloured RAMP at TELEOP end (9x2=18) |

### RP Priority for Code Design
1. **MOVEMENT RP** — always achievable; code both ROBOTS to LEAVE and return to BASE.
2. **PATTERN RP** — requires MOTIF detection and colour-sorted scoring. High value, achievable with good code.
3. **GOAL RP** — 36 ARTIFACTS requires multiple RAMP cycles; plan GATE operations for 4+ fills.

### Optimal MATCH Scorecard Target

| Category | Target Score |
|----------|-------------|
| 2x LEAVE | 6 |
| 3 pre-loaded CLASSIFIED in AUTO | 9 |
| 3 matching PATTERN points in AUTO | 6 |
| 30 TELEOP CLASSIFIED ARTIFACTS | 90 |
| 18 TELEOP PATTERN points | 18 |
| 2x fully in BASE + bonus | 30 |
| **Total estimate** | **~159** |

---

## 13. Robot Starting Configuration & Expansion Limits

### STARTING CONFIGURATION
- ROBOT must fit within an **18 in x 18 in x 18 in cube**.
- Exception: pre-loaded ARTIFACTS may extend outside the cube.
- ROBOT must start over a LAUNCH LINE.
- ROBOT must touch own ALLIANCE's GOAL or FIELD perimeter.
- ROBOT must be fully on own alliance side (columns A-C for blue, D-F for red).

### Expansion Limits During MATCH

| Dimension | Limit | Method |
|-----------|-------|--------|
| Horizontal (at all times) | 18 in x 18 in | **Mechanical hard stop required** — software limit alone is insufficient |
| Vertical (standard) | 18 in | Software or mechanical |
| Vertical (final 20 sec, outside LAUNCH ZONES only) | Up to 38 in | Software or mechanical |

```java
// Software enforcement of conditional vertical expansion
public boolean canExpandVertically() {
    double timeRemaining = getMatchTimeRemaining();
    boolean inFinalPeriod = (timeRemaining <= 20.0);
    boolean outsideLaunchZone = !isInLaunchZone();
    return inFinalPeriod && outsideLaunchZone;
}

// In lift control:
if (liftUpButtonPressed) {
    if (lift.getCurrentHeight() <= 18.0 || canExpandVertically()) {
        lift.extend();
    }
    // else: silently block — do not extend past 18 in until permitted
}
```

---

## 14. Control System Hardware

### Required Configuration

| Component | Recommended Part | Part Number |
|-----------|-----------------|-------------|
| ROBOT CONTROLLER | REV Control Hub | REV-31-1595 |
| ROBOT CONTROLLER (alt) | Android smartphone + REV Expansion Hub | See R704 |
| Additional Expansion Hub (optional, max 1) | REV Expansion Hub | REV-31-1153 |
| DRIVER STATION | REV Driver Hub | REV-31-1596 |
| DRIVER STATION (alt) | Approved Android smartphone | See R704 |

### Recommended Software Versions

| Software | Recommended Version |
|----------|---------------------|
| Control Hub OS | 1.1.2 |
| Hub Firmware | 1.8.2 |
| ROBOT CONTROLLER App | 11.0 |
| DRIVER STATION App | 11.0 |
| REV Servo Hub Firmware | 25.0.2 |
| REV Driver Hub OS | 1.2.0 |

### Device Naming Convention

```
ROBOT CONTROLLER:  <teamNumber>-RC     e.g. 12345-RC
DRIVER STATION:    <teamNumber>-DS     e.g. 12345-DS
Spare:             <teamNumber>-A-RC / <teamNumber>-B-DS
```

### Wi-Fi & Connectivity Rules
- RC Wi-Fi password must be changed from default.
- DS must have Airplane Mode ON, Wi-Fi ON, Bluetooth OFF.
- All remembered Wi-Fi Direct groups must be removed from smartphones.
- No other wireless devices may connect to the RC network during a MATCH.
- Teams may be asked to use a specific Wi-Fi frequency band or channel at events.

---

## 15. Motors, Servos & Actuator Limits

### Motor Limit: 8 motors maximum (across all configurations)

| Legal Motor | Voltage |
|------------|---------|
| AndyMark NeveRest (am-3104, am-3104b, am-3104c) | 12V DC |
| goBILDA Yellow Jacket 520x Series | 12V DC |
| goBILDA 5000 Series | 12V DC |
| REV Robotics HD Hex (REV-41-1291) | 12V DC |
| REV Robotics Core Hex (REV-41-1300) | 12V DC |
| TETRIX MAX TorqueNADO (W44260) | 12V DC |
| Studica Robotics Maverick (75001) | 12V DC |
| SWYFT Robotics SWYFT Spike Motor | 12V DC |
| NFR Products Yuksel | 12V DC |

> Factory-installed vibration/autofocus motors in COTS computing devices and motors integral to COTS sensors (LIDAR, etc.) do NOT count toward the 8-motor limit.

### Servo Limit: 10 servos maximum (across all configurations)

| Class | Max Stall Current @ 6V | Max Mechanical Output @ 6V |
|-------|------------------------|---------------------------|
| Standard Servo | <= 4 A | <= 8 W |
| Linear Servo | <= 1 A | N/A |

Examples: goBILDA Dual Mode Servo (2000-0025-0003), REV Smart Servo (REV-41-1097), AndyMark High-Torque Servo (am-4954), Axon MAX+.

### Power Regulation Devices

| Device | Part Number | Load Limit |
|--------|-------------|-----------|
| REV Control Hub Motor Ports | REV-31-1595 | 2 motors/port |
| REV Control Hub Servo Ports | REV-31-1595 | 2 servos/port |
| REV Expansion Hub Motor Ports | REV-31-1153 | 2 motors/port |
| REV Expansion Hub Servo Ports | REV-31-1153 | 2 servos/port |
| REV SPARKmini | REV-31-1230 | 2 motors/device |
| REV Servo Power Module | REV-11-1144 | 2 servos/port @ 6V |
| REV Servo Hub | REV-11-1855 | 2 servos/port @ 6V |
| goBILDA 6V Servo Power Injector | 3125-0001-0001 | 2 servos/port |
| Studica Servo Power Block | 75005 | 2 servos/port @ 6V |

### Motor Budget Planning (Typical Mecanum ROBOT)

| Subsystem | Motor Count |
|-----------|------------|
| Mecanum drivetrain | 4 |
| Intake roller | 1 |
| Launcher / shooter | 1 |
| Vertical lift (endgame) | 1 |
| GATE pusher (if motorised) | 1 |
| **Total** | **8 (maximum allowed)** |

---

## 16. Power Architecture

### Main Battery
- **1x approved 12V NiMH battery only.** No substitutions.
- Must have in-line 20A ATM mini blade fuse.
- Legal batteries: AndyMark am-5290, goBILDA 3100-0012-0020, REV REV-31-1302, Studica 70025, TETRIX W39057, WATTOS WT-NMH1230.

### Main Power Switch (required)
One of: AndyMark am-4969, goBILDA 3103-0005-0001, REV REV-31-1387, Studica 70182, TETRIX W39129, WATTOS WTS-SW1220.

### Auxiliary Power (Peripherals Only)
- COTS USB battery packs (<=100 Wh, <=5V/5A or 12V/5A USB-PD) permitted for LEDs, cameras, powered USB hubs.
- Must NOT power any actuator or any device receiving control signals from the control hub.
- Must remain electrically isolated from main robot power.

### Wiring Requirements

| Circuit Type | Minimum Wire Gauge |
|---|---|
| 12V Main / Motor Power (general) | 18 AWG |
| PWM / Servo / LED (<=10A) | 22 AWG |
| Signal Level (I2C, encoder, DIO, RS485) | 28 AWG |

Wire colours: Positive = red / yellow / white / brown / black-with-stripe. Negative = black or blue.

### CUSTOM CIRCUITS (R613)
- May not provide regulated output voltages exceeding 5V.
- Exception: 5V limit does not apply if circuit is solely used to power LEDs.
- May pass through unregulated battery voltage.
- May not alter power pathways between battery, power switch, power regulators, and actuators.

---

## 17. Legal Sensors & Vision Devices

### USB Vision Devices (R715)

| Device | Notes |
|--------|-------|
| Any UVC-compatible USB webcam (e.g. Logitech C270) | UVC stream only — no additional data interfaces |
| Limelight Vision Limelight 3A (LL_3A) | Programmable vision coprocessor natively supported by FTC SDK |

**NOT allowed:** OpenMV, Luxonis OAK-1, Limelight 3G, stereoscopic cameras.

### Configurable (Not Programmable) Vision Coprocessors (R702)
- DFRobot HuskyLens, Charmed Labs Pixy2: configurable but not programmable — firmware updates from manufacturer only.

### Other Sensors
- IMUs (e.g. Adafruit BNO055): use as-is; manufacturer firmware updates only.
- SparkFun Optical Tracking Odometry Sensor: manufacturer firmware updates only; custom firmware NOT permitted.
- Digital Chicken Labs OctoQuad FTC Edition: manufacturer updates only; no custom firmware.

### Lasers (R717)
- Must be part of a sensor.
- IEC/EN 60825-1 Class I or IEC/EN 62471 Exempt rated only.
- Non-visible spectrum only.

### Wireless (R709)
- No Bluetooth at events (2.4 GHz prohibited).
- No Wi-Fi beyond the RC network during a MATCH.
- Cameras and passive IR sensors (detecting field elements) are NOT wireless devices — they are permitted.

---

## 18. Software Architecture & SDK Requirements

### FTC SDK
- **Version 11.0** minimum (ROBOT CONTROLLER App and DRIVER STATION App must match major.minor version).
- Language: Java. Blocks also supported but not recommended for high-performance code.
- OpModes: `LinearOpMode` (recommended) or iterative `OpMode`.

### Recommended Repository Structure

```
TeamCode/
    auto/
        AutoBlue.java          // Blue alliance autonomous
        AutoRed.java           // Red alliance autonomous
        AutoCalibrate.java     // Field measurement / sensor cal OpMode
    teleop/
        TeleopMain.java        // Main competition TELEOP
        TeleopTest.java        // Development / testing
    subsystems/
        Drivetrain.java        // Mecanum drive (robot-centric + field-centric)
        Intake.java            // ARTIFACT collection mechanism
        Launcher.java          // ARTIFACT scoring into GOAL
        Gate.java              // Own GATE pusher mechanism
        Lift.java              // Vertical extension (endgame BASE return)
        VisionSystem.java      // AprilTag + colour detection
    util/
        MotifDetector.java     // OBELISK reading logic
        PatternTracker.java    // RAMP fill state and PATTERN point calculator
        FieldConstants.java    // Zone boundaries, tile coordinates
        RobotState.java        // Shared state between AUTO and TELEOP
    hardware/
        HardwareInit.java      // Hardware map bindings
```

### MOTIF Persistence Between AUTO and TELEOP

```java
// Shared state class — static fields persist within same process lifecycle
public class RobotState {
    public static Motif activeMotif = Motif.UNKNOWN;
    public static int artifactsThroughSquare = 0;
    public static int currentPatternPoints = 0;
}

// In AUTO: save
RobotState.activeMotif = detectedMotif;

// In TELEOP: retrieve
Motif motif = RobotState.activeMotif;
```

### AprilTag Detection Setup

```java
// Hardware initialisation
AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
    .setDrawAxes(false)
    .setDrawTagID(true)
    .setDrawTagOutline(true)
    .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
    .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
    .build();

VisionPortal visionPortal = new VisionPortal.Builder()
    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
    .addProcessor(aprilTagProcessor)
    .build();

// Usage in OpMode
List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
for (AprilTagDetection detection : detections) {
    int tagId = detection.id;
    double range    = detection.ftcPose.range;   // distance in inches
    double bearing  = detection.ftcPose.bearing; // degrees left/right
    double yaw      = detection.ftcPose.yaw;     // heading offset
}
```

### Mecanum Drivetrain

```java
// Robot-centric drive
public void driveRobotCentric(double drive, double strafe, double turn) {
    double frontLeft  = drive + strafe + turn;
    double frontRight = drive - strafe - turn;
    double backLeft   = drive - strafe + turn;
    double backRight  = drive + strafe + turn;

    double max = Math.max(1.0,
                 Math.max(Math.abs(frontLeft),
                 Math.max(Math.abs(frontRight),
                 Math.max(Math.abs(backLeft), Math.abs(backRight)))));
    motorFL.setPower(frontLeft  / max);
    motorFR.setPower(frontRight / max);
    motorBL.setPower(backLeft   / max);
    motorBR.setPower(backRight  / max);
}

// Field-centric drive (recommended for precision positioning in LAUNCH ZONE)
public void driveFieldCentric(double drive, double strafe, double turn, double headingRad) {
    double adjustedDrive  =  drive  * Math.cos(headingRad) + strafe * Math.sin(headingRad);
    double adjustedStrafe = -drive  * Math.sin(headingRad) + strafe * Math.cos(headingRad);
    driveRobotCentric(adjustedDrive, adjustedStrafe, turn);
}
```

### PD Controller for GOAL Alignment (AprilTag)

```java
double kP_bearing = 0.04;
double kD_bearing = 0.002;
double kP_range   = 0.05;
double prevBearingError = 0;

public double computeYawCorrection(AprilTagDetection detection) {
    double error = detection.ftcPose.bearing;  // degrees
    double derivative = error - prevBearingError;
    prevBearingError = error;
    return kP_bearing * error + kD_bearing * derivative;
}

public boolean isAlignedToGoal(AprilTagDetection detection, double targetRangeIn) {
    return Math.abs(detection.ftcPose.bearing) < 2.0          // within 2 degrees
        && Math.abs(detection.ftcPose.range - targetRangeIn) < 1.5;  // within 1.5 inches
}
```

---

## 19. OpMode Requirements & Match-Ready Checklist

### Pre-Match Requirements (G303, G304, G305)

```
[ ] ROBOT is within 18 in x 18 in x 18 in STARTING CONFIGURATION
[ ] ROBOT is over a LAUNCH LINE
[ ] ROBOT is touching own ALLIANCE's GOAL or FIELD perimeter
[ ] ROBOT is fully on own alliance side (A-C for blue, D-F for red)
[ ] ROBOT SIGNS show correct ALLIANCE colour
[ ] OpMode selected and INIT pressed in DRIVER STATION app
[ ] ROBOT is motionless after INIT
[ ] If AUTO OpMode: 30-second timer enabled in DRIVER STATION
[ ] Pre-loaded ARTIFACTS in contact with ROBOT (up to 3)
```

### OpMode Selection Notes
- FIELD STAFF use INIT as the signal that the team is ready.
- All teams must select and INIT an OpMode even if not planning to run AUTO.
- Recommended: create a DoNothing AUTO as a fallback.

```java
@Autonomous(name = "DoNothing")
public class DoNothingAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        waitForStart();
        // ROBOT stays stationary — no points but fully legal
    }
}
```

---

## 20. Key Game Rules Affecting Code

### AUTO Rules

| Rule | Description | Code Response |
|------|-------------|---------------|
| G401 | No driver input during AUTO (except start/stop buttons) | All AUTO movement must be pre-programmed |
| G402 | No crossing to opponent's side or disrupting their ARTIFACTS | Hard field boundary check in path planning |
| G401 | OBELISK randomised after DRIVE TEAMS set up — before MATCH start | Detect OBELISK AprilTag in first few seconds of AUTO, not during INIT |
| G403 | No powered movement during 8-second AUTO-to-TELEOP transition | Stop all motors at AUTO end; timer gate before re-enabling in TELEOP |

### TELEOP Rules

| Rule | Description | Code Response |
|------|-------------|---------------|
| G404 | No powered movement after MATCH ends | Stop all motor outputs when `opModeIsActive()` returns false |
| G408 | Max 3 ARTIFACTS in CONTROL simultaneously | Possession counter; disable intake at count = 3 |
| G414 | Horizontal expansion <= 18 in at all times | Mechanical hard stop required (software insufficient per R105) |
| G415 | Vertical expansion > 18 in only in final 20 sec, outside LAUNCH ZONES | Software timer gate and zone check before allowing lift |
| G416 | LAUNCH only from LAUNCH ZONE | Check ROBOT position before enabling launcher motors |
| G417.A | Do not contact opponent's GATE | Alliance-specific GATE position check |
| G417.B | Never apply closing force to any GATE | Retract-only gate mechanism; never pull |
| G418 | Do not contact ARTIFACTS on RAMP (own or opponent) | Clearance control during GATE operation |
| G419 | Only LAUNCH into own GOAL through open top | Vision system or driver prompt to confirm own GOAL alignment |
| G422 | PIN limit: 3 seconds maximum | Drive away after 3 sec of contact with stationary opponent |
| G427 | BASE ZONE protection in final 20 sec | Do not contact opponent near their BASE ZONE after T-20 |

---

## 21. Violation Reference — Penalties Code Must Avoid

| Violation | Penalty | Code Prevention |
|-----------|---------|-----------------|
| G402: Cross to opponent side in AUTO | MAJOR FOUL per instance | Hard boundary in autonomous path planning |
| G402: Disrupt opponent ARTIFACTS in AUTO | MAJOR FOUL per ARTIFACT | Avoid navigating to opponent side |
| G403: Powered movement during 8-sec transition | MAJOR FOUL | Stop motors at AUTO end; guard before TELEOP start |
| G404: Powered movement / LAUNCH after MATCH ends | MINOR/MAJOR FOUL | Stop all outputs at opModeIsActive() = false |
| G408: >3 ARTIFACTS in CONTROL | MINOR FOUL per extra; YELLOW CARD if excessive | Intake possession counter with hard limit |
| G414: Horizontal overexpansion | MINOR/MAJOR FOUL | Mechanical hard stop |
| G415: Premature vertical expansion | MINOR/MAJOR FOUL | Software timer + zone gate |
| G416: LAUNCH outside LAUNCH ZONE | MINOR FOUL per ARTIFACT; MAJOR if enters GOAL | Position verification before launch |
| G417.A: Contact opponent's GATE | MAJOR FOUL + opponent awarded PATTERN RP | Alliance-specific targeting |
| G418: Contact ARTIFACTS on RAMP | MAJOR FOUL per ARTIFACT; PATTERN RP loss | Clearance control |
| G419.B: Score into opponent's GOAL | MAJOR FOUL per ARTIFACT + PATTERN RP to opponent | Vision/driver confirmation of own GOAL |
| G422: PIN > 3 seconds | MINOR FOUL + more per 3 sec | Drive away timer |

---

## 22. Field Zones — Coordinates & Boundaries

### Zone Summary

| Zone | Dimensions | Alliance | Key Rule |
|------|------------|----------|----------|
| LAUNCH ZONE (GOAL side) | 6 tiles x 3 tiles | Own | Must be in this zone to LAUNCH |
| LAUNCH ZONE (audience side) | 2 tiles x 1 tile | Own | Must be in this zone to LAUNCH |
| LOADING ZONE | ~23 in x 23 in | Own | Human player ARTIFACT delivery (TELEOP only) |
| BASE ZONE | 18 in x 18 in | Own | End-game return scoring |
| GATE ZONE | 2.75 in x 10 in | Own | Protected from opponent contact |
| SECRET TUNNEL ZONE | ~46.5 in x 6.125 in | Alliance-specific | Collect OVERFLOW after opponent GATE operation |
| DEPOT | 30 in tape strip at GOAL base | Own | ARTIFACTS here score 1 pt at TELEOP end |

### TILE Coordinate System
- TILE intersections defined by the manual (Figure 9-4).
- Columns A-C: Blue alliance side. Columns D-F: Red alliance side.
- Rows numbered audience-to-GOAL side.
- Use odometry (dead wheels or motor encoders + IMU) for field-relative positioning.

### Navigation Reference
- Use AprilTag IDs 20 (blue GOAL) and 24 (red GOAL) for GOAL targeting. Stable placement.
- OBELISK AprilTags NOT for navigation (placement varies per MATCH).
- For field-relative coordinates: establish origin at known ROBOT start position, track with odometry.

---

## 23. LOADING ZONE Human-Player Interaction Model

### Rules (G432)
- DRIVE TEAM members may only introduce, remove, or move ARTIFACTS within the LOADING ZONE.
- Only permitted during TELEOP (not AUTO or transition period).
- No tools — hands only.
- ARTIFACTS may only leave the LOADING ZONE if CONTROLLED by a ROBOT that:
  1. Was in the LOADING ZONE when CONTROL began, AND
  2. Still CONTROLS the ARTIFACT when leaving the LOADING ZONE.

### Storage Limit (G434)
- Maximum 6 ARTIFACTS out of play in ALLIANCE AREA during TELEOP.
- Violation: MINOR FOUL per ARTIFACT over limit, recurring every 3 seconds.

### Code Implication
- ROBOT should approach LOADING ZONE corner to receive ARTIFACTS from human player.
- After intake, ROBOT exits LOADING ZONE with ARTIFACT in CONTROL and proceeds to LAUNCH ZONE.
- Intake possession counter: enable when count < 3, block when count >= 3.
- Human player receives signal from ROBOT approach to begin loading.

---

## 24. Drive Train & Drivetrain Constraints

### Traction Device Rules (R201)
- High-traction wheels (e.g. AndyMark am-2256) and high-grip treads (e.g. Roughtop am-3309) that contact TILE floor are **prohibited** — they damage foam TILES.
- Approved: standard rubber wheels, mecanum wheels, omni wheels, compliant wheels.
- Note: prohibited traction surfaces may be used as intake elements as long as they do not contact the TILE floor.

### Recommended Drivetrain: Mecanum
- Enables holonomic movement (strafe, diagonal, rotate in place) — critical for LAUNCH ZONE precision and LOADING ZONE approach.
- 4 motors (uses 4 of 8-motor budget).
- Legal COTS mecanum wheel kits permitted (single DoF per R303.I).

### Motor Allocation Recommendation

| Subsystem | Motors |
|-----------|--------|
| Mecanum drivetrain | 4 |
| Intake/collector | 1 |
| Launcher flywheel(s) | 1-2 |
| Lift (endgame) | 1 |
| **Total** | 7-8 |

### IMU Usage (for heading correction)
- REV Control Hub has a built-in IMU (BHI260AP or BNO055 depending on revision).
- Use for field-centric drive and autonomous heading correction.

```java
// Get current robot heading (for field-centric drive)
YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
double headingRad = Math.toRadians(orientation.getYaw(AngleUnit.DEGREES));
```

---

## 25. Qualification Ranking Logic (for strategy optimisation)

### Ranking Score (RS)
- RS = average RANKING POINTS per Qualification MATCH (excluding SURROGATE MATCHES).
- Tiebreakers applied in this order:
  1. RS
  2. Average ALLIANCE MATCH points excluding FOULS
  3. Average BASE points
  4. Average AUTO points
  5. Random

### RP Earning Strategy Per MATCH

```
Always achievable:   WIN (3) + MOVEMENT RP (1) = 4 RP minimum with a win
Stretch target:      WIN (3) + MOVEMENT RP (1) + GOAL RP (1) + PATTERN RP (1) = 6 RP maximum
```

### Points-to-RP Conversion Check

```java
// Check if MOVEMENT RP threshold is met (standard events: 16 pts)
public boolean isMovementRpAchieved(int leavePoints, int basePoints) {
    return (leavePoints + basePoints) >= 16;
}

// Check if GOAL RP threshold is met (standard events: 36 ARTIFACTS through SQUARE)
public boolean isGoalRpAchieved(int artifactsThroughSquare) {
    return artifactsThroughSquare >= 36;
}

// Check if PATTERN RP threshold is met (standard events: 18 pts)
public boolean isPatternRpAchieved(int patternPoints) {
    return patternPoints >= 18;
}
```

### Advancement Points Note
Advancement Points are separate from RANKING POINTS — they combine:
- Qualification Phase Performance (based on rank, inverse error function distribution, 2-16 pts)
- ALLIANCE Selection Results (21 minus ALLIANCE number)
- Playoff Performance (40/20/10/5 for 1st-4th)
- Team Judged Awards (60/30/15 for Inspire 1st/2nd/3rd; 12/6/3 for other awards)

---

## 26. Complete Programming Checklist

### AUTO
- [ ] AprilTag detector initialised and active before MATCH start
- [ ] MOTIF detected from OBELISK AprilTag (IDs 21, 22, 23) within first 5 seconds of AUTO
- [ ] MOTIF stored in RobotState for TELEOP retrieval
- [ ] ROBOT crosses LAUNCH LINE (LEAVE points — 3 pts per ROBOT)
- [ ] ROBOT aligned to own GOAL using GOAL AprilTag (ID 20 or 24)
- [ ] Pre-loaded ARTIFACTS scored as CLASSIFIED through GOAL top
- [ ] Additional SPIKE MARK ARTIFACTS collected and scored if time permits
- [ ] ROBOT never crosses into opponent's side (columns A-C for blue, D-F for red)
- [ ] All motors stopped at AUTO end (before 8-sec transition period)
- [ ] No movement during 8-second AUTO-to-TELEOP transition

### TELEOP
- [ ] Active MOTIF loaded from RobotState at TELEOP start
- [ ] Field-centric mecanum drive on gamepad1 with IMU heading feedback
- [ ] ARTIFACT possession counter active (max 3 in CONTROL)
- [ ] Intake disabled when possession count >= 3
- [ ] LAUNCH mechanism enabled only when ROBOT is in LAUNCH ZONE
- [ ] GOAL AprilTag targeting / bearing display for driver alignment
- [ ] GATE operation: push own GATE only, hold open, retract without closing force
- [ ] Pattern status display on DRIVER STATION (next colour needed, RAMP fill)
- [ ] Endgame timer: vertical expansion only enabled in final 20 sec outside LAUNCH ZONES
- [ ] BASE return triggered at approximately T-25 seconds
- [ ] All motors stopped when opModeIsActive() returns false (no movement post-0:00)

### Safety & Compliance
- [ ] Horizontal expansion mechanically limited to 18 in at all times (not software-only)
- [ ] ROBOT SIGNS attached and showing correct ALLIANCE colour (R402)
- [ ] OpMode correctly named and selectable from DRIVER STATION
- [ ] INIT button press leaves ROBOT completely motionless
- [ ] Wi-Fi password changed from default on Control Hub (R718)
- [ ] Team number configured: `<number>-RC` and `<number>-DS` (R707)
- [ ] No devices broadcasting Bluetooth or ad-hoc Wi-Fi during MATCH (E301, R709)
- [ ] Battery secured against inversion and side-impact (R606)
- [ ] All wiring visible for inspection (R612)
- [ ] ROBOT SIGNS on at least 2 locations, >= 90 degrees apart (R401)

### Vision Pipeline
- [ ] AprilTag 36h11 family configured in VisionPortal
- [ ] OBELISK detection: reads tags 21 (GPP), 22 (PGP), 23 (PPG)
- [ ] GOAL targeting: reads tag 20 (blue alliance) or 24 (red alliance)
- [ ] Colour sensor or camera for ARTIFACT colour discrimination (purple vs green)
- [ ] OBELISK reading occurs in AUTO, not used for navigation (placement varies per MATCH)
- [ ] Vision pipeline stopped or paused during TELEOP if not needed (reduces CPU load)

---

*Document compiled from FTC 2025-2026 DECODE presented by RTX Competition Manual, Team Update 27 (TU27) — the authoritative season ruleset. All point values, RP thresholds, field dimensions, and rules are sourced directly from TU27. In any discrepancy, the latest official English PDF on the FIRST Game and Season Materials page supersedes this document.*
