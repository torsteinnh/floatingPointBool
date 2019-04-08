package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.BuildThread;
import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.event.build.BuildStatusEvent;
import com.booleanbyte.worldsynth.event.build.BuildStatusListener;
import com.booleanbyte.worldsynth.module.AbstractModule.OutputRequest;
import com.booleanbyte.worldsynth.module.ModuleOutput;
import com.booleanbyte.worldsynth.standalone.ui.preview.AbstractPreviewRender;
import com.booleanbyte.worldsynth.standalone.ui.preview.NullRender;
import com.booleanbyte.worldsynth.standalone.ui.preview.UndefinedRender;

public class PreviewPanel extends JPanel {
	
	private static final long serialVersionUID = -6247030974332736201L;
	
	private BuildThread mainBuildThread = null;
	
	private Synth currentPreviewSynth = null;
	private Device currentPreviewDevice = null;
	
	private AbstractPreviewRender previewRender = new NullRender();
	
	public PreviewPanel() {
		this.setBorder(BorderFactory.createTitledBorder("Preview"));
		this.setLayout(new BorderLayout());
		
		this.add(previewRender, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	public void updatePeview() {
		updatePeview(currentPreviewSynth, currentPreviewDevice);
	}
	
	public void updatePeview(Synth synth, Device device) {
		currentPreviewSynth = synth;
		currentPreviewDevice = device;
		
		//TODO remove
		BuildStatusListener buildListener = new BuildStatusListener() {
			
			@Override
			public void buildUpdate(BuildStatusEvent event) {
				if(event.getMessage().equals("Preparing inputrequests")) {
					WorldSynthMainUI.informationLine.updateInfoText("Preparing input requests for: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Translating requests")) {
					WorldSynthMainUI.informationLine.updateInfoText("Tranlating request from: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Preparing inputdata")) {
					WorldSynthMainUI.informationLine.updateInfoText("Preparing input data to: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Building device")) {
					WorldSynthMainUI.informationLine.updateInfoText("Building: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Registered request")) {
					WorldSynthMainUI.informationLine.updateInfoText("Registered request for building: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Built device")) {
					WorldSynthMainUI.informationLine.updateInfoText("Done building: " + event.getDevice().toString());
				}
			}
		};
		
		if(device != null) {
			if(device.deviceOutputs.length > 0) {
				if(device.deviceOutputs[0] != null) {
					AbstractDatatype requestData = device.deviceOutputs[0].getDatatype().getPreviewDatatype();
					ModuleOutput output = (ModuleOutput) device.deviceOutputs[0].getIO();
					OutputRequest request = device.module.new OutputRequest(output, requestData);
					
					buildOnNewTreadAndPushToPreview(synth, device, request, buildListener);
				}
			}
		}
	}
	
	private void setPreviewRender(AbstractPreviewRender render) {
		previewRender = render;
		this.removeAll();
		this.add(previewRender, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	private AbstractPreviewRender getRenderForDatatype(AbstractDatatype datatype) {
		if(datatype == null) {
			return new NullRender();
		}
		else {
			AbstractPreviewRender render = datatype.getPreviewRender();
			if(render == null) {
				return new UndefinedRender();
			}
			return render;
		}
	}
	
	private void buildOnNewTreadAndPushToPreview(Synth synth, Device device, OutputRequest request, BuildStatusListener buildListener) {
		
		BuildStatusListener localBuildListener = new BuildStatusListener() {
			
			@Override
			public void buildUpdate(BuildStatusEvent event) {
				buildListener.buildUpdate(event);
				if(mainBuildThread.dataReady()) {
					
					WorldSynthMainUI.informationLine.updateInfoText("Preparing preview: " + event.getDevice().toString());
					
					AbstractDatatype builtData = mainBuildThread.getBuiltData();
					
					AbstractPreviewRender newRender = getRenderForDatatype(builtData);
					if(!previewRender.getClass().equals(newRender.getClass())) {
						setPreviewRender(newRender);
					}	
					
					previewRender.pushDataToRender(builtData);
					mainBuildThread = null;
					
					WorldSynthMainUI.informationLine.updateInfoText("Done building device and prevew: " + event.getDevice().toString());
					
					repaint();
				}
			}
		};
		
		mainBuildThread = new BuildThread(synth, device, request, localBuildListener);
		mainBuildThread.start();
	}
}
