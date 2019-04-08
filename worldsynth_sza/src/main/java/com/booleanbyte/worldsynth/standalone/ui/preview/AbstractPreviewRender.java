package com.booleanbyte.worldsynth.standalone.ui.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;

public abstract class AbstractPreviewRender extends JPanel {
	private static final long serialVersionUID = -6895917441328824400L;

	public AbstractPreviewRender() {
		setPreferredSize(new Dimension(450, 450));
		setLayout(new BorderLayout());
	}
	
	public abstract void pushDataToRender(AbstractDatatype data);
}
