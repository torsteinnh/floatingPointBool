package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.AbstractModuleRegister.ModuleEntry;

public class DeviceSearchPopup extends JFrame {
	private static final long serialVersionUID = 9057529386422753602L;
	
	private JTextField searchField;
	private JComboBox<ModuleEntry> suggestionDropdown;
	
	private SynthEditorPanel synthEditor;
	
	public DeviceSearchPopup(SynthEditorPanel synthEditor) {
		this.synthEditor = synthEditor;
		
		this.setUndecorated(true);
		this.setLocationRelativeTo(synthEditor);
		this.setAlwaysOnTop(true);
		
		searchField = new JTextField(20);
		searchField.setFont(new Font(searchField.getFont().getFontName(), searchField.getFont().getStyle(), 20));
		
		DefaultComboBoxModel<ModuleEntry> model = new DefaultComboBoxModel<ModuleEntry>();
		suggestionDropdown = new JComboBox<ModuleEntry>(model);
		suggestionDropdown.setPreferredSize(new Dimension(suggestionDropdown.getPreferredSize().width, 0));
		suggestionDropdown.setBackground(Color.WHITE);
		suggestionDropdown.setFont(new Font(searchField.getFont().getFontName(), searchField.getFont().getStyle(), 20));
		
		suggestionDropdown.putClientProperty("is_adjusting", false);
		suggestionDropdown.setSelectedItem(null);
		suggestionDropdown.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!((boolean) suggestionDropdown.getClientProperty("is_adjusting")) && suggestionDropdown.getSelectedItem() != null) {
					Object selectedItem = suggestionDropdown.getSelectedItem();
					if(selectedItem != null) {
						ModuleEntry me = (ModuleEntry) selectedItem;
						addModuleToSynth(me);
					}
					dispose();
				}
			}
		});
		
		searchField.setLayout(new BorderLayout());
		searchField.add(suggestionDropdown, BorderLayout.PAGE_END);
		
		searchField.getDocument().addDocumentListener(new DocumentListener() {
		    public void insertUpdate(DocumentEvent e) {
		        updateList();
		    }

		    public void removeUpdate(DocumentEvent e) {
		        updateList();
		    }

		    public void changedUpdate(DocumentEvent e) {
		        updateList();
		    }

		    private void updateList() {
		    	suggestionDropdown.putClientProperty("is_adjusting", true);
		        model.removeAllElements();
		        String searchInput = searchField.getText();
		        boolean hasEntries = false;
		        if(!searchInput.isEmpty()) {
		        	String[] splitSearchInput = searchInput.split(" ");
		            for(ModuleEntry item : WorldSynthCore.moduleRegister.getRegisteredModuleEntries()) {
		                if(stringContains(item.toString(), splitSearchInput)) {
		                    model.addElement(item);
		                    hasEntries = true;
		                }
		            }
		        }
		        suggestionDropdown.setPopupVisible(model.getSize() > 0);
		        if(hasEntries) {
		        	suggestionDropdown.setSelectedIndex(0);
		        }
		        suggestionDropdown.putClientProperty("is_adjusting", false);
		    }
		    
		    private boolean stringContains(String subject, String[] subStrings) {
		    	for(String s: subStrings) {
		    		if(!subject.toLowerCase().contains(s.toLowerCase())) {
		    			return false;
		    		}
		    	}
		    	return true;
		    }
		});
		
		this.setLayout(new BorderLayout());
		this.add(searchField, BorderLayout.PAGE_START);
		
		searchField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				dispose();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "apply");
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		
		searchField.getActionMap().put("close", new DisposeAction());
		searchField.getActionMap().put("apply", new ApplyAction());
		searchField.getActionMap().put("up", new UpAction());
		searchField.getActionMap().put("down", new DownAction());
		
		this.pack();
		this.setVisible(true);
	}
	
	private void addModuleToSynth(ModuleEntry moduleEntry) {
		try {
			AbstractModule moduleInstance;
			moduleInstance = WorldSynthCore.constructModule(moduleEntry);
			Device newDevice = new Device(moduleInstance, synthEditor.getCenterCoordinateX(), synthEditor.getCenterCoordinateY());
			synthEditor.setTempDevice(newDevice);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | InstantiationException e1) {
			e1.printStackTrace();
		}
	}
	
	private class DisposeAction extends AbstractAction {
		private static final long serialVersionUID = 7106147999888231148L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
		
	}
	
	private class ApplyAction extends AbstractAction {
		private static final long serialVersionUID = 5640716159386116616L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Object selectedItem = suggestionDropdown.getSelectedItem();
			if(selectedItem != null) {
				ModuleEntry me = (ModuleEntry) selectedItem;
				addModuleToSynth(me);
			}
			dispose();
		}
	}
	
	private class UpAction extends AbstractAction {
		private static final long serialVersionUID = -7334349123834618680L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = suggestionDropdown.getSelectedIndex() - 1;
			if(index >= 0) {
				suggestionDropdown.putClientProperty("is_adjusting", true);
				suggestionDropdown.setSelectedIndex(index);
				suggestionDropdown.putClientProperty("is_adjusting", false);
			}
			else {
				suggestionDropdown.putClientProperty("is_adjusting", true);
				suggestionDropdown.setSelectedIndex(suggestionDropdown.getItemCount() - 1);
				suggestionDropdown.putClientProperty("is_adjusting", false);
			}
		}
	}
	
	private class DownAction extends AbstractAction {
		private static final long serialVersionUID = -8873440183148199521L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = suggestionDropdown.getSelectedIndex() + 1;
			if(index < suggestionDropdown.getItemCount()) {
				suggestionDropdown.putClientProperty("is_adjusting", true);
				suggestionDropdown.setSelectedIndex(index);
				suggestionDropdown.putClientProperty("is_adjusting", false);
			}
			else {
				suggestionDropdown.putClientProperty("is_adjusting", true);
				suggestionDropdown.setSelectedIndex(0);
				suggestionDropdown.putClientProperty("is_adjusting", false);
			}
		}
	}
}
