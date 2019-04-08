package com.booleanbyte.worldsynth.standalone.ui.syntheditor.history;

import com.booleanbyte.worldsynth.standalone.ui.syntheditor.Coordinate;

public class EditorHistoryEventModuleMove extends AbstractEditorHistoryEvent {
	
	private Object[] movedSubjects;
	private Coordinate[] oldCoordinates;
	private Coordinate[] newCoordinates;
	
	public EditorHistoryEventModuleMove(Object[] movesSubjects, Coordinate[] oldCoordinates, Coordinate[] newCoordinates) {
		this.movedSubjects = movesSubjects;
		this.oldCoordinates = oldCoordinates;
		this.newCoordinates = newCoordinates;
	}
	
	public Object[] getMovedSubjects() {
		return movedSubjects;
	}
	
	public Coordinate[] getOldCoordinates() {
		return oldCoordinates;
	}
	
	public Coordinate[] getNewCoordinates() {
		return newCoordinates;
	}
	
	public void setOldCoordinates(Coordinate[] oldCoordinates) {
		this.oldCoordinates = oldCoordinates;
	}
	
	public void setNewCoordinates(Coordinate[] newCoordinates) {
		this.newCoordinates = newCoordinates;
	}
}
