package com.booleanbyte.worldsynth.common;

import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.device.Device.DeviceIO;
import com.booleanbyte.worldsynth.common.device.DeviceConnector;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.event.device.ModuleParameterChangeListener;
import com.booleanbyte.worldsynth.event.synth.SynthEvent;
import com.booleanbyte.worldsynth.event.synth.SynthEvent.SynthEventType;
import com.booleanbyte.worldsynth.event.synth.SynthListener;

public class Synth {
	
	private ArrayList<SynthListener> listeners = new ArrayList<SynthListener>();
	private ModuleParameterChangeListener deviceParameterListener;
	
	private String name = "";
	
	protected ArrayList<Device> deviceList;
	protected ArrayList<DeviceConnector> deviceConnectorList;
	
	public Synth(String name) {
		this.name = name;
		deviceList = new ArrayList<Device>();
		deviceConnectorList = new ArrayList<DeviceConnector>();
	}
	
	public Synth(String name, ArrayList<Device> deviceList, ArrayList<DeviceConnector> deviceConnectorList) {
		this.name = name;
		this.deviceList = deviceList;
		for(Device d: deviceList) {
			d.registerMemberSynth(this);
		}
		this.deviceConnectorList = deviceConnectorList;
		
		cleanupDeviceconnectors();
	}
	
