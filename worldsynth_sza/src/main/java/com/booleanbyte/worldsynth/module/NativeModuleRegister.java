package com.booleanbyte.worldsynth.module;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.booleanbyte.worldsynth.common.Commons;
import com.booleanbyte.worldsynth.module.macro.ModuleScalarMacroEntry;
import com.booleanbyte.worldsynth.module.macro.ModuleScalarMacroExit;
import com.booleanbyte.worldsynth.module.macro.ModuleSzaMacroEntry;
import com.booleanbyte.worldsynth.module.macro.ModuleSzaMacroExit;
import com.booleanbyte.worldsynth.module.scalar.ModuleScalarClamp;
import com.booleanbyte.worldsynth.module.scalar.ModuleScalarCombiner;
import com.booleanbyte.worldsynth.module.scalar.ModuleScalarConstat;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaAnd;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaFullAdder;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaNand;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaNoop;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaNor;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaNot;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaOr;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaVariable;
import com.booleanbyte.worldsynth.module.sza.ModuleSzaXor;

public class NativeModuleRegister extends AbstractModuleRegister {
	
	public NativeModuleRegister() {
		super();
		
		try {
			//Scalars
			registerModule(ModuleScalarConstat.class, "\\Scalar");
			registerModule(ModuleScalarClamp.class, "\\Scalar");
			registerModule(ModuleScalarCombiner.class, "\\Scalar");
			
			
			//SZA
			registerModule(ModuleSzaNoop.class, "\\SZA");
			registerModule(ModuleSzaVariable.class, "\\SZA");
			
			registerModule(ModuleSzaNot.class, "\\SZA");
			registerModule(ModuleSzaAnd.class, "\\SZA");
			registerModule(ModuleSzaNand.class, "\\SZA");
			registerModule(ModuleSzaOr.class, "\\SZA");
			registerModule(ModuleSzaNor.class, "\\SZA");
			registerModule(ModuleSzaXor.class, "\\SZA");
			
			registerModule(ModuleSzaFullAdder.class, "\\SZA");
			
			
			//Macro
			registerModule(ModuleMacro.class, "\\Macro");
			registerModule(ModuleScalarMacroEntry.class, "\\Macro\\IO");
			registerModule(ModuleScalarMacroExit.class, "\\Macro\\IO");
			registerModule(ModuleSzaMacroEntry.class, "\\Macro\\macro");
			registerModule(ModuleSzaMacroExit.class, "\\Macro\\macro");

			//Load external libs
			File libFolder = new File(Commons.getExecutionDirectory(), "lib");
			if(!libFolder.exists()) {
				libFolder.mkdir();
			}
			loadeExternalLib(libFolder);
			
		} catch (ClassNotModuleExeption e) {
			throw new RuntimeException(e);
		}
	}
	
	private void loadeExternalLib(File libDirectory) {
		
		if(!libDirectory.isDirectory()) {
			System.err.println("Lib directory \"" + libDirectory.getAbsolutePath() + "\" does not exist");
			return;
		}
		
		for(File sub: libDirectory.listFiles()) {
			if(sub.isDirectory()) {
				loadeExternalLib(sub);
			}
			else if(sub.getAbsolutePath().endsWith(".jar")) {
				try {
					ModuleRegisterLoader loader = new ModuleRegisterLoader(sub);
					for(AbstractModuleRegister libRegister: loader.getModuleRegisters()) {
						for(ModuleEntry moduleEntry: libRegister.getRegisteredModuleEntries()) {
							try {
								registerModule(moduleEntry);
							} catch (ClassNotModuleExeption e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
