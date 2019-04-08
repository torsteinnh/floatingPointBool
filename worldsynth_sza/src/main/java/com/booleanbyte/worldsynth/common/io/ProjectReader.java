package com.booleanbyte.worldsynth.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.booleanbyte.worldsynth.common.Synth;

public class ProjectReader {
	
	public static Synth readSynthFromFile(File file) {
		//TODO Errorhandeling and error feedback
		String documentformat = "";
		
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bytebuffer = new byte[(int) file.length()];
			fis.read(bytebuffer);
			fis.close();
			documentformat = new String(bytebuffer);
		} catch (IOException e) {
			e.printStackTrace();
			//TODO Handle this in a more mature way
			return null;
		}
	
		Element rootElement = new Element(documentformat);
		
		return new Synth(rootElement);
	}
}
