package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BooleanParameterCheckbox extends JPanel {
	private static final long serialVersionUID = -6749200582598378310L;
	
	private boolean value;
	
	private JLabel nameLable;
	private JCheckBox parameterCheckbox;
	
	public BooleanParameterCheckbox(String lable, boolean initValue) {
		this.value = initValue;
		
		nameLable = new JLabel(lable);
		parameterCheckbox = new JCheckBox();
		parameterCheckbox.setSelected(initValue);
		
		parameterCheckbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				value = parameterCheckbox.isSelected();
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		add(nameLable, gbc);
		
		gbc.gridx = 1;
		add(parameterCheckbox, gbc);
	}
	
	public boolean getValue() {
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
			gridPanel.add(parameterCheckbox, gbc);
		}
		else {
			throw new Exception("gridpanel layoutmanager is not gridbag");
		}
	}
}
