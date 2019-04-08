package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.device.Device;

public class SelectionRectangle {
	private float startCoordinateX;
	private float startCoordinateY;
	private float endCoordinateX;
	private float endCoordinateY;
	
	public SelectionRectangle(float startCoordinateX, float startCoordinateY) {
		this.endCoordinateX = this.startCoordinateX = startCoordinateX;
		this.endCoordinateY = this.startCoordinateY = startCoordinateY;
	}
	
	public void setEndCoordinate(float endCoordinateX, float endCoordinateY) {
		this.endCoordinateX = endCoordinateX;
		this.endCoordinateY = endCoordinateY;
	}
	
	public float getX1() {
		if(startCoordinateX < endCoordinateX) {
			return startCoordinateX;
		}
		return endCoordinateX;
	}
	
	public float getY1() {
		if(startCoordinateY < endCoordinateY) {
			return startCoordinateY;
		}
		return endCoordinateY;
	}
	
	public float getX2() {
		if(startCoordinateX > endCoordinateX) {
			return startCoordinateX;
		}
		return endCoordinateX;
	}
	
	public float getY2() {
		if(startCoordinateY > endCoordinateY) {
			return startCoordinateY;
		}
		return endCoordinateY;
	}
	
	public Device[] getDevicesInside(SynthEditorPanel synthEditor) {
		ArrayList<Device> containedDeviceList = new ArrayList<Device>();
		for(Device d: synthEditor.getSynth().getDeviceList()) {
			if(contains(d.posX, d.posY) && contains(d.posX+d.deviceWidth, d.posY+d.deviceHeight)) {
				containedDeviceList.add(d);
			}
		}
		Device[] list = new Device[0];
		return containedDeviceList.toArray(list);
	}
	
	private boolean contains(float x, float y) {
		if(x < getX1() || x > getX2()) {
			return false;
		}
		if(y < getY1() || y > getY2()) {
			return false;
		}
		return true;
	}
}
