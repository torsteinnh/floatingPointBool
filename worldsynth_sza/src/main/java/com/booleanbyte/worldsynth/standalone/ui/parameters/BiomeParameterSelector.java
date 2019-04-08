package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.biome.Biome;
import com.booleanbyte.worldsynth.common.biome.BiomeRegistry;

public class BiomeParameterSelector extends JPanel {
	private static final long serialVersionUID = 438550050885523301L;

	private Biome value;
	
	private JLabel nameLable;
	private JComboBox<Biome> parameterDropdownSelector = new JComboBox<Biome>();
	
	public BiomeParameterSelector(String lable, Biome initValue) {
		this.value = initValue;
		nameLable = new JLabel(lable);
		
		for(Biome biome: BiomeRegistry.getBiomesAlphabetically()) {
			parameterDropdownSelector.addItem(biome);
		}
		parameterDropdownSelector.setSelectedItem(value);
		
		parameterDropdownSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				value = (Biome)parameterDropdownSelector.getSelectedItem();
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
	
	public Biome getValue() {
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
