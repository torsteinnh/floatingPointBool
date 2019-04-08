package com.booleanbyte.worldsynth.common.device;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import com.booleanbyte.worldsynth.common.Synth;
import com.booleanbyte.worldsynth.common.WorldSynthCore;
import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.event.module.ModuleIoChangeEvent;
import com.booleanbyte.worldsynth.event.module.ModuleIoChangeListener;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.AbstractModule.InputRequest;
import com.booleanbyte.worldsynth.module.AbstractModule.OutputRequest;
import com.booleanbyte.worldsynth.module.AbstractModuleRegister.ModuleEntry;
import com.booleanbyte.worldsynth.module.ModuleIO;
import com.booleanbyte.worldsynth.module.ModuleOutput;

/**
 * The {@link Device} is a wrapper for the {@link AbstractModule}.
 */

public class Device {
	
	private Synth memberSynth = null;
	
	public static int nextDeviceID = 1;
	public int deviceID;
	
	public String customName = "";
	
	public float posX;
	public float posY;
	
	public float deviceWidth = 100;
	public float deviceHeight = 35;
	
	public AbstractModule module;
	
	public DeviceIO[] deviceInputs;
	public DeviceIO[] deviceOutputs;
	
	public Device(AbstractModule module, float posX, float posY) {
		this.module = module;
		this.posX = posX;
		this.posY = posY;
		
		buildDeviceForModule(module);
		
		deviceID = nextDeviceID;
		nextDeviceID++;
	}
	
	public Device(Element element) {
		fromElement(element);
		
		buildDeviceForModule(module);
	}
	
	public void reidentify() {
		deviceID = nextDeviceID++;
	}
	
	public void registerMemberSynth(Synth memberSynth) {
		this.memberSynth = memberSynth;
	}
	
	private void buildDeviceForModule(AbstractModule module) {
		module.registerWrapper(this);
		if(module.getInputs() != null) {
			ArrayList<DeviceIO> input = new ArrayList<DeviceIO>();
			for(int i = 0; i < module.getInputs().length; i++) {
				ModuleIO io = module.getInputs()[i];
				DeviceIO deviceInput = new DeviceIO(io, 0, 0 + 10 + 15*i, true);
				input.add(deviceInput);
			}
			deviceInputs = new DeviceIO[input.size()];
			deviceInputs = input.toArray(deviceInputs);
		}
		else {
			deviceInputs = new DeviceIO[0];
		}
		
		if(module.getOutputs() != null) {
			ArrayList<DeviceIO> output = new ArrayList<DeviceIO>();
			for(int i = 0; i < module.getOutputs().length; i++) {
				ModuleIO io = module.getOutputs()[i];
				DeviceIO deviceOutput = new DeviceIO(io, deviceWidth-10, 0 + 10 + 15*i, false);
				output.add(deviceOutput);
			}
			deviceOutputs = new DeviceIO[output.size()];
			deviceOutputs = output.toArray(deviceOutputs);
		}
		else {
			deviceOutputs = new DeviceIO[0];
		}
		
		float inputHeight = 0;
		if(module.getInputs() != null) {
			inputHeight = module.getInputs().length * 15 + 20;
		}
		float outputHeight = 0;
		if(module.getOutputs() != null) {
			outputHeight = module.getOutputs().length * 15 + 20;
		}
		
		if(deviceHeight < inputHeight) {
			deviceHeight = inputHeight;
		}
		if(deviceHeight < outputHeight) {
			deviceHeight = outputHeight;
		}
		
		module.setModuleListener(new ModuleIoChangeListener() {
			
			@Override
			public void ioChanged(ModuleIoChangeEvent e) {
				reBuildDeviceForModule(module);
			}
		});
	}
	
