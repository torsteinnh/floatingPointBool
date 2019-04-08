package com.booleanbyte.worldsynth.module;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;

/**
 * This is an extension of {@link ModuleIO} for module inputs 
 */
public class ModuleInput extends ModuleIO {
	
	public ModuleInput(AbstractDatatype data, String name) {
		super(data, name);
	}
	
	public ModuleInput(AbstractDatatype data, String name, boolean visible) {
		super(data, name, visible);
	}
}
