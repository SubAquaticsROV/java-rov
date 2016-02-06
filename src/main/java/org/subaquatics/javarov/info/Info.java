package org.subaquatics.javarov;

public interface Info {

	public Type getType();
	public String getHumanReadableString();

	public enum Type {
		MESSAGE;
	}
}