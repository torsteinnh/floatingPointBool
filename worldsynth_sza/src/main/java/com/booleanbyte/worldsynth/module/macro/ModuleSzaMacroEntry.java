package com.booleanbyte.worldsynth.module.macro;

import com.booleanbyte.worldsynth.common.datatype.DatatypeSza;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;

public class ModuleSzaMacroEntry extends AbstractModuleMacroEntry {

	@Override
	public String getModuleName() {
		return "Macro entry (SZA)";
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] in = {
				new ModuleInput(new DatatypeSza(), "Macro entry [" + macroIoId + "]", true)
		};
		return in;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] out = {
				new ModuleOutput(new DatatypeSza(), "Macro entry [" + macroIoId + "]")
		};
		return out;
	}

}
