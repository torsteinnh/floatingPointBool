package com.booleanbyte.worldsynth.module.scalar;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.datatype.DatatypeScalar;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.IModuleClass;
import com.booleanbyte.worldsynth.module.ModuleClass;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;
import com.booleanbyte.worldsynth.standalone.ui.parameters.DoubleParameterSlider;

public class ModuleScalarClamp extends AbstractModule {
	
	double lowClamp = 0.0;
	double highClamp = 1.0;

	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		DatatypeScalar requestData = (DatatypeScalar) request.data;
		
		if(inputs.get("input") == null) {
			//If the main input is null, there is not enough input and then just return null
			return null;
		}
		if(inputs.get("high") != null) {
			highClamp = ((DatatypeScalar) inputs.get("high")).data;
		}
		if(inputs.get("low") != null) {
			lowClamp = ((DatatypeScalar) inputs.get("low")).data;
		}
		
		double i0 = ((DatatypeScalar) inputs.get("input")).data;
		double o = clamp(i0);
		
		requestData.data = o;
		
		return requestData;
	}
	
	private double clamp(double height) {
		if(height > highClamp) height = highClamp;
		else if(height < lowClamp) height = lowClamp;
		return height;
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		
		inputRequests.put("input", new InputRequest(getInput(0), outputRequest.data));
		inputRequests.put("high", new InputRequest(getInput(1), outputRequest.data));
		inputRequests.put("low", new InputRequest(getInput(2), outputRequest.data));
		
		return inputRequests;
	}

	@Override
	public String getModuleName() {
		return "Scalar clamp";
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.MODIFIER;
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] i = {
				new ModuleInput(new DatatypeScalar(), "Primary input"),
				new ModuleInput(new DatatypeScalar(), "High clamp"),
				new ModuleInput(new DatatypeScalar(), "Low clamp")
				};
		return i;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] o = {
				new ModuleOutput(new DatatypeScalar(), "Primary output")
				};
		return o;
	}

	@Override
	public boolean isBypassable() {
		return false;
	}
	
	@Override
	public ActionListener moduleUI(JPanel uiPanel) {
		uiPanel.setLayout(new GridBagLayout());
		
		////////// Parameters //////////
		
		DoubleParameterSlider parameterHighClamp = new DoubleParameterSlider("High clamp", 0.0, 1.0, highClamp, 1000);
		DoubleParameterSlider parameterLowClamp = new DoubleParameterSlider("Low clamp", 0.0, 1.0, lowClamp, 1000);
		
		try {
			parameterHighClamp.addToGrid(uiPanel, 0);
			parameterLowClamp.addToGrid(uiPanel, 1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//////////
		
		ActionListener applyAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				highClamp = parameterHighClamp.getValue();
				lowClamp = parameterLowClamp.getValue();
			}
		};
		
		return applyAction;
	}

	@Override
	public ArrayList<Element> toElementList() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("lowclamp", String.valueOf(lowClamp)));
		paramenterElements.add(new Element("highclamp", String.valueOf(highClamp)));
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("lowclamp")) {
				lowClamp = Double.parseDouble(e.content);
			}
			else if(e.tag.equals("highclamp")) {
				highClamp = Double.parseDouble(e.content);
			}
		}
	}

}
