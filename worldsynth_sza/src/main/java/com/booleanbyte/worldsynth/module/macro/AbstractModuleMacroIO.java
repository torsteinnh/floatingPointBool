package com.booleanbyte.worldsynth.module.macro;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.ModuleMacro;

public abstract class AbstractModuleMacroIO extends AbstractModule {
	
	private ModuleMacro macroModule;
	
	private static int nextMacroIoId = 0;
	protected int macroIoId = -1;
	
	@Override
	protected void preInit() {
		if(macroIoId == -1) {
			macroIoId = nextMacroIoId();
		}
	}
	
	@Override
	public boolean isBypassable() {
		return false;
	}

	@Override
	public ActionListener moduleUI(JPanel uiPanel) {
		return null;
	}

	@Override
	public ArrayList<Element> toElementList() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("macroioid", String.valueOf(macroIoId)));
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("macroioid")) {
				macroIoId = Integer.parseInt(e.content);
				if(macroIoId >= nextMacroIoId) {
					nextMacroIoId = macroIoId + 1;
				}
			}
		}
		reregisterIO();
	}
	
	public void setParentMacroModule(ModuleMacro macroModule) {
		this.macroModule = macroModule;
	}
	
	protected ModuleMacro getMacroModule() {
		return macroModule;
	}
	
	private int nextMacroIoId() {
		return nextMacroIoId++;
	}
}
