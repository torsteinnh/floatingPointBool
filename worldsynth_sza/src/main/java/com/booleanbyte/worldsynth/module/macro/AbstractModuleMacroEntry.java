package com.booleanbyte.worldsynth.module.macro;

import java.util.HashMap;
import java.util.Map;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.module.IModuleClass;
import com.booleanbyte.worldsynth.module.ModuleClass;

public abstract class AbstractModuleMacroEntry extends AbstractModuleMacroIO {
	
	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		if(getMacroModule() == null) {
			return null;
		}
		
		InputRequest inRequest = new InputRequest(getInput(0), request.data);
		return getMacroModule().buildMacroInput(inRequest);
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		
		return inputRequests;
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.MACRO_ENTRY;
	}
}
