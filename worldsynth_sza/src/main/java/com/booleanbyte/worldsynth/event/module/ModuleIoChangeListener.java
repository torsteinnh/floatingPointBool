package com.booleanbyte.worldsynth.event.module;

import java.util.EventListener;

public interface ModuleIoChangeListener extends EventListener {
	
	public void ioChanged(ModuleIoChangeEvent e);
	
}
