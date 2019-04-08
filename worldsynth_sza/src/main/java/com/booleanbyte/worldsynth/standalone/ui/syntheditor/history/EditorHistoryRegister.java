package com.booleanbyte.worldsynth.standalone.ui.syntheditor.history;

import java.util.ArrayList;

public class EditorHistoryRegister {
	
	private ArrayList<AbstractEditorHistoryEvent> history = new ArrayList<AbstractEditorHistoryEvent>();
	private int historyLenght;
	private int historyIndex = -1;
	
	public EditorHistoryRegister(int historyLenght) {
		this.historyLenght = historyLenght;
	}
	
	public void addHistoryEvent(AbstractEditorHistoryEvent event) {
		//If new history entry is added while not at the end of the history, remove old branch from history and create new branch
		while(historyIndex < history.size()-1) {
			history.remove(history.size()-1);
		}
		
		history.add(event);
		historyIndex++;
		
		//If history gets longer than the specified history length, remove oldest history entry
		if(history.size() > historyLenght) {
			history.remove(0);
			historyIndex--;
		}
	}
	
	public AbstractEditorHistoryEvent getLastHistoryEvent() {
		if(historyIndex < 0) {
			return null;
		}
		return history.get(historyIndex--);
	}
	
	public AbstractEditorHistoryEvent getNextHistoryEvent() {
		if(historyIndex+1 > history.size()-1) {
			return null;
		}
		return history.get(++historyIndex);
	}
}
