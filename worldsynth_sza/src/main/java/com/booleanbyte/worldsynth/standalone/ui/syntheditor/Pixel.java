package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

public class Pixel {
	
	int x;
	int y;
	
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Pixel(Coordinate c, SynthEditorPanel synthEditor) {
		float x = c.x * synthEditor.getZoom();
		float y = c.y * synthEditor.getZoom();
		
		x -= synthEditor.getCenterCoordinateX() * synthEditor.getZoom();
		y -= synthEditor.getCenterCoordinateY() * synthEditor.getZoom();
		
		x += synthEditor.getWidth() / 2;
		y += synthEditor.getHeight() / 2;
		
		this.x = (int) x;
		this.y = (int) y;
	}
}