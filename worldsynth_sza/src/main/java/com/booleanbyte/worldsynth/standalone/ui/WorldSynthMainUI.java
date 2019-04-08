package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.io.ProjectReader;
import com.booleanbyte.worldsynth.common.io.ProjectWriter;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.SynthEditorPanel;

public class WorldSynthMainUI extends JFrame {
	private static final long serialVersionUID = 1857017119803116241L;
	
	public static WorldSynthMainUI instance;
	
	public static PreviewPanel previewPanel;
	public static SynthBrowserPanel synthBrowser;
	public static InformationLine informationLine;
	
	private static ArrayList<SynthEditorPanel> synthEditors;
	private static SynthEditorPanel currentSynthEditor;
	
	private static JTabbedPane editorsTabbedPane;
	
	public WorldSynthMainUI() {
		instance = this;
		
		this.setTitle("WorldSynth SZA");
		this.setName("MainUI");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1500, 800);
		this.setMinimumSize(new Dimension(1000, 564));
		this.setLocationRelativeTo(null);
		
		Synth tempSynth = new Synth("Unnamed synth");
		
		//Do the the layout
		setLayout(new BorderLayout());
		
		//Add preview and synth browser
		JPanel previewAndBrowserPanel = new JPanel(new BorderLayout());
		previewPanel = new PreviewPanel();
		synthBrowser = new SynthBrowserPanel(tempSynth);
		previewAndBrowserPanel.add(previewPanel, BorderLayout.PAGE_START);
		previewAndBrowserPanel.add(synthBrowser, BorderLayout.CENTER);
		
		//Add editors
		JPanel editorsPanel = new JPanel(new BorderLayout());
		editorsPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		editorsPanel.setMinimumSize(new Dimension(500, 300));
		
		//Create information line
		informationLine = new InformationLine();
		informationLine.updateInfoText("Welcome to WorldSynth SZA");
		
