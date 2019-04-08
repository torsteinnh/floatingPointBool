package com.booleanbyte.worldsynth.common.datatype;

import java.awt.Color;

import com.booleanbyte.worldsynth.standalone.ui.preview.AbstractPreviewRender;
import com.booleanbyte.worldsynth.standalone.ui.preview.ScalarRender;

public class DatatypeScalar extends AbstractDatatype {
	
	public double data;

	@Override
	public Color getDatatypeColor() {
		return new Color(255, 100, 100);
	}

	@Override
	public String getDatatypeName() {
		return "Scalar";
	}

	@Override
	public AbstractDatatype clone() {
		DatatypeScalar df = new DatatypeScalar();
		df.data = data;
		return df;
	}
	
	@Override
	public AbstractDatatype getPreviewDatatype() {
		return new DatatypeScalar();
	}
	
	@Override
	public AbstractPreviewRender getPreviewRender() {
		return new ScalarRender();
	}
}
