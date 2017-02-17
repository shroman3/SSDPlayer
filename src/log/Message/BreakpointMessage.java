package log.Message;

import java.awt.Color;

public class BreakpointMessage extends Message {
	public BreakpointMessage(String text) {
		super(text, Color.yellow, "HIT");
	}
}
