package com.booleanbyte.worldsynth.event.synth;

import java.util.EventObject;

import com.booleanbyte.worldsynth.common.device.Device;

public class SynthEvent extends EventObject {
	private static final long serialVersionUID = 7606299207519621064L;
	
	private SynthEventType eventType;
	private Device device;
	
	public SynthEvent(SynthEventType eventType, Device device, Object source) {
		super(source);
		this.eventType = eventType;
		this.device = device;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public SynthEventType getEventType() {
		return eventType;
	}
	
	public enum SynthEventType {
		DEVICE_ADDED,
		DEVICE_REMOVES,
		DEVICE_MODIFIED;
	}
}
