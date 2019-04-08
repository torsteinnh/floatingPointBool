package com.booleanbyte.worldsynth.module.macro;

import com.booleanbyte.worldsynth.common.datatype.DatatypeScalar;
import com.booleanbyte.worldsynth.module.ModuleInput;
import com.booleanbyte.worldsynth.module.ModuleOutput;

public class ModuleScalarMacroEntry extends AbstractModuleMacroEntry {

	@Override
	public String getModuleName() {
		return "Macro entry (scalar)";
	}

	@Override
	public ModuleInput[] registerInputs() {
		ModuleInput[] in = {
				new ModuleInput(new DatatypeScalar(), "Macro entry [" + macroIoId + "]", true)
		};
		return in;
	}

	@Override
	public ModuleOutput[] registerOutputs() {
		ModuleOutput[] out = {
				new ModuleOutput(new DatatypeScalar(), "Macro entry [" + macroIoId + "]")
		};
		return out;
	}

}
