package com.booleanbyte.worldsynth.standalone.ui.syntheditor.history;

import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.Element;

public class EditorHistoryEventModuleEdit extends AbstractEditorHistoryEvent {
	
	private Device subject;
	private Element oldParameterElemetn;
	private Element newParameterElement;
	
	public EditorHistoryEventModuleEdit(Device subjects, Element oldParameterElement, Element newParameterElement) {
		this.subject = subjects;
		this.oldParameterElemetn = oldParameterElement;
		this.newParameterElement = newParameterElement;
	}
	
	public Device getSubject() {
		return subject;
	}
	
	public Element getOldParameterElement() {
		return oldParameterElemetn;
	}
	
	public Element getNewParameterElement() {
		return newParameterElement;
	}
}
