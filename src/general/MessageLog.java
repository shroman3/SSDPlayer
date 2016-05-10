package general;

import ui.breakpoints.LogView;

public class MessageLog {
	public static LogView view;
	
	public static void initialize(LogView logView) {
		view = logView;
	}
	
	public static void log(String message) {
		view.log(message);
	}
}
