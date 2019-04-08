package com.booleanbyte.worldsynth.common.datatype;

import java.awt.Color;

import com.booleanbyte.worldsynth.standalone.ui.preview.AbstractPreviewRender;
import com.booleanbyte.worldsynth.standalone.ui.preview.SzaRender;

public class DatatypeSza extends AbstractDatatype {
	
	public String plastique = "";

	@Override
	public Color getDatatypeColor() {
		return new Color(100, 255, 100);
	}

	@Override
	public String getDatatypeName() {
		return "SZA";
	}

	@Override
	public AbstractDatatype clone() {
		DatatypeSza df = new DatatypeSza();
		df.plastique = new String(plastique);
		return df;
	}
	
	@Override
	public AbstractDatatype getPreviewDatatype() {
		return new DatatypeSza();
	}
	
	@Override
	public AbstractPreviewRender getPreviewRender() {
		return new SzaRender();
	}
	
	public void simplify() {
//		plastique = plastique.replace("--", "");
//		plastique = plastique.replace("+-", "-");
	}
}
