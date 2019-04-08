package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.awt.Polygon;
import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.device.Device;

public class SelectionLasso {
	
	private ArrayList<float[]> points = new ArrayList<float[]>();
	
	public SelectionLasso(float startCoordinateX, float startCoordinateY) {
		float[] newPoint = {startCoordinateX, startCoordinateY};
		points.add(newPoint);
		points.add(newPoint.clone());
	}
	
	public void setCurrentCoordinate(float coordinateX, float coordinateY) {
		float[] latestPoint = points.get(points.size() - 2);
		float[] currentPoint = {coordinateX, coordinateY};
		points.set(points.size() - 1, currentPoint);
		if(dist(latestPoint, currentPoint) > 10) {
			points.add(currentPoint.clone());
		}
	}
	
	public float[][] getPoints() {
		float[][] pointsArray = new float[0][2];
		pointsArray = this.points.toArray(pointsArray);
		return pointsArray;
	}
	
	public Device[] getDevicesInside(SynthEditorPanel synthEditor) {
		ArrayList<Device> containedDeviceList = new ArrayList<Device>();
		
		//Build polygon
		Polygon polygon = new Polygon();
		for(float[] p: points) {
			polygon.addPoint((int)p[0], (int)p[1]);
		}
		
		//check for containment
		for(Device d: synthEditor.getSynth().getDeviceList()) {
			if(polygon.contains(d.posX, d.posY, d.deviceWidth, d.deviceHeight)) {
				containedDeviceList.add(d);
			}
		}
		
		//Return contained devices
		Device[] list = new Device[0];
		return containedDeviceList.toArray(list);
	}
	
	private float dist(float[] p1, float[] p2) {
		double dx = p2[0] - p1[0];
		double dy = p2[1] - p1[1];
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
}
