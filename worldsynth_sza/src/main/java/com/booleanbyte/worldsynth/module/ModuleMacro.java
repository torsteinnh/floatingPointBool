package com.booleanbyte.worldsynth.module;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.event.synth.SynthEvent;
import com.booleanbyte.worldsynth.event.synth.SynthListener;
import com.booleanbyte.worldsynth.module.macro.AbstractModuleMacroEntry;

public class ModuleMacro extends AbstractModule {
	
	private ModuleMacro instance = this;
	
	private Synth macroSynth;
	private SynthChangeListener listener;
	
	File synthFile = null;
	
	int ioDeviceId = 0;
	
	Device[] entryDevices;
	Device[] exitDevices;
	
	public ModuleMacro() {
		setMacroSynth(new Synth("New macro synth"));
	}
	
	public ModuleMacro(Synth macroSynth) {
		setMacroSynth(macroSynth);
	}

	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		ModuleOutput exitOutput = request.output;
		Device exitDevice = null;
		for(int i = 0; i < exitDevices.length; i++) {
			if(exitDevices[i].module.outputs[0] == exitOutput) {
				exitDevice = exitDevices[i];
				break;
			}
		}
		if(exitDevice == null) {
			return null;
		}
		
		return WorldSynthCore.getDeviceOutput(macroSynth, exitDevice, request);
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		return inputRequests;
	}

	@Override
	public String getModuleName() {
		return "Macro";
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.MACRO;
	}

	@Override
	public ModuleInput[] registerInputs() {
		if(entryDevices == null) {
			entryDevices = new Device[0];
			return null;
		}
		if(entryDevices.length == 0) {
			return null;
		}
		ModuleInput[] in = new ModuleInput[entryDevices.length];
		for(int i = 0; i < entryDevices.length; i++) {
			in[i] = entryDevices[i].module.inputs[0];
		}
		return in;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		if(exitDevices == null) {
			exitDevices = new Device[0];
		}
		if(exitDevices.length == 0) {
			return null;
		}
		ModuleOutput[] out = new ModuleOutput[exitDevices.length];
		for(int i = 0; i < exitDevices.length; i++) {
			out[i] = exitDevices[i].module.outputs[0];
		}
		return out;
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
		
		paramenterElements.add(macroSynth.toElement());
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("synth")) {
				setMacroSynth(new Synth(e));
			}
		}
	}
	
	private void setMacroSynth(Synth synth) {
		if(macroSynth != null) {
			macroSynth.removeSynthListener(listener);
		}
		
		macroSynth = synth;
		
		ArrayList<Device> tempEntryDevices = new ArrayList<Device>();
		ArrayList<Device> tempExitDevices = new ArrayList<Device>();
		
		for(Device d: synth.getDeviceList()) {
			if(d.module.getModuleClass() == ModuleClass.MACRO_ENTRY) {
				tempEntryDevices.add(d);
				((AbstractModuleMacroEntry)d.module).setParentMacroModule(this);
			}
			else if(d.module.getModuleClass() == ModuleClass.MACRO_EXIT) {
				tempExitDevices.add(d);
			}
		}
		
		entryDevices = new Device[tempEntryDevices.size()];
		if(tempEntryDevices.size() > 0) {
			entryDevices = tempEntryDevices.toArray(entryDevices);
		}
		
		exitDevices = new Device[tempExitDevices.size()];
		if(tempExitDevices.size() > 0) {
			exitDevices = tempExitDevices.toArray(exitDevices);
		}
		
		reregisterIO();
		
		listener = new SynthChangeListener();
		macroSynth.addSynthListerner(listener);
	}
	
	public Synth getMacroSynth() {
		return macroSynth;
	}
	
	private class SynthChangeListener implements SynthListener {
		@Override
		public void deviceRemoved(SynthEvent event) {
			if(event.getDevice().module.getModuleClass() == ModuleClass.MACRO_ENTRY) {
				Device[] newEntryDevices = new Device[entryDevices.length-1];
				
				int j = 0;
				for(int i = 0; i < entryDevices.length; i++) {
					Device d = entryDevices[i];
					if(d == event.getDevice()) {
						continue;
					}
					newEntryDevices[j] = d;
					j++;
				}
				
				entryDevices = newEntryDevices;
				reregisterIO();
			}
			else if(event.getDevice().module.getModuleClass() == ModuleClass.MACRO_EXIT) {
				Device[] newExitDevices = new Device[exitDevices.length-1];
				
				int j = 0;
				for(int i = 0; i < exitDevices.length; i++) {
					Device d = exitDevices[i];
					if(d == event.getDevice()) {
						continue;
					}
					newExitDevices[j] = d;
					j++;
				}
				
				exitDevices = newExitDevices;
				reregisterIO();
			}
		}
		
		@Override
		public void deviceModified(SynthEvent event) {
		}
		
		@Override
		public void deviceAdded(SynthEvent event) {
			if(event.getDevice().module.getModuleClass() == ModuleClass.MACRO_ENTRY) {
				int currentEntryDeviceCount = 0;
				if(entryDevices != null) {
					currentEntryDeviceCount = entryDevices.length;
				}
				Device[] newEntryDevices = new Device[currentEntryDeviceCount+1];
				
				for(int i = 0; i < currentEntryDeviceCount; i++) {
					newEntryDevices[i] = entryDevices[i];
				}
				((AbstractModuleMacroEntry) event.getDevice().module).setParentMacroModule(instance);
				newEntryDevices[currentEntryDeviceCount] = event.getDevice();
				
				entryDevices = newEntryDevices;
				reregisterIO();
			}
			else if(event.getDevice().module.getModuleClass() == ModuleClass.MACRO_EXIT) {
				int currentExitDeviceCount = 0;
				if(exitDevices != null) {
					currentExitDeviceCount = exitDevices.length;
				}
				Device[] newExitDevices = new Device[currentExitDeviceCount+1];
				
				for(int i = 0; i < currentExitDeviceCount; i++) {
					newExitDevices[i] = exitDevices[i];
				}
				newExitDevices[currentExitDeviceCount] = event.getDevice();
				
				exitDevices = newExitDevices;
				reregisterIO();
			}
		}
	}
	
	public AbstractDatatype buildMacroInput(InputRequest request) {
		return buildInputData(request);
	}
}
