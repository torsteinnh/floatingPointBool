package com.booleanbyte.worldsynth.module.macro;

import com.booleanbyte.worldsynth.common.datatype.DatatypeScalar;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;

public class ModuleScalarMacroExit extends AbstractModuleMacroExit {


	@Override
	public String getModuleName() {
		return "Macro exit (scalar)";
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] in = {
				new ModuleInput(new DatatypeScalar(), "Macro exit [" + macroIoId + "]")
		};
		return in;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] out = {
				new ModuleOutput(new DatatypeScalar(), "Macro exit [" + macroIoId + "]", true)
		};
		return out;
	}
}
