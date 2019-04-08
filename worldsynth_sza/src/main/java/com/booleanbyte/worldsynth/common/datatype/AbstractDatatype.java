package com.booleanbyte.worldsynth.common.datatype;

import java.awt.Color;

import com.booleanbyte.worldsynth.standalone.ui.preview.AbstractPreviewRender;

public abstract class AbstractDatatype {
	
	public AbstractDatatype() {
		
	}
	
	/**
	 * @return The {@link Color} that represents the datatype at the IO rendering in the UI
	 */
	public abstract Color getDatatypeColor();
	
	/**
	 * @return The name of the datattype
	 */
	public abstract String getDatatypeName();
	
	public abstract AbstractDatatype clone();
	
	public AbstractPreviewRender getPreviewRender() {
		return null;
	}
	
	public abstract AbstractDatatype getPreviewDatatype();
}
