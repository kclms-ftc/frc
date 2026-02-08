/**
 * AprilTagDetection.java
 * 
 * Data structure for storing AprilTag detection information.
 * Contains position, orientation, and distance measurements.
 * 
 * This class is used to pass detection data from the AprilTagProcessor
 * to the display and analysis components.
 */

/**
 * Represents a single detected AprilTag with all its properties
 */
public class AprilTagDetection {
    
    // ============================================================================
    // FIELDS - Tag Identification
    // ============================================================================
    
    /** Unique identifier for this tag (0-586 for TAG_36h11) */
    private int id;
    
    /** Name or description of the tag */
    private String name;
    
    /** Confidence score for this detection (0.0 to 1.0) */
    private double confidence;
    
    // ============================================================================
    // FIELDS - Position (X, Y, Z in inches)
    // ============================================================================
    
    /** X position: Horizontal distance to the right (inches) */
    private double x;
    
    /** Y position: Forward distance (inches) */
    private double y;
    
    /** Z position: Vertical distance upward (inches) */
    private double z;
    
    // ============================================================================
    // FIELDS - Orientation (Pitch, Roll, Yaw in degrees)
    // ============================================================================
    
    /** Pitch: Rotation around X-axis (degrees, -180 to 180) */
    private double pitch;
    
    /** Roll: Rotation around Y-axis (degrees, -180 to 180) */
    private double roll;
    
    /** Yaw: Rotation around Z-axis (degrees, -180 to 180) */
    private double yaw;
    
    // ============================================================================
    // FIELDS - Distance Measurements
    // ============================================================================
    
    /** Range: Total distance from camera to tag (inches) */
    private double range;
    
    /** Bearing: Horizontal angle to tag (degrees, -180 to 180) */
    private double bearing;
    
    /** Elevation: Vertical angle to tag (degrees, -90 to 90) */
    private double elevation;
    
    // ============================================================================
    // FIELDS - Metadata
    // ============================================================================
    
    /** Timestamp when detection occurred (milliseconds) */
    private long timestamp;
    
    /** Frame number in which detection occurred */
    private int frameNumber;
    
    /** Camera resolution width (pixels) */
    private int cameraWidth;
    
    /** Camera resolution height (pixels) */
    private int cameraHeight;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    /**
     * Create a new AprilTag detection with minimal information
     * 
     * @param id The tag ID
     * @param x X position (inches)
     * @param y Y position (inches)
     * @param z Z position (inches)
     */
    public AprilTagDetection(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = System.currentTimeMillis();
        this.confidence = 1.0;
        this.name = "Tag " + id;
    }
    
    /**
     * Create a new AprilTag detection with full information
     * 
     * @param id The tag ID
     * @param x X position (inches)
     * @param y Y position (inches)
     * @param z Z position (inches)
     * @param pitch Pitch rotation (degrees)
     * @param roll Roll rotation (degrees)
     * @param yaw Yaw rotation (degrees)
     * @param range Distance to tag (inches)
     * @param bearing Horizontal angle (degrees)
     * @param elevation Vertical angle (degrees)
     */
    public AprilTagDetection(int id, double x, double y, double z,
                            double pitch, double roll, double yaw,
                            double range, double bearing, double elevation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.range = range;
        this.bearing = bearing;
        this.elevation = elevation;
        this.timestamp = System.currentTimeMillis();
        this.confidence = 1.0;
        this.name = "Tag " + id;
    }
    
    // ============================================================================
    // GETTERS - Tag Identification
    // ============================================================================
    
    /**
     * Get the tag ID
     * @return Tag ID (0-586)
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the tag name
     * @return Tag name or description
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the tag name
     * @param name Tag name or description
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the detection confidence
     * @return Confidence score (0.0 to 1.0)
     */
    public double getConfidence() {
        return confidence;
    }
    
    /**
     * Set the detection confidence
     * @param confidence Confidence score (0.0 to 1.0)
     */
    public void setConfidence(double confidence) {
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
    }
    
    // ============================================================================
    // GETTERS - Position
    // ============================================================================
    
    /**
     * Get X position (horizontal distance to the right)
     * @return X position in inches
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get Y position (forward distance)
     * @return Y position in inches
     */
    public double getY() {
        return y;
    }
    
    /**
     * Get Z position (vertical distance upward)
     * @return Z position in inches
     */
    public double getZ() {
        return z;
    }
    
    /**
     * Set position
     * @param x X position (inches)
     * @param y Y position (inches)
     * @param z Z position (inches)
     */
    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // ============================================================================
    // GETTERS - Orientation
    // ============================================================================
    
    /**
     * Get pitch (rotation around X-axis)
     * @return Pitch in degrees (-180 to 180)
     */
    public double getPitch() {
        return pitch;
    }
    
