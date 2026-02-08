/**
 * AprilTagWebcamScanner.java
 * 
 * Main application for detecting AprilTags using a webcam.
 * This is a standalone Java application that can run independently
 * on any computer with a webcam and Java 11+.
 * 
 * Features:
 * - Real-time AprilTag detection
 * - Position and orientation tracking
 * - Multiple tag detection
 * - Console output with detection data
 * - Webcam feed visualization
 * - Easy to modify and extend
 * 
 * Usage:
 * javac -cp lib/* src/*.java
 * java -cp lib/*:src AprilTagWebcamScanner
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Main AprilTag Scanner Application
 * 
 * This class serves as the entry point for the webcam-based AprilTag detection system.
 * It initializes the camera, sets up the AprilTag processor, and continuously
 * detects and displays AprilTag information.
 */
public class AprilTagWebcamScanner {

    // ============================================================================
    // CONFIGURATION CONSTANTS - EDIT THESE TO CUSTOMIZE BEHAVIOR
    // ============================================================================
    
    /**
     * Enable or disable webcam usage
     * true = use external USB webcam
     * false = use built-in camera (if available)
     * 
     * EDIT THIS: Change to false to use built-in camera
     */
    private static final boolean USE_WEBCAM = true;
    
    /**
     * Camera resolution width in pixels
     * Common values: 320, 640, 800, 1280
     * Higher resolution = better detection but slower processing
     * 
     * EDIT THIS: Increase for better quality, decrease for speed
     */
    private static final int CAMERA_WIDTH = 640;
    
    /**
     * Camera resolution height in pixels
     * Common values: 240, 480, 600, 720
     * Should maintain 4:3 or 16:9 aspect ratio
     * 
     * EDIT THIS: Increase for better quality, decrease for speed
     */
    private static final int CAMERA_HEIGHT = 480;
    
    /**
     * Camera frames per second (FPS)
     * Common values: 15, 24, 30, 60
     * Higher FPS = more responsive but more CPU usage
     * 
     * EDIT THIS: Increase for more responsive detection
     */
    private static final int CAMERA_FPS = 30;
    
    /**
     * AprilTag decimation factor
     * 1 = full resolution (slower but more accurate)
     * 2 = half resolution (faster)
     * 3 = third resolution (fastest, default)
     * 4 = quarter resolution (very fast)
     * Higher decimation = faster processing but less accurate
     * 
     * EDIT THIS: Decrease for accuracy, increase for speed
     */
    private static final int APRILTAG_DECIMATION = 3;
    
    /**
     * Enable debug output to console
     * true = print detailed debug information
     * false = print only detection results
     * 
     * EDIT THIS: Set to true for troubleshooting
     */
    private static final boolean DEBUG_MODE = true;
    
    /**
     * Update interval in milliseconds
     * How often to refresh the display and check for detections
     * 33 ms = ~30 FPS, 16 ms = ~60 FPS
     * 
     * EDIT THIS: Decrease for faster updates
     */
    private static final long UPDATE_INTERVAL_MS = 33;
    
    /**
     * Maximum runtime in seconds (0 = unlimited)
     * Useful for testing - set to 0 for continuous operation
     * 
     * EDIT THIS: Set to limit runtime for testing
     */
    private static final long MAX_RUNTIME_SECONDS = 0;
    
    // ============================================================================
    // INSTANCE VARIABLES
    // ============================================================================
    
    /**
     * AprilTag processor instance
     * Handles the actual detection algorithm
     */
    private AprilTagProcessor aprilTagProcessor;
    
    /**
     * Camera manager instance
     * Handles webcam initialization and frame capture
     */
    private CameraManager cameraManager;
    
    /**
     * Display manager instance
     * Handles visualization of camera feed and detections
     */
    private DisplayManager displayManager;
    
    /**
     * List of currently detected AprilTags
     */
    private List<AprilTagDetection> currentDetections;
    
    /**
     * Flag to control the main loop
     */
    private boolean isRunning;
    
