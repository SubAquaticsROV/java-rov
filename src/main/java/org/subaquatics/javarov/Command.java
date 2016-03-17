package org.subaquatics.javarov;

public class Command {

	private String name;
	private String parameters;
	private String description;
	private CommandExecutor executor;

	public Command(String name, String parameters, String description, CommandExecutor executor) {
		this.name = name;
		this.parameters = parameters;
		this.description = description;
		this.executor = executor;
	}

	public boolean execute(String arg) {
		return executor.execute(arg);
	}

	public String getName() {
		return name;
	}

	public String getParameters() {
		return parameters;
	}

	public String getDescription() {
		return description;
	}

	public static interface CommandExecutor {
		public boolean execute(String arg);
	}
}