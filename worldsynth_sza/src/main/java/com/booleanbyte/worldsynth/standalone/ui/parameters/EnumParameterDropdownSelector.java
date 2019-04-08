package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EnumParameterDropdownSelector<E extends Enum<E>> extends JPanel {
	private static final long serialVersionUID = -4023171116028389054L;
	
	private E value;
	
	private JLabel nameLable;
	private JComboBox<String> parameterDropdownSelector;
	
	public EnumParameterDropdownSelector(String lable, Class<E> typeClass, E initValue) {
		this.value = initValue;
		
		nameLable = new JLabel(lable);
		parameterDropdownSelector = new JComboBox<String>();
		
		for(Enum<E> par: typeClass.getEnumConstants()) {
			parameterDropdownSelector.addItem(par.name());
			if(par.equals(value)) {
				parameterDropdownSelector.setSelectedItem(par.name());
			}
		}
		
		parameterDropdownSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(E par: typeClass.getEnumConstants()) {
					if(par.name().equals((String) parameterDropdownSelector.getSelectedItem())) {
						value = par;
						break;
					}
				}
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		add(nameLable, gbc);
		
		gbc.gridx = 1;
		add(parameterDropdownSelector, gbc);
	}
	
	public E getValue() {
		return value;
	}
	
	public void addToGrid(JPanel gridPanel, int row) throws Exception {
		if(gridPanel.getLayout() instanceof GridBagLayout) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			
			gbc.gridy = row;
			gbc.gridx = 0;
			gridPanel.add(nameLable, gbc);
			
			gbc.gridx = 1;
			gridPanel.add(parameterDropdownSelector, gbc);
		}
		else {
			throw new Exception("gridpanel layoutmanager is not gridbag");
		}
	}
}
