package com.booleanbyte.worldsynth.standalone.ui.preview;

import java.awt.Dimension;

import javax.swing.JTextArea;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.datatype.DatatypeSza;

public class SzaRender extends AbstractPreviewRender {
	private static final long serialVersionUID = -6934156350658455775L;
	
	private JTextArea valueField;
	
	public SzaRender() {
		valueField = new JTextArea();
		valueField.setPreferredSize(new Dimension(450,  450));
		valueField.setLineWrap(true);
		add(valueField);
	}
	
	@Override
	public void pushDataToRender(AbstractDatatype data) {
		DatatypeSza castData = (DatatypeSza) data;
		valueField.setText(castData.plastique);
	}
}
