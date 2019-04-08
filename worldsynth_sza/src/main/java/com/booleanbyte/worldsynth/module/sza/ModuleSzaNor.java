package com.booleanbyte.worldsynth.module.sza;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.booleanbyte.worldsynth.common.datatype.AbstractDatatype;
import com.booleanbyte.worldsynth.common.datatype.DatatypeSza;
import com.booleanbyte.worldsynth.common.io.Element;
import com.booleanbyte.worldsynth.module.AbstractModule;
import com.booleanbyte.worldsynth.module.IModuleClass;
import com.booleanbyte.worldsynth.module.ModuleClass;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;

public class ModuleSzaNor extends AbstractModule {
	
	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		DatatypeSza requestData = (DatatypeSza) request.data;
		
		if(inputs.get("a") == null || inputs.get("b") == null) {
			return null;
		}
		
		String a = ((DatatypeSza) inputs.get("a")).plastique;
		String b = ((DatatypeSza) inputs.get("b")).plastique;
		
		requestData.plastique = "(-(" + a + "+" + b + "))";
		requestData.simplify();
		
		return requestData;
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		
		inputRequests.put("a", new InputRequest(getInput(0), outputRequest.data));
		inputRequests.put("b", new InputRequest(getInput(1), outputRequest.data));
		
		return inputRequests;
	}

	@Override
	public String getModuleName() {
		return "SZA NOR";
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.COMBINER;
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] i = {
				new ModuleInput(new DatatypeSza(), "a"),
				new ModuleInput(new DatatypeSza(), "b")
				};
		return i;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] o = {
				new ModuleOutput(new DatatypeSza(), "o")
				};
		return o;
	}

	@Override
	public boolean isBypassable() {
		return false;
	}
	
	@Override
	public ActionListener moduleUI(JPanel uiPanel) {
		ActionListener applyAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		};
		
		return applyAction;
	}

	@Override
	public ArrayList<Element> toElementList() {
		return new ArrayList<Element>();
	}

	@Override
	public void fromElement(Element element) {
	}
}
