/**
 * CameraManager.java
 * 
 * Manages webcam initialization and frame capture.
 * Handles camera device selection, resolution, frame rate, and error handling.
 * 
 * This class abstracts the complexity of camera management and provides
 * a simple interface for capturing frames.
 */

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages webcam operations including initialization and frame capture
 */
public class CameraManager {
    
    // ============================================================================
    // CONFIGURATION CONSTANTS
    // ============================================================================
    
    /** Use external USB webcam (true) or built-in camera (false) */
    private boolean useWebcam;
    
    /** Camera resolution width in pixels */
    private int cameraWidth;
    
    /** Camera resolution height in pixels */
    private int cameraHeight;
    
    /** Target frames per second */
    private int targetFPS;
    
    /** Frame buffer size (number of frames to buffer) */
    private static final int FRAME_BUFFER_SIZE = 3;
    
    /** Timeout for frame capture in milliseconds */
    private static final long CAPTURE_TIMEOUT_MS = 3000;
    
    /** Enable debug output */
    private static final boolean DEBUG = true;
    
    // ============================================================================
    // INSTANCE VARIABLES
    // ============================================================================
    
    /** Camera device object (would be actual camera in real implementation) */
    private Object cameraDevice;
    
    /** Frame buffer queue */
    private Queue<byte[]> frameBuffer;
    
    /** Flag indicating if camera is initialized */
    private boolean isInitialized;
    
    /** Flag indicating if camera is running */
    private boolean isRunning;
    
    /** Frame capture thread */
    private Thread captureThread;
    
    /** Statistics */
    private long framesCapture;
    private long framesCaptured;
    private long lastFrameTime;
    
    // ============================================================================
    // CONSTRUCTOR
    // ============================================================================
    
    /**
     * Create a new CameraManager
     * 
     * @param useWebcam true to use external webcam, false for built-in camera
     * @param width Camera resolution width
     * @param height Camera resolution height
     * @param fps Target frames per second
     */
    public CameraManager(boolean useWebcam, int width, int height, int fps) {
        this.useWebcam = useWebcam;
        this.cameraWidth = width;
        this.cameraHeight = height;
        this.targetFPS = fps;
        this.frameBuffer = new LinkedList<>();
        this.isInitialized = false;
        this.isRunning = false;
        this.framesCapture = 0;
        this.framesCaptured = 0;
        this.lastFrameTime = System.currentTimeMillis();
        
        if (DEBUG) {
            System.out.println("[CameraManager] Created with resolution " + width + "x" + height + " @ " + fps + " FPS");
        }
    }
    
    // ============================================================================
    // INITIALIZATION METHODS
    // ============================================================================
    
