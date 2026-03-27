# diff between what we planned and what we built

heres a quick rundown of the `teamcode/readme.md` dev plan compared to the code we actually have in `contents/contv2.md`.

## the good news
- architectures solid (100% matches what we wanted)
- no cheating by using `setPower` in teleop
- shooter is honestly way better than the plan (added a non blocking feeder, pidf rpm gate, and reverifying aim between balls)
- auto has 6 phases with timeouts so we dont get stuck

## things we missed

### drivetrain
we got mecanum maths and the three speed modes working perfectly, but theres no `driveDistance` or `turnToAngle`. means our auto can only sit there and shoot but not drive anywhere.

### intake
three states are in (off/in/reverse), but the anti-jam code is stuck inside `IntakeTest` and hasnt been moved to the main `Intake` class yet. so the competition intake will just burn out the motor if a ball gets jammed.

### turret
auto aim is nice but we totally forgot to make preset positions (like press A to face front, press B to snap to the scoring zone). the `angle_servo` is mapped but we arent using it.

### vision
we have apriltags but no pipeline to see colors, so we cant track the balls. the turret assumes the camera is exactly in the middle which is probably going to cause an offset error at long range. 

### auto
we only made `AutoShooter`. we need an `AutoMain` that can actually drive around, pick up balls, and park.

## fixes to do first
1. move that anti jam logic so we dont fry the intake motor
2. set some deadzones on the controller so the robot stops sliding randomly
3. figure out what the angle servo is supposed to do for the turret
