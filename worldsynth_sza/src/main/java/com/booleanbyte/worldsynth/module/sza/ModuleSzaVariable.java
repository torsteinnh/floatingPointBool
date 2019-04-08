package com.booleanbyte.worldsynth.module.sza;

import java.awt.GridBagLayout;
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
import com.booleanbyte.worldsynth.standalone.ui.parameters.StringParameterField;

public class ModuleSzaVariable extends AbstractModule {
	
	private String name = "f";
	
	@Override
	public AbstractDatatype buildModule(Map<String, AbstractDatatype> inputs, OutputRequest request) {
		DatatypeSza requestData = (DatatypeSza) request.data;
		
		requestData.plastique = name;
		
		return requestData;
	}

	@Override
	public Map<String, InputRequest> getInputRequests(OutputRequest outputRequest) {
		HashMap<String, InputRequest> inputRequests = new HashMap<String, InputRequest>();
		return inputRequests;
	}

	@Override
	public String getModuleName() {
		return "SZA VARIABLE";
	}

	@Override
	public IModuleClass getModuleClass() {
		return ModuleClass.GENERATOR;
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] i = {
				};
		return i;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] o = {
				new ModuleOutput(new DatatypeSza(), "o1")
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
		
		//////////Parameters //////////
		StringParameterField parameterConstant = new StringParameterField("Varaible name", name);
		
		try {
			parameterConstant.addToGrid(uiPanel, 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//////////
		
		ActionListener applyAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				name = parameterConstant.getValue();
			}
		};
		
		return applyAction;
	}

	@Override
	public ArrayList<Element> toElementList() {
		ArrayList<Element> paramenterElements = new ArrayList<Element>();
		
		paramenterElements.add(new Element("name", name));
		
		return paramenterElements;
	}

	@Override
	public void fromElement(Element element) {
		for(Element e: element.elements) {
			if(e.tag.equals("name")) {
				name = e.content;
			}
		}
	}
}
