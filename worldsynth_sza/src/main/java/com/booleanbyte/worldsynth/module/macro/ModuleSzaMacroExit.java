package com.booleanbyte.worldsynth.module.macro;

import com.booleanbyte.worldsynth.common.datatype.DatatypeSza;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;

public class ModuleSzaMacroExit extends AbstractModuleMacroExit {


	@Override
	public String getModuleName() {
		return "Macro exit (SZA)";
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] in = {
				new ModuleInput(new DatatypeSza(), "Macro exit [" + macroIoId + "]")
		};
		return in;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] out = {
				new ModuleOutput(new DatatypeSza(), "Macro exit [" + macroIoId + "]", true)
		};
		return out;
	}
}