		//Setup editor tabs
		editorsTabbedPane = new JTabbedPane();
		editorsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		editorsPanel.add(editorsTabbedPane, BorderLayout.CENTER);
		editorsTabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectIndex = editorsTabbedPane.getSelectedIndex();
				if(selectIndex > 0) {
					currentSynthEditor = synthEditors.get(selectIndex-1);
					synthBrowser.setBrowserContent(currentSynthEditor.getSynth());
				}
			}
		});
		
		//Setup keybinds
		editorsTabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control O"), "open");
		editorsTabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control N"), "new");
		
		editorsTabbedPane.getActionMap().put("open", new OpenAction());
		editorsTabbedPane.getActionMap().put("new", new NewAction());
		
		//Add everything to the window
		this.setJMenuBar(new MenuBar());
		this.add(previewAndBrowserPanel, BorderLayout.LINE_START);
		this.add(editorsPanel, BorderLayout.CENTER);
		this.add(informationLine, BorderLayout.PAGE_END);
		
		//Add a an empty synth
		synthEditors = new ArrayList<SynthEditorPanel>();
		openSynthEditor(tempSynth, null);
		
		this.setVisible(true);
	}
	
	public class MenuBar extends JMenuBar {
		private static final long serialVersionUID = -4888120227734421557L;

		public MenuBar() {
			
			JMenu fileMenu = new JMenu("File");
			
			JMenuItem newMenu = new JMenuItem("New");
			newMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openSynthEditor(new Synth("Unnamed synth"), null);
				}
			});
			fileMenu.add(newMenu);
			
			
			JMenuItem saveMenu = new JMenuItem("Save");
			saveMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File file = currentSynthEditor.getSaveFile();
					if(file == null) {
						file = selectSaveFile();
					}
					if(file == null) {
						return;
					}
					
					String synthName = file.getName();
					synthName = synthName.substring(0, synthName.lastIndexOf(".wsynth"));
					
					renameSynth(currentSynthEditor.getSynth(), synthName);
					currentSynthEditor.setSaveFile(file);
					
					ProjectWriter.writeSynthToFile(currentSynthEditor.getSynth(), file);
					currentSynthEditor.registerSavePerformed();
				}
			});
			fileMenu.add(saveMenu);
			
			
			JMenuItem saveAsMenu = new JMenuItem("Save as");
			saveAsMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File file = selectSaveFile();
					if(file == null) {
						return;
					}
					
					String synthName = file.getName();
					synthName = synthName.substring(0, synthName.lastIndexOf(".wsynth"));
					
					renameSynth(currentSynthEditor.getSynth(), synthName);
					currentSynthEditor.setSaveFile(file);
					
					ProjectWriter.writeSynthToFile(currentSynthEditor.getSynth(), file);
					currentSynthEditor.registerSavePerformed();
				}
			});
			fileMenu.add(saveAsMenu);
			
			
			JMenuItem openMenu = new JMenuItem("Open");
			openMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("WorldSynth", "wsynth");
					fileChooser.setFileFilter(filter);
					
					int returnVal = fileChooser.showOpenDialog(editorsTabbedPane);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						openSynthEditor(ProjectReader.readSynthFromFile(fileChooser.getSelectedFile()), fileChooser.getSelectedFile());
					}
				}
			});
			fileMenu.add(openMenu);
			
			this.add(fileMenu);
			
			//Edit menu
			JMenu editMenu = new JMenu("Edit");
			this.add(editMenu);
			
			//Help menu
			JMenu helpMenu = new JMenu("Help");
			this.add(helpMenu);
		}
		
		private File selectSaveFile() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(currentSynthEditor.getSynth().getName() + ".wsynth"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("WorldSynth", "wsynth");
			fileChooser.setFileFilter(filter);
			
			int returnVal = fileChooser.showSaveDialog(editorsTabbedPane);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if(!file.getAbsolutePath().endsWith(".wsynth")) {
					file = new File(file.getAbsolutePath() + ".wsynth");
				}
				if(file.exists()) {
					String[] options = {"Abort", "Overwrite"};
					int overwrite = JOptionPane.showOptionDialog(null, "The file \"" + file.getName() + "\" already exists!\nDo you want  to overwrite it?", "Overwrite existing file?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
					if(overwrite == 0) return null;
				}
				
				return file;
			}
			
			return null;
		}
	}

	
	public static void openSynthEditor(Synth synth, File synthFile) {
		//Check if the synth is already in the editor
		for(int i = 0; i < synthEditors.size(); i++) {
			if(synth.equals(synthEditors.get(i).getSynth())) {
				editorsTabbedPane.setSelectedIndex(i+1);
				currentSynthEditor = synthEditors.get(i);
				synthBrowser.setBrowserContent(synth);
				return;
			}
		}
		
		//If synth is not in editor already
		SynthEditorPanel newSynthEditorPanel = new SynthEditorPanel(synth, synthFile);
		synthEditors.add(newSynthEditorPanel);
		int newIndex = synthEditors.size()-1;
		
		//Add the new synth editor to the tabbed pane
		editorsTabbedPane.add(synth.getName(), newSynthEditorPanel);
		//Set make the editor tab closable with custom tab component
		editorsTabbedPane.setTabComponentAt(newIndex, new SyntheditorClosableTabRenderComponent(newSynthEditorPanel, instance));
		
		editorsTabbedPane.setSelectedIndex(newIndex);
		currentSynthEditor = newSynthEditorPanel;
		synthBrowser.setBrowserContent(synth);
	}
	
	public void closeSynthEditor(Synth synth) {
		//Find the editor for the synth
		for(SynthEditorPanel editor: synthEditors) {
			if(editor.getSynth() == synth) {
				synthEditors.remove(editor);
				editorsTabbedPane.remove(editor);
				currentSynthEditor = null;
				synthBrowser.setBrowserContent(null);
				
				if(editorsTabbedPane.getSelectedComponent() instanceof SynthEditorPanel) {
					SynthEditorPanel newSelectedEditor = (SynthEditorPanel) editorsTabbedPane.getSelectedComponent();
					synthBrowser.setBrowserContent(newSelectedEditor.getSynth());
					currentSynthEditor = newSelectedEditor;
				}
				
				break;
			}
			
		}
	}
	
	public static SynthEditorPanel getCurrentSynthEditor() {
		return currentSynthEditor;
	}
	
	public static void renameSynth(Synth synth, String name) {
		synth.setName(name);
		
		//Check if the synth is already in an editor and rename the editor if it is
		for(int i = 0; i < synthEditors.size(); i++) {
			if(synth.equals(synthEditors.get(i).getSynth())) {
				editorsTabbedPane.setTitleAt(i, name);
				return;
			}
		}
	}
	
	public static void repaintTabbedPane() {
		editorsTabbedPane.repaint();
	}
	
	private class OpenAction extends AbstractAction {
		private static final long serialVersionUID = 3830339699969618285L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("WorldSynth", "wsynth");
			fileChooser.setFileFilter(filter);
			
			int returnVal = fileChooser.showOpenDialog(editorsTabbedPane);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				openSynthEditor(ProjectReader.readSynthFromFile(fileChooser.getSelectedFile()), fileChooser.getSelectedFile());
			}
		}
		
	}
	
	private class NewAction extends AbstractAction {
		private static final long serialVersionUID = 3830339699969618285L;

		@Override
		public void actionPerformed(ActionEvent e) {
			openSynthEditor(new Synth("Unnamed synth"), null);
		}
		
	}
}
