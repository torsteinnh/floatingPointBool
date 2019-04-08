package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.event.device.ModuleParameterChangeEvent;
import com.booleanbyte.worldsynth.event.device.ModuleParameterChangeListener;

public class ModuleParametersUI extends JFrame {
	
	private static final long serialVersionUID = 6327282899036030185L;
	
	private ArrayList<ModuleParameterChangeListener> parameterListeners = new ArrayList<ModuleParameterChangeListener>();
	
	private Device subject;
	
	private Element oldParameterElement;
	
	private JButton applyButton;

	public ModuleParametersUI(Device moduleWrapper) {
		subject = moduleWrapper;
		
		//Setup window
		setTitle(moduleWrapper.module.getModuleName());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setAlwaysOnTop(true);
		
		//Get the current parameters for comparison if something has changed
		oldParameterElement = moduleWrapper.module.toElement();
		
		JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyChange();
			}
		});
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				notifyChange();
			}
		});
		
		bPanel.add(cancelButton);
		bPanel.add(applyButton);
		bPanel.add(okButton);
		add(bPanel, BorderLayout.PAGE_END);
		
		JPanel panel = new JPanel();
		ActionListener moduleApplyListener = moduleWrapper.module.moduleUI(panel);
		applyButton.addActionListener(moduleApplyListener);
		okButton.addActionListener(moduleApplyListener);
		
		//Setup keybinds
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "apply");
		panel.getActionMap().put("apply", new ApplyAction());
		
		add(panel, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}
	
	public void addParameterListener(ModuleParameterChangeListener listener) {
		parameterListeners.add(listener);
	}
	
	private void notifyChange() {
		Element newParameterElement = subject.module.toElement();
		if(!oldParameterElement.equals(newParameterElement)) {
			notifyListenersParametersChanged(subject, oldParameterElement, newParameterElement);
			oldParameterElement = newParameterElement;
		}
	}
	
	private void notifyListenersParametersChanged(Device subject, Element oldParameterElement, Element newParameterElement) {
		for(ModuleParameterChangeListener listener: parameterListeners) {
			listener.parametersChanged(new ModuleParameterChangeEvent(subject, oldParameterElement, newParameterElement, this));
		}
	}
	
	private class ApplyAction extends AbstractAction {
		private static final long serialVersionUID = 1045362299863378046L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			applyButton.doClick();
		}
		
	}
}