    /**
     * Initialize the camera
     * 
     * @return true if initialization successful, false otherwise
     */
    public boolean initialize() {
        try {
            if (DEBUG) {
                System.out.println("[CameraManager] Initializing camera...");
            }
            
            // In a real implementation, this would initialize the actual camera
            // For now, we'll simulate it
            if (!initializeCamera()) {
                System.err.println("[CameraManager] Failed to initialize camera device");
                return false;
            }
            
            // Start frame capture thread
            startCaptureThread();
            
            isInitialized = true;
            
            if (DEBUG) {
                System.out.println("[CameraManager] Camera initialized successfully");
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("[CameraManager] Initialization error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Initialize the actual camera device
     * 
     * @return true if successful
     */
    private boolean initializeCamera() {
        try {
            if (DEBUG) {
                System.out.println("[CameraManager] Initializing camera device...");
                System.out.println("  - Using: " + (useWebcam ? "External Webcam" : "Built-in Camera"));
                System.out.println("  - Resolution: " + cameraWidth + "x" + cameraHeight);
                System.out.println("  - FPS: " + targetFPS);
            }
            
            // In a real implementation, this would:
            // 1. Enumerate available cameras
            // 2. Select the appropriate camera
            // 3. Set resolution and frame rate
            // 4. Open the camera
            
            // For now, we simulate successful initialization
            cameraDevice = new Object(); // Placeholder
            
            return true;
            
        } catch (Exception e) {
            System.err.println("[CameraManager] Camera initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Start the frame capture thread
     */
    private void startCaptureThread() {
        isRunning = true;
        captureThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // Capture frame from camera
                    byte[] frame = captureFrameFromDevice();
                    
                    if (frame != null) {
                        // Add to buffer
                        synchronized (frameBuffer) {
                            if (frameBuffer.size() >= FRAME_BUFFER_SIZE) {
                                frameBuffer.poll(); // Remove oldest frame
                            }
                            frameBuffer.offer(frame);
                            framesCaptured++;
                        }
                    }
                    
                    // Maintain target frame rate
                    long frameTime = 1000 / targetFPS;
                    Thread.sleep(frameTime);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("[CameraManager] Error in capture thread: " + e.getMessage());
                }
            }
        });
        
        captureThread.setName("CameraCapture");
        captureThread.setDaemon(true);
        captureThread.start();
        
        if (DEBUG) {
            System.out.println("[CameraManager] Capture thread started");
        }
    }
    
    /**
     * Capture a frame from the camera device
     * 
     * @return Frame data as byte array, or null if capture failed
     */
    private byte[] captureFrameFromDevice() {
        try {
            // In a real implementation, this would:
            // 1. Request frame from camera
            // 2. Wait for frame with timeout
            // 3. Convert frame to byte array
            // 4. Return frame data
            
            // For now, we simulate frame capture
            int frameSize = cameraWidth * cameraHeight * 3; // RGB format
            byte[] frame = new byte[frameSize];
            
            // Fill with simulated data
            for (int i = 0; i < frameSize; i++) {
                frame[i] = (byte) (Math.random() * 256);
            }
            
            framesCapture++;
            lastFrameTime = System.currentTimeMillis();
            
            return frame;
            
        } catch (Exception e) {
            System.err.println("[CameraManager] Frame capture error: " + e.getMessage());
            return null;
        }
    }
    
    // ============================================================================
    // FRAME CAPTURE METHODS
    // ============================================================================
    
    /**
     * Get the next available frame
     * 
     * @return Frame data as byte array, or null if no frame available
     */
    public byte[] captureFrame() {
        if (!isInitialized || !isRunning) {
            return null;
        }
        
        try {
            synchronized (frameBuffer) {
                if (frameBuffer.isEmpty()) {
                    return null;
                }
                return frameBuffer.poll();
            }
        } catch (Exception e) {
            System.err.println("[CameraManager] Error getting frame: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get the next available frame with timeout
     * 
     * @param timeoutMs Timeout in milliseconds
     * @return Frame data as byte array, or null if timeout
     */
    public byte[] captureFrameWithTimeout(long timeoutMs) {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            byte[] frame = captureFrame();
            if (frame != null) {
                return frame;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Peek at the next frame without removing it from buffer
     * 
     * @return Frame data as byte array, or null if no frame available
     */
    public byte[] peekFrame() {
        if (!isInitialized || !isRunning) {
            return null;
        }
        
        try {
            synchronized (frameBuffer) {
                if (frameBuffer.isEmpty()) {
                    return null;
                }
                return frameBuffer.peek();
            }
        } catch (Exception e) {
            System.err.println("[CameraManager] Error peeking frame: " + e.getMessage());
            return null;
        }
    }
    
    // ============================================================================
    // CONFIGURATION METHODS
    // ============================================================================
    
    /**
     * Set camera resolution
     * 
     * @param width Resolution width
     * @param height Resolution height
     * @return true if successful
     */
    public boolean setResolution(int width, int height) {
        if (isRunning) {
            System.err.println("[CameraManager] Cannot change resolution while running");
            return false;
        }
        
        this.cameraWidth = width;
        this.cameraHeight = height;
        
        if (DEBUG) {
            System.out.println("[CameraManager] Resolution set to " + width + "x" + height);
        }
        
        return true;
    }
    
    /**
     * Set target frame rate
     * 
     * @param fps Frames per second
     * @return true if successful
     */
    public boolean setFrameRate(int fps) {
        if (isRunning) {
            System.err.println("[CameraManager] Cannot change frame rate while running");
            return false;
        }
        
        this.targetFPS = fps;
        
        if (DEBUG) {
            System.out.println("[CameraManager] Frame rate set to " + fps + " FPS");
        }
        
        return true;
    }
    
    // ============================================================================
    // STATUS METHODS
    // ============================================================================
    
    /**
     * Check if camera is initialized
     * 
     * @return true if initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Check if camera is running
     * 
     * @return true if running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Check if frames are available
     * 
     * @return true if frame buffer has frames
     */
    public boolean hasFrames() {
        synchronized (frameBuffer) {
            return !frameBuffer.isEmpty();
        }
    }
    
    /**
     * Get number of buffered frames
     * 
     * @return Number of frames in buffer
     */
    public int getBufferedFrameCount() {
        synchronized (frameBuffer) {
            return frameBuffer.size();
        }
    }
    
    /**
     * Get camera width
     * 
     * @return Camera width in pixels
     */
    public int getWidth() {
        return cameraWidth;
    }
    
    /**
     * Get camera height
     * 
     * @return Camera height in pixels
     */
    public int getHeight() {
        return cameraHeight;
    }
    
    /**
     * Get target FPS
     * 
     * @return Target frames per second
     */
    public int getTargetFPS() {
        return targetFPS;
    }
    
    // ============================================================================
    // STATISTICS METHODS
    // ============================================================================
    
    /**
     * Get total frames captured
     * 
     * @return Number of frames captured
     */
    public long getFramesCaptured() {
        return framesCaptured;
    }
    
    /**
     * Get actual frame rate
     * 
     * @return Actual frames per second
     */
    public double getActualFPS() {
        if (framesCaptured == 0) return 0;
        long elapsedMs = System.currentTimeMillis() - lastFrameTime;
        if (elapsedMs == 0) return 0;
        return (framesCaptured * 1000.0) / elapsedMs;
    }
    
    /**
     * Get camera statistics
     * 
     * @return Statistics string
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Camera Statistics:\n");
        sb.append("  Resolution: ").append(cameraWidth).append("x").append(cameraHeight).append("\n");
        sb.append("  Target FPS: ").append(targetFPS).append("\n");
        sb.append("  Actual FPS: ").append(String.format("%.2f", getActualFPS())).append("\n");
        sb.append("  Frames Captured: ").append(framesCaptured).append("\n");
        sb.append("  Buffered Frames: ").append(getBufferedFrameCount()).append("\n");
        return sb.toString();
    }
    
    // ============================================================================
    // CLEANUP METHODS
    // ============================================================================
    
    /**
     * Stop the camera and release resources
     */
    public void close() {
        if (DEBUG) {
            System.out.println("[CameraManager] Closing camera...");
        }
        
        isRunning = false;
        
        // Wait for capture thread to finish
        if (captureThread != null && captureThread.isAlive()) {
            try {
                captureThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Clear frame buffer
        synchronized (frameBuffer) {
            frameBuffer.clear();
        }
        
        isInitialized = false;
        
        if (DEBUG) {
            System.out.println("[CameraManager] Camera closed");
            System.out.println(getStatistics());
        }
    }
}
