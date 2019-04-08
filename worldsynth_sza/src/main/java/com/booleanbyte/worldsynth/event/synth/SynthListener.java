package com.booleanbyte.worldsynth.event.synth;

import java.util.EventListener;

public interface SynthListener extends EventListener {
	
	public void deviceAdded(SynthEvent event);
	public void deviceRemoved(SynthEvent event);
	public void deviceModified(SynthEvent event);
}
