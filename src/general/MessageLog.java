package general;

import log.Message.Message;
import ui.LogView;
import ui.TracePlayer;

import java.util.HashSet;
import java.util.Set;

public class MessageLog {
	public static LogView mView;
	public static TracePlayer mTracePlayer = null;
	public static Set<String> loggedMessages;

	public static void initialize(LogView logView) {
		mView = logView;
		loggedMessages = new HashSet<>();
	}

	public static void clearLoggedMessages(){
		loggedMessages = new HashSet<>();
	}

	public static void setTracePlayer(TracePlayer tracePlayer) {
		mTracePlayer = tracePlayer;
	}

	public static void log(Message message) {
		if (mView != null) {
			mView.log(message);
		} else {
			System.out.println(message.getText());
		}
	}

	public static void logOnce(Message message){
		if (!loggedMessages.contains(message.getText())){
			loggedMessages.add(message.getText());
			log(message);
		}
	}

	public static void logAndPause(Message message) {
		if ((mTracePlayer != null) && (!mTracePlayer.isPaused())) {
			mTracePlayer.pauseTrace();
		}
		log(message);
	}

	public static void logAndAbort(Message message) {
		if (mTracePlayer != null) {
			mTracePlayer.abort();
		}
		log(message);
	}
}
