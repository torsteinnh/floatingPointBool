package com.booleanbyte.worldsynth.common.device;

import java.util.ArrayList;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device.DeviceIO;
import com.booleanbyte.worldsynth.common.io.Element;

public class DeviceConnector {
	
	public static int nextDeviceConnectorID = 1;
	public int deviceConnectorID;
	
	/**
	 * The {@link Device} that the connector is connected into the output of
	 */
	public Device outputDevice;
	/**
	 * The output {@link DeviceIO} of the {@link Device} that the connector is connected into
	 */
	public DeviceIO deviceOutput;
	
	/**
	 * The {@link Device} that the connector is connected into the input of
	 */
	public Device inputDevice;
	/**
	 * The input {@link DeviceIO} of the {@link Device} that the connector is connected into
	 */
	public DeviceIO deviceInput;
	
	public DeviceConnector(Device outputDevice, DeviceIO deviceOutput, Device inputDevice, DeviceIO deviceInput) {
		this.outputDevice = outputDevice;
		this.deviceOutput = deviceOutput;
		this.inputDevice = inputDevice;
		this.deviceInput = deviceInput;
		
		deviceConnectorID = nextDeviceConnectorID;
		nextDeviceConnectorID++;
	}
	
	public DeviceConnector(Synth synth, Element dce) {
		fromElement(synth, dce);
	}
	
	public void reidentify() {
		deviceConnectorID = nextDeviceConnectorID++;
	}

	/**
	 * Sets the {@link Device} and {@link DeviceIO} that the connector is connected into the output of
	 * @param outputDevice
	 * @param deviceOutput
	 */
	public void setOutputDevice(Device outputDevice, DeviceIO deviceOutput) {
		this.outputDevice = outputDevice;
		this.deviceOutput = deviceOutput;
	}
	
	/**
	 * Sets the {@link Device} and {@link DeviceIO} that the connector is connected into the input of
	 * @param inputDevice
	 * @param deviceInput
	 */
	public void setInputDevice(Device inputDevice, DeviceIO deviceInput) {
		this.inputDevice = inputDevice;
		this.deviceInput = deviceInput;
	}
	
	public AbstractDatatype getOutputDeviceDatatype() {
		if(outputDevice != null) {
			return deviceOutput.getDatatype();
		}
		return null;
	}
	
	public AbstractDatatype getInputDeviceDatatype() {
		if(inputDevice != null) {
			return deviceInput.getDatatype();
		}
		return null;
	}
	
	public boolean verifyInputOutputType() {
		if(getInputDeviceDatatype().getDatatypeName().equals(getOutputDeviceDatatype().getDatatypeName())) {
			return true;
		}
		return false;
	}
	
	public boolean verifyConnection() {
		if(deviceInput == null) {
			return false;
		}
		else if(deviceOutput == null) {
			return false;
		}
		return true;
	}
	
	public Element toElement() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("outputdeviceid", String.valueOf(outputDevice.deviceID)));
		paramenterElements.add(new Element("outputdeviceio", String.valueOf(deviceOutput.getName())));
		paramenterElements.add(new Element("inputdeviceid", String.valueOf(inputDevice.deviceID)));
		paramenterElements.add(new Element("inputdeviceio", String.valueOf(deviceInput.getName())));
		
		Element moduleElement = new Element("deviceconnector " + deviceConnectorID, paramenterElements);
		return moduleElement;
	}
	
	public void fromElement(Synth synth, Element element) {
		
		int inputDeviceID = -1;
		String inputDeviceIO = "";
		int outputDeviceID = -1;
		String outputDeviceIO = "";
		
		for(Element e: element.elements) {
			if(e.tag.equals("outputdeviceid")) {
				outputDeviceID = Integer.parseInt(e.content);
			}
			else if(e.tag.equals("outputdeviceio")) {
				outputDeviceIO = e.content;
			}
			else if(e.tag.equals("inputdeviceid")) {
				inputDeviceID = Integer.parseInt(e.content);
			}
			else if(e.tag.equals("inputdeviceio")) {
				inputDeviceIO = e.content;
			}
		}
		
		outputDevice = synth.getDeviceByID(outputDeviceID);
		for(DeviceIO io: outputDevice.deviceOutputs) {
			if(io.getName().equals(outputDeviceIO)) {
				deviceOutput = io;
				break;
			}
		}
		
		inputDevice = synth.getDeviceByID(inputDeviceID);
		for(DeviceIO io: inputDevice.deviceInputs) {
			if(io.getName().equals(inputDeviceIO)) {
				deviceInput = io;
				break;
			}
		}
	}
}
