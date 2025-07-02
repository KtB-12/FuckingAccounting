	package application;
	
	public class SidebarState {
	    private static boolean minimized = false;
	
	    public static boolean isMinimized() {
	        return minimized;
	    }
	
	    public static void setMinimized(boolean value) {
	        minimized = value;
	    }
	}