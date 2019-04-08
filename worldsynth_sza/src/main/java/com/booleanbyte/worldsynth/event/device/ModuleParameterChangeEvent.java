package com.booleanbyte.worldsynth.event.device;

import java.util.EventObject;

import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.Element;

public class ModuleParameterChangeEvent extends EventObject {
	private static final long serialVersionUID = 3628269900944374852L;
	
	private Device subject;
	private Element oldParameterElement;
	private Element newParameterElement;

	public ModuleParameterChangeEvent(Device subject, Element oldParameterElement, Element newParameterElement, Object source) {
		super(source);
		this.oldParameterElement = oldParameterElement;
		this.newParameterElement = newParameterElement;
	}
	
	public Element getOldParameterElement() {
		return oldParameterElement;
	}
	
	public Element getNewParameterElement() {
		return newParameterElement;
	}
	
	public Device getSubject() {
		return subject;
	}
}
