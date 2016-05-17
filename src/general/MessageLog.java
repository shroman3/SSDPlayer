package general;

import ui.TracePlayer;
import ui.breakpoints.LogView;

public class MessageLog {
	public static LogView mView;
	public static TracePlayer mTracePlayer;
	
	public static void initialize(LogView logView, TracePlayer tracePlayer) {
		mView = logView;
		mTracePlayer = tracePlayer;
	}
	
	public static void log(String message) {
		mView.log(message);
	}
	
	public static void logAndPause(String message) {
		if (!mTracePlayer.isPaused()) {
			log(message);
			mTracePlayer.pauseTrace();   
		}
	}
}
