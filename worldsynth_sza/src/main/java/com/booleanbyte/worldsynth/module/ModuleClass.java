package com.booleanbyte.worldsynth.module;

import java.awt.Color;

public enum ModuleClass implements IModuleClass {
	GENERATOR {
		@Override
		public Color classColor() {return new Color(200, 255, 200);}
	},
	MODIFIER {
		@Override
		public Color classColor() {return new Color(255, 255, 100);}
	},
	COMBINER {
		@Override
		public Color classColor() {return new Color(100, 100, 255);}
	},
	SELECTOR {
		@Override
		public Color classColor() {return new Color(255, 100, 255);}
	},
	
	
	MACRO {
		@Override
		public Color classColor() {return new Color(250, 250, 250);}
		
	},
	MACRO_ENTRY {
		@Override
		public Color classColor() {return new Color(250, 250, 250);}
		
	},
	MACRO_EXIT {
		@Override
		public Color classColor() {return new Color(250, 250, 250);}
		
	};
}
