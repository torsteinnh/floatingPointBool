package com.booleanbyte.worldsynth.module;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;

/**
 * {@link ModuleIO}  is used by the parent {@link Device} object that wraps a module
 *  and the rendering core to define its inputs and outputs. It contains a {@link AbstractDatatype}
 *  prototype used to define the input or output type, and the name of the input or output.
 */
public class ModuleIO {
	AbstractDatatype data;
	String name;
	boolean visible;
	
	public ModuleIO(AbstractDatatype data, String name) {
		this(data, name, true);
	}
	
	public ModuleIO(AbstractDatatype data, String name, boolean visible) {
		this.data = data;
		this.name = name;
		this.visible = visible;
	}
	
	public AbstractDatatype getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
