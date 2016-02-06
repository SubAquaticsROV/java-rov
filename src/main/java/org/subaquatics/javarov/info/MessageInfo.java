package org.subaquatics.javarov.info;

public class MessageInfo implements Info {

	private String message;

	public MessageInfo(String message) {
		this.message = message;
	}

	public Type getType() {
		return Type.MESSAGE;
	}

	public String getHumanReadableString() {
		return message;
	}

}