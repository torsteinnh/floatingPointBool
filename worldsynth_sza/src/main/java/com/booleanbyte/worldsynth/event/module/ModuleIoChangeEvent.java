package com.booleanbyte.worldsynth.event.module;

import java.util.EventObject;

public class ModuleIoChangeEvent extends EventObject {
	private static final long serialVersionUID = 3477520059797146016L;
	
	private String message;

	public ModuleIoChangeEvent(String message, Object source) {
		super(source);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
