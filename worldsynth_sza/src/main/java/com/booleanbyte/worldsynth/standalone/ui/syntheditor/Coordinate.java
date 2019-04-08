package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

public class Coordinate {
	
	float x;
	float y;
	
	public Coordinate(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(Pixel p, SynthEditorPanel synthEditor) {
		float x = (float) p.x;
		float y = (float) p.y;
		
		x -= synthEditor.getWidth() / 2;
		y -= synthEditor.getHeight() / 2;
		
		x += synthEditor.getCenterCoordinateX() * synthEditor.getZoom();
		y += synthEditor.getCenterCoordinateY() * synthEditor.getZoom();
		
		x = x / synthEditor.getZoom();
		y = y / synthEditor.getZoom();
		
		this.x = x;
		this.y = y;
	}
}