    /**
     * Statistics tracking
     */
    private long frameCount;
    private long totalDetections;
    private long startTime;
    
    // ============================================================================
    // CONSTRUCTOR
    // ============================================================================
    
    /**
     * Initialize the AprilTag Scanner
     */
    public AprilTagWebcamScanner() {
        this.currentDetections = new ArrayList<>();
        this.isRunning = false;
        this.frameCount = 0;
        this.totalDetections = 0;
        this.startTime = System.currentTimeMillis();
        
        if (DEBUG_MODE) {
            System.out.println("[INIT] AprilTag Webcam Scanner initializing...");
        }
    }
    
    // ============================================================================
    // INITIALIZATION METHODS
    // ============================================================================
    
    /**
     * Initialize all components of the scanner
     * 
     * @return true if initialization successful, false otherwise
     */
    public boolean initialize() {
        try {
            if (DEBUG_MODE) {
                System.out.println("[INIT] Initializing camera manager...");
            }
            
            // Initialize camera
            cameraManager = new CameraManager(USE_WEBCAM, CAMERA_WIDTH, CAMERA_HEIGHT, CAMERA_FPS);
            if (!cameraManager.initialize()) {
                System.err.println("[ERROR] Failed to initialize camera");
                return false;
            }
            
            if (DEBUG_MODE) {
                System.out.println("[INIT] Initializing AprilTag processor...");
            }
            
            // Initialize AprilTag processor
            aprilTagProcessor = new AprilTagProcessor(APRILTAG_DECIMATION);
            if (!aprilTagProcessor.initialize()) {
                System.err.println("[ERROR] Failed to initialize AprilTag processor");
                return false;
            }
            
            if (DEBUG_MODE) {
                System.out.println("[INIT] Initializing display manager...");
            }
            
            // Initialize display
            displayManager = new DisplayManager(CAMERA_WIDTH, CAMERA_HEIGHT);
            if (!displayManager.initialize()) {
                System.err.println("[ERROR] Failed to initialize display");
                return false;
            }
            
            if (DEBUG_MODE) {
                System.out.println("[INIT] All components initialized successfully");
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ============================================================================
    // MAIN EXECUTION METHODS
    // ============================================================================
    
    /**
     * Start the AprilTag scanner
     * This method runs the main detection loop
     */
    public void start() {
        if (DEBUG_MODE) {
            System.out.println("[START] AprilTag scanner starting...");
        }
        
        isRunning = true;
        mainLoop();
    }
    
    /**
     * Main detection loop
     * Continuously captures frames, processes them, and displays results
     */
    private void mainLoop() {
        System.out.println("\n========================================");
        System.out.println("AprilTag Webcam Scanner - RUNNING");
        System.out.println("========================================");
        System.out.println("Press 'Q' in the display window to quit");
        System.out.println("========================================\n");
        
        while (isRunning) {
            try {
                long frameStartTime = System.currentTimeMillis();
                
                // Check if max runtime exceeded
                if (MAX_RUNTIME_SECONDS > 0) {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    if (elapsedSeconds >= MAX_RUNTIME_SECONDS) {
                        if (DEBUG_MODE) {
                            System.out.println("[MAIN] Max runtime reached");
                        }
                        isRunning = false;
                        break;
                    }
                }
                
                // Capture frame from camera
                byte[] frameData = cameraManager.captureFrame();
                if (frameData == null) {
                    if (DEBUG_MODE) {
                        System.out.println("[WARN] Failed to capture frame");
                    }
                    continue;
                }
                
                // Process frame with AprilTag detector
                currentDetections = aprilTagProcessor.processFrame(frameData);
                
                // Update statistics
                frameCount++;
                if (currentDetections.size() > 0) {
                    totalDetections += currentDetections.size();
                    displayDetections();
                }
                
                // Display frame with detections
                displayManager.displayFrame(frameData, currentDetections);
                
                // Check for user input (quit command)
                if (displayManager.shouldQuit()) {
                    isRunning = false;
                }
                
                // Maintain target frame rate
                long frameProcessTime = System.currentTimeMillis() - frameStartTime;
                long sleepTime = UPDATE_INTERVAL_MS - frameProcessTime;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isRunning = false;
            } catch (Exception e) {
                System.err.println("[ERROR] Error in main loop: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        shutdown();
    }
    
    // ============================================================================
    // DETECTION DISPLAY METHODS
    // ============================================================================
    
    /**
     * Display information about detected AprilTags
     */
    private void displayDetections() {
        System.out.println("\n--- AprilTag Detection Results ---");
        System.out.println("Detected: " + currentDetections.size() + " tag(s)");
        
        for (int i = 0; i < currentDetections.size(); i++) {
            AprilTagDetection detection = currentDetections.get(i);
            System.out.println("\n[Tag " + (i + 1) + "]");
            System.out.println("  ID: " + detection.getId());
            System.out.println("  Position (inches):");
            System.out.println("    X (Right):   " + String.format("%.2f", detection.getX()));
            System.out.println("    Y (Forward): " + String.format("%.2f", detection.getY()));
            System.out.println("    Z (Up):      " + String.format("%.2f", detection.getZ()));
            System.out.println("  Orientation (degrees):");
            System.out.println("    Pitch: " + String.format("%.2f", detection.getPitch()));
            System.out.println("    Roll:  " + String.format("%.2f", detection.getRoll()));
            System.out.println("    Yaw:   " + String.format("%.2f", detection.getYaw()));
            System.out.println("  Distance: " + String.format("%.2f", detection.getRange()) + " inches");
            System.out.println("  Bearing:  " + String.format("%.2f", detection.getBearing()) + "°");
            System.out.println("  Elevation: " + String.format("%.2f", detection.getElevation()) + "°");
            System.out.println("  Confidence: " + String.format("%.2f", detection.getConfidence()));
        }
        
        System.out.println("----------------------------------\n");
    }
    
    // ============================================================================
    // STATISTICS METHODS
    // ============================================================================
    
    /**
     * Display performance statistics
     */
    private void displayStatistics() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        double elapsedSeconds = elapsedTime / 1000.0;
        double fps = frameCount / elapsedSeconds;
        
        System.out.println("\n========================================");
        System.out.println("Performance Statistics");
        System.out.println("========================================");
        System.out.println("Frames Processed: " + frameCount);
        System.out.println("Average FPS: " + String.format("%.2f", fps));
        System.out.println("Total Detections: " + totalDetections);
        System.out.println("Elapsed Time: " + String.format("%.2f", elapsedSeconds) + " seconds");
        if (frameCount > 0) {
            System.out.println("Average Tags/Frame: " + String.format("%.2f", (double) totalDetections / frameCount));
        }
        System.out.println("========================================\n");
    }
    
    // ============================================================================
    // SHUTDOWN METHODS
    // ============================================================================
    
    /**
     * Shutdown the scanner and clean up resources
     */
    private void shutdown() {
        System.out.println("\n[SHUTDOWN] Shutting down AprilTag scanner...");
        
        displayStatistics();
        
        if (displayManager != null) {
            displayManager.close();
        }
        
        if (cameraManager != null) {
            cameraManager.close();
        }
        
        if (aprilTagProcessor != null) {
            aprilTagProcessor.close();
        }
        
        System.out.println("[SHUTDOWN] AprilTag scanner stopped");
    }
    
    // ============================================================================
    // MAIN ENTRY POINT
    // ============================================================================
    
    /**
     * Main method - Entry point for the application
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("AprilTag Webcam Scanner v1.0");
        System.out.println("========================================\n");
        
        // Create scanner instance
        AprilTagWebcamScanner scanner = new AprilTagWebcamScanner();
        
        // Initialize all components
        if (!scanner.initialize()) {
            System.err.println("[ERROR] Failed to initialize scanner");
            System.exit(1);
        }
        
        // Start the scanner
        scanner.start();
        
        System.out.println("[MAIN] Application terminated");
        System.exit(0);
    }
}
