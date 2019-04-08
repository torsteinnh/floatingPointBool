package com.booleanbyte.worldsynth.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.booleanbyte.worldsynth.common.biome.BiomeRegistry;
import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.device.Device.DeviceIO;
import com.booleanbyte.worldsynth.common.device.DeviceConnector;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.common.material.MaterialRegistry;
import com.booleanbyte.worldsynth.event.build.BuildStatusEvent;
import com.booleanbyte.worldsynth.event.build.BuildStatusListener;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.AbstractModule.InputRequest;
import com.booleanbyte.worldsynth.module.AbstractModule.OutputRequest;
import com.booleanbyte.worldsynth.module.AbstractModuleRegister.ModuleEntry;
import com.booleanbyte.worldsynth.module.ModuleOutput;
import com.booleanbyte.worldsynth.module.NativeModuleRegister;

public class WorldSynthCore {
	
	private static BuildStatusListener buildListener;
	
	public static NativeModuleRegister moduleRegister;
	
	public WorldSynthCore() {
		//Initialize  the material  registry
		new MaterialRegistry();
		//Initialize the biome registry
		new BiomeRegistry();
		
		//Register modules
		moduleRegister = new NativeModuleRegister();
	}
	
	public static void setBuildListener(BuildStatusListener listener) {
		buildListener = listener;
	}
	
	public static AbstractDatatype getDeviceOutput(Synth synth, Device device, OutputRequest request) {
		
		if(buildListener != null) {
			buildListener.buildUpdate(new BuildStatusEvent("Registered request", device, request, Thread.currentThread()));
		}
		
		if(buildListener != null) {
			buildListener.buildUpdate(new BuildStatusEvent("Preparing inputrequests", device, request, Thread.currentThread()));
		}
		
		//Get the input requests the module wants for building the output requested
		//and make sure all requests are unique instances.
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		for(Entry<String, InputRequest> entry: device.module.getInputRequests(request).entrySet()) {
			if(entry == null) {
				continue;
			}
			inputRequests.put(entry.getKey(), device.module.new InputRequest(entry.getValue().getInput(), entry.getValue().getData().clone()));
		
			if(buildListener != null) {
				buildListener.buildUpdate(new BuildStatusEvent("Translating requests", device, request, Thread.currentThread()));
			}
		}
		
		//Translate the input requests to output requests
		HashMap<String, OutputRequest> outputRequests = new HashMap<String, OutputRequest>();
		HashMap<String, Device> outputRequestsModuleWrapper = new HashMap<String, Device>();
		for(Entry<String, InputRequest> entry: inputRequests.entrySet()) {
			//Transform inputrequests into output requests if possible
			DeviceIO input = device.getDeviceIOByModuleIO(entry.getValue().getInput());
			DeviceConnector[] connectors = synth.getConnectorsByDeviceIO(input);
			if(connectors.length == 0) {
				continue;
			}
			DeviceConnector connector = connectors[0];
			Device requestedDevice = connector.outputDevice;
			AbstractModule requestedModule = requestedDevice.module;
			DeviceIO requestedDeviceIO = connector.deviceOutput;
			ModuleOutput requestedModuleOutput = (ModuleOutput) requestedDeviceIO.getIO();
			
			OutputRequest newOutputRequest = requestedModule.new OutputRequest(requestedModuleOutput, entry.getValue().getData());
			outputRequests.put(entry.getKey(), newOutputRequest);
			outputRequestsModuleWrapper.put(entry.getKey(), requestedDevice);
		}
		
		if(buildListener != null) {
			buildListener.buildUpdate(new BuildStatusEvent("Preparing inputdata", device, request, Thread.currentThread()));
		}
		
		//Invoke the outputrequests
		HashMap<String, AbstractDatatype> data = new HashMap<String, AbstractDatatype>();
		for(Entry<String, OutputRequest> entry: outputRequests.entrySet()) {
			AbstractDatatype newData = getDeviceOutput(synth, outputRequestsModuleWrapper.get(entry.getKey()), entry.getValue());
			if(newData != null) {
				data.put(entry.getKey(), newData);
			}
		}
		
		if(buildListener != null) {
			buildListener.buildUpdate(new BuildStatusEvent("Building device", device, request, Thread.currentThread()));
		}
		
		//Build the module wrapped
		AbstractDatatype output = device.module.buildModule(data, request);
		
		if(buildListener != null) {
			buildListener.buildUpdate(new BuildStatusEvent("Built device", device, request, Thread.currentThread()));
		}
		
		//Return the data from the build
		return output;
	}
	
	public static AbstractModule constructModule(Class<? extends AbstractModule> moduleClass) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		Constructor<? extends AbstractModule> ct = moduleClass.getConstructor();
		AbstractModule moduleInstance = ct.newInstance();
		moduleInstance.init();
		return moduleInstance;
	}
	
	public static AbstractModule constructModule(Class<? extends AbstractModule> moduleClass, Element moduleElement) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		Constructor<? extends AbstractModule> ct = moduleClass.getConstructor();
		AbstractModule moduleInstance = ct.newInstance();
		moduleInstance.init();
		moduleInstance.fromElement(moduleElement);
		return moduleInstance;
	}
	
	public static AbstractModule constructModule(ModuleEntry moduleEntry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		Constructor<? extends AbstractModule> ct = moduleEntry.getModuleClass().getConstructor();
		AbstractModule moduleInstance = ct.newInstance();
		moduleInstance.init();
		return moduleInstance;
	}
	
	public static AbstractModule constructModule(ModuleEntry moduleEntry, Element moduleElement) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		Constructor<? extends AbstractModule> ct = moduleEntry.getModuleClass().getConstructor();
		AbstractModule moduleInstance = ct.newInstance();
		moduleInstance.init();
		moduleInstance.fromElement(moduleElement);
		return moduleInstance;
	}
}
