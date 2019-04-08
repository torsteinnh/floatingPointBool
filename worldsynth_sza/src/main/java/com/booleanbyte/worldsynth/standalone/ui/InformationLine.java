package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.event.build.BuildStatusEvent;
import com.booleanbyte.worldsynth.event.build.BuildStatusListener;

public class InformationLine extends JPanel {
	private static final long serialVersionUID = 8887611304647458186L;
	
	private JLabel updateLable;
	
	private BuildStatusListener buildListener;
	
	public InformationLine() {
		setLayout(new FlowLayout());
		updateLable = new JLabel("");
		add(updateLable);
		
		buildListener = new BuildStatusListener() {
			
			@Override
			public void buildUpdate(BuildStatusEvent event) {
				if(event.getMessage().equals("Preparing inputrequests")) {
					updateInfoText("Preparing input requests for: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Translating requests")) {
					updateInfoText("Tranlating request from: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Preparing inputdata")) {
					updateInfoText("Preparing input data to: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Building device")) {
					updateInfoText("Building: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Registered request")) {
					updateInfoText("Registered request for building: " + event.getDevice().toString());
				}
				else if(event.getMessage().equals("Built device")) {
					updateInfoText("Done building: " + event.getDevice().toString());
				}
			}
		};
		
		WorldSynthCore.setBuildListener(buildListener);
	}
	
	public synchronized void updateInfoText(String text) {
		updateLable.setText(text);
		updateLable.repaint();
		System.out.println(text);
	}
}
