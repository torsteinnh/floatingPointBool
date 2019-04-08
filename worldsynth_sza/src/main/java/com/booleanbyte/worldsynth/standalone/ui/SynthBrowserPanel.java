package com.booleanbyte.worldsynth.standalone.ui;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.device.Device;

public class SynthBrowserPanel extends JPanel {
	private static final long serialVersionUID = 7279738094050377717L;
	
	Synth synth;
	
	private JTree projectBrowserTree;
	private JScrollPane projectBrowserTreeScrollPane;
	
	public SynthBrowserPanel(Synth synth) {
		this.synth = synth;
		this.setBorder(BorderFactory.createTitledBorder("Synth browser"));
		this.setLayout(new BorderLayout());
		
		setBrowserContent(synth);
	}
	
	public void setBrowserContent(Synth synth) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		
		if(synth != null) {
			for(Device d: synth.getDeviceList()) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(d);
				root.add(newNode);
			}
		}
		
		projectBrowserTree = new JTree(root);
		projectBrowserTree.setRootVisible(false);
		projectBrowserTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		projectBrowserTree.putClientProperty("useroperated", true);
		
		projectBrowserTree.addTreeSelectionListener(new SelectionListener());
		
		projectBrowserTreeScrollPane = new JScrollPane(projectBrowserTree);
		
		removeAll();
		add(projectBrowserTreeScrollPane);
		
		revalidate();
		repaint();
	}
	
	public void setSelectedDevice(Device[] devices) {
		
		//Iterate and compile a selection path array
		TreePath[] selectionPaths = new TreePath[devices.length];
		for(int i = 0; i < devices.length; i++) {
			selectionPaths[i] = getSelectionPath(devices[i]);
		}
		
		projectBrowserTree.putClientProperty("useroperated", false);
		projectBrowserTree.setSelectionPaths(selectionPaths);
		projectBrowserTree.putClientProperty("useroperated", true);
	}
	
	private TreePath getSelectionPath(Device device) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) projectBrowserTree.getModel().getRoot();
		Enumeration<?> rootEnumeration = rootNode.breadthFirstEnumeration();
		while(rootEnumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootEnumeration.nextElement();
			
//			if(node == null) {
//				return null;
//			}
			
			if(node.getUserObject() == device) {
				return new TreePath(node.getPath());
			}
		}
		return null;
	}
	
	private class SelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			boolean isUeseroperated = (boolean) projectBrowserTree.getClientProperty("useroperated");
			if(!isUeseroperated) {
				return;
			}
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) projectBrowserTree.getLastSelectedPathComponent();
			
			if(node == null) {
				return;
			}
			
			WorldSynthMainUI.getCurrentSynthEditor().setSelectedDevices(new Device[]{(Device) node.getUserObject()}); 
		}
		
	}
}
