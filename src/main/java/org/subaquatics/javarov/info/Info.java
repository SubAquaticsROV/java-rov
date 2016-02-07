package org.subaquatics.javarov.info;

public interface Info {

	public Type getType();
	public String getHumanReadableString();

	public enum Type {
		MESSAGE;
	}
}