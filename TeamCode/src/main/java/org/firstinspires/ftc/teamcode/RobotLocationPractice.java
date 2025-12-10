package org.firstinspires.ftc.teamcode;

public class RobotLocationPractice {
    double angle;
    double x;
    double y;

    // constructor method
    public RobotLocationPractice(double angle) {
        this.angle = angle;
    }

    public double getHeading() {
        // this method normalises robot heating between -180 and 180
        // this is useful for calculating turn angles, especially when crossing the 0, 360 boundary

        double angle = this.angle; //copy angle of imu

        while (angle > 180){
            angle -= 360; //subtract until in target range
        }

        while (angle <= -180){
            angle += 360; //add until in target range
        }
        return angle; // return normalised value
    }


    public void turnRobot(double angleChange) {
        angle += angleChange;
    }
    public void setAngle(double angle) { // setter function
        this.angle = angle;
    }

    public double getAngle() { // getter function
        return this.angle;
    }

    //
    public void changeX(double changeAmount) {
        x += changeAmount;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getX() {
        return this.x;
    }


    public void changeY(double changeAmount) {
        y += changeAmount;
    }
    public void setY(double y){
        this.y = y;
    }

    public double getY(){
        return this.y;
    }
}
