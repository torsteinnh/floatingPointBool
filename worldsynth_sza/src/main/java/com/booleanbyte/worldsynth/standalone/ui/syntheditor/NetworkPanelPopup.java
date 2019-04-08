package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.ProjectReader;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.AbstractModuleRegister.ModuleEntry;
import com.booleanbyte.worldsynth.module.ModuleMacro;

public class NetworkPanelPopup extends JPopupMenu {
	private static final long serialVersionUID = -7887789083971125859L;
	
	private int x;
	private int y;
	private SynthEditorPanel synthEditor;
	
	public NetworkPanelPopup(int x, int y, SynthEditorPanel synthEditor) {
		
		this.x = x;
		this.y = y;
		this.synthEditor = synthEditor;
		
		DeviceMenuStructure structure = new DeviceMenuStructure("root");
		
		//Synth
		add(new SynthImportMenu());
		addSeparator();
		
		//Build the module menu structure
		for(ModuleEntry moduleEntry: WorldSynthCore.moduleRegister.getRegisteredModuleEntries()) {
			structure.addModuleToStructure(moduleEntry);
		}
		
		//Convert the module menu structure to JMenu
		for(DeviceMenuStructure item: structure.subStructures) {
			JMenuItem newRootItem = convertStructureToJMenuItem(item);
			add(newRootItem);
		}
	}
	
	private JMenuItem convertStructureToJMenuItem(DeviceMenuStructure structure) {
		JMenuItem item;
		
		//If item is a module
		if(structure.module != null) {
			item = new JMenuItem(structure.itemName);
			item.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Coordinate newDeviceCoord = new Coordinate(new Pixel(x, y), synthEditor);
						AbstractModule moduleInstance = WorldSynthCore.constructModule(structure.module);
						Device newDevice = new Device(moduleInstance, newDeviceCoord.x, newDeviceCoord.y);
						synthEditor.setTempDevice(newDevice);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			return item;
		}
		
		//Else item is a parent item
		else {
			item = new JMenu(structure.itemName);
			for(DeviceMenuStructure subStructure: structure.subStructures) {
				JMenuItem subItem = convertStructureToJMenuItem(subStructure);
				item.add(subItem);
			}
			return item;
		}
	}
	
	private class DeviceMenuStructure {
		ArrayList<DeviceMenuStructure> subStructures = new ArrayList<DeviceMenuStructure>();
		
		Class<? extends AbstractModule> module = null;
		String itemName = null;
		
		public DeviceMenuStructure(String itemName) {
			this.itemName = itemName;
		}

		public DeviceMenuStructure(Class<? extends AbstractModule> module, String moduleName) {
			this.module = module;
			this.itemName = moduleName;
		}
		
		private void addModuleToStructure(Class<? extends AbstractModule> moduleClass, String menuPath, String moduleName) {
			if(menuPath.startsWith("\\")) {
				menuPath = menuPath.replaceFirst("\\\\", "");
				String subStructureName = menuPath.split("\\\\", 2)[0];
				menuPath = menuPath.replaceFirst(subStructureName, "");
				
				for(DeviceMenuStructure subStructure: subStructures) {
					if(subStructure.itemName.equals(subStructureName)) {
						subStructure.addModuleToStructure(moduleClass, menuPath, moduleName);
						return;
					}
				}
				
				DeviceMenuStructure newSubItem = new DeviceMenuStructure(subStructureName);
				newSubItem.addModuleToStructure(moduleClass, menuPath, moduleName);
				subStructures.add(newSubItem);
				return;
			}
			else {
				subStructures.add(new DeviceMenuStructure(moduleClass, moduleName));
			}
		}
		
		public void addModuleToStructure(ModuleEntry entry) {
			String menuPath = entry.getModuleMenuPath();
			String moduleName = entry.getModuleName();
			Class<? extends AbstractModule> moduleClass = entry.getModuleClass();
			
			addModuleToStructure(moduleClass, menuPath, moduleName);
		}
	}
	
	private class SynthImportMenu extends JMenu {
		private static final long serialVersionUID = 484197825619603011L;

		public SynthImportMenu() {
			super("Synth import");
			
			add(new BlueprintItem());
			add(new MacroItem());
		}
	}
	
	private class BlueprintItem extends JMenuItem {
		private static final long serialVersionUID = 5854176349249897925L;
		
		public BlueprintItem() {
			super("Blueprint");
			
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					File synthFile = getSynthFile();
					if(synthFile == null) {
						return;
					}
					else if(!synthFile.exists()) {
						return;
					}
					
					Synth blueprintSynth = ProjectReader.readSynthFromFile(synthFile);
					synthEditor.setTempSynth(new TempSynth(blueprintSynth.toElement()));
				}
			});
		}
	}
	
	private class MacroItem extends JMenuItem {
		private static final long serialVersionUID = 2353601063774708005L;

		public MacroItem() {
			super("Macro");
			
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					File synthFile = getSynthFile();
					if(synthFile == null) {
						return;
					}
					else if(!synthFile.exists()) {
						return;
					}
					
					Synth macroSynth = ProjectReader.readSynthFromFile(synthFile);
					Device macro = new Device(new ModuleMacro(macroSynth), 0.0f, 0.0f);
					synthEditor.setTempDevice(macro);
				}
			});
		}
	}
	
	private File getSynthFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("WorldSynth", "wsynth");
		fileChooser.setFileFilter(filter);
		
		int returnVal = fileChooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}
}