	public Synth(Element element) {
		deviceList = new ArrayList<Device>();
		deviceConnectorList = new ArrayList<DeviceConnector>();
		fromElement(element);
		
		cleanupDeviceconnectors();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addDevice(Device device) {
		deviceList.add(device);
		device.registerMemberSynth(this);
		notifyListenersDeviceAdded(device);
	}
	
	public DeviceConnector[] removeDevice(Device device) {
		//Get the device connectors currently connected to the device and remove them
		DeviceConnector[] removableDeviceConnectors = getConnectorsByDevice(device);
		for(DeviceConnector c: removableDeviceConnectors) {
			deviceConnectorList.remove(c);
		}
		
		//Remove device and notify listeners of it's removal
		deviceList.remove(device);
		notifyListenersDeviceRemoved(device);
		
		//Return a list of the device connectors that was removed
		return removableDeviceConnectors;
	}
	
	public ArrayList<Device> getDeviceList() {
		return deviceList;
	}
	
	public Device getDeviceByID(int deviceID) {
		for(Device d: deviceList) {
			if(d.deviceID == deviceID) {
				return d;
			}
		}
		return null;
	}
	
	public void addDeviceConnector(DeviceConnector deviceConnector) {
		deviceConnectorList.add(deviceConnector);
	}
	
	public ArrayList<DeviceConnector> getDeviceConnectorList() {
		return deviceConnectorList;
	}

	public void removeDeviceConnector(DeviceConnector deviceConnector) {
		deviceConnectorList.remove(deviceConnector);
	}
	
	public DeviceConnector[] getConnectorsByDeviceIO(DeviceIO io) {
		ArrayList<DeviceConnector> deviceConnectors = new ArrayList<DeviceConnector>();
		
		for(DeviceConnector c: deviceConnectorList) {
			if(c.deviceInput == io) deviceConnectors.add(c);
			else if(c.deviceOutput == io) deviceConnectors.add(c);
		}
		
		DeviceConnector[] connectors = new DeviceConnector[deviceConnectors.size()];
		connectors = deviceConnectors.toArray(connectors);
		return connectors;
	}
	
	public DeviceConnector[] getConnectorsByDevice(Device device) {
		ArrayList<DeviceConnector> deviceConnectors = new ArrayList<DeviceConnector>();
		
		for(DeviceConnector c: deviceConnectorList) {
			if(c.inputDevice == device) deviceConnectors.add(c);
			else if(c.outputDevice == device) deviceConnectors.add(c);
		}
		
		DeviceConnector[] connectors = new DeviceConnector[deviceConnectors.size()];
		connectors = deviceConnectors.toArray(connectors);
		return connectors;
	}
	
	public void addSynthListerner(SynthListener listener) {
		listeners.add(listener);
	}
	
	public void removeSynthListener(SynthListener listener) {
		listeners.remove(listener);
	}
	
	public boolean containsDevice(Device device) {
		for(Device d: deviceList) {
			if(d == device) {
				return true;
			}
		}
		return false;
	}
	
	private void notifyListenersDeviceAdded(Device addedDevice) {
		for(SynthListener listener: listeners) {
			listener.deviceAdded(new SynthEvent(SynthEventType.DEVICE_ADDED, addedDevice, this));
		}
	}
	
	private void notifyListenersDeviceRemoved(Device removedDevice) {
		for(SynthListener listener: listeners) {
			listener.deviceRemoved(new SynthEvent(SynthEventType.DEVICE_REMOVES, removedDevice, this));
		}
	}
	
	private void notifyListenersDeviceModified(Device modifiedDevice) {
		for(SynthListener listener: listeners) {
			listener.deviceModified(new SynthEvent(SynthEventType.DEVICE_MODIFIED, modifiedDevice, this));
		}
	}
	
	private void setDeviceList(ArrayList<Device> deviceList) {
		this.deviceList = deviceList;
		for(Device d: deviceList) {
			d.registerMemberSynth(this);
		}
	}
	
	private void setDeviceConnectorList(ArrayList<DeviceConnector> deviceConnectorList) {
		this.deviceConnectorList = deviceConnectorList;
	}
	
	public Element toElement() {
		ArrayList<Element> deviceElements = new ArrayList<Element>();
		for(Device d: deviceList) {
			deviceElements.add(d.toElement());
		}
		
		ArrayList<Element> deviceConnectorElements = new ArrayList<Element>();
		for(DeviceConnector dc: deviceConnectorList) {
			deviceConnectorElements.add(dc.toElement());
		}
		
		Element devicesElement = new Element("devices", deviceElements);
		Element deviceConnectorsElement = new Element("deviceconnectors", deviceConnectorElements);
		
		ArrayList<Element> synthElements = new ArrayList<Element>();
		
		synthElements.add(new Element("name", name));
		synthElements.add(new Element("nextdeviceid", String.valueOf(Device.nextDeviceID)));
		synthElements.add(new Element("nextdeviceconnectorid", String.valueOf(DeviceConnector.nextDeviceConnectorID)));
		synthElements.add(devicesElement);
		synthElements.add(deviceConnectorsElement);
		
		Element synthElement = new Element("synth", synthElements);
		
		return synthElement;
	}
	
	protected void fromElement(Element synthElement) {
		//TODO remove root tag
		if(synthElement.tag.equals("root") || synthElement.tag.equals("synth")) {
			for(Element e: synthElement.elements) {
				if(e.tag.equals("name")) {
					if(e.content != null) {
						name = e.content;
					}
				}
				else if(e.tag.equals("nextdeviceid")) {
					if(Device.nextDeviceID < Integer.parseInt(e.content)) {
						Device.nextDeviceID = Integer.parseInt(e.content);
					}
				}
				else if(e.tag.equals("nextdeviceconnectorid")) {
					if(DeviceConnector.nextDeviceConnectorID < Integer.parseInt(e.content)) {
						DeviceConnector.nextDeviceConnectorID = Integer.parseInt(e.content);
					}
				}
				else if(e.tag.equals("devices")) {
					setDeviceList(extractDevices(e));
				}
				else if(e.tag.equals("deviceconnectors")) {
					setDeviceConnectorList(extractDeviceConnectors(e));
				}
			}
		}
	}
	
	private ArrayList<Device> extractDevices(Element devicesElement) {
		ArrayList<Device> devices = new ArrayList<Device>();
		
		if(devicesElement.elements.size() > 0) {
			for(Element de: devicesElement.elements) {
				devices.add(new Device(de));
			}
		}
		
		return devices;
	}
	
	private ArrayList<DeviceConnector> extractDeviceConnectors(Element deviceConnectorsElement) {
		ArrayList<DeviceConnector> deviceConnectors = new ArrayList<DeviceConnector>();
		
		if(deviceConnectorsElement.elements.size() > 0) {
			for(Element dce: deviceConnectorsElement.elements) {
				deviceConnectors.add(new DeviceConnector(this, dce));
			}
		}
		
		return deviceConnectors;
	}
	
	private void cleanupDeviceconnectors() {
		ArrayList<DeviceConnector> invalidConnectors = new ArrayList<DeviceConnector>();
		
		for(DeviceConnector dc: deviceConnectorList) {
			if(!dc.verifyConnection()) {
				invalidConnectors.add(dc);
			}
		}
		
		for(DeviceConnector dc: invalidConnectors) {
			deviceConnectorList.remove(dc);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Improve equals check
		return super.equals(obj);
	}
}
