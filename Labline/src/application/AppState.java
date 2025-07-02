package application;

public class AppState {
    // Private static instance of the class
    private static AppState instance;
    
    // State variables
    private boolean isSidebarMinimized = false;
    private double sidebarWidth = 260;
    private double homePaneWidth = 230;
    
    // Private constructor to prevent instantiation
    private AppState() {}
    
    // Public method to get the single instance
    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }
    
    // Getters and setters
    public boolean isSidebarMinimized() {
        return isSidebarMinimized;
    }
    
    public void setSidebarMinimized(boolean minimized) {
        this.isSidebarMinimized = minimized;
        // Update dependent values
        if (minimized) {
            sidebarWidth = 115;
            homePaneWidth = 80;
        } else {
            sidebarWidth = 260;
            homePaneWidth = 230;
        }
    }
    
    public double getSidebarWidth() {
        return sidebarWidth;
    }
    
    public double getHomePaneWidth() {
        return homePaneWidth;
    }
}