package com.booleanbyte.worldsynth.common;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.event.build.BuildStatusEvent;
import com.booleanbyte.worldsynth.event.build.BuildStatusListener;
import com.booleanbyte.worldsynth.module.AbstractModule.OutputRequest;

public class BuildThread extends Thread {
	
	private volatile AbstractDatatype builtData = null;
	
	private volatile boolean dataReady = false;
	
	private volatile Synth synth;
	private volatile Device device;
	private volatile OutputRequest request;
	private volatile BuildStatusListener buildLestener;
	
	public BuildThread(Synth synth, Device device, OutputRequest request, BuildStatusListener listener) {
		this.synth = synth;
		this.device = device;
		this.request = request;
		this.buildLestener = listener;
	}
	
	@Override
	public void run() {
		builtData = WorldSynthCore.getDeviceOutput(synth, device, request);
		dataReady = true;
		buildLestener.buildUpdate(new BuildStatusEvent("Build thread finished", device, request, Thread.currentThread()));
	}
	
	public AbstractDatatype getBuiltData() {
		return builtData;
	}
	
	public boolean dataReady() {
		return dataReady;
	}
}
