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

public class DoubleParameterSlider extends JPanel {
	private static final long serialVersionUID = -4023171116028389054L;
	
	private double value;
	
	private JLabel nameLable;
	private JSlider parameterSlider;
	private JTextField parameterField;
	
	public DoubleParameterSlider(String lable, double low, double high, double initValue, int sliderRes) {
		this(lable, low, high, initValue, sliderRes, 1.0);
	}
	
	public DoubleParameterSlider(String lable, double low, double high, double initValue, int sliderRes, double rescale) {
		this.value = initValue;
		
		double dLow = low;
		double dHigh = high;
		double dInitValue = initValue;
		double dSliderRes = (double) sliderRes;
		
		int initSliderValue = (int) constrainedMap(dInitValue, dLow, dHigh, 0.0, dSliderRes);
		
		nameLable = new JLabel(lable);
		parameterSlider = new JSlider(JSlider.HORIZONTAL, 0, sliderRes, initSliderValue);
		parameterField = new JTextField(String.valueOf(value * rescale), 10);
		
		parameterSlider.putClientProperty("changing", false);
		parameterField.putClientProperty("changing", false);
		
		parameterSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if((boolean) parameterField.getClientProperty("changing")) {
					return;
				}
				parameterSlider.putClientProperty("changing", true);
				value = map((double) parameterSlider.getValue(), 0.0, dSliderRes, dLow, dHigh);
				parameterField.setText(String.valueOf(value * rescale));
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
				
				String text = parameterField.getText().replace(",", ".");
				try {
					value = Double.parseDouble(text) / rescale;
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
		return Math.max(Math.min(val, max), min);
	}
	
	private double constrainedMap(double val, double imin, double imax, double omin, double omax) {
		double m = map(val, imin, imax, omin, omax);
		m = constrain(m, omin, omax);
		return m;
	}
	
	public double getValue() {
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
