package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.device.DeviceConnector;
import com.booleanbyte.worldsynth.common.io.Element;

public class TempSynth extends Synth {
	
	public TempSynth(ArrayList<Device> deviceList, ArrayList<DeviceConnector> deviceConnectorList) {
		//Create this as temp synth and set devicelist and connectorlist, then reinstance.
		super("temp");
		this.deviceList = deviceList;
		this.deviceConnectorList = deviceConnectorList;
		reinstance();
		
		recenterSynth();
	}
	
	public TempSynth(Element element) {
		super(element);
		recenterSynth();
	}
	
	private void recenterSynth() {
		//Average positions and find the averaged center of the blueprint
		float x = 0;
		float y = 0;
		
		for(Device d: getDeviceList()) {
			x += d.posX;
			y += d.posY;
		}
		x /= getDeviceList().size();
		y /= getDeviceList().size();
		
		//Reidentify devices from the synth to avoid id conflictions and reposition
		for(Device d: getDeviceList()) {
			d.posX = d.posX - x;
			d.posY = d.posY - y;
		}
	}
	
	public void reinstance() {
		fromElement(toElement());
		for(Device d: deviceList) {
			d.reidentify();
		}
		for(DeviceConnector dc: deviceConnectorList) {
			dc.reidentify();
		}
	}
	
	public void setSenter(float x, float y) {
		recenterSynth();
		
		for(Device d: getDeviceList()) {
			d.posX = d.posX + x;
			d.posY = d.posY + y;
		}
	}
}
