package log.Message;

import java.awt.Color;

public abstract class Message {
	private String mText;
	private Color mColor;
	private String mTypeDesc;

	public Message(String text, Color color, String typeDesc) {
		this.mText = text;
		this.mColor = color;
		this.mTypeDesc = typeDesc;
	}

	public String getText() {
		return this.mTypeDesc + ": " + this.mText;
	}

	public Color getColor() {
		return this.mColor;
	}

	@Override
	public int hashCode(){
		return mText.hashCode();
	}
}
