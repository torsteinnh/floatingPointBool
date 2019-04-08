package com.booleanbyte.worldsynth.module;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;

/**
 * This is an extension of {@link ModuleIO} for module outputs 
 */
public class ModuleOutput extends ModuleIO {
	
	public ModuleOutput(AbstractDatatype datat, String name) {
		super(datat, name);
	}
	
	public ModuleOutput(AbstractDatatype datat, String name, boolean visible) {
		super(datat, name, visible);
	}
}
