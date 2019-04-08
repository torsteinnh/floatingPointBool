package com.booleanbyte.worldsynth.standalone.ui.syntheditor.history;

public class EditorHistoryEventModuleAddRemove extends AbstractEditorHistoryEvent {
	
	private Object[] addedSubjects;
	private Object[] removedSubjects;
	
	public EditorHistoryEventModuleAddRemove(Object[] addedSubjects, Object[] removedSubjects) {
		this.addedSubjects = addedSubjects;
		this.removedSubjects = removedSubjects;
	}
	
	public Object[] getAddedSubjects() {
		return addedSubjects;
	}
	
	public Object[] getRemovedSubjects() {
		return removedSubjects;
	}
}
