package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.device.Device.DeviceNameEditor;
import com.booleanbyte.worldsynth.module.ModuleMacro;
import com.booleanbyte.worldsynth.standalone.ui.WorldSynthMainUI;

public class DeviceMenuPopup extends JPopupMenu {
	private static final long serialVersionUID = 6716626635327790323L;

	public DeviceMenuPopup(Device device, SynthEditorPanel synthEditor) {
		JMenuItem openModuleUI = new JMenuItem("Parameters");
		openModuleUI.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synthEditor.openDeviceParametersEditor(device);
			}
		});
		this.add(openModuleUI);
		
		JMenuItem openDeviceCustomName = new JMenuItem("Rename");
		openDeviceCustomName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DeviceNameEditor dne = device.openDeviceNameEditor();
				dne.setLocationRelativeTo(synthEditor);
				dne.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(e.getActionCommand().equals("Apply") || e.getActionCommand().equals("OK")) {
							synthEditor.repaint();
							WorldSynthMainUI.synthBrowser.setBrowserContent(synthEditor.getSynth());
							WorldSynthMainUI.synthBrowser.setSelectedDevice(synthEditor.getSelectedDevices());
						}
						if(device.module instanceof ModuleMacro) {
							ModuleMacro macro = (ModuleMacro) device.module;
							WorldSynthMainUI.renameSynth(macro.getMacroSynth(), device.customName);
						}
					}
				});
			}
		});
		this.add(openDeviceCustomName);
		
		JMenuItem bypasDevice = new JMenuItem("Bypass");
		bypasDevice.setEnabled(device.isBypassable());
		bypasDevice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				device.setBypassed(!device.isBypassed());
			}
		});
		this.add(bypasDevice);
		
		JMenuItem deleteDevice = new JMenuItem("Delete");
		deleteDevice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synthEditor.removeDevice(device);
			}
		});
		this.add(deleteDevice);
		
		if(device.module instanceof ModuleMacro) {
			JMenuItem openMacro = new JMenuItem("OpenMacro");
			openMacro.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					WorldSynthMainUI.openSynthEditor(((ModuleMacro)device.module).getMacroSynth(), null); 
				}
			});
			this.add(openMacro);
		}
	}
}
