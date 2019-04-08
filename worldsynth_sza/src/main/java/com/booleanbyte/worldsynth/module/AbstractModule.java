package com.booleanbyte.worldsynth.module;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.device.Device;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.event.module.ModuleIoChangeEvent;
import com.booleanbyte.worldsynth.event.module.ModuleIoChangeListener;

/**
 * This is the abstract class for a module, to register a module for use in constructing
 * a device from, it has to be an extension of this class.
 * This class specifies the methods that is needed to create a module that can cooperate with
 * WorldSynth in any form. And also contains the important datastructures of a module and their
 * definition.
 */
public abstract class AbstractModule {
	
	private ModuleIoChangeListener moduleListener = null;
	private Device wrapperDevice = null;
	
	/**
	 * The array that holds the {@link ModuleInput} objects that specify the inputs to the module.<br>
	 * {@link ModuleInput} is an extension of {@link ModuleIO} and is used by the parent
	 * {@link Device} object that wraps the module and the rendering core to handle data exchange
	 * between devices so it comes as in the right {@link AbstractDatatype} at the right place.
	 */
	protected ModuleInput[] inputs;
	
	/**
	 * The array that holds the {@link ModuleOutput} objects that specify the inputs to the module.<br>
	 * {@link ModuleOutput} is an extension of {@link ModuleIO} and is used by the parent
	 * {@link Device} object that wraps the module and the rendering core to handle data exchange
	 * between devices so it comes as in the right {@link AbstractDatatype} at the right place.
	 */
	protected ModuleOutput[] outputs;
	
	private boolean bypassed = false;
	
	protected void preInit() {
	}
	
	public final void init() {
		preInit();
		inputs = registerInputs();
		outputs = registerOutputs();
	}
	
	public final void registerWrapper(Device wrapperDevice) {
		this.wrapperDevice = wrapperDevice;
	}
	
	/**
	 * Sets whether the module is bypassed, this is called by the device that wraps the module
	 * and should not be called from anywhere else.
	 * @param bypass {@code true} sets the module as bypassed
	 */
	public final void setBypassed(boolean bypass) {
		bypassed = bypass && isBypassable();
	}
	
	/**
	 * Gets whether the module is bypassed
	 * @return {@code true} if the module is bypassed, {@code false} if not.
	 */
	public final boolean isBypassed() {
		return bypassed;
	}
	
