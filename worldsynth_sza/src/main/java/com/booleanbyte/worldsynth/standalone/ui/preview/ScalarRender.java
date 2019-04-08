package com.booleanbyte.worldsynth.standalone.ui.preview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.datatype.DatatypeScalar;

public class ScalarRender extends AbstractPreviewRender {
	private static final long serialVersionUID = 699411027270891254L;
	
	private double value;
	
	public void setScalar(double value) {
		this.value = value;
	}
	
	@Override
	public void pushDataToRender(AbstractDatatype data) {
		DatatypeScalar castData = (DatatypeScalar) data;
		setScalar(castData.data);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		g.drawString(String.valueOf(value), 10, getHeight()/2-40);
	}
}
