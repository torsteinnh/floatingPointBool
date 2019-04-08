package com.booleanbyte.worldsynth.event.device;

import java.util.EventListener;

public interface ModuleParameterChangeListener extends EventListener {
	
	public void parametersChanged(ModuleParameterChangeEvent e);
	
}
