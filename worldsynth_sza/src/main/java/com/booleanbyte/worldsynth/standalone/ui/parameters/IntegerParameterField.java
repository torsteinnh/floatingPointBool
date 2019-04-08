package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class IntegerParameterField extends JPanel {
	private static final long serialVersionUID = -4023171116028389054L;
	
	private int value;
	
	private JLabel nameLable;
	private JTextField parameterField;
	
	public IntegerParameterField(String lable, int initValue) {
		this.value = initValue;
				
		nameLable = new JLabel(lable);
		parameterField = new JTextField(String.valueOf(value), 10);
		
		parameterField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				applyChange();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				applyChange();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				applyChange();
			}
			
			private void applyChange() {
				parameterField.putClientProperty("changing", true);
				
				String text = parameterField.getText();
				try {
					value = Integer.parseInt(text);
					parameterField.setBackground(Color.WHITE);
					
				} catch (NumberFormatException exception) {
					parameterField.setBackground(Color.RED);
				}
				
				parameterField.putClientProperty("changing", false);
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		add(nameLable, gbc);
		
		gbc.gridx = 2;
		add(parameterField, gbc);
	}
	
	public int getValue() {
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
			gridPanel.add(parameterField, gbc);
		}
		else {
			throw new Exception("gridpanel layoutmanager is not gridbag");
		}
	}
}
