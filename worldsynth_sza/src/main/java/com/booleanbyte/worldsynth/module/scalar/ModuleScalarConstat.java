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

public class ModuleScalarConstat extends AbstractModule {
	
	double constant;
	
	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		DatatypeScalar requestData = (DatatypeScalar) request.data;
		requestData.data = constant;
		return requestData;
	}
	
	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		return inputRequests;
	}

	@Override
	public ModuleInput[] registerInputs() {
		return null;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] o = {new ModuleOutput(new DatatypeScalar(), "output")};
		return o;
	}

	@Override
	public ActionListener moduleUI(JPanel uiPanel) {
		
		uiPanel.setLayout(new GridBagLayout());
		
		////////// Parameters //////////
		DoubleParameterSlider parameterConstant = new DoubleParameterSlider("Constant", 0.0, 1.0, constant, 1000);
		
		try {
			parameterConstant.addToGrid(uiPanel, 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//////////
		
		ActionListener applyAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				constant = parameterConstant.getValue();
			}
		};
		
		return applyAction;
	}

	@Override
	public String getModuleName() {
		return "Scalar constant";
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.GENERATOR;
	}

	@Override
	public boolean isBypassable() {
		return false;
	}
	
	@Override
	public ArrayList<Element> toElementList() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("constant", String.valueOf(constant)));
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("constant")) {
				constant = Double.parseDouble(e.content);
			}
		}
	}
}