	/**
	 * This function is dedicated to building the module based on the output request and the
	 * data input to the module. The Output request will specify what output there is a request
	 * for, and the returned data should be the data that belongs to this output if there is
	 * multiple outputs from the module.<br>
	 * If a module has inputs that is not connected to anything else, the data could not be built
	 * earlier in the module chain, the input will contain a {@code null} value, so a check for null
	 * on all inputs with corresponding plans if it is null should be made.
	 * Preferably if the module could not be built it should return null.
	 * 
	 * @param inputs
	 * @param request
	 * @return
	 */
	public abstract AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request);
	
	/**
	 * This method is used to design all the inputrequests for the module, potentially
	 * according to an outputrequest.<br>
	 * An {@link ModuleInputRequest} holds is initiated using the {@link ModuleInput} and
	 * an instance of the datatype extending {@link AbstractDatatype} that is applicable
	 * to the given input. The information the datatype needs to contain for being used
	 * as request data depends on the dataype used, but the data contained will usually
	 * specify the required area/volume/or other bounds of the data that the  wants
	 * and that should be built by the proceeding module to that builds the wanted data.<br>
	 * All the {@link ModuleInputRequest} instances needs to be added to an ArrayList that is returned.
	 * <br><br>
	 * Example of an {@link ModuleInputRequest} for a heightmap:<br>
	 * {@code DatatypeHeightmap heightdata = new DatatypeHeightmap(x, y, width, height, resolution);}<br>
	 * {@code ModuleInputRequest ir0 = new ModuleInputRequest(inputs[0], heightdata);}<br><br>
	 * This will create an {@link ModuleInputRequest} to the {@link ModuleInput} of index 0, for a heightmap
	 * within the square described by starting in the coordinate (x, y), and having the width and
	 * height defined, and a distance between every datpoint in the heighmap equal to the resolution value.
	 * <br><br>
	 * The {@code ModuleOutputRequest} parameter of the method contains data with the same kind of
	 * structuring defining the bound of the data it wants. This can be used to define the
	 * necessary bounds of the data this module needs to request to be able to build the requested
	 * data. If the datatype is the same and there is not a need for changing the bounds of the
	 * data to be bigger(or smaller), the {@code data} field can often be extracted from the {@link ModuleOutputRequest}
	 * and be used directly in the {@link ModuleInputRequest}.
	 * 
	 * @param outputRequest The output request containing the {@link ModuleOutput} there is requested data from,
	 * and the {@code data} that is requested
	 * @return An {@link ArrayList} containing elements of {@link ModuleInputRequest} for all the inputs to the module
	 */
	public abstract Map<String, InputRequest> getInputRequests(OutputRequest outputRequest);
	
	/**
	 * The name of the module is a descriptive name that tells the user what this module does.
	 * It will be the name that it is shown by in the menu for adding devices, and the device
	 * will take on this name and show it over the top of the device in the device editor. If
	 * the device i given a custom name, this will still be shown in parentheses to indicate
	 * the function of the device.
	 * 
	 * @return The name of this module
	 */
	public abstract String getModuleName();
	
	/**
	 * The meta tag of the module is a descriptive tag that can be used to tell the user additional
	 * information about the module instance if it for example has several operation modes, an example
	 * of a module with multiple operation modes could be a combiner. Without a meta tag it will show
	 * up as a combiner, but the user has no way of knowing what operation mode it is in; addition, subtraction,
	 * multiply..., unless the user makes sure to give it a descriptive name.<br>
	 * If a meta tag is used it will not be shown in the menu for adding devices, but the device
	 * will take on this meta tag and show it in square brackets "[]" as a part of the module name
	 * over the top of the device in the device editor.<br>
	 * This method by default returns null. A null indicates that there is no meta tag applied to the
	 * module and there is not shown any meta tag together with the module name in this case. 
	 * 
	 * @return A string naming the meta tag of the module, null if no tag is to be applied.
	 */
	public String getModuleMetaTag() {
		return null;
	}
	
	/**
	 * @return The {@link IModuleClass} category that this module belongs to
	 */
	public abstract IModuleClass getModuleClass();
	
	/**
	 * Used by the rendering of the device in deviceview for coloring the device
	 * that wraps the module. By default it returns the color set for the ModuleClass
	 * the module is defined as. But it can be overrided to set a unique color.
	 * @return The {@link Color} to render the device containing this module
	 * in when viewed in the device editor.
	 */
	public Color getModuleColor() {
		return getModuleClass().classColor();
	}
	
	/**
	 * @return An array of {@link ModuleInput}. A module input contains an empty instance
	 * of the {@code datatype} extending {@link AbstractDatatype} that that the input takes,
	 * and the name of the input. The name should be unique among the inputs and outputs on
	 * a device. The order they are listed in the array, is the order they appear in on the
	 * device that wraps the module.
	 */
	public abstract ModuleInput[] registerInputs();
	
	public final ModuleInput[] getInputs() {
		return inputs;
	}
	
	public final ModuleInput getInput(int index) {
		return inputs[index];
	}
	
	public final ModuleInput getInput(String name) {
		for(ModuleInput input: inputs) {
			if(input.getName().equals(name)) {
				return input;
			}
		}
		return null;
	}
	
	/**
	 * @return An array of {@link ModuleOutput}. A module output contains an empty instance
	 * of the {@code datatype} extending {@link AbstractDatatype} that that the output gives,
	 * and the name of the output. The name should be unique among the inputs and outputs on
	 * a device. The order they are listed in the array, is the order they appear in on the
	 * device that wraps the module.
	 */
	public abstract ModuleOutput[] registerOutputs();
	
	public final ModuleOutput[] getOutputs() {
		return outputs;
	}
	
	public final ModuleOutput getOutput(int index) {
		return outputs[index];
	}
	
	public final ModuleOutput getOutput(String name) {
		for(ModuleOutput output: outputs) {
			if(output.getName().equals(name)) {
				return output;
			}
		}
		return null;
	}
	
	/**
	 * This method is used in the case of a need for changes to the IO of a module when it's been initiated.
	 * Calling this method will cause the module to register IO again by running the two methods
	 * {@link #registerInputs()} and {@link #registerOutputs()} again, and update the wrapping device
	 * about a change in IO.
	 */
	protected final void reregisterIO() {
		inputs = registerInputs();
		outputs = registerOutputs();
		notifyModuleListenerIoChange();
	}
	
	public final void setModuleListener(ModuleIoChangeListener listener) {
		moduleListener = listener;
	}
	
	private final void notifyModuleListenerIoChange() {
		if(moduleListener != null) {
			moduleListener.ioChanged(new ModuleIoChangeEvent("Module IO changed", this));
		}
	}
	
	/**
	 * Defines whether the device wrapping this module will be possible to bypass.
	 * @return {@code true} if the module is bypassable, and {@code false} if not.
	 */
	public abstract boolean isBypassable();
	
	/**
	 * This method is used to create the UI for a module. This will be the UI that is
	 * displayed when a device in deviceview is doubleclicked on or its parameter menu
	 * is used when the device is right clicked on.
	 * This is used to adjust the parameters of the modules algorithm available to the user.<br>
	 * The UI is defined by making changes to the {@link JPanel} parameter {@code uiPanel} passed to the
	 * method, this will be contained inside the window for the UI. Buttons for applying or canceling
	 * the actions taken is added automatically externally, and is not to be added in this {@link JPanel}.<br>
	 * The function is expected to return a {@link ActionListener} that defines the behavior for applying
	 * the parameters from the UI into the when the user presses either the "Apply" or "OK" button.
	 * The "Apply" button will only apply the parameters, while the "OK" button will also close the UI.
	 * 
	 * @param uiPanel The {@link JPanel} in the UI to contain the module-specific UI layout
	 * @return {@link ActionListener} for apply event handling internal in the module
	 */
	public abstract ActionListener moduleUI(JPanel uiPanel);
	
	
	
	/**
	 * Used in the process of building a node tree, to pass a request for data from a devices module.
	 * This is the request received by a module for it to determine what data to build and what
	 * inputs it needs to build, so it can construct the corresponding inputrequests to send out.
	 */
	public class OutputRequest {
		public ModuleOutput output;
		public AbstractDatatype data;
		
		public OutputRequest(ModuleOutput output, AbstractDatatype data) {
			this.output = output;
			this.data = data;
		}
	}
	
	/**
	 * Used in the process of building a node tree, to carry a request for data to a device module.
	 * This is the request constructed by a module based on the outputrequest it takes in.
	 */
	public class InputRequest {
		public ModuleInput input;
		public AbstractDatatype data;
		
		public InputRequest(ModuleInput input, AbstractDatatype data) {
			this.input = input;
			this.data = data;
		}
		
		public ModuleInput getInput() {
			return input;
		}
		
		public AbstractDatatype getData() {
			return data;
		}
	}
	
	@Override
	public final String toString() {
		return getModuleName();
	}
	
	/**
	 * Used to create an {@link Element} that contains the parameters of the module that is needed
	 * to save its state.
	 * @return An element that represents the module
	 */
	public Element toElement() {
		return new Element("module \"" + getModuleName() + "\"", toElementList());
	}
	
	/**
	 * This method is used to create a {@link ArrayList} of {@link Element}. This list should
	 * contain elements containing the content of the different parameters for the module to
	 * recreate it to the same state as it is when saved.
	 * 
	 * @return An {@link ArrayList} containing the {@link Element} objects containing the necessary 
	 * data to recreate the module in the same state  as when saving it.
	 */
	public abstract ArrayList<Element> toElementList();
	
	/**
	 * This method is used to convert an element containing the elements created in the {@code toElementList} method.
	 * To decode them use a for loop like {@code for(Element e: element.elements)} to cycle trough them and check
	 * the tag to identify the element and parse its content to the desired type. The content is stored as string
	 * and needs to be converted to the correct type.
	 * 
	 * @param element The parent element containing all the stored elements created by the {@code toElementList} method
	 */
	//TODO Maybe this should be from element list
	public abstract void fromElement(Element element);
	
	/**
	 * This method is used by a module to get data built for an input to itself. The use of this method is to
	 * be avoided if it's reasonably possible. It's intended use is for modules where one or multiple inputs
	 * needs to be known before the correct or necessary inputrequests can be registered in the
	 * {@link #getInputRequests(OutputRequest) getInputRequests}, or when an input needs to be built on demand
	 * from the module's UI.
	 * <br><br>
	 * Examples of these two cases are an selector with high input count where possibly few
	 * of them are used at the time, then this method can be used to early get the input for selection and only
	 * registering inputrequests for the inputs that will be used with the specific input data, as for example in
	 * a selector device to reduce unnecessary builds that will never be used and thus increase performance.
	 * <br>
	 * An example of the use from module UI is for an exporter device that wants to build a specific dataset
	 * to save to file, and thus needs to be able to the get the necessary data built for this.
	 * 
	 * @param request An {@link InputRequest} for the data to build
	 * @return
	 */
	public final AbstractDatatype buildInputData(InputRequest request) {
		return wrapperDevice.buildInputData(request);
	}
}
