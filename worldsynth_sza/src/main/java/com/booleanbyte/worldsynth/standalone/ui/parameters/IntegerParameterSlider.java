package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class IntegerParameterSlider extends JPanel {
	private static final long serialVersionUID = -6749200582598378310L;
	
	private int value;
	
	private JLabel nameLable;
	private JSlider parameterSlider;
	private JTextField parameterField;
	
	public IntegerParameterSlider(String lable, int low, int high, int initValue, int sliderRes) {
		this.value = initValue;
		
		double dLow = (double) low;
		double dHigh = (double) high;
		double dInitValue = (double) initValue;
		double dSliderRes = (double) sliderRes;
		
		int initSliderValue = (int) constrainedMap(dInitValue, dLow, dHigh, 0.0, dSliderRes);
		
		nameLable = new JLabel(lable);
		parameterSlider = new JSlider(JSlider.HORIZONTAL, 0, sliderRes, initSliderValue);
		parameterField = new JTextField(String.valueOf(value), 10);
		
		parameterSlider.putClientProperty("changing", false);
		parameterField.putClientProperty("changing", false);
		
		parameterSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((boolean) parameterField.getClientProperty("changing")) {
					return;
				}
				parameterSlider.putClientProperty("changing", true);
				value = (int) map((double) parameterSlider.getValue(), 0.0, dSliderRes, dLow, dHigh);
				parameterField.setText(String.valueOf(value));
				parameterSlider.putClientProperty("changing", false);
			}
		});
		
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
				if((boolean) parameterSlider.getClientProperty("changing")) {
					return;
				}
				parameterField.putClientProperty("changing", true);
				
				String text = parameterField.getText();
				try {
					value = Integer.parseInt(text);
					parameterSlider.setValue((int) constrainedMap(value, dLow, dHigh, 0.0, dSliderRes));
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
		
		gbc.gridx = 1;
		add(parameterSlider, gbc);
		
		gbc.gridx = 2;
		add(parameterField, gbc);
	}
	
	private double map(double val, double imin, double imax, double omin, double omax) {
		return omin + (val - imin) * (omax - omin) / (imax - imin);
	}
	
	private double constrain(double val, double min, double max) {
		return Math.min(Math.max(val, min), max);
	}
	
	private double constrainedMap(double val, double imin, double imax, double omin, double omax) {
		double m = map(val, imin, imax, omin, omax);
		m = constrain(m, omin, omax);
		return m;
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
			gridPanel.add(parameterSlider, gbc);
			
			gbc.gridx = 2;
			gridPanel.add(parameterField, gbc);
		}
		else {
			throw new Exception("gridpanel layoutmanager is not gridbag");
		}
	}
}
