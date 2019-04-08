package com.booleanbyte.worldsynth.module;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.WorldSynthCore;

public abstract class AbstractModuleRegister {
	
	private ArrayList<ModuleEntry> moduleList;
	
	public AbstractModuleRegister() {
		moduleList = new ArrayList<ModuleEntry>();
	}
	
	/**
	 * Used to register a new module.<br>
	 * Modules are registered as class objects as the modules are added in devices
	 * using reflection.<br>
	 * The menupath defines where in the device menu it will be found, the format of the path
	 * uses "\\" to define a new layer in the menu.<br>
	 * Ex: "\\Heightmap\\Generator" places the module the submenu "Generator" of the menu "Heightmap"
	 * in the root menu.<br>
	 * ROOT -> Heightmap -> Generator -> [name of the module]
	 * @param module The class object of the module to register
	 * @param moduleMenuPath The menupath to place the module in
	 * @throws ClassNotModuleExeption If the class is not an extension of {@link AbstractModule}
	 */
	public void registerModule(Class<? extends AbstractModule> moduleClass, String moduleMenuPath) throws ClassNotModuleExeption {
		if(AbstractModule.class.isAssignableFrom(moduleClass) && !moduleClass.equals(AbstractModule.class)) {
			AbstractModule moduleInstance;
			try {
				moduleInstance = WorldSynthCore.constructModule(moduleClass);
				String moduleName = moduleInstance.getModuleName();
				moduleList.add(new ModuleEntry(moduleClass, moduleMenuPath, moduleName));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
				e.printStackTrace();
				//TODO Exception could not register device
			}
		}
		else {
			throw new ClassNotModuleExeption("Tried to register invalid module " + moduleClass.toString());
		}
	}
	
	public void registerModule(ModuleEntry moduleEntry) throws ClassNotModuleExeption {
		moduleList.add(moduleEntry);
	}
	
	/**
	 * Gets an {@link ArrayList} of the module entries 
	 * @return
	 */
	public ArrayList<ModuleEntry> getRegisteredModuleEntries() {
		return moduleList;
	}
	
	/**
	 * Gets the menu path string for the module. The menupath defines where in the device menu
	 * used in the network editor the module will be placed.
	 * @param moduleClass to get the menupath for
	 * @return The menupath as a String
	 */
	public String getMenuPath(Class<? extends AbstractModule> moduleClass) {
		for(int i = 0; i < moduleList.size(); i++) {
			if(moduleList.get(i).getModuleClass().equals(moduleClass)) {
				return moduleList.get(i).getModuleMenuPath();
			}
		}
		return null;
	}
	
	public class ModuleEntry {
		private Class<? extends AbstractModule> moduleClass;
		private String moduleMenuPath;
		private String moduleName;
		
		public ModuleEntry(Class<? extends AbstractModule> moduleClass, String moduleMenuPath, String moduleName) {
			this.moduleClass = moduleClass;
			this.moduleMenuPath = moduleMenuPath;
			this.moduleName = moduleName;
		}
		
		public Class<? extends AbstractModule> getModuleClass() {
			return moduleClass;
		}
		
		public String getModuleMenuPath() {
			return moduleMenuPath;
		}
		
		public String getModuleName() {
			return moduleName;
		}
		
		@Override
		public String toString() {
			return moduleName + "   [" + moduleMenuPath + "]";
		}
	}
}
