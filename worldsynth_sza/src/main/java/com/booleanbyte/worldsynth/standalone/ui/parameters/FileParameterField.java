package com.booleanbyte.worldsynth.standalone.ui.parameters;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileParameterField extends JPanel {
	private static final long serialVersionUID = -4023171116028389054L;
	
	private File value;
	
	private JLabel nameLable;
	private JTextField parameterField;
	private JButton directoryDialogButton;
	
	public FileParameterField(String lable, File initValue, boolean folder, FileNameExtensionFilter filter) {
		this.value = initValue;
				
		nameLable = new JLabel(lable);
		parameterField = new JTextField(String.valueOf(value), 40);
		directoryDialogButton = new JButton("...");
		
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
				
				boolean acceptedExtesion = true;
				File newParameterFile = new File(parameterField.getText());
				if(filter != null) {
					acceptedExtesion = filter.accept(newParameterFile);
				}
				
				if(acceptedExtesion) {
					value = newParameterFile;
					parameterField.setBackground(Color.WHITE);
				}
				else {
					parameterField.setBackground(Color.RED);
				}
				
				parameterField.putClientProperty("changing", false);
			}
		});
		
		directoryDialogButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooserDialog = new JFileChooser();
				if(initValue != null) {
					if(initValue.exists()) {
						fileChooserDialog = new JFileChooser(initValue);
					}
				}
				
				if(filter != null) {
					fileChooserDialog.setFileFilter(filter);
				}
				
				fileChooserDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(folder) {
					fileChooserDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				int returnVal = fileChooserDialog.showOpenDialog(SwingUtilities.getWindowAncestor(directoryDialogButton));
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					value = fileChooserDialog.getSelectedFile();
					parameterField.setText(value.getAbsolutePath());
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
		add(parameterField, gbc);
		
		gbc.gridx = 2;
		add(directoryDialogButton, gbc);
	}
	
	public File getValue() {
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
			
			gbc.gridx = 2;
			gridPanel.add(directoryDialogButton, gbc);
		}
		else {
			throw new Exception("gridpanel layoutmanager is not gridbag");
		}
	}
}
