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
import com.booleanbyte.worldsynth.standalone.ui.parameters.EnumParameterDropdownSelector;

public class ModuleScalarCombiner extends AbstractModule {
	
	Operation operation = Operation.ADDITION;
	
	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		DatatypeScalar requestData = (DatatypeScalar) request.data;
		
		if(inputs.get("primary") == null || inputs.get("secondary") == null) {
			//If the primary or secondary input is null, there is not enough input and then just return null
			return null;
		}
		
		double i0 = (double) ((DatatypeScalar) inputs.get("primary")).data;
		double i1 = (double) ((DatatypeScalar) inputs.get("secondary")).data;
		
		double o = 0.0;
		switch (operation) {
		case ADDITION:
			o = i0 + i1;
			break;
		case SUBTRACTION:
			o = i0 - i1;
			break;
		case MULTIPLICATION:
			o = i0 * i1;
			break;
		case DIVISION:
			o = i0 / i1;
			break;
		}
		
		requestData.data = o;
		
		return requestData;
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		
		inputRequests.put("primary", new InputRequest(getInput(0), outputRequest.data));
		inputRequests.put("secondary", new InputRequest(getInput(1), outputRequest.data));
		
		return inputRequests;
	}

	@Override
	public String getModuleName() {
		return "Scalar combiner";
	}
	
	@Override
	public String getModuleMetaTag() {
		return operation.name().substring(0, 3);
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.COMBINER;
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] i = {
				new ModuleInput(new DatatypeScalar(), "Primary input"),
				new ModuleInput(new DatatypeScalar(), "Secondary input")
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
		EnumParameterDropdownSelector<Operation> parameterOperation = new EnumParameterDropdownSelector<Operation>("Arithmetic operation", Operation.class, operation);
		
		try {
			parameterOperation.addToGrid(uiPanel, 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//////////
		
		ActionListener applyAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				operation = parameterOperation.getValue();
			}
		};
		
		return applyAction;
	}

	@Override
	public ArrayList<Element> toElementList() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("operation", operation.name()));
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("operation")) {
				for(Operation type: Operation.values()) {
					if(e.content.equals(type.name())) {
						operation = type;
						break;
					}
				}
			}
		}
	}
	
	private enum Operation {
		ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION;
	}
}
