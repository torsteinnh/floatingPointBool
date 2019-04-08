package com.booleanbyte.worldsynth.standalone.ui.syntheditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.device.Device.DeviceIO;
import com.booleanbyte.worldsynth.common.device.Device.DeviceNameEditor;
import com.booleanbyte.worldsynth.common.device.DeviceConnector;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.common.io.ProjectWriter;
import com.booleanbyte.worldsynth.event.device.ModuleParameterChangeEvent;
import com.booleanbyte.worldsynth.event.device.ModuleParameterChangeListener;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.ModuleMacro;
import com.booleanbyte.worldsynth.standalone.ui.ModuleParametersUI;
import com.booleanbyte.worldsynth.standalone.ui.WorldSynthMainUI;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.history.AbstractEditorHistoryEvent;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.history.EditorHistoryEventModuleAddRemove;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.history.EditorHistoryEventModuleEdit;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.history.EditorHistoryEventModuleMove;
import com.booleanbyte.worldsynth.standalone.ui.syntheditor.history.EditorHistoryRegister;

public class SynthEditorPanel extends JPanel {
	private static final long serialVersionUID = -547760763770836326L;
	
	private Synth synth;
	private EditorHistoryRegister history = new EditorHistoryRegister(20);
	private boolean unsavedChanges = false;
	
	private File synthFile = null;
	
	private float centerCoordX = 0;
	private float centerCoordY = 0;
	private float zoom = 1F;
	
	private Listener listener;
	
	/**
	 * The {@link Device} that the mouse is hovering over
	 */
	private Device deviceOver = null;
	/**
	 * The {@link DeviceIO} that the mouse is hovering over
	 */
	private DeviceIO deviceIOOver = null;
	/**
	 * The list of {@link Device}s that are currently selected
	 */
	private ArrayList<Device> selectedDevices = new ArrayList<Device>();
	
	private TempSynth tempSynth = null;
	private Device tempDevice = null;
	private DeviceConnector tempDeviceConnector = null;
	
	public SynthEditorPanel(Synth synth) {
		this(synth, null);
	}
	