    /**
     * Get roll (rotation around Y-axis)
     * @return Roll in degrees (-180 to 180)
     */
    public double getRoll() {
        return roll;
    }
    
    /**
     * Get yaw (rotation around Z-axis)
     * @return Yaw in degrees (-180 to 180)
     */
    public double getYaw() {
        return yaw;
    }
    
    /**
     * Set orientation
     * @param pitch Pitch in degrees
     * @param roll Roll in degrees
     * @param yaw Yaw in degrees
     */
    public void setOrientation(double pitch, double roll, double yaw) {
        this.pitch = normalizeAngle(pitch);
        this.roll = normalizeAngle(roll);
        this.yaw = normalizeAngle(yaw);
    }
    
    // ============================================================================
    // GETTERS - Distance Measurements
    // ============================================================================
    
    /**
     * Get range (total distance from camera to tag)
     * @return Range in inches
     */
    public double getRange() {
        return range;
    }
    
    /**
     * Get bearing (horizontal angle to tag)
     * @return Bearing in degrees (-180 to 180)
     */
    public double getBearing() {
        return bearing;
    }
    
    /**
     * Get elevation (vertical angle to tag)
     * @return Elevation in degrees (-90 to 90)
     */
    public double getElevation() {
        return elevation;
    }
    
    /**
     * Set distance measurements
     * @param range Range in inches
     * @param bearing Bearing in degrees
     * @param elevation Elevation in degrees
     */
    public void setDistanceMeasurements(double range, double bearing, double elevation) {
        this.range = Math.max(0, range);
        this.bearing = normalizeAngle(bearing);
        this.elevation = Math.max(-90, Math.min(90, elevation));
    }
    
    // ============================================================================
    // GETTERS - Metadata
    // ============================================================================
    
    /**
     * Get detection timestamp
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get frame number
     * @return Frame number
     */
    public int getFrameNumber() {
        return frameNumber;
    }
    
    /**
     * Set frame number
     * @param frameNumber Frame number
     */
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
    
    /**
     * Get camera width
     * @return Camera width in pixels
     */
    public int getCameraWidth() {
        return cameraWidth;
    }
    
    /**
     * Get camera height
     * @return Camera height in pixels
     */
    public int getCameraHeight() {
        return cameraHeight;
    }
    
    /**
     * Set camera resolution
     * @param width Camera width in pixels
     * @param height Camera height in pixels
     */
    public void setCameraResolution(int width, int height) {
        this.cameraWidth = width;
        this.cameraHeight = height;
    }
    
    // ============================================================================
    // UTILITY METHODS
    // ============================================================================
    
    /**
     * Check if this is a high-confidence detection
     * @return true if confidence > 0.8
     */
    public boolean isHighConfidence() {
        return confidence > 0.8;
    }
    
    /**
     * Check if tag is within a certain distance
     * @param maxDistance Maximum distance in inches
     * @return true if range <= maxDistance
     */
    public boolean isWithinDistance(double maxDistance) {
        return range <= maxDistance;
    }
    
    /**
     * Check if tag is facing camera (not at extreme angle)
     * @return true if pitch and roll are within ±45 degrees
     */
    public boolean isFacingCamera() {
        return Math.abs(pitch) <= 45 && Math.abs(roll) <= 45;
    }
    
    /**
     * Calculate distance from camera to tag using position
     * @return Distance in inches
     */
    public double calculateDistance() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    /**
     * Normalize angle to -180 to 180 range
     * @param angle Angle in degrees
     * @return Normalized angle
     */
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
    
    // ============================================================================
    // STRING REPRESENTATION
    // ============================================================================
    
    /**
     * Get string representation of detection
     * @return Formatted string with all detection data
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AprilTagDetection {\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Name: ").append(name).append("\n");
        sb.append("  Confidence: ").append(String.format("%.2f", confidence)).append("\n");
        sb.append("  Position: X=").append(String.format("%.2f", x))
          .append(", Y=").append(String.format("%.2f", y))
          .append(", Z=").append(String.format("%.2f", z)).append("\n");
        sb.append("  Orientation: Pitch=").append(String.format("%.2f", pitch))
          .append("°, Roll=").append(String.format("%.2f", roll))
          .append("°, Yaw=").append(String.format("%.2f", yaw)).append("°\n");
        sb.append("  Distance: Range=").append(String.format("%.2f", range))
          .append(", Bearing=").append(String.format("%.2f", bearing))
          .append("°, Elevation=").append(String.format("%.2f", elevation)).append("°\n");
        sb.append("  Timestamp: ").append(timestamp).append("\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Get compact string representation
     * @return Single-line formatted string
     */
    public String toCompactString() {
        return String.format("Tag %d: Pos(%.1f,%.1f,%.1f) Dist=%.1f Conf=%.2f",
                id, x, y, z, range, confidence);
    }
}