	private void reBuildDeviceForModule(AbstractModule module) {
		//Make a list of the current device connectors that are connected to the device so connectors connected to later removed IOs can be removed
		DeviceConnector[] currentConnectors = memberSynth.getConnectorsByDevice(this);
		
		//Only create new DeviceIO objects for ModuleIO that is new, the rest can be reused
		if(module.getInputs() != null) {
			ArrayList<DeviceIO> input = new ArrayList<DeviceIO>();
			for(int i = 0; i < module.getInputs().length; i++) {
				//Get the ModuleIO and create a DeviceIO
				ModuleIO moduleIo = module.getInputs()[i];
				DeviceIO deviceInput = new DeviceIO(moduleIo, 0, 0 + 10 + 15*i, true);
				
				//Check if there is an existing DeviceIO for this ModuleIO and overwrite the new DeviceIO with the existing
				//This check is done using the name of the ModuleIO
				for(DeviceIO dio: deviceInputs) {
					if(dio.io.getName().equals(moduleIo.getName())) {
						deviceInput = dio;
						deviceInput.io = moduleIo;
						break;
					}
				}
				
				input.add(deviceInput);
			}
			deviceInputs = new DeviceIO[input.size()];
			deviceInputs = input.toArray(deviceInputs);
		}
		//Only create new DeviceIO objects for ModuleIO that is new, the rest can be reused
		if(module.getOutputs() != null) {
			ArrayList<DeviceIO> output = new ArrayList<DeviceIO>();
			for(int i = 0; i < module.getOutputs().length; i++) {
				//Get the ModuleIO and create a DeviceIO
				ModuleIO moduleIo = module.getOutputs()[i];
				DeviceIO deviceOutput = new DeviceIO(moduleIo, deviceWidth-10, 0 + 10 + 15*i, false);
				
				//Check if there is an existing DeviceIO for this ModuleIO and overwrite the new DeviceIO with the existing
				//This check is done using the name of the ModuleIO
				for(DeviceIO dio: deviceOutputs) {
					if(dio.io.getName().equals(moduleIo.getName())) {
						deviceOutput = dio;
						deviceOutput.io = moduleIo;
						break;
					}
				}
				
				output.add(deviceOutput);
			}
			deviceOutputs = new DeviceIO[output.size()];
			deviceOutputs = output.toArray(deviceOutputs);
		}
		
		//calculate the graphical dimensions of the device
		float inputHeight = 0;
		if(module.getInputs() != null) {
			inputHeight = module.getInputs().length * 15 + 20;
		}
		float outputHeight = 0;
		if(module.getOutputs() != null) {
			outputHeight = module.getOutputs().length * 15 + 20;
		}
		
		deviceHeight = 35;
		if(deviceHeight < inputHeight) {
			deviceHeight = inputHeight;
		}
		if(deviceHeight < outputHeight) {
			deviceHeight = outputHeight;
		}
		
		//TODO Consider if this should be done in the synth itself by a iochange listener
		//Cleanup the synth this module is a member of for the new unconnected connectors
		for(DeviceConnector dc: currentConnectors) {
			boolean connected = false;
			for(DeviceIO dio: deviceInputs) {
				if(dc.deviceInput == dio) {
					connected = true;
					break;
				}
			}
			for(DeviceIO dio: deviceOutputs) {
				if(dc.deviceOutput == dio) {
					connected = true;
					break;
				}
			}
			
			if(!connected) {
				memberSynth.removeDeviceConnector(dc);
			}
		}
	}
	
	public boolean isBypassable() {
		return module.isBypassable();
	}
	
	public void setBypassed(boolean bypass) {
		module.setBypassed(bypass);
	}
	
	public boolean isBypassed() {
		return module.isBypassed();
	}
	
	public DeviceIO isOverDeviceIO(float posX, float posY) {
		if(deviceInputs != null) {
			for(DeviceIO io: deviceInputs) {
				if(posX > io.posX && posX < io.posX+io.ioRenderSize && posY > io.posY && posY < io.posY+io.ioRenderSize) {
					return io;
				}
			}
		}
		if(deviceOutputs != null) {
			for(DeviceIO io: deviceOutputs) {
				if(posX > io.posX && posX < io.posX+io.ioRenderSize && posY > io.posY && posY < io.posY+io.ioRenderSize) {
					return io;
				}
			}
		}
		return null;
	}
	
	public DeviceIO getDeviceIOByModuleIO(ModuleIO moduleIO) {
		if(deviceInputs != null) {
			for(DeviceIO io: deviceInputs) {
				if(io.io == moduleIO) {
					return io;
				}
			}
		}
		if(deviceOutputs != null) {
			for(DeviceIO io: deviceOutputs) {
				if(io.io == moduleIO) {
					return io;
				}
			}
		}
		return null;
	}
	
	public class DeviceIO {
		
		private ModuleIO io;
		private boolean isInput;
		
		public float ioRenderSize = 10;
		
		/**
		 * Relative position in device
		 */
		public float posX;
		
		/**
		 * Relative position in device
		 */
		public float posY;
		
		public DeviceIO(ModuleIO io, float posX, float posY, boolean isInput) {
			this.io = io;
			this.posX = posX;
			this.posY = posY;
			this.isInput = isInput;
		}
		
		public boolean isInput() {
			return isInput;
		}

		public AbstractDatatype getDatatype() {
			return io.getData();
		}
		
		public ModuleIO getIO() {
			return io;
		}
		
		public String getName() {
			return io.getName();
		}
		
		@Override
		public String toString() {
			return io.getName() + " (" + io.getData().getDatatypeName() + ")";
		}
	}
	
	public DeviceNameEditor openDeviceNameEditor() {
		return new DeviceNameEditor();
	}

	public String toString() {
		if(!customName.equals("")) {
			if(module.getModuleMetaTag() != null) {
				return customName + " (" + module.getModuleName() + " [" + module.getModuleMetaTag()+ "])";
			}
			return customName + " (" + module.getModuleName()+ ")";
		}
		if(module.getModuleMetaTag() != null) {
			return module.getModuleName() + " [" + module.getModuleMetaTag()+ "]";
		}
		return module.getModuleName();
	}
	
	public class DeviceNameEditor extends JFrame {
		private static final long serialVersionUID = -6316781388913465890L;
		
		JTextField customNameField;
		
