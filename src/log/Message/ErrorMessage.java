package log.Message;

import java.awt.Color;

public class ErrorMessage extends Message {
	public ErrorMessage(String text) {
		super(text, Color.red, "Error");
	}
}
