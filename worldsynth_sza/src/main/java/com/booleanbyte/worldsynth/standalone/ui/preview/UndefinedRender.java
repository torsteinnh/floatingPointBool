package com.booleanbyte.worldsynth.standalone.ui.preview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;

public class UndefinedRender extends AbstractPreviewRender {
	private static final long serialVersionUID = -8987245008340710948L;

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("TimesRoman", Font.BOLD, 40));
		g.drawString("UNDEFINED RENDER", 10, getHeight()/2-40);
		
		g.drawString("MISSING RENDER", 40, getHeight()/2+40);
		g.drawString("FOR THIS DATATYPE", 10, getHeight()/2+80);
	}
	
	@Override
	public void pushDataToRender(AbstractDatatype data) {
	}
}