		protected EventListenerList listenerList = new EventListenerList();
		
		public DeviceNameEditor() {
			setTitle(module.getModuleName() + " custom name editor");
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLayout(new BorderLayout());
			setAlwaysOnTop(true);
			
			customNameField = new JTextField(customName, 20);
			
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			namePanel.add(new JLabel("Custom name: "));
			namePanel.add(customNameField);
			add(namePanel, BorderLayout.CENTER);
			
			JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					fireActionPerformed(new ActionEvent(DeviceNameEditor.this, ActionEvent.ACTION_PERFORMED, "Cancel"));
					dispose();
				}
			});
			
			JButton applyButton = new JButton("Apply");
			applyButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					customName = customNameField.getText();
					fireActionPerformed(new ActionEvent(DeviceNameEditor.this, ActionEvent.ACTION_PERFORMED, "Apply"));
				}
			});
			
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					customName = customNameField.getText();
					fireActionPerformed(new ActionEvent(DeviceNameEditor.this, ActionEvent.ACTION_PERFORMED, "OK"));
					dispose();
				}
			});
			
			bPanel.add(cancelButton);
			bPanel.add(applyButton);
			bPanel.add(okButton);
			add(bPanel, BorderLayout.PAGE_END);
			
			pack();
			setVisible(true);
		}
		
		public void addActionListener(ActionListener l) {
	        listenerList.add(ActionListener.class, l);
	    }
		
		protected void fireActionPerformed(ActionEvent event) {
	        // Guaranteed to return a non-null array
	        Object[] listeners = listenerList.getListenerList();
	        ActionEvent e = null;
	        // Process the listeners last to first, notifying
	        // those that are interested in this event
	        for (int i = listeners.length-2; i>=0; i-=2) {
	            if (listeners[i]==ActionListener.class) {
	                // Lazily create the event:
	            	String actionCommand = event.getActionCommand();
                    e = new ActionEvent(DeviceNameEditor.this, ActionEvent.ACTION_PERFORMED, actionCommand, event.getWhen(), event.getModifiers());
	                ((ActionListener)listeners[i+1]).actionPerformed(e);
	            }
	        }
	    }
	}
	
	public Element toElement() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("id", String.valueOf(deviceID)));
		paramenterElements.add(new Element("name", customName));
		paramenterElements.add(new Element("x", String.valueOf(posX)));
		paramenterElements.add(new Element("y", String.valueOf(posY)));
		paramenterElements.add(new Element("bypass", String.valueOf(isBypassed())));
		paramenterElements.add(new Element("moduleclass", module.getClass().getName()));
		paramenterElements.add(new Element("module", module.toElement()));
		
		Element moduleElement = new Element("device " + deviceID, paramenterElements);
		return moduleElement;
	}
	
	public void fromElement(Element element) {
		
		boolean bypass = false;
		
		String moduleclass = null;
		Element moduleElement = null;
		
		for(Element e: element.elements) {
			if(e.tag.equals("name")) {
				if(e.content != null) {
					customName = e.content;
				}
			}
			else if(e.tag.equals("id")) {
				deviceID = Integer.parseInt(e.content);
			}
			else if(e.tag.equals("x")) {
				posX = Float.parseFloat(e.content);
			}
			else if(e.tag.equals("y")) {
				posY = Float.parseFloat(e.content);
			}
			else if(e.tag.equals("bypass")) {
				bypass = Boolean.parseBoolean(e.content);
			}
			else if(e.tag.equals("moduleclass")) {
				moduleclass = e.content;
			}
			else if(e.tag.equals("module")) {
				moduleElement = e.elements.get(0);
			}
		}
		if(moduleclass != null && moduleElement != null) {
			try {
				for(ModuleEntry moduleEntry: WorldSynthCore.moduleRegister.getRegisteredModuleEntries()) {
					Class<? extends AbstractModule> c = moduleEntry.getModuleClass();
					if(c.getName().equals(moduleclass)) {
						AbstractModule module = WorldSynthCore.constructModule(c, moduleElement);
						this.module = module;
					}
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		
		setBypassed(bypass);
	}
	
	public AbstractDatatype buildInputData(InputRequest request) {
		//Transform inputrequests into output requests if possible
		DeviceIO input = getDeviceIOByModuleIO(request.getInput());
		DeviceConnector[] connectors = memberSynth.getConnectorsByDeviceIO(input);
		DeviceConnector connector = null;
		if(connectors.length > 0) {
			connector = connectors[0];
		}
		if(connector == null) {
			return null;
		}
		Device requestedDevice = connector.outputDevice;
		
		AbstractModule requestedModule = requestedDevice.module;
		DeviceIO requestedDeviceIO = connector.deviceOutput;
		ModuleOutput requestedModuleOutput = (ModuleOutput) requestedDeviceIO.getIO();
		
		OutputRequest outputRequest = requestedModule.new OutputRequest(requestedModuleOutput, request.getData());
		
		return WorldSynthCore.getDeviceOutput(memberSynth, requestedDevice, outputRequest);
	}
}
