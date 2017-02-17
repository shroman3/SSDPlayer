package general;

import log.Message.Message;
import ui.LogView;
import ui.TracePlayer;

public class MessageLog {
	public static LogView mView;
	public static TracePlayer mTracePlayer = null;

	public static void initialize(LogView logView) {
		mView = logView;
	}

	public static void setTracePlayer(TracePlayer tracePlayer) {
		mTracePlayer = tracePlayer;
	}

	public static void log(Message message) {
		mView.log(message);
	}

	public static void logAndPause(Message message) {
		if ((mTracePlayer != null) && (!mTracePlayer.isPaused())) {
			mTracePlayer.pauseTrace();
		}
		log(message);
	}
}