	public SynthEditorPanel(Synth synth, File synthFile) {
		this.synth = synth;
		this.synthFile = synthFile;
		
		listener = new Listener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.addMouseWheelListener(listener);
		
		//Keybindings
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "search");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delete");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("BACK_SPACE"), "delete");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control R"), "rename");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control B"), "bypass");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control E"), "edit");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "undo");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"), "redo");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "save");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control X"), "cut");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control C"), "copy");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control V"), "paste");

		
		this.getActionMap().put("search", new DeviceSearchAction());
		this.getActionMap().put("delete", new DeviceDeleteAction());
		this.getActionMap().put("rename", new DeviceRenameAction());
		this.getActionMap().put("bypass", new DeviceBypassAction());
		this.getActionMap().put("edit", new DeviceEditAction());
		this.getActionMap().put("undo", new UndoAction());
		this.getActionMap().put("redo", new RedoAction());
		this.getActionMap().put("escape", new EscapeAction());
		this.getActionMap().put("save", new SaveAction());
		this.getActionMap().put("cut", new CutAction());
		this.getActionMap().put("copy", new CopyAction());
		this.getActionMap().put("paste", new PasteAction());
	}
	
	public Synth getSynth() {
		return synth;
	}
	
	public void addDevice(Device device) {
		addDevices(new Device[]{device});
	}
	
	public void addDevices(Device[] devices) {
		for(Device d: devices) {
			synth.addDevice(d);
		}
		
		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(devices, null));
		registerUnsavedChangePerformed();
		
		WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
		repaint();
	}
	
	public void removeDevice(Device device) {
		removeDevices(new Device[]{device});
	}
	
	
	public void removeDevices(Device[] devices) {
		ArrayList<DeviceConnector> removedConnectors = new ArrayList<DeviceConnector>();
		
		for(Device d: devices) {
			//Remove device and add register the removed deviceconnectors
			DeviceConnector[] rc = synth.removeDevice(d);
			for(DeviceConnector dc: rc) {
				removedConnectors.add(dc);
			}
			//Remove devices from selection
			removeDeviceFromSelection(d);
		}
		
		//Register in history
		//First we concentrate all the subjects into one array
		Object[] subjects = new Object[devices.length + removedConnectors.size()];
		for(int i = 0; i < devices.length; i++) {
			subjects[i] = devices[i];
		}
		for(int i = 0; i < removedConnectors.size(); i++) {
			subjects[devices.length + i] = removedConnectors.get(i);
		}
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(null, subjects));
		registerUnsavedChangePerformed();
		
		WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
		repaint();
	}
	
	public void addDeviceConnector(DeviceConnector deviceConnector) {
		synth.addDeviceConnector(deviceConnector);

		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(new Object[]{deviceConnector}, null));
		registerUnsavedChangePerformed();
		
		repaint();
	}
	
	public void addDeviceConnectors(DeviceConnector[] deviceConnectors) {
		for(DeviceConnector dc: deviceConnectors) {
			synth.addDeviceConnector(dc);
		}
		
		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(deviceConnectors, null));
		registerUnsavedChangePerformed();
		
		repaint();
	}
	
	public void removeDeviceConnector(DeviceConnector deviceConnector) {
		synth.removeDeviceConnector(deviceConnector);
		
		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(null, new Object[]{deviceConnector}));
		registerUnsavedChangePerformed();
		
		repaint();
	}
	
	public void removeDeviceConnectors(DeviceConnector[] deviceConnectors) {
		for(DeviceConnector dc: deviceConnectors) {
			synth.removeDeviceConnector(dc);
		}
		
		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(null, deviceConnectors));
		registerUnsavedChangePerformed();
		
		repaint();
	}
	
	public Device[] getSelectedDevices() {
		Device[] devices = new Device[0];
		devices = selectedDevices.toArray(devices);
		return devices;
	}
	
	public Device getLatestSelectedDevice() {
		if(selectedDevices.size() == 0) {
			return null;
		}
		return selectedDevices.get(selectedDevices.size() - 1);
	}
	
	public void setSelectedDevices(Device[] devices) {
		//Check that the list of devices is not empty
		if(devices.length == 0) {
			return;
		}
		//Check that the devices is part of the synth being edited in this editor
		for(Device d: devices) {
			if(!synth.containsDevice(d)) {
				return;
			}
		}
		
		//If it contains all the devices set the selection
		//Clear selection and add the device as the only entry
		selectedDevices.clear();
		for(Device d: devices) {
			selectedDevices.add(d);
		}
		
		repaint();
		//Update preview with latest selected device
		WorldSynthMainUI.previewPanel.updatePeview(synth, getLatestSelectedDevice());
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
	}
	
	public void toggleSelectedDevice(Device device) {
		//Check that the device is part of the synth being edited in this editor
		if(synth.containsDevice(device)) {
			//Check if the device is already contained in the selection
			//If the device already is part of the selection remove it so the instance only is contained once when added at the end
			if(selectedDevices.contains(device)) {
				selectedDevices.remove(device);
			}
			else {
				selectedDevices.add(device);
			}
			
			repaint();
			//Update preview with latest selected device
			WorldSynthMainUI.previewPanel.updatePeview(synth, getLatestSelectedDevice());
			WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
		}
	}
	
	public void removeDeviceFromSelection(Device device) {
		selectedDevices.remove(device);
		
		repaint();
		//Update preview with latest selected device
		WorldSynthMainUI.previewPanel.updatePeview(synth, device);
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
	}
	
	public void clearSelectedDevices() {
		selectedDevices.clear();
		
		repaint();
		//Update preview with latest selected device
		WorldSynthMainUI.previewPanel.updatePeview(synth, null);
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(new Color(220, 220, 220));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//Draw center cross;
		g.setColor(Color.GRAY);
		Pixel centerCoordinatePixel = new Pixel(new Coordinate(0, 0), this);
		g.drawLine(centerCoordinatePixel.x, 0, centerCoordinatePixel.x, getHeight());
		g.drawLine(0, centerCoordinatePixel.y, getWidth(), centerCoordinatePixel.y);
		
		//Draw the rest of the grid
		g.setColor(Color.LIGHT_GRAY);
		float gridIncrement = 50;
		for(float cx = gridIncrement; (new Pixel(new Coordinate(cx, 0), this)).x < getWidth(); cx += gridIncrement) {
			int x = (new Pixel(new Coordinate(cx, 0), this)).x;
			g.drawLine(x, 0, x, getHeight());
		}
		for(float cx = -gridIncrement; (new Pixel(new Coordinate(cx, 0), this)).x > 0; cx -= gridIncrement) {
			int x = (new Pixel(new Coordinate(cx, 0), this)).x;
			g.drawLine(x, 0, x, getHeight());
		}
		for(float cy = gridIncrement; (new Pixel(new Coordinate(0, cy), this)).y < getHeight(); cy += gridIncrement) {
			int y = (new Pixel(new Coordinate(0, cy), this)).y;
			g.drawLine(0, y, getWidth(), y);
		}
		for(float cy = -gridIncrement; (new Pixel(new Coordinate(0, cy), this)).y > 0; cy -= gridIncrement) {
			int y = (new Pixel(new Coordinate(0, cy), this)).y;
			g.drawLine(0, y, getWidth(), y);
		}
		
		//Draw devices
		for(Device d: synth.getDeviceList()) {
			paintDevice(d, g);
		}
		
		//Draw deviceconnectors
		for(DeviceConnector dc: synth.getDeviceConnectorList()) {
			paintDeviceConnector(dc, g);
		}
		
		//Draw the temp device
		if(tempDevice != null) {
			paintDevice(tempDevice, g);
		}
		
		//Draw the temp deviceconnector
		if(tempDeviceConnector != null) {
			paintDeviceConnector(tempDeviceConnector, g);
		}
		
		//Draw the tempsynth
		if(tempSynth != null) {
			for(Device d: tempSynth.getDeviceList()) {
				paintDevice(d, g);
			}
			for(DeviceConnector dc: tempSynth.getDeviceConnectorList()) {
				paintDeviceConnector(dc, g);
			}
		}
		
		//Draw the selectionrectangle
		if(listener.selectionRectangle != null) {
			paintSelectionRectangle(listener.selectionRectangle, g);
		}
		
		//Draw the selectionlasso
		if(listener.selectionLasso != null) {
			paintSelectionLasso(listener.selectionLasso, g);
		}
		
		//TODO Draw more when implementing grouping, comments...
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public float getCenterCoordinateX() {
		return centerCoordX;
	}
	
	public float getCenterCoordinateY() {
		return centerCoordY;
	}
	
	public void setTempDevice(Device device) {
		Coordinate mouseCoordinate = new Coordinate(new Pixel(listener.lastMousePosX, listener.lastMousePosY), this);
		device.posX = mouseCoordinate.x - device.deviceWidth/2;
		device.posY = mouseCoordinate.y - device.deviceHeight/2;
		tempDevice = device;
		repaint();
	}
	
	public void setTempSynth(TempSynth tempSynth) {
		Coordinate mouseCoordinate = new Coordinate(new Pixel(listener.lastMousePosX, listener.lastMousePosY), this);
		tempSynth.setSenter(mouseCoordinate.x, mouseCoordinate.y);
		this.tempSynth = tempSynth;
		repaint();
	}
	
	public void openDeviceParametersEditor(Device device) {
		ModuleParametersUI mui = new ModuleParametersUI(device);
		mui.addParameterListener(new ModuleParameterChangeListener() {
			
			@Override
			public void parametersChanged(ModuleParameterChangeEvent e) {
				WorldSynthMainUI.previewPanel.updatePeview();
				history.addHistoryEvent(new EditorHistoryEventModuleEdit(device, e.getOldParameterElement(), e.getNewParameterElement()));
				registerUnsavedChangePerformed();
			}
		});
		
		mui.setLocationRelativeTo(this);
	}
	
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}
	
	public void registerSavePerformed() {
		unsavedChanges = false;
		WorldSynthMainUI.repaintTabbedPane();
	}
	
	public File getSaveFile() {
		return synthFile;
	}
	
	public void setSaveFile(File file) {
		synthFile = file;
	}
	
	private void registerUnsavedChangePerformed() {
		unsavedChanges = true;
		WorldSynthMainUI.repaintTabbedPane();
	}
	
	private void applyTempDevice() {
		addDevice(tempDevice);
		//Reinstance new device of same type
		//TODO maybe move this to a clone method inside device
		try {
			AbstractModule moduleInstance;
			moduleInstance = WorldSynthCore.constructModule(tempDevice.module.getClass());
			Device newDevice = new Device(moduleInstance, tempDevice.posX, tempDevice.posY);
			tempDevice = newDevice;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | InstantiationException e1) {
			e1.printStackTrace();
			tempDevice = null;
		}
	}
	
	private void applyTempSynth() {
		ArrayList<Object> added = new ArrayList<Object>();
		
		for(Device d: tempSynth.getDeviceList()) {
			synth.addDevice(d);
			added.add(d);
		}
		for(DeviceConnector dc: tempSynth.getDeviceConnectorList()) {
			synth.addDeviceConnector(dc);
			added.add(dc);
		}
		tempSynth.reinstance();
		
		//Register in history
		history.addHistoryEvent(new EditorHistoryEventModuleAddRemove(added.toArray(new Object[0]), null));
		registerUnsavedChangePerformed();
		
		WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
		WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
		
		repaint();
	}
	
	private void paintDevice(Device device, Graphics2D g) {
		Color deviceColor = device.module.getModuleColor();
		//If device is bypasset it is rendered gray
		if(device.isBypassed()) {
			deviceColor = Color.LIGHT_GRAY;
		}
		Color deviceOverColor = deviceColor.darker();
		Color deviceStrokeColor = Color.BLACK;
		Color deviceOverStrokeColor = Color.MAGENTA;
		
		Color deviceIOStrokeColor = Color.BLACK;
		Color deviceIOOverStrokeColor = Color.MAGENTA;
		
		float deviceStrokeSize = 2;
		float deviceIOStrokeSize = deviceStrokeSize;
		
		Coordinate coord = new Coordinate(device.posX, device.posY);
		Pixel pixel = new Pixel(coord, this);
		
		//Draw main device rect;
		if(deviceOver == device) {
			g.setColor(deviceOverColor);
			g.fillRect(pixel.x, pixel.y, (int) (device.deviceWidth*zoom), (int) (device.deviceHeight*zoom));
			g.setColor(deviceOverStrokeColor);
			g.setStroke(new BasicStroke(deviceStrokeSize*zoom));
			g.drawRect(pixel.x, pixel.y, (int) (device.deviceWidth*zoom), (int) (device.deviceHeight*zoom));
		}
		else {
			g.setColor(deviceColor);
			g.fillRect(pixel.x, pixel.y, (int) (device.deviceWidth*zoom), (int) (device.deviceHeight*zoom));
			g.setColor(deviceStrokeColor);
			g.setStroke(new BasicStroke(deviceStrokeSize*zoom));
			g.drawRect(pixel.x, pixel.y, (int) (device.deviceWidth*zoom), (int) (device.deviceHeight*zoom));
		}
		if(selectedDevices.contains(device)) {
			float[] dash = {10.0f};
			g.setStroke(new BasicStroke(deviceStrokeSize*zoom, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 5));
			g.setColor(Color.WHITE);
			if(device == getLatestSelectedDevice()) {
				g.setColor(Color.GREEN);
			}
			g.drawRect(pixel.x, pixel.y, (int) (device.deviceWidth*zoom), (int) (device.deviceHeight*zoom));
		}
		
		//Draw device IO inputs
		if(device.module.getInputs() != null) {
			for(DeviceIO io: device.deviceInputs) {
				if(!io.getIO().isVisible() && !(device.module instanceof ModuleMacro)) {
					continue;
				}
				g.setColor(io.getDatatype().getDatatypeColor());
				g.fillRect((int) (pixel.x + io.posX*zoom), (int) (pixel.y + io.posY*zoom), (int) (io.ioRenderSize*zoom), (int) (io.ioRenderSize*zoom));
				g.setStroke(new BasicStroke(deviceIOStrokeSize*zoom));
				if(io == deviceIOOver) g.setColor(deviceIOOverStrokeColor);
				else g.setColor(deviceIOStrokeColor);
				g.drawRect((int) (pixel.x + io.posX*zoom), (int) (pixel.y + io.posY*zoom), (int) (io.ioRenderSize*zoom), (int) (io.ioRenderSize*zoom));
			}
		}
		
		//Draw device IO outputs
		if(device.module.getOutputs() != null) {
			for(DeviceIO io: device.deviceOutputs) {
				if(!io.getIO().isVisible() && !(device.module instanceof ModuleMacro)) {
					continue;
				}
				g.setColor(io.getDatatype().getDatatypeColor());
				g.fillRect((int) (pixel.x + io.posX*zoom), (int) (pixel.y + io.posY*zoom), (int) (io.ioRenderSize*zoom), (int) (io.ioRenderSize*zoom));
				g.setStroke(new BasicStroke(deviceIOStrokeSize*zoom));
				if(io == deviceIOOver) g.setColor(deviceIOOverStrokeColor);
				else g.setColor(deviceIOStrokeColor);
				g.drawRect((int) (pixel.x + io.posX*zoom), (int) (pixel.y + io.posY*zoom), (int) (io.ioRenderSize*zoom), (int) (io.ioRenderSize*zoom));
			}
		}
		
		//Draw device bypass
		if(device.isBypassed()) {
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(deviceStrokeSize*zoom*2));
			//Draw line between main input and main output
			Pixel mi = new Pixel((int) (pixel.x + device.deviceInputs[0].posX*zoom + device.deviceInputs[0].ioRenderSize*zoom/2), (int) (pixel.y + device.deviceInputs[0].posY*zoom + device.deviceInputs[0].ioRenderSize*zoom/2));
			Pixel mo = new Pixel((int) (pixel.x + device.deviceOutputs[0].posX*zoom + device.deviceOutputs[0].ioRenderSize*zoom/2), (int) (pixel.y + device.deviceOutputs[0].posY*zoom + device.deviceOutputs[0].ioRenderSize*zoom/2));
			g.drawLine(mi.x, mi.y, mi.x+(int) (10*zoom), mi.y+(int) (10*zoom));
			g.drawLine(mi.x+(int) (10*zoom), mi.y+(int) (10*zoom), mo.x-(int) (10*zoom), mo.y+(int) (10*zoom));
			g.drawLine(mo.x-(int) (10*zoom), mo.y+(int) (10*zoom), mo.x, mo.y);
		}
		
		//Draw text
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (15*zoom)));
		g.drawString(device.toString(), pixel.x, pixel.y - 5*zoom);
		
		//Draw a string to indicate the io name if hvering over one
		if(deviceOver == device && deviceIOOver != null) {
			g.setColor(Color.GRAY);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) (15*zoom)));
			g.drawString(deviceIOOver.toString(), pixel.x, pixel.y + (device.deviceHeight + 15)*zoom);
		}
		
	}
	
	private void paintDeviceConnector(DeviceConnector connector, Graphics2D g) {
		int startX = 0;
		int startY = 0;
		int endX = 0;
		int endY = 0;
		
		if(connector.outputDevice != null && connector.deviceOutput != null) {
			float x = connector.outputDevice.posX + connector.deviceOutput.posX + connector.deviceOutput.ioRenderSize/2;
			float y = connector.outputDevice.posY + connector.deviceOutput.posY + connector.deviceOutput.ioRenderSize/2;
			
			Pixel pixel = new Pixel(new Coordinate(x, y), this);
			startX = pixel.x;
			startY = pixel.y;
		}
		else {
			startX = listener.lastMousePosX;
			startY = listener.lastMousePosY;
		}
		
		if(connector.inputDevice != null && connector.deviceInput != null) {
			float x = connector.inputDevice.posX + connector.deviceInput.posX + connector.deviceInput.ioRenderSize/2;
			float y = connector.inputDevice.posY + connector.deviceInput.posY + connector.deviceInput.ioRenderSize/2;
			
			Pixel pixel = new Pixel(new Coordinate(x, y), this);
			endX = pixel.x;
			endY = pixel.y;
		}
		else {
			endX = listener.lastMousePosX;
			endY = listener.lastMousePosY;
		}
		
		//Draw ball at the connection point
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2*zoom));
		g.drawLine(startX, startY, endX, endY);
		if(connector.outputDevice != null) g.fillOval((int)(startX-3*zoom), (int)(startY-3*zoom), (int)(6*zoom), (int)(6*zoom));
		if(connector.inputDevice != null) g.fillOval((int)(endX-3*zoom), (int)(endY-3*zoom), (int)(6*zoom), (int)(6*zoom));
	}
	
	private void paintSelectionRectangle(SelectionRectangle selectionRectangle, Graphics2D g) {
		Coordinate startCoord = new Coordinate(selectionRectangle.getX1(), selectionRectangle.getY1());
		Pixel startPixel = new Pixel(startCoord, this);
		Coordinate endCoord = new Coordinate(selectionRectangle.getX2(), selectionRectangle.getY2());
		Pixel endPixel = new Pixel(endCoord, this);
		
		float[] dash = {10.0f};
		g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 5));
		g.setColor(Color.BLACK);
		g.drawRect(startPixel.x, startPixel.y, endPixel.x-startPixel.x, endPixel.y-startPixel.y);
	}
	
	private void paintSelectionLasso(SelectionLasso selectionLasso, Graphics2D g) {
		Polygon lassoPolygon = new Polygon();
		for(float[] p: selectionLasso.getPoints()) {
			Coordinate coord = new Coordinate(p[0], p[1]);
			Pixel pixel = new Pixel(coord, this);
			lassoPolygon.addPoint(pixel.x, pixel.y);
		}
		
		
		float[] dash = {10.0f};
		g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 5));
		g.setColor(Color.BLACK);
		g.drawPolygon(lassoPolygon);
	}
	
	private class Listener implements MouseListener, MouseMotionListener, MouseWheelListener {
		
		private boolean mousePressed = false;
		private int mouseButton = 0;
		
		private boolean firstDragEvent = true;
		
		private EditorHistoryEventModuleMove tempHistoryEventMove = null;
		
		int lastMousePosX;
		int lastMousePosY;
		
		SelectionRectangle selectionRectangle = null;
		SelectionLasso selectionLasso = null;

		@Override
		public void mouseClicked(MouseEvent e) {
			//Single click left
			if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
				if(tempDevice != null) {
					applyTempDevice();
				}
				
				else if(tempSynth != null) {
					applyTempSynth();
				}
				
				else if(tempDeviceConnector == null && deviceIOOver != null) {
					if(deviceIOOver.isInput()) {
						DeviceConnector[] dc = synth.getConnectorsByDeviceIO(deviceIOOver);
						if(dc.length > 0) {
							for(DeviceConnector c: dc) {
								removeDeviceConnector(c);
							}
							repaint();
						}
						tempDeviceConnector = new DeviceConnector(null, null, deviceOver, deviceIOOver);
					}
					else {
						tempDeviceConnector = new DeviceConnector(deviceOver, deviceIOOver, null, null);
					}
				}
				else if (tempDeviceConnector != null && deviceIOOver == null) {
					tempDeviceConnector = null;
					repaint();
				}
				else if (tempDeviceConnector != null && deviceIOOver != null) {
					if(tempDeviceConnector.inputDevice != null && tempDeviceConnector.deviceInput != null && !deviceIOOver.isInput() && tempDeviceConnector.inputDevice != deviceOver) {
						//If the mouse is over an output that is not on the already connected device and the connected io is an input
						tempDeviceConnector.setOutputDevice(deviceOver, deviceIOOver);
						if(tempDeviceConnector.verifyInputOutputType()) {
							addDeviceConnector(tempDeviceConnector);
						}
						tempDeviceConnector = null;
					}
					else if(tempDeviceConnector.outputDevice != null && tempDeviceConnector.deviceOutput != null && deviceIOOver.isInput() && tempDeviceConnector.outputDevice != deviceOver) {
						//If the mouse is over an input that is not on the already connected device and the connected io is an output
						DeviceConnector[] c = synth.getConnectorsByDeviceIO(deviceIOOver);
						
						tempDeviceConnector.setInputDevice(deviceOver, deviceIOOver);
						
						if(tempDeviceConnector.verifyInputOutputType()) {
							if(c.length > 0) {
								//If there is already a connector on the input remove it so it gets replaced
								for(DeviceConnector dc: c) {
									removeDeviceConnector(dc);
								}
							}
							addDeviceConnector(tempDeviceConnector);
						}
						tempDeviceConnector = null;
					}
				}
				else if(deviceOver != null) {
					if(e.isControlDown()) {
						toggleSelectedDevice(deviceOver);
					}
					else {
						setSelectedDevices(new Device[]{deviceOver});
					}
				}
			}
			
			//Double click left
			else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				if(deviceOver != null) {
					if(deviceOver.module instanceof ModuleMacro) {
						WorldSynthMainUI.openSynthEditor(((ModuleMacro)deviceOver.module).getMacroSynth(), null); 
					}
					else {
						openDeviceParametersEditor(deviceOver);
					}
				}
			}
			
			//Single click right
			if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
				//Temp device clear
				if(tempDevice != null) {
					tempDevice = null;
					repaint();
				}
				
				//Tempsynth clear
				else if(tempSynth != null) {
					tempSynth = null;
					repaint();
				}
				
				//Device menu
				else if(deviceOver != null) {
					DeviceMenuPopup pop = new DeviceMenuPopup(deviceOver, SynthEditorPanel.this);
					pop.show(e.getComponent(), e.getX(), e.getY());
				}
				
				//Editor device addition menu
				else {
					NetworkPanelPopup pop = new NetworkPanelPopup(lastMousePosX, lastMousePosY, SynthEditorPanel.this);
					pop.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousePressed = true;
			mouseButton = e.getButton();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousePressed = false;
			mouseButton = 0;
			
			firstDragEvent = true;
			
			if(tempHistoryEventMove != null) {
				history.addHistoryEvent(tempHistoryEventMove);
				tempHistoryEventMove = null;
				registerUnsavedChangePerformed();
			}
			
			if(selectionRectangle != null) {
				setSelectedDevices(selectionRectangle.getDevicesInside(SynthEditorPanel.this));
				selectionRectangle = null;
				repaint();
			}
			
			else if(selectionLasso != null) {
				setSelectedDevices(selectionLasso.getDevicesInside(SynthEditorPanel.this));
				selectionLasso = null;
				repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			repaint();
			requestFocus();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			deviceOver = null;
			repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			float maxZoom = 5F;
			float minZoom = 0.05F;
			
			zoom -= (float) e.getWheelRotation() * zoom / 10;
			if(zoom < minZoom) zoom = minZoom;
			else if(zoom > maxZoom) zoom = maxZoom;
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Coordinate mouseCoordinate = new Coordinate(new Pixel(e.getX(), e.getY()), SynthEditorPanel.this);
			
			if(mouseButton == MouseEvent.BUTTON1 && tempDevice != null && firstDragEvent) {
				applyTempDevice();
			}
			
			else if(mouseButton == MouseEvent.BUTTON1 && tempDevice != null) {
				tempDevice.posX = mouseCoordinate.x - tempDevice.deviceWidth/2;
				tempDevice.posY = mouseCoordinate.y - tempDevice.deviceHeight/2;
				repaint();
			}
			
			//Move around view
			else if(mouseButton == MouseEvent.BUTTON3) {
				float diffX = (float) (lastMousePosX - e.getX());
				float diffY = (float) (lastMousePosY - e.getY());
				
				lastMousePosX = e.getX();
				lastMousePosY = e.getY();
				
				centerCoordX += diffX / zoom;
				centerCoordY += diffY / zoom;
				
				repaint();
			}
			//Move multiple selected
			else if(mouseButton == MouseEvent.BUTTON1 && selectedDevices.contains(deviceOver) && deviceIOOver == null && !e.isControlDown()) {
				//Create a move event with the old coordniates at first move
				if(firstDragEvent) {
					Object[] moveSubjects = new Object[selectedDevices.size()];
					Coordinate[] oldCoordinate = new Coordinate[selectedDevices.size()];
					Coordinate[] newCoordinate = new Coordinate[selectedDevices.size()];
					for(int i = 0; i < selectedDevices.size(); i++) {
						moveSubjects[i] = selectedDevices.get(i);
						oldCoordinate[i] = new Coordinate(selectedDevices.get(i).posX, selectedDevices.get(i).posY);
						newCoordinate[i] = new Coordinate(selectedDevices.get(i).posX, selectedDevices.get(i).posY);
					}
					tempHistoryEventMove = new EditorHistoryEventModuleMove(moveSubjects, oldCoordinate, newCoordinate);
				}
				
				//Move selected devices
				float diffX = (float) (lastMousePosX - e.getX());
				float diffY = (float) (lastMousePosY - e.getY());
				lastMousePosX = e.getX();
				lastMousePosY = e.getY();
				
				for(Device d: selectedDevices) {
					d.posX -= diffX / zoom;
					d.posY -= diffY / zoom;
				}
				
				//Update latest positions for history event
				for(int i = 0; i < selectedDevices.size(); i++) {
					tempHistoryEventMove.getNewCoordinates()[i].x = selectedDevices.get(i).posX;
					tempHistoryEventMove.getNewCoordinates()[i].y = selectedDevices.get(i).posY;
				}
				
				repaint();
			}
			//Move single unselected
			else if(mouseButton == MouseEvent.BUTTON1 && deviceOver != null && deviceIOOver == null) {
				//Create a move event with the old coordniates at first move
				if(firstDragEvent) {
					Object[] moveSubjects = {deviceOver};
					Coordinate[] oldCoordinate = {new Coordinate(deviceOver.posX, deviceOver.posY)};
					Coordinate[] newCoordinate = {new Coordinate(deviceOver.posX, deviceOver.posY)};
					tempHistoryEventMove = new EditorHistoryEventModuleMove(moveSubjects, oldCoordinate, newCoordinate);
				}
				
				//Move device
				float diffX = (float) (lastMousePosX - e.getX());
				float diffY = (float) (lastMousePosY - e.getY());
				lastMousePosX = e.getX();
				lastMousePosY = e.getY();
				deviceOver.posX -= diffX / zoom;
				deviceOver.posY -= diffY / zoom;
				
				//Update latest position for history event
				tempHistoryEventMove.getNewCoordinates()[0].x = deviceOver.posX;
				tempHistoryEventMove.getNewCoordinates()[0].y = deviceOver.posY;
//				
				repaint();
			}
			//Perform multiselection lasso area selection
			else if(mouseButton == MouseEvent.BUTTON1 && (e.isControlDown() || selectionLasso != null)) {
				if(selectionLasso == null) {
					selectionLasso = new SelectionLasso(mouseCoordinate.x, mouseCoordinate.y);
					repaint();
				}
				else {
					selectionLasso.setCurrentCoordinate(mouseCoordinate.x, mouseCoordinate.y);
					repaint();
				}
			}
			
			//Perform multiselection rectangle area selection
			else if(mouseButton == MouseEvent.BUTTON1) {
				if(selectionRectangle == null) {
					selectionRectangle = new SelectionRectangle(mouseCoordinate.x, mouseCoordinate.y);
					repaint();
				}
				else {
					selectionRectangle.setEndCoordinate(mouseCoordinate.x, mouseCoordinate.y);
					repaint();
				}
			}
			
			firstDragEvent = false;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			lastMousePosX = e.getX();
			lastMousePosY = e.getY();
			
			boolean foundDevice = false;
			boolean foundDeviceIO = false;
			Device lastDevice = deviceOver;
			DeviceIO lastDeviceIO = deviceIOOver;
			
			Coordinate mouseCoordinate = new Coordinate(new Pixel(e.getX(), e.getY()), SynthEditorPanel.this);
			
			//move tempdevice
			if(tempDevice != null) {
				tempDevice.posX = mouseCoordinate.x - tempDevice.deviceWidth/2;
				tempDevice.posY = mouseCoordinate.y - tempDevice.deviceHeight/2;
				repaint();
			}
			
			else if(tempSynth != null) {
				tempSynth.setSenter(mouseCoordinate.x, mouseCoordinate.y);
				repaint();
			}
			
			//Is mouse over a device
			for(Device d: synth.getDeviceList()) {
				if(mouseCoordinate.x >= d.posX && mouseCoordinate.x <= d.posX+d.deviceWidth && mouseCoordinate.y >= d.posY && mouseCoordinate.y <= d.posY+d.deviceHeight) {
					deviceOver = d;
					foundDevice = true;
					
					//Is mouse over an IO
					if(d.deviceInputs != null) {
						for(DeviceIO io: d.deviceInputs) {
							if(!io.getIO().isVisible()) {
								continue;
							}
							if(mouseCoordinate.x >= d.posX+io.posX && mouseCoordinate.x <= d.posX+io.posX+io.ioRenderSize && mouseCoordinate.y >= d.posY+io.posY && mouseCoordinate.y <= d.posY+io.posY+io.ioRenderSize) {
								deviceIOOver = io;
								foundDeviceIO = true;
								break;
							}
						}
					}
					if(d.deviceOutputs != null) {
						for(DeviceIO io: d.deviceOutputs) {
							if(!io.getIO().isVisible()) {
								continue;
							}
							if(mouseCoordinate.x >= d.posX+io.posX && mouseCoordinate.x <= d.posX+io.posX+io.ioRenderSize && mouseCoordinate.y >= d.posY+io.posY && mouseCoordinate.y <= d.posY+io.posY+io.ioRenderSize) {
								deviceIOOver = io;
								foundDeviceIO = true;
								break;
							}
						}
					}
				}
			}
			if(!foundDevice) {
				deviceOver = null;
			}
			if(!foundDeviceIO) {
				deviceIOOver = null;
			}
			if(deviceOver != lastDevice || deviceIOOver != lastDeviceIO || tempDeviceConnector != null) {
				repaint();
			}
		}
	}
	
	private class DeviceDeleteAction extends AbstractAction {
		private static final long serialVersionUID = 3141083760976092742L;

		@Override
		public void actionPerformed(ActionEvent e) {
			removeDevices(selectedDevices.toArray(new Device[0]));
			
			WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
			clearSelectedDevices();
		}
	}
	
	private class DeviceRenameAction extends AbstractAction {
		private static final long serialVersionUID = -4061122877203853046L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(selectedDevices.size() == 0) {
				return;
			}
			Device selectedDevice = selectedDevices.get(selectedDevices.size()-1);
			DeviceNameEditor dne = selectedDevice.openDeviceNameEditor();
			dne.setLocationRelativeTo(SynthEditorPanel.this);
			dne.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("Apply") || e.getActionCommand().equals("OK")) {
						WorldSynthMainUI.getCurrentSynthEditor().repaint();
						WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
						WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
					}
				}
			});
		}
	}
	
	private class DeviceBypassAction extends AbstractAction {
		private static final long serialVersionUID = -4061122877203853046L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(selectedDevices.size() == 0) {
				return;
			}
			Device selectedDevice = selectedDevices.get(selectedDevices.size()-1);
			selectedDevice.setBypassed(!selectedDevice.isBypassed());
			repaint();
		}
		
	}
	
	private class DeviceEditAction extends AbstractAction {
		private static final long serialVersionUID = 3349065451288442206L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(selectedDevices.size() == 0) {
				return;
			}
			Device selectedDevice = selectedDevices.get(selectedDevices.size()-1);
			openDeviceParametersEditor(selectedDevice);
		}
	}
	
	private class DeviceSearchAction extends AbstractAction {
		private static final long serialVersionUID = -4789168343429466170L;

		@Override
		public void actionPerformed(ActionEvent e) {
			new DeviceSearchPopup(SynthEditorPanel.this);
		}
	}
	
	private class UndoAction extends AbstractAction {
		private static final long serialVersionUID = -1551624972107493281L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractEditorHistoryEvent abstractHistoryEvent = history.getLastHistoryEvent();
			if(abstractHistoryEvent == null) {
				return;
			}
			
			if(abstractHistoryEvent instanceof EditorHistoryEventModuleAddRemove) {
				EditorHistoryEventModuleAddRemove historyEvent = (EditorHistoryEventModuleAddRemove) abstractHistoryEvent;
				if(historyEvent.getAddedSubjects() != null) {
					for(Object subject: historyEvent.getAddedSubjects()) {
						if(subject instanceof Device) {
							synth.removeDevice((Device) subject);
						}
						else if(subject instanceof DeviceConnector) {
							synth.removeDeviceConnector((DeviceConnector) subject);
						}
					}
				}
				
				if(historyEvent.getRemovedSubjects() != null) {
					for(Object subject: historyEvent.getRemovedSubjects()) {
						if(subject instanceof Device) {
							synth.addDevice((Device) subject);
						}
						else if(subject instanceof DeviceConnector) {
							synth.addDeviceConnector((DeviceConnector) subject);
						}
					}
				}
			}
			else if(abstractHistoryEvent instanceof EditorHistoryEventModuleMove) {
				EditorHistoryEventModuleMove historyEvent = (EditorHistoryEventModuleMove) abstractHistoryEvent;
				for(int i = 0; i < historyEvent.getMovedSubjects().length; i++) {
					Device movedDevice = (Device) historyEvent.getMovedSubjects()[i];
					Coordinate oldCoorinate = historyEvent.getOldCoordinates()[i];
					
					movedDevice.posX = oldCoorinate.x;
					movedDevice.posY = oldCoorinate.y;
				}
			}
			else if(abstractHistoryEvent instanceof EditorHistoryEventModuleEdit) {
				EditorHistoryEventModuleEdit historyEvent = (EditorHistoryEventModuleEdit) abstractHistoryEvent;
				historyEvent.getSubject().module.fromElement(historyEvent.getOldParameterElement());
			}
			
			registerUnsavedChangePerformed();
			
			WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
			WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
			
			repaint();
		}
	}
	
	private class RedoAction extends AbstractAction {
		private static final long serialVersionUID = -8077390024435066724L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractEditorHistoryEvent abstractHistoryEvent = history.getNextHistoryEvent();
			if(abstractHistoryEvent == null) {
				return;
			}
			
			if(abstractHistoryEvent instanceof EditorHistoryEventModuleAddRemove) {
				EditorHistoryEventModuleAddRemove historyEvent = (EditorHistoryEventModuleAddRemove) abstractHistoryEvent;
				if(historyEvent.getAddedSubjects() != null) {
					for(Object subject: historyEvent.getAddedSubjects()) {
						if(subject instanceof Device) {
							synth.addDevice((Device) subject);
						}
						else if(subject instanceof DeviceConnector) {
							synth.addDeviceConnector((DeviceConnector) subject);
						}
					}
				}
				
				if(historyEvent.getRemovedSubjects() != null) {
					for(Object subject: historyEvent.getRemovedSubjects()) {
						if(subject instanceof Device) {
							synth.removeDevice((Device) subject);
						}
						else if(subject instanceof DeviceConnector) {
							synth.removeDeviceConnector((DeviceConnector) subject);
						}
					}
				}
			}
			else if(abstractHistoryEvent instanceof EditorHistoryEventModuleMove) {
				EditorHistoryEventModuleMove historyEvent = (EditorHistoryEventModuleMove) abstractHistoryEvent;
				for(int i = 0; i < historyEvent.getMovedSubjects().length; i++) {
					Device movedDevice = (Device) historyEvent.getMovedSubjects()[i];
					Coordinate newCoorinate = historyEvent.getNewCoordinates()[i];
					
					movedDevice.posX = newCoorinate.x;
					movedDevice.posY = newCoorinate.y;
				}
			}
			else if(abstractHistoryEvent instanceof EditorHistoryEventModuleEdit) {
				EditorHistoryEventModuleEdit historyEvent = (EditorHistoryEventModuleEdit) abstractHistoryEvent;
				historyEvent.getSubject().module.fromElement(historyEvent.getNewParameterElement());
			}
			
			registerUnsavedChangePerformed();
			
			WorldSynthMainUI.synthBrowser.setBrowserContent(synth);
			WorldSynthMainUI.synthBrowser.setSelectedDevice(getSelectedDevices());
			
			repaint();
		}
	}
	
	private class EscapeAction extends AbstractAction {
		private static final long serialVersionUID = 3830339699969618285L;

		@Override
		public void actionPerformed(ActionEvent e) {
			tempDevice = null;
			tempDeviceConnector = null;
			tempSynth = null;
			repaint();
		}
		
	}
	
	private class CutAction extends AbstractAction {
		private static final long serialVersionUID = -9065207633165331484L;

		@Override
		public void actionPerformed(ActionEvent e) {
			//Find the selected connectors
			ArrayList<DeviceConnector> selectedConnectors = new ArrayList<DeviceConnector>();
			for(Device d: selectedDevices) {
				for(DeviceConnector dc: synth.getConnectorsByDevice(d)) {
					if(!selectedConnectors.contains(dc) && selectedDevices.contains(dc.inputDevice) && selectedDevices.contains(dc.outputDevice)) {
						selectedConnectors.add(dc);
					}
				}
			}
			
			//Instance a tempsynth
			TempSynth temp = new TempSynth(selectedDevices, selectedConnectors);
			
			removeDevices(selectedDevices.toArray(new Device[0]));
			clearSelectedDevices();
			
			Clipboard cliphboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable tempsynthText = new StringSelection(temp.toElement().toDocumentformat());
			cliphboard.setContents(tempsynthText, null);
		}
		
	}
	
	private class CopyAction extends AbstractAction {
		private static final long serialVersionUID = -5099891528332322516L;

		@Override
		public void actionPerformed(ActionEvent e) {
			//Find the selected connectors
			ArrayList<DeviceConnector> selectedConnectors = new ArrayList<DeviceConnector>();
			for(Device d: selectedDevices) {
				for(DeviceConnector dc: synth.getConnectorsByDevice(d)) {
					if(!selectedConnectors.contains(dc) && selectedDevices.contains(dc.inputDevice) && selectedDevices.contains(dc.outputDevice)) {
						selectedConnectors.add(dc);
					}
				}
			}
			
			//Instance a tempsynth
			TempSynth temp = new TempSynth(selectedDevices, selectedConnectors);
			
			Clipboard sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable tempsynthText = new StringSelection(temp.toElement().toDocumentformat());
			sysClipboard.setContents(tempsynthText, null);
		}
		
	}
	
	private class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 3307388595227985003L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Clipboard sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipTransferable = sysClipboard.getContents(null);
			
			if(clipTransferable != null) {
				String tempSynthText = "";
				try {
					tempSynthText = (String) clipTransferable.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException | IOException e1) {
					e1.printStackTrace();
				}
				if(!tempSynthText.startsWith("<synth>")) {
					return;
				}
				
				setTempSynth(new TempSynth(new Element(tempSynthText)));
			}
		}
		
	}
	
	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 5782763493850638677L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(synthFile == null) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File(synth.getName() + ".wsynth"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("WorldSynth", "wsynth");
				fileChooser.setFileFilter(filter);
				
				int returnVal = fileChooser.showSaveDialog(SynthEditorPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if(!file.getAbsolutePath().endsWith(".wsynth")) {
						file = new File(file.getAbsolutePath() + ".wsynth");
					}
					if(file.exists()) {
						String[] options = {"Abort", "Overwrite"};
						int overwrite = JOptionPane.showOptionDialog(null, "The file \"" + file.getName() + "\" already exists!\nDo you want  to overwrite it?", "Overwrite existing file?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
						if(overwrite == 0) return;
					}
					
					synthFile = file;
					
					String synthName = file.getName();
					synthName = synthName.substring(0, synthName.lastIndexOf(".wsynth"));
					
					WorldSynthMainUI.renameSynth(synth, synthName);
				}
			}
			
			ProjectWriter.writeSynthToFile(synth, synthFile);
			registerSavePerformed();
		}
		
	}
